## Web Auction Site

Auction (Ebay-like) Website Project (Web Applications Technology Course, DIT @ UoA): Created a functional website (front & backend) for trading items via auction. Implemented bidding, buying, messaging, searching and submitting auctions. Used Nearest Neighbour Collaborative Filtering to recommend items to users.

Team: 
Georgios Anastasiou (https://github.com/gsiou)
Athanasios Avgetidis (https://github.com/ThanosAvg)

### Fork changelog

- Add Maven
- Add Spring core
- Local project deploy via Maven Jetty plugin (to run execute `mvn clean jetty:run`)
- Replace some boilerplate code with Lombok annotations (e.g entities)
- Rename some fields
- Rename base package to com.github.gsiou
- Refactor AuctionSubmit.java to some extent
- Refactor IndexServlet to some point
- Refactor MessageServlet
- Refactor UserLoginServlet to some point
- Remove local persistence.xml file
- Refactor XML import process in AdminServlet.java
- Refactor XML export process in AdminServlet.java
- Refactor user registration process