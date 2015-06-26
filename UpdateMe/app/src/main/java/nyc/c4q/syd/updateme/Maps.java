package nyc.c4q.syd.updateme;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.plus.Plus;

public class Maps extends MapFragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final LatLng SAN_FRAN = new LatLng(37.765927, -122.449972);
    private String API_KEY = "AIzaSyDTaAeiCfVCXJhdweubPkgIvsni3s1-9ss";
    private GoogleMap googleMap;
    private LatLng pin, location;
    private boolean hasMarker = false;
    private Marker mark;
    private GoogleApiClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new GoogleApiClient.Builder(getActivity())
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
        client.connect();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cardview, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        googleMap = this.getMap();
        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13));
        GoogleMapOptions options = new GoogleMapOptions();
        options.compassEnabled(true)
                .mapType(GoogleMap.MAP_TYPE_NORMAL)
                .rotateGesturesEnabled(true)
                .scrollGesturesEnabled(true)
                .tiltGesturesEnabled(true)
                .zoomControlsEnabled(true)
                .zoomGesturesEnabled(true);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (!hasMarker) {
                    mark = googleMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
                    hasMarker = true;
                } else {
                    mark.remove();
                    mark = googleMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
                }
                pin = latLng;
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(40.7127, 74.0059))
                .title("Marker"));
    }


    @Override
    public void onConnected(Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(client);
        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            LatLng location = new LatLng(latitude, longitude);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 18.0f));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("Suspended", "Connection Suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("Failed", "Connection Failed");
    }

    // https://maps.googleapis.com/maps/api/directions/json?origin=longlat&destination=longlat&key=API_KEY

        // settings for map view
//        GoogleMapOptions options = new GoogleMapOptions();
//        options.mapType(GoogleMap.MAP_TYPE_SATELLITE);
}
