package ru.shanalotte.servlets;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.shanalotte.config.MessageManager;
import ru.shanalotte.config.PropertiesHolder;
import ru.shanalotte.dao.AuctionDAO;
import ru.shanalotte.dao.UserDAO;
import ru.shanalotte.entities.Auction;
import ru.shanalotte.entities.User;
import ru.shanalotte.service.XmlReader;
import ru.shanalotte.service.XmlService;
import ru.shanalotte.service.XmlWriter;
import ru.shanalotte.xmlentities.Item;
import ru.shanalotte.xmlentities.Items;

@WebServlet("/Admin")
@MultipartConfig
public class AdminServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private static final int USERS_PER_PAGE = 20;

  @Autowired
  private UserDAO userDAO;
  @Autowired
  private AuctionDAO auctionDAO;
  @Autowired
  private MessageManager messageManager;
  @Autowired
  private XmlService xmlService;
  @Autowired
  private XmlReader xmlReader;
  @Autowired
  private XmlWriter xmlWriter;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    ApplicationContext applicationContext = loadSpringContextFromServletContext(config);
    final AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
    beanFactory.autowireBean(this);
  }

  private AnnotationConfigApplicationContext loadSpringContextFromServletContext(ServletConfig config) {
    return (AnnotationConfigApplicationContext) config.getServletContext().getAttribute(PropertiesHolder.SPRINT_CONTEXT_ATTRIBUTE_NAME_IN_SERVLET_CONTEXT);
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    if (requestHasAdminAuthorizationLevel(request)) {
      processAdminRequest(request, response);
    } else {
      showUnauthorizedAdminRequestErrorMessage(request, response);
    }
  }

  private void processAdminRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String type = determineRequestType(request);
    int maxPages = determineMaxPagesAmountPerType(type);
    int currentPage = determineCurrentPage(request, maxPages);
    List<User> userList;
    if (type.equals("all")) {
      userList = userDAO.listUsersOfPage(currentPage, USERS_PER_PAGE);
    } else {
      userList = userDAO.listUnactivatedUsersOfPage(currentPage, USERS_PER_PAGE);
    }
    request.setAttribute("currentPage", currentPage);
    request.setAttribute("totalPages", maxPages);
    request.setAttribute("userType", type);
    request.setAttribute("userList", userList);
    RequestDispatcher requestDispatcher;
    requestDispatcher = getServletContext().getRequestDispatcher("/admin.jsp");
    requestDispatcher.forward(request, response);
  }

  private String determineRequestType(HttpServletRequest request) {
    String type = request.getParameter("type");
    if (type == null || (!type.equals("all") && !type.equals("unactivated"))) {
      type = "all";
    }
    return type;
  }

  private int determineMaxPagesAmountPerType(String type) {
    int maxPages;
    if (type.equals("all")) {
      maxPages = (int) Math.ceil((float) userDAO.userCount() / USERS_PER_PAGE);
    } else {
      maxPages = (int) Math.ceil((float) userDAO.unactivatedUserCount() / USERS_PER_PAGE);
    }
    return maxPages;
  }

  private int determineCurrentPage(HttpServletRequest request, int maxPages) {
    int currentPage = 0;
    if (request.getParameter("page") != null) {
      currentPage = Integer.parseInt(request.getParameter("page"));
    }
    if (currentPage < 0 || currentPage > maxPages - 1) {
      currentPage = 0;
    }
    return currentPage;
  }

  private boolean requestHasAdminAuthorizationLevel(HttpServletRequest request) {
    return request.getSession().getAttribute("userID") != null &&
        (int) request.getSession().getAttribute("access") == PropertiesHolder.ADMIN_ACCESS_LEVEL;
  }

  private void showUnauthorizedAdminRequestErrorMessage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    request.setAttribute("error", messageManager.getMessage("login.unauthorizedAccess.admin"));
    RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher("/loginerror.jsp");
    requestDispatcher.forward(request, response);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    if (!requestHasAdminAuthorizationLevel(request)) {
      showUnauthorizedAdminRequestErrorMessage(request, response);
      return;
    }
    String action = request.getParameter("action");
    if (action != null) {
      resolveAction(request, response, action);
    } else {
      RequestDispatcher requestDispatcher;
      requestDispatcher = getServletContext().getRequestDispatcher("/admin.jsp");
      requestDispatcher.forward(request, response);
    }
  }

  private void resolveAction(HttpServletRequest request, HttpServletResponse response, String action) throws IOException, ServletException {
    if (action.equals("activate") || action.equals("deactivate")) {
      doActivationOrDeactivation(request, response);
    } else if (action.equals("loadDataset")) {
      loadDataset(request, response);
    } else if (action.equals("exportDataset")) {
      exportDataset(response);
    }
  }

  private void doActivationOrDeactivation(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    String message = "";
    boolean successfullyActivatedOrDeactivated = false;
    String userIdToActivate = request.getParameter("userid");
    String action = request.getParameter("action");
    if (userIdToActivate != null) {
      User userToActivate = userDAO.findByID(userIdToActivate);
      if (userToActivate != null) {
        userDAO.changeAccess(userToActivate, action.equals("activate") ? 1 : 0);
        successfullyActivatedOrDeactivated = true;
      } else {
        message = messageManager.getMessage("activation.userNotFound");
      }
    } else {
      message = messageManager.getMessage("activation.missingID");
    }
    if (successfullyActivatedOrDeactivated) {
      redirectAdminWhereHeWasBefore(request, response);
    } else {
      response.sendRedirect("Admin?message=" + URLEncoder.encode(message, "UTF-8"));
    }
  }

  private void redirectAdminWhereHeWasBefore(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String urlWhereAdminWasBefore = "Admin?";
    if (request.getParameter("page") != null) {
      urlWhereAdminWasBefore += "page=" + request.getParameter("page");
    }
    if (request.getParameter("type") != null) {
      urlWhereAdminWasBefore += "&type=" + request.getParameter("type");
    }
    response.sendRedirect(urlWhereAdminWasBefore);
  }

  private void exportDataset(HttpServletResponse response) throws IOException {
    xmlService.createXmlMarshaller().ifPresentOrElse(
        marshaller -> exportDataset(marshaller, response),
        () -> showExportFailedMessage(response)
    );
  }

  private void exportDataset(Marshaller marshaller, HttpServletResponse response) {
    List<Auction> allAuctions = auctionDAO.list();
    List<Item> xmlEntries = xmlWriter.auctionsToXml(allAuctions);
    Items xmlData = new Items();
    xmlData.setItems(xmlEntries);
    sendXmlFileToUser(marshaller, response, xmlData);
  }

  private void showExportFailedMessage(HttpServletResponse response) {
    try {
      response.sendRedirect("Admin?message=" + URLEncoder.encode("Export failed.", StandardCharsets.UTF_8));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  private void sendXmlFileToUser(Marshaller marshaller, HttpServletResponse response, Items items) {
    try {
      response.setContentType("text/plain");
      response.setHeader("Content-Disposition", "attachment;filename=itemsall.xml");
      marshaller.marshal(items, response.getOutputStream());
    } catch (JAXBException e) {
      showExportFailedMessage(response);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void loadDataset(HttpServletRequest request, HttpServletResponse response) {
    xmlReader.unmarshalXmlData(request).ifPresentOrElse(
        xmlData -> importDatasetFromXml(xmlData, response),
        () -> showImportFailedMessage(response)
    );
  }

  public void importDatasetFromXml(Items items, HttpServletResponse response) {
    for (Item nextXmlEntry : items.getItems()) {
      xmlReader.importXmlEntry(nextXmlEntry);
    }
    try {
      response.sendRedirect("Admin?message=" + URLEncoder.encode("Import successful.", "UTF-8"));
    } catch (IOException e) {
      e.printStackTrace();
      showImportFailedMessage(response);
    }
  }

  public void showImportFailedMessage(HttpServletResponse response) {
    try {
      response.sendRedirect("Admin?message=" + URLEncoder.encode("Import failed.", "UTF-8"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
