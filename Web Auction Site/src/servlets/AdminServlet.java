package servlets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import dao.AuctionDAO;
import dao.AuctionDAOI;
import dao.CategoryDAO;
import dao.CategoryDAOI;
import dao.UserDAO;
import dao.UserDAOI;
import dao.User_bid_AuctionDAO;
import dao.User_bid_AuctionDAOI;
import entities.Auction;
import entities.Category;
import entities.User;

/**
 * Servlet implementation class AdminServlet
 */
@WebServlet("/Admin")
@MultipartConfig
public class AdminServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static final int usersPerPage = 20;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AdminServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher disp;
		if(request.getSession().getAttribute("userID") != null && 
				(int) request.getSession().getAttribute("access") == 100){
			
			// Type indicates whether we bring all users or unactivated only.
			// If user request does not specify type, default is all
			String type = request.getParameter("type");
			UserDAOI dao = new UserDAO();
			if(type == null || (!type.equals("all") && !type.equals("unactivated"))){
				type = "all";
			}
			
			// Find total pages per type.
			int max_pages;
			if(type.equals("all")){
				max_pages = (int) Math.ceil((float) dao.userCount() / usersPerPage);
			}
			else{
				max_pages = (int) Math.ceil((float) dao.unactivatedUserCount() / usersPerPage);
			}
			
			// If user did not request a page, default to the 1st (0 indexed).
			int page = 0;
			if(request.getParameter("page") != null){
				page = Integer.parseInt(request.getParameter("page"));
			}
			
			// If user requests negative page, he gets the 1st.
			if(page < 0){
				page = 0;
			}
			
			// If user requests page > than max, get the last.
			if(page > max_pages - 1){
				page = max_pages - 1;
			}
			
			List<User> userList = null;
			if(type.equals("all")){
				userList = dao.listUsersOfPage(page, usersPerPage);
			}
			else{
				userList = dao.listUnactivatedUsersOfPage(page, usersPerPage);
			}
			
			request.setAttribute("currentPage", page);
			request.setAttribute("totalPages", max_pages);
			request.setAttribute("userType", type);
			request.setAttribute("userList", userList);
			disp = getServletContext().getRequestDispatcher("/admin.jsp");
			disp.forward(request, response);
			//response.sendRedirect("Admin");
		}
		else{
			request.setAttribute("error", "You must login as admin to continue.");
			disp = getServletContext().getRequestDispatcher("/loginerror.jsp");
			disp.forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Check if user is actually admin.
		RequestDispatcher disp;
		if(request.getSession().getAttribute("userID") == null || 
				(int) request.getSession().getAttribute("access") != 100){
			request.setAttribute("error", "You must login as admin to continue.");
			disp = getServletContext().getRequestDispatcher("/loginerror.jsp");
			disp.forward(request, response);
			return ;
		}
		
		String action;
		String message = "";
		action = (String) request.getParameter("action");
		if(action != null){
			
			// Call apropriate function according to action parameter.
			if(action.equals("activate") || action.equals("deactivate")){
				activation(request, response);
			}
			else if(action.equals("loadDataset")){
				loadDataset(request, response);
			}
			
		}
		else{
			message = "Invalid action.";
			disp = getServletContext().getRequestDispatcher("/admin.jsp");
			disp.forward(request, response);
		}
	}
	
	private void activation(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String userid;
		String action;
		String message = "";
		boolean success = false;
		userid = (String) request.getParameter("userid");
		action = (String) request.getParameter("action");
		if(userid != null){
			UserDAOI dao = new UserDAO();
			User myuser = dao.findByID(userid);
			if(myuser != null){
				dao.changeAccess(myuser, action.equals("activate") ? 1 : 0);
				success = true;
			}
			else{
				message = "User does not exist.";
			}
		}
		else{
			message = "Invalid data";
		}
		if(success){
			// We dont want the page to change so we send admin where he/she was.
			String redir_url = "Admin?";
			if(request.getParameter("page") != null){
				redir_url += "page=" + request.getParameter("page"); // add page parameter
			}
			if(request.getParameter("type") != null){
				redir_url += "&type=" + request.getParameter("type"); // add type parameter
			}
			response.sendRedirect(redir_url);
		}
		else{
			RequestDispatcher disp;
			disp = getServletContext().getRequestDispatcher("/admin.jsp");
		}
	}

	private void loadDataset(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		Part filePart = request.getPart("file");
		InputStream content = filePart.getInputStream();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// factory.setValidating(true);
		factory.setIgnoringElementContentWhitespace(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(content);
			NodeList nodeList = doc.getElementsByTagName("Item");
			Node current;
			CategoryDAOI dao = new CategoryDAO();
			UserDAOI udao = new UserDAO();
			AuctionDAOI audao = new AuctionDAO();
			User_bid_AuctionDAOI ubadao = new User_bid_AuctionDAO();
			
			// Import categories.
			for (int i = 0; i < nodeList.getLength(); i++) {
				current = nodeList.item(i);
				if (current.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) current;
					ArrayList<String> elemList = new ArrayList<>();
					for (int j = 0; j < e.getElementsByTagName("Category").getLength(); j++) {
						Category cat = new Category();
						cat.setName(e.getElementsByTagName("Category").item(j).getTextContent());
						if (j != 0) { // A parent category exists.
							cat.setParent(elemList.get(j - 1));
						}
						dao.create(cat);
						elemList.add(e.getElementsByTagName("Category").item(j).getTextContent());
					}
				}
			}
			
			// Import bidding users.
			nodeList = doc.getElementsByTagName("Bidder");
			for(int i = 0; i < nodeList.getLength(); i++){
				current = nodeList.item(i);
				if(current.getNodeType() == Node.ELEMENT_NODE){
					Element e = (Element) current;
					User user = new User();
					user.setUserId(e.getAttribute("UserID"));
					user.setBid_rating(Float.parseFloat(e.getAttribute("Rating")));
					user.setPassword("");
					user.setAccess_lvl(1);
					if(e.getElementsByTagName("Location").item(0) != null){
						user.setAddress(e.getElementsByTagName("Location").item(0).getTextContent());
					}
					if(e.getElementsByTagName("Country").item(0) != null){
						user.setCountry(e.getElementsByTagName("Country").item(0).getTextContent());
					}
					udao.create(user);
				}
			}
			
			// Import sellers.
			nodeList = doc.getElementsByTagName("Seller");
			for(int i = 0; i < nodeList.getLength(); i++){
				current = nodeList.item(i);
				if(current.getNodeType() == Node.ELEMENT_NODE){
					Element e = (Element) current;
					User user = new User();
					user.setUserId(e.getAttribute("UserID"));
					user.setSell_rating(Float.parseFloat(e.getAttribute("Rating")));
					user.setPassword("");
					user.setAccess_lvl(1);
					if(!udao.create(user)){
						// User exists, we have to update him.
						User temp = udao.findByID(user.getUserId());
						temp.setSell_rating(user.getSell_rating());
						udao.update(temp);
					}
				}
			}
			// Import auctions.
			nodeList = doc.getElementsByTagName("Item");
			for(int i = 0; i < nodeList.getLength(); i++){
				//System.out.println("Test");
				current = nodeList.item(i);
				if(current.getNodeType() == Node.ELEMENT_NODE){
					Element e = (Element) current;
					Boolean addCat = true;
					Auction auc = new Auction();
					//auc.setAuctionId(Integer.parseInt(e.getAttribute("ItemID")));
					auc.setName(e.getElementsByTagName("Name").item(0).getTextContent());
					
					List<Category> categories = new ArrayList<>();
					for(int j = 0; j < e.getElementsByTagName("Category").getLength(); j++){
						categories.add(dao.find(e.getElementsByTagName("Category").item(j).getTextContent()));
						dao.addAuctionTo(auc, categories.get(j).getName());
					}
					auc.setCategories(categories);
					
					auc.setCurrent_Bid(Float.parseFloat(e.getElementsByTagName("Currently").item(0).getTextContent()
							.substring(1).replace(",", "")));
					auc.setStarting_Bid(Float.parseFloat(e.getElementsByTagName("First_Bid").item(0).getTextContent()
							.substring(1).replace(",", "")));
					auc.setNum_of_bids(Integer.parseInt(e.getElementsByTagName("Number_of_Bids").item(0).getTextContent()));
					auc.setDescription(e.getElementsByTagName("Description").item(0).getTextContent());
					auc.setCountry(e.getElementsByTagName("Country").item(0).getTextContent());
					SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yy HH:mm:ss", Locale.ENGLISH);
					auc.setStart_time(sdf.parse(e.getElementsByTagName("Started").item(0).getTextContent()));
					auc.setExpiration_time(sdf.parse(e.getElementsByTagName("Ends").item(0).getTextContent()));
					
					String tempid;
					tempid = ((Element) e.getElementsByTagName("Seller").item(0)).getAttribute("UserID");
					User tempuser = udao.findByID(tempid);
					auc.setCreator(tempuser);
					tempuser.getAuctions().add(auc);
					
					String lat = ((Element)e.getElementsByTagName("Location").item(0)).getAttribute("Latitude");
					String lon = ((Element)e.getElementsByTagName("Location").item(0)).getAttribute("Longitude");
					if(!lat.equals("") && !lon.equals("")){
						auc.setLatitude(Float.parseFloat(lat));
						auc.setLongitude(Float.parseFloat(lon));
					}
					auc.setLocation(e.getElementsByTagName("Location").item(0).getTextContent());
					if(e.getElementsByTagName("Buy_Price").item(0) != null){
						auc.setBuy_Price(Float.parseFloat(e.getElementsByTagName("Buy_Price").item(0).getTextContent()
								.substring(1).replace(",", "")));
					}
					audao.create(auc);
					
					// User bid auction import.
					String userid;
					//int aucid = auc.getAuctionId();
					Date dt;
					float price;
					Node temp;
					Element bid;
					if(e.getElementsByTagName("Bid") != null){
						for(int j = 0; j < e.getElementsByTagName("Bid").getLength(); j++){
							bid = (Element) e.getElementsByTagName("Bid").item(j);
							dt = sdf.parse(bid.getElementsByTagName("Time").item(0).getTextContent());
							price = Float.parseFloat(bid.getElementsByTagName("Amount").item(0).getTextContent()
									.substring(1).replace(",", ""));
							temp = bid.getElementsByTagName("Bidder").item(0);
							userid = ((Element) temp).getAttribute("UserID");
							tempuser = udao.findByID(userid);
							ubadao.create(tempuser, auc, dt, price);
						}
					}
					//System.out.println("Im here");
				}
			}
			response.sendRedirect("Admin?message=" + URLEncoder.encode("Import successful.", "UTF-8"));
		} catch (Exception e) {
			System.out.println("An error occured while importing data.");
			System.out.println(e.getMessage());
			response.sendRedirect("Admin?message=" + URLEncoder.encode("Import failed.", "UTF-8"));
		}
	}
}
