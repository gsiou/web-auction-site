<html>
	<head>
	<link rel="stylesheet" type="text/css" href="res/styles.css">
    <link rel="icon" href="res/favicon.ico" type="image/x-icon" />
	<title>Login|Hammer Deals</title>
</head>
<body>
	<a href="${pageContext.request.contextPath}/" title="Hammer Deals">
	<img class="big-logo-center" src="logo.png" >
	</a>
	<div class="reg-table">
		<header class="Gtext" >Login</header>
		<form action="Login" method="post">
			<h3 style="color: red">${error}</h3>
			<input type="text" name="Username" class="textbox-register" placeholder="Username"><br>
			<input type="password" name="Password" class="textbox-register" placeholder="Password"><br>
	
			<input type="submit" class="button-register" value="Login">
		</form>
	</div>
</body>
</html>