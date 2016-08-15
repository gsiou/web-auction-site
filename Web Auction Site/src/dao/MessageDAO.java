package dao;

import javax.persistence.EntityManager;

import entities.Message;
import utils.EntityManagerHelper;

public class MessageDAO implements MessageDAOI{

	@Override
	public void create(Message msg) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		em.persist(msg);
		em.flush();
	}

	@Override
	public Message find(int msg_id) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Message msg = em.find(Message.class, msg_id); 
        return msg;
	}

}
