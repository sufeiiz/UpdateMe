package nyc.c4q.syd.updateme;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.util.Calendar;
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
            items = new ArrayList<String>();
            readItems();
            itemsAdapter = new ArrayAdapter<String>(context, R.layout.todo_list, R.id.text, items);
            lvItems.setAdapter(itemsAdapter);
            setListViewHeightBasedOnChildren(lvItems);
            if (items.size() == 0)
                items.add("Add your first to do list now!");

            ImageButton add = (ImageButton) v.findViewById(R.id.add);
            add.setOnClickListener(addTODOListener);
            lvItems.setOnItemClickListener(lvItemClickListener);
            lvItems.setOnItemLongClickListener(lvItemLongClickListener);
        }

        // save and load items from to-do list
        private void readItems() {
            File filesDir = context.getFilesDir();
            File todoFile = new File(filesDir, "todo.txt");
            try {
                items = new ArrayList<String>(FileUtils.readLines(todoFile));
            } catch (IOException e) {
                items = new ArrayList<String>();
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

        AdapterView.OnItemLongClickListener lvItemLongClickListener = (new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, long id) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                final DatePicker setDate = new DatePicker(context);
                setDate.setSpinnersShown(false);
                dialogBuilder.setTitle("Set Reminder on Date")
                        .setView(setDate)
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                notificationDate(items.get(position),
                                        setDate.getYear(), setDate.getMonth(), setDate.getDayOfMonth(),
                                        parent, position);
                            }
                        });
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                return true;
            }
        });

        public void notificationDate(final String task, final int year, final int month, final int day,
                                     final AdapterView<?> parent, final int position) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            final TimePicker setTime = new TimePicker(context);
            dialogBuilder.setTitle("Set Reminder at Time")
                    .setView(setTime)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Set Reminder", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            setNotification(task, year, month, day,
                                    setTime.getCurrentHour(), setTime.getCurrentMinute());
                            //TODO: make invisible again?
                            View image = parent.getChildAt(position);
                            image.findViewById(R.id.icon).setVisibility(View.VISIBLE);
                        }
                    });
            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
        }

        public void setNotification(String task, int year, int month, int day, int hour, int min) {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, min);
            long millis = cal.getTimeInMillis();

            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("task", task);
            PendingIntent mAlarmSender = PendingIntent.getBroadcast(context, 0, intent, 0);
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, millis, mAlarmSender);
        }
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

        private ArrayList<String> stocks;
        private ArrayAdapter<String> stockAdapter;
        private ListView lvStocks;

        public StockViewHolder(View v) {
            super(v);

            lvStocks = (ListView) v.findViewById(R.id.stockList);
            stocks = new ArrayList<String>();
            readItems();
            stockAdapter = new ArrayAdapter<String>(context, R.layout.stock_textview, stocks);
            lvStocks.setAdapter(stockAdapter);
            setListViewHeightBasedOnChildren(lvStocks);
            if (stocks.size() == 0)
                stocks.add("MSFT");

            ImageButton addStock = (ImageButton) v.findViewById(R.id.addStock);
            addStock.setOnClickListener(addStockListener);
            lvStocks.setOnItemClickListener(lvItemClickListener);
            lvStocks.setOnItemLongClickListener(lvItemLongClickListener);
        }

        // save and load items from stock list
        private void readItems() {
            File filesDir = context.getFilesDir();
            File todoFile = new File(filesDir, "stock.txt");
            try {
                stocks = new ArrayList<String>(FileUtils.readLines(todoFile));
            } catch (IOException e) {
                stocks = new ArrayList<String>();
            }
        }

        private void writeItems() {
            File filesDir = context.getFilesDir();
            File todoFile = new File(filesDir, "stock.txt");
            try {
                FileUtils.writeLines(todoFile, stocks);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // add item to stock list & adjust view size
        View.OnClickListener addStockListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                final EditText todoET = new EditText(context);
                dialogBuilder.setTitle("Enter Stock")
                        .setMessage("Which stock would you like to add today?")
                        .setView(todoET)
                        .setPositiveButton("Add Stock", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String itemText = todoET.getText().toString().toUpperCase();
                                stocks.add(itemText);
                                setListViewHeightBasedOnChildren(lvStocks);
                            }
                        });
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                writeItems();
            }
        };


        AdapterView.OnItemClickListener lvItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View item, final int pos, long id) {
                String stockSymbol = stocks.get(pos);
                Intent i = new Intent(context, StockInfoActivity.class);
                i.putExtra("stock", stockSymbol);
                context.startActivity(i);
            }
        };

        // option to delete item from stock list
        AdapterView.OnItemLongClickListener lvItemLongClickListener = new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                dialogBuilder.setTitle("Remove Stock?")
                        .setMessage("Are you sure?")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                stocks.remove(i);
                                stockAdapter.notifyDataSetChanged();
                                writeItems();
                                setListViewHeightBasedOnChildren(lvStocks);
                                Toast.makeText(context, "Stock Removed!", Toast.LENGTH_LONG).show();
                            }
                        });
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                return true;
            }

        };
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
            StockCard stockCard = (StockCard) cardsArray.get(position);
            StockViewHolder stockViewHolder = (StockViewHolder) holder;

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
