package com.github.gsiou.dao;

import java.util.Date;

import com.github.gsiou.entities.Auction;
import com.github.gsiou.entities.User;
import com.github.gsiou.entities.UserBid;
import com.github.gsiou.entities.UserBidPK;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.github.gsiou.utils.EntityManagerHelper;

@Repository
public class UserBidDAOImpl implements UserBidDAO {

	private EntityManagerHelper entityManagerHelper;

	@Autowired
	public UserBidDAOImpl(EntityManagerHelper entityManagerHelper) {
		this.entityManagerHelper = entityManagerHelper;
	}


	@Override
	public boolean create(User user, Auction auction, Date time, float amount){
		String userid = user.getUserId();
		int aucid = auction.getAuctionId();
		UserBidPK pk = new UserBidPK();
		pk.setAuctionId(aucid);
		pk.setUserId(userid);
		UserBid uba = new UserBid();
		uba.setId(pk);
		uba.setTime(time);
		uba.getId().setPrice(amount);
		uba.setAuction(auction);
		uba.setUser(user);
		uba.setAuction(auction);
		EntityManager em = entityManagerHelper.getEntityManager();
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
	public boolean create(UserBid uba){
		EntityManager em = entityManagerHelper.getEntityManager();
		if(find(uba.getId().getUserId(), uba.getId().getAuctionId()) == null){
			em.persist(uba);
			em.flush();
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public UserBid find(String userid, int aucid) {
		EntityManager em = entityManagerHelper.getEntityManager();
		UserBidPK pk = new UserBidPK();
		pk.setAuctionId(aucid);
		pk.setUserId(userid);
		UserBid uba = em.find(UserBid.class, pk);
        return uba;
	}
}
