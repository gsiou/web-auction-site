package ru.shanalotte.servlets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import ru.shanalotte.dao.AuctionDAO;
import ru.shanalotte.dao.AuctionDAOI;
import ru.shanalotte.dao.CategoryDAO;
import ru.shanalotte.dao.CategoryDAOI;
import ru.shanalotte.dao.ImageDAO;
import ru.shanalotte.dao.ImageDAOI;
import ru.shanalotte.dao.UserDAO;
import ru.shanalotte.dao.UserDAOI;
import ru.shanalotte.entities.Auction;
import ru.shanalotte.entities.Category;
import ru.shanalotte.entities.Image;
import ru.shanalotte.entities.User;

/**
 * Servlet implementation class AuctionSubmit
 */

@WebServlet("/AuctionSubmit")
@MultipartConfig
public class AuctionSubmit extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int maxImages = 6;
	private static final int maxNameChars = 45;
	private static final int maxLocationChars = 255;
	private static final int maxCountryChars = 45;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AuctionSubmit() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher disp;
		if(request.getSession().getAttribute("userID") == null){
			disp = getServletContext().getRequestDispatcher("/loginerror.jsp");
			disp.forward(request, response);
			return ;
		}
		
		String action = request.getParameter("action");
		if(action == null){
			// No action
			disp = getServletContext().getRequestDispatcher("/404.html");
			disp.forward(request, response);
			return ;
		}
		else if (action.equals("submit")) {

			// Get user country and location to be preloaded on the submission
			// interface.
			UserDAOI dao = new UserDAO();
			User currentUser = dao.findByID(request.getSession().getAttribute("userID").toString());
			String userCountry = currentUser.getCountry();
			String userLocation = currentUser.getAddress();
			request.setAttribute("auctionCountry", userCountry);
			request.setAttribute("auctionLocation", userLocation);
			
			// Set the submission action.
			request.setAttribute("action", "submit");

			disp = getServletContext().getRequestDispatcher("/auction_submit.jsp");
			disp.forward(request, response);
			return ;
		}
		else if (action.equals("edit")){
			
			// Check if we have "id" parameter.
			String strId = request.getParameter("id");
			if(strId == null){
				disp = getServletContext().getRequestDispatcher("/404.html");
				disp.forward(request, response);
				return ;
			}
			
			// Safely parse the id.
			int id;
			try{
				id = Integer.parseInt(strId);
			}
			catch (NumberFormatException e){
				disp = getServletContext().getRequestDispatcher("/404.html");
				disp.forward(request, response);
				return ;
			}
			
			// We try to load the auction.
			AuctionDAOI aucdao = new AuctionDAO();
			Auction auction = aucdao.findByID(id);
			if (auction == null){
				disp = getServletContext().getRequestDispatcher("/404.html");
				disp.forward(request, response);
				return ;
			}
			
			// If logged in user is not the creator, we cannot allow him to edit.
			UserDAOI userdao = new UserDAO();
			User myuser = userdao.findByID(request.getSession().getAttribute("userID").toString());
			if(!auction.getCreator().getUserId().equals(myuser.getUserId())){
				disp = getServletContext().getRequestDispatcher("/404.html");
				disp.forward(request, response);
				return ;
			}
			
			// If auction has started, we cannot allow it to be edited.
			if (auction.getStartTime() != null){
				disp = getServletContext().getRequestDispatcher("/404.html");
				disp.forward(request, response);
				return ;
			}
			
			// Specify action and id
			request.setAttribute("action", "edit");
			request.setAttribute("auctionId", id);
			
			// Preload auction data in form fields.
			request.setAttribute("auctionName", auction.getName());
			request.setAttribute("auctionDescription", auction.getDescription());
			request.setAttribute("auctionStartingBid", auction.getStartingBid());
			request.setAttribute("auctionBuyPrice", auction.getBuyPrice());
			
			// Date has to be broken into parts first.
			Date exp_time = auction.getExpirationTime();
			
			request.setAttribute("auctionEndYear",new SimpleDateFormat("yyyy").format(exp_time));
			request.setAttribute("auctionEndMonth",new SimpleDateFormat("MM").format(exp_time));
			request.setAttribute("auctionEndDay", new SimpleDateFormat("dd").format(exp_time));
			request.setAttribute("auctionEndHour", new SimpleDateFormat("HH").format(exp_time));
			request.setAttribute("auctionEndMinute", new SimpleDateFormat("mm").format(exp_time));
			request.setAttribute("auctionLocation", auction.getLocation());
			request.setAttribute("auctionCountry", auction.getCountry());
			request.setAttribute("auctionLongitude", auction.getLongitude());
			request.setAttribute("auctionLatitude", auction.getLatitude());
			
			// Categories must be in >parentCategory>childCategory format
			// for the interface to function properly.
			String category_formatted = ">";
			for (Category cat : auction.getCategories()){
				category_formatted += cat.getName() + ">";
			}
			request.setAttribute("auctionCategory", category_formatted);
			
			disp = getServletContext().getRequestDispatcher("/auction_submit.jsp");
			disp.forward(request, response);
			return ;
		}
		else {
			// Invalid action
			disp = getServletContext().getRequestDispatcher("/404.html");
			disp.forward(request, response);
			return ;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		RequestDispatcher disp;
		
		String action = request.getParameter("action");
		if(action == null){
			return ;
		}
		
		if(action.equals("submit") || action.equals("edit")){
			// Action submit and edit are identical to a great extent.
			// Their difference is that submit creates a new entity and persists it
			// while edit updates the fields of an existing one.
			
			// First check if our user is logged in.
			if(request.getSession().getAttribute("userID") == null){
				disp = getServletContext().getRequestDispatcher("/loginerror.jsp");
				disp.forward(request, response);
				return;
			}
			
			boolean isSubmit = false;
			boolean isEdit = false;
			
			if(action.equals("submit")){
				isSubmit = true;
			}
			else{
				isEdit = true;
			}
			
			AuctionDAOI aucdao = new AuctionDAO();
			Auction auc = null;
			
			if (isEdit) {
				// When action is "edit" we have to make sure we are given an id 
				// of the auction to update.
				String strId = request.getParameter("id");
				if (strId == null) {
					forwardMessage(request, response, "No auction specified!");
					return;
				}

				// Safely parse the id.
				int id;
				try {
					id = Integer.parseInt(strId);
				} catch (NumberFormatException e) {
					forwardMessage(request, response, "Invalid auction id!");
					return;
				}

				// Fetch the auction.
				auc = aucdao.findByID(id);
				if (auc == null) {
					forwardMessage(request, response, "Auction does not exist!");
					return;
				}
				
				// If auction has started, we cannot allow it to be edited.
				if (auc.getStartTime() != null){
					forwardMessage(request, response, "Cannot edit an expired auction!");
					return ;
				}
			}
			else{ // Submit means we create a new auction
				auc = new Auction();
			}
			
			// Collect parameters.
			String name = request.getParameter("name");
			String description = request.getParameter("description");
			String starting = request.getParameter("starting");
			String buyprice = request.getParameter("buyprice");
			String latitude = request.getParameter("latitude");
			String longitude = request.getParameter("longitude");
			String country = request.getParameter("country");
			String location = request.getParameter("location");
			String endsyear = request.getParameter("endsyear");
			String endsmonth = request.getParameter("endsmonth");
			String endsday = request.getParameter("endsday");
			String endshour = request.getParameter("endshour");
			String endsminute = request.getParameter("endsminute");
			String categories = request.getParameter("categories");
			//Part filePart = request.getPart("file");

			String message = "";
			if(name == null || name.length() > maxNameChars){
				message = "You have to specify a name! (Max characters: " + maxNameChars +")";
			}
			else if(description == null){
				message = "You have to give a description of the product";
			}
			else if(starting == null){
				message = "You have to give a starting bid";
			}
			else if(country == null || country.length() > maxCountryChars){
				message = "You have to provide your country! (Max characters: " + maxCountryChars +")";
			}
			else if(location == null || location.length() > maxLocationChars){
				message = "You have to provide your location! (Max characters: " + maxLocationChars +")";
			}
			else if (categories == null || categories.length() < 2){
				message = "You have to provide categories for your item";
			}
			else if(endsyear == null || endsmonth == null || endsday == null
					|| endshour == null || endsday == null){
				message = "You have to specify and ending date";
			}
			else{
				// We are good.
				
				//Validate Date.
				Date ends_date = null;

				StringBuilder sb = new StringBuilder();
				sb.append(String.format("%04d ", Integer.parseInt(endsyear)));
				sb.append(String.format("%02d ", Integer.parseInt(endsmonth)));
				sb.append(String.format("%02d ", Integer.parseInt(endsday)));
				sb.append(String.format("%02d ", Integer.parseInt(endshour)));
				sb.append(String.format("%02d ", Integer.parseInt(endsminute)));

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd HH mm");
				try {
					ends_date = sdf.parse(sb.toString());
				} catch (ParseException e) {
					message = "Invalid date format";
					forwardMessage(request, response, message);
					return;
				}

				// Starting date is current date.
				Date current_date = new Date();

				// Check if given date is older than current.
				if(current_date.after(ends_date)){
					message = "Ending date cannot be a past date";
					forwardMessage(request, response, message);
					return;
				}
				
				// Check numbers first
				float starting_f = 0, 
						latitude_f = 0, 
						longitude_f = 0, 
						buyprice_f = 0;
				try {
					starting_f = Float.parseFloat(starting);
					if(starting_f <= 0){
						forwardMessage(request, response, "Zero/Negative starting price!");
						return ;
					}
					
					if (!latitude.equals("") && !longitude.equals("")) {
						latitude_f = Float.parseFloat(latitude);
						longitude_f = Float.parseFloat(longitude);
						
						if(latitude_f <= 0 || longitude_f <= 0){
							forwardMessage(request, response, "Invalid/Negative coordinates!");
							return ;
						}
						
					}
					
					if (!buyprice.equals("")) {
						buyprice_f = Float.parseFloat(buyprice);
						if(buyprice_f <= 0){
							forwardMessage(request, response, "Zero/Negative buy price!");
							return ;
						}
					}
				} catch (NumberFormatException e) {
					forwardMessage(request, response, "Invalid number data.");
					return ;
				}
				
				
				// Start constructing data.
				CategoryDAOI catdao = new CategoryDAO();
				UserDAOI udao = new UserDAO();
				
				Category cat;
				User user;
				

				
				// Set numberic values first.
				auc.setStartingBid(starting_f);
				if (!latitude.equals("") && !longitude.equals("")) {
					auc.setLatitude(latitude_f);
					auc.setLongitude(longitude_f);
				}
				if (!buyprice.equals("")) {
					auc.setBuyPrice(buyprice_f);
				}
				
				auc.setName(name);
				auc.setDescription(description);
				auc.setStartTime(null); // Is set when user activates auction.
				auc.setExpirationTime(ends_date);
				auc.setCountry(country);
				auc.setLocation(location);
				auc.setNumOfBids(0);
				auc.setCurrentBid(auc.getStartingBid());
				
				// Parse categories
				String[] categories_list;
				categories_list = categories.substring(1).split(">");
				
				
				// Set the categories.
				ArrayList<Category> catlist = new ArrayList<>();
				for(String s : categories_list){
					cat = catdao.find(s);
					if(cat != null){
						//cat.getAuctions().add(auc);
						catlist.add(cat);
					}
				}
				
				auc.setCategories(catlist);
				
				// Add auction to seller
				user = udao.findByID(request.getSession().getAttribute("userID").toString());
				user.getAuctions().add(auc);
				auc.setCreator(user);
				
				if(isSubmit){
					// In submission case we have to create a new entity.
					aucdao.create(auc);
				}
				else{
					// In edit we just update.
					aucdao.updateAuction(auc);
				}
				
				// Upload images.
				// Get all image parts from imagefiles input element.
				List<Part> file_parts = request.getParts().stream().filter(
						part -> "imagefiles".equals(part.getName())).
						collect(Collectors.toList()); //http://stackoverflow.com/a/2424824

				int counter = 0;
				int auction_id = auc.getAuctionId();
				String extension, file_name, save_file_name;
				File image_file, savepath;
				InputStream content;
				long file_size;
				long max_file_size = Long.parseLong(getServletContext().getInitParameter("images.maxsize")); // web.xml
				savepath = new File(getServletContext().getInitParameter("images.location"));
				
				// Load image dao to create new images.
				Image image;
				ImageDAOI imgdao = new ImageDAO();
				ArrayList<Image> auction_images = new ArrayList<>();
				ArrayList<Auction> auctions; // To save image's auctions.

				System.out.println("auc = " + auc);
				System.out.println("auc images = " + auc.getImages());

				int already_stored = auc.getImages().size();
				if (already_stored + file_parts.size() <= maxImages) {
					for (Part file_part : file_parts) {
						file_name = Paths.get(file_part.getSubmittedFileName()).getFileName().toString();
						if (file_name.lastIndexOf(".") != -1) { // No extension.
							extension = file_name.substring(file_name.lastIndexOf("."));
							file_size = file_part.getSize();
							if (extension.equalsIgnoreCase(".png") || extension.equalsIgnoreCase(".jpg")
									|| extension.equals(".jpeg")) { // Only
																	// accept
																	// png, jpg
																	// and jpeg.

								if (file_size < max_file_size) { // Accept only
																	// small
																	// files.
									content = file_part.getInputStream();
									int image_id = counter + already_stored;
									save_file_name = auction_id + "_" + image_id + extension;// Name
																								// and
																								// extension.
									image_file = new File(savepath, save_file_name);
									Files.copy(content, image_file.toPath()); // Write
																				// file.

									// Store image in db.
									image = new Image();
									image.setUrl(save_file_name);
									auctions = new ArrayList<>();
									auctions.add(auc);
									image.setAuctions(auctions);
									imgdao.create(image);
									auction_images.add(image);

									counter++; // Prevent duplicate names.
								}
							}
						}
						// auc.setImages(auction_images);
						auc.getImages().addAll(auction_images);
					}
				}
				
				response.sendRedirect(request.getContextPath() + "/Manage");
				return ;
			}
			forwardMessage(request, response, message);
			return ;
		}
		else if(action.equals("fetch_categories")){
			// Fetch categories for dynamic calls in category picking.
			JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);
			String parent_category = (String)data.get("parent_category").getAsString();
			if(parent_category.equals("")){
				parent_category = null; // For jpa query.
			}
			CategoryDAOI dao = new CategoryDAO();
			List<Category> cat_list = dao.listChildren(parent_category);
			
			// Prepare json object with results.
			JsonArray  cat_arr = new JsonArray();
			// JsonPrimitive element = new JsonPrimitive()
			for(Category c : cat_list){
				cat_arr.add(new JsonPrimitive(c.getName()));
			}
			JsonObject message = new JsonObject();
			message.add("categories", cat_arr);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(message.toString());
			return ;
		}
		else if(action.equals("activate")){
			// First check if our user is logged in.
			if(request.getSession().getAttribute("userID") == null){
				disp = getServletContext().getRequestDispatcher("/loginerror.jsp");
				disp.forward(request, response);
				return;
			}
			
			String strId = request.getParameter("id");
			if(strId == null){
				response.getWriter().write("No auction specified");
				return ;
			}
			
			int id = -1;
			try{
				id = Integer.parseInt(strId);
			}
			catch (NumberFormatException e){
				response.getWriter().write("Invalid id");
			}
			
			// Activate given auction by setting the start time.
			AuctionDAOI aucdao = new AuctionDAO();
			Auction auction = aucdao.findByID(id);
			if(auction == null){
				response.getWriter().write("Auction does not exist!");
				return ;
			}
			
			if(auction.getExpirationTime().before(new Date())){
				response.getWriter().write("Auction expiration date is a past date!");
				return ;
			}
			
			auction.setStartTime(new Date());
			aucdao.updateAuction(auction);
			
			response.sendRedirect(request.getContextPath() + "/Manage");
		}
	}
	
	public void forwardMessage(HttpServletRequest request, HttpServletResponse response, String message) throws ServletException, IOException{
		RequestDispatcher disp;
		request.setAttribute("message", message);
		disp = getServletContext().getRequestDispatcher("/error.jsp");
		disp.forward(request, response);
	}
}
