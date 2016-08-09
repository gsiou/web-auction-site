package dao;

import java.util.Date;

import javax.persistence.EntityManager;

import entities.Auction;
import entities.User;
import entities.User_bid_Auction;
import entities.User_bid_AuctionPK;
import utils.EntityManagerHelper;

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
	public User_bid_Auction find(String userid, int aucid) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		User_bid_AuctionPK pk = new User_bid_AuctionPK();
		pk.setAuction_AuctionId(aucid);
		pk.setUser_UserId(userid);
		User_bid_Auction uba = em.find(User_bid_Auction.class, pk);
        return uba;
	}
}
