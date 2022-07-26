package ru.shanalotte.xmlentities;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Bids")
public class Bids {
	private List<Bid> bids;
	@XmlElement(name = "Bid")
	public List<Bid> getBids(){
		return this.bids;
	}
	public void setBids(List<Bid> bids){
		this.bids = bids;
	}
}
