package ru.shanalotte.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.shanalotte.config.MessageManager;
import ru.shanalotte.config.PropertiesHolder;
import ru.shanalotte.dao.AuctionDAO;
import ru.shanalotte.dao.CategoryDAO;
import ru.shanalotte.dao.UserBidDAO;
import ru.shanalotte.dao.UserDAO;
import ru.shanalotte.entities.Auction;
import ru.shanalotte.entities.Category;
import ru.shanalotte.entities.User;
import ru.shanalotte.entities.UserBid;
import ru.shanalotte.service.XmlReader;
import ru.shanalotte.service.XmlService;
import ru.shanalotte.service.XmlWriter;
import ru.shanalotte.xmlentities.Bid;
import ru.shanalotte.xmlentities.Bidder;
import ru.shanalotte.xmlentities.Bids;
import ru.shanalotte.xmlentities.Item;
import ru.shanalotte.xmlentities.Items;
import ru.shanalotte.xmlentities.LocationElem;
import ru.shanalotte.xmlentities.UserElem;

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
  private CategoryDAO categoryDAO;
  @Autowired
  private UserBidDAO userBidDAO;
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

  private void showExportFailedMessage(HttpServletResponse response) {
    try {
      response.sendRedirect("Admin?message=" + URLEncoder.encode("Export failed.", StandardCharsets.UTF_8));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void exportDataset(Marshaller marshaller, HttpServletResponse response) {
    List<Auction> auctions = auctionDAO.list();
    List<Item> xmlEntries = xmlWriter.auctionsToXml(auctions);
    Items xmlData = new Items();
    xmlData.setItems(xmlEntries);
    sendXmlFileToUser(marshaller, response, xmlData);
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

  private void loadDataset(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    JAXBContext jc;
    Items items;
    try {
      jc = JAXBContext.newInstance(Items.class);
      Unmarshaller unmarshaller = jc.createUnmarshaller();
      Part filePart = request.getPart("file");
      InputStream content = filePart.getInputStream();
      items = (Items) unmarshaller.unmarshal(content);
    } catch (Exception e1) {
      response.sendRedirect("Admin?message=" + URLEncoder.encode("Import failed.", "UTF-8"));
      return;
    }


    for (Item i : items.getItems()) {
      Category cat;
      String parent;
      parent = null;
      Auction auc = new Auction();
      List<Category> categories = new ArrayList<>();
      Category placeholder = null;
      for (String c : i.getCategories()) {
        cat = categoryDAO.find(c);
        if (cat == null) {
          // Does not exist in db yet.
          placeholder = new Category();
          placeholder.setName(c);
          placeholder.setParent(parent);
          if (categories.contains(placeholder)) {
            cat = new Category();
            cat.setName(c + " (" + parent + ")");
            cat.setParent(parent);
            cat.setAuctions(new ArrayList<Auction>());
          } else {
            cat = new Category();
            cat.setName(c);
            cat.setParent(parent);
            cat.setAuctions(new ArrayList<Auction>());
          }
        }
        cat.getAuctions().add(auc);
        categories.add(cat);
        parent = cat.getName();
      }
      auc.setCategories(categories);

      User creator = userDAO.findByID(i.getSeller().getUserID());
      if (creator == null) {
        creator = new User();
        creator.setUserId(i.getSeller().getUserID());
        creator.setAccessLvl(1);
        creator.setPassword("");
        creator.setEmail("");
      }
      creator.setCountry(i.getCountry());
      creator.setSellRating(i.getSeller().getRating());
      if (i.getLocation().getLatitude() != 0 && i.getLocation().getLatitude() != 0) {
        creator.setLatitude(i.getLocation().getLatitude());
        creator.setLongitude(i.getLocation().getLongitude());
      }

      //System.out.println("Loc: " + i.getLocation().getLatitude() + i.getLocation().getLongitude());
      auc.setName(i.getName());
      auc.setDescription(i.getDescription());
      auc.setCreator(creator);
      auc.setCountry(i.getCountry());
      auc.setLocation(i.getLocation().getLocation());
      auc.setLatitude(i.getLocation().getLatitude());
      auc.setLongitude(i.getLocation().getLongitude());
      auc.setNumOfBids(i.getNumber_of_bids());

      try {
        auc.setStartingBid(Float.parseFloat(i.getFirst_bid().substring(1)));
      } catch (NumberFormatException ex) {
        auc.setStartingBid(0);
      }

      if (i.getBuy_Price() != null) {
        try {
          auc.setBuyPrice(Float.parseFloat(i.getBuy_Price().substring(1)));
        } catch (NumberFormatException ex) {
          auc.setBuyPrice(0);
        }
      }

      try {
        auc.setCurrentBid(Float.parseFloat(i.getCurrently().substring(1)));
      } catch (NumberFormatException ex) {
        auc.setCurrentBid(0);
      }

      SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yy HH:mm:ss", Locale.ENGLISH);
      try {
        auc.setStartTime(sdf.parse(i.getStarted()));
      } catch (ParseException e) {
        auc.setStartTime(null);
      }

      try {
        //auc.setExpiration_time(sdf.parse(i.getEnds()));
        auc.setExpirationTime(sdf.parse("Jan-01-17 23:30:01"));
      } catch (ParseException e) {
        auc.setExpirationTime(null);
      }

      auctionDAO.create(auc);

      if (i.getBids().getBids() != null) {
        for (Bid b : i.getBids().getBids()) {
          User bidder = userDAO.findByID(b.getBidder().getUserID());
          if (bidder == null) {
            bidder = new User();
            bidder.setUserId(b.getBidder().getUserID());
            bidder.setPassword("");
            bidder.setAccessLvl(1);
            bidder.setEmail("");
          }
          bidder.setCountry(b.getBidder().getCountry());
          bidder.setAddress(b.getBidder().getLocation());
          bidder.setBidRating(b.getBidder().getRating());

          float price;
          try {
            price = Float.parseFloat(b.getAmount().substring(1));
          } catch (NumberFormatException ex) {
            price = 0;
          }

          Date time;
          try {
            time = sdf.parse(i.getStarted());
          } catch (ParseException e) {
            time = null;
          }
          userBidDAO.create(bidder, auc, time, price);
        }
      }
    }
    response.sendRedirect("Admin?message=" + URLEncoder.encode("Import successful.", "UTF-8"));
  }
}
