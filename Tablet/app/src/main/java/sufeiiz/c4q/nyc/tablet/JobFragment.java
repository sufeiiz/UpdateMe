package sufeiiz.c4q.nyc.tablet;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;

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
 * Created by sufeizhao on 7/23/15.
 */
public class JobFragment extends Fragment {

    private ArrayList<JobPosition> jobList;
    protected String title, company;
    protected LinearLayout layout;
    protected ListView list;
    ListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.job_layout, container, false);

        list = (ListView) v.findViewById(R.id.list);

        JobSearchAsync jobSearchAsync = new JobSearchAsync(v.getContext());
        jobSearchAsync.execute("java");

        return v;
    }

    public void openUrl(int position) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(jobList.get(position).getLink()));
        startActivity(browserIntent);
    }

    public class JobSearchAsync extends AsyncTask<String, Void, ArrayList<JobPosition>> {

        //the base string that will be enlarged bt user input
        private final String defaultString = "https://jobs.github.com/positions.json?description=";
        private ArrayList<JobPosition> arrayJobs;
        private Context context;

        public JobSearchAsync (Context context){
            this.context = context;
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

            String[] columns = new String[] { "_id", "title", "company" };
            int[] to = new int[] { R.id.title, R.id.company };

            MatrixCursor cursor = new MatrixCursor(columns);

            for (JobPosition job : jobPositions) {
                cursor.addRow(new Object[] { 1, job.getTitle(), job.getCompany()});
            }

            adapter = new SimpleCursorAdapter(context, R.layout.job_list, cursor,
                    new String[] {"title", "company"}, to);
            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    openUrl(position);
                }
            });
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

}
