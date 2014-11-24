package com.blue_stingray.healthy_life_app.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.Alert;
import com.blue_stingray.healthy_life_app.model.Lifeline;
import com.blue_stingray.healthy_life_app.ui.adapter.AlertListAdapter;
import com.blue_stingray.healthy_life_app.ui.adapter.LifelineRequestListAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class LifelineRequestFragment extends Fragment {

    private ListView lifelineRequestList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lifeline_requests, container,false);
        getActivity().setTitle(R.string.title_lifeline_request);
        lifelineRequestList = (ListView) view.findViewById(R.id.lifeline_request_list);
        createList();
        return view;
    }

    public void createList() {
        // dummy data
        Lifeline[] dummyData = new Lifeline[]{
                new Lifeline(),
                new Lifeline(),
                new Lifeline(),
                new Lifeline(),
                new Lifeline(),
                new Lifeline(),
                new Lifeline(),
                new Lifeline(),
                new Lifeline()
        };

        ArrayList<Lifeline> lifelines = new ArrayList<>(Arrays.asList(dummyData));
        final LifelineRequestListAdapter adapter = new LifelineRequestListAdapter(getActivity(), lifelines);

        if(lifelineRequestList != null) {
            lifelineRequestList.setAdapter(adapter);
        }
    }
}
