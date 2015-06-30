package nyc.c4q.syd.updateme;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by July on 6/26/15.
 */
public class JobSearchAsync extends AsyncTask<String, Void, ArrayList<JobPosition>> {
    //the base string that will be enlarged bt user input
    private final String defaultString = "https://jobs.github.com/positions.json?description=";
    private ArrayList<JobPosition> arrayJobs;
    private Context context;

    public JobSearchAsync (Context context){
        this.context = context;
    }

    //create a listener interface to know when jobAsync is done loading data
    public interface MyListener {
        void onLoadComplete(List<JobPosition> jobs);
    }

    private MyListener listener;

    public void setListener(MyListener listener) {
        this.listener = listener;
    }

    @Override
    protected ArrayList<JobPosition> doInBackground(String... params) {
        String line, job;
        arrayJobs = new ArrayList<>();

        //if the user specifies his position and location
        if (null != params && params.length > 0) {
            job = params[0];
        } else {
            //default position when the user doesn't put in anything
            job = "java";
        }
        try {
            URL urlString = new URL(defaultString + job);
            HttpsURLConnection connection = (HttpsURLConnection) urlString.openConnection();
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            //raw JSON string
            String resultString = stringBuilder.toString();
            arrayJobs = parseJSON(resultString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayJobs;

    }

    @Override
    protected void onPostExecute(ArrayList<JobPosition> jobPositions) {
        super.onPostExecute(jobPositions);
        if (listener != null) {
            listener.onLoadComplete(jobPositions);
        }
    }

    //method to parse JSON
    public ArrayList<JobPosition> parseJSON(String rawString) throws JSONException {
        JSONArray jsonArray = new JSONArray(rawString);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject c = jsonArray.getJSONObject(i);
            String title = c.getString("title");
            String company = c.getString("company");
            String link = c.getString("url");
            arrayJobs.add(new JobPosition(title, company, link));
        }
        return arrayJobs;
    }
}