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

/**
 * Servlet implementation class AdminServlet
 */
@WebServlet("/Admin")
public class AdminServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
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
			// Gather user list.
			UserDAOI dao = new UserDAO();
			List<User> userList = dao.list();
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
		}
		
		String action;
		String userid;
		String message = "";
		boolean success = false;
		action = (String) request.getParameter("action");
		userid = (String) request.getParameter("userid");
		if(action != null || userid != null){
			if(action.equals("activate") || action.equals("deactivate")){
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
		}
		else{
			message = "Invalid data";
		}
		if(success){
			response.sendRedirect("Admin");
		}
		else{
			disp = getServletContext().getRequestDispatcher("/admin.jsp");
		}
	}

}
