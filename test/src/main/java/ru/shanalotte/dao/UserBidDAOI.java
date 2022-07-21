package ru.shanalotte.dao;

import java.util.Date;

import ru.shanalotte.entities.Auction;
import ru.shanalotte.entities.User;
import ru.shanalotte.entities.UserBid;

public interface UserBidDAOI {
	
	public boolean create(User user, Auction auction, Date time, float amount);
	
	public UserBid find(String userid, int aucid);

	boolean create(UserBid uba);
}
