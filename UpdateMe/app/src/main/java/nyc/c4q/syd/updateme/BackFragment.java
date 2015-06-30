package nyc.c4q.syd.updateme;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by sufeizhao on 6/30/15.
 */
public class BackFragment extends Fragment {
    private EditText userPosition;
    private EditText userLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_back, container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userPosition = (EditText) view.findViewById(R.id.position);
        userLocation = (EditText) view.findViewById(R.id.location);
    }


    //method to pass the data to Job Activity
    public String getPosition() {
        return userPosition.getText().toString();
    }

    public String getLocation() {
        return userLocation.getText().toString();
    }

}
