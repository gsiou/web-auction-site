package ru.shanalotte.servlets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

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
import ru.shanalotte.dao.AuctionDAO;
import ru.shanalotte.dao.CategoryDAO;
import ru.shanalotte.dao.ImageDAO;
import ru.shanalotte.dao.UserBidDAO;
import ru.shanalotte.dao.UserDAO;
import ru.shanalotte.entities.Auction;
import ru.shanalotte.entities.Category;
import ru.shanalotte.entities.Image;
import ru.shanalotte.entities.User;

@WebServlet("/AuctionSubmit")
@MultipartConfig
public class AuctionSubmit extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private static final int MAX_IMAGES = 6;
  private static final int MAX_NAME_CHARS = 45;
  private static final int MAX_LOCATION_CHARS = 255;
  private static final int MAX_COUNTRY_CHARS = 45;

  @Autowired
  private UserDAO userDAO;

  @Autowired
  private AuctionDAO auctionDAO;

  @Autowired
  private CategoryDAO categoryDAO;

  @Autowired
  private UserBidDAO userBidDAO;

  @Autowired
  private ImageDAO imageDAO;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    ApplicationContext applicationContext = (AnnotationConfigApplicationContext) config.getServletContext().getAttribute("springcontext");
    final AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
    beanFactory.autowireBean(this);
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    RequestDispatcher disp;
    if (request.getSession().getAttribute("userID") == null) {
      showLoginErrorMessage(request, response);
      return;
    }
    String action = request.getParameter("action");
    if (action == null) {
      show404page(request, response);
    } else if (action.equals("submit")) {
      showSubmitPage(request, response);
    } else if (action.equals("edit")) {
      processEdit(request, response);
    } else {
      show404page(request, response);
    }
  }

  private void processEdit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String idString = request.getParameter("id");
    if (idString == null) {
      show404page(request, response);
      return;
    }
    int id;
    try {
      id = Integer.parseInt(idString);
    } catch (NumberFormatException e) {
      show404page(request, response);
      return;
    }
    Auction auction = auctionDAO.findByID(id);
    if (auction == null || auction.getStartTime() != null) {
      show404page(request, response);
      return;
    }
    if (userIsNotCreatorOfAnAuction(auction, getCurrentUser(request))) {
      show404page(request, response);
      return;
    }
    request.setAttribute("action", "edit");
    request.setAttribute("auctionId", id);
    request.setAttribute("auctionName", auction.getName());
    request.setAttribute("auctionDescription", auction.getDescription());
    request.setAttribute("auctionStartingBid", auction.getStartingBid());
    request.setAttribute("auctionBuyPrice", auction.getBuyPrice());
    Date expirationTime = auction.getExpirationTime();
    request.setAttribute("auctionEndYear", new SimpleDateFormat("yyyy").format(expirationTime));
    request.setAttribute("auctionEndMonth", new SimpleDateFormat("MM").format(expirationTime));
    request.setAttribute("auctionEndDay", new SimpleDateFormat("dd").format(expirationTime));
    request.setAttribute("auctionEndHour", new SimpleDateFormat("HH").format(expirationTime));
    request.setAttribute("auctionEndMinute", new SimpleDateFormat("mm").format(expirationTime));
    request.setAttribute("auctionLocation", auction.getLocation());
    request.setAttribute("auctionCountry", auction.getCountry());
    request.setAttribute("auctionLongitude", auction.getLongitude());
    request.setAttribute("auctionLatitude", auction.getLatitude());
    String formattedCategories = formatAuctionCategories(auction);
    request.setAttribute("auctionCategory", formattedCategories);
    RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher("/auction_submit.jsp");
    requestDispatcher.forward(request, response);
  }

  private String formatAuctionCategories(Auction auction) {
    String formattedCategories = ">";
    for (Category cat : auction.getCategories()) {
      formattedCategories += cat.getName() + ">";
    }
    return formattedCategories;
  }

  private User getCurrentUser(HttpServletRequest request) {
    return userDAO.findByID(request.getSession().getAttribute("userID").toString());
  }

  private boolean userIsNotCreatorOfAnAuction(Auction auction, User currentUser) {
    return !auction.getCreator().getUserId().equals(currentUser.getUserId());
  }

  private void show404page(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher("/404.html");
    requestDispatcher.forward(request, response);
  }

  private void showSubmitPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    User currentUser = getCurrentUser(request);
    String userCountry = currentUser.getCountry();
    String userLocation = currentUser.getAddress();
    request.setAttribute("auctionCountry", userCountry);
    request.setAttribute("auctionLocation", userLocation);
    request.setAttribute("action", "submit");
    RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher("/auction_submit.jsp");
    requestDispatcher.forward(request, response);
  }


  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String action = request.getParameter("action");
    if (action == null) {
      return;
    }
    switch (action) {
      case "submit":
      case "edit":
        saveOrUpdateAuction(request, response, action);
        break;
      case "fetch_categories":
        fetchCategories(request, response);
        break;
      case "activate":
        activateAuction(request, response);
        break;
    }
  }

  private void saveOrUpdateAuction(HttpServletRequest request, HttpServletResponse response, String action) throws ServletException, IOException {
    if (request.getSession().getAttribute("userID") == null) {
      showLoginErrorMessage(request, response);
      return;
    }
    boolean isSubmit = false;
    boolean isEdit = false;
    if (action.equals("submit")) {
      isSubmit = true;
    } else {
      isEdit = true;
    }
    Auction auction = null;
    if (isEdit) {
      auction = prepareAuctionForEditing(request, response);
      if (auction == null) return;
    } else {
      auction = new Auction();
    }
    String name = request.getParameter("name");
    String description = request.getParameter("description");
    String starting = request.getParameter("starting");
    String buyprice = request.getParameter("buyprice");
    String latitude = request.getParameter("latitude");
    String longitude = request.getParameter("longitude");
    String country = request.getParameter("country");
    String location = request.getParameter("location");
    String endsyear = request.getParameter("endsyear");
    String endsmonth = request.getParameter("endsmonth");
    String endsday = request.getParameter("endsday");
    String endshour = request.getParameter("endshour");
    String endsminute = request.getParameter("endsminute");
    String categories = request.getParameter("categories");
    String message = "";
    if (name == null || name.length() > MAX_NAME_CHARS) {
      message = "You have to specify a name! (Max characters: " + MAX_NAME_CHARS + ")";
    } else if (description == null) {
      message = "You have to give a description of the product";
    } else if (starting == null) {
      message = "You have to give a starting bid";
    } else if (country == null || country.length() > MAX_COUNTRY_CHARS) {
      message = "You have to provide your country! (Max characters: " + MAX_COUNTRY_CHARS + ")";
    } else if (location == null || location.length() > MAX_LOCATION_CHARS) {
      message = "You have to provide your location! (Max characters: " + MAX_LOCATION_CHARS + ")";
    } else if (categories == null || categories.length() < 2) {
      message = "You have to provide categories for your item";
    } else if (endsyear == null || endsmonth == null || endsday == null || endshour == null) {
      message = "You have to specify and ending date";
    } else {
      Date endsDate = null;
      StringBuilder sb = new StringBuilder();
      sb.append(String.format("%04d ", Integer.parseInt(endsyear)));
      sb.append(String.format("%02d ", Integer.parseInt(endsmonth)));
      sb.append(String.format("%02d ", Integer.parseInt(endsday)));
      sb.append(String.format("%02d ", Integer.parseInt(endshour)));
      sb.append(String.format("%02d ", Integer.parseInt(endsminute)));
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd HH mm");
      try {
        endsDate = sdf.parse(sb.toString());
      } catch (ParseException e) {
        message = "Invalid date format";
        forwardMessage(request, response, message);
        return;
      }
      Date current_date = new Date();
      if (current_date.after(endsDate)) {
        message = "Ending date cannot be a past date";
        forwardMessage(request, response, message);
        return;
      }
      float starting_f = 0,
          latitude_f = 0,
          longitude_f = 0,
          buyprice_f = 0;
      try {
        starting_f = Float.parseFloat(starting);
        if (starting_f <= 0) {
          forwardMessage(request, response, "Zero/Negative starting price!");
          return;
        }
        if (!latitude.equals("") && !longitude.equals("")) {
          latitude_f = Float.parseFloat(latitude);
          longitude_f = Float.parseFloat(longitude);
          if (latitude_f <= 0 || longitude_f <= 0) {
            forwardMessage(request, response, "Invalid/Negative coordinates!");
            return;
          }
        }
        if (!buyprice.equals("")) {
          buyprice_f = Float.parseFloat(buyprice);
          if (buyprice_f <= 0) {
            forwardMessage(request, response, "Zero/Negative buy price!");
            return;
          }
        }
      } catch (NumberFormatException e) {
        forwardMessage(request, response, "Invalid number data.");
        return;
      }
      Category cat;
      User user;
      auction.setStartingBid(starting_f);
      if (!latitude.equals("") && !longitude.equals("")) {
        auction.setLatitude(latitude_f);
        auction.setLongitude(longitude_f);
      }
      if (!buyprice.equals("")) {
        auction.setBuyPrice(buyprice_f);
      }
      auction.setName(name);
      auction.setDescription(description);
      auction.setStartTime(null); // Is set when user activates auction.
      auction.setExpirationTime(endsDate);
      auction.setCountry(country);
      auction.setLocation(location);
      auction.setNumOfBids(0);
      auction.setCurrentBid(auction.getStartingBid());
      String[] categories_list;
      categories_list = categories.substring(1).split(">");
      ArrayList<Category> catlist = new ArrayList<>();
      for (String s : categories_list) {
        cat = categoryDAO.find(s);
        if (cat != null) {
          catlist.add(cat);
        }
      }
      auction.setCategories(catlist);
      user = userDAO.findByID(request.getSession().getAttribute("userID").toString());
      user.getAuctions().add(auction);
      auction.setCreator(user);
      if (isSubmit) {
        auctionDAO.create(auction);
      } else {
        auctionDAO.updateAuction(auction);
      }
      List<Part> file_parts = request.getParts().stream().filter(
              part -> "imagefiles".equals(part.getName())).
          collect(Collectors.toList());
      int counter = 0;
      int auction_id = auction.getAuctionId();
      String extension, file_name, save_file_name;
      File image_file, savepath;
      InputStream content;
      long file_size;
      long max_file_size = Long.parseLong(getServletContext().getInitParameter("images.maxsize")); // web.xml
      savepath = new File(getServletContext().getInitParameter("images.location"));
      Image image;
      ArrayList<Image> auction_images = new ArrayList<>();
      ArrayList<Auction> auctions; // To save image's auctions.
      int already_stored = auction.getImages().size();
      if (already_stored + file_parts.size() <= MAX_IMAGES) {
        for (Part file_part : file_parts) {
          file_name = Paths.get(file_part.getSubmittedFileName()).getFileName().toString();
          if (file_name.lastIndexOf(".") != -1) { // No extension.
            extension = file_name.substring(file_name.lastIndexOf("."));
            file_size = file_part.getSize();
            if (extension.equalsIgnoreCase(".png") || extension.equalsIgnoreCase(".jpg")
                || extension.equals(".jpeg")) {
              if (file_size < max_file_size) {
                content = file_part.getInputStream();
                int image_id = counter + already_stored;
                save_file_name = auction_id + "_" + image_id + extension;// Name
                image_file = new File(savepath, save_file_name);
                Files.copy(content, image_file.toPath()); // Write
                image = new Image();
                image.setUrl(save_file_name);
                auctions = new ArrayList<>();
                auctions.add(auction);
                image.setAuctions(auctions);
                imageDAO.create(image);
                auction_images.add(image);
                counter++;
              }
            }
          }
          auction.getImages().addAll(auction_images);
        }
      }
      response.sendRedirect(request.getContextPath() + "/Manage");
      return;
    }
    forwardMessage(request, response, message);
  }

  private Auction prepareAuctionForEditing(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    Auction auction;
    String strId = request.getParameter("id");
    if (strId == null) {
      forwardMessage(request, response, "No auction specified!");
      return null;
    }
    int id;
    try {
      id = Integer.parseInt(strId);
    } catch (NumberFormatException e) {
      forwardMessage(request, response, "Invalid auction id!");
      return null;
    }
    auction = auctionDAO.findByID(id);
    if (auction == null) {
      forwardMessage(request, response, "Auction does not exist!");
      return null;
    }
    if (auction.getStartTime() != null) {
      forwardMessage(request, response, "Cannot edit an expired auction!");
      return null;
    }
    return auction;
  }

  private void activateAuction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    if (request.getSession().getAttribute("userID") == null) {
      showLoginErrorMessage(request, response);
      return;
    }
    String strId = request.getParameter("id");
    if (strId == null) {
      response.getWriter().write("No auction specified");
      return;
    }
    int id = -1;
    try {
      id = Integer.parseInt(strId);
    } catch (NumberFormatException e) {
      response.getWriter().write("Invalid id");
    }
    Auction auction = auctionDAO.findByID(id);
    if (auction == null) {
      response.getWriter().write("Auction does not exist!");
      return;
    }
    if (auction.getExpirationTime().before(new Date())) {
      response.getWriter().write("Auction expiration date is a past date!");
      return;
    }
    auction.setStartTime(new Date());
    auctionDAO.updateAuction(auction);
    response.sendRedirect(request.getContextPath() + "/Manage");
  }

  private void fetchCategories(HttpServletRequest request, HttpServletResponse response) throws IOException {
    JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);
    String parentCategory = data.get("parent_category").getAsString();
    if (parentCategory.equals("")) {
      parentCategory = null;
    }
    List<Category> categoryList = categoryDAO.listChildren(parentCategory);
    JsonArray jsonElements = new JsonArray();
    for (Category category : categoryList) {
      jsonElements.add(new JsonPrimitive(category.getName()));
    }
    JsonObject message = new JsonObject();
    message.add("categories", jsonElements);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(message.toString());
  }

  private void showLoginErrorMessage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    RequestDispatcher disp;
    disp = getServletContext().getRequestDispatcher("/loginerror.jsp");
    disp.forward(request, response);
  }

  public void forwardMessage(HttpServletRequest request, HttpServletResponse response, String message) throws ServletException, IOException {
    RequestDispatcher disp;
    request.setAttribute("message", message);
    disp = getServletContext().getRequestDispatcher("/error.jsp");
    disp.forward(request, response);
  }
}
