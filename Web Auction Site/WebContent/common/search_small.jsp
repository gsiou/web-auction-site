<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<script src="res/search_fetch_categories.js"></script>

<div id="uni-search">
	<form action="${pageContext.request.contextPath}/Search" method="get">
		<a href="${pageContext.request.contextPath}" title="Hammer Deals"><img
			class="tiny-logo-left" src="logo.png"></a>
		<div class="small-txtbtncontainer">
			<input type="text" placeholder="Search..." class="textbox-search" name="search_terms" />
			<input type="submit" class="button-search" value="Search" />
			<select id="category-dropdown" name="category" class="select-search">
				<option value="all">All Categories</option>
			</select>
		</div>
	</form>
</div>