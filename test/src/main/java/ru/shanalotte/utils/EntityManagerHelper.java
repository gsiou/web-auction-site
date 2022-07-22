package ru.shanalotte.utils;

import jakarta.persistence.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service
public class EntityManagerHelper implements InitializingBean {
	
	private EntityManagerFactory emf;
  private ThreadLocal<EntityManager> threadLocal;

    @Override
    public void afterPropertiesSet() {
        emf = Persistence.createEntityManagerFactory("Web Auction Site");
        threadLocal = new ThreadLocal<>();
    }

    public EntityManager getEntityManager() {
        EntityManager em = threadLocal.get();
        if (em == null) {
            em = emf.createEntityManager();
            threadLocal.set(em);
        }
        return em;
    }


    public void closeEntityManager() {
        EntityManager em = threadLocal.get();
        if (em != null) {
            em.close();
            threadLocal.set(null);
        }
    }

    public void closeEntityManagerFactory() {
        emf.close();
    }

    public void beginTransaction() {
        getEntityManager().getTransaction().begin();
    }
    
    public EntityTransaction getTransaction() {
        return getEntityManager().getTransaction();
    }

    public void rollback() {
        getEntityManager().getTransaction().rollback();
    }

    public void commit() {
        getEntityManager().getTransaction().commit();
    } 

}