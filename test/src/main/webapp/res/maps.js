function initMap() {
		var mapDiv = document.getElementById('map');
		var map = new google.maps.Map(mapDiv, {
			center: {lat: 37.96544812571401, lng: 23.567562103271484},zoom: 8});
		
		var marker;
		google.maps.event.addListener(map, 'click', function(event) {
			
			if ( marker ) {
			    marker.setPosition(event.latLng);
			  } 
			else {
				marker = new google.maps.Marker({
						position: event.latLng,
						map: map,
						title: 'Your location' });
			}
			
			document.getElementById('lat').value = event.latLng.lat();
			document.getElementById('lng').value = event.latLng.lng();
		});

}