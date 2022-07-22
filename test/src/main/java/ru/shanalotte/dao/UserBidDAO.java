package ru.shanalotte.dao;

import java.util.Date;

import ru.shanalotte.entities.Auction;
import ru.shanalotte.entities.User;
import ru.shanalotte.entities.UserBid;

public interface UserBidDAO {
	
	boolean create(User user, Auction auction, Date time, float amount);
	
	UserBid find(String userid, int aucid);

	boolean create(UserBid uba);
}
