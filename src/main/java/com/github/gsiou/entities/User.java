package com.github.gsiou.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import com.github.gsiou.dto.RegistrationRequestData;
import com.github.gsiou.utils.HelperFunctions;
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

	@ToString.Exclude
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

  public static User fromDto(RegistrationRequestData registrationData) {
		String hashedPassword = HelperFunctions.hash(registrationData.getPassword1());
		User user = new User();
		user.setUserId(registrationData.getUserid());
		user.setPassword(hashedPassword);
		user.setBidRating(0);
		user.setSellRating(0);
		user.setCountry(registrationData.getCountry());
		user.setAddress(registrationData.getCountry());
		user.setPhone(registrationData.getPhone());
		user.setEmail(registrationData.getEmail());
		user.setTrn(registrationData.getTrn());
		if (!registrationData.getLatitude().equals("") && !registrationData.getLongitude().equals("")) {
			user.setLatitude(Float.parseFloat(registrationData.getLatitude()));
			user.setLongitude(Float.parseFloat(registrationData.getLongitude()));
		}
		return user;
  }

  public void addUserBidAuction(UserBid userBidAuction) {
		getUserBidAuctions().add(userBidAuction);
		userBidAuction.setUser(this);
	}

}