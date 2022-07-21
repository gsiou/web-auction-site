package ru.shanalotte.servlets;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.shanalotte.dao.MessageDAO;
import ru.shanalotte.dao.MessageDAOI;
import ru.shanalotte.dao.UserDAO;
import ru.shanalotte.dao.UserDAOI;
import ru.shanalotte.entities.Message;
import ru.shanalotte.entities.User;

/**
 * Servlet implementation class MessageServlet
 */
@WebServlet("/Messages")
public class MessageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private static final int ENTRIES_PER_MESSAGE_PAGE = 10;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MessageServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// First check if our user is logged in.
		RequestDispatcher disp;
		if(request.getSession().getAttribute("userID") == null){
			disp = getServletContext().getRequestDispatcher("/loginerror.jsp");
			disp.forward(request, response);
			return;
		}
		
		if(request.getParameter("sendto") != null){
			request.setAttribute("send_username", request.getParameter("sendto"));
		}
		
		if(request.getParameter("subject") != null){
			request.setAttribute("subject", request.getParameter("subject"));
		}
		disp = request.getRequestDispatcher("/message_list.jsp");
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
			return ;
		}
		
		if(action.equals("send")){
			// AJAX call 
			// Gather data
			JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);
			String user, subject, text;
			user = data.get("msg-to").getAsString();
			subject = data.get("msg-subject").getAsString();
			text = data.get("msg-body").getAsString();
			
			// Check if everything is filled out
			String message = "";
			boolean success = false;
			if(user == null || user.equals("")){
				message = "You must specify the recipient username";
			}
			else if(subject == null || subject.equals("")){
				message = "You must give a subject";
			}
			else if(text == null || text.equals("")){
				message = "Message cannot be empty";
			}
			else{
				// Check if user exists.
				User recipient;
				UserDAOI udao = new UserDAO();
				recipient = udao.findByID(user);
				if(recipient == null){
					message = "User not found";
				}
				else{
					// Get our user.
					User sender = udao.findByID(request.getSession().getAttribute("userID").toString());
					
					// Create message.
					Message msg = new Message();
					msg.setRead(false);
					msg.setShowReceived(true);
					msg.setShowSent(true);
					msg.setSubject(subject);
					msg.setText(text);
					msg.setUserFrom(sender);
					msg.setUserTo(recipient);
					msg.setTime(new Date());
					MessageDAOI msgdao = new MessageDAO();
					msgdao.create(msg);
					success = true;
					message = "Message sent";
					
				}
			}
			JsonObject reply = new JsonObject();
			reply.addProperty("success",success);
			reply.addProperty("message", message);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(reply.toString());
		}
		else if(action.equals("fetch")){
			JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);
			String type = (String)data.get("type").getAsString();
			String page = (String)data.get("page").getAsString();
			
			if(type == null || !(type.equals("sent") || type.equals("received"))){
			}
			else if(page == null || page.equals("")){
			}
			else{
				// Fetch user's messages according to type and page
				int entries_per_page = ENTRIES_PER_MESSAGE_PAGE;
				String userid = request.getSession().getAttribute("userID").toString();
				UserDAOI udao = new UserDAO();
				User myuser = udao.findByID(userid); // Get logged in user.
				MessageDAOI msgdao = new MessageDAO();
				
				// Gather all messages.
				List<Message> msg_list = null;
				if(type.equals("sent")){
					msg_list = msgdao.getSentOf(myuser, Integer.parseInt(page), entries_per_page);
				}
				else{
					msg_list = msgdao.getReceivedOf(myuser, Integer.parseInt(page), entries_per_page);
				}
				
				// Prepare Json Response.
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
				JsonArray msg_arr = new JsonArray();
				JsonObject msg;
				
				for(Message m : msg_list){
					msg = new JsonObject();
					if(type.equals("sent")){
						msg.addProperty("user", m.getUserTo().getUserId());
					}
					else{
						msg.addProperty("user", m.getUserFrom().getUserId());
					}
					msg.addProperty("subject", m.getSubject());
					msg.addProperty("date", sdf.format(m.getTime()));
					msg.addProperty("body", m.getText());
					msg.addProperty("id", m.getId());
					if(myuser.getUserId().equals(m.getUserTo().getUserId())){
						// In this case we care about read status
						msg.addProperty("read", m.isRead());
					}
					else{
						// When user is the one that sent the message
						// he/she does not care whether it is read or not.
						msg.addProperty("read", true);
					}
					msg_arr.add(msg);
				}
				
				int pages_number; // Let the front end know how many pages exists. 
				if(type.equals("sent")){
					pages_number = (int) (Math.ceil(msgdao.getCountSent(myuser)/ (double) ENTRIES_PER_MESSAGE_PAGE));
				}
				else{
					pages_number = (int) (Math.ceil(msgdao.getCountReceived(myuser)/ (double) ENTRIES_PER_MESSAGE_PAGE));
				}
				
				JsonObject reply = new JsonObject();
				reply.add("messages", msg_arr);
				reply.addProperty("pages", pages_number);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(reply.toString());
			}
		}
		else if(action.equals("read")){
			JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);
			int message_id = (int) data.get("message_id").getAsInt();	
			
			MessageDAOI msgdao = new MessageDAO();
			Message msg = msgdao.find(message_id);
			
			String userid = request.getSession().getAttribute("userID").toString();
			UserDAOI udao = new UserDAO();
			User myuser = udao.findByID(userid); // Get logged in user.
			
			if(msg != null && msg.getUserTo().getUserId().equals(myuser.getUserId())) {
				msg.setRead(true);
			}
		}
		else if(action.equals("count")){
			String userid = request.getSession().getAttribute("userID").toString();
			UserDAOI udao = new UserDAO();
			User myuser = udao.findByID(userid); // Get logged in user.
			MessageDAOI msgdao = new MessageDAO();
			long msg_count = msgdao.getCountUnreadOf(myuser);
			JsonObject reply = new JsonObject();
			reply.addProperty("count", msg_count);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(reply.toString());
		}
		else if(action.equals("delete")){
			JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);
			int message_id = (int) data.get("message_id").getAsInt();
			String message_type = data.get("message_type").getAsString();
			String userid = request.getSession().getAttribute("userID").toString();
			
			UserDAOI udao = new UserDAO();
			User myuser = udao.findByID(userid);
			MessageDAOI msgdao = new MessageDAO();
			
			Message mymsg = msgdao.find(message_id);
			if(message_type.equals("sent")){
				if(!mymsg.getUserFrom().getUserId().equals(myuser.getUserId())){
					return ; // Cant delete another persons messages.
				}
			}
			else{
				if(!mymsg.getUserTo().getUserId().equals(myuser.getUserId())){
					return ;
				}
			}
			
			if(message_type.equals("sent")){
				mymsg.setShowSent(false);
			}
			else if(message_type.equals("received")){
				mymsg.setShowReceived(false);
			}
			
			if(mymsg.isShowReceived() == false && mymsg.isShowSent() == false){
				// Delete
				msgdao.delete(mymsg.getId());
			}
			else{
				// Update
				msgdao.update(mymsg);
			}
		}
	}
}
