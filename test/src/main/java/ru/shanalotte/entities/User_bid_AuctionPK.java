package ru.shanalotte.entities;

import java.io.Serializable;
import jakarta.persistence.*;
/**
 * The primary key class for the User_bid_Auction database table.
 * 
 */
@Embeddable
public class User_bid_AuctionPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="User_UserId", insertable=false, updatable=false)
	private String user_UserId;

	@Column(name="Auction_AuctionId", insertable=false, updatable=false)
	private int auction_AuctionId;

	@Column(name="Price")
	private float price;

	public User_bid_AuctionPK() {
	}
	public String getUser_UserId() {
		return this.user_UserId;
	}
	public void setUser_UserId(String user_UserId) {
		this.user_UserId = user_UserId;
	}
	public int getAuction_AuctionId() {
		return this.auction_AuctionId;
	}
	public void setAuction_AuctionId(int auction_AuctionId) {
		this.auction_AuctionId = auction_AuctionId;
	}
	public float getPrice() {
		return this.price;
	}
	public void setPrice(float price) {
		this.price = price;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof User_bid_AuctionPK)) {
			return false;
		}
		User_bid_AuctionPK castOther = (User_bid_AuctionPK)other;
		return 
			this.user_UserId.equals(castOther.user_UserId)
			&& (this.auction_AuctionId == castOther.auction_AuctionId)
			&& (this.price == castOther.price);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.user_UserId.hashCode();
		hash = hash * prime + this.auction_AuctionId;
		hash = hash * prime + Float.floatToIntBits(this.price);
		
		return hash;
	}
}