package ru.shanalotte.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jakarta.persistence.*;


/**
 * The persistent class for the Auction database table.
 * 
 */
@Entity
@Table(name="Auction")
@NamedQueries({
	@NamedQuery(name="Auction.findAll", query="SELECT a FROM Auction a"),
	@NamedQuery(name="Auction.findInactiveOfUser",
		query="SELECT a FROM Auction a WHERE a.creator = :user AND a.start_time IS NULL"),
	@NamedQuery(name="Auction.findActiveOfUser",
		query="SELECT a FROM Auction a WHERE a.creator = :user AND "
				+ "a.expiration_time >= :date AND a.user IS NULL AND a.start_time IS NOT NULL"),
	@NamedQuery(name="Auction.findSoldOfUser",
		query="SELECT a FROM Auction a WHERE a.creator = :user AND"
				+ "(a.user IS NOT NULL OR a.expiration_time < :date)"),
	@NamedQuery(name="Auction.findUserBiddedAuctions",
		query="SELECT DISTINCT(a) FROM Auction a, User_bid_Auction u WHERE u.user = :user AND u.auction=a "
				+ "AND a.expiration_time > :date AND a.user IS NULL"),
	@NamedQuery(name="Auction.findUserWonAuctions",
		query="SELECT DISTINCT(a) FROM Auction a, User_bid_Auction u WHERE (u.user = :user AND u.auction=a AND "
				+ "a.expiration_time < :date AND u.id.price = a.current_Bid) OR (a.user = :user)"),
	@NamedQuery(name="Auction.findUserLostAuctions",
		query="SELECT DISTINCT(a) FROM Auction a, User_bid_Auction u WHERE (u.user = :user AND u.auction=a AND "
				+ "a.expiration_time < :date AND u.id.price <> a.current_Bid) AND (a.user IS NULL OR a.user <> :user)"),
	@NamedQuery(name="Auction.findAuctionBids",
		query="SELECT u FROM Auction a, User_bid_Auction u WHERE u.auction = :auction AND u.auction=a "
				+ " ORDER BY u.time DESC"),
	@NamedQuery(name="Auction.findUserUniqueBids",
		query="SELECT DISTINCT(uba.auction) FROM User_bid_Auction uba WHERE uba.user = :user"),
	@NamedQuery(name="Auction.findUserUniqueActiveBids",
		query="SELECT DISTINCT(uba.auction) FROM User_bid_Auction uba WHERE uba.user = :user "
			+ "AND uba.auction.expiration_time >= :date AND uba.auction.user IS NULL AND uba.auction.start_time IS NOT NULL"),
	@NamedQuery(name="Auction.findPopular", 
		query="SELECT a FROM Auction a WHERE "
				+ "a.expiration_time >= :date AND a.user IS NULL AND a.start_time IS NOT NULL "
				+ "ORDER BY a.num_of_bids DESC")
})
public class Auction implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name="AuctionId" ,insertable = false, updatable = false)
	private int auctionId;

	@Column(name="Buy_Price")
	private float buy_Price;

	@Column(name="Country")
	private String country;

	@JoinColumn(name="Creator")
	@ManyToOne(cascade = CascadeType.PERSIST)
	private User creator;

	@Column(name="Current_Bid")
	private float current_Bid;

	@Lob
	@Column(name="Description")
	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="Expiration_time")
	private Date expiration_time;

	@Column(name="Latitude")
	private float latitude;

	@Column(name="Location")
	private String location;

	@Column(name="Longitude")
	private float longitude;

	@Column(name="Name")
	private String name;

	@Column(name="Num_of_bids")
	private int num_of_bids;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="Start_time")
	private Date start_time;

	@Column(name="Starting_Bid")
	private float starting_Bid;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="Buyout_user")
	private User user;

	//bi-directional many-to-many association to Category
	@ManyToMany(mappedBy="auctions", cascade = CascadeType.PERSIST)
	private List<Category> categories;

	//bi-directional many-to-many association to Image
	@ManyToMany(mappedBy="auctions")
	private List<Image> images = new ArrayList<>();

	//bi-directional many-to-one association to User_bid_Auction
	@OneToMany(mappedBy="auction")
	private List<User_bid_Auction> userBidAuctions = new ArrayList<>();

	public Auction() {
	}

	public int getAuctionId() {
		return this.auctionId;
	}

	public void setAuctionId(int auctionId) {
		this.auctionId = auctionId;
	}

	public float getBuy_Price() {
		return this.buy_Price;
	}

	public void setBuy_Price(float buy_Price) {
		this.buy_Price = buy_Price;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public User getCreator() {
		return this.creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public float getCurrent_Bid() {
		return this.current_Bid;
	}

	public void setCurrent_Bid(float current_Bid) {
		this.current_Bid = current_Bid;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getExpiration_time() {
		return this.expiration_time;
	}

	public void setExpiration_time(Date expiration_time) {
		this.expiration_time = expiration_time;
	}

	public float getLatitude() {
		return this.latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public float getLongitude() {
		return this.longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNum_of_bids() {
		return this.num_of_bids;
	}

	public void setNum_of_bids(int num_of_bids) {
		this.num_of_bids = num_of_bids;
	}

	public Date getStart_time() {
		return this.start_time;
	}

	public void setStart_time(Date start_time) {
		this.start_time = start_time;
	}

	public float getStarting_Bid() {
		return this.starting_Bid;
	}

	public void setStarting_Bid(float starting_Bid) {
		this.starting_Bid = starting_Bid;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Category> getCategories() {
		return this.categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public List<Image> getImages() {
		return this.images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public List<User_bid_Auction> getUserBidAuctions() {
		return this.userBidAuctions;
	}

	public void setUserBidAuctions(List<User_bid_Auction> userBidAuctions) {
		this.userBidAuctions = userBidAuctions;
	}
	public User_bid_Auction addUserBidAuction(User_bid_Auction userBidAuction) {
		getUserBidAuctions().add(userBidAuction);
		userBidAuction.setAuction(this);

		return userBidAuction;
	}

	public User_bid_Auction removeUserBidAuction(User_bid_Auction userBidAuction) {
		getUserBidAuctions().remove(userBidAuction);
		userBidAuction.setAuction(null);

		return userBidAuction;
	}

}