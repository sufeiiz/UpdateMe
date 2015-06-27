package nyc.c4q.syd.updateme;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by July on 6/26/15.
 */
public class MainAdapter extends RecyclerView.Adapter {
    private List<Card> cardsArray;
    private Context context;

    // MAP VARIABLES
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient client;
    private LatLng markerLatLng, locationLatLng;
    private GoogleMap map;
    private Location location;
    private boolean hasMarker = false;
    private Marker mark;
    private LocationRequest mLocationRequest;
    private DirectionsFetcher df;
    private TextView info;

    public MainAdapter(Context context, List<Card> cardsArray) {
        this.context = context;
        this.cardsArray = cardsArray;
    }


    //create viewHolder for every card
    public class JobViewHolder extends RecyclerView.ViewHolder {
        protected TextView title1;
        protected TextView company1;
        protected CardView cardView1;

        protected TextView title2;
        protected TextView company2;
        protected CardView cardView2;

        protected TextView title3;
        protected TextView company3;
        protected CardView cardView3;

        public JobViewHolder(View v) {
            super(v);
            title1 = (TextView) v.findViewById(R.id.title1);
            company1 = (TextView) v.findViewById(R.id.company1);
            cardView1 = (CardView) v.findViewById(R.id.card_view1);

            title2 = (TextView) v.findViewById(R.id.title2);
            company2 = (TextView) v.findViewById(R.id.company2);
            cardView2 = (CardView) v.findViewById(R.id.card_view2);

            title3 = (TextView) v.findViewById(R.id.title3);
            company3 = (TextView) v.findViewById(R.id.company3);
            cardView3 = (CardView) v.findViewById(R.id.card_view3);
        }
    }

    public class MapViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback,
            GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

        public MapViewHolder(View v) {
            super(v);

            // Connect to Geolocation API to make current location request
            buildGoogleApiClient();
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10000)        // 10 seconds, in milliseconds
                    .setFastestInterval(1000); // 1 second, in milliseconds

            // Create MapFragment based on map xml
            MapFragment mapFragment = (MapFragment) ((Activity) context).getFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            map = mapFragment.getMap();

            Button button = (Button) v.findViewById(R.id.change_destination);
            info = (TextView) v.findViewById(R.id.map_info);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: PLACES NOT DONE
                }
            });
        }

        @Override
        public void onMapReady(final GoogleMap map) {

            map.setMyLocationEnabled(true);
            map.getUiSettings().setMapToolbarEnabled(true);

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
                    connectionResult.startResolutionForResult(((Activity) context), CONNECTION_FAILURE_RESOLUTION_REQUEST);
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
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            locationLatLng = new LatLng(latitude, longitude);
            String origin = latitude + "," + longitude;

            // Set initial view to current location
            MarkerOptions options = new MarkerOptions()
                    .position(locationLatLng)
                    .title("I am here!");
            System.out.println("map: " + map);
            map.addMarker(options);
            map.moveCamera(CameraUpdateFactory.newLatLng(locationLatLng));
            map.animateCamera(CameraUpdateFactory.zoomTo(18));
            df = new DirectionsFetcher(info, map, origin, "new+york", "car");
            df.execute();
        }

        private void setUpMapIfNeeded() {
            // if map was not already instantiated, try to obtain the map from the MapFragment
            if (map == null)
                map = ((MapFragment) ((Activity) context).getFragmentManager().findFragmentById(R.id.map)).getMap();
        }

        protected synchronized void buildGoogleApiClient() {
            client = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();
            client.connect();
        }
    }

    //get type of card
    @Override
    public int getItemViewType(int position) {
        return cardsArray.get(position).getType();
    }

    //inflate layout for each card
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_layout, parent, false);
            return new JobViewHolder(itemView);
        }
        if (viewType == 2) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_layout, parent, false);
            return new MapViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 1) {

            JobViewHolder jobViewHolder = (JobViewHolder) holder;
            if (cardsArray != null && position < cardsArray.size()) {
                JobCard jobCard = (JobCard) cardsArray.get(position);

                List<JobPosition> jobs = jobCard.getJobArray();
                if (jobs.size() > 0) {
                    jobViewHolder.title1.setText("" + jobs.get(0).getTitle());
                    jobViewHolder.company1.setText("" + jobs.get(0).getCompany());
                }

                if (jobs.size() > 1) {
                    jobViewHolder.title2.setText("" + jobs.get(1).getTitle());
                    jobViewHolder.company2.setText("" + jobs.get(1).getCompany());
                }
                if (jobs.size() > 2) {
                    jobViewHolder.title3.setText("" + jobs.get(2).getTitle());
                    jobViewHolder.company3.setText("" + jobs.get(2).getCompany());
                }
            }

        }
        if (holder.getItemViewType() == 2) {
            MapCard mapCard = (MapCard) cardsArray.get(position);
            MapViewHolder mapHolder = (MapViewHolder) holder;
        }
    }

    @Override
    public int getItemCount() {
        return cardsArray.size();
    }
}
