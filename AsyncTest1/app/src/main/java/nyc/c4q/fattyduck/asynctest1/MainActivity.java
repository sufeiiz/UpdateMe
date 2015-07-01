package nyc.c4q.fattyduck.asynctest1;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    String[] texts = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "b", "c", "d", "e", "f", "g", "h", "i",
            "j", "k", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "b",
            "c", "d", "e", "f", "g", "h", "i", "j", "k", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "b", "c", "d", "e",
            "f", "g", "h", "i", "j", "k", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "b", "c", "d", "e", "f", "g", "h",
            "i", "j", "k", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
    ListView mainlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_main);
        mainlist = (ListView) findViewById(R.id.listView);
        mainlist.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>()));
        new myTask().execute();

    }

    class myTask extends AsyncTask<Void, String, Void>{
        ArrayAdapter<String> adapter;
        private int count = 0;
        @Override
        protected void onPreExecute() {
          adapter = (ArrayAdapter<String>) mainlist.getAdapter();
            setProgressBarIndeterminate(false);
            setProgressBarVisibility(true);
        }

        @Override
        protected Void doInBackground(Void... Voids) {
            for(String item: texts){
                publishProgress(item);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            adapter.add(values[0]);
            count++;
            setProgress((int)((double)(count/texts.length)*10000));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setProgressBarVisibility(false);
        }
    }
}
