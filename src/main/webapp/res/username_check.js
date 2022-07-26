$(document).ready(function() {  
	var minimum_length = 3;  
    var length_error = 'Username must be longer';
    var invalid='Username is not available';
    var valid='Username is valid';
  
    $('#username_check_button').click(function(){
        if($('#Username').val().length < minimum_length){  
            $('#pop-up-message').html(length_error);  
        }else{
        	var username=$('#Username').val();
        	var object = {username:username};
        	$.ajax({
        		url : 'Registration?Action=namecheck',
        	    type: "POST",
        	    data : JSON.stringify(object),
        	    contentType: "application/json; charset=utf-8",
        	    dataType: 'json',
        	    success:function(data){
        	    	var response=data.response;
        	    	if(Boolean(response)){
        	    		$('#pop-up-message').html(invalid);
        	    	}
        	    	else{
        	    		$('#pop-up-message').html(valid);
        	    	}
        	    }
        	 });
        }
        $( "#pop-up-message" ).fadeIn(1).fadeOut(4000,0, function() {});
    });
});