package ru.shanalotte.dao;

import java.util.List;

import ru.shanalotte.entities.Message;
import ru.shanalotte.entities.User;
import ru.shanalotte.entities.User;

public interface MessageDAO {
	void create(Message msg);
	
	Message find(int msg_id);
	
	void delete(int msg_id);
	
	void update(Message message);
	
	List<Message> getSentOf(User user, int page, int entries_per_page);
	
	List<Message> getReceivedOf(User user, int page, int entries_per_page);
	
	long getCountSent(User user);
	
	long getCountReceived(User user);
	
	long getCountUnreadOf(User user);
}
