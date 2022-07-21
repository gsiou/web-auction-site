<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
	<head>
	<link rel="stylesheet" type="text/css" href="res/styles.css">
    <link rel="icon" href="res/favicon.ico" type="image/x-icon" />
    <script type="text/javascript" src="res/maps.js"> </script> 
	<script src="jquery-3.1.0.min.js"></script>
    <script type="text/javascript" src="res/username_check.js"> </script>
    <script type="text/javascript" src="res/jquery.validate.min.js"></script>
    <script type="text/javascript" src="res/registration_validate.js"></script>
	<title>Register|Hammer Deals</title>
		
</head>
<body class="page-background">
	<a href="${pageContext.request.contextPath}/" title="Hammer Deals">
	<img class="big-logo-center" src="logo.png" >
	</a>
	<div class="reg-table">
		<header class="Gtext" >Register</header>
		<form action="Registration" method="post" id="regform">
			<h3 style="color: red">${message}</h3>
			<div id='pop-up-message'></div>
			<input type="hidden" id="Action" name="Action" value="submit">
			<input type="text" id="Username" name="Username" class="textbox-register"
				placeholder="Username"><input type='button' id='username_check_button' value='Check Availability'>
				<br> <input type="password"
				name="Password" id="password" class="textbox-register" placeholder="Password"><br>
			<input type="password" name="Password_conf" class="textbox-register"
				placeholder="Confirm Password"><br> <input type="email"
				name="Email" class="textbox-register" placeholder="Email"><br>
			<input type="number" name="Trn" class="textbox-register"
				placeholder="Trn"><br> <input type="number"
				name="Phone" class="textbox-register" placeholder="Phone"><br>
			<input type="text" name="Address" class="textbox-register"
				placeholder="Address"><br> <input type="text"
				name="Country" class="textbox-register" placeholder="Country"><br>
				
			    <p>Add your location(Optional)</p>
    			<div id="map"></div>
				<script async defer
				    src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAeUHWIAbJ6ik1KM6PcqEHPM0uCWYF1cfM&callback=initMap">
				</script>
			<input type="hidden" name="Latitude" id="lat" />
			<input type="hidden" name="Longitude" id="lng" />
			
			<input type="submit" class="button-register" value="Register">
		</form>
	</div>
</body>
</html>