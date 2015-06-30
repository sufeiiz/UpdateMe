package nyc.c4q.syd.updateme;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by July on 6/26/15.
 */
public class MainAdapter extends RecyclerView.Adapter {
    private List<Card> cardsArray;
    private Context context;
    private List<JobPosition> jobs;

    public MainAdapter(Context context, List<Card> cardsArray) {
        this.context = context;
        this.cardsArray = cardsArray;
    }

    //create viewHolder for every card
    public class JobViewHolder extends RecyclerView.ViewHolder {
        protected TextView title1;
        protected TextView company1;
        protected CardView cardView1;

        protected TextView title2;
        protected TextView company2;
        protected CardView cardView2;

        protected TextView title3;
        protected TextView company3;
        protected CardView cardView3;

        protected ImageView info;

        public JobViewHolder(View v) {
            super(v);
            title1 = (TextView) v.findViewById(R.id.title1);
            company1 = (TextView) v.findViewById(R.id.company1);
            cardView1 = (CardView) v.findViewById(R.id.card_view1);
            cardView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (jobs!=null && jobs.size()>0) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(jobs.get(0).getLink()));
                        context.startActivity(browserIntent);
                    }
                }
            });

            title2 = (TextView) v.findViewById(R.id.title2);
            company2 = (TextView) v.findViewById(R.id.company2);
            cardView2 = (CardView) v.findViewById(R.id.card_view2);
            cardView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (jobs!=null && jobs.size()>0) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(jobs.get(1).getLink()));
                        context.startActivity(browserIntent);
                    }
                }
            });

            title3 = (TextView) v.findViewById(R.id.title3);
            company3 = (TextView) v.findViewById(R.id.company3);
            cardView3 = (CardView) v.findViewById(R.id.card_view3);
            cardView3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (jobs!=null && jobs.size()>0) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(jobs.get(2).getLink()));
                        context.startActivity(browserIntent);
                    }
                }
            });

            info = (ImageView) v.findViewById(R.id.info_icon);
            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (jobs!= null && jobs.size()>0) {
                        Intent intent = new Intent(context, JobActivity.class);
                        intent.putExtra("jobs", (java.io.Serializable) jobs);
                        context.startActivity(intent);
                    }
                }
            });

        }
    }

    public class MapViewHolder extends RecyclerView.ViewHolder {
        protected TextView string;

        public MapViewHolder(View v) {
            super(v);
            string = (TextView) v.findViewById(R.id.tv);

        }
    }

    //get type of card
    @Override
    public int getItemViewType(int position) {
        return cardsArray.get(position).getType();
    }

    //inflate layout for each card
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_layout, parent, false);
            return new JobViewHolder(itemView);
        }
        if (viewType == 2) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_layout, parent, false);
            return new MapViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 1) {

            JobViewHolder jobViewHolder = (JobViewHolder) holder;
            if (cardsArray != null && position < cardsArray.size()) {
                JobCard jobCard = (JobCard) cardsArray.get(position);

                jobs = jobCard.getJobArray();
                if (jobs.size() > 0) {
                    jobViewHolder.title1.setText("" + jobs.get(0).getTitle());
                    jobViewHolder.company1.setText("" + jobs.get(0).getCompany());
                }

                if (jobs.size() > 1) {
                    jobViewHolder.title2.setText("" + jobs.get(1).getTitle());
                    jobViewHolder.company2.setText("" + jobs.get(1).getCompany());
                }
                if (jobs.size() > 2) {
                    jobViewHolder.title3.setText("" + jobs.get(2).getTitle());
                    jobViewHolder.company3.setText("" + jobs.get(2).getCompany());
                }
            }

        }
        if (holder.getItemViewType() == 2) {
            MapCard mapCard = (MapCard) cardsArray.get(position);
            MapViewHolder mapHolder = (MapViewHolder) holder;
            mapHolder.string.setText(mapCard.getString());
        }
    }

    @Override
    public int getItemCount() {
        return cardsArray.size();
    }

    public void openUrl() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(jobs.get(0).getLink()));
        context.startActivity(browserIntent);
    }
}
