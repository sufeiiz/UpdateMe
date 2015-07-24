package sufeiiz.c4q.nyc.tablet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends Activity {

    private FragmentManager fm;
    private CharSequence mTitle;
    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.container, new MenuFragment(fm));
        ft.add(R.id.container2, new ContentFragment());
        ft.commit();

    }

    public static class MenuFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";
        private FragmentManager fm;

        public static MenuFragment newInstance(int sectionNumber, FragmentManager fm) {
            MenuFragment fragment = new MenuFragment(fm);
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public MenuFragment() {
        }

        public MenuFragment(FragmentManager fm) {
            this.fm = fm;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            String[] menu = getResources().getStringArray(R.array.menu);
            ListView listview = (ListView) rootView.findViewById(R.id.menu_list);

            listview.setAdapter(new ArrayAdapter<>(rootView.getContext(), android.R.layout.simple_list_item_1, menu));
            listview.setOnItemClickListener(new DrawerItemClickListener(listview, fm));
            return rootView;
        }
    }

    public static class ContentFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public static ContentFragment newInstance(int sectionNumber) {
            ContentFragment fragment = new ContentFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public ContentFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_content, container, false);
            return rootView;
        }
    }

    private static class DrawerItemClickListener implements ListView.OnItemClickListener {
        private ListView listview;
        private FragmentManager fm;
        private DrawerItemClickListener(ListView listview, FragmentManager fm) {
            this.listview = listview;
            this.fm = fm;
        }

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Fragment fragment = new ContentFragment();

            switch(position) {
                case 0:
                    fragment = new ToDoFragment();
                    break;
                case 1:
                    fragment = new JobFragment();
                    break;
                case 2:
                    fragment = new sufeiiz.c4q.nyc.tablet.MapFragment();
                    break;
                case 3:
                    fragment = new StockFragment();
                    break;
            }

            Bundle args = new Bundle();
            args.putInt(ContentFragment.ARG_SECTION_NUMBER, position);
            fragment.setArguments(args);

            // Insert the fragment by replacing any existing fragment
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.container2, fragment);
            ft.addToBackStack(null);
            ft.commit();

            listview.setItemChecked(position, true);
        }
    }
}
