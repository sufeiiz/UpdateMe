package nyc.c4q.syd.updateme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by July on 6/26/15.
 */
public class MainAdapter extends RecyclerView.Adapter {
    private List<Card> cardsArray;
    private Context context;
    private List<JobPosition> jobs;

    public MainAdapter(Context context, List<Card> cardsArray) {
        this.context = context;
        this.cardsArray = cardsArray;
    }

    /* TO-DO LIST */
    public class ToDoViewHolder extends RecyclerView.ViewHolder {

        private ArrayList<String> items;
        private ArrayAdapter<String> itemsAdapter;
        private ListView lvItems;

        public ToDoViewHolder(View v) {
            super(v);

            lvItems = (ListView) v.findViewById(R.id.list);
            items = new ArrayList<>();
            readItems();
            itemsAdapter = new ArrayAdapter<>(context, R.layout.todo_textview, items);
            lvItems.setAdapter(itemsAdapter);
            setListViewHeightBasedOnChildren(lvItems);
            if (items.size() == 0)
                items.add("Add your first to do list now!");

            ImageButton add = (ImageButton) v.findViewById(R.id.add);
            add.setOnClickListener(addTODOListener);
            lvItems.setOnItemClickListener(lvItemClickListener);
        }

        // save and load items from to-do list
        private void readItems() {
            File filesDir = context.getFilesDir();
            File todoFile = new File(filesDir, "todo.txt");
            try {
                items = new ArrayList<>(FileUtils.readLines(todoFile));
            } catch (IOException e) {
                items = new ArrayList<>();
            }
        }

