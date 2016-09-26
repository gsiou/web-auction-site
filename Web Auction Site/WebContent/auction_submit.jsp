<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
	<head>
	<jsp:include page="/common/common.jsp" />
    <script type="text/javascript" src="res/maps.js"> </script> 
    <script type="text/javascript" src="res/category_fetch.js"> </script> 
    <script type="text/javascript" src="res/jquery.validate.min.js"></script>
    <script type="text/javascript" src="res/auction_submit_validate.js"></script>
	<title>Submit an Auction|Hammer Deals</title>
	<style>
		#category_list{
			//background-color: white;
			border-width: 1px;
			width: 50%;
			margin-left: auto;
			margin-right: auto;
		}
		.button-category {
		    width: 100%;
		    background-color: orange;
		    color: white;
		    margin-top: 1%;
		    border: none;
		    cursor: pointer;
		    font-size: 20px;
		}
	</style>
</head>
<body class="page-background">
	<jsp:include page="/common/header_bar.jsp" />
	<jsp:include page="/common/search_small.jsp" />
    <br>
	<div class="reg-table">
		<h1>Submit an auction</h1>
		<h2 style="color:red">${message}</h2>
		<form action="AuctionSubmit" method="post" id="form" autocomplete="off" enctype="multipart/form-data">
			<input type="hidden" id="action" name="action" value="${action}">
			<input type="hidden" name="id" value="${auctionId}">
			<h4>Product Category(Required)</h4>
			<div id="category_list">
			</div>
			<input type="text" class="textbox-register" id="categories_tbox" name="categories" value="${auctionCategory}" readonly>
			<br>
			<button type="button" id="category_btn">Reset</button>
			<br>
			<hr>
			<br>
			<h4>Product Name (Required)</h4>
			<input type="text" name="name" class="textbox-register" placeholder="Product Name" value="${auctionName}">
			<hr>
			<br>
			<h4>Product Description (Required)</h4>
			<textarea name="description" class="textbox-register" cols="86" rows="20" placeholder="Description">${auctionDescription}</textarea>
			<hr>
			<br>
			<h4>Starting Bid (Required) / Buy Price (Optional)</h4>
			<input type="number" name="starting" class="textbox-register" 
				placeholder="Starting Bid" style="width:auto" value="${auctionStartingBid}">
			<input type="number" name="buyprice" class="textbox-register" 
				placeholder="Buy Price (Optional)" style="width:auto" value="${auctionBuyPrice}">
			<hr>
			<br>
			<h4>Auction end time (Required)</h4>
			<table style="width: 50%; margin-left: auto; margin-right: auto;">
				<tr>
					<th>Year</th>
					<th>Month</th>
					<th>Day</th>
					<th>Hour(24h)</th>
					<th>Minute</th>
				</tr>
				<tr>
					<td><input type="number" name="endsyear"
						class="textbox-register" style="width: 100%;" placeholder="Year" value="${auctionEndYear}"></td>
					<td><input type="number" name="endsmonth"
						class="textbox-register" style="width: 100%;" placeholder="Month"
						max="12" min="1" value="${auctionEndMonth}"></td>
					<td><input type="number" name="endsday"
						class="textbox-register" style="width: 100%;" placeholder="Day"
						max="31" min="1" value="${auctionEndDay}"></td>
					<td><input type="number" name="endshour"
						class="textbox-register" style="width: 100%;"
						placeholder="Hour(24h)" max="23" min="0" value="${auctionEndHour}"></td>
					<td><input type="number" name="endsminute"
						class="textbox-register" style="width: 100%;" placeholder="Minute"
						max="59" min="0" value="${auctionEndMinute}"></td>
				</tr>
			</table>
			<hr>
			<br>
			<h4>Product Image(s) (Optional):</h4>
			<input type="file" name="imagefiles" multiple="true">
			<br>
			<hr>
			<h4>Auction Location (Required)</h4>
			<input type="text" name="country" class="textbox-register" placeholder="Country" value="${auctionCountry}">
			<input type="text" name="location" class="textbox-register" placeholder="Location" value="${auctionLocation}">
			<h4>Auction Location on the map (Optional)</h4>
    		<div id="map"></div>
			<script async defer
				src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAeUHWIAbJ6ik1KM6PcqEHPM0uCWYF1cfM&callback=initMap">
			</script>
			<input type="hidden" name="latitude" id="lat" />
			<input type="hidden" name="longitude" id="lng" />
			<input type="submit" class="button-register" value="Submit">
		</form>
	</div>
</body>
</html>
