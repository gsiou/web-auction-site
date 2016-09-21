package dao;

import java.util.List;

import entities.Auction;

public interface AuctionDAOI {
	
	public boolean create(Auction auction);
	
	public Auction findByID(int id);
	
	public List<Auction> search(AuctionSearchOptions search_options);
}
