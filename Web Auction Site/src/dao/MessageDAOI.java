package dao;

import java.util.List;

import entities.Message;
import entities.User;

public interface MessageDAOI {
	public void create(Message msg);
	
	public Message find(int msg_id);
	
	public void delete(int msg_id);
	
	public void update(Message message);
	
	public List<Message> getSentOf(User user, int page, int entries_per_page);
	
	public List<Message> getReceivedOf(User user, int page, int entries_per_page);
	
	public long getCountSent(User user);
	
	public long getCountReceived(User user);
	
	public long getCountUnreadOf(User user);
}
