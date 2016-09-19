<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
  <head>
    <link rel="stylesheet" type="text/css" href="res/styles.css">
    <link rel="icon" href="res/favicon.ico" type="image/x-icon" />
    <title>Web Auction Site by RobCo Industries</title>
    <script src="jquery-3.1.0.min.js"></script>
  </head>
  <body class="page-background">
    <header class="menubar">
    	<c:choose>
    		<c:when test="${sessionScope.userID == null}">
      			<a href="Login">Login</a> |
      			<a href="Registration">Register</a>
      		</c:when>
      		<c:otherwise>
      			Logged in as <strong>${sessionScope.userID}</strong> |
      			<a href="${pageContext.request.contextPath}/Messages">Messages()</a> |
      			<a href="${pageContext.request.contextPath}/Logout">Log Out</a>
      		</c:otherwise>
      	</c:choose>
    </header>
    <div id="search">
      <form>
      	<a href="${pageContext.request.contextPath}" title="Hammer Deals"><img class="small-logo-left" src="logo.png" ></a>
        <div class="txtbtncontainer">
          <input type="text" placeholder="Search..." class="textbox-search" name="search_terms" />
          <input type="submit" class="button-search" value="Search"/>
          <select name="category" class="select-search">
            <option value="all">All Categories</option>
            <c:forEach items="${categoryList}" var="category" varStatus="count">
            	<option value="${fn:escapeXml(category.name)}">
            		<c:out value="${category.name}"/>
            	</option>
           	</c:forEach>
          </select>
        </div><br>
        <a href="#" class="link1" id="advanced-activate">Advanced Search...</a>
      	<div id="advanced-search" style="display: none;">
        	<br>
      		Price range: <input type="number" name="price-from" placeholder="Minimum...">
      		<input type="number" name="price-to" placeholder="Maximum...">
    		Location: <input type="text" name="location" placeholder="Location...">
      	</div>
      </form>
    </div>
    <footer>RobCo Industries 2016</footer>
    <script>
    $(function(){
    	$("#advanced-activate").click(function(){
    		$("#advanced-activate").hide();
    		$("#advanced-search").show(1000);
    	});
    });
    </script>
  </body>
</html>
  
