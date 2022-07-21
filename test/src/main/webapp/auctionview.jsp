<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
  <head>
	<jsp:include page="/common/common.jsp" />
    <script type="text/javascript"> 
    var latitude = <%=request.getAttribute("latitude")%>;
	var longitude = <%=request.getAttribute("longitude")%>;
	var cur_bid = <%=request.getAttribute("current_bid")%>;
	var buy_price = <%=request.getAttribute("buy_price")%>;</script>
	<script type="text/javascript" src="res/get_map_location.js"></script>
    <script type="text/javascript" src="res/jssor.slider.min.js"></script>
    <script type="text/javascript" src="res/ImageSlider.js"> </script>
    <script type="text/javascript" src="res/bid_validate.js"> </script>
    <title>${name}|Hammer Deals</title>
  </head>
  <style>
	.categories-text{
		text-align:center;
		font-size: 80%;
	}
	.view-flex-boxes{
		display:flex;
		flex-wrap: wrap;
		width: 100%;
		justify-content: center;
	}
	.desc-size{
		width:100%;
		height:75%;
	}
  </style>
  <body class="page-background">
	<jsp:include page="/common/header_bar.jsp" />
	<jsp:include page="/common/search_small.jsp" />         
   	<div class="categories-text">
	 	<p>Listed categories:
	    <c:forEach items="${categories}" var="category">
	    	>${category}
	    </c:forEach></p>
	</div>
	    
	<div class="auction-name">
	    <p>${name}</p>
	</div>
	
	<div class="Gtext-small">
	    <p>Creator:<c:out value="${creator.userId}"/> (${creator.sell_rating})</p>
	</div>
    
    <div class="view-flex-boxes">
    	
	<div id="slider_container" class="slider_container">
        <div data-u="slides" class="slides">
        	<c:choose>
				<c:when test="${not empty imageList}">
					<c:forEach items="${imageList}" var="image">
    					<div><img data-u="image" src="image/${image}" ></div>
    				</c:forEach>
				</c:when>
				<c:otherwise>
					<div><img data-u="image" src="default_img.png"/></div>
				</c:otherwise>
			</c:choose>
    	</div>
        <div data-u="navigator" class="jssorb21" style="bottom: 16px; right: 6px;">
            <div data-u="prototype"></div>
        </div>
        <script>
            init_jssor_slider("slider_container");
        </script> 
    </div>
    
    <div class="view-table">
    	<p>Start Time:${start_time}</p>
    	<p>Expiration Time:${expiration_time}</p>
    	<c:choose>
    		<c:when test="${buy_price == 0}">
    			<p>Buy Price:(-) Bid-only</p>
    		</c:when>
    		<c:otherwise>
    			<c:choose>
    				<c:when test="${buy_out == false and expired==false and sessionScope.userID != null}">
    					<form action="AuctionView?auctionID=${param.auctionID}" method="post">
    						<input type="hidden" name="action" value="buyout"> 
    						<p> Buy Price:${buy_price}$ <input type='submit' id='orange_button' value='Buy item'></p>
    					</form> 
    				</c:when>
    				<c:otherwise>
    		  			<p> Buy Price:${buy_price}$</p>
    		  		</c:otherwise>
    		  	</c:choose>
    		</c:otherwise>    		
		</c:choose>
    	<p>Starting Price:${starting_bid}$</p>
    	<form action="bid-history">    
      	<input type="hidden" name="action" value="bid_history" />
      	<p>Current Bid:${current_bid}$ [<a href="AuctionView?auctionID=${param.auctionID}&page=history">${num_of_bids} bids</a>]</p> 
  		</form>
  		<div id='pop-up-message'>${sessionScope.bid_response}</div>
  		<c:remove var="bid_response"/>
  		<c:choose>
    		<c:when test="${buy_out == false and expired==false and sessionScope.userID != null}">
		  		<form action="AuctionView?auctionID=${param.auctionID}" method="post" onsubmit="return validateBid()">
		  			<input type="hidden" name="action" value="bidAuction"> 
		    		<input type="text" id='Bid_amount' name="Bid_input" class="bid-input">
		    		<input type='submit' id='orange_button' value='Place Bid'>
		    	</form>
		    </c:when>
		</c:choose>
    	<p>Location:<c:out value="${location}"/></p>
    	<p>Country:<c:out value="${country}"/></p>
    </div>
    
    <div class="desc-table" >
	<p>Description:</p>
    	<textarea readonly name="description" class="textbox-register desc-size"><c:out value="${description}" /></textarea>
    </div>
    
    <div id="view-map" ></div>
	<script async defer
		src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAeUHWIAbJ6ik1KM6PcqEHPM0uCWYF1cfM&callback=initMap">
	</script>
	
	</div>
    
  </body>
</html> 