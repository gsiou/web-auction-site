package entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the User_bid_Auction database table.
 * 
 */
@Entity
@Table(name="User_bid_Auction")
@NamedQuery(name="User_bid_Auction.findAll", query="SELECT u FROM User_bid_Auction u")
public class User_bid_Auction implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private User_bid_AuctionPK id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="Time")
	private Date time;

	//bi-directional many-to-one association to Auction
	@ManyToOne
	@JoinColumn(name="Auction_AuctionId")
	private Auction auction;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="User_UserId")
	private User user;

	public User_bid_Auction() {
	}

	public User_bid_AuctionPK getId() {
		return this.id;
	}

	public void setId(User_bid_AuctionPK id) {
		this.id = id;
	}

	public Date getTime() {
		return this.time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Auction getAuction() {
		return this.auction;
	}

	public void setAuction(Auction auction) {
		this.auction = auction;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setPrice(float price){
		this.id.setPrice(price);
	}
	
	public float getPrice(){
		return this.id.getPrice();
	}
}