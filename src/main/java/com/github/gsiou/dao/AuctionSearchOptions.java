package com.github.gsiou.dao;

import java.util.Date;

public class AuctionSearchOptions {
	private boolean price_min_search = false;
	private boolean price_max_search = false;
	private float price_min = 0;
	private float price_max = 0;
	private boolean location_search = false;
	private String location = "";
	private boolean description_search = false;
	private String description = "";
	private boolean category_search = false;
	private String category = "";
	private Date min_date = null;
	
	public AuctionSearchOptions(Date min_date){
		this.min_date = min_date;
	}
	
	public Date getMinDate(){
		return this.min_date;
	}
	
	public void setMinPrice(float min){
		this.price_min_search = true;
		this.price_min = min;
	}
	
	public void setMaxPrice(float max){
		this.price_max_search = true;
		this.price_max = max;
	}
	
	public boolean hasMinPrice(){
		return this.price_min_search;
	}
	
	public boolean hasMaxPrice(){
		return this.price_max_search;
	}
	
	public float getMinPrice(){
		return this.price_min;
	}
	
	public float getMaxPrice(){
		return this.price_max;
	}
	
	public void setLocation(String location){
		this.location_search = true;
		this.location = location;
	}
	
	public boolean hasLocation(){
		return this.location_search;
	}
	
	public String getLocation(){
		return this.location;
	}
	
	public void setDescription(String description){
		this.description_search = true;
		this.description = description;
	}
	
	public boolean hasDescription(){
		return this.description_search;
	}
	
	public String getDescription(){
		return this.description;
	}
	
	public void setCategory(String category){
		this.category_search = true;
		this.category = category;
	}
	
	public boolean hasCategory(){
		return this.category_search;
	}
	
	public String getCategory(){
		return this.category;
	}
}
