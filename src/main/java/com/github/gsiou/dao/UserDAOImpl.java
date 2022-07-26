package com.github.gsiou.dao;

import java.util.List;

import com.github.gsiou.entities.User;
import jakarta.persistence.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.github.gsiou.utils.EntityManagerHelper;

@Repository
public class UserDAOImpl implements UserDAO {

	private EntityManagerHelper entityManagerHelper;

	@Autowired
	public UserDAOImpl(EntityManagerHelper entityManagerHelper) {
		this.entityManagerHelper = entityManagerHelper;
	}


	@Override
	public User findByID(String user_id){
		EntityManager em = entityManagerHelper.getEntityManager();
		User user = em.find(User.class, user_id); 
        return user;
	}

	
	@Override
	public boolean create(User user) {
		EntityManager em = entityManagerHelper.getEntityManager();
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
		EntityManager em = entityManagerHelper.getEntityManager();
		Query query = em.createNamedQuery("User.findAll");
		@SuppressWarnings("unchecked")
		List<User> users = query.getResultList();  
        return users;
	}

	@Override
	public void changeAccess(User user, int access_level){
		user.setAccessLvl(access_level);
		EntityManager em = entityManagerHelper.getEntityManager();
		em.persist(user);
		em.flush();
	}
	
	@Override public void update(User tempUser){
		EntityManager em = entityManagerHelper.getEntityManager();
		em.merge(tempUser);
	}

	@Override
	public List<User> listUsersOfPage(int page, int entries_per_page) {
		EntityManager em = entityManagerHelper.getEntityManager();
		TypedQuery<User> getUsers = em.createNamedQuery("User.findAll", User.class);
		getUsers.setFirstResult(page * entries_per_page);
		getUsers.setMaxResults(entries_per_page);
		return getUsers.getResultList();
	}

	@Override
	public List<User> listUnactivatedUsersOfPage(int page, int entries_per_page) {
		EntityManager em = entityManagerHelper.getEntityManager();
		TypedQuery<User> getUsers = em.createNamedQuery("User.findUnactivated", User.class);
		getUsers.setFirstResult(page * entries_per_page);
		getUsers.setMaxResults(entries_per_page);
		return getUsers.getResultList();
	}

	@Override
	public long userCount() {
		EntityManager em = entityManagerHelper.getEntityManager();
		Query countUsers = em.createNamedQuery("User.countAll");
		return ((Number) countUsers.getSingleResult()).intValue();
	}

	@Override
	public long unactivatedUserCount() {
		EntityManager em = entityManagerHelper.getEntityManager();
		Query countUsers = em.createNamedQuery("User.countUnactivated");
		return ((Number) countUsers.getSingleResult()).intValue();
	}

	@Override
	public List<User> listFrequentBidders(User caller) {
		EntityManager em = entityManagerHelper.getEntityManager();
		TypedQuery<User> query = em.createNamedQuery("User.findFrequentBidders", User.class);
		query.setParameter("user", caller);
		List<User> users = query.getResultList();  
        return users;
	}
}
