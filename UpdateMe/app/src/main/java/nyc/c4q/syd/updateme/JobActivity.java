package nyc.c4q.syd.updateme;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by sufeizhao on 6/30/15.
 */
public class JobActivity extends FragmentActivity{
    //eliminate the possibility of toast to appear twice on both sides of the card when there is no job match
    public static int showToast = 3;

    private boolean showingBack;
    private FrontFragment front;
    private BackFragment back;
    private Handler handler;
    private FlipAnimation flipAnimation;
    private FlipAnimation backFlip;
    private TextView header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_activity);

        //create handler for animation control
        handler = new Handler(getMainLooper());

        //create two fragments
        front = new FrontFragment();
        back = new BackFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, back, "fragmentRight").commit();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, front, "fragmentLeft").commit();

        header = (TextView) findViewById(R.id.header);

        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BackFragment right = (BackFragment) getSupportFragmentManager().findFragmentByTag("fragmentRight");
                FrontFragment left = (FrontFragment) getSupportFragmentManager().findFragmentByTag("fragmentLeft");

                //get user input from the settings section
                String userInput = right.getPosition() + "&location=" + right.getLocation();
                left.fetchData(userInput);
                changeHeaderText();
                //show toast only on one side of the card
                showToast+=1;

                flipAnimation = new FlipAnimation(left.getView(), right.getView());
                backFlip = new FlipAnimation(left.getView(), right.getView());
                handler.removeCallbacks(rotate);
                handler.postDelayed(rotate, 260);
            }

        });
    }

    private Runnable rotate = new Runnable() {

        @Override
        public void run() {
            if (!showingBack) {
                front.getView().startAnimation(flipAnimation);
                back.getView().startAnimation(flipAnimation);
                showingBack = true;
            } else {
                showingBack = false;
                backFlip.reverse();
                front.getView().startAnimation(backFlip);
                back.getView().startAnimation(backFlip);

            }
        }
    };

    public void changeHeaderText() {
        if(showToast%2==0) {
            header.setText("Full Jobs List");
        }
        else {
            header.setText("Modify Search");
        }
    }

}
