package ru.shanalotte.xmlentities;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "User")
public class UserElem {

	private int rating;
	private String userID;
	
	@XmlAttribute(name = "Rating")
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	
	@XmlAttribute(name = "UserID")
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}

	
}
