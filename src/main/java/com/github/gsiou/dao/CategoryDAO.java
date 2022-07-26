package com.github.gsiou.dao;

import java.util.List;

import com.github.gsiou.entities.Auction;
import com.github.gsiou.entities.Category;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryDAO {
	Category find(String name);
	
	boolean create(Category category);
	
	boolean addAuctionTo(Auction auction, String category);
	
	List<Category> listChildren(String parent);
	
	List<Category> findAll();
}
