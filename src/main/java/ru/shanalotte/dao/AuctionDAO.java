package ru.shanalotte.dao;

import java.util.Date;
import java.util.List;

import ru.shanalotte.entities.Auction;
import ru.shanalotte.entities.User;
import ru.shanalotte.entities.UserBid;

public interface AuctionDAO {
	
	boolean create(Auction auction);
	
	Auction findByID(int id);
	
	List<Auction> search(AuctionSearchOptions search_options, int page, int entries_per_page);
	
	List<Auction> list();
	
	List<Auction> findInactiveOf(User user);
	
	List<Auction> findActiveOf(User user, Date date);
	
	List<Auction> findSoldOf(User user, Date date);
	
	List<Auction> findUserBiddedAuctions(User user, Date date);
	
	List<Auction> findUserWonAuctions(User user, Date date);
	
	List<Auction> findUserLostAuctions(User user, Date date);
	
	List<UserBid> findAuctionBids(Auction auction);
	
	List<Auction> findUserUniqueBids(User user);
	
	List<Auction> findUserUniqueActiveBids(User user, Date date);
	
	List<Auction> findPopular(int number_of_auctions, Date date);
	
	void updateAuction(Auction updated_auction);
}
