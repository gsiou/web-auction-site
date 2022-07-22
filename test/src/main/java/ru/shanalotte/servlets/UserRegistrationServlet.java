package ru.shanalotte.servlets;

import java.io.IOException;

import com.google.gson.Gson;
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
import ru.shanalotte.dao.AuctionDAO;
import ru.shanalotte.dao.UserDAOImpl;
import ru.shanalotte.dao.UserDAO;
import ru.shanalotte.entities.User;
import ru.shanalotte.utils.HelperFunctions;

/**
 * Servlet implementation class UserRegistrationServlet
 */
@WebServlet("/Registration")
public class UserRegistrationServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private static final int minPassword = 4;


  @Autowired
  private UserDAO userDAO;

  @Autowired
  private AuctionDAO auctionDAO;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    ApplicationContext applicationContext = (AnnotationConfigApplicationContext) config.getServletContext().getAttribute("springcontext");
    final AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
    beanFactory.autowireBean(this);
  }


  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    RequestDispatcher disp;
    if (request.getSession().getAttribute("userID") != null) {
      // User is already logged in, he can't register another account.
      disp = getServletContext().getRequestDispatcher("/already_logged.jsp");
    } else {
      disp = getServletContext().getRequestDispatcher("/register.jsp");
    }
    disp.forward(request, response);
  }

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String action = (String) request.getParameter("Action");
    if (action.equals("namecheck")) {
      JsonObject message = new JsonObject();
      JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);
      String username = (String) data.get("username").getAsString();
      User checkuser = userDAO.findByID(username);
      if (checkuser != null)
        message.addProperty("response", true);
      else
        message.addProperty("response", false);
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      response.getWriter().write(message.toString());
    } else {

      boolean register_success = false;
      String msg = "";

      // Gather form data.
      String userid = request.getParameter("Username");
      String password1 = request.getParameter("Password");
      String password2 = request.getParameter("Password_conf");
      String email = request.getParameter("Email");
      String address = request.getParameter("Address");
      String country = request.getParameter("Country");
      String phone = request.getParameter("Phone");
      String trn = request.getParameter("Trn");
      String longitude = request.getParameter("Longitude");
      String latitude = request.getParameter("Latitude");
      String hashed_password = "";
      // Validate form input.
      if (userid == null) {
        msg = "Username cannot be empty.";
      } else if (password1 == null) {
        msg = "Password cannot be empty.";
      } else if (password2 == null) {
        msg = "You have to verify your password.";
      } else if (email == null) {
        msg = "Email cannot be empty.";
      } else if (address == null) {
        msg = "Address cannot be empty.";
      } else if (country == null) {
        msg = "Country cannot be empty.";
      } else if (phone == null) {
        msg = "Phone cannot be empty.";
      } else if (trn == null) {
        msg = "Tax Registration Number cannot be empty.";
      } else {
        // We have what we need.

        if (password1.length() < minPassword) {
          // We cannot accept empty passwords.
          msg = "Password must be at least " +
              minPassword +
              " characters long.";
        } else if (!password1.equals(password2)) {
          // Passwords must match.
          msg = "Passwords do not match";
        } else {
          hashed_password = HelperFunctions.hash(password1);

          // Populate user object.
          User user = new User();
          user.setUserId(userid);
          user.setPassword(hashed_password);
          user.setBidRating(0);
          user.setSellRating(0);
          user.setCountry(country);
          user.setAddress(address);
          user.setPhone(phone);
          user.setEmail(email);
          user.setTrn(trn);

          // If this is the first user of the system, make him admin.
          if (userDAO.userCount() == 0) {
            user.setAccessLvl(100);
          } else {
            user.setAccessLvl(0); // Default access level.
          }

          if (!latitude.equals("") && !longitude.equals("")) {
            user.setLatitude(Float.parseFloat(latitude));
            user.setLongitude(Float.parseFloat(longitude));
          }

          if (userDAO.create(user)) {
            // Success.
            msg = "User registration succeded.";
            register_success = true;
          } else {
            // Duplicate username.
            msg = "User with that username exists.";
          }
        }
      }
      RequestDispatcher disp;
      request.setAttribute("message", msg);
      if (register_success) {
        // Redirect to success page
        disp = getServletContext().getRequestDispatcher("/reg_success.jsp");
      } else {
        // Redirect back to registration page with message.
        disp = getServletContext().getRequestDispatcher("/register.jsp");
      }
      disp.forward(request, response);
    }
  }

}
