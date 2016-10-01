<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
  <head>
	<jsp:include page="/common/common.jsp" />
    <script type="text/javascript"> 
    var cur_bid = <%=request.getAttribute("current_bid")%>;
	var buy_price = <%=request.getAttribute("buy_price")%>;</script>
    <script type="text/javascript" src="res/bid_validate.js"> </script>
    <script type="text/javascript" src="res/fade_out_text.js"> </script>
    <title>${name}|Hammer Deals</title>
  </head>
  <body class="page-background">
	<jsp:include page="/common/header_bar.jsp" />
	<jsp:include page="/common/search_small.jsp" />
	<br>
	<a style="position:relative;left:5%;" href="${pageContext.request.contextPath}/AuctionView?auctionID=${param.auctionID}&page=view">Back to Auction</a>    
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
	    	<img width=50%; height=auto; src="image/${image}" ><br><br>
	    	
	    	<div style="font-size:110%">Current Bid:${current_bid}$</div><br>
	    	<div id='pop-up-message'></div>
	    	<c:choose>
    			<c:when test="${buy_out == false and expired==false and sessionScope.userID != null}">
			    	<form action="AuctionView?auctionID=${param.auctionID}" method="post" onsubmit="return validateBid()">
			    		<input type="hidden" name="action" value="bidAuction"> 
			    		<input type="text" id='Bid_amount' name="Bid_input" class="bid-input" placeholder="Place Bid">
		    			<input type='submit' id='orange_button' value='Place Bid'>
		    		</form>
		    	</c:when>
			</c:choose>		    		
		</div>
	
	</div>

   </body>
</html>