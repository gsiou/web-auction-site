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
    <style>
		.text-pos{
			position:relative;
			left:5%;
		}
		.reg-width{
			width:50%;
		}
		.prev-img-width{
			width:30%;
		}
		.flex-display{
			display:flex;
		}
		.bid-size{
			font-size:110%	
		}	
  	</style>
  <body class="page-background">
	<jsp:include page="/common/header_bar.jsp" />
	<jsp:include page="/common/search_small.jsp" />
	<br>
	<a class="link3 text-pos" href="${pageContext.request.contextPath}/AuctionView?auctionID=${param.auctionID}&page=view">Back to Auction</a>    
    <p class="Gtext text-pos">Bid History</p>
    <div id='success-message'>${sessionScope.bid_response}</div>
  	<c:remove var="bid_response"/>
    
    <div class="flex-display">
    	
	    <div class="reg-table reg-width">
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
		
		<div class="reg-table prev-img-width">
	    	<p><b>${name}</b></p>
	    	<c:choose>
				<c:when test="${not empty auction.images[0].url}">
					<img width=50%; height=auto; src="${pageContext.request.contextPath}/image/${images[0].url}" />
				</c:when>
				<c:otherwise>
					<img width=50%; height=auto; src="default_img.png"/>
				</c:otherwise>
			</c:choose>
	    	
	    	<div class="bid-size">Current Bid:${current_bid}$</div><br>
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