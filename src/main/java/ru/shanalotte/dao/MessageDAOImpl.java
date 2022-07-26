package ru.shanalotte.dao;

import java.util.List;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.shanalotte.entities.Message;
import ru.shanalotte.entities.User;
import ru.shanalotte.utils.EntityManagerHelper;

@Repository
public class MessageDAOImpl implements MessageDAO {

	private EntityManagerHelper entityManagerHelper;

	@Autowired
	public MessageDAOImpl(EntityManagerHelper entityManagerHelper) {
		this.entityManagerHelper = entityManagerHelper;
	}

	@Override
	public void create(Message msg) {
		EntityManager em = entityManagerHelper.getEntityManager();
		em.persist(msg);
		em.flush();
	}

	@Override
	public Message find(int msg_id) {
		EntityManager em = entityManagerHelper.getEntityManager();
		Message msg = em.find(Message.class, msg_id); 
        return msg;
	}

	@Override
	public List<Message> getSentOf(User user, int page, int entries_per_page) {
		EntityManager em = entityManagerHelper.getEntityManager();
		TypedQuery<Message> getSentMsgQ = em.createNamedQuery("Message.getSentOf", Message.class);
		getSentMsgQ.setParameter("user", user);
		getSentMsgQ.setFirstResult(entries_per_page * page);
		getSentMsgQ.setMaxResults(entries_per_page);
		return getSentMsgQ.getResultList();
	}

	@Override
	public List<Message> getReceivedOf(User user, int page, int entries_per_page) {
		EntityManager em = entityManagerHelper.getEntityManager();
		TypedQuery<Message> getReceivedMsgQ = em.createNamedQuery("Message.getReceivedOf", Message.class);
		getReceivedMsgQ.setParameter("user", user);
		getReceivedMsgQ.setFirstResult(entries_per_page * page);
		getReceivedMsgQ.setMaxResults(entries_per_page);
		return getReceivedMsgQ.getResultList();
	}

	@Override
	public long getCountSent(User user) {
		EntityManager em = entityManagerHelper.getEntityManager();
		Query getCountQ = em.createNamedQuery("Message.countSent");
		getCountQ.setParameter("user", user);
		return ((Number) getCountQ.getSingleResult()).longValue();
	}

	@Override
	public long getCountReceived(User user) {
		EntityManager em = entityManagerHelper.getEntityManager();
		Query getCountQ = em.createNamedQuery("Message.countReceived");
		getCountQ.setParameter("user", user);
		return ((Number) getCountQ.getSingleResult()).longValue();
	}

	@Override
	public long getCountUnreadOf(User user) {
		EntityManager em = entityManagerHelper.getEntityManager();
		Query getUnreadMsgQ = em.createNamedQuery("Message.getUnreadOf");
		getUnreadMsgQ.setParameter("user", user);
		return ((Number) getUnreadMsgQ.getSingleResult()).longValue();
	}

	@Override
	public void delete(int msg_id) {
		EntityManager em = entityManagerHelper.getEntityManager();
		Message mymsg = em.find(Message.class, msg_id);
		em.remove(mymsg);
	}

	@Override
	public void update(Message message) {
		EntityManager em = entityManagerHelper.getEntityManager();
		em.merge(message);
	}

}
