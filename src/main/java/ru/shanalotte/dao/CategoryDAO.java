package ru.shanalotte.dao;

import java.util.List;

import org.springframework.stereotype.Repository;
import ru.shanalotte.entities.Auction;
import ru.shanalotte.entities.Category;

@Repository
public interface CategoryDAO {
	Category find(String name);
	
	boolean create(Category category);
	
	boolean addAuctionTo(Auction auction, String category);
	
	List<Category> listChildren(String parent);
	
	List<Category> findAll();
}
