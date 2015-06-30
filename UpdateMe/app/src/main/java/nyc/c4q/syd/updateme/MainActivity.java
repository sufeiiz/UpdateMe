package nyc.c4q.syd.updateme;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
    public static final String KEY_QUERY = "query";
    public static final String KEY_RESULTS = "results";
    public static final String KEY_QUOTE = "quote";


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
    public class StockInfoAsync extends AsyncTask<String, Void, ArrayList<StockInfo>> {
        private MainActivity _activity;

        public StockInfoAsync(MainActivity activity) {
            this._activity = activity;
        }

        String string = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quote%20where" +
                "%20symbol%20in%20(%22YHOO%22%2C%22AAPL%22%2C%22GOOG%22%2C%22MSFT%22)&format=json&diagnostics=true&env=store" +
                "%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
        String results;
        ArrayList<StockInfo> stockArray = new ArrayList<StockInfo>();
        @Override
        protected ArrayList<StockInfo> doInBackground(String... strings) {

            BufferedReader reader;
            String line;

            try {
                URL url = new URL(string);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                StringBuilder stringBuilder = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line=reader.readLine())!=null){
                    stringBuilder.append(line+"\n");
                }
                results = stringBuilder.toString();
                if(results!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(results);
                        JSONObject queryJSON = jsonObject.getJSONObject(KEY_QUERY);
                        JSONObject resultsJSON = queryJSON.getJSONObject(KEY_RESULTS);
                        JSONArray quoteArray = resultsJSON.getJSONArray(KEY_QUOTE);

                        for(int i = 0; i<quoteArray.length(); i++){
                            JSONObject j = quoteArray.getJSONObject(i);
                            stockArray.add(new StockInfo("Symbol: "+j.getString("symbol"),
                                    "Price: "+j.getString("LastTradePriceOnly"),
                                    "Change: "+j.getString("Change")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<StockInfo> stockInfos) {
            super.onPostExecute(stockInfos);
            for(int i =0; i<stockArray.size(); i++) {
                _activity.stockList.add(stockArray.get(i));
            }
        }
    }

}

