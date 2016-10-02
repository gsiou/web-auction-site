package dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import entities.Auction;
import entities.Image;
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
	
	@Override
	public List<Image> findImagesofAuction(Auction auction) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		TypedQuery<Image> getAuctImages;
		getAuctImages = em.createNamedQuery("Image.findImagesofAuction", Image.class);
		getAuctImages.setParameter("auction", auction);
		return getAuctImages.getResultList();
	}
	
}
