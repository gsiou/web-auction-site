package entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the User database table.
 * 
 */
@Entity
@Table(name="User")
@NamedQuery(name="User.findAll", query="SELECT u FROM User u")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="UserId")
	private String userId;

	@Column(name="Access_lvl")
	private int access_lvl;

	@Column(name="Address")
	private String address;

	@Column(name="Bid_rating")
	private float bid_rating;

	@Column(name="Country")
	private String country;

	@Column(name="Email")
	private String email;

	@Column(name="Latitude")
	private float latitude;

	@Column(name="Longitude")
	private float longitude;

	@Column(name="Password")
	private String password;

	@Column(name="Phone")
	private String phone;

	@Column(name="Sell_rating")
	private float sell_rating;

	@Column(name="Trn")
	private String trn;

	//bi-directional many-to-one association to Message
	@OneToMany(mappedBy="user_from")
	private List<Message> messages1;

	//bi-directional many-to-one association to Message
	@OneToMany(mappedBy="user_to")
	private List<Message> messages2;

	//bi-directional many-to-many association to Auction
	@ManyToMany
	@JoinTable(
		name="User_has_Auction"
		, joinColumns={
			@JoinColumn(name="User_UserId")
			}
		, inverseJoinColumns={
			@JoinColumn(name="Auction_AuctionId")
			}
		)
	private List<Auction> auctions;

	//bi-directional many-to-one association to User_bid_Auction
	@OneToMany(mappedBy="user")
	private List<User_bid_Auction> userBidAuctions;

	public User() {
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getAccess_lvl() {
		return this.access_lvl;
	}

	public void setAccess_lvl(int access_lvl) {
		this.access_lvl = access_lvl;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public float getBid_rating() {
		return this.bid_rating;
	}

	public void setBid_rating(float bid_rating) {
		this.bid_rating = bid_rating;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public float getLatitude() {
		return this.latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return this.longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public float getSell_rating() {
		return this.sell_rating;
	}

	public void setSell_rating(float sell_rating) {
		this.sell_rating = sell_rating;
	}

	public String getTrn() {
		return this.trn;
	}

	public void setTrn(String trn) {
		this.trn = trn;
	}

	public List<Message> getMessages1() {
		return this.messages1;
	}

	public void setMessages1(List<Message> messages1) {
		this.messages1 = messages1;
	}

	public Message addMessages1(Message messages1) {
		getMessages1().add(messages1);
		messages1.setUser_from(this);

		return messages1;
	}

	public Message removeMessages1(Message messages1) {
		getMessages1().remove(messages1);
		messages1.setUser_from(null);

		return messages1;
	}

	public List<Message> getMessages2() {
		return this.messages2;
	}

	public void setMessages2(List<Message> messages2) {
		this.messages2 = messages2;
	}

	public Message addMessages2(Message messages2) {
		getMessages2().add(messages2);
		messages2.setUser_to(this);

		return messages2;
	}

	public Message removeMessages2(Message messages2) {
		getMessages2().remove(messages2);
		messages2.setUser_to(null);

		return messages2;
	}

	public List<Auction> getAuctions() {
		return this.auctions;
	}

	public void setAuctions(List<Auction> auctions) {
		this.auctions = auctions;
	}

	public List<User_bid_Auction> getUserBidAuctions() {
		return this.userBidAuctions;
	}

	public void setUserBidAuctions(List<User_bid_Auction> userBidAuctions) {
		this.userBidAuctions = userBidAuctions;
	}

	public User_bid_Auction addUserBidAuction(User_bid_Auction userBidAuction) {
		getUserBidAuctions().add(userBidAuction);
		userBidAuction.setUser(this);

		return userBidAuction;
	}

	public User_bid_Auction removeUserBidAuction(User_bid_Auction userBidAuction) {
		getUserBidAuctions().remove(userBidAuction);
		userBidAuction.setUser(null);

		return userBidAuction;
	}

}