package dao;

import entities.User;
import java.util.List;

public interface UserDAOI {
	
	public boolean create(User user);
	
	public User findByID(String user_id);

	public List<User> list();
	
	public void changeAccess(User user, int access_level);
}
