$(document).ready(function () {
	// Set up rules for validation
    $('#form').validate({
        rules: {
            name: {
                required: true,
                maxlength: 45
            },
            description: {
                required: true
            },
            categories:{
            	required: true,
            	minlength: 2
            },
            starting:{
            	required: true,
            	min: 0.01
            },
            endsyear:{
            	required: true,
            	min: 0,
            },
            endsmonth:{
            	required: true,
            	min: 1,
            	max: 12
            },
            endsday:{
            	required: true,
            	min: 1,
            	max: 31
            },
            endshour:{
            	required: true,
            	min: 0,
            	max: 23
            },
            endsminute:{
            	required: true,
            	min: 0,
            	max: 59
            },
            country:{
            	required: true,
            	maxlength: 45
            },
            location:{
            	required: true,
            	maxlength: 255
            },
            buyprice:{
            	min: 0.01
            },
        },
        errorElement: 'div'
    });
    
    $("input[type='submit']").click(function(){
        var $imagesUploaded = $("input[type='file']");
        if (parseInt($imagesUploaded.get(0).files.length)>6){
         alert("You can only upload a maximum of 6 images");
         return false;
        }
    });
});