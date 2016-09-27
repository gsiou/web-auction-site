package dao;

import java.util.Date;
import java.util.List;

import entities.Auction;
import entities.User;
import entities.User_bid_Auction;

public interface AuctionDAOI {
	
	public boolean create(Auction auction);
	
	public Auction findByID(int id);
	
	public List<Auction> search(AuctionSearchOptions search_options, int page, int entries_per_page);
	
	public List<Auction> findInactiveOf(User user);
	
	public List<Auction> findActiveOf(User user, Date date);
	
	public List<Auction> findSoldOf(User user, Date date);
	
	public List<Auction> findUserBiddedAuctions(User user, Date date);
	
	public List<Auction> findUserWonAuctions(User user, Date date);
	
	public List<Auction> findUserLostAuctions(User user, Date date);
	
	public List<User_bid_Auction> findAuctionBids(Auction auction);
	
	public void updateAuction(Auction updated_auction);
}
