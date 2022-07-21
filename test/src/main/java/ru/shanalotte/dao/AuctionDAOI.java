package ru.shanalotte.dao;

import java.util.Date;
import java.util.List;

import ru.shanalotte.entities.Auction;
import ru.shanalotte.entities.User;
import ru.shanalotte.entities.UserBid;

public interface AuctionDAOI {
	
	public boolean create(Auction auction);
	
	public Auction findByID(int id);
	
	public List<Auction> search(AuctionSearchOptions search_options, int page, int entries_per_page);
	
	public List<Auction> list();
	
	public List<Auction> findInactiveOf(User user);
	
	public List<Auction> findActiveOf(User user, Date date);
	
	public List<Auction> findSoldOf(User user, Date date);
	
	public List<Auction> findUserBiddedAuctions(User user, Date date);
	
	public List<Auction> findUserWonAuctions(User user, Date date);
	
	public List<Auction> findUserLostAuctions(User user, Date date);
	
	public List<UserBid> findAuctionBids(Auction auction);
	
	public List<Auction> findUserUniqueBids(User user);
	
	public List<Auction> findUserUniqueActiveBids(User user, Date date);
	
	public List<Auction> findPopular(int number_of_auctions, Date date);
	
	public void updateAuction(Auction updated_auction);
}
