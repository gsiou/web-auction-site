package dao;

import javax.persistence.EntityManager;

import entities.Image;
import entities.User;
import utils.EntityManagerHelper;

public class ImageDAO implements ImageDAOI{

	@Override
	public boolean create(Image image) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		if(find(image.getUrl()) == null){
			em.persist(image);
			em.flush();
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public Image find(String url) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Image image = em.find(Image.class, url); 
        return image;
	}
	
}
