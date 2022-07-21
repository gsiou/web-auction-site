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
			<input type="text" placeholder="Search..." class="textbox-search" name="description" />
			<input type="submit" class="button-search" value="Search" />
			<select id="category-dropdown" name="category" class="select-search">
				<option value="all">All Categories</option>
			</select>
		</div>
		<br>
        <a href="#" class="link1" id="advanced-activate">Advanced Search...</a>
      	<div id="advanced-search" style="display: none;">
        	<br>
      		Price range: <input type="number" name="price-from" placeholder="Minimum...">
      		<input type="number" name="price-to" placeholder="Maximum...">
    		Location: <input type="text" name="location" placeholder="Location...">
    		<br>
    		<br>
    		<a href="#" class="link1" id="category-pick-activate">Show all categories</a>
    		<div id="category-pick" style="display: none;">
    			<input type="hidden" value="pick" id="action"/>
    			<button type="button" id="refresh_btn">Reload</button>
    			<div id="category_list">
				</div>
    		</div>
      	</div>
	</form>
</div>