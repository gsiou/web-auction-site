<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Error loading page</title>
</head>
<body>
	<h2 style="color: red; text-align:center">${error}</h2>
	<h3 style="text-align:center">
		<a href="${pageContext.request.contextPath}/Login">Log In</a> to access this resource.
	</h3>
</body>
</html>