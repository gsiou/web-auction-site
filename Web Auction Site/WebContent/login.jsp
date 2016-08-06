<html>
	<head>
	<link rel="stylesheet" type="text/css" href="res/styles.css">
    <link rel="icon" href="res/favicon.ico" type="image/x-icon" />
	<title>Login|Auction Deals</title>
</head>
<body>
	
	<div class="reg-table">
		<header class="logo" > Hammer Deals<br>Login </header>
		<form action="Login" method="post">
			<h3 style="color: red">${error}</h3>
			<input type="text" name="Username" class="textbox-register" placeholder="Username"><br>
			<input type="password" name="Password" class="textbox-register" placeholder="Password"><br>
	
			<input type="submit" class="button-register" value="Login">
		</form>
	</div>
</body>
</html>