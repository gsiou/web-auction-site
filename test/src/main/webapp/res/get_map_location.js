function initMap() {
		var mapDiv = document.getElementById('view-map');
		var mapLatlng = new google.maps.LatLng(latitude,longitude);
		var map = new google.maps.Map(mapDiv, {
			center: mapLatlng,zoom: 8});
		
		var marker;			

		marker = new google.maps.Marker({
				position: mapLatlng,
				map: map,
			title: 'Auction Location' });

}