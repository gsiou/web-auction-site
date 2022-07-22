package ru.shanalotte.dao;

import ru.shanalotte.entities.User;
import java.util.List;

public interface UserDAO {
	
	boolean create(User user);
	
	User findByID(String user_id);

	List<User> list();
	
	List<User> listFrequentBidders(User caller);
	
	List<User> listUsersOfPage(int page, int entries_per_page);
	
	List<User> listUnactivatedUsersOfPage(int page, int entries_per_page);
	
	long userCount();
	
	long unactivatedUserCount();
	
	void changeAccess(User user, int access_level);
	
	void update(User user);
}
