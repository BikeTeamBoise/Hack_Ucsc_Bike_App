package hack16.hackathon;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.location.Location;

import android.content.IntentSender;
import android.view.View;

import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener,ActivityCompat.OnRequestPermissionsResultCallback {

    private static final LatLng LOWER_MANHATTAN = new LatLng(40.722543, -73.998585);
    private static final LatLng TIMES_SQUARE = new LatLng(40.7577, -73.9857);
    private static final LatLng BROOKLYN_BRIDGE = new LatLng(40.7057, -73.9964);

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private final ArrayList<LatLng> points = new ArrayList<>();
    public static final String TAG = MapsActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        googleMap = mapFragment.getMap();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if(getIntent().hasExtra("lat_1")){
            clear();
            LatLng l = new LatLng(getIntent().getDoubleExtra("lat_1",0), getIntent().getDoubleExtra("long_1",0));
            points.add(l);
            l = new LatLng(getIntent().getDoubleExtra("lat_2",0), getIntent().getDoubleExtra("long_2",0));
            points.add(l);
            String url = getMapsApiDirectionsUrl(points);
            ReadTask downloadTask = new ReadTask();
            downloadTask.execute(url);
        }

// Setting a click event handler for the map
        googleMap.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                points.add(latLng);
                // Clears the previously touched position


                // Animating to the touched position
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                googleMap.addMarker(markerOptions);
            }
        });
    }

    private String getMapsApiDirectionsUrl(ArrayList<LatLng> points){
        String origin = "origin="+points.get(0).latitude+","+points.get(0).longitude;
        String destination= "destination="+points.get(points.size()-1).latitude+","+points.get(points.size()-1).longitude;
        String waypoints = "waypoints=";
        for(int i=1; i<points.size()-1;i++){
            if(i==points.size()-1){
                waypoints = waypoints+points.get(i).latitude+","+points.get(i).longitude;
            }
            else {
                waypoints = waypoints + points.get(i).latitude+","+points.get(i).longitude + "|";
            }
        }

        /*String waypoints = "waypoints="
                + LOWER_MANHATTAN.latitude+","+LOWER_MANHATTAN.longitude
                +"|"+ BROOKLYN_BRIDGE.latitude+","+BROOKLYN_BRIDGE.longitude
                +"|"+TIMES_SQUARE.latitude+","+TIMES_SQUARE.longitude;
        String origin = "origin=Brooklyn";
        String destination = "destination=Queens";*/
        String mode = "mode=bicycling";
        String key = "AIzaSyA4sV9WEO9GRF42EsvaO-9WDq9iWFMrpao";
        String params = origin+"&"+waypoints+"&"+destination+"&"+mode+"&key="+key;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                +output+"?"+params;
        return url;
    }

    /**
     * private void addMarkers() {
     if (googleMap != null) {
     googleMap.addMarker(new MarkerOptions().position(BROOKLYN_BRIDGE)
     .title("First Point"));
     googleMap.addMarker(new MarkerOptions().position(LOWER_MANHATTAN)
     .title("Second Point"));
     googleMap.addMarker(new MarkerOptions().position(WALL_STREET)
     .title("Third Point"));
     }
     }
     */

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url){
            String data = "";
            try{
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            }
            catch(IOException e){
                return "Unable to retrieve web page.";
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>>{
        @Override
        protected List<List<HashMap<String,String>>> doInBackground(String... jsonData){
            JSONObject jo;
            List<List<HashMap<String,String>>> routes = null;

            try{
                jo = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jo);
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        protected void onPostExecute(List<List<HashMap<String,String>>> routes){
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(5);
                polyLineOptions.color(Color.BLUE);
            }
            try {
                googleMap.addPolyline(polyLineOptions);
            }
            catch(Exception e){
                System.out.println("Dont go to africa");
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }
        // Add a marker in Santa Cruz and move the camera
       /*
        LatLng santaCruz = new LatLng(36.9719, -122.0264);

        mMap.addMarker(new MarkerOptions().position(santaCruz).title("Marker in Santa Cruz"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(santaCruz));*/
        setUpMapIfNeeded();

    }

    @Override
    /**Whenever a new location is detected*/
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    @Override
    /**When the client finally connects to location services */
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(location);
        }
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString()); /**see if a actual location is printed or nothing */

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!");
        /*mMap.addMarker(options);*/
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }
    @Override
    /**logs*/
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    /**incase error has resolution*/
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            //LatLng santaCruz = new LatLng(36.9719, -122.0264);

          //  mMap.addMarker(new MarkerOptions().position(santaCruz).title("Marker in Santa Cruz"));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(santaCruz));
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }
    /*called if mMap is not null - puts marker down*/
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(37.00221,-122.055745)).title("Upper Campus Trail"));
        //mMap.addMarker(new MarkerOptions().position(TIMES_SQUARE).title("Times_square"));
        //mMap.addMarker(new MarkerOptions().position(LOWER_MANHATTAN).title("Lower manhattan"));
        //mMap.addMarker(new MarkerOptions().position(BROOKLYN_BRIDGE).title("Brooklyn bridge"));

    }
    /*sets up map if possible*/
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (googleMap != null) {
                setUpMap();
                //addLines();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    //Button click action for the temporary button
    public void goToRoute(View view){
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }
    public void funcEraseMarkers(View view){
        clear();
    }
    public void funcMakeWaypoint(View view){
        if(points.size() >= 2) {
            String url = getMapsApiDirectionsUrl(points);
            ReadTask downloadTask = new ReadTask();
            downloadTask.execute(url);
            googleMap.addMarker(new MarkerOptions().position(points.get(0)).title("Start"));
            googleMap.addMarker(new MarkerOptions().position(points.get(points.size() - 1)).title("Finish"));

            Database db = new Database(this);

            db.addRoute();
            db.addCustomRoute(points.get(0));
            db.addCustomRoute(points.get(points.size() - 1));

            clear();
        }
    }

    public void clear(){
        googleMap.clear();
        points.clear();
    }
}
