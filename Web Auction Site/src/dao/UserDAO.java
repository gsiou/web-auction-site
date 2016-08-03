package dao;

import javax.persistence.EntityManager;

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

}
