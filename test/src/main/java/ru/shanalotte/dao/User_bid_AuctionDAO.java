package ru.shanalotte.dao;

import java.util.Date;

import jakarta.persistence.*;
import ru.shanalotte.entities.Auction;
import ru.shanalotte.entities.User;
import ru.shanalotte.entities.User_bid_Auction;
import ru.shanalotte.entities.User_bid_AuctionPK;
import ru.shanalotte.utils.EntityManagerHelper;

public class User_bid_AuctionDAO implements User_bid_AuctionDAOI{
	
	@Override
	public boolean create(User user, Auction auction, Date time, float amount){
		String userid = user.getUserId();
		int aucid = auction.getAuctionId();
		User_bid_AuctionPK pk = new User_bid_AuctionPK();
		pk.setAuction_AuctionId(aucid);
		pk.setUser_UserId(userid);
		User_bid_Auction uba = new User_bid_Auction();
		uba.setId(pk);
		uba.setTime(time);
		uba.setPrice(amount);
		uba.setAuction(auction);
		uba.setUser(user);
		uba.setAuction(auction);
		EntityManager em = EntityManagerHelper.getEntityManager();
		if(find(userid, aucid) == null){
			em.persist(uba);
			em.flush();
			return true;
		}
		else{
			return false;
		}
	}
	
	@Override
	public boolean create(User_bid_Auction uba){
		EntityManager em = EntityManagerHelper.getEntityManager();
		if(find(uba.getId().getUser_UserId(), uba.getId().getAuction_AuctionId()) == null){
			em.persist(uba);
			em.flush();
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public User_bid_Auction find(String userid, int aucid) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		User_bid_AuctionPK pk = new User_bid_AuctionPK();
		pk.setAuction_AuctionId(aucid);
		pk.setUser_UserId(userid);
		User_bid_Auction uba = em.find(User_bid_Auction.class, pk);
        return uba;
	}
}
