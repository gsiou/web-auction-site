package ru.shanalotte.helper;

import java.util.Comparator;

public class AuctionFrequencyComparator implements Comparator<AuctionFrequency>{

	@Override
	public int compare(AuctionFrequency af0, AuctionFrequency af1) {
		return new Integer(af0.getAuctionId()).compareTo(new Integer(af1.getAuctionId()));
	}
	
}