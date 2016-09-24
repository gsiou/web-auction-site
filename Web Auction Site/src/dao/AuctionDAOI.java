package dao;

import java.util.Date;
import java.util.List;

import entities.Auction;
import entities.User;

public interface AuctionDAOI {
	
	public boolean create(Auction auction);
	
	public Auction findByID(int id);
	
	public List<Auction> search(AuctionSearchOptions search_options);
	
	public List<Auction> findInactiveOf(User user);
	
	public List<Auction> findActiveOf(User user, Date date);
	
	public List<Auction> findSoldOf(User user, Date date);
	
	public List<Auction> findUserBiddedAuctions(User user, Date date);
	
	public List<Auction> findUserWonAuctions(User user, Date date);
	
	public List<Auction> findUserLostAuctions(User user, Date date);
}
