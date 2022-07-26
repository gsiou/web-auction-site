package com.github.gsiou.servlets;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import com.github.gsiou.dao.MessageDAO;
import com.github.gsiou.dao.UserDAO;
import com.github.gsiou.entities.Message;
import com.github.gsiou.entities.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@WebServlet("/Messages")
public class MessageServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private static final int ENTRIES_PER_MESSAGE_PAGE = 10;

  @Autowired
  private MessageDAO messageDAO;

  @Autowired
  private UserDAO userDAO;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    ApplicationContext applicationContext = (AnnotationConfigApplicationContext) config.getServletContext().getAttribute("springcontext");
    final AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
    beanFactory.autowireBean(this);
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    if (request.getSession().getAttribute("userID") == null) {
      showLoginErrorMessage(request, response);
      return;
    }
    if (request.getParameter("sendto") != null) {
      request.setAttribute("send_username", request.getParameter("sendto"));
    }
    if (request.getParameter("subject") != null) {
      request.setAttribute("subject", request.getParameter("subject"));
    }
    RequestDispatcher disp = request.getRequestDispatcher("/message_list.jsp");
    disp.forward(request, response);
  }

  private void showLoginErrorMessage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    RequestDispatcher requestDispatcher;
    requestDispatcher = getServletContext().getRequestDispatcher("/loginerror.jsp");
    requestDispatcher.forward(request, response);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    if (request.getSession().getAttribute("userID") == null) {
      showLoginErrorMessage(request, response);
      return;
    }
    String action = request.getParameter("action");
    if (action == null) {
      return;
    }
    switch (action) {
      case "send":
        sendMessage(request, response);
        break;
      case "fetch":
        fetchMessage(request, response);
        break;
      case "read":
        readMessage(request);
        break;
      case "count":
        countUnreadMessages(request, response);
        break;
      case "delete":
        deleteMessage(request);
        break;
    }
  }


  private void sendMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
    JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);
    String sendToUser = data.get("msg-to").getAsString();
    String subject = data.get("msg-subject").getAsString();
    String text = data.get("msg-body").getAsString();
    String message = "";
    boolean success = false;
    if (sendToUser == null || sendToUser.equals("")) {
      message = "You must specify the recipient username";
    } else if (subject == null || subject.equals("")) {
      message = "You must give a subject";
    } else if (text == null || text.equals("")) {
      message = "Message cannot be empty";
    } else {
      User recipient = userDAO.findByID(sendToUser);
      if (recipient == null) {
        message = "Recipient not found";
      } else {
        User sender = userDAO.findByID(request.getSession().getAttribute("userID").toString());
        Message msg = new Message();
        msg.setRead(false);
        msg.setShowReceived(true);
        msg.setShowSent(true);
        msg.setSubject(subject);
        msg.setText(text);
        msg.setUserFrom(sender);
        msg.setUserTo(recipient);
        msg.setTime(new Date());
        messageDAO.create(msg);
        success = true;
        message = "Message sent";
      }
    }
    JsonObject reply = new JsonObject();
    reply.addProperty("success", success);
    reply.addProperty("message", message);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(reply.toString());
  }

  private void fetchMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
    JsonObject messageData = new Gson().fromJson(request.getReader(), JsonObject.class);
    String type = messageData.get("type").getAsString();
    String page = messageData.get("page").getAsString();
    if (type != null && (type.equals("sent") || type.equals("received"))) {
      if (page != null && !page.equals("")) {
        String userID = request.getSession().getAttribute("userID").toString();
        User loggedUser = userDAO.findByID(userID); // Get logged in user.
        List<Message> messageList = fetchUserMessageByTypeAndPage(type, page, loggedUser);
        JsonObject reply = prepareJsonReply(type, loggedUser, messageList);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(reply.toString());
      }
    }
  }

  private void readMessage(HttpServletRequest request) throws IOException {
    JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);
    int messageId = data.get("message_id").getAsInt();
    Message message = messageDAO.find(messageId);
    String userID = request.getSession().getAttribute("userID").toString();
    User loggedUser = userDAO.findByID(userID);
    if (message != null && message.getUserTo().getUserId().equals(loggedUser.getUserId())) {
      message.setRead(true);
    }
  }

  private void countUnreadMessages(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String userID = request.getSession().getAttribute("userID").toString();
    User loggerUser = userDAO.findByID(userID);
    long unreadMessagesCount = messageDAO.getCountUnreadOf(loggerUser);
    JsonObject reply = new JsonObject();
    reply.addProperty("count", unreadMessagesCount);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(reply.toString());
  }

  private void deleteMessage(HttpServletRequest request) throws IOException {
    JsonObject messageData = new Gson().fromJson(request.getReader(), JsonObject.class);
    int messageId = (int) messageData.get("message_id").getAsInt();
    String messageType = messageData.get("message_type").getAsString();
    String userID = request.getSession().getAttribute("userID").toString();
    User loggedUser = userDAO.findByID(userID);
    Message message = messageDAO.find(messageId);
    if (messageType.equals("sent")) {
      if (!message.getUserFrom().getUserId().equals(loggedUser.getUserId())) {
        return;
      }
    } else {
      if (!message.getUserTo().getUserId().equals(loggedUser.getUserId())) {
        return;
      }
    }
    if (messageType.equals("sent")) {
      message.setShowSent(false);
    } else if (messageType.equals("received")) {
      message.setShowReceived(false);
    }
    if (!message.isShowReceived() && !message.isShowSent()) {
      messageDAO.delete(message.getId());
    } else {
      messageDAO.update(message);
    }
  }

  private JsonObject prepareJsonReply(String type, User loggedUser, List<Message> messageList) {
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
    JsonArray messageArray = new JsonArray();
    JsonObject jsonObject;
    for (Message m : messageList) {
      jsonObject = new JsonObject();
      if (type.equals("sent")) {
        jsonObject.addProperty("user", m.getUserTo().getUserId());
      } else {
        jsonObject.addProperty("user", m.getUserFrom().getUserId());
      }
      jsonObject.addProperty("subject", m.getSubject());
      jsonObject.addProperty("date", sdf.format(m.getTime()));
      jsonObject.addProperty("body", m.getText());
      jsonObject.addProperty("id", m.getId());
      if (loggedUser.getUserId().equals(m.getUserTo().getUserId())) {
        jsonObject.addProperty("read", m.isRead());
      } else {
        jsonObject.addProperty("read", true);
      }
      messageArray.add(jsonObject);
    }
    int pagesNumber;
    if (type.equals("sent")) {
      pagesNumber = (int) (Math.ceil(messageDAO.getCountSent(loggedUser) / (double) ENTRIES_PER_MESSAGE_PAGE));
    } else {
      pagesNumber = (int) (Math.ceil(messageDAO.getCountReceived(loggedUser) / (double) ENTRIES_PER_MESSAGE_PAGE));
    }
    JsonObject reply = new JsonObject();
    reply.add("messages", messageArray);
    reply.addProperty("pages", pagesNumber);
    return reply;
  }

  private List<Message> fetchUserMessageByTypeAndPage(String type, String page, User loggedUser) {
    List<Message> messageList = null;
    if (type.equals("sent")) {
      messageList = messageDAO.getSentOf(loggedUser, Integer.parseInt(page), ENTRIES_PER_MESSAGE_PAGE);
    } else {
      messageList = messageDAO.getReceivedOf(loggedUser, Integer.parseInt(page), ENTRIES_PER_MESSAGE_PAGE);
    }
    return messageList;
  }
}
