package ru.shanalotte.entities;

import java.io.Serializable;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.*;


/**
 * The persistent class for the Category database table.
 * 
 */
@Entity
@Table(name="Category")
@NamedQueries({
	@NamedQuery(name="Category.findAll", query="SELECT c FROM Category c"),
	@NamedQuery(name="Category.findChildren", query="SELECT c FROM Category c WHERE c.parent = :parent"),
	@NamedQuery(name="Category.findRoot", query="SELECT c FROM Category c WHERE c.parent IS NULL"),
})
public class Category implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="Name")
	private String name;

	@Column(name="Parent")
	private String parent;

	//bi-directional many-to-many association to Auction
	@ManyToMany
	@JoinTable(
		name="Auction_has_Category"
		, joinColumns={
			@JoinColumn(name="Category_Name")
			}
		, inverseJoinColumns={
			@JoinColumn(name="Auction_AuctionId")
			}
		)
	private List<Auction> auctions;

	public Category() {
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParent() {
		return this.parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public List<Auction> getAuctions() {
		return this.auctions;
	}

	public void setAuctions(List<Auction> auctions) {
		this.auctions = auctions;
	}
	
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof Category)) {
			return false;
		}
		Category castOther = (Category)other;
		return 
			this.name.equals(castOther.name);
	}
	
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.name.hashCode();
		
		return hash;
	}

}