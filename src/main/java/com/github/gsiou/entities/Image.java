package com.github.gsiou.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.List;

@Entity
@Table(name = "Image")
@Getter
@Setter
@ToString
@NoArgsConstructor
@NamedQueries({
    @NamedQuery(name = "Image.findAll", query = "SELECT i FROM Image i"),
    @NamedQuery(name = "Image.findImagesofAuction", query = "SELECT i FROM Image i WHERE :auction MEMBER OF i.auctions "),
})
public class Image implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  private String url;

  @ManyToMany
  @JoinTable(name = "Auction_has_Image",
      joinColumns = {@JoinColumn(name = "table1_url")},
      inverseJoinColumns = {@JoinColumn(name = "Auction_AuctionId")
      }
  )
  @ToString.Exclude
  private List<Auction> auctions;

}