package nyc.c4q.syd.updateme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by sufeizhao on 6/29/15.
 */
public class MapSettings extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String API_KEY = "AIzaSyDTaAeiCfVCXJhdweubPkgIvsni3s1-9ss";
    private static final LatLngBounds BOUNDS = new LatLngBounds(
            new LatLng(40.498425, -74.250219), new LatLng(40.792266, -73.776434));
    private PlaceAutocompleteAdapter mAdapter;
    protected static final int RESULT_CODE = 123;
    private GoogleApiClient client;
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

        client = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        Button btnLoadDirections = (Button) findViewById(R.id.load_directions);
        mode = (Spinner) findViewById(R.id.mode_spinner);
        //TODO: spinner layout
//        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.mode_arrays, R.id.spinner_text);
//        mode.setAdapter(adapter);

        if (mode_saved.equals("Transit")) {
            mode.setSelection(1);
        } else if (mode_saved.equals("Bicycling")) {
            mode.setSelection(2);
        } else if (mode_saved.equals("Walking")) {
            mode.setSelection(3);
        } else {
            mode.setSelection(0);
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
        home.setOnItemClickListener(mAutocompleteClickListener);

        home.setText(home_address);
        work.setText(work_address);

        mAdapter = new PlaceAutocompleteAdapter(this, android.R.layout.simple_list_item_1,
                client, BOUNDS, null);
        home.setAdapter(mAdapter);
        work.setAdapter(mAdapter);

    }

    public void loadState(){
        Log.d(TAG, "loadState()");
        home_address = preferences.getString(HOME, "");
        work_address = preferences.getString(WORK, "");
        mode_saved = preferences.getString(MODE, "");
    }

    // Listener that handles selections from suggestions from the AutoCompleteTextView that
    // displays Place suggestions. Gets the place id of the selected item and issues a request to
    // the Places Geo Data API to retrieve more details about the place.
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Autocomplete item selected: " + item.description);

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(client, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(TAG, "Called getPlaceById to get Place details for " + item.placeId);
        }
    };

    // Callback for results from a Places Geo Data API query that shows the first place result in
    // the details view on screen.
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            places.release();
        }
    };

    // Called when the Activity could not connect to Google Play services and the auto manager
    // could resolve the error automatically.
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }
}
