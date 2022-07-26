package com.github.gsiou.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="user_bid_auction")
@NamedQuery(name="UserBid.findAll", query="SELECT u FROM UserBid u")
public class UserBid implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private UserBidPK id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="Time")
	private Date time;

	@ManyToOne
	@JoinColumn(name="Auction_AuctionId", insertable = false, updatable = false)
	private Auction auction;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name="User_UserId", insertable = false, updatable = false)
	private User user;




}