<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link rel="stylesheet" type="text/css" href="res/styles.css">
	<link rel="icon" href="res/favicon.ico" type="image/x-icon" />
	<script src="jquery-3.1.0.min.js"></script>
	<title>Admin Panel</title>
</head>
<body>
	<header class="menubar">
		<a href="${pageContext.request.contextPath}">Home</a>
	</header>
	<div style="text-align: center">
		<h3>${param.message}</h3>
		<button id="btn-activate" class="button-register">Manage Users</button>
	</div>
	<div class="reg-table" id="useractivate">
		<h2>Admin Panel</h2>
		<button id="toggle-inactive">Toggle Not Activated</button>
		<table class="user-table">
			<tr><th>UserID</th><th>Access Level</th><th>Action</th></tr>
			<c:forEach items="${userList}" var="user">
				<tr class="${(user.access_lvl == 0) ? 'activated':'deactivated'}">
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
	<script>
		// Start with both hidden
		$("#useractivate").hide();
		$("#datasetupload").hide();
		
		// Expand on clicd
		$("#btn-activate").click(function() {
			//$("#datasetupload").toggle(1000);
			$("#useractivate").toggle(1000);
		});
		$("#btn-dataset").click(function() {
			$("#datasetupload").toggle(1000);
			//$("#useractivate").toggle(1000);
		})
		$("#toggle-active").click(function(){
			$(".activated").toggle();
		});
		$("#toggle-inactive").click(function(){
			$(".deactivated").toggle();
		});
	</script>
</body>
</html>