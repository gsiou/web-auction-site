package dao;

import entities.Auction;

public interface AuctionDAOI {
	
	public boolean create(Auction auction);
	
	public Auction findByID(int id);
	
}
