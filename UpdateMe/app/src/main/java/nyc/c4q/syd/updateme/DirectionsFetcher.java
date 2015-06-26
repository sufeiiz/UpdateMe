package nyc.c4q.syd.updateme;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.maps.android.PolyUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sufeizhao on 6/25/15.
 */
public class DirectionsFetcher extends AsyncTask<URL, Integer, Void> {

    private static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private List<LatLng> latLngs = new ArrayList<LatLng>();
    private String origin, destination;
    private boolean directionsFetched = false;
    private GoogleMap map;
    private Context context;

    public DirectionsFetcher(Context context, GoogleMap map, String origin, String destination) {
        this.context = context;
        this.map = map;
        this.origin = origin;
        this.destination = destination;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        map.clear();
    }

    protected Void doInBackground(URL... urls) {
        try {
            HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest request) {
                    request.setParser(new JsonObjectParser(JSON_FACTORY));
                }
            });

            GenericUrl url = new GenericUrl("http://maps.googleapis.com/maps/api/directions/json");
            url.put("origin", origin);
            url.put("destination", destination);
            url.put("sensor", false);

            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse httpResponse = request.execute();
            DirectionsResult directionsResult = httpResponse.parseAs(DirectionsResult.class);

            String encodedPoints = directionsResult.routes.get(0).overviewPolyLine.points;
            latLngs = PolyUtil.decode(encodedPoints);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;

    }

    protected void onProgressUpdate(Integer... progress) {
    }

    protected void onPostExecute(Void result) {
        directionsFetched = true;
        System.out.println("Adding polyline");
        addPolylineToMap(latLngs);
        System.out.println("Fix Zoom");
        fixZoomForLatLngs(map, latLngs);
        System.out.println("Start anim");
    }

    public static class DirectionsResult {
        // @Key("routes")
        public List<Route> routes;

    }

    public static class Route {
        // @Key("overview_polyline")
        public OverviewPolyLine overviewPolyLine;

    }

    public static class OverviewPolyLine {
        // @Key("points")
        public String points;

    }

    public void addPolylineToMap(List<LatLng> latLngs) {
        PolylineOptions options = new PolylineOptions();
        for (LatLng latLng : latLngs) {
            options.add(latLng);
        }
        map.addPolyline(options);
    }

    public static void fixZoomForLatLngs(GoogleMap googleMap, List<LatLng> latLngs) {
        if (latLngs != null && latLngs.size() > 0) {
            LatLngBounds.Builder bc = new LatLngBounds.Builder();

            for (LatLng latLng : latLngs) {
                bc.include(latLng);
            }

            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50), 4000, null);
        }
    }
}
