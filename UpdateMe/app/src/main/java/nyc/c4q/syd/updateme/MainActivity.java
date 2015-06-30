package nyc.c4q.syd.updateme;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by July on 6/26/15.
 */
public class MainActivity extends Activity implements JobSearchAsync.MyListener {

    private ArrayList<JobPosition> jobList;
    private JobCard jobCard;
    private StockCard stockCard;
    private MainAdapter adapter;
    private GoogleApiClient client;
    public ArrayList<StockInfo> stockList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create a list of different card types
        ArrayList<Card> cards = new ArrayList<Card>();
        jobList = new ArrayList<JobPosition>();
        stockList = new ArrayList<StockInfo>();
        new StockInfoAsync(this).execute();


        JobSearchAsync jobSearchAsync = new JobSearchAsync();
        jobSearchAsync.setListener(this);
        jobSearchAsync.execute("java");


        jobCard = new JobCard("Software Developer", "Google", jobList);
        MapCard mapCard = new MapCard("Map");
        stockCard = new StockCard("null", "null", "null", stockList);

        cards.add(jobCard);
        cards.add(mapCard);
        cards.add(stockCard);

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

