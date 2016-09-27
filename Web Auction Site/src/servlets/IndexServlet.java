package servlets;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.AuctionDAO;
import dao.AuctionDAOI;
import dao.AuctionSearchOptions;
import dao.CategoryDAO;
import dao.CategoryDAOI;
import dao.UserDAO;
import dao.UserDAOI;
import entities.Auction;
import entities.Category;
import entities.User;
import entities.Image;

/**
 * Servlet implementation class IndexServlet
 */
@WebServlet(urlPatterns = {"", "/Search", "/Manage"})
public class IndexServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int searchResultsPerPage = 15;
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public IndexServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// String action = request.getParameter("action");
		String url_path = request.getRequestURI().substring(request.getContextPath().length());
		if (url_path.equals("/Search")) { // Load search results
			search(request, response);
		}
		else if (url_path.equals("/Manage")) { // Manage auctions
			manageAuctions(request, response);
		}
		else { // Load front page
			RequestDispatcher disp = getServletContext().getRequestDispatcher("/index.jsp");
			disp.forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	protected void search(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		// Create an AuctionSearchOptions with current date as minimum.
		AuctionSearchOptions options = new AuctionSearchOptions(new Date());
		String description_param = request.getParameter("description");
		String category_param = request.getParameter("category");
		String price_from_param = request.getParameter("price-from");
		String price_to_param = request.getParameter("price-to");
		String location_param = request.getParameter("location");
		
		// Check every parameter and form options.
		if(description_param != null && !description_param.equals("")){
			options.setDescription(description_param);
		}
		if(category_param != null && !category_param.equals("") && !category_param.equals("all")){
			options.setCategory(category_param);
		}
		if(price_from_param != null){
			float price_from_safe;
			try{
				price_from_safe = Float.parseFloat(price_from_param);
				options.setMinPrice(price_from_safe);
				
			} catch(NumberFormatException e){
				// Nothing to be handled, we just dont add the option.
			}
		}
		if(price_to_param != null){
			float price_to_safe;
			try{
				price_to_safe = Float.parseFloat(price_to_param);
				options.setMaxPrice(price_to_safe);
				
			} catch(NumberFormatException e){
				// Nothing to be handled, we just dont add the option.
			}
		}
		
		if(location_param != null && !location_param.equals("")){
			options.setLocation(location_param);
		}
		
		// Determine which page we have to present
		String pageStr = request.getParameter("page");
		int page = 0;
		if(pageStr != null){
			try{
				page = Integer.parseInt(pageStr);
			}
			catch (NumberFormatException e){
				// Page will remain 0, nothing to handle.
			}
		}
		
		if(page < 0){
			// We cannot allow negative pages.
			page = 0;
		}
		
		// Inject the dao with our options.
		AuctionDAOI aucdao = new AuctionDAO();
		List<Auction> search_results;
		search_results = aucdao.search(options, page, searchResultsPerPage);
		
		boolean has_next_page = false;
		boolean has_prev_page = false;
		
		// Check if we have a next page.
		if (aucdao.search(options, ((page + 1) * searchResultsPerPage), 1).size() > 0){
			// Above line checks if next item of fetched page exists.
			// If it does not exist, no more pages exist.
			has_next_page = true;
		}
		else {
			has_next_page = false;
		}
		
		if (page != 0){
			has_prev_page = true;
		}
		else {
			has_prev_page = false;
		}
		
		Image default_img=new Image();
		default_img.setUrl("default_img.png");
		default_img.setAuctions(null);
		List<Image> default_list=new ArrayList<>();
		default_list.add(default_img);
		
		for (Auction s_res : search_results) {
			System.out.println(s_res.getAuctionId());
            if((s_res.getImages()).isEmpty()){
            	System.out.println(s_res.getAuctionId());
            	s_res.setImages(default_list);
            }
		}
		
		request.setAttribute("searchResults", search_results);
		request.setAttribute("nextPage", has_next_page);
		request.setAttribute("prevPage", has_prev_page);
		request.setAttribute("currentPage", page);
		request.setAttribute("descriptionParam", description_param);
		request.setAttribute("locationParam", location_param);
		request.setAttribute("categoryParam", category_param);
		request.setAttribute("priceMinParam", price_from_param);
		request.setAttribute("priceMaxParam", price_to_param);
		
		RequestDispatcher disp = getServletContext().getRequestDispatcher("/search_results.jsp");
		disp.forward(request, response);
	}
	
	protected void manageAuctions(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		RequestDispatcher disp;
		if(request.getSession().getAttribute("userID") == null){
			disp = getServletContext().getRequestDispatcher("/loginerror.jsp");
			disp.forward(request, response);
			return ;
		}
		
		// Get current user.
		UserDAOI userdao = new UserDAO();
		User user = userdao.findByID(request.getSession().getAttribute("userID").toString());
		
		// We have 4 types of auctions, inactive, active, sold and bidded.
		// Fetch them in different lists.
		List<Auction> inactive;
		List<Auction> active;
		List<Auction> sold;
		List<Auction> won;
		List<Auction> lost;
		List<Auction> bidded;
		
		AuctionDAOI aucdao = new AuctionDAO();
		inactive = aucdao.findInactiveOf(user);
		active = aucdao.findActiveOf(user, new Date());
		sold = aucdao.findSoldOf(user, new Date());
		won = aucdao.findUserWonAuctions(user, new Date());
		lost = aucdao.findUserLostAuctions(user, new Date());
		bidded = aucdao.findUserBiddedAuctions(user, new Date());
		
		request.setAttribute("inactiveList", inactive);
		request.setAttribute("activeList", active);
		request.setAttribute("soldList", sold);
		request.setAttribute("wonList", won);
		request.setAttribute("lostList", lost);
		request.setAttribute("biddedList", bidded);
		
		disp = getServletContext().getRequestDispatcher("/auction_manage.jsp");
		disp.forward(request, response);
	}
}
