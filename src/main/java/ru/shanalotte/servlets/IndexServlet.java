package ru.shanalotte.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;
import ru.shanalotte.dao.AuctionDAO;
import ru.shanalotte.dao.AuctionSearchOptions;
import ru.shanalotte.dao.CategoryDAO;
import ru.shanalotte.dao.ImageDAO;
import ru.shanalotte.dao.UserBidDAO;
import ru.shanalotte.dao.UserDAO;
import ru.shanalotte.entities.Auction;
import ru.shanalotte.entities.Image;
import ru.shanalotte.entities.User;

@Service
@WebServlet(urlPatterns = {"", "/Search", "/Manage"})
public class IndexServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  private static final int searchResultsPerPage = 15;

  private static final int MAX_RECOMMENDATIONS = 5;

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
  public void init(final ServletConfig config) throws ServletException {
    super.init(config);
    ApplicationContext applicationContext = (AnnotationConfigApplicationContext) config.getServletContext().getAttribute("springcontext");
    final AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
    beanFactory.autowireBean(this);
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String urlPath = request.getRequestURI().substring(request.getContextPath().length());
    if (urlPath.equals("/Search")) {
      processSearchRequest(request, response);
    } else if (urlPath.equals("/Manage")) {
      showAuctionsPage(request, response);
    } else {
      showFrontPage(request, response);
    }
  }

  private void showFrontPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    Cookie[] cookies = request.getCookies();
    StringBuilder commonPicks = new StringBuilder();
    StringBuilder uncommonPicks = new StringBuilder();
    if (cookies != null) {
      for (Cookie ck : cookies) {
        if (ck.getName().equals("common_picks"))
          commonPicks.append(ck.getValue());
        if (ck.getName().equals("uncommon_picks"))
          uncommonPicks.append(ck.getValue());
      }
    }
    List<Auction> recommendations = new ArrayList<>();
    ArrayList<String> imagePaths = new ArrayList<>();
    String delimiter = "-";
    if (commonPicks.length() > 0) {
      String[] commonIds = commonPicks.toString().split(delimiter);
      for (String commonId : commonIds) {
        recommendations.add(auctionDAO.findByID(Integer.parseInt(commonId)));
        List<Image> auction_images = imageDAO.findImagesofAuction(auctionDAO.findByID(Integer.parseInt(commonId)));
        if (auction_images.isEmpty()) {
          imagePaths.add(null);
        } else {
          imagePaths.add(auction_images.get(0).getUrl());
        }
        if (recommendations.size() == MAX_RECOMMENDATIONS)
          break;
      }
    }
    if ((uncommonPicks.length() > 0) && recommendations.size() < MAX_RECOMMENDATIONS) {
      String[] uncommon_ids = uncommonPicks.toString().split(delimiter);
      for (String uncommon_id : uncommon_ids) {
        recommendations.add(auctionDAO.findByID(Integer.parseInt(uncommon_id)));
        List<Image> auction_images = imageDAO.findImagesofAuction(auctionDAO.findByID(Integer.parseInt(uncommon_id)));
        if (auction_images.isEmpty()) {
          imagePaths.add(null);
        } else {
          imagePaths.add(auction_images.get(0).getUrl());
        }
        if (recommendations.size() == MAX_RECOMMENDATIONS)
          break;
      }
    }
    if (recommendations.size() < MAX_RECOMMENDATIONS) {
      Date curr_date = new Date();
      int topAuctionsPoolSize = 20;
      List<Auction> pop_aucts = auctionDAO.findPopular(topAuctionsPoolSize, curr_date);
      Collections.shuffle(pop_aucts);
      for (Auction paucs : pop_aucts) {
        if (!recommendations.contains(paucs)) {
          recommendations.add(paucs);
          List<Image> auction_images = imageDAO.findImagesofAuction(paucs);
          if (auction_images.isEmpty()) {
            imagePaths.add(null);
          } else {
            imagePaths.add(auction_images.get(0).getUrl());
          }
          if (recommendations.size() == MAX_RECOMMENDATIONS)
            break;
        }
      }
    }
    request.setAttribute("recommended_aucts", recommendations);
    request.setAttribute("rec_aucts_imgs", imagePaths);
    RequestDispatcher disp = getServletContext().getRequestDispatcher("/index.jsp");
    disp.forward(request, response);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doGet(request, response);
  }


  protected void processSearchRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    AuctionSearchOptions options = prepareAuctionSearchOptions(request);
    String pageNumberString = request.getParameter("page");
    int searchPageNumber = 0;
    if (pageNumberString != null) {
      try {
        searchPageNumber = Integer.parseInt(pageNumberString);
      } catch (NumberFormatException ignored) {
      }
    }
    if (searchPageNumber < 0) {
      searchPageNumber = 0;
    }
    List<Auction> searchResults;
    searchResults = auctionDAO.search(options, searchPageNumber, searchResultsPerPage);
    boolean hasNextPage = auctionDAO.search(options, ((searchPageNumber + 1) * searchResultsPerPage), 1).size() > 0;
    boolean hasPrevPage = searchPageNumber != 0;
    request.setAttribute("searchResults", searchResults);
    request.setAttribute("nextPage", hasNextPage);
    request.setAttribute("prevPage", hasPrevPage);
    request.setAttribute("currentPage", searchPageNumber);
    request.setAttribute("descriptionParam", request.getParameter("description"));
    request.setAttribute("locationParam", request.getParameter("location"));
    request.setAttribute("categoryParam", request.getParameter("category"));
    request.setAttribute("priceMinParam", request.getParameter("price-from"));
    request.setAttribute("priceMaxParam", request.getParameter("price-to"));
    RequestDispatcher disp = getServletContext().getRequestDispatcher("/search_results.jsp");
    disp.forward(request, response);
  }

  private AuctionSearchOptions prepareAuctionSearchOptions(HttpServletRequest request) {
    AuctionSearchOptions options = new AuctionSearchOptions(new Date());
    String descriptionParam = request.getParameter("description");
    String categoryParam = request.getParameter("category");
    String priceFromParam = request.getParameter("price-from");
    String priceToParam = request.getParameter("price-to");
    String locationParam = request.getParameter("location");
    if (descriptionParam != null && !descriptionParam.equals("")) {
      options.setDescription(descriptionParam);
    }
    if (categoryParam != null && !categoryParam.equals("") && !categoryParam.equals("all")) {
      options.setCategory(categoryParam);
    }
    if (priceFromParam != null) {
      try {
        float price_from_safe = Float.parseFloat(priceFromParam);
        options.setMinPrice(price_from_safe);
      } catch (NumberFormatException ignored) {
      }
    }
    if (priceToParam != null) {
      try {
        float price_to_safe = Float.parseFloat(priceToParam);
        options.setMaxPrice(price_to_safe);
      } catch (NumberFormatException ignored) {
      }
    }
    if (locationParam != null && !locationParam.equals("")) {
      options.setLocation(locationParam);
    }
    return options;
  }

  protected void showAuctionsPage(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    if (request.getSession().getAttribute("userID") == null) {
      RequestDispatcher disp = getServletContext().getRequestDispatcher("/loginerror.jsp");
      disp.forward(request, response);
      return;
    }
    User currentUser = userDAO.findByID(request.getSession().getAttribute("userID").toString());
    var inactiveAuctions = auctionDAO.findInactiveOf(currentUser);
    var activeAuctions = auctionDAO.findActiveOf(currentUser, new Date());
    var soldAuctions = auctionDAO.findSoldOf(currentUser, new Date());
    var wonAuctions = auctionDAO.findUserWonAuctions(currentUser, new Date());
    var lostAuctions = auctionDAO.findUserLostAuctions(currentUser, new Date());
    var biddedAuctions = auctionDAO.findUserBiddedAuctions(currentUser, new Date());
    request.setAttribute("inactiveList", inactiveAuctions);
    request.setAttribute("activeList", activeAuctions);
    request.setAttribute("soldList", soldAuctions);
    request.setAttribute("wonList", wonAuctions);
    request.setAttribute("lostList", lostAuctions);
    request.setAttribute("biddedList", biddedAuctions);
    RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher("/auction_manage.jsp");
    requestDispatcher.forward(request, response);
  }
}
