<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<jsp:include page="/common/common.jsp" />
<title>Insert title here</title>
<style>
	.preview-img{
		width:200px;
		height:auto;
	}
	.search-table{
		text-align: center;
	}
</style>
</head>
<body>
	<jsp:include page="/common/header_bar.jsp" />
	<table class="search-table">
		<tr>
			<th>Name</th>
			<th>Description</th>
			<th>Location</th>
			<th>Price</th>
			<th>Image</th>
		</tr>
		<c:forEach items="${searchResults}" var="auction" varStatus="count">
			<tr>
				<td><a
					href="${pageContext.request.contextPath}/AuctionView?page=view&auctionID=${auction.auctionId}">
						<c:out value="${auction.name}" />
				</a></td>
				<td><c:out value="${auction.description}"/></td>
				<td><c:out value="${auction.location}"/></td>
				<td></td>
				<td>
					<img class="preview-img" src="${pageContext.request.contextPath}/image/${auction.images[0].url}"/>
				</td>
			</tr>
		</c:forEach>
	</table>
</body>
</html>