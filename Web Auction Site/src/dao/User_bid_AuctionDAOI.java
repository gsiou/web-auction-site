package dao;

import java.util.Date;

import entities.Auction;
import entities.User;
import entities.User_bid_Auction;

public interface User_bid_AuctionDAOI {
	
	public boolean create(User user, Auction auction, Date time, float amount);
	
	public User_bid_Auction find(String userid, int aucid);
}
