package ru.shanalotte.dao;

import java.util.List;

import ru.shanalotte.entities.Auction;
import ru.shanalotte.entities.Category;

public interface CategoryDAOI {
	public Category find(String name);
	
	public boolean create(Category category);
	
	public boolean addAuctionTo(Auction auction, String category);
	
	public List<Category> listChildren(String parent);
	
	public List<Category> findAll();
}
