package sufeiiz.c4q.nyc.tablet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by sufeizhao on 7/24/15.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient client;
    private static final String PREFS_NAME = "Settings";
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final String HOME = "home", WORK = "work", MODE = "mode", RUN = "rundirection";
    public SharedPreferences preferences = null;
    public SharedPreferences.Editor editor;
    private String mode, origin, destination;
    private LatLng markerLatLng, locationLatLng;
    private GoogleMap map;
    private Location location;
    private boolean hasMarker = false, hasSavedAdd = false;
    private Marker mark;
    private LocationRequest mLocationRequest;
    private DirectionsFetcher df;
    private TextView info;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_layout, container, false);

        preferences = v.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        info = (TextView) v.findViewById(R.id.map_info);

        // Connect to Geolocation API to make current location request
        buildGoogleApiClient();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)        // 10 seconds, in milliseconds
                .setFastestInterval(1000); // 1 second, in milliseconds

        // Create MapFragment based on map xml
        com.google.android.gms.maps.MapFragment mapFragment = (com.google.android.gms.maps.MapFragment) ((Activity) v.getContext()).getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        map = mapFragment.getMap();
        loadState();

        ImageButton settings = (ImageButton) v.findViewById(R.id.change_destination);
        settings.setOnClickListener(new settingsListener(v.getContext()));

        return v;
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        map.getUiSettings().setMapToolbarEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(true);
        map.getUiSettings().setTiltGesturesEnabled(true);
        map.getUiSettings().setScrollGesturesEnabled(true);
        map.getUiSettings().setRotateGesturesEnabled(true);
        map.setMyLocationEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // create new marker when map is clicked, manipulated to allow only 1 marker
        map.setOnMapClickListener(createMarkerListener);
        map.setOnMarkerClickListener(new onClickMarkerListner(this.getActivity()));
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
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(((Activity) this.getActivity()), CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("Map", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }


    @Override // must override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    private void handleNewLocation(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        locationLatLng = new LatLng(latitude, longitude);
        origin = latitude + "," + longitude;

        // Set initial view to current location
        map.moveCamera(CameraUpdateFactory.newLatLng(locationLatLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));

        if (hasSavedAdd) {
            Log.d("Map", "Has Saved Address - loading destination");
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

    //TODO toggle work/home
    // Load SharedPreference
    public void loadState() {
        Log.d("Map", "loadState()");

        // if home is null
        if (preferences.getString(HOME, "").isEmpty() &&
                preferences.getString(WORK, "").isEmpty()) {
            info.setText("Set your destination in Settings →");
            hasSavedAdd = false;
        } else
            hasSavedAdd = true;

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(RUN, hasSavedAdd).apply();
        preferences.getBoolean(RUN, false);
        mode = preferences.getString(MODE, "car");

        if (preferences.getBoolean("hasChanged", false))
            map.clear();
    }

    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(this.getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        client.connect();
        Log.d("Map", "Connected to Google API Client");
    }

    // start settings activity
    public class settingsListener implements View.OnClickListener {
        private Context c;

        public settingsListener(Context c) {
            this.c = c;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(c, MapSettings.class);
            c.startActivity(intent);
        }
    };

    // create new marker and remove old marker
    GoogleMap.OnMapClickListener createMarkerListener = new GoogleMap.OnMapClickListener() {
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
    };

    // option to get directions to marker, or save marker in settings
    public class onClickMarkerListner implements GoogleMap.OnMarkerClickListener {
        private Context context;

        public onClickMarkerListner(Context context) {
            this.context = context;
        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            markerLatLng = marker.getPosition();
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            final String[] items = {"Directions to", "Save as Home", "Save as Work"};
            dialogBuilder.setTitle("Choose an Option:")
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (items[which].equalsIgnoreCase("Directions to")) {
                                destination = markerLatLng.latitude + "," + markerLatLng.longitude;
                                df = new DirectionsFetcher(info, map, origin, destination, mode);
                                df.execute();
                            } else if (items[which].equalsIgnoreCase("Save as Home")) {
                                editor.putString(HOME, markerLatLng.latitude + "," + markerLatLng.longitude);
                                editor.apply();
                                Toast.makeText(context, "Location has been saved as Home", Toast.LENGTH_LONG).show();
                            } else {
                                editor.putString(WORK, markerLatLng.latitude + "," + markerLatLng.longitude);
                                editor.apply();
                                Toast.makeText(context, "Location has been saved as Home", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
            return true;
        }
    };
}
