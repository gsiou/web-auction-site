<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<script>
	$(function() {
		var send_data = {
			parent_category : "",
		};
		$.ajax({
				url : '${pageContext.request.contextPath}/AuctionSubmit?action=fetch_categories',
				type : "POST",
				data : JSON.stringify(send_data),
				contentType : "application/json; charset=utf-8",
				dataType : 'json',
				success : function(data) {
					$.each(data.categories, function(i, item) {
						$("#category-dropdown").append($("<option/>", {
							value : item,
							text : item,
						}));
					});
				},
		});
	});
</script>
<div id="uni-search">
	<form>
		<a href="${pageContext.request.contextPath}" title="Hammer Deals"><img
			class="tiny-logo-left" src="logo.png"></a>
		<div class="small-txtbtncontainer">
			<input type="text" placeholder="Search..." class="textbox-search"
				name="search_terms" /> <input type="submit" class="button-search"
				value="Search" /> <select id="category-dropdown" name="category" class="select-search">
				<option value="all">All Categories</option>
			</select>
		</div>
	</form>
</div>