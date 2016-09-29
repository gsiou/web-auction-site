package dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

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
	
	@Override public void update(User tempUser){
		EntityManager em = EntityManagerHelper.getEntityManager();
		em.merge(tempUser);
	}

	@Override
	public List<User> listUsersOfPage(int page, int entries_per_page) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		TypedQuery<User> getUsers = em.createNamedQuery("User.findAll", User.class);
		getUsers.setFirstResult(page * entries_per_page);
		getUsers.setMaxResults(entries_per_page);
		return getUsers.getResultList();
	}

	@Override
	public List<User> listUnactivatedUsersOfPage(int page, int entries_per_page) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		TypedQuery<User> getUsers = em.createNamedQuery("User.findUnactivated", User.class);
		getUsers.setFirstResult(page * entries_per_page);
		getUsers.setMaxResults(entries_per_page);
		return getUsers.getResultList();
	}

	@Override
	public long userCount() {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Query countUsers = em.createNamedQuery("User.countAll");
		return ((Number) countUsers.getSingleResult()).intValue();
	}

	@Override
	public long unactivatedUserCount() {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Query countUsers = em.createNamedQuery("User.countUnactivated");
		return ((Number) countUsers.getSingleResult()).intValue();
	}

	@Override
	public List<User> listFrequentBidders() {
		EntityManager em = EntityManagerHelper.getEntityManager();
		TypedQuery<User> query = em.createNamedQuery("User.findFrequentBidders", User.class);
		List<User> users = query.getResultList();  
        return users;
	}
}
