package dao;

import javax.persistence.EntityManager;

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
	
}
