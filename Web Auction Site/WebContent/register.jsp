<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<html>
	<head>
	<link rel="stylesheet" type="text/css" href="res/styles.css">
    <link rel="icon" href="res/favicon.ico" type="image/x-icon" />
	<title>Register|Auction Deals</title>
</head>
<body>
	<header class="logo" > Hammer Deals<br>Register </header>
	<div class="reg-table">
		<form action="Registration" method="post">
			<h3 style="color: red">${message}</h3>
			<input type="text" name="Username" class="textbox-register"
				placeholder="Username"><br> <input type="password"
				name="Password" class="textbox-register" placeholder="Password"><br>
			<input type="password" name="Password_conf" class="textbox-register"
				placeholder="Confirm Password"><br> <input type="email"
				name="Email" class="textbox-register" placeholder="Email"><br>
			<input type="number" name="Trn" class="textbox-register"
				placeholder="Trn"><br> <input type="number"
				name="Phone" class="textbox-register" placeholder="Phone"><br>
			<input type="text" name="Address" class="textbox-register"
				placeholder="Address"><br> <input type="text"
				name="Country" class="textbox-register" placeholder="Country"><br>

			<input type="submit" class="button-register" value="Register">
		</form>
	</div>
</body>
</html>