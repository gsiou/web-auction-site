package servlets;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import dao.AuctionDAO;
import dao.AuctionDAOI;
import dao.CategoryDAO;
import dao.CategoryDAOI;
import dao.UserDAO;
import dao.UserDAOI;
import entities.Auction;
import entities.Category;
import entities.User;

/**
 * Servlet implementation class AuctionSubmit
 */

@WebServlet("/AuctionSubmit")
@MultipartConfig
public class AuctionSubmit extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
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
		// Get user country and location to be preloaded on the submission interface.
		UserDAOI dao = new UserDAO();
		User currentUser = dao.findByID(request.getSession().getAttribute("userID").toString());
		String userCountry = currentUser.getCountry();
		String userLocation = currentUser.getAddress();
		request.setAttribute("userCountry", userCountry);
		request.setAttribute("userLocation", userLocation);

		disp = getServletContext().getRequestDispatcher("/auction_submit.jsp");
		disp.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// First check if our user is logged in.
		RequestDispatcher disp;
		if(request.getSession().getAttribute("userID") == null){
			disp = getServletContext().getRequestDispatcher("/loginerror.jsp");
			disp.forward(request, response);
			return;
		}
		
		String action = request.getParameter("action");
		if(action == null){
			System.out.println("Null action");
			return ;
		}
		if(action.equals("submit")){
			// Create new Auction.
			
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

			// Check if we have everything.
			boolean success = false;
			String message = "";
			if(name == null){
				message = "You have to specify a name";
			}
			else if(description == null){
				message = "You have to give a description of the product";
			}
			else if(starting == null){
				message = "You have to give a starting bid";
			}
			else if(country == null || location == null){
				message = "You have to provide your location";
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
					// TODO: handle this
					e.printStackTrace();
					message = "Invalid date format";
					forwardMessage(request, response, message);
					return;
				}

				// Starting date is current date.
				Date starting_date = new Date();

				// Check if given date is older than current.
				if(starting_date.after(ends_date)){
					message = "Ending date cannot be a past date";
					forwardMessage(request, response, message);
					return;
				}
				
				// Start constructing data.
				AuctionDAOI aucdao = new AuctionDAO();
				CategoryDAOI catdao = new CategoryDAO();
				UserDAOI udao = new UserDAO();
				
				Auction auc = new Auction();
				Category cat;
				User user;
				
				auc.setName(name);
				auc.setDescription(description);
				auc.setStart_time(starting_date);
				auc.setExpiration_time(ends_date);
				auc.setCountry(country);
				auc.setLocation(location);
				auc.setStarting_Bid(Float.parseFloat(starting));
				auc.setNum_of_bids(0);
				auc.setCurrent_Bid(auc.getStarting_Bid());
				//auc.setImages...
				if(!latitude.equals("") && !longitude.equals("")){
					auc.setLatitude(Float.parseFloat(latitude));
					auc.setLongitude(Float.parseFloat(longitude));
				}
				if(!buyprice.equals("")){
					auc.setBuy_Price(Float.parseFloat(buyprice));
				}
				
				// Parse categories
				String[] categories_list;
				categories_list = categories.substring(1).split(">");
				
				// Set the categories.
				ArrayList<Category> catlist = new ArrayList<>();
				for(String s : categories_list){
					cat = catdao.find(s);
					if(cat != null){
						cat.getAuctions().add(auc);
						catlist.add(cat);
						//auc.getCategories().add(cat);
					}
				}
				auc.setCategories(catlist);
				
				// Add auction to seller
				user = udao.findByID(request.getSession().getAttribute("userID").toString());
				user.getAuctions().add(auc);
				ArrayList<User> users_list = new ArrayList<>();
				users_list.add(user);
				auc.setUsers(users_list);
				//auc.getUsers().add(user);
				
				aucdao.create(auc);
			}
			System.out.println(message);
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
		}
	}
	
	public void forwardMessage(HttpServletRequest request, HttpServletResponse response, String message) throws ServletException, IOException{
		RequestDispatcher disp;
		request.setAttribute("message", message);
		disp = getServletContext().getRequestDispatcher("AuctionSubmit");
		disp.forward(request, response);
	}
}
