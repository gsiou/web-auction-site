package servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;

import dao.AuctionDAO;
import dao.AuctionDAOI;
import dao.UserDAO;
import dao.UserDAOI;
import entities.Auction;
import entities.User;
import helper.AuctionFrequency;
import helper.AuctionFrequencyComparator;
import utils.HelperFunctions;

/**
 * Servlet implementation class UserLoginServlet
 */
@WebServlet(urlPatterns = {"/Login", "/Logout"})
public class UserLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserLoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher disp;
		String url_path = request.getRequestURI().substring(request.getContextPath().length());
		if (url_path.equals("/Login")) {
			if (request.getSession().getAttribute("userID") != null) {
				// User is already logged in, prevent him from double-logging.
				disp = getServletContext().getRequestDispatcher("/already_logged.jsp");
			} else {
				disp = getServletContext().getRequestDispatcher("/login.jsp");
			}
			disp.forward(request, response);
		}
		else if (url_path.equals("/Logout")){
			if (request.getSession().getAttribute("userID") != null) {
				//Remove Cookies
				Cookie[] cookies = request.getCookies();
				
				if(cookies != null){
					for(Cookie ck : cookies){
						if(ck.getName().equals("common_picks")){
							ck.setMaxAge(0);
							response.addCookie(ck);
						}
						if(ck.getName().equals("uncommon_picks")){
							ck.setMaxAge(0);
							response.addCookie(ck);
						}
					}
				}
				// Destroy session.
				request.getSession().invalidate();
			}
			response.sendRedirect(request.getContextPath());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean login_success = false;
		String hashed_password = "";
		String message = "Incorect username or password"; // Default error message.
		// Gather Form data
		
		String username = "";
		String password = "";
		username = request.getParameter("Username");
		password = request.getParameter("Password");
		UserDAOI dao = new UserDAO();
		User myuser = dao.findByID(username);
		if(myuser != null){
			// User exists.
			hashed_password = HelperFunctions.hash(password);
			if(hashed_password.equals(myuser.getPassword())){
				// Password match.
				if(myuser.getAccess_lvl() != 0){
					login_success = true;
				}
				else{
					// User not activated.
					message = "You are not activated yet.";
				}
			}
		}
		
		RequestDispatcher disp; 
		
		if(login_success){
			request.getSession().setAttribute("userID", username);
			request.getSession().setAttribute("access", myuser.getAccess_lvl());
			
			// Calculate recommendations for user using cosine similarity.
			UserDAOI userdao = new UserDAO();
			AuctionDAOI auctdao = new AuctionDAO();
			List<User> allusers = userdao.listFrequentBidders(myuser);
			List<Auction> my_user_bids = auctdao.findUserUniqueBids(myuser);
			System.out.println("Starting process with users: " + allusers.size());
			if (!my_user_bids.isEmpty()) {
				int common_aucts;
				double cosine_sim;
				int k_neighbors=5;
				User[] k_nearest_users=new User[k_neighbors];
				double[] neighbors_cosine=new double[k_neighbors];
				int neighbors_found=0;
				for (User u : allusers) {
					common_aucts = 0;
					List<Auction> check_user_bids = auctdao.findUserUniqueBids(u);
					for (Auction check_aucts : check_user_bids) {
						for (Auction my_aucts : my_user_bids) {
							if (check_aucts.getAuctionId() == my_aucts.getAuctionId())
								common_aucts++;
						}
					}
					cosine_sim = common_aucts / (Math.sqrt(check_user_bids.size()) * Math.sqrt(my_user_bids.size()));
					if(cosine_sim>0){
						if(neighbors_found<k_neighbors){
							k_nearest_users[neighbors_found]=u;
							neighbors_cosine[neighbors_found]=cosine_sim;
							neighbors_found++;
						}
						else{
							int less_similar=0;
							for(int i=1;i<k_neighbors;i++){
								if(neighbors_cosine[i]<neighbors_cosine[less_similar])
									less_similar=i;
							}
							k_nearest_users[less_similar]=u;
							neighbors_cosine[less_similar]=cosine_sim;
						}
					}
					System.out.println(
							"Cosine similarity(" + u.getUserId() + "," + myuser.getUserId() + ")=" + cosine_sim);
				}
				
				
				// Gather the auctions we are about to recommend.
				ArrayList<Integer> recommended_auctions = new ArrayList<>();
				for(int i=0;i<neighbors_found;i++){
					System.out.println(
							"Nearest Users(" + k_nearest_users[i].getUserId() + "," + neighbors_cosine[i] + ")");
					for(Auction a : auctdao.findUserUniqueActiveBids(k_nearest_users[i], new Date())){
						if(!my_user_bids.contains(a)){
							recommended_auctions.add(a.getAuctionId());
						}
					}
				}
				
				// Find unique auction ids.
				Set<Integer> auction_set = new HashSet<>(); // SIngle instance of each auction id.
				auction_set.addAll(recommended_auctions);
				
				// Find frequency of each auction id.
				List<AuctionFrequency> common_picks = new ArrayList<>();
				ArrayList<Integer> uncommon_picks = new ArrayList<>();
				System.out.println("rec auctions:"+recommended_auctions);
				for(Integer a : auction_set){
					int freq = Collections.frequency(recommended_auctions, a);
					if(freq > 1){ // Only care for common.
						common_picks.add(new AuctionFrequency(a, freq));
					}
					else{
						uncommon_picks.add(a);
					}
				}
				
				// Sort common picks.
				Collections.sort(common_picks, new AuctionFrequencyComparator());
				System.out.println("commonpicks:"+common_picks);
				
				// Right here common_picks contains all common auctions between nn
				// and uncommon picks contails the others mixed.
				
				
				
				// Populate cookies.
				int max_auct_ids=5;
				String common_picks_cookie="";
				System.out.println("Common picks size:"+common_picks.size());
				int picks_send=0;
				for(AuctionFrequency cp : common_picks){	
					if(picks_send<max_auct_ids){
						common_picks_cookie+=cp.getAuctionId();
						common_picks_cookie+="-";
						picks_send++;
					}
				}
				System.out.println("Common picks cookie:"+common_picks_cookie);
				String uncommon_picks_cookie="";
				System.out.println("Uncommon picks size:"+uncommon_picks.size());
				if(picks_send<max_auct_ids){
					for(Integer ucp : uncommon_picks){
						if(picks_send<max_auct_ids){
							uncommon_picks_cookie+=ucp.toString();
							uncommon_picks_cookie+="-";
							picks_send++;
						}
					}
				}
				System.out.println("Uncommon picks cookie:"+uncommon_picks_cookie);
				
				Cookie common_picks_ck = new Cookie("common_picks",common_picks_cookie);
			    Cookie uncommon_picks_ck = new Cookie("uncommon_picks",uncommon_picks_cookie);
		
			    common_picks_ck.setMaxAge(60*60*24);   
			    uncommon_picks_ck.setMaxAge(60*60*24); 
		
			    response.addCookie( common_picks_ck );
			    response.addCookie( uncommon_picks_ck );
			    response.setContentType("text/html");
			     
			} else {
				System.out.println("User has no bids,print top items");
			}
			
			response.sendRedirect(request.getContextPath()); // Return to home page;
		}
		else{
			request.setAttribute("error", message);
			disp = getServletContext().getRequestDispatcher("/login.jsp");
			disp.forward(request, response);
		}
	}

}
