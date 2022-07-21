package ru.shanalotte.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.List;

@Entity
@Table(name="User")
@Getter
@Setter
@ToString
@NoArgsConstructor
@NamedQueries({
	@NamedQuery(name="User.findAll", query="SELECT u FROM User u"),
	@NamedQuery(name="User.findUnactivated", query="SELECT u FROM User u WHERE u.accessLvl = 0"),
	@NamedQuery(name="User.countAll", query="SELECT COUNT(u) FROM User u"),
	@NamedQuery(name="User.countUnactivated", query="SELECT COUNT(u) FROM User u WHERE u.accessLvl = 0"),
	@NamedQuery(name="User.findFrequentBidders", query="SELECT u FROM User u WHERE u <> :user AND (SELECT DISTINCT COUNT(uba) FROM UserBid uba WHERE uba.user = u) > 2")
})
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="UserId")
	private String userId;

	@Column(name="Access_lvl")
	private int accessLvl;

	@Column(name="Address")
	private String address;

	@Column(name="Bid_rating")
	private float bidRating;

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
	private float sellRating;

	@Column(name="Trn")
	private String trn;

	@OneToMany(mappedBy="user")
	private List<Auction> auctions;

	@ToString.Exclude
	@OneToMany(mappedBy= "userFrom")
	private List<Message> messages1;

	@ToString.Exclude
	@OneToMany(mappedBy= "userTo")
	private List<Message> messages2;

	@ToString.Exclude
	@OneToMany(mappedBy="user")
	private List<UserBid> userBidAuctions;

	public void addUserBidAuction(UserBid userBidAuction) {
		getUserBidAuctions().add(userBidAuction);
		userBidAuction.setUser(this);
	}

}