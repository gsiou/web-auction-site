package dao;

import entities.Image;

public interface ImageDAOI {
	public boolean create(Image image);
	
	public Image find(String url);
}
