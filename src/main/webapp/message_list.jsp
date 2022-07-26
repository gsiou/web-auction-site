<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    
<html>
<head>
<jsp:include page="/common/common.jsp" />
<title>Messages|Hammer Deals</title>
<style type="text/css">
	#message-list{
		background-color: white;
		margin-left: auto;
		margin-right: auto;
		width: 100%;
		text-align: center;
	}
	.subject{
		width:75%;
	}
	.user{
		width: 10%;
	}
	.date{
		width: 15%;
	}
	#message-list a{
		color: blue;
		text-decoration: underline;
	}
	#message-list a:hover{
		color: white;
		text-decoration: none;
		background-color: orange
	}
	#message-form{
		margin-left: auto;
		margin-right: auto; 
		
	}
	.message-body{
		display: none;
	}
	.unread{
		font-weight: bold;
	}
	.read{
		font-weight: normal;
	}
	.selected-tab{
		font-weight: bold;
		border-bottom: 3px solid orange;
	}
</style>
<script src="jquery-3.1.0.min.js"></script>
<script src="res/messages.js"></script>
</head>
<body class="page-background">
	<jsp:include page="/common/header_bar.jsp" />
	<div class="reg-table">
		<button class="button-register" id="btn-show-send" >Send a message</button>
		<div id="message-send">
			<h4 id="result"></h4>
			<form id="message-form" ${empty requestScope.send_username ? "style='display:none'" : ""}">
				Send To:
				<br><input type="text" class="textbox-register" id="msg-to" value="${requestScope.send_username}">
				<br>Subject:
				<br><input type="text" class="textbox-register" id="msg-subject" value="${requestScope.subject}">
				<br>Body:
				<br><textarea class="textbox-register" cols="86" rows="20" id="msg-body"></textarea>
				<button class="button-register">Send</button>
				<hr>
			</form>
		</div>
		<br>
		<a id="btn-received" class="link1">Inbox</a><a id="btn-sent" class="link1">Sent</a><a id="btn-refresh" class="link1">Refresh</a>
		<hr>
		<table id="message-list">
			<tr><th>Subject</th><th>User</th><th>Date</th></tr>
			<tbody id="message-list-tbody">
			</tbody>
		</table>
		Pages:
		<div id="pages">
		</div>
	</div>
</body>
</html>