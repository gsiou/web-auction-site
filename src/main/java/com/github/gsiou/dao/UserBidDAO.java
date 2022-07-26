package com.github.gsiou.dao;

import java.util.Date;

import com.github.gsiou.entities.Auction;
import com.github.gsiou.entities.User;
import com.github.gsiou.entities.UserBid;

public interface UserBidDAO {
	
	boolean create(User user, Auction auction, Date time, float amount);
	
	UserBid find(String userid, int aucid);

	boolean create(UserBid uba);
}
