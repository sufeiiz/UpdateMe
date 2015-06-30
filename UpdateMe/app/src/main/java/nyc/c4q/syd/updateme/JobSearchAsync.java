package nyc.c4q.syd.updateme;

import android.os.AsyncTask;
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
    private final String rawString = "https://jobs.github.com/positions.json?description=";

    public interface MyListener {
        void onLoadComplete(List<JobPosition> jobs);
    }

    private MyListener listener;

    public void setListener(MyListener listener) {
        this.listener = listener;
    }

    @Override
    protected ArrayList<JobPosition> doInBackground(String... params) {
        ArrayList<JobPosition> arrayJobs = new ArrayList<JobPosition>();
        BufferedReader reader;
        String line;




        String job = "java";
        if(null != params && params.length > 0) {
            job = params[0];
        }

        try {
            URL urlString = new URL(rawString + job);
            HttpsURLConnection connection = (HttpsURLConnection) urlString.openConnection();
            StringBuilder stringBuilder = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            String resultString = stringBuilder.toString();

            if (resultString != null) {
                try {
                    JSONArray jsonArray =  new JSONArray(resultString);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject c = jsonArray.getJSONObject(i);
                        String title = c.getString("title");
                        String company = c.getString("company");
                        String link = c.getString("url");
                        arrayJobs.add(new JobPosition(title, company, link));
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
        return arrayJobs;

    }

    @Override
    protected void onPostExecute(ArrayList<JobPosition> jobPositions) {
        super.onPostExecute(jobPositions);
        if(listener != null) {
            listener.onLoadComplete(jobPositions);
        }
    }
}