package dao;

import entities.Auction;
import entities.Category;

public interface CategoryDAOI {
	public Category find(String name);
	
	public boolean create(Category category);
	
	public boolean addAuctionTo(Auction auction, String category);
}
