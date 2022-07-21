package ru.shanalotte.helper;

public class AuctionFrequency{
	private int frequency;
	private int auctionId;
	
	public AuctionFrequency(int auctionId, int frequency){
		this.frequency = frequency;
		this.auctionId = auctionId;
	}
	
	public int getFrequency() {
		return this.frequency;
	}
	
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	public int getAuctionId(){
		return this.auctionId;
	}
	
	public void setAuctionId(int auctionId){
		this.auctionId = auctionId;
	}
}
