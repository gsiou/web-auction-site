function validateBid(){
    	var response;
    	var amount=$('#Bid_amount').val();
    	if (amount <= cur_bid){
    		$('#pop-up-message').html('Bid must exceed current bid');
    		$('#pop-up-message').fadeIn(1).fadeOut(4000,0);
    		return false;
    	}
    	else if(amount >= buy_price && buy_price > 0){
    		$('#pop-up-message').html('Bid exceeds buy price.Please use buy option');
    		$('#pop-up-message').fadeIn(1).fadeOut(4000,0);
    		return false;
    	}
    	else{
	        var response = confirm("Are you sure that you want to bid this auction for " + amount + "$?");
	        if (response == true) {
	        	return true;
	        }
	        else{
	        	return false;
	        }
    	}
}