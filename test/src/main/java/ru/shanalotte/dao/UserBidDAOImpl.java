package ru.shanalotte.dao;

import java.util.Date;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.shanalotte.entities.Auction;
import ru.shanalotte.entities.User;
import ru.shanalotte.entities.UserBid;
import ru.shanalotte.entities.UserBidPK;
import ru.shanalotte.utils.EntityManagerHelper;

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
