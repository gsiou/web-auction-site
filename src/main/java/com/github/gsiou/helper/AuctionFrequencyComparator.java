package com.github.gsiou.helper;

import java.util.Comparator;

public class AuctionFrequencyComparator implements Comparator<AuctionFrequency>{

	@Override
	public int compare(AuctionFrequency af0, AuctionFrequency af1) {
		return Integer.compare(af0.getAuctionId(), af1.getAuctionId());
	}
	
}