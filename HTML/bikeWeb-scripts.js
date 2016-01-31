// JavaScript Document
$(document).ready(function(e) {
	var topAdjustment, scrollTime, easing;
	var windowWidth = $(window).outerWidth();
	var windowHeight = $(window).height();
	var sectionHeight = $("")
   //set initial values for variables
   topAdjustment = 0;//how many pixels do you want the scroll to stop below the top of the browser i.e. height of topBarCentering
   scrollTime = 1000;//how much time in milliseconds 1000ms=1s do you want the scroll to occur
   easing = "easeInOutQuint";//type of easing for the scroll effect
   //actions when buttons are clicked
   arrowOpacityFlicker(1000);
   $(".btn").click(function () {
      var btnClicked=$(this).attr("ID");
      var sectionNumber=btnClicked.substring(3,5);//extracts the button number, i.e. btn1 becomes 1
      var targetSection = "#section"+sectionNumber;
      if(sectionNumber!=5){//since there's no section0, this execute for sectionNumbers not equal to 0
         $(targetSection).goTo();//calls on function goTo below to scroll to targetSection
      }
   });
   $(".section").css ({
		"height":windowHeight
   })
   $("#bikeImage").css({
		"left":(windowWidth/2) - 300
	})
	$("#introText").css({
		"left":(windowWidth-330)/2
	})
	$("#introArrow").css({
		"left":(windowWidth/2) - 50,
		"top":(windowHeight/2)+200
	})
	$("#map").css({
		
		"left":0,
		"width":windowWidth*.50,//See if this value is messed up or not on larger screens
		"height":(windowHeight*.8)
	})
	$("#bikeAssistantName").css({
		"left":(windowWidth-676)/2,
		top:(windowHeight/2)-100
	})
	$(".Content").css({
		"left":(windowWidth*.60),
		"width":(windowWidth)*.30+10,
		"height":(windowHeight*.80)
	})
	$("#right-panel").css({
		"width":(windowWidth)*.30
	})
	$("#map2").css({
		"width":(windowWidth-200),
		"left":100,
		"height":(windowHeight-200)
	})
	$("#introArrow").click(function () {
      var btnClicked=$(this).attr("ID");
      var sectionNumber=2;
      var targetSection = "#section"+sectionNumber;
      $(targetSection).goTo();//calls on function goTo below to scroll to targetSection
   });
   
   (function($) {//defining a function
      $.fn.goTo = function() {
         $("html, body").stop().animate({//stop is for quick clicking of buttons to stop current action
            "scrollTop": $(this).offset().top - topAdjustment + "px"//the executes the scrolling action to new y position
         }, scrollTime, easing);
         return this;
      }
   })(jQuery);	
   
var newURL, fadeOutTime=2000;
$(".linkBackToProjects").css({"cursor":"pointer"});//ensures the link has the hand cursor
$(".linkBackToProjects").click(function() {//executes when you click on any div with class="linkToProject"
      fadeToWhite();//This line actually starts the animation
});
//For outro animation that fades everything to white

function fadeToWhite(){
      $("html").animate({//since html contains everything, changing its opacity fades out everything in the html
            "opacity":"0" 
      },fadeOutTime,function(){
            window.location.href = "/~HunterC";
      });
};

//end of Outro Animation script
var newH, el, thingsH, peopleH, eventsH, tableOfContentsH, introH, maxH, selectedTitleState, t, selectedTitleY, curentExpanded;
var slideTime = 500; easingMethod = "easeInOutQuint";
	
	selectedTitleState = "collapsed";
	var crewHeaderClicked, crewHeaderNumber, crewInfo;
	$(".crewInfo").slideUp(1);//forces any contentBox that is down by default to be collapsed


function arrowOpacityFlicker(repeats) {
	if(repeats > 0) {
		$("#introArrow").animate({
			opacity:".5",
			top:(windowHeight/2)+210
		},1000,"linear");
		$("#introArrow").animate({
			opacity:"1",
			top:(windowHeight/2)+200
		},1000,"linear");
		arrowOpacityFlicker(repeats-1)
	}
}
 
});


var map;
var infowindow;

function initialize() {
  var santacruz = {lat: 36.9700, lng: -122.0300};

  map = new google.maps.Map(document.getElementById('map2'), {
    center: santacruz,
    zoom: 13
  });

  infowindow = new google.maps.InfoWindow();

  var service = new google.maps.places.PlacesService(map);
  service.nearbySearch({
    location: santacruz,
    radius: 20000,
    types: ['bicycle_store']
  }, callback);
}

function callback(results, status) {
  if (status === google.maps.places.PlacesServiceStatus.OK) {
    for (var i = 0; i < results.length; i++) {
      createMarker(results[i]);
    }
  }
}

function createMarker(place) {
  var placeLoc = place.geometry.location;
  var marker = new google.maps.Marker({
    map: map,
    position: place.geometry.location
  });

  google.maps.event.addListener(marker, 'click', function() {
    infowindow.setContent(place.name);
    infowindow.open(map, this);
  });
}

function initMap() {
  var origin_place_id = 'College Nine, Santa Cruz, CA, United States';
  var destination_place_id = 'Crown College, Santa Cruz, CA, United States';
  var map = new google.maps.Map(document.getElementById('map'), {
    zoom: 4,
    center: {lat: 37.0000, lng: -122.0580}
  });

  var directionsService = new google.maps.DirectionsService;
  var directionsDisplay = new google.maps.DirectionsRenderer({
    draggable: true,
    map: map,
    panel: document.getElementById('right-panel')
  });
  
  
  <!-- Add in here-->
  var origin_input = document.getElementById('origin-input');
  var destination_input = document.getElementById('destination-input');
  
  map.controls[google.maps.ControlPosition.TOP_LEFT].push(origin_input);
  map.controls[google.maps.ControlPosition.TOP_LEFT].push(destination_input);
  <!--End here-->
  

  directionsDisplay.addListener('directions_changed', function() {
    computeTotalDistance(directionsDisplay.getDirections());
  });

  displayRoute(origin_place_id, destination_place_id, directionsService,
      directionsDisplay);
}

function displayRoute(origin, destination, service, display) {
  service.route({
    origin: origin,
    destination: destination,
    waypoints: [],
    travelMode: google.maps.TravelMode.BICYCLING,
    avoidTolls: true
  }, function(response, status) {
    if (status === google.maps.DirectionsStatus.OK) {
      display.setDirections(response);
    } else {
      alert('Could not display directions due to: ' + status);
    }
  });
}

function computeTotalDistance(result) {
  var total = 0;
  var myroute = result.routes[0];
  for (var i = 0; i < myroute.legs.length; i++) {
    total += myroute.legs[i].distance.value;
  }
  total = total / 1000;
  document.getElementById('total').innerHTML = total + ' km';
}