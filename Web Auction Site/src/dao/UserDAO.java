package dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import entities.User;
import utils.EntityManagerHelper;

public class UserDAO implements UserDAOI {

	@Override
	public User findByID(String user_id){
		EntityManager em = EntityManagerHelper.getEntityManager();
		User user = em.find(User.class, user_id); 
        return user;
	}
	
	@Override
	public boolean create(User user) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		if(findByID(user.getUserId()) == null){
			em.persist(user);
			em.flush();
			return true;
		}
		else{
			return false;
		}
	}
	
	@Override
	public List<User> list(){
		EntityManager em = EntityManagerHelper.getEntityManager();
		Query query = em.createNamedQuery("User.findAll");
		@SuppressWarnings("unchecked")
		List<User> users = query.getResultList();  
        return users;
	}

	@Override
	public void changeAccess(User user, int access_level){
		user.setAccess_lvl(access_level);
		EntityManager em = EntityManagerHelper.getEntityManager();
		em.persist(user);
		em.flush();
	}
}
