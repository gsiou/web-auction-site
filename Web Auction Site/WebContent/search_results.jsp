<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<html>
<head>
<jsp:include page="/common/common.jsp" />
<title>Search Results</title>
<style>
.preview-img {
	width: 200px;
	height: auto;
}
</style>
</head>
<body class="page-background">
	<jsp:include page="/common/header_bar.jsp" />
	<jsp:include page="/common/search_small.jsp" />
	<br>
	<div class="reg-table">
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
					<td><a class="link1"
						href="${pageContext.request.contextPath}/AuctionView?page=view&auctionID=${auction.auctionId}">
							<c:out value="${auction.name}" />
					</a></td>
					<td><c:out value="${auction.description}" /></td>
					<td><c:out value="${auction.location}" /></td>
					<td><strong>Current Bid:</strong> <c:out value="${auction.current_Bid}" />
						<c:if test="${auction.buy_Price > 0}">
							<br>
							<strong>Buy Price:</strong> <c:out value="${auction.buy_Price}" />
						</c:if>
					</td>
					<td><img class="preview-img"
						src="${pageContext.request.contextPath}/image/${auction.images[0].url}" />
					</td>
				</tr>
			</c:forEach>
		</table>
		<hr>
		<c:if test="${prevPage}">
			<a class="link1" href="
				${pageContext.request.contextPath}/Search?
				description=${descriptionParam}&category=${categoryParam}&location=${locationParam}
				&price-from=${priceMinParam}&price-to=${priceMaxParam}&page=${currentPage-1}">
				Previous Page
			</a>
		</c:if>
		<c:if test="${nextPage}">
			<a class="link1" href="
				${pageContext.request.contextPath}/Search?
				description=${descriptionParam}&category=${categoryParam}&location=${locationParam}
				&price-from=${priceMinParam}&price-to=${priceMaxParam}&page=${currentPage+1}">
				Next Page
			</a>
		</c:if>
	</div>
</body>
</html>