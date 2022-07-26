
package ru.shanalotte.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="Auction")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NamedQueries({
	@NamedQuery(name="Auction.findAll", query="SELECT a FROM Auction a"),
	@NamedQuery(name="Auction.findInactiveOfUser",
		query="SELECT a FROM Auction a WHERE a.creator = :user AND a.startTime IS NULL"),
	@NamedQuery(name="Auction.findActiveOfUser",
		query="SELECT a FROM Auction a WHERE a.creator = :user AND "
				+ "a.expirationTime >= :date AND a.user IS NULL AND a.startTime IS NOT NULL"),
	@NamedQuery(name="Auction.findSoldOfUser",
		query="SELECT a FROM Auction a WHERE a.creator = :user AND"
				+ "(a.user IS NOT NULL OR a.expirationTime < :date)"),
	@NamedQuery(name="Auction.findUserBiddedAuctions",
		query="SELECT DISTINCT(a) FROM Auction a, UserBid u WHERE u.user = :user AND u.auction=a "
				+ "AND a.expirationTime > :date AND a.user IS NULL"),
	@NamedQuery(name="Auction.findUserWonAuctions",
		query="SELECT DISTINCT(a) FROM Auction a, UserBid u WHERE (u.user = :user AND u.auction=a AND "
				+ "a.expirationTime < :date AND u.id.price = a.currentBid) OR (a.user = :user)"),
	@NamedQuery(name="Auction.findUserLostAuctions",
		query="SELECT DISTINCT(a) FROM Auction a, UserBid u WHERE (u.user = :user AND u.auction=a AND "
				+ "a.expirationTime < :date AND u.id.price <> a.currentBid) AND (a.user IS NULL OR a.user <> :user)"),
	@NamedQuery(name="Auction.findAuctionBids",
		query="SELECT u FROM Auction a, UserBid u WHERE u.auction = :auction AND u.auction=a "
				+ " ORDER BY u.time DESC"),
	@NamedQuery(name="Auction.findUserUniqueBids",
		query="SELECT DISTINCT(uba.auction) FROM UserBid uba WHERE uba.user = :user"),
	@NamedQuery(name="Auction.findUserUniqueActiveBids",
		query="SELECT DISTINCT(uba.auction) FROM UserBid uba WHERE uba.user = :user "
			+ "AND uba.auction.expirationTime >= :date AND uba.auction.user IS NULL AND uba.auction.startTime IS NOT NULL"),
	@NamedQuery(name="Auction.findPopular", 
		query="SELECT a FROM Auction a WHERE "
				+ "a.expirationTime >= :date AND a.user IS NULL AND a.startTime IS NOT NULL "
				+ "ORDER BY a.numOfBids DESC")
})
public class Auction implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name="AuctionId" ,insertable = false, updatable = false)
	private int auctionId;

	@Column(name="Buy_Price")
	private float buyPrice;

	@Column(name="Country")
	private String country;

	@JoinColumn(name="Creator")
	@ManyToOne(cascade = CascadeType.PERSIST)
	private User creator;

	@Column(name="Current_Bid")
	private float currentBid;

	@Lob
	@Column(name="Description")
	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="Expiration_time")
	private Date expirationTime;

	@Column(name="Latitude")
	private float latitude;

	@Column(name="Location")
	private String location;

	@Column(name="Longitude")
	private float longitude;

	@Column(name="Name")
	private String name;

	@Column(name="Num_of_bids")
	private int numOfBids;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="Start_time")
	private Date startTime;

	@Column(name="Starting_Bid")
	private float startingBid;

	@ManyToOne
	@JoinColumn(name="Buyout_user")
	private User user;

	@ManyToMany(mappedBy="auctions", cascade = CascadeType.PERSIST)
	@ToString.Exclude
	private List<Category> categories;

	@ManyToMany(mappedBy="auctions")
	@ToString.Exclude
	private List<Image> images = new ArrayList<>();

	@OneToMany(mappedBy="auction")
	@ToString.Exclude
	private List<UserBid> userBidAuctions = new ArrayList<>();

	public void addBid(UserBid userBidAuction) {
		userBidAuctions.add(userBidAuction);
		userBidAuction.setAuction(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Auction auction = (Auction) o;
		return auctionId == auction.auctionId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(auctionId);
	}
}