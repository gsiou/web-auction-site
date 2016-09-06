package servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Collections;
import java.util.Comparator;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.UserDAO;
import dao.UserDAOI;
import entities.User;
import entities.Image;
import entities.Category;
import entities.User_bid_Auction;
import entities.User_bid_AuctionPK;
import dao.AuctionDAO;
import dao.AuctionDAOI;
import entities.Auction;
import dao.User_bid_AuctionDAO;
import dao.User_bid_AuctionDAOI;

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
			System.out.println("Null action");
			return ;
		}
		else if(action.equals("history")){
			AuctionDAOI dao = new AuctionDAO();
			int auctionid=Integer.parseInt(request.getParameter("auctionID"));
			Auction currentAuction = dao.findByID(auctionid);
			String auction_name=currentAuction.getName();
			request.setAttribute("name",auction_name);
			List<User_bid_Auction> bidding_users=currentAuction.getUserBidAuctions();
			Comparator<User_bid_Auction> bidComparator = new Comparator<User_bid_Auction>() {
			    public int compare(User_bid_Auction b1, User_bid_Auction b2) {
			        return b1.getTime().compareTo(b2.getTime());
			    }
			};
			Collections.sort(bidding_users, bidComparator);
			for (User_bid_Auction auct_im : bidding_users) {
	            System.out.println(auct_im.getPrice());
			}
			request.setAttribute("user_biddings",bidding_users);
			float starting_bid=currentAuction.getStarting_Bid();
			request.setAttribute("starting_bid",starting_bid);
			Date start_time=currentAuction.getStart_time();
			request.setAttribute("start_time",start_time);
			List<Image> auction_images=currentAuction.getImages();
			if(auction_images.isEmpty()){
				request.setAttribute("image","default_img.png");
			}
			else{
				request.setAttribute("image",auction_images.get(0).getUrl());
			}
			float current_bid=currentAuction.getCurrent_Bid();
			request.setAttribute("current_bid",current_bid);
			disp = getServletContext().getRequestDispatcher("/bid_history.jsp");
			disp.forward(request, response);
		}
		else if(action.equals("view")){
			AuctionDAOI dao = new AuctionDAO();
			int auctionid=Integer.parseInt(request.getParameter("auctionID"));
			Auction currentAuction = dao.findByID(auctionid);
			String auction_name=currentAuction.getName();
			request.setAttribute("name",auction_name);
			List<Image> auction_images=currentAuction.getImages();
			ArrayList<String> image_paths=new ArrayList<>();
			for (Image auct_im : auction_images) {
	            image_paths.add(auct_im.getUrl());
			}
			if(image_paths.isEmpty()){
				image_paths.add("default_img.png");
			}	
			request.setAttribute("imageList",image_paths);
			List<Category> auction_categories=currentAuction.getCategories();
			ArrayList<String> categories=new ArrayList<>();
			for (Category auct_cat : auction_categories) {
	            categories.add(auct_cat.getName());
			}
			request.setAttribute("categories",categories);
			
			float auct_latitude=currentAuction.getLatitude();
			float auct_longitude=currentAuction.getLongitude();
			String location=currentAuction.getLocation();
			String country=currentAuction.getCountry();
			float buy_price=currentAuction.getBuy_Price();
			float starting_bid=currentAuction.getStarting_Bid();
			float current_bid=currentAuction.getCurrent_Bid();
			int num_of_bids=currentAuction.getNum_of_bids();
			String description=currentAuction.getDescription();
			Date start_time=currentAuction.getStart_time();
			Date expiration_time=currentAuction.getExpiration_time();
			
			request.setAttribute("latitude",auct_latitude);
			request.setAttribute("longitude",auct_longitude);
			request.setAttribute("location",location);
			request.setAttribute("country",country);
			request.setAttribute("buy_price",buy_price);
			request.setAttribute("starting_bid",starting_bid);
			request.setAttribute("current_bid",current_bid);
			request.setAttribute("num_of_bids",num_of_bids);
			request.setAttribute("description",description);
			request.setAttribute("start_time",start_time);
			request.setAttribute("expiration_time",expiration_time);
			
			disp = getServletContext().getRequestDispatcher("/auctionview.jsp");
			disp.forward(request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		RequestDispatcher disp;
		if(request.getSession().getAttribute("userID") == null){
			disp = getServletContext().getRequestDispatcher("/loginerror.jsp");
			disp.forward(request, response);
			return;
		}
		AuctionDAOI dao = new AuctionDAO();
		int auctionid=Integer.parseInt(request.getParameter("auctionID"));
		Auction currentAuction = dao.findByID(auctionid);
		float current_bid=currentAuction.getCurrent_Bid();
		String submited_bid=request.getParameter("Bid_input");
		float sub_bid=Float.parseFloat(submited_bid);
		if(sub_bid>current_bid){
			User_bid_Auction new_bid=new User_bid_Auction();
			new_bid.setAuction(currentAuction);
			new_bid.setPrice(sub_bid);
			Date starting_date = new Date();
			new_bid.setTime(starting_date);
			UserDAOI udao = new UserDAO();
			User user = udao.findByID(request.getSession().getAttribute("userID").toString());
			new_bid.setUser(user);
			User_bid_AuctionPK new_bid_pk=new User_bid_AuctionPK();
			new_bid_pk.setAuction_AuctionId(currentAuction.getAuctionId());
			new_bid_pk.setUser_UserId(user.getUserId());
			new_bid.setId(new_bid_pk);
			
			user.addUserBidAuction(new_bid);
			currentAuction.addUserBidAuction(new_bid);
			currentAuction.setCurrent_Bid(sub_bid);
			currentAuction.setNum_of_bids(currentAuction.getNum_of_bids()+1);
			
			User_bid_AuctionDAOI bidao=new User_bid_AuctionDAO();
			bidao.create(user, currentAuction,starting_date,sub_bid);
			request.getSession().setAttribute("bid_response", "Bid succesfully submitted");
			response.sendRedirect("AuctionView?auctionID="+auctionid+"&page=history");
		}
		else{
			request.getSession().setAttribute("bid_response", "Bid must be higher than current bid");
			response.sendRedirect("AuctionView?auctionID="+auctionid+"&page=view");
		}
	}

}
