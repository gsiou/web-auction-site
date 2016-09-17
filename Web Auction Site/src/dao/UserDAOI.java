package dao;

import entities.User;
import java.util.List;

public interface UserDAOI {
	
	public boolean create(User user);
	
	public User findByID(String user_id);

	public List<User> list();
	
	public List<User> listUsersOfPage(int page, int entries_per_page);
	
	public List<User> listUnactivatedUsersOfPage(int page, int entries_per_page);
	
	public long userCount();
	
	public long unactivatedUserCount();
	
	public void changeAccess(User user, int access_level);
	
	public void update(User user);
}
