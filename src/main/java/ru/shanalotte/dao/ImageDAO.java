package ru.shanalotte.dao;

import java.util.List;

import org.springframework.stereotype.Repository;
import ru.shanalotte.entities.Auction;
import ru.shanalotte.entities.Image;

@Repository
public interface ImageDAO {
	boolean create(Image image);
	
	Image find(String url);
	
	List<Image> findImagesofAuction(Auction auction);
}
