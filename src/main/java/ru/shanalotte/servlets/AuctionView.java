package ru.shanalotte.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;


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
import ru.shanalotte.dao.CategoryDAO;
import ru.shanalotte.dao.UserDAO;
import ru.shanalotte.dao.ImageDAO;
import ru.shanalotte.entities.User;
import ru.shanalotte.entities.Image;
import ru.shanalotte.entities.Category;
import ru.shanalotte.entities.UserBid;
import ru.shanalotte.entities.UserBidPK;
import ru.shanalotte.dao.AuctionDAO;
import ru.shanalotte.entities.Auction;
import ru.shanalotte.dao.UserBidDAO;

@WebServlet("/AuctionView")
public class AuctionView extends HttpServlet {
  private static final long serialVersionUID = 1L;

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
    String action = request.getParameter("page");
    if (action.equals("history")) {
      showAuctionHistory(request, response);
    } else if (action.equals("view")) {
      showAuctionInfo(request, response);
    }
  }

  private void showAuctionHistory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    int requestedAuctionId = Integer.parseInt(request.getParameter("auctionID"));
    Auction currentAuction = auctionDAO.findByID(requestedAuctionId);
    request.setAttribute("name", currentAuction.getName());
    request.setAttribute("user_biddings", auctionDAO.findAuctionBids(currentAuction));
    request.setAttribute("starting_bid", currentAuction.getStartingBid());
    request.setAttribute("start_time", currentAuction.getStartTime());
    request.setAttribute("images", imageDAO.findImagesofAuction(currentAuction));
    request.setAttribute("current_bid", currentAuction.getCurrentBid());
    request.setAttribute("buy_out", currentAuction.getUser() != null);
    request.setAttribute("expired", new Date().after(currentAuction.getExpirationTime()));
    RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher("/bid_history.jsp");
    requestDispatcher.forward(request, response);
  }

  private void showAuctionInfo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    int requestedAuctionId = Integer.parseInt(request.getParameter("auctionID"));
    Auction currentAuction = auctionDAO.findByID(requestedAuctionId);
    request.setAttribute("name", currentAuction.getName());
    request.setAttribute("imageList", prepareAuctionImagesUrl(currentAuction));
    request.setAttribute("categories", prepareAuctionCategories(currentAuction));
    request.setAttribute("expired", new Date().after(currentAuction.getExpirationTime()));
    request.setAttribute("buy_out", currentAuction.getUser() != null);
    request.setAttribute("latitude", currentAuction.getLatitude());
    request.setAttribute("longitude", currentAuction.getLongitude());
    request.setAttribute("location", currentAuction.getLocation());
    request.setAttribute("country", currentAuction.getCountry());
    request.setAttribute("buy_price", currentAuction.getBuyPrice());
    request.setAttribute("starting_bid", currentAuction.getStartingBid());
    request.setAttribute("current_bid", currentAuction.getCurrentBid());
    request.setAttribute("num_of_bids", currentAuction.getNumOfBids());
    request.setAttribute("description", currentAuction.getDescription());
    request.setAttribute("creator", currentAuction.getCreator());
    request.setAttribute("start_time", currentAuction.getStartTime());
    request.setAttribute("expiration_time", currentAuction.getExpirationTime());
    request.setAttribute("buy_user", currentAuction.getUser());
    RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher("/auctionview.jsp");
    requestDispatcher.forward(request, response);
  }

  private ArrayList<String> prepareAuctionCategories(Auction auction) {
    List<Category> auctionCategories = new ArrayList<>(auction.getCategories());
    ArrayList<String> categories = new ArrayList<>();
    String previousCategory = "";
    int categoriesQty = auctionCategories.size();
    for (int i = 0; i < categoriesQty; i++) {
      for (Category category : auctionCategories) {
        if (category.getParent() == null ||
            category.getParent().equals(previousCategory)) {
          categories.add(category.getName());
          previousCategory = category.getName();
          auctionCategories.remove(category);
          break;
        }
      }
    }
    return categories;
  }

  private ArrayList<String> prepareAuctionImagesUrl(Auction auction) {
    List<Image> auctionImages = imageDAO.findImagesofAuction(auction);
    ArrayList<String> imagesUrl = new ArrayList<>();
    for (Image image : auctionImages) {
      imagesUrl.add(image.getUrl());
    }
    return imagesUrl;
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getSession().getAttribute("userID") == null) {
			redirectToLoginErrorPage(request, response);
			return;
		}
		String action = request.getParameter("action");
		if (action.equals("bidAuction")) {
			processBidAttempt(request, response);
		} else if (action.equals("buyout")) {
			processBuyOutAttempt(request, response);
		}
  }

	private void processBuyOutAttempt(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int requestedAuctionId = Integer.parseInt(request.getParameter("auctionID"));
		Auction currentAuction = auctionDAO.findByID(requestedAuctionId);
		if (new Date().after(currentAuction.getExpirationTime()) || currentAuction.getUser() != null) {
			notifyThatAuctionIsBoughtOutOrExpired(request, response, requestedAuctionId);
		}
		completeBuyOut(request, response, currentAuction);
	}

	private void completeBuyOut(HttpServletRequest request, HttpServletResponse response, Auction auction) throws IOException {
		User user = userDAO.findByID(request.getSession().getAttribute("userID").toString());
		auction.setUser(user);
		request.getSession().setAttribute("bid_response", "Item successfully bought");
		response.sendRedirect("AuctionView?auctionID=" + auction.getAuctionId() + "&page=view");
	}

	private void notifyThatAuctionIsBoughtOutOrExpired(HttpServletRequest request, HttpServletResponse response, int requstedAuctionId) throws IOException {
		request.getSession().setAttribute("bid_response", "Error,item expired or already bought");
		response.sendRedirect("AuctionView?auctionID=" + requstedAuctionId + "&page=view");
	}

	private void processBidAttempt(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int requestedAuctionId = Integer.parseInt(request.getParameter("auctionID"));
		Auction currentAuction = auctionDAO.findByID(requestedAuctionId);
		float currentBid = currentAuction.getCurrentBid();
		float buyPrice = currentAuction.getBuyPrice();
		String submittedBidInput = request.getParameter("Bid_input");
		float submittedBidValue = Float.parseFloat(submittedBidInput);
		if (submittedBidValue >= buyPrice && buyPrice > 0) {
			notifyThatBidHigherThanBuyPrice(request, response, requestedAuctionId);
		} else if (submittedBidValue > currentBid) {
			createBid(request, response, currentAuction, submittedBidValue);
		} else {
			notifyThatBidShouldBeHigherThenCurrentBid(request, response, requestedAuctionId);
		}
	}

	private void notifyThatBidShouldBeHigherThenCurrentBid(HttpServletRequest request, HttpServletResponse response, int requestedAuctionId) throws IOException {
		request.getSession().setAttribute("bid_response", "Bid must be higher than current bid");
		response.sendRedirect("AuctionView?auctionID=" + requestedAuctionId + "&page=view");
	}

	private void createBid(HttpServletRequest request, HttpServletResponse response, Auction auction, float submittedBidValue) throws IOException {
		UserBidPK newBidPk = new UserBidPK();
		newBidPk.setAuctionId(auction.getAuctionId());
		User user = userDAO.findByID(request.getSession().getAttribute("userID").toString());
		newBidPk.setUserId(user.getUserId());
		newBidPk.setPrice(submittedBidValue);
		UserBid newBid = new UserBid();
		newBid.setId(newBidPk);
		newBid.setUser(user);
		newBid.setAuction(auction);
		Date starting_date = new Date();
		newBid.setTime(starting_date);
		user.addUserBidAuction(newBid);
		auction.addBid(newBid);
		auction.setCurrentBid(submittedBidValue);
		auction.setNumOfBids(auction.getNumOfBids() + 1);
		userBidDAO.create(user, auction, starting_date, submittedBidValue);
		request.getSession().setAttribute("bid_response", "Bid succesfully submitted");
		response.sendRedirect("AuctionView?auctionID=" + auction.getAuctionId() + "&page=history");
	}

	private void notifyThatBidHigherThanBuyPrice(HttpServletRequest request, HttpServletResponse response, int requestedAuctionId) throws IOException {
		request.getSession().setAttribute("bid_response", "Bid exceeds buy price. Please use buy option");
		response.sendRedirect("AuctionView?auctionID=" + requestedAuctionId + "&page=view");
	}

	private void redirectToLoginErrorPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher disp = getServletContext().getRequestDispatcher("/loginerror.jsp");
		disp.forward(request, response);
	}
}
