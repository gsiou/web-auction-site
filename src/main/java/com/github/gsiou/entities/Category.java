package com.github.gsiou.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "Category")
@NamedQueries({
    @NamedQuery(name = "Category.findAll", query = "SELECT c FROM Category c"),
    @NamedQuery(name = "Category.findChildren", query = "SELECT c FROM Category c WHERE c.parent = :parent"),
    @NamedQuery(name = "Category.findRoot", query = "SELECT c FROM Category c WHERE c.parent IS NULL"),
})
public class Category implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "Name")
  @EqualsAndHashCode.Include
  private String name;

  @Column(name = "Parent")
  private String parent;

  @ManyToMany
  @JoinTable(name = "Auction_has_Category",
      joinColumns = {@JoinColumn(name = "Category_Name")},
      inverseJoinColumns = {@JoinColumn(name = "Auction_AuctionId")})

  @ToString.Exclude
  private List<Auction> auctions;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Category category = (Category) o;
    return name != null && Objects.equals(name, category.name);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}