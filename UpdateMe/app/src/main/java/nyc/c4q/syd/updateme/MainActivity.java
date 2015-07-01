package nyc.c4q.syd.updateme;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements JobSearchAsync.MyListener {

    private ArrayList<JobPosition> jobList;
    private JobCard jobCard;
    private MainAdapter adapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set a progress bar for jobs loading
        progressBar = (ProgressBar) findViewById(R.id.progress);
        //create a jobList container for data which will get returned from jobAsync
        jobList = new ArrayList<JobPosition>();
        StockCard stockCard = new StockCard();
        //start jobs JSON parsing and fetching the data for the default java positions
        JobSearchAsync jobSearchAsync = new JobSearchAsync(this);
        jobSearchAsync.setListener(this);
        jobSearchAsync.execute("java");

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

