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
import entities.User;
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
	            List<User> allusers = userdao.listFrequentBidders();
	            System.out.println("Starting process with users: " + allusers.size());
	            for(User u : allusers){
	            	// Cosine similarity goes here.
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
