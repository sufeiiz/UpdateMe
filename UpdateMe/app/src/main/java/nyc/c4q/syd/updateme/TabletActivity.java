package nyc.c4q.syd.updateme;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by ann on 7/21/15.
 */
public class TabletActivity extends Activity {

    private FragmentManager fm;
    private FragmentTransaction ft;
    private ListView menu_list;
    private static int mCurrentSelectedPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tablet);

        fm = getFragmentManager();
        ft = fm.beginTransaction();
        ft.add(R.id.container, new MenuFragment());
        ft.add(R.id.container, new ContentFragment());
        ft.commit();

    }

    public static class MenuFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            ListView menu_list = (ListView) inflater.inflate(R.layout.menu_fragment, container, false);
            menu_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    //TODO replace
//            ft.replace(R.id.container, fragment);
//            ft.addToBackStack(null);
//            ft.commit()
                }
            });

            menu_list.setAdapter(new ArrayAdapter<String>(
                    menu_list.getContext(),
                    android.R.layout.simple_list_item_1,
                    getResources().getStringArray(R.array.menu)));
            menu_list.setItemChecked(mCurrentSelectedPosition, true);
            return menu_list;
        }
    }

    public static class ContentFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.menu_fragment, container, false);
;

            return view;
        }
    }
}
