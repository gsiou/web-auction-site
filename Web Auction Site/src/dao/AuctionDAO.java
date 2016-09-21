package dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import entities.Auction;
import entities.Category;
import utils.EntityManagerHelper;

public class AuctionDAO implements AuctionDAOI{

	@Override
	public boolean create(Auction auction) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		if(findByID(auction.getAuctionId()) == null){
			em.persist(auction);
			em.flush();
			return true;
		}
		else{
			return false;
		}
	}
	
	@Override
	public Auction findByID(int id){
		EntityManager em = EntityManagerHelper.getEntityManager();
		Auction auction = em.find(Auction.class, id); 
        return auction;
	}

	@Override
	public List<Auction> search(AuctionSearchOptions search_options) {
		// Construct the query.
		// We only care about active auctions.
		String query_str = "SELECT a FROM Auction a WHERE " +
				"(a.expiration_time >= :date AND a.user IS NULL)";
		if(search_options.hasCategory()){
			query_str += " AND :category MEMBER OF a.categories";
		}
		if(search_options.hasDescription()){
			query_str += " AND (a.description LIKE :description OR a.name LIKE :description)";
		}
		if(search_options.hasLocation()){
			query_str += " AND a.location LIKE :location";
		}
		if(search_options.hasMinPrice()){
			query_str += " AND (a.current_Bid > :minprice OR a.buy_Price > :minprice)";
		}
		if(search_options.hasMaxPrice()){
			query_str += " AND (a.current_Bid < :maxprice OR a.buy_Price < :maxprice)";
		}

		// Fill in the parameters.
		EntityManager em = EntityManagerHelper.getEntityManager();
		TypedQuery<Auction> searchQuery = em.createQuery(query_str, Auction.class);
		searchQuery.setParameter("date", search_options.getMinDate());
		if(search_options.hasCategory()){
			CategoryDAOI cdao = new CategoryDAO();
			Category category_obj = cdao.find(search_options.getCategory());
			if(category_obj == null){ // Category does not exist, return nothing.
				return null;
			}
			else{ // Category exists, add it to the query.
				searchQuery.setParameter("category", category_obj);
			}
		}
		if(search_options.hasDescription()){
			searchQuery.setParameter("description", "%" + search_options.getDescription() + "%");
		}
		if(search_options.hasLocation()){
			searchQuery.setParameter("location", search_options.getLocation());
		}
		if(search_options.hasMinPrice()){
			searchQuery.setParameter("minprice", search_options.getMinPrice());
		}
		if(search_options.hasMaxPrice()){
			searchQuery.setParameter("maxprice", search_options.getMaxPrice());
		}
		
		return searchQuery.getResultList();
	}
	
}