        private void writeItems() {
            File filesDir = context.getFilesDir();
            File todoFile = new File(filesDir, "todo.txt");
            try {
                FileUtils.writeLines(todoFile, items);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // add item to to-do list & adjust view size
        View.OnClickListener addTODOListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                final EditText todoET = new EditText(context);
                dialogBuilder.setTitle("Add Todo Task Item")
                        .setMessage("What is on your list today?")
                        .setView(todoET)
                        .setPositiveButton("Add Task", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String itemText = todoET.getText().toString();
                                items.add(itemText);
                                setListViewHeightBasedOnChildren(lvItems);
                            }
                        });
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                writeItems();
            }
        };

        // option to delete item from to-do list
        AdapterView.OnItemClickListener lvItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View item, final int pos, long id) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                dialogBuilder.setTitle("Remove Task")
                        .setMessage("Have you completed this task?")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                items.remove(pos);
                                itemsAdapter.notifyDataSetChanged();
                                writeItems();
                                setListViewHeightBasedOnChildren(lvItems);
                                Toast.makeText(context, "Well done!", Toast.LENGTH_LONG).show();
                            }
                        });
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
            }
        };
    }

    /* JOB VIEW */
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

        protected ImageView info;

        public JobViewHolder(View v) {
            super(v);
            title1 = (TextView) v.findViewById(R.id.title1);
            company1 = (TextView) v.findViewById(R.id.company1);
            cardView1 = (CardView) v.findViewById(R.id.card_view1);
            cardView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (jobs!=null && jobs.size()>0 && !MainActivity.notConnected) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(jobs.get(0).getLink()));
                    context.startActivity(browserIntent);
                    }
                }
            });

            title2 = (TextView) v.findViewById(R.id.title2);
            company2 = (TextView) v.findViewById(R.id.company2);
            cardView2 = (CardView) v.findViewById(R.id.card_view2);
            cardView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (jobs!=null && jobs.size()>0 && !MainActivity.notConnected) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(jobs.get(1).getLink()));
                    context.startActivity(browserIntent);
                    }
                }
            });

            title3 = (TextView) v.findViewById(R.id.title3);
            company3 = (TextView) v.findViewById(R.id.company3);
            cardView3 = (CardView) v.findViewById(R.id.card_view3);
            cardView3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (jobs!=null && jobs.size()>0 && !MainActivity.notConnected) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(jobs.get(2).getLink()));
                    context.startActivity(browserIntent);
                    }
                }
            });

            info = (ImageView) v.findViewById(R.id.info_icon);
            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, JobActivity.class);
                    context.startActivity(intent);

                }
            });

        }
    }

    /* GOOGLE MAP*/
    public class MapViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback,
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

        public MapViewHolder(View v) {
            super(v);
            preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            editor = preferences.edit();
            info = (TextView) v.findViewById(R.id.map_info);

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
            loadState();

            ImageButton settings = (ImageButton) v.findViewById(R.id.change_destination);
            settings.setOnClickListener(settingsListener);
        }

        @Override
        public void onMapReady(final GoogleMap map) {
            map.getUiSettings().setMapToolbarEnabled(true);
            map.setMyLocationEnabled(true);
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            GoogleMapOptions options = new GoogleMapOptions();
            options.compassEnabled(true);
            options.rotateGesturesEnabled(true);
            options.scrollGesturesEnabled(true);
            options.tiltGesturesEnabled(true);
            options.mapToolbarEnabled(true);

            // create new marker when map is clicked, manipulated to allow only 1 marker
            map.setOnMapClickListener(createMarkerListener);
            map.setOnMarkerClickListener(onClickMarkerListner);
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
                    connectionResult.startResolutionForResult(((Activity) context), CONNECTION_FAILURE_RESOLUTION_REQUEST);
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
            map.animateCamera(CameraUpdateFactory.zoomTo(17));

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
            client = new GoogleApiClient.Builder(context)
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
        View.OnClickListener settingsListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapSettings.class);
                context.startActivity(intent);
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
        GoogleMap.OnMarkerClickListener onClickMarkerListner = new GoogleMap.OnMarkerClickListener() {
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

    /* STOCK CARD */
    public class StockViewHolder extends RecyclerView.ViewHolder {

        protected TextView stockName1, stockName2, stockName3, stockName4;
        protected TextView stockPrice1, stockPrice2, stockPrice3, stockPrice4;
        protected TextView stockChange1, stockChange2, stockChange3, stockChange4;
        protected CardView cardView1, cardView2, cardView3, cardView4;

        public StockViewHolder(View v) {
            super(v);
            stockChange1 = (TextView) v.findViewById(R.id.stockChange1);
            stockChange2 = (TextView) v.findViewById(R.id.stockChange2);
            stockChange3 = (TextView) v.findViewById(R.id.stockChange3);
            stockChange4 = (TextView) v.findViewById(R.id.stockChange4);

            stockName1 = (TextView) v.findViewById(R.id.stockName1);
            stockName2 = (TextView) v.findViewById(R.id.stockName2);
            stockName3 = (TextView) v.findViewById(R.id.stockName3);
            stockName4 = (TextView) v.findViewById(R.id.stockName4);

            stockPrice1 = (TextView) v.findViewById(R.id.stockPrice1);
            stockPrice2 = (TextView) v.findViewById(R.id.stockPrice2);
            stockPrice3 = (TextView) v.findViewById(R.id.stockPrice3);
            stockPrice4 = (TextView) v.findViewById(R.id.stockPrice4);

            cardView1 = (CardView) v.findViewById(R.id.stockCardview1);
            cardView2 = (CardView) v.findViewById(R.id.stockCardview2);
            cardView3 = (CardView) v.findViewById(R.id.stockCardview3);
            cardView4 = (CardView) v.findViewById(R.id.stockCardview4);
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
        if (viewType == 0) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_layout, parent, false);
            return new ToDoViewHolder(itemView);
        }
        if (viewType == 1) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_layout, parent, false);
            return new JobViewHolder(itemView);
        }
        if (viewType == 2) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_layout, parent, false);
            return new MapViewHolder(itemView);
        }
        if (viewType == 3) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_layout, parent, false);
            return new StockViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 0) {
            ToDoCard todoCard = (ToDoCard) cardsArray.get(position);
            ToDoViewHolder todoHolder = (ToDoViewHolder) holder;
        }

        if (holder.getItemViewType() == 1) {

            JobViewHolder jobViewHolder = (JobViewHolder) holder;
            if (cardsArray != null && position < cardsArray.size()) {
                JobCard jobCard = (JobCard) cardsArray.get(position);

                jobs = jobCard.getJobArray();
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

        if (holder.getItemViewType() == 3) {
            StockViewHolder stockViewHolder = (StockViewHolder) holder;

            if (cardsArray != null && position < cardsArray.size()) {
                StockCard stockCard = (StockCard) cardsArray.get(position);
                List<StockInfo> stocks = stockCard.getStockArray();
                if (stocks.size() > 0) {
                    stockViewHolder.stockName1.setText(stocks.get(0).getSymbol());
                    stockViewHolder.stockPrice1.setText(stocks.get(0).getLastTradePriceOnly());
                    stockViewHolder.stockChange1.setText(stocks.get(0).getChange());
                    String str = stocks.get(0).getChange().substring(8);
                    double changes = Double.parseDouble(str);
                    if (changes > 0) {
                        stockViewHolder.stockChange1.setBackgroundColor(Color.GREEN);
                    }
                    if (changes < 0) {
                        stockViewHolder.stockChange1.setBackgroundColor(Color.RED);
                    }
                }
                if (stocks.size() > 1) {

                    stockViewHolder.stockName2.setText(stocks.get(1).getSymbol());
                    stockViewHolder.stockPrice2.setText(stocks.get(1).getLastTradePriceOnly());
                    stockViewHolder.stockChange2.setText(stocks.get(1).getChange());
                    String str = stocks.get(1).getChange().substring(8);
                    double changes = Double.parseDouble(str);
                    if (changes > 0) {
                        stockViewHolder.stockChange2.setBackgroundColor(Color.GREEN);
                    }
                    if (changes < 0) {
                        stockViewHolder.stockChange2.setBackgroundColor(Color.RED);
                    }
                }
                if (stocks.size() > 2) {

                    stockViewHolder.stockName3.setText(stocks.get(2).getSymbol());
                    stockViewHolder.stockPrice3.setText(stocks.get(2).getLastTradePriceOnly());
                    stockViewHolder.stockChange3.setText(stocks.get(2).getChange());
                    String str = stocks.get(2).getChange().substring(8);
                    double changes = Double.parseDouble(str);
                    if (changes > 0) {
                        stockViewHolder.stockChange3.setBackgroundColor(Color.GREEN);
                    }
                    if (changes < 0) {
                        stockViewHolder.stockChange3.setBackgroundColor(Color.RED);
                    }
                }
                if (stocks.size() > 3) {

                    stockViewHolder.stockName4.setText(stocks.get(3).getSymbol());
                    stockViewHolder.stockChange4.setText(stocks.get(3).getChange());
                    stockViewHolder.stockPrice4.setText(stocks.get(3).getLastTradePriceOnly());
                    String str = stocks.get(3).getChange().substring(8);
                    double changes = Double.parseDouble(str);
                    if (changes > 0) {
                        stockViewHolder.stockChange4.setBackgroundColor(Color.GREEN);
                    }
                    if (changes < 0) {
                        stockViewHolder.stockChange4.setBackgroundColor(Color.RED);
                    }
                }

            }
        }
    }

    @Override
    public int getItemCount() {
        return cardsArray.size();
    }

    // adjust listview height for to-to list
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT));
            }
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
