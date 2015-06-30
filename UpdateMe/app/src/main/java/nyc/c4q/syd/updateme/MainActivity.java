package nyc.c4q.syd.updateme;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by July on 6/26/15.
 */
public class MainActivity extends Activity implements JobSearchAsync.MyListener {

    private ArrayList<JobPosition> jobList;
    private JobCard jobCard;
    private MainAdapter adapter;
    private ProgressBar progressBar;
    public ArrayList<StockInfo> stockList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set a progress bar for jobs loading
        progressBar = (ProgressBar) findViewById(R.id.progress);
        //create a jobList container for data which will get returned from jobAsync
        jobList = new ArrayList<JobPosition>();
        stockList = new ArrayList<>();

        StockCard stockCard = new StockCard("null", "null", "null", stockList);
        //start jobs JSON parsing and fetching the data for the default java positions
        JobSearchAsync jobSearchAsync = new JobSearchAsync(this);
        jobSearchAsync.setListener(this);
        jobSearchAsync.execute("java");

        new StockInfoAsync(this).execute();
        //create a list of different card types
        ArrayList<Card> cards = new ArrayList<Card>();
        ToDoCard todoCard = new ToDoCard("Items");
        jobCard = new JobCard("Software Developer", "Google", jobList);
        MapCard mapCard = new MapCard("Map");
        cards.add(todoCard);
        cards.add(jobCard);
        cards.add(mapCard);
        cards.add(stockCard);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new MainAdapter(this, cards);
        recyclerView.setAdapter(adapter);
    }

    //method to get Data from jobAsync and update
    @Override
    public void onLoadComplete(List<JobPosition> jobs) {
        //setter in JobCard to update List
        jobCard.setJobArray(jobs);
        //very important! when all the Recycler View is set up, it is not populated bc jobAsync didn't finish parsing and returning data
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.INVISIBLE);
    }
}

