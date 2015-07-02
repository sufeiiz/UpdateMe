package nyc.c4q.syd.updateme;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by July on 6/25/15.
 */

public class FrontFragment extends Fragment implements JobSearchAsync.MyListener {

    private ProgressBar progressBar;
    List<JobPosition> js;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_top, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //when the activity first created the user will get default java job positions
        fetchData("java");
    }

    //method to populate recycler viw when async task finishes JSON parsing
    @Override
    public void onLoadComplete(List<JobPosition> jobs) {
        js = jobs;
        if (getView() == null || isDetached()) return;
        progressBar.setVisibility(View.INVISIBLE);

        if (jobs.size()==0 && JobActivity.showToast%2!=0 && !MainActivity.notConnected) {
                showCustomToast();
        }

        RecyclerView mRecyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view_jobs);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        JobAdapter mAdapter = new JobAdapter(jobs, getActivity());
        mRecyclerView.setAdapter(mAdapter);
    }

    //start asyncTask when activity created
    public void fetchData(String string) {
        JobSearchAsync jobSearchAsync = new JobSearchAsync(getActivity());
        jobSearchAsync.setListener(this);
        jobSearchAsync.execute(string);
        progressBar.setVisibility(View.VISIBLE);
    }


    //create a custom toast when there no positions to match the user's input
    public void showCustomToast() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.toast_layout, null);
        Toast toast = new Toast(getActivity());
        toast.setGravity(Gravity.CENTER_HORIZONTAL,0,0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }



}

