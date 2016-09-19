package dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import entities.Auction;
import entities.Category;
import utils.EntityManagerHelper;

public class CategoryDAO implements CategoryDAOI{
	@Override
	public Category find(String name){
		EntityManager em = EntityManagerHelper.getEntityManager();
		Category cat = em.find(Category.class, name); 
        return cat;
	}
	
	@Override
	public boolean create(Category category){
		EntityManager em = EntityManagerHelper.getEntityManager();
		if(find(category.getName()) == null){
			em.persist(category);
			em.flush();
			return true;
		}
		else{
			return false;
		}
	}
	
	@Override
	public boolean addAuctionTo(Auction auction, String category){
		Category cat = find(category);
		if(cat != null){
			cat.getAuctions().add(auction);
				return true;
		}
		return false;
	}
	
	@Override
	public List<Category> listChildren(String parent){
		EntityManager em = EntityManagerHelper.getEntityManager();
		TypedQuery<Category> getChildrenQ;
		if(parent != null){
			getChildrenQ = em.createNamedQuery("Category.findChildren", Category.class);
			getChildrenQ.setParameter("parent", parent);
		}
		else{
			getChildrenQ = em.createNamedQuery("Category.findRoot", Category.class);
		}
		List<Category> category_list = getChildrenQ.getResultList();
		return category_list;
	}

	@Override
	public List<Category> findAll() {
		EntityManager em = EntityManagerHelper.getEntityManager();
		TypedQuery<Category> getAll;
		getAll = em.createNamedQuery("Category.findAll", Category.class);
		return getAll.getResultList();
	}
}
