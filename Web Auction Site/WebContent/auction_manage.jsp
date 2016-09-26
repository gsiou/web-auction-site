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
.auction-type{
	text-align: center;
}
</style>
</head>
<body>
	<jsp:include page="/common/header_bar.jsp" />
	<h2 class="auction-type">My Auctions</h2>
	<div class="reg-table">
		<h2>Inactive Auctions</h2>
		<table class="search-table auction-list">
			<c:forEach items="${inactiveList}" var="auction" varStatus="count">
				<tr>
					<td><a class="link2"
						href="${pageContext.request.contextPath}/AuctionView?page=view&auctionID=${auction.auctionId}">
							<c:out value="${auction.name}"/> </a></td>
					<td>
						<a href="${pageContext.request.contextPath}/AuctionSubmit?action=edit&id=${auction.auctionId}" class="link2">Edit</button>
					</td>
					<td>
						<form action="${pageContext.request.contextPath}/AuctionSubmit" method="post">
							<input type="hidden" name="action" value="activate">
							<input type="hidden" name="id" value="${auction.auctionId}">
							<input type="submit" class="button-register" value="Start">
						</form>
					</td>
				</tr>
			</c:forEach>
		</table>
		<hr>
		<h2>Expired Auctions</h2>
		<table class="search-table auction-list">
			<c:forEach items="${soldList}" var="auction" varStatus="count">
				<tr>
					<td><a class="link2"
						href="${pageContext.request.contextPath}/AuctionView?page=view&auctionID=${auction.auctionId}">
							<c:out value="${auction.name}"/> </a></td>
				</tr>
			</c:forEach>
		</table>
		<hr>
		<h2>Active Auctions</h2>
		<table class="search-table auction-list">
			<c:forEach items="${activeList}" var="auction" varStatus="count">
				<tr>
					<td><a class="link2"
						href="${pageContext.request.contextPath}/AuctionView?page=view&auctionID=${auction.auctionId}">
							<c:out value="${auction.name}"/> </a></td>
				</tr>
			</c:forEach>
		</table>
		<hr>
		<h2>Auctions I Won</h2>
		<table class="search-table auction-list">
			<c:forEach items="${wonList}" var="auction" varStatus="count">
				<tr>
					<td><a class="link2"
						href="${pageContext.request.contextPath}/AuctionView?page=view&auctionID=${auction.auctionId}">
							<c:out value="${auction.name}"/> </a></td>
					<td><a class="link2" 
						href="${pageContext.request.contextPath}/Messages?sendto=${auction.creator.userId}">Send message</a>
					</td>
				</tr>
			</c:forEach>
		</table>
		<hr>
		<h2>Auctions I Lost</h2>
		<table class="search-table auction-list">
			<c:forEach items="${lostList}" var="auction" varStatus="count">
				<tr>
					<td><a class="link2"
						href="${pageContext.request.contextPath}/AuctionView?page=view&auctionID=${auction.auctionId}">
							<c:out value="${auction.name}"/> </a></td>
				</tr>
			</c:forEach>
		</table>
		<hr>
		<h2>Active Auctions I Bidded</h2>
		<table class="search-table auction-list">
			<c:forEach items="${biddedList}" var="auction" varStatus="count">
				<tr>
					<td><a class="link2"
						href="${pageContext.request.contextPath}/AuctionView?page=view&auctionID=${auction.auctionId}">
							<c:out value="${auction.name}"/> </a></td>
				</tr>
			</c:forEach>
		</table>
	</div>
</body>
</html>