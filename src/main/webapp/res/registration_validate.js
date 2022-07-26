$(document).ready(function () {

    $('#regform').validate({
        rules: {
            Username: {
            	required: true
            },
            Password: {
            	required: true,
            	minlength: 4
            	
            },
            Password_conf: {
            	required: true,
            	minlength: 4,
            	equalTo: "#password"
            },
            Email: {
            	required: true,
            	email: true
            },
            Trn: {
            	required: true
            },
            Phone: {
            	required: true
            },
            Address: {
            	required: true
            },
            Country: {
            	required: true
            }
            
        },
        errorElement: 'div',
        messages: {
        	Password_conf: {
        		equalTo: "Passwords do not match."
        	}
        }
    });
});