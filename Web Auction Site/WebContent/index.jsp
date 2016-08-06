<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
  <head>
    <link rel="stylesheet" type="text/css" href="res/styles.css">
    <link rel="icon" href="res/favicon.ico" type="image/x-icon" />
    <title>Web Auction Site by RobCo Industries</title>
  </head>
  <body>
    <header class="menubar">
    	<c:choose>
    		<c:when test="${sessionScope.userID == null}">
      			<a href="Login">Login</a> |
      			<a href="Registration">Register</a>
      		</c:when>
      		<c:otherwise>
      			Logged in as <strong>${sessionScope.userID}</strong> |
      			<a href="/Messages">Messages()</a> |
      			<a href="/Logout">Log Out</a>
      		</c:otherwise>
      	</c:choose>
    </header>
    <div id="search">
      <h1>Web Auction Site</h1>
      <form>
        <div class="txtbtncontainer">
          <input type="text" placeholder="Search..." class="textbox-search" name="search_terms" />
          <input type="submit" class="button-search" value="Search"/>
          <select name="category" class="select-search">
            <option value="all">All Categories</option>
            <option value="tech">Technology</option>
            <option value="clothes">Clothing</option>
          </select>
        </div>
      </form>
    </div>
    <footer>RobCo Industries 2016</footer>
  </body>
</html>
  
