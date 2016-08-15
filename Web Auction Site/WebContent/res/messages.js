$(document).ready(function(){
	$("#btn-show-send").click(function(){
		$("#message-form").toggle(1000);
	});
	$("#message-form").submit(function(e){
		send_message();
		e.preventDefault();
	});
});

window.current_page = 0;
window.total_pages = 0;

/*
 * type is "incoming" or "sent"
 */
function load_messages(type, page){
	send_data = {
		type: type,
		page: page,
	};
	$.ajax({
		url: 'Messages?action=fetch',
		type: "POST",
		data: JSON.stringify(send_data),
		contentType: "application/json; charset=utf-8",
		dataType: 'json',
		success: function(data){
			$.each(data.messages, function(i, item){
				$("<tr/>",{
					html: $("<a/>", {
						href: "#",
						onclick: "",
						html: render_row(item.subject, item.user, item.date),
						class: item.read ? "message-read" : "message-unread"
					})
				}).appendTo("#message_list");
			});
		}
			
	});
}

function render_row(subject, user, date){
	//
}

function send_message(){
	obj = {
			'msg-to' : $("#msg-to").val(),
			'msg-subject' : $("#msg-subject").val(),
			'msg-body' : $("#msg-body").val()
	};
	$.ajax({
		url: "Messages?action=send",
		type: "POST",
		data: JSON.stringify(obj),
		contentType: "application/json; charset=utf-8",
		dataType: 'json',
		success: function(data){
			if(Boolean(data.success)){
				$("#message-form").hide(1000);
				$("#result").text(data.message);
			}
			else{
				$("#result").html("<span style='color: red'>" + data.message + "</span>");
				$(document).scrollTop($("#result").offset().top); // So user can see the message.
			}
		},
	});
}