<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<script>
$.ajax({
	url: '${pageContext.request.contextPath}/Messages?action=count',
	type: "POST",
	data: null,
	contentType: "application/json; charset=utf-8",
	dataType: 'json',
	success: function(data){
		$("#msg-count").text(data.count);
	}
});
</script>
<header class="menubar">
	<c:choose>
		<c:when test="${sessionScope.userID == null}">
			<a href="Login">Login</a> |
      		<a href="Registration">Register</a>
		</c:when>
		<c:otherwise>
      		Logged in as <strong>${sessionScope.userID}</strong> |
      		<a href="${pageContext.request.contextPath}/Manage">Manage Auctions</a> |
      		<a href="${pageContext.request.contextPath}/Messages">Messages(<span id="msg-count"></span>)</a> |
      		<a href="${pageContext.request.contextPath}/Logout">Log Out</a>
		</c:otherwise>
	</c:choose>
</header>