package nyc.c4q.syd.updateme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
    public static final String PREFS_NAME = "Settings";
    private final String TAG = "SharedPref";
    public SharedPreferences preferences = null;
    public SharedPreferences.Editor editor;
    private final String HOME = "home";
    private final String WORK = "work";
    private final String MODE = "mode";
    private final String RUN = "rundirection";
    private String mode;

    private GoogleApiClient client;
    private LatLng markerLatLng, locationLatLng;
    private GoogleMap map;
    private Location location;
    private boolean hasMarker = false, hasSavedAdd = false;
    private Marker mark;
    private LocationRequest mLocationRequest;
    private DirectionsFetcher df;
    private TextView info;
    private String origin;
    private String destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cardview);
        setUpMapIfNeeded();

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        editor = preferences.edit();
        info = (TextView) findViewById(R.id.map_info);

        // Connect to Geolocation API to make current location request
        buildGoogleApiClient();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)        // 10 seconds, in milliseconds
                .setFastestInterval(1000); // 1 second, in milliseconds

        // Create MapFragment based on map xml
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button button = (Button) findViewById(R.id.change_destination);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, nyc.c4q.syd.updateme.Places.class);
                startActivity(intent);
            }
        });


    }

    public void loadState() {
        Log.d(TAG, "loadState()");

        // if home is null
        if (preferences.getString(HOME, "").isEmpty() &&
                preferences.getString(WORK, "").isEmpty()) {
            info.setText("Set your destination in Settings ->");
            hasSavedAdd = false;
        } else
            hasSavedAdd = true;

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(RUN, hasSavedAdd);
        editor.apply();
        preferences.getBoolean(RUN, false);
        mode = preferences.getString(MODE, "car");

        if (preferences.getBoolean("hasChanged", false))
            map.clear();
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

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                markerLatLng = marker.getPosition();
                markerDialog();
                return true;
            }
        });
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
    public void onConnectionSuspended(int i) {
    }

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
            Log.i("MAP", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    private void handleNewLocation(Location location) {
        Log.d("MAP", location.toString());

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        locationLatLng = new LatLng(latitude, longitude);
        origin = latitude + "," + longitude;

        // Set initial view to current location
        map.moveCamera(CameraUpdateFactory.newLatLng(locationLatLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(18));

        if (hasSavedAdd) {
            String temp;
            if (!preferences.getString(HOME, "").isEmpty() && !preferences.getString(WORK, "").isEmpty()) {
                long time = System.currentTimeMillis();
                if (time > 43200000)
                    temp = preferences.getString(HOME, "");
                else
                    temp = preferences.getString(WORK, "");
            } else if (preferences.getString(HOME, "").isEmpty())
                temp = preferences.getString(WORK, "");
            else
                temp = preferences.getString(HOME, "");

            destination = temp.replaceAll(" ", "+");
            df = new DirectionsFetcher(info, map, origin, destination, mode);
            df.execute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        client.connect();
        loadState();
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


    private void markerDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Choose an Option:");
        final String[] items = {"Directions to", "Save as Home", "Save as Work"};
        dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which].equalsIgnoreCase("Directions to")) {
                    destination = markerLatLng.latitude + "," + markerLatLng.longitude;
                    df = new DirectionsFetcher(info, map, origin, destination, mode);
                    df.execute();
                } else if (items[which].equalsIgnoreCase("Save as Home")) {
                    editor.putString(HOME, markerLatLng.latitude + "," + markerLatLng.longitude);
                    editor.apply();
                    Toast.makeText(MainActivity.this, "Location has been saved as Home", Toast.LENGTH_LONG).show();
                } else {
                    editor.putString(WORK, markerLatLng.latitude + "," + markerLatLng.longitude);
                    editor.apply();
                    Toast.makeText(MainActivity.this, "Location has been saved as Home", Toast.LENGTH_LONG).show();
                }
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
}
