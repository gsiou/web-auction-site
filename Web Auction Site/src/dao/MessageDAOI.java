package dao;

import entities.Message;

public interface MessageDAOI {
	public void create(Message msg);
	
	public Message find(int msg_id);
}
