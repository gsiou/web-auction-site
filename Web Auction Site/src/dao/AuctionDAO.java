package dao;

import javax.persistence.EntityManager;

import entities.Auction;
import entities.User;
import utils.EntityManagerHelper;

public class AuctionDAO implements AuctionDAOI{

	@Override
	public boolean create(Auction auction) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		if(findByID(auction.getAuctionId()) == null){
			em.persist(auction);
			em.flush();
			return true;
		}
		else{
			return false;
		}
	}
	
	@Override
	public Auction findByID(int id){
		EntityManager em = EntityManagerHelper.getEntityManager();
		Auction auction = em.find(Auction.class, id); 
        return auction;
	}
	
}
