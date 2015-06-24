package nyc.c4q.syd.updateme;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends ActionBarActivity{

    static String yahooStockInfo = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20" +
            "yahoo.finance.quote%20where%20symbol%20in%20(%22MSFT%22)&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org" +
            "%2Falltableswithkeys&callback=";
    static String stockSymbol = "";
    static String stockChange = "";
    static String stockPrice = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new myAsyncTask.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private class myAsyncTask extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
            HttpPost httpPost = new HttpPost(yahooStockInfo);
            httpPost.setHeader("Content-type", "application-json");
            InputStream inputStream = null;
            String result = null;

            try{
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder theStringBuilder = new StringBuilder();
                String line = null;

                while ((line=reader.readLine())!=null){
                    theStringBuilder.append(line+"\n");
                }
                result = theStringBuilder.toString();
            }catch (Exception e){
                e.printStackTrace();
            }
            finally {
                try{
                    if(inputStream!=null) inputStream.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            JSONObject jsonObject;

            try{
                result.substring(7);
                result = result.substring(0,result.length()-2);
                //Log.v()
                jsonObject = new JSONObject(result);
                JSONObject queryJSONObject = jsonObject.getJSONObject("query");
                JSONObject resultJSONObject = queryJSONObject.getJSONObject("results");
                JSONObject quoteJSONObject = resultJSONObject.getJSONObject("quote");
            }catch (Exception e ){

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }
}
