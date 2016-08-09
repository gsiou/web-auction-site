package entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the Auction database table.
 * 
 */
@Entity
@Table(name="Auction")
@NamedQuery(name="Auction.findAll", query="SELECT a FROM Auction a")
public class Auction implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="AuctionId")
	private int auctionId;

	@Column(name="Buy_Price")
	private float buy_Price;

	@Column(name="Country")
	private String country;

	@Column(name="Current_Bid")
	private float current_Bid;

	@Lob
	@Column(name="Description")
	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="Expiration_time")
	private Date expiration_time;

	@Column(name="Image")
	private String image;

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

	//bi-directional many-to-many association to Category
	@ManyToMany(mappedBy="auctions")
	private List<Category> categories;

	//bi-directional many-to-many association to User
	@ManyToMany(mappedBy="auctions")
	private List<User> users;

	//bi-directional many-to-one association to User_bid_Auction
	@OneToMany(mappedBy="auction")
	private List<User_bid_Auction> userBidAuctions;

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

	public String getImage() {
		return this.image;
	}

	public void setImage(String image) {
		this.image = image;
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

	public List<Category> getCategories() {
		return this.categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public List<User> getUsers() {
		return this.users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
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