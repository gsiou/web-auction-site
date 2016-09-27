$(document).ready(function () {

    $('#form').validate({
        rules: {
            name: {
                required: true
            },
            description: {
                required: true
            },
            categories:{
            	required: true,
            	minlength: 2
            },
            starting:{
            	required: true
            },
            endsyear:{
            	required: true
            },
            endsmonth:{
            	required: true
            },
            endsday:{
            	required: true
            },
            endshour:{
            	required: true
            },
            endsminute:{
            	required: true
            },
            country:{
            	required: true
            },
            location:{
            	required: true
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