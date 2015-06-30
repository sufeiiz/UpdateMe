package nyc.c4q.syd.updateme;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sufeizhao on 6/27/15.
 */
public class DirectionsFetcher extends AsyncTask<URL, GoogleMap, Void> {

    private static final String API_KEY = "&key=AIzaSyDTaAeiCfVCXJhdweubPkgIvsni3s1-9ss";
    private List<LatLng> latLngs = new ArrayList<>();
    private String url = "https://maps.googleapis.com/maps/api/directions/json?";
    private String origin = "origin=";
    private String destination = "&destination=";
    private String mode = "&mode=";
    private String time, miles;
    private GoogleMap map;
    private TextView info;

    public DirectionsFetcher(TextView info, GoogleMap map, String origin, String destination, String mode) {
        this.info = info;
        this.map = map;
        this.origin += origin;
        this.destination += destination;
        this.mode += mode;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        map.clear();
    }

    protected Void doInBackground(URL... urls) {
        String input = readURL();
        Log.d("Map", "Parsing JSON");

        try {
            // read polyline points
            JSONObject reader = new JSONObject(input);
            JSONArray routes = reader.getJSONArray("routes");
            JSONObject list = routes.getJSONObject(0);
            JSONObject overview = list.getJSONObject("overview_polyline");
            String points = overview.getString("points");
            latLngs = PolyUtil.decode(points);

            // read time & distance
            JSONArray legs = list.getJSONArray("legs");
            JSONObject list2 = legs.getJSONObject(0);
            JSONObject distance = list2.getJSONObject("distance");
            miles = distance.getString("text");
            JSONObject duration = list2.getJSONObject("duration");
            time = duration.getString("text");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Void result) {
        Log.d("Map", "Adding polyline");
        addPolylineToMap(latLngs);
        Log.d("Map", "Fix Zoom");
        fixZoomForLatLngs(map, latLngs);
        info.setText("It will take " + time + " to get to your destination. Total distance: " + miles);
    }

    public void addPolylineToMap(List<LatLng> latLngs) {
        PolylineOptions options = new PolylineOptions()
                .width(10)
                .color(Color.parseColor("#009688"));

        for (LatLng latLng : latLngs)
            options.add(latLng);

        map.addPolyline(options);

        // add marker at destionation
        LatLng dest = latLngs.get(latLngs.size()-1);
        map.addMarker(new MarkerOptions().position(dest));
    }

    public static void fixZoomForLatLngs(GoogleMap map, List<LatLng> latLngs) {
        if (latLngs != null && latLngs.size() > 0) {
            LatLngBounds.Builder bc = new LatLngBounds.Builder();

            for (LatLng latLng : latLngs)
                bc.include(latLng);

            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50));
        }
    }

    public String readURL() {
        url = url + origin + destination + mode + API_KEY;

        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            org.apache.http.HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}
