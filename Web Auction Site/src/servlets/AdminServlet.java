package servlets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

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
import org.xml.sax.InputSource;

import dao.CategoryDAO;
import dao.CategoryDAOI;
import dao.UserDAO;
import dao.UserDAOI;
import entities.Category;
import entities.User;

/**
 * Servlet implementation class AdminServlet
 */
@WebServlet("/Admin")
@MultipartConfig
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
			response.sendRedirect("Admin");
		}
		else{
			RequestDispatcher disp;
			disp = getServletContext().getRequestDispatcher("/admin.jsp");
		}
	}

	private void loadDataset(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		Part filePart = request.getPart("file");
		InputStream content = filePart.getInputStream();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		//factory.setValidating(true);
		factory.setIgnoringElementContentWhitespace(true);
		try{
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(content);
			NodeList nodeList = doc.getElementsByTagName("Item");
			Node current;
			CategoryDAOI dao = new CategoryDAO();
			for(int i = 0; i < nodeList.getLength(); i++){
				current = nodeList.item(i);
				if(current.getNodeType() == Node.ELEMENT_NODE){
					ArrayList<String> elemList = new ArrayList<>();
					Element e = (Element) current;
					for(int j = 0; j < e.getElementsByTagName("Category").getLength(); j++){
						Category cat = new Category();
						cat.setName(e.getElementsByTagName("Category").item(j).getTextContent());
						if(j != 0){ // A parent category exists.
							cat.setParent(elemList.get(j-1));
						}
						dao.create(cat);
						elemList.add(e.getElementsByTagName("Category").item(j).getTextContent());
					}
				}
			}
			response.sendRedirect("Admin?message=" + URLEncoder.encode("Import successful.", "UTF-8"));
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
}
