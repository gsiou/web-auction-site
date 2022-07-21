package ru.shanalotte.dao;

import java.util.Date;

import ru.shanalotte.entities.Auction;
import ru.shanalotte.entities.User;
import ru.shanalotte.entities.User_bid_Auction;

public interface User_bid_AuctionDAOI {
	
	public boolean create(User user, Auction auction, Date time, float amount);
	
	public User_bid_Auction find(String userid, int aucid);

	boolean create(User_bid_Auction uba);
}
