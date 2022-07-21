package ru.shanalotte.dao;

import java.util.List;

import ru.shanalotte.entities.Auction;
import ru.shanalotte.entities.Image;

public interface ImageDAOI {
	public boolean create(Image image);
	
	public Image find(String url);
	
	public List<Image> findImagesofAuction(Auction auction);
}
