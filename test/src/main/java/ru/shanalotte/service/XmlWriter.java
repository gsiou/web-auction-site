package ru.shanalotte.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import ru.shanalotte.entities.Auction;
import ru.shanalotte.entities.Category;
import ru.shanalotte.entities.UserBid;
import ru.shanalotte.xmlentities.Bid;
import ru.shanalotte.xmlentities.Bidder;
import ru.shanalotte.xmlentities.Bids;
import ru.shanalotte.xmlentities.Item;
import ru.shanalotte.xmlentities.LocationElem;
import ru.shanalotte.xmlentities.UserElem;

@Service
public class XmlWriter {

  public List<Item> auctionsToXml(List<Auction> auctions) {
    List<Item> xmlEntries = new ArrayList<>();
    for (Auction currentAuction : auctions) {
      Item nextAuctionXmlEntry = auctionToXml(currentAuction);
      xmlEntries.add(nextAuctionXmlEntry);
    }
    return xmlEntries;
  }

  private Bidder bidderToXml(UserBid userBid) {
    Bidder currentBidder = new Bidder();
    currentBidder.setCountry(userBid.getUser().getCountry());
    currentBidder.setLocation(userBid.getUser().getAddress());
    currentBidder.setRating((int) userBid.getUser().getBidRating());
    currentBidder.setUserID(userBid.getUser().getUserId());
    return currentBidder;
  }

  private UserElem userToXml(Auction currentAuction) {
    UserElem currentUser;
    currentUser = new UserElem();
    currentUser.setUserID(currentAuction.getCreator().getUserId());
    currentUser.setRating((int) currentAuction.getCreator().getSellRating());
    return currentUser;
  }

  private LocationElem locationToXml(Auction currentAuction) {
    LocationElem currentLoc;
    currentLoc = new LocationElem();
    currentLoc.setLocation(currentAuction.getLocation());
    currentLoc.setLatitude(currentAuction.getLatitude());
    currentLoc.setLongitude(currentAuction.getLongitude());
    return currentLoc;
  }

  private Item auctionToXml(Auction currentAuction) {
    Item xmlEntry = new Item();
    xmlEntry.setName(currentAuction.getName());
    xmlEntry.setDescription(currentAuction.getDescription());
    xmlEntry.setCountry(currentAuction.getCountry());
    xmlEntry.setCurrently("$" + currentAuction.getCurrentBid());
    xmlEntry.setFirst_bid("$" + currentAuction.getStartingBid());
    xmlEntry.setNumber_of_bids(currentAuction.getNumOfBids());
    if (currentAuction.getBuyPrice() != 0) {
      xmlEntry.setBuy_Price("$" + currentAuction.getBuyPrice());
    }
    LocationElem currentLoc = locationToXml(currentAuction);
    xmlEntry.setLocation(currentLoc);
    UserElem currentUser = userToXml(currentAuction);
    xmlEntry.setSeller(currentUser);
    SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yy HH:mm:ss", Locale.ENGLISH);
    if (currentAuction.getStartTime() == null) {
      xmlEntry.setStarted("");
    } else {
      xmlEntry.setStarted(sdf.format(currentAuction.getStartTime()));
    }
    xmlEntry.setEnds(sdf.format(currentAuction.getExpirationTime()));
    List<String> currentCategories = categoriesToXml(currentAuction);
    xmlEntry.setCategories(currentCategories);
    List<Bid> currentBidList = bidsToXml(currentAuction, sdf);
    Bids currentBids = new Bids();
    currentBids.setBids(currentBidList);
    xmlEntry.setBids(currentBids);
    return xmlEntry;
  }

  private List<Bid> bidsToXml(Auction currentAuction, SimpleDateFormat sdf) {
    List<Bid> bidsXml = new ArrayList<>();
    for (UserBid userBid : currentAuction.getUserBidAuctions()) {
      Bidder currentBidder = bidderToXml(userBid);
      Bid currentBid = new Bid();
      currentBid.setAmount("$" + userBid.getId().getPrice());
      currentBid.setTime(sdf.format(userBid.getTime()));
      currentBid.setBidder(currentBidder);
      bidsXml.add(currentBid);
    }
    return bidsXml;
  }

  private List<String> categoriesToXml(Auction currentAuction) {
    List<String> currentCategories = new ArrayList<>();
    List<Category> categoriesCopy = new ArrayList<>(currentAuction.getCategories());
    int categoriesNumber = categoriesCopy.size();
    String prevCategory = "";
    for (int i = 0; i < categoriesNumber; i++) {
      for (Category category : categoriesCopy) {
        if (category.getParent() == null ||
            category.getParent().equals(prevCategory)) {
          currentCategories.add(category.getName());
          prevCategory = category.getName();
          categoriesCopy.remove(category);
          break;
        }
      }
    }
    return currentCategories;
  }

}
