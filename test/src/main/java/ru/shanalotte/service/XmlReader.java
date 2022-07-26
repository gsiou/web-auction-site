package ru.shanalotte.service;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.shanalotte.dao.AuctionDAO;
import ru.shanalotte.dao.CategoryDAO;
import ru.shanalotte.dao.UserBidDAO;
import ru.shanalotte.dao.UserDAO;
import ru.shanalotte.entities.Auction;
import ru.shanalotte.entities.Category;
import ru.shanalotte.entities.User;
import ru.shanalotte.xmlentities.Bid;
import ru.shanalotte.xmlentities.Item;
import ru.shanalotte.xmlentities.Items;

@Service
public class XmlReader {

  private final UserDAO userDAO;
  private final AuctionDAO auctionDAO;
  private final CategoryDAO categoryDAO;
  private final UserBidDAO userBidDAO;

  @Autowired
  public XmlReader(UserDAO userDAO, AuctionDAO auctionDAO, CategoryDAO categoryDAO, UserBidDAO userBidDAO) {
    this.userDAO = userDAO;
    this.auctionDAO = auctionDAO;
    this.categoryDAO = categoryDAO;
    this.userBidDAO = userBidDAO;
  }

  public Optional<Items> unmarshalXmlData(HttpServletRequest request) {
    try {
      JAXBContext jc = JAXBContext.newInstance(Items.class);
      Unmarshaller unmarshaller = jc.createUnmarshaller();
      Part filePart = request.getPart("file");
      InputStream content = filePart.getInputStream();
      return Optional.ofNullable((Items) unmarshaller.unmarshal(content));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private Auction importAuctionFromXml(Item xmlEntry) {
    Auction auction = new Auction();
    auction.setName(xmlEntry.getName());
    auction.setDescription(xmlEntry.getDescription());
    auction.setCountry(xmlEntry.getCountry());
    auction.setLocation(xmlEntry.getLocation().getLocation());
    auction.setLatitude(xmlEntry.getLocation().getLatitude());
    auction.setLongitude(xmlEntry.getLocation().getLongitude());
    auction.setNumOfBids(xmlEntry.getNumber_of_bids());
    try {
      auction.setStartingBid(Float.parseFloat(xmlEntry.getFirst_bid().substring(1)));
    } catch (NumberFormatException ex) {
      auction.setStartingBid(0);
    }
    if (xmlEntry.getBuy_Price() != null) {
      try {
        auction.setBuyPrice(Float.parseFloat(xmlEntry.getBuy_Price().substring(1)));
      } catch (NumberFormatException ex) {
        auction.setBuyPrice(0);
      }
    }
    try {
      auction.setCurrentBid(Float.parseFloat(xmlEntry.getCurrently().substring(1)));
    } catch (NumberFormatException ex) {
      auction.setCurrentBid(0);
    }
    SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yy HH:mm:ss", Locale.ENGLISH);
    try {
      auction.setStartTime(sdf.parse(xmlEntry.getStarted()));
    } catch (ParseException e) {
      auction.setStartTime(null);
    }
    try {
      auction.setExpirationTime(sdf.parse("Jan-01-17 23:30:01"));
    } catch (ParseException e) {
      auction.setExpirationTime(null);
    }
    return auction;
  }

  public void importXmlEntry(Item xmlEntry) {
    Auction auction = importAuctionFromXml(xmlEntry);
    List<Category> categories = importCategories(xmlEntry, auction);
    auction.setCategories(categories);
    User creator = importAuctionCreator(xmlEntry);
    auction.setCreator(creator);
    auctionDAO.create(auction);
    if (xmlEntry.getBids().getBids() != null) {
      importAuctionBids(xmlEntry, auction);
    }
  }

  private void importAuctionBids(Item xmlEntry, Auction auction) {
    for (Bid b : xmlEntry.getBids().getBids()) {
      User bidder = userDAO.findByID(b.getBidder().getUserID());
      if (bidder == null) {
        bidder = new User();
        bidder.setUserId(b.getBidder().getUserID());
        bidder.setPassword("");
        bidder.setAccessLvl(1);
        bidder.setEmail("");
      }
      bidder.setCountry(b.getBidder().getCountry());
      bidder.setAddress(b.getBidder().getLocation());
      bidder.setBidRating(b.getBidder().getRating());
      float price;
      try {
        price = Float.parseFloat(b.getAmount().substring(1));
      } catch (NumberFormatException ex) {
        price = 0;
      }
      SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yy HH:mm:ss", Locale.ENGLISH);
      Date time;
      try {
        time = sdf.parse(xmlEntry.getStarted());
      } catch (ParseException e) {
        time = null;
      }
      userBidDAO.create(bidder, auction, time, price);
    }
  }

  private User importAuctionCreator(Item nextXmlEntry) {
    User creator = userDAO.findByID(nextXmlEntry.getSeller().getUserID());
    if (creator == null) {
      creator = new User();
      creator.setUserId(nextXmlEntry.getSeller().getUserID());
      creator.setAccessLvl(1);
      creator.setPassword("");
      creator.setEmail("");
    }
    creator.setCountry(nextXmlEntry.getCountry());
    creator.setSellRating(nextXmlEntry.getSeller().getRating());
    if (nextXmlEntry.getLocation().getLatitude() != 0 && nextXmlEntry.getLocation().getLatitude() != 0) {
      creator.setLatitude(nextXmlEntry.getLocation().getLatitude());
      creator.setLongitude(nextXmlEntry.getLocation().getLongitude());
    }
    return creator;
  }

  private List<Category> importCategories(Item nextXmlEntry, Auction auc) {
    String parentCategory = null;
    List<Category> categories = new ArrayList<>();
    if (nextXmlEntry.getCategories() == null) {
      return new ArrayList<>();
    }
    for (String nextCategory : nextXmlEntry.getCategories()) {
      Category category = categoryDAO.find(nextCategory);
      if (category == null) {
        Category placeholder = new Category();
        placeholder.setName(nextCategory);
        placeholder.setParent(parentCategory);
        if (categories.contains(placeholder)) {
          category = new Category();
          category.setName(nextCategory + " (" + parentCategory + ")");
          category.setParent(parentCategory);
          category.setAuctions(new ArrayList<Auction>());
        } else {
          category = new Category();
          category.setName(nextCategory);
          category.setParent(parentCategory);
          category.setAuctions(new ArrayList<Auction>());
        }
      }
      category.getAuctions().add(auc);
      categories.add(category);
      parentCategory = category.getName();
    }
    return categories;
  }
}
