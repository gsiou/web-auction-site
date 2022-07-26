<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link rel="stylesheet" type="text/css" href="res/styles.css">
	<link rel="icon" href="res/favicon.ico" type="image/x-icon" />
	<script src="jquery-3.1.0.min.js"></script>
	<title>Admin Panel</title>
</head>
<body class="page-background">
	<header class="menubar">
		<a href="${pageContext.request.contextPath}">Home</a>
	</header>
	<div style="text-align: center">
		<h3>${param.message}</h3>
		<button id="btn-activate" class="button-register">Hide/Show Users</button>
	</div>
	<div class="reg-table" id="useractivate">
		<h2>Admin Panel</h2>
		<c:choose>
			<c:when test="${userType == 'all'}">
				<a href="Admin?page=${currentPage}&type=unactivated">Show Only Not Activated</a>
			</c:when>
			<c:otherwise>
				<a href="Admin?page=${currentPage}&type=all">Show All</a>
			</c:otherwise>
		</c:choose>
		
		<br>
		<c:if test="${currentPage-1 >= 0}">
			<a href="Admin?page=${currentPage-1}&type=${userType}">Prev</a>
		</c:if>
		<c:if test="${currentPage+1 < totalPages}">
			<a href="Admin?page=${currentPage+1}&type=${userType}">Next</a>
		</c:if>
		<table class="user-table">
			<tr><th>UserID</th><th>Access Level</th><th>Action</th></tr>
			<c:forEach items="${userList}" var="user" varStatus="count">
				<tr>
					<td><a href="javascript:void(0);" onclick="details('${count.index}')">
							<c:out value="${user.userId}"/>
						</a>
					</td>
					<td>${user.accessLvl}</td>
					<c:choose>
						<c:when test="${user.accessLvl == 0}">
							<td>
								<form action="" method="post">
									<input type="hidden" name="userid" value="${fn:escapeXml(user.userId)}">
									<input type="hidden" name="action" value="activate">
									<input type="submit" value="Activate User">
								</form>
							</td>
						</c:when>
						<c:otherwise>
							<td>
								<form action="" method="post">
									<input type="hidden" name="userid" value="${fn:escapeXml(user.userId)}">
									<input type="hidden" name="action" value="deactivate">
									<input type="submit" value="Deactivate User">
								</form>
							</td>
						</c:otherwise>
					</c:choose>
				</tr>
				<tr class="details" id="details${count.index}">
					<td>
						<div>
							Trn: <c:out value="${user.trn}"/> <br>
							Phone: <c:out value="${user.phone}"/> <br>
							Address: <c:out value="${user.address}"/> <br>
							Country: <c:out value="${user.country}"/> <br>
							Email: <c:out value="${user.email}"/> <br>
							Bid Rating: <c:out value="${user.bidRating}"/> <br>
							Sell Rating: <c:out value="${user.sellRating}"/> <br>
						</div>
					</td>
				</tr>
			</c:forEach>
		</table>
		<c:if test="${currentPage-1 >= 0}">
			<a href="Admin?page=${currentPage-1}&type=${userType}">Prev</a>
		</c:if>
		<c:if test="${currentPage+1 < totalPages}">
			<a href="Admin?page=${currentPage+1}&type=${userType}">Next</a>
		</c:if>
		
	</div>
	<div style="text-align: center">
		<button id="btn-dataset" class="button-register">Upload Dataset XML</button>
	</div>
	<div class="reg-table" id="datasetupload">
		<h2>Insert data from dataset xml</h2>
		<form action="" method="post" enctype="multipart/form-data">
			DataFile: 
			<input type="file" name="file"><br>
			<input type="hidden" name="action" value="loadDataset">
			<input type="submit" value="Upload File">
		</form>
	</div>
	<div style="text-align: center">
		<form action="" method="post">
			<input type="hidden" name="action" value="exportDataset">
			<button id="btn-export" class="button-register">Export Dataset XML</button>
		</form>
	</div>
	<script>
		// Hide dataset import on startup
		$("#datasetupload").hide();
		
		// Hide user details on startup
		$(".details").hide();
		
		// Expand on clicd
		$("#btn-activate").click(function() {
			//$("#datasetupload").toggle(1000);
			$("#useractivate").toggle(1000);
		});
		$("#btn-dataset").click(function() {
			$("#datasetupload").toggle(1000);
			//$("#useractivate").toggle(1000);
		})
		
		var details = function (userindex){
			$(".details").hide(10);
			$("#details" + userindex).toggle(1000);
		}
	</script>
</body>
</html>