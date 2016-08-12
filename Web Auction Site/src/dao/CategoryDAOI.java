package dao;

import java.util.List;

import entities.Auction;
import entities.Category;

public interface CategoryDAOI {
	public Category find(String name);
	
	public boolean create(Category category);
	
	public boolean addAuctionTo(Auction auction, String category);
	
	public List<Category> listChildren(String parent);
}
