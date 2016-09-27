package entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the Image database table.
 * 
 */
@Entity
@Table(name="Image")
@NamedQueries({
	@NamedQuery(name="Image.findAll", query="SELECT i FROM Image i"),
	@NamedQuery(name="Image.findImagesofAuction", query="SELECT i FROM Image i WHERE :auction MEMBER OF i.auctions "),
})
public class Image implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String url;

	//bi-directional many-to-many association to Auction
	@ManyToMany
	@JoinTable(
		name="Auction_has_Image"
		, joinColumns={
			@JoinColumn(name="table1_url")
			}
		, inverseJoinColumns={
			@JoinColumn(name="Auction_AuctionId")
			}
		)
	private List<Auction> auctions;

	public Image() {
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Auction> getAuctions() {
		return this.auctions;
	}

	public void setAuctions(List<Auction> auctions) {
		this.auctions = auctions;
	}

}