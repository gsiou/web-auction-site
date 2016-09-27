<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<html>
	<head>
		<jsp:include page="/common/common.jsp" />
		<title>Error</title>
	</head>
	<body class="page-background">
		<jsp:include page="/common/header_bar.jsp" />
		<jsp:include page="/common/search_small.jsp" />
    	<br>
		<div class="reg-table">
			<h2 style="color:red;">Error: <c:out value="${message}"/></h2>
			<button class="button-register" onclick="window.history.back()">Go back</button>
		</div>
	</body>
</html>
