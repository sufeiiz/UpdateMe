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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create a list of different card types
        ArrayList<Card> cards = new ArrayList<Card>();
        jobList = new ArrayList<JobPosition>();

        JobSearchAsync jobSearchAsync = new JobSearchAsync();
        jobSearchAsync.setListener(this);
        jobSearchAsync.execute("java");

        ToDoCard todoCard = new ToDoCard("Items");
        jobCard = new JobCard("Software Developer", "Google", jobList);
        MapCard mapCard = new MapCard("Map");
        cards.add(todoCard);
        cards.add(jobCard);
        cards.add(mapCard);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new MainAdapter(this, cards);
        recyclerView.setAdapter(adapter);



    }

    @Override
    public void onLoadComplete(List<JobPosition> jobs) {
        Log.d("yuliya", jobs.size() + "");
        jobCard.setJobArray(jobs);
        adapter.notifyDataSetChanged();
    }


}

