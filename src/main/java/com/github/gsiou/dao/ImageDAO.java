package com.github.gsiou.dao;

import java.util.List;

import com.github.gsiou.entities.Auction;
import com.github.gsiou.entities.Image;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageDAO {
	boolean create(Image image);
	
	Image find(String url);
	
	List<Image> findImagesofAuction(Auction auction);
}
