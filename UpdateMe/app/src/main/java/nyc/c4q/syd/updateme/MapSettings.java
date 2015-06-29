package nyc.c4q.syd.updateme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sufeizhao on 6/29/15.
 */
public class MapSettings extends Activity {
    private static final String API_KEY = "AIzaSyDTaAeiCfVCXJhdweubPkgIvsni3s1-9ss";
    protected static final int RESULT_CODE = 123;
    private AutoCompleteTextView home;
    private AutoCompleteTextView work;
    private Spinner mode;
    public static final String PREFS_NAME = "Settings";
    private final String TAG = "SharedPref";
    public SharedPreferences preferences = null;
    private final String HOME = "home";
    private final String WORK = "work";
    private final String MODE = "mode";
    private final String CHANGE = "hasChanged";
    private String home_address = "";
    private String work_address = "";
    private String mode_saved = "";
    private boolean hasChanged;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.map_settings_layout);
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        loadState();

        Button btnLoadDirections = (Button) findViewById(R.id.load_directions);
        mode = (Spinner) findViewById(R.id.mode_spinner);

        switch (mode_saved) {
            case "Transit":
                mode.setSelection(1);
                break;
            case "Bicycling":
                mode.setSelection(2);
                break;
            case "Walking":
                mode.setSelection(3);
                break;
            default:
                mode.setSelection(0);
                break;
        }

        btnLoadDirections.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent data = new Intent();

                if (home.getText().toString().equals(preferences.getString(HOME, "")) &&
                        work.getText().toString().equals(preferences.getString(WORK, "")) &&
                        mode.getSelectedItem().toString().equals(preferences.getString(MODE, "")))
                    hasChanged = false;
                else
                    hasChanged = true;

                Log.d(TAG, "saveState()");
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(HOME, home.getText().toString());
                editor.putString(WORK, work.getText().toString());
                editor.putString(MODE, mode.getSelectedItem().toString());
                editor.putBoolean(CHANGE, hasChanged);
                editor.apply();

                MapSettings.this.setResult(RESULT_CODE, data);
                MapSettings.this.finish();
            }
        });

        home = (AutoCompleteTextView) findViewById(R.id.from);
        work = (AutoCompleteTextView) findViewById(R.id.to);

        home.setText(home_address);
        work.setText(work_address);

        home.setAdapter(new PlacesAutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line));
        work.setAdapter(new PlacesAutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line));

    }

    public void loadState(){
        Log.d(TAG, "loadState()");
        home_address = preferences.getString(HOME, "");
        work_address = preferences.getString(WORK, "");
        mode_saved = preferences.getString(MODE, "");
    }

    private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;

        public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }


        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }


        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
//                        resultList = autocomplete(constraint.toString());

                        // Assign the data the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }


                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    }
                    else {
                        notifyDataSetInvalidated();
                    }
                }};
            return filter;
        }
    }

    private static final String PLACES_AUTOCOMPLETE_API = "https://maps.googleapis.com/maps/api/place/autocomplete/json";

//    private ArrayList<String> autocomplete(String input) {
//
//        ArrayList<String> resultList = new ArrayList<String>();

//        try {
//
//            HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
//                                                                                        @Override
//                                                                                        public void initialize(HttpRequest request) {
//                                                                                            request.setParser(new JsonObjectParser(JSON_FACTORY));
//                                                                                        }
//                                                                                    }
//            );
//
//            GenericUrl url = new GenericUrl(PLACES_AUTOCOMPLETE_API);
//            url.put("input", input);
//            url.put("key", API_KEY);
//            url.put("sensor",false);
//
//            HttpRequest request = requestFactory.buildGetRequest(url);
//            HttpResponse httpResponse = request.execute();
//            PlacesResult directionsResult = httpResponse.parseAs(PlacesResult.class);
//
//            List<Prediction> predictions = directionsResult.predictions;
//            for (Prediction prediction : predictions) {
//                resultList.add(prediction.description);
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return resultList;
//    }

    public static class PlacesResult {


        // @Key("predictions")
        public List<Prediction> predictions;


    }


    public static class Prediction {
        // @Key("description")
        public String description;

        // @Key("id")
        public String id;

    }
}
