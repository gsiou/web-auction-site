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
import ru.shanalotte.config.PropertiesHolder;
import ru.shanalotte.dao.AuctionDAO;
import ru.shanalotte.dao.UserDAO;
import ru.shanalotte.dto.RegistrationRequestData;
import ru.shanalotte.dto.RegistrationDataValidationStatus;
import ru.shanalotte.entities.User;
import ru.shanalotte.service.RegistrationDtoValidator;

@WebServlet("/Registration")
public class UserRegistrationServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  RequestDispatcher disp;
  @Autowired
  private UserDAO userDAO;
  @Autowired
  private AuctionDAO auctionDAO;
  @Autowired
  private RegistrationDtoValidator registrationDtoValidator;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    ApplicationContext applicationContext = (AnnotationConfigApplicationContext) config.getServletContext().getAttribute("springcontext");
    final AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
    beanFactory.autowireBean(this);
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    if (userIsAlreadyLoggedIn(request)) {
      showYouAreAlreadyLoggedMessage(request, response);
    } else {
      redirectToRegistrationPage(request, response);
    }
  }

  public void showYouAreAlreadyLoggedMessage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher("/already_logged.jsp");
    requestDispatcher.forward(request, response);
  }

  public void redirectToRegistrationPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher("/register.jsp");
    requestDispatcher.forward(request, response);
  }

  private boolean userIsAlreadyLoggedIn(HttpServletRequest request) {
    return request.getSession().getAttribute("userID") != null;
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String action = request.getParameter("Action");
    if (action.equals("namecheck")) {
      answerIfRequestedUserLoginAlreadyExists(request, response);
    } else {
      processRegistration(request, response);
    }
  }

  private void processRegistration(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    RegistrationRequestData registrationData = extractRegistrationDataFromRequest(request);
    RegistrationDataValidationStatus registrationDataValidationStatus = registrationDtoValidator.validate(registrationData);
    if (!registrationDataValidationStatus.isValidated()) {
      notifyUserThatRegistrationDataIsInvalid(request, response, registrationDataValidationStatus);
    } else if (isUserAlreadyExists(registrationData.getUserid())) {
      notifyUserThatUsernameAlreadyTaken(request, response);
    } else {
      createUser(registrationData);
      notifyUserAboutSuccessfulRegistration(request, response);
    }
  }

  private void createUser(RegistrationRequestData registrationData) {
    User userToCreate = User.fromDto(registrationData);
    setAdminStatusIfItIsFirstUser(userToCreate);
    userDAO.create(userToCreate);
  }

  private void notifyUserThatUsernameAlreadyTaken(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    request.setAttribute("message", "User with that username exists.");
    RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher("/register.jsp");
    requestDispatcher.forward(request, response);
  }

  private void notifyUserAboutSuccessfulRegistration(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    request.setAttribute("message", "Successfully registered");
    RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher("/reg_success.jsp");
    requestDispatcher.forward(request, response);
  }

  private void notifyUserThatRegistrationDataIsInvalid(HttpServletRequest request, HttpServletResponse response, RegistrationDataValidationStatus registrationDataValidationStatus) throws ServletException, IOException {
    request.setAttribute("message", registrationDataValidationStatus.getValidationErrorMessage());
    RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher("/register.jsp");
    requestDispatcher.forward(request, response);
  }

  private RegistrationRequestData extractRegistrationDataFromRequest(HttpServletRequest request) {
    return RegistrationRequestData.from(request);
  }

  private void setAdminStatusIfItIsFirstUser(User user) {
    if (userDAO.userCount() == 0) {
      user.setAccessLvl(PropertiesHolder.ADMIN_ACCESS_LEVEL);
    } else {
      user.setAccessLvl(0);
    }
  }

  private void answerIfRequestedUserLoginAlreadyExists(HttpServletRequest request, HttpServletResponse response) throws IOException {
    JsonObject message = new JsonObject();
    JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);
    String username = data.get("username").getAsString();
    message.addProperty("response", isUserAlreadyExists(username));
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(message.toString());
  }

  private boolean isUserAlreadyExists(String username) {
    return userDAO.findByID(username) != null;
  }

}
