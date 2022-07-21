package ru.shanalotte.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;


import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.shanalotte.dao.UserDAO;
import ru.shanalotte.dao.UserDAOI;
import ru.shanalotte.dao.ImageDAO;
import ru.shanalotte.dao.ImageDAOI;
import ru.shanalotte.entities.User;
import ru.shanalotte.entities.Image;
import ru.shanalotte.entities.Category;
import ru.shanalotte.entities.UserBid;
import ru.shanalotte.entities.UserBidPK;
import ru.shanalotte.dao.AuctionDAO;
import ru.shanalotte.dao.AuctionDAOI;
import ru.shanalotte.entities.Auction;
import ru.shanalotte.dao.UserBidDAO;
import ru.shanalotte.dao.UserBidDAOI;

/**
 * Servlet implementation class AuctionView
 */
@WebServlet("/AuctionView")
public class AuctionView extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AuctionView() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher disp;
		String action=request.getParameter("page");
		if(action == null){
			return ;
		}
		else if(action.equals("history")){
			/* Bid history servlet implementation */
			
			AuctionDAOI dao = new AuctionDAO();
			int auctionid=Integer.parseInt(request.getParameter("auctionID"));
			Auction currentAuction = dao.findByID(auctionid);
			
			/* Get name,starting bid,bids and start time of the current auction */
			
			String auction_name=currentAuction.getName();
			request.setAttribute("name",auction_name);
			List<UserBid> bidding_users=dao.findAuctionBids(currentAuction);
			request.setAttribute("user_biddings",bidding_users);
			float starting_bid=currentAuction.getStartingBid();
			request.setAttribute("starting_bid",starting_bid);
			Date startTime=currentAuction.getStartTime();
			request.setAttribute("start_time",startTime);
			
			/* Get auction's images */
			
			ImageDAOI imgdao = new ImageDAO();
			List<Image> auction_images=imgdao.findImagesofAuction(currentAuction);
			request.setAttribute("images",auction_images);
			
			/* Get auction's current bid and set if it is sold/expired or not */
			
			float current_bid=currentAuction.getCurrentBid();
			request.setAttribute("current_bid",current_bid);
			User buyout_user=currentAuction.getUser();
			if(buyout_user == null)
				request.setAttribute("buy_out",false);
			else
				request.setAttribute("buy_out",true);
			Date expirationTime=currentAuction.getExpirationTime();
			Date current_time = new Date();
			if(current_time.after(expirationTime))
				request.setAttribute("expired",true);
			else
				request.setAttribute("expired",false);
			disp = getServletContext().getRequestDispatcher("/bid_history.jsp");
			disp.forward(request, response);
		}
		else if(action.equals("view")){
			/* Auction View servlet implementation */
			
			/* Find current auction*/
			
			AuctionDAOI dao = new AuctionDAO();
			int auctionid=Integer.parseInt(request.getParameter("auctionID"));
			Auction currentAuction = dao.findByID(auctionid);
			String auction_name=currentAuction.getName();
			request.setAttribute("name",auction_name);
			ImageDAOI imgdao = new ImageDAO();
			
			/* Find auction's images*/
			
			List<Image> auction_images=imgdao.findImagesofAuction(currentAuction);
			ArrayList<String> image_paths=new ArrayList<>();
			for (Image auct_im : auction_images) {
	            image_paths.add(auct_im.getUrl());
			}
			request.setAttribute("imageList",image_paths);
			
			/* Find auction's listed categories*/
			
			List<Category> auction_categories = new ArrayList<>(currentAuction.getCategories());
			ArrayList<String> categories=new ArrayList<>();
			String prev_category = "";
			int categories_number = auction_categories.size();
			for(int i = 0; i < categories_number; i++){
				for (Category auct_cat : auction_categories) {
					if(auct_cat.getParent() == null ||
							auct_cat.getParent().equals(prev_category)){
						categories.add(auct_cat.getName());
						prev_category = auct_cat.getName();
						auction_categories.remove(auct_cat);
						break;
					}
				}
			}
			request.setAttribute("categories",categories);
			
			/* Set the attributes for the view */
			
			float auct_latitude=currentAuction.getLatitude();
			float auct_longitude=currentAuction.getLongitude();
			String location=currentAuction.getLocation();
			String country=currentAuction.getCountry();
			float buy_price=currentAuction.getBuyPrice();
			float starting_bid=currentAuction.getStartingBid();
			float current_bid=currentAuction.getCurrentBid();
			int numOfBids=currentAuction.getNumOfBids();
			String description=currentAuction.getDescription();
			User creator=currentAuction.getCreator();
			Date startTime=currentAuction.getStartTime();
			Date expirationTime=currentAuction.getExpirationTime();
			Date current_time = new Date();
			if(current_time.after(expirationTime))
				request.setAttribute("expired",true);
			else
				request.setAttribute("expired",false);
			User buyout_user=currentAuction.getUser();
			if(buyout_user == null)
				request.setAttribute("buy_out",false);
			else
				request.setAttribute("buy_out",true);
			
			
			request.setAttribute("latitude",auct_latitude);
			request.setAttribute("longitude",auct_longitude);
			request.setAttribute("location",location);
			request.setAttribute("country",country);
			request.setAttribute("buy_price",buy_price);
			request.setAttribute("starting_bid",starting_bid);
			request.setAttribute("current_bid",current_bid);
			request.setAttribute("num_of_bids",numOfBids);
			request.setAttribute("description",description);
			request.setAttribute("creator",creator);
			request.setAttribute("start_time",startTime);
			request.setAttribute("expiration_time",expirationTime);
			request.setAttribute("buy_user",buyout_user);
			
			disp = getServletContext().getRequestDispatcher("/auctionview.jsp");
			disp.forward(request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String action=request.getParameter("action");
		if(action == null){
			System.out.println("Null action");
			return ;
		}
		else if(action.equals("bidAuction")){
			/* Bid server implementation */
			
			RequestDispatcher disp;
			if(request.getSession().getAttribute("userID") == null){
				/* Do not work for unlogged users */
				disp = getServletContext().getRequestDispatcher("/loginerror.jsp");
				disp.forward(request, response);
				return;
			}
			AuctionDAOI dao = new AuctionDAO();
			int auctionid=Integer.parseInt(request.getParameter("auctionID"));
			Auction currentAuction = dao.findByID(auctionid);
			float current_bid=currentAuction.getCurrentBid();
			float buy_price=currentAuction.getBuyPrice();
			String submited_bid=request.getParameter("Bid_input");
			float sub_bid=Float.parseFloat(submited_bid);
			if(sub_bid>=buy_price && buy_price > 0){
				/* If bid exceeds buy price */
				
				request.getSession().setAttribute("bid_response", "Bid exceeds buy price.Please use buy option");
				response.sendRedirect("AuctionView?auctionID="+auctionid+"&page=view");
			}
			else if(sub_bid>current_bid){
				/* If the bid is valid */
				
				UserBidPK new_bid_pk=new UserBidPK();
				new_bid_pk.setAuctionId(currentAuction.getAuctionId());
				UserDAOI udao = new UserDAO();
				User user = udao.findByID(request.getSession().getAttribute("userID").toString());
				new_bid_pk.setUserId(user.getUserId());
				new_bid_pk.setPrice(sub_bid);
				UserBid new_bid=new UserBid();
				new_bid.setId(new_bid_pk);
				new_bid.setUser(user);
				new_bid.setAuction(currentAuction);
				Date starting_date = new Date();
				new_bid.setTime(starting_date);
				
				user.addUserBidAuction(new_bid);
				currentAuction.addBid(new_bid);
				currentAuction.setCurrentBid(sub_bid);
				currentAuction.setNumOfBids(currentAuction.getNumOfBids()+1);
				
				UserBidDAOI bidao=new UserBidDAO();
				bidao.create(user, currentAuction,starting_date,sub_bid);
				request.getSession().setAttribute("bid_response", "Bid succesfully submitted");
				response.sendRedirect("AuctionView?auctionID="+auctionid+"&page=history");
			}
			else
			{
				request.getSession().setAttribute("bid_response", "Bid must be higher than current bid");
				response.sendRedirect("AuctionView?auctionID="+auctionid+"&page=view");
			}
		}
		else if(action.equals("buyout")){
			/* Buyout implementation */
			
			RequestDispatcher disp;
			if(request.getSession().getAttribute("userID") == null){
				disp = getServletContext().getRequestDispatcher("/loginerror.jsp");
				disp.forward(request, response);
				return;
			}
			AuctionDAOI dao = new AuctionDAO();
			int auctionid=Integer.parseInt(request.getParameter("auctionID"));
			Auction currentAuction = dao.findByID(auctionid);
			User buyout_user=currentAuction.getUser();
			Date expiration_date=currentAuction.getExpirationTime();
			Date current_date = new Date();
			if(current_date.after(expiration_date) || buyout_user!=null){
				request.getSession().setAttribute("bid_response", "Error,item expired or already bought");
				response.sendRedirect("AuctionView?auctionID="+auctionid+"&page=view");
			}
			UserDAOI udao = new UserDAO();
			User user = udao.findByID(request.getSession().getAttribute("userID").toString());
			currentAuction.setUser(user);
			request.getSession().setAttribute("bid_response", "Item successfully bought");
			response.sendRedirect("AuctionView?auctionID="+auctionid+"&page=view");
		}
	}

}
