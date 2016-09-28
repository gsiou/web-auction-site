$(function() {
	var send_data = {
		parent_category : "",
	};
	$.ajax({
		url : 'AuctionSubmit?action=fetch_categories',
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
	
	$("#refresh_btn").click(function(){
		window.categories_last = "0";
		fetch("");
	});
	
	$("#advanced-activate").click(function(){
		$("#advanced-activate").hide();
		$("#advanced-search").show(1000);
	});
	$("#category-pick-activate").click(function(){
		$("#category-pick-activate").hide();
		$("#category-dropdown").replaceWith(
				"<input type='text' name='category' class='select-search' id='categories_tbox' readonly>"
		); 
		$("#category-pick").show(1000);
		fetch("");
	})
});

window.categories_text = "";
window.categories_last = "0";

function fetch(parent){
	var send_data={
			parent_category : parent,
	};
	$("#category_list").hide(100);
	if(window.categories_last != parent){
		$("#categories_tbox").val(parent);
		window.categories_last = parent;
		$("#category_list").html("");
		$.ajax({
			url : 'AuctionSubmit?action=fetch_categories',
			type: "POST",
			data : JSON.stringify(send_data),
			contentType: "application/json; charset=utf-8",
			dataType: 'json',
			success:function(data){
				$.each(data.categories, function(i, item){
					$("<span/>", {
						html: $("<button/>", {
							id: item,
							text: item + " ",
							onclick: "fetch('" + item + "')",
							class: "button-category",
							type: "button"
						})
					}).appendTo("#category_list");
				});
			},
			error: function(){
				alert("Connection to database failed! Try refreshing the page.");
			}
		});
		$(document).scrollTop();
	}
	else{
		$("#category_list").html("");
		$("#category_btn").show(1000);
	}
	$("#category_list").show(1000);
}