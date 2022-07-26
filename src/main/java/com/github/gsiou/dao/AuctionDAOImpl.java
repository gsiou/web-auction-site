package com.github.gsiou.dao;

import java.util.Date;
import java.util.List;
import com.github.gsiou.entities.Auction;
import com.github.gsiou.entities.Category;
import com.github.gsiou.entities.User;
import com.github.gsiou.entities.UserBid;
import com.github.gsiou.utils.EntityManagerHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AuctionDAOImpl implements AuctionDAO {

  @Autowired
  private final EntityManagerHelper entityManagerHelper;
  @Autowired
  private CategoryDAO categoryDAO;

  @Autowired
  public AuctionDAOImpl(EntityManagerHelper entityManagerHelper) {
    this.entityManagerHelper = entityManagerHelper;
  }

  @Override
  public boolean create(Auction auction) {
    EntityManager em = entityManagerHelper.getEntityManager();
    if (findByID(auction.getAuctionId()) == null) {
      em.persist(auction);
      em.flush();
      return true;
    } else {
      return false;
    }
  }

  @Override
  public Auction findByID(int id) {
    EntityManager em = entityManagerHelper.getEntityManager();
    return em.find(Auction.class, id);
  }

  @Override
  public List<Auction> search(AuctionSearchOptions search_options, int page, int entries_per_page) {
    // Construct the query.
    // We only care about active auctions.
    String query_str = "SELECT a FROM Auction a WHERE " +
        "(a.expirationTime >= :date AND a.user IS NULL AND a.startTime IS NOT NULL)";
    if (search_options.hasCategory()) {
      query_str += " AND :category MEMBER OF a.categories";
    }
    if (search_options.hasDescription()) {
      query_str += " AND (a.description LIKE :description OR a.name LIKE :description)";
    }
    if (search_options.hasLocation()) {
      query_str += " AND a.location LIKE :location";
    }
    if (search_options.hasMinPrice()) {
      query_str += " AND (a.currentBid > :minprice OR a.buyPrice > :minprice)";
    }
    if (search_options.hasMaxPrice()) {
      query_str += " AND (a.currentBid < :maxprice OR a.buyPrice < :maxprice)";
    }

    // Fill in the parameters.
    EntityManager em = entityManagerHelper.getEntityManager();
    TypedQuery<Auction> searchQuery = em.createQuery(query_str, Auction.class);
    searchQuery.setParameter("date", search_options.getMinDate());
    if (search_options.hasCategory()) {
      Category category_obj = categoryDAO.find(search_options.getCategory());
      if (category_obj == null) { // Category does not exist, return nothing.
        return null;
      } else { // Category exists, add it to the query.
        searchQuery.setParameter("category", category_obj);
      }
    }
    if (search_options.hasDescription()) {
      searchQuery.setParameter("description", "%" + search_options.getDescription() + "%");
    }
    if (search_options.hasLocation()) {
      searchQuery.setParameter("location", search_options.getLocation());
    }
    if (search_options.hasMinPrice()) {
      searchQuery.setParameter("minprice", search_options.getMinPrice());
    }
    if (search_options.hasMaxPrice()) {
      searchQuery.setParameter("maxprice", search_options.getMaxPrice());
    }

    // Return only a page of the results
    searchQuery.setFirstResult(page * entries_per_page);
    searchQuery.setMaxResults(entries_per_page);
    return searchQuery.getResultList();
  }

  @Override
  public List<Auction> findInactiveOf(User user) {
    EntityManager em = entityManagerHelper.getEntityManager();
    TypedQuery<Auction> searchQuery = em.createNamedQuery("Auction.findInactiveOfUser", Auction.class);
    searchQuery.setParameter("user", user);
    return searchQuery.getResultList();
  }

  @Override
  public List<Auction> findActiveOf(User user, Date date) {
    EntityManager em = entityManagerHelper.getEntityManager();
    TypedQuery<Auction> searchQuery = em.createNamedQuery("Auction.findActiveOfUser", Auction.class);
    searchQuery.setParameter("user", user);
    searchQuery.setParameter("date", date);
    return searchQuery.getResultList();
  }

  @Override
  public List<Auction> findSoldOf(User user, Date date) {
    EntityManager em = entityManagerHelper.getEntityManager();
    TypedQuery<Auction> searchQuery = em.createNamedQuery("Auction.findSoldOfUser", Auction.class);
    searchQuery.setParameter("user", user);
    searchQuery.setParameter("date", date);
    return searchQuery.getResultList();
  }

  @Override
  public List<Auction> findUserBiddedAuctions(User user, Date date) {
    EntityManager em = entityManagerHelper.getEntityManager();
    TypedQuery<Auction> searchQuery = em.createNamedQuery("Auction.findUserBiddedAuctions", Auction.class);
    searchQuery.setParameter("user", user);
    searchQuery.setParameter("date", date);
    return searchQuery.getResultList();
  }

  @Override
  public List<Auction> findUserWonAuctions(User user, Date date) {
    EntityManager em = entityManagerHelper.getEntityManager();
    TypedQuery<Auction> searchQuery = em.createNamedQuery("Auction.findUserWonAuctions", Auction.class);
    searchQuery.setParameter("user", user);
    searchQuery.setParameter("date", date);
    return searchQuery.getResultList();
  }

  @Override
  public List<Auction> findUserLostAuctions(User user, Date date) {
    EntityManager em = entityManagerHelper.getEntityManager();
    TypedQuery<Auction> searchQuery = em.createNamedQuery("Auction.findUserLostAuctions", Auction.class);
    searchQuery.setParameter("user", user);
    searchQuery.setParameter("date", date);
    return searchQuery.getResultList();
  }

  @Override
  public List<UserBid> findAuctionBids(Auction auction) {
    EntityManager em = entityManagerHelper.getEntityManager();
    TypedQuery<UserBid> searchQuery = em.createNamedQuery("Auction.findAuctionBids", UserBid.class);
    searchQuery.setParameter("auction", auction);
    return searchQuery.getResultList();
  }

  @Override
  public void updateAuction(Auction updated_auction) {
    EntityManager em = entityManagerHelper.getEntityManager();
    em.merge(updated_auction);
  }

  @Override
  public List<Auction> findUserUniqueBids(User user) {
    EntityManager em = entityManagerHelper.getEntityManager();
    TypedQuery<Auction> searchQuery = em.createNamedQuery("Auction.findUserUniqueBids", Auction.class);
    searchQuery.setParameter("user", user);
    return searchQuery.getResultList();
  }

  @Override
  public List<Auction> findUserUniqueActiveBids(User user, Date date) {
    EntityManager em = entityManagerHelper.getEntityManager();
    TypedQuery<Auction> searchQuery = em.createNamedQuery("Auction.findUserUniqueActiveBids", Auction.class);
    searchQuery.setParameter("user", user);
    searchQuery.setParameter("date", date);
    return searchQuery.getResultList();
  }

  @Override
  public List<Auction> findPopular(int number_of_auctions, Date date) {
    EntityManager em = entityManagerHelper.getEntityManager();
    TypedQuery<Auction> searchQuery = em.createNamedQuery("Auction.findPopular", Auction.class);
    searchQuery.setParameter("date", date);
    searchQuery.setMaxResults(number_of_auctions);
    return searchQuery.getResultList();
  }

  @Override
  public List<Auction> list() {
    EntityManager em = entityManagerHelper.getEntityManager();
    TypedQuery<Auction> searchQuery = em.createNamedQuery("Auction.findAll", Auction.class);
    return searchQuery.getResultList();
  }
}
