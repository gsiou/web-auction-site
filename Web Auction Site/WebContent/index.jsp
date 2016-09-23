<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
  <head>
  	<jsp:include page="/common/common.jsp" />
    <title>Web Auction Site by RobCo Industries</title>
  </head>
  <body class="page-background">
	<jsp:include page="/common/header_bar.jsp" />
    <div id="search">
      <form action="${pageContext.request.contextPath}/Search" method="GET">
      	<a href="${pageContext.request.contextPath}" title="Hammer Deals"><img class="small-logo-left" src="logo.png" ></a>
        <div class="txtbtncontainer">
          <input type="text" placeholder="Search..." class="textbox-search" name="description" />
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
  
