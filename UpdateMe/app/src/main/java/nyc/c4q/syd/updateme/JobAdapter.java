package nyc.c4q.syd.updateme;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

/**
 * Created by July on 6/26/15.
 */
public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewHolder> {
    private final Activity mActivity;
    private List<JobPosition> arrayJobs;

    //constructor
    public JobAdapter(List<JobPosition> arrayJobs, Activity mActivity) {
        this.arrayJobs = arrayJobs;
        this.mActivity = mActivity;
    }

    //create a view holder for a row
    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView title;
        protected TextView company;
        protected CardView card;
        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            company = (TextView) v.findViewById(R.id.company);
            card = (CardView) v.findViewById(R.id.card);
        }
    }


    @Override
    public JobAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_recycler_view, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    //set up the contents for the layout views
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrl(position);
            }
        });
        holder.title.setText(arrayJobs.get(position).getTitle());
        holder.company.setText(arrayJobs.get(position).getCompany());
    }

    // Return the size of the dataset
    @Override
    public int getItemCount() {
        return arrayJobs.size();
    }

    public void openUrl(int position) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(arrayJobs.get(position).getLink()));
        mActivity.startActivity(browserIntent);
    }
}
