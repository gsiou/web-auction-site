<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
  <head>
    <link rel="stylesheet" type="text/css" href="res/styles.css">
    <link rel="icon" href="res/favicon.ico" type="image/x-icon" />
    <script src="jquery-3.1.0.min.js"></script>
    <script type="text/javascript"> 
    var cur_bid = <%=request.getAttribute("current_bid")%>;
	var buy_price = <%=request.getAttribute("buy_price")%>;</script>
    <script type="text/javascript" src="res/bid_validate.js"> </script>
    <script type="text/javascript" src="res/fade_out_text.js"> </script>
    <title>${name}|Hammer Deals</title>
  </head>
  <body class="page-background">
	<header class="menubar">
    	<c:choose>
    		<c:when test="${sessionScope.userID == null}">
      			<a href="Login">Login</a> |
      			<a href="Registration">Register</a>
      		</c:when>
      		<c:otherwise>
      			Logged in as <strong>${sessionScope.userID}</strong> |
      			<a href="/Messages">Messages()</a> |
      			<a href="/Logout">Log Out</a>
      		</c:otherwise>
      	</c:choose>
    </header>
    <div id="uni-search">
      <form>
      	<a href="index.jsp" title="Hammer Deals"><img class="tiny-logo-left" src="logo.png" ></a>
        <div class="small-txtbtncontainer">
          <input type="text" placeholder="Search..." class="textbox-search" name="search_terms" />
          <input type="submit" class="button-search" value="Search"/>
          <select name="category" class="select-search">
            <option value="all">All Categories</option>
            <option value="tech">Technology</option>
            <option value="clothes">Clothing</option>
          </select>
        </div>
      </form>
    </div>
    
    <p class="Gtext" style="position:relative;left:5%;">Bid History</p>
    <div id='success-message'>${sessionScope.bid_response}</div>
  	<c:remove var="bid_response"/>
    
    <div style="display:flex;">
    	
	    <div class="reg-table" style="width:50%;">
		    <table class="user-table">
		    	<tr><th>UserID</th><th>Bid</th><th>Time</th></tr>
		    	<c:forEach items="${user_biddings}" var="bidding">
		    		<tr class="activated">
							<td>${bidding.user.userId}(${bidding.user.bid_rating})</td>
							<td>${bidding.price}$</td>
							<td>${bidding.time}</td>
					</tr>
				</c:forEach>
					<tr class="activated">
							<td><b>Starting Price</b></td>
							<td>${starting_bid}$</td>
							<td>${start_time}</td>
					</tr>
			</table>
		</div>
		
		<div class="reg-table" style="width:30%;">
	    	<p><b>${name}</b></p>
	    	<img width=50%; height=40%; src="image/${image}" ><br><br>
	    	
	    	<div style="font-size:110%">Current Bid:${current_bid}$</div><br>
	    	<div id='pop-up-message'></div>
	    	<form action="AuctionView?auctionID=${param.auctionID}" method="post" onsubmit="return validateBid()">
	    		<input type="number" id='Bid_amount' name="Bid_input" class="bid-input" placeholder="Place Bid">
    			<input type='submit' id='orange_button' value='Place Bid'>
    		</form>
		</div>
	
	</div>

   </body>
</html>