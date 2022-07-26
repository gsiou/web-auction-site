package com.github.gsiou.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.github.gsiou.dao.AuctionDAO;
import com.github.gsiou.dao.UserDAO;
import com.github.gsiou.entities.Auction;
import com.github.gsiou.entities.User;
import com.github.gsiou.helper.AuctionFrequency;
import com.github.gsiou.helper.AuctionFrequencyComparator;
import com.github.gsiou.utils.HelperFunctions;
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

@WebServlet(urlPatterns = {"/Login", "/Logout"})
public class UserLoginServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

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

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String urlPath = request.getRequestURI().substring(request.getContextPath().length());
    if (urlPath.equals("/Login")) {
      processUserLogin(request, response);
    } else if (urlPath.equals("/Logout")) {
      processUserLogout(request, response);
    }
  }

  private void processUserLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (request.getSession().getAttribute("userID") != null) {
      removeCookies(request, response);
      request.getSession().invalidate();
    }
    response.sendRedirect(request.getContextPath());
  }

  private void removeCookies(HttpServletRequest request, HttpServletResponse response) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals("common_picks") || cookie.getName().equals("uncommon_picks")) {
          cookie.setMaxAge(0);
          response.addCookie(cookie);
        }
      }
    }
  }

  private void processUserLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    RequestDispatcher requestDispatcher;
    if (request.getSession().getAttribute("userID") != null) {
      requestDispatcher = getServletContext().getRequestDispatcher("/already_logged.jsp");
    } else {
      requestDispatcher = getServletContext().getRequestDispatcher("/login.jsp");
    }
    requestDispatcher.forward(request, response);
  }

  public void showErrorLoginMessage(String message, HttpServletRequest request, HttpServletResponse response) {
    request.setAttribute("error", message);
    RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher("/login.jsp");
    try {
      requestDispatcher.forward(request, response);
    } catch (ServletException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public boolean userCredentialsIsValid(HttpServletRequest request, HttpServletResponse response) {
    String username = request.getParameter("Username");
    String password = request.getParameter("Password");
    User user = userDAO.findByID(username);
    if (user != null) {
      String hashedPassword = HelperFunctions.hash(password);
      if (hashedPassword.equals(user.getPassword())) {
        if (user.getAccessLvl() == 0) {
          showErrorLoginMessage("You are not activated yet.", request, response);
          return false;
        }
        return true;
      }
    }
    showErrorLoginMessage("Wrong username or password", request, response);
    return false;
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    if (!userCredentialsIsValid(request, response)) {
      return;
    }
    String username = request.getParameter("Username");
    User loggedUser = userDAO.findByID(username);
    request.getSession().setAttribute("userID", username);
    request.getSession().setAttribute("access", loggedUser.getAccessLvl());
    List<User> frequentBidders = userDAO.listFrequentBidders(loggedUser);
    List<Auction> userUniqueBids = auctionDAO.findUserUniqueBids(loggedUser);
    if (!userUniqueBids.isEmpty()) {
      double cosineSim;
      int kNeighbors = 5;
      User[] kNearestUsers = new User[kNeighbors];
      double[] neighborsCosine = new double[kNeighbors];
      int neighborsFound = 0;
      for (User u : frequentBidders) {
        int commonAuctions = 0;
        List<Auction> check_user_bids = auctionDAO.findUserUniqueBids(u);
        for (Auction check_aucts : check_user_bids) {
          for (Auction my_aucts : userUniqueBids) {
            if (check_aucts.getAuctionId() == my_aucts.getAuctionId())
              commonAuctions++;
          }
        }
        cosineSim = commonAuctions / (Math.sqrt(check_user_bids.size()) * Math.sqrt(userUniqueBids.size()));
        if (cosineSim > 0) {
          if (neighborsFound < kNeighbors) {
            kNearestUsers[neighborsFound] = u;
            neighborsCosine[neighborsFound] = cosineSim;
            neighborsFound++;
          } else {
            int less_similar = 0;
            for (int i = 1; i < kNeighbors; i++) {
              if (neighborsCosine[i] < neighborsCosine[less_similar])
                less_similar = i;
            }
            kNearestUsers[less_similar] = u;
            neighborsCosine[less_similar] = cosineSim;
          }
        }
      }
      ArrayList<Integer> recommendedAuctions = gatherRecommendedAuctions(userUniqueBids, kNearestUsers, neighborsFound);
      Set<Integer> auctionSet = new HashSet<>();
      auctionSet.addAll(recommendedAuctions);
      List<AuctionFrequency> commonPicks = new ArrayList<>();
      ArrayList<Integer> uncommonPicks = new ArrayList<>();
      for (Integer a : auctionSet) {
        int freq = Collections.frequency(recommendedAuctions, a);
        if (freq > 1) {
          commonPicks.add(new AuctionFrequency(a, freq));
        } else {
          uncommonPicks.add(a);
        }
      }
      Collections.sort(commonPicks, new AuctionFrequencyComparator());
      int maxAuctionsIdsQty = 5;
      String commonPicksCookie = "";
      int picksSend = 0;
      for (AuctionFrequency cp : commonPicks) {
        if (picksSend < maxAuctionsIdsQty) {
          commonPicksCookie += cp.getAuctionId();
          commonPicksCookie += "-";
          picksSend++;
        }
      }
      Cookie common_picks_ck = new Cookie("common_picks", commonPicksCookie);
      String uncommonPicksCookie = "";
      if (picksSend < maxAuctionsIdsQty) {
        for (Integer ucp : uncommonPicks) {
          if (picksSend < maxAuctionsIdsQty) {
            uncommonPicksCookie += ucp.toString();
            uncommonPicksCookie += "-";
            picksSend++;
          }
        }
      }
      Cookie uncommon_picks_ck = new Cookie("uncommon_picks", uncommonPicksCookie);
      common_picks_ck.setMaxAge(60 * 60 * 24);
      uncommon_picks_ck.setMaxAge(60 * 60 * 24);
      response.addCookie(common_picks_ck);
      response.addCookie(uncommon_picks_ck);
      response.setContentType("text/html");
    }
    response.sendRedirect(request.getContextPath());
  }

  private ArrayList<Integer> gatherRecommendedAuctions(List<Auction> userUniqueBids, User[] kNearestUsers, int neighborsFound) {
    ArrayList<Integer> recommendedAuctions = new ArrayList<>();
    for (int i = 0; i < neighborsFound; i++) {
      for (Auction a : auctionDAO.findUserUniqueActiveBids(kNearestUsers[i], new Date())) {
        if (!userUniqueBids.contains(a)) {
          recommendedAuctions.add(a.getAuctionId());
        }
      }
    }
    return recommendedAuctions;
  }

}
