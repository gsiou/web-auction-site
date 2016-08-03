package dao;

import entities.User;

public interface UserDAOI {
	
	public boolean create(User user);
	
	public User findByID(String user_id);

}
