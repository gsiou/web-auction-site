<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="res/styles.css">
<link rel="icon" href="res/favicon.ico" type="image/x-icon" />
<title>Admin Panel</title>
</head>
<body>
	<header class="menubar">
		<a href="${pageContext.request.contextPath}">Home</a>
	</header>
	<div class="reg-table">
		<h2>Admin Panel</h2>
		<table class="user-table">
			<tr><th>UserID</th><th>Access Level</th><th>Action</th></tr>
			<c:forEach items="${userList}" var="user">
				<tr>
					<td>${user.userId}</td>
					<td>${user.access_lvl}</td>
					<c:choose>
						<c:when test="${user.access_lvl == 0}">
							<td>
								<form action="" method="post">
									<input type="hidden" name="userid" value="${user.userId}">
									<input type="hidden" name="action" value="activate">
									<input type="submit" value="Activate User">
								</form>
							</td>
						</c:when>
						<c:otherwise>
							<td>
								<form action="" method="post">
									<input type="hidden" name="userid" value="${user.userId}">
									<input type="hidden" name="action" value="deactivate">
									<input type="submit" value="Deactivate User">
								</form>
							</td>
						</c:otherwise>
					</c:choose>
				</tr>
			</c:forEach>
		</table>
	</div>
</body>
</html>