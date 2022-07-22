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
    String url_path = request.getRequestURI().substring(request.getContextPath().length());
    if (url_path.equals("/Search")) { // Load search results
      search(request, response);
    } else if (url_path.equals("/Manage")) {
      manageAuctions(request, response);
    } else { // Load front page

      //Get recommended auction ids from cookies
      Cookie[] cookies = request.getCookies();
      String common_picks = "";
      String uncommon_picks = "";

      if (cookies != null) {
        for (Cookie ck : cookies) {
          if (ck.getName().equals("common_picks"))
            common_picks += ck.getValue();
          if (ck.getName().equals("uncommon_picks"))
            uncommon_picks += ck.getValue();
        }
      }

      //Parse auction ids strings

      int max_recommendations = 5;
      List<Auction> recommendations = new ArrayList<>();
      ArrayList<String> image_paths = new ArrayList<>();
      String delim = "-";
      if (!common_picks.isEmpty()) {
        String[] common_ids = common_picks.split(delim);
        for (int i = 0; i < common_ids.length; i++) {
          recommendations.add(auctionDAO.findByID(Integer.parseInt(common_ids[i])));
          List<Image> auction_images = imageDAO.findImagesofAuction(auctionDAO.findByID(Integer.parseInt(common_ids[i])));
          if (auction_images.isEmpty()) {
            image_paths.add(null);
          } else {
            image_paths.add(auction_images.get(0).getUrl());
          }
          if (recommendations.size() == max_recommendations)
            break;
        }
      }
      if (!uncommon_picks.isEmpty() && recommendations.size() < max_recommendations) {
        String[] uncommon_ids = uncommon_picks.split(delim);
        for (int i = 0; i < uncommon_ids.length; i++) {
          recommendations.add(auctionDAO.findByID(Integer.parseInt(uncommon_ids[i])));
          List<Image> auction_images = imageDAO.findImagesofAuction(auctionDAO.findByID(Integer.parseInt(uncommon_ids[i])));
          if (auction_images.isEmpty()) {
            image_paths.add(null);
          } else {
            image_paths.add(auction_images.get(0).getUrl());
          }
          if (recommendations.size() == max_recommendations)
            break;
        }
      }
      if (recommendations.size() < max_recommendations) {
        Date curr_date = new Date();
        int top_auctions_pool_size = 20;
        List<Auction> pop_aucts = auctionDAO.findPopular(top_auctions_pool_size, curr_date);
        Collections.shuffle(pop_aucts);
        for (Auction paucs : pop_aucts) {
          if (!recommendations.contains(paucs)) {
            recommendations.add(paucs);
            List<Image> auction_images = imageDAO.findImagesofAuction(paucs);
            if (auction_images.isEmpty()) {
              image_paths.add(null);
            } else {
              image_paths.add(auction_images.get(0).getUrl());
            }
            if (recommendations.size() == max_recommendations)
              break;
          }
        }
      }

      request.setAttribute("recommended_aucts", recommendations);
      request.setAttribute("rec_aucts_imgs", image_paths);

      RequestDispatcher disp = getServletContext().getRequestDispatcher("/index.jsp");
      disp.forward(request, response);
    }
  }

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
   * response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doGet(request, response);
  }

  protected void search(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // Create an AuctionSearchOptions with current date as minimum.
    AuctionSearchOptions options = new AuctionSearchOptions(new Date());
    String description_param = request.getParameter("description");
    String category_param = request.getParameter("category");
    String price_from_param = request.getParameter("price-from");
    String price_to_param = request.getParameter("price-to");
    String location_param = request.getParameter("location");

    // Check every parameter and form options.
    if (description_param != null && !description_param.equals("")) {
      options.setDescription(description_param);
    }
    if (category_param != null && !category_param.equals("") && !category_param.equals("all")) {
      options.setCategory(category_param);
    }
    if (price_from_param != null) {
      float price_from_safe;
      try {
        price_from_safe = Float.parseFloat(price_from_param);
        options.setMinPrice(price_from_safe);

      } catch (NumberFormatException e) {
        // Nothing to be handled, we just dont add the option.
      }
    }
    if (price_to_param != null) {
      float price_to_safe;
      try {
        price_to_safe = Float.parseFloat(price_to_param);
        options.setMaxPrice(price_to_safe);

      } catch (NumberFormatException e) {
        // Nothing to be handled, we just dont add the option.
      }
    }

    if (location_param != null && !location_param.equals("")) {
      options.setLocation(location_param);
    }

    // Determine which page we have to present
    String pageStr = request.getParameter("page");
    int page = 0;
    if (pageStr != null) {
      try {
        page = Integer.parseInt(pageStr);
      } catch (NumberFormatException e) {
        // Page will remain 0, nothing to handle.
      }
    }

    if (page < 0) {
      // We cannot allow negative pages.
      page = 0;
    }

    // Inject the dao with our options.
    List<Auction> search_results;
    search_results = auctionDAO.search(options, page, searchResultsPerPage);

    boolean has_next_page = false;
    boolean has_prev_page = false;

    // Check if we have a next page.
    if (auctionDAO.search(options, ((page + 1) * searchResultsPerPage), 1).size() > 0) {
      // Above line checks if next item of fetched page exists.
      // If it does not exist, no more pages exist.
      has_next_page = true;
    } else {
      has_next_page = false;
    }

    if (page != 0) {
      has_prev_page = true;
    } else {
      has_prev_page = false;
    }

    request.setAttribute("searchResults", search_results);
    request.setAttribute("nextPage", has_next_page);
    request.setAttribute("prevPage", has_prev_page);
    request.setAttribute("currentPage", page);
    request.setAttribute("descriptionParam", description_param);
    request.setAttribute("locationParam", location_param);
    request.setAttribute("categoryParam", category_param);
    request.setAttribute("priceMinParam", price_from_param);
    request.setAttribute("priceMaxParam", price_to_param);

    RequestDispatcher disp = getServletContext().getRequestDispatcher("/search_results.jsp");
    disp.forward(request, response);
  }

  protected void manageAuctions(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    RequestDispatcher disp;
    if (request.getSession().getAttribute("userID") == null) {
      disp = getServletContext().getRequestDispatcher("/loginerror.jsp");
      disp.forward(request, response);
      return;
    }

    // Get current user.
    User user = userDAO.findByID(request.getSession().getAttribute("userID").toString());

    // We have 4 types of auctions, inactive, active, sold and bidded.
    // Fetch them in different lists.
    List<Auction> inactive;
    List<Auction> active;
    List<Auction> sold;
    List<Auction> won;
    List<Auction> lost;
    List<Auction> bidded;

    inactive = auctionDAO.findInactiveOf(user);
    active = auctionDAO.findActiveOf(user, new Date());
    sold = auctionDAO.findSoldOf(user, new Date());
    won = auctionDAO.findUserWonAuctions(user, new Date());
    lost = auctionDAO.findUserLostAuctions(user, new Date());
    bidded = auctionDAO.findUserBiddedAuctions(user, new Date());

    request.setAttribute("inactiveList", inactive);
    request.setAttribute("activeList", active);
    request.setAttribute("soldList", sold);
    request.setAttribute("wonList", won);
    request.setAttribute("lostList", lost);
    request.setAttribute("biddedList", bidded);

    disp = getServletContext().getRequestDispatcher("/auction_manage.jsp");
    disp.forward(request, response);
  }
}
