package servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

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
import entities.User_bid_Auction;
import xmlentities.Bid;
import xmlentities.Bidder;
import xmlentities.Bids;
import xmlentities.Item;
import xmlentities.Items;
import xmlentities.LocationElem;
import xmlentities.UserElem;

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
			
			// If user requests invalid page he gets the 1st.
			if(page < 0 || page > max_pages - 1){
				page = 0;
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
		action = (String) request.getParameter("action");
		if(action != null){
			
			// Call apropriate function according to action parameter.
			if(action.equals("activate") || action.equals("deactivate")){
				activation(request, response);
			}
			else if(action.equals("loadDataset")){
				loadDataset(request, response);
			}
			else if(action.equals("exportDataset")){
				exportDataset(request, response);
			}
			
		}
		else{
			disp = getServletContext().getRequestDispatcher("/admin.jsp");
			disp.forward(request, response);
		}
	}
	
	private void exportDataset(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, IOException {
		JAXBContext jc = null;
		Items items = null;
		Marshaller marshaller = null;
		try {
			jc = JAXBContext.newInstance(Items.class);
			marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		} catch (JAXBException e) {
			response.sendRedirect("Admin?message=" + URLEncoder.encode("Export failed.", "UTF-8"));
			return;
		}
		
		// Populate xml entity classes.
		
		// Bring all auctions from db.
		AuctionDAOI aucdao = new AuctionDAO();
		List<Auction> auctions = aucdao.list();
		
		List<Item> item_list = new ArrayList<>();
		Item current;
		LocationElem current_loc;
		UserElem current_user;
		Bids current_bids;
		Bid current_bid;
		List<Bid> current_bid_list;
		Bidder current_bidder;
		List<String> current_categories;
		List<Category> categories_copy;
		String prev_category = "";
		int categories_number;
		
		for(Auction a : auctions){
			current = new Item();
			current.setName(a.getName());
			current.setDescription(a.getDescription());
			current.setCountry(a.getCountry());
			current.setCurrently("$" + a.getCurrent_Bid());
			current.setFirst_bid("$" + a.getStarting_Bid());
			current.setNumber_of_bids(a.getNum_of_bids());
			if(a.getBuy_Price() != 0){
				current.setBuy_Price("$" + a.getBuy_Price());;
			}
			
			/* Location */
			current_loc = new LocationElem();
			current_loc.setLocation(a.getLocation());
			current_loc.setLatitude(a.getLatitude());
			current_loc.setLongitude(a.getLongitude());;
			current.setLocation(current_loc);
			
			/* Seller */
			current_user = new UserElem();
			current_user.setUserID(a.getCreator().getUserId());
			current_user.setRating((int) a.getCreator().getSell_rating());
			current.setSeller(current_user);
			
			/* Start/End Dates */
			SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yy HH:mm:ss", Locale.ENGLISH);
			if(a.getStart_time() == null){
				current.setStarted("");
			}
			else{
				current.setStarted(sdf.format(a.getStart_time()));
			}
			current.setEnds(sdf.format(a.getExpiration_time()));
			
			/* Categories */
			current_categories = new ArrayList<String>();
			categories_copy = new ArrayList<Category>(a.getCategories());
			categories_number = categories_copy.size();
			for(int i = 0; i < categories_number; i++){ // Sort them
				for (Category auct_cat : categories_copy) {
					if(auct_cat.getParent() == null || 
							auct_cat.getParent().equals(prev_category)){
						current_categories.add(auct_cat.getName());
						prev_category = auct_cat.getName();
						categories_copy.remove(auct_cat);
						break;
					}
				}
			}
			current.setCategories(current_categories);
			
			/* Bids */
			current_bid_list = new ArrayList<Bid>();
			for(User_bid_Auction uba : a.getUserBidAuctions()){
				current_bidder = new Bidder();
				current_bidder.setCountry(uba.getUser().getCountry());
				current_bidder.setLocation(uba.getUser().getAddress());
				current_bidder.setRating((int) uba.getUser().getBid_rating());
				current_bidder.setUserID(uba.getUser().getUserId());
				
				current_bid = new Bid();
				current_bid.setAmount("$" + uba.getPrice());
				current_bid.setTime(sdf.format(uba.getTime()));
				current_bid.setBidder(current_bidder);
				current_bid_list.add(current_bid);
			}
			current_bids = new Bids();
			current_bids.setBids(current_bid_list);
			current.setBids(current_bids);
			item_list.add(current);
		}
		items = new Items();
		items.setItems(item_list);
		
		try {
			response.setContentType("text/plain");
			response.setHeader("Content-Disposition", "attachment;filename=itemsall.xml");
			marshaller.marshal(items, response.getOutputStream());
			
		} catch (JAXBException e) {
			response.sendRedirect("Admin?message=" + URLEncoder.encode("Export failed.", "UTF-8"));
			return;
		}
		
	}

	private void activation(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
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
			response.sendRedirect("Admin?message=" + URLEncoder.encode(message, "UTF-8"));
		}
	}

	private void loadDataset(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		JAXBContext jc;
		Items items;
		try {
			jc = JAXBContext.newInstance(Items.class);
	        Unmarshaller unmarshaller = jc.createUnmarshaller();
			Part filePart = request.getPart("file");
			InputStream content = filePart.getInputStream();
	        items = (Items) unmarshaller.unmarshal(content);
		} catch (Exception e1) {
			response.sendRedirect("Admin?message=" + URLEncoder.encode("Import failed.", "UTF-8"));
			return;
		}


        for(Item i : items.getItems()){
        	Category cat;
        	CategoryDAOI catdao = new CategoryDAO();
        	String parent;
        	parent = null;
        	Auction auc = new Auction();
        	List<Category> categories = new ArrayList<>();
        	Category placeholder = null;
        	for(String c : i.getCategories()){
        		cat = catdao.find(c);
        		if(cat == null){
        			// Does not exist in db yet.
        			placeholder = new Category();
        			placeholder.setName(c);
        			placeholder.setParent(parent);
            		if(categories.contains(placeholder)){
        				cat = new Category();
        				cat.setName(c + " (" + parent + ")");
        				cat.setParent(parent);
        				cat.setAuctions(new ArrayList<Auction>());
            		}
            		else{
            			cat = new Category();
            			cat.setName(c);
            			cat.setParent(parent);
            			cat.setAuctions(new ArrayList<Auction>());
            		}
        		}
        		cat.getAuctions().add(auc);
        		categories.add(cat);
        		parent = cat.getName();
        	}
        	auc.setCategories(categories);

        	UserDAOI userdao = new UserDAO();
        	User creator = userdao.findByID(i.getSeller().getUserID());
        	if(creator == null){
        		creator = new User();
        		creator.setUserId(i.getSeller().getUserID());
        		creator.setAccess_lvl(1);
        		creator.setPassword("");
        		creator.setEmail("");
        	}
        	creator.setCountry(i.getCountry());
    		creator.setSell_rating(i.getSeller().getRating());
    		if(i.getLocation().getLatitude() != 0 && i.getLocation().getLatitude() != 0){
        		creator.setLatitude(i.getLocation().getLatitude());
        		creator.setLongitude(i.getLocation().getLongitude());
    		}
    		
    		//System.out.println("Loc: " + i.getLocation().getLatitude() + i.getLocation().getLongitude());
        	auc.setName(i.getName());
        	auc.setDescription(i.getDescription());
        	auc.setCreator(creator);
        	auc.setCountry(i.getCountry());
        	auc.setLocation(i.getLocation().getLocation());
        	auc.setLatitude(i.getLocation().getLatitude());
        	auc.setLongitude(i.getLocation().getLongitude());
        	auc.setNum_of_bids(i.getNumber_of_bids());
        	
        	try{
        		auc.setStarting_Bid(Float.parseFloat(i.getFirst_bid().substring(1)));
        	} catch (NumberFormatException ex){
        		auc.setStarting_Bid(0);
        	}
        	
        	if(i.getBuy_Price() != null){
        		try{
        			auc.setBuy_Price(Float.parseFloat(i.getBuy_Price().substring(1)));
        		} catch (NumberFormatException ex){
        			auc.setBuy_Price(0);
        		}
        	}
        	
        	try{
        		auc.setCurrent_Bid(Float.parseFloat(i.getCurrently().substring(1)));
        	} catch (NumberFormatException ex){
        		auc.setCurrent_Bid(0);
        	}
        	
        	SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yy HH:mm:ss", Locale.ENGLISH);
			try {
				auc.setStart_time(sdf.parse(i.getStarted()));
			} catch (ParseException e) {
				auc.setStart_time(null);
			}
			
			try{
				//auc.setExpiration_time(sdf.parse(i.getEnds()));
				auc.setExpiration_time(sdf.parse("Jan-01-17 23:30:01"));
			} catch (ParseException e){
				auc.setExpiration_time(null);
			}
			
        	AuctionDAOI aucdao = new AuctionDAO();
        	aucdao.create(auc);
			
			if (i.getBids().getBids() != null) {
				User_bid_AuctionDAOI ubadao = new User_bid_AuctionDAO();
				for (Bid b : i.getBids().getBids()) {
					User bidder = userdao.findByID(b.getBidder().getUserID());
					if(bidder == null){
						bidder = new User();
						bidder.setUserId(b.getBidder().getUserID());
						bidder.setPassword("");
						bidder.setAccess_lvl(1);
						bidder.setEmail("");
					}
					bidder.setCountry(b.getBidder().getCountry());
					bidder.setAddress(b.getBidder().getLocation());
					bidder.setBid_rating(b.getBidder().getRating());
					
					float price;
					try{
						price = Float.parseFloat(b.getAmount().substring(1));
					} catch (NumberFormatException ex){
						price = 0;
					}
					
					Date time;
					try {
						time = sdf.parse(i.getStarted());
					} catch (ParseException e) {
						time = null;
					}
					ubadao.create(bidder, auc, time, price);
				}
			}
        }
        response.sendRedirect("Admin?message=" + URLEncoder.encode("Import successful.", "UTF-8"));
	}
}
