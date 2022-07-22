package ru.shanalotte.dao;

import java.util.List;

import jakarta.persistence.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.shanalotte.entities.Auction;
import ru.shanalotte.entities.Category;
import ru.shanalotte.utils.EntityManagerHelper;

@Repository
public class CategoryDAOImpl implements CategoryDAO {


	private EntityManagerHelper entityManagerHelper;

	@Autowired
	public CategoryDAOImpl(EntityManagerHelper entityManagerHelper) {
		this.entityManagerHelper = entityManagerHelper;
	}

	@Override
	public Category find(String name){
		EntityManager em = entityManagerHelper.getEntityManager();
		Category cat = em.find(Category.class, name); 
        return cat;
	}
	
	@Override
	public boolean create(Category category){
		EntityManager em = entityManagerHelper.getEntityManager();
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
		EntityManager em = entityManagerHelper.getEntityManager();
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
		EntityManager em = entityManagerHelper.getEntityManager();
		TypedQuery<Category> getAll;
		getAll = em.createNamedQuery("Category.findAll", Category.class);
		return getAll.getResultList();
	}
}
