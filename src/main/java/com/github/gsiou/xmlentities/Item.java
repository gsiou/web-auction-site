package com.github.gsiou.xmlentities;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class Item {
	private String name;
	private List<String> categories;
	private String currently;
	private String first_bid;
	private String buy_price;
	private int number_of_bids;
	private LocationElem location;
	private String country;
	private String started;
	private String ends;
	private UserElem seller;
	private String description;
	private Bids bids;
	
	@XmlElement(name="Name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name="Category")
	public List<String> getCategories() {
		return categories;
	}
	public void setCategories(List<String> categories) {
		this.categories = categories;
	}
	
	@XmlElement(name="Currently")
	public String getCurrently() {
		return currently;
	}
	public void setCurrently(String currently) {
		this.currently = currently;
	}
	
	@XmlElement(name="Buy_Price")
	public String getBuy_Price(){
		return this.buy_price;
	}
	public void setBuy_Price(String buy_price){
		this.buy_price = buy_price;
	}
	
	@XmlElement(name="First_Bid")
	public String getFirst_bid() {
		return first_bid;
	}
	public void setFirst_bid(String first_bid) {
		this.first_bid = first_bid;
	}
	public int getNumber_of_bids() {
		return number_of_bids;
	}
	
	@XmlElement(name="Number_of_Bids")
	public void setNumber_of_bids(int number_of_bids) {
		this.number_of_bids = number_of_bids;
	}
	
	@XmlElement(name="Location")
	public LocationElem getLocation() {
		return location;
	}
	public void setLocation(LocationElem location) {
		this.location = location;
	}
	
	@XmlElement(name="Country")
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
	@XmlElement(name="Started")
	public String getStarted() {
		return started;
	}
	public void setStarted(String started) {
		this.started = started;
	}
	
	@XmlElement(name="Ends")
	public String getEnds() {
		return ends;
	}
	public void setEnds(String ends) {
		this.ends = ends;
	}
	
	@XmlElement(name="Seller")
	public UserElem getSeller() {
		return seller;
	}
	public void setSeller(UserElem seller) {
		this.seller = seller;
	}
	
	@XmlElement(name="Description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@XmlElement(name="Bids")
	public Bids getBids(){
		return this.bids;
	}
	public void setBids(Bids bids){
		this.bids = bids;
	}
}
