package entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the Category database table.
 * 
 */
@Entity
@NamedQuery(name="Category.findAll", query="SELECT c FROM Category c")
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
			@JoinColumn(name="Auctions_ItemId")
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

}