package com.github.gsiou.entities;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserBidPK implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="User_UserId", insertable=false, updatable=false)
	private String userId;

	@Column(name="Auction_AuctionId", insertable=false, updatable=false)
	private int auctionId;

	@Column(name="Price")
	private float price;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UserBidPK that = (UserBidPK) o;
		return auctionId == that.auctionId && Objects.equals(userId, that.userId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId, auctionId);
	}
}