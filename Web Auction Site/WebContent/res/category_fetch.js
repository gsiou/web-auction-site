$(document).ready(function(){
	$("#category_btn").click(function(){
		window.categories_text = "";
		window.categories_last = "0";
		fetch("");
	});
	// If we submit new instead of editing
	// fetch categories at startup
	if($("#action").val() == "submit"){
		fetch("");
	}
});

window.categories_text = "";
window.categories_last = "0";

function fetch(parent){
	var send_data={
			parent_category : parent,
	};
	$("#category_list").hide(100);
	if(window.categories_last != parent){
		window.categories_text += parent + ">";
		$("#categories_tbox").val(window.categories_text);
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
		//$(document).scrollTop($("#category_list").offset().top ); // Scroll back to categories
		$(document).scrollTop();
	}
	else{
		$("#category_list").html("");
		$("#category_btn").show(1000);
	}
	$("#category_list").show(1000);
}