package ru.shanalotte.dao;

import java.util.List;

import jakarta.persistence.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.shanalotte.entities.Auction;
import ru.shanalotte.entities.Image;
import ru.shanalotte.utils.EntityManagerHelper;

@Repository
public class ImageDAOImpl implements ImageDAO {

	private EntityManagerHelper entityManagerHelper;

	@Autowired
	public ImageDAOImpl(EntityManagerHelper entityManagerHelper) {
		this.entityManagerHelper = entityManagerHelper;
	}

	@Override
	public boolean create(Image image) {
		EntityManager em = entityManagerHelper.getEntityManager();
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
		EntityManager em = entityManagerHelper.getEntityManager();
		Image image = em.find(Image.class, url); 
        return image;
	}
	
	@Override
	public List<Image> findImagesofAuction(Auction auction) {
		EntityManager em = entityManagerHelper.getEntityManager();
		TypedQuery<Image> getAuctImages;
		getAuctImages = em.createNamedQuery("Image.findImagesofAuction", Image.class);
		getAuctImages.setParameter("auction", auction);
		return getAuctImages.getResultList();
	}
	
}
