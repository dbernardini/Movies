package com.bernardini.danilo.movies.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bernardini.danilo.movies.R;
import com.bernardini.danilo.movies.asyncTasks.MyMoviesTask;
import com.bernardini.danilo.movies.asyncTasks.MyTvTask;

public class MyTvFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;

    public MyTvFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mytv, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new MyTvTask(getActivity(), swipeRefreshLayout).execute();
            }
        });

        new MyTvTask(getActivity()).execute();

        return view;
    }

}
