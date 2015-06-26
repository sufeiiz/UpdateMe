package nyc.c4q.syd.updateme;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Random;

/**
 * Created by sufeizhao on 6/21/15.
 */
public class MainActivity extends Activity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    // Define a request code to send to Google Play services
    // This code is returned in Activity.onActivityResult
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;

    private GoogleApiClient client;
    private LatLng markerLatLng, locationLatLng;
    private GoogleMap map;
    private Location location;
    private boolean hasMarker = false;
    private Marker mark;
    private final String TAG = "MAP";
    private LocationRequest mLocationRequest;
    private String DIRECTION_URL = "https://maps.googleapis.com/maps/api/directions/json?";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cardview);
        setUpMapIfNeeded();

        // Connect to Geolocation API to make current location request
        buildGoogleApiClient();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)        // 10 seconds, in milliseconds
                .setFastestInterval(1000); // 1 second, in milliseconds

        // Create MapFragment based on map xml
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap map) {

        map.setMyLocationEnabled(true);
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(, 13));

        // create new marker when map is clicked, manipulated to allow only 1 marker
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (!hasMarker) {
                    mark = map.addMarker(new MarkerOptions().position(latLng).draggable(true));
                    hasMarker = true;
                } else {
                    mark.remove();
                    mark = map.addMarker(new MarkerOptions().position(latLng).draggable(true));
                }
                markerLatLng = latLng;
            }
        });

        new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //TODO: directions to and from?
                return false;
            }
        };
    }



    // Override methods for Connection Call Back for Geolocation API
    @Override
    public void onConnected(Bundle bundle) {
        location = LocationServices.FusedLocationApi.getLastLocation(client);
        if (location == null)
            LocationServices.FusedLocationApi.requestLocationUpdates(client, mLocationRequest, this);
        else
            handleNewLocation(location);
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        locationLatLng = new LatLng(latitude, longitude);

        // Set initial view to current location
        MarkerOptions options = new MarkerOptions()
                .position(locationLatLng)
                .title("I am here!");
        map.addMarker(options);
        map.moveCamera(CameraUpdateFactory.newLatLng(locationLatLng));
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        client.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (client.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
            client.disconnect();
        }
    }

    private void setUpMapIfNeeded() {
        // if map was not already instantiated, try to obtain the map from the MapFragment
        if (map == null)
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
    }

    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }








    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
