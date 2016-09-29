package servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.UserDAO;
import dao.UserDAOI;
import dao.AuctionDAO;
import dao.AuctionDAOI;
import entities.User;
import entities.Auction;
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
			
			// Calculate recommendations for user and give him appropriate cookies.
	        if(request.getSession().getAttribute("userID") != null){
	            UserDAOI userdao = new UserDAO();
	            AuctionDAOI auctdao= new AuctionDAO();
	            List<User> allusers = userdao.listFrequentBidders();
	            List<Auction> my_user_bids = auctdao.findUserUniqueBids(myuser);
	            System.out.println("Starting process with users: " + allusers.size());
	            if(!my_user_bids.isEmpty()){
	            	int common_aucts;
	            	double cosine_sim;
	            	for(User u : allusers){
	            		common_aucts=0;
	            		List<Auction> check_user_bids = auctdao.findUserUniqueBids(u);
	            		for(Auction check_aucts : check_user_bids){
	            			for(Auction my_aucts : my_user_bids){
	            				if(check_aucts.getAuctionId() == my_aucts.getAuctionId())
	            					common_aucts++;
	            			}
	            		}
	            		cosine_sim=common_aucts/(Math.sqrt(check_user_bids.size())*Math.sqrt(my_user_bids.size()));
	            		System.out.println("Cosice similarity(" + u.getUserId() + "," + myuser.getUserId() + ")="+ cosine_sim);
	            	}
	            }
	            else{
	            	System.out.println("User has no bids,print top items");
	            }
	            System.out.println("Ending process..");
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
