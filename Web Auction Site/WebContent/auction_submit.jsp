<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
	<head>
	<link rel="stylesheet" type="text/css" href="res/styles.css">
    <link rel="icon" href="res/favicon.ico" type="image/x-icon" />
    <script type="text/javascript" src="res/maps.js"> </script> 
    <script src="jquery-3.1.0.min.js"></script>
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
    <br>
	<div class="reg-table">
		<h1>Submit an auction</h1>
		<form action="AuctionSubmit" method="post" id="form" autocomplete="off" enctype="multipart/form-data">
			<input type="hidden" name="action" value="submit">
			<h4>Product Category(Required)</h4>
			<div id="category_list">
			</div>
			<input type="text" class="textbox-register" id="categories_tbox" name="categories" readonly>
			<br>
			<button type="button" id="category_btn">Reset</button>
			<br>
			<hr>
			<br>
			<h4>Product Name (Required)</h4>
			<input type="text" name="name" class="textbox-register" placeholder="Product Name">
			<hr>
			<br>
			<h4>Product Description (Required)</h4>
			<textarea name="description" class="textbox-register" cols="86" rows="20" placeholder="Description"></textarea>
			<hr>
			<br>
			<h4>Starting Bid (Required) / Buy Price (Optional)</h4>
			<input type="number" name="starting" class="textbox-register" placeholder="Starting Bid" style="width:auto">
			<input type="number" name="buyprice" class="textbox-register" placeholder="Buy Price (Optional)" style="width:auto">
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
						class="textbox-register" style="width: 100%;" placeholder="Year"></td>
					<td><input type="number" name="endsmonth"
						class="textbox-register" style="width: 100%;" placeholder="Month"
						max="12" min="1"></td>
					<td><input type="number" name="endsday"
						class="textbox-register" style="width: 100%;" placeholder="Day"
						max="31" min="1"></td>
					<td><input type="number" name="endshour"
						class="textbox-register" style="width: 100%;"
						placeholder="Hour(24h)" max="23" min="0"></td>
					<td><input type="number" name="endsminute"
						class="textbox-register" style="width: 100%;" placeholder="Minute"
						max="59" min="0"></td>
				</tr>
			</table>
			<hr>
			<br>
			<h4>Product Image(s) (Optional):</h4>
			<input type="file" name="imagefiles" multiple="true">
			<br>
			<hr>
			<h4>Auction Location (Required)</h4>
			<input type="text" name="country" class="textbox-register" placeholder="Country" value="${userCountry}">
			<input type="text" name="location" class="textbox-register" placeholder="Location" value="${userLocation}">
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
