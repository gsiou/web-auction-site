package dao;

import java.util.List;

import entities.Auction;
import entities.Image;

public interface ImageDAOI {
	public boolean create(Image image);
	
	public Image find(String url);
	
	public List<Image> findImagesofAuction(Auction auction);
}
