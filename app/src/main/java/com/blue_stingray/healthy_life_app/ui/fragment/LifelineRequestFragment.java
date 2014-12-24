package com.blue_stingray.healthy_life_app.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.Alert;
import com.blue_stingray.healthy_life_app.model.Application;
import com.blue_stingray.healthy_life_app.model.Lifeline;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.RetrofitDialogCallback;
import com.blue_stingray.healthy_life_app.ui.ViewHelper;
import com.blue_stingray.healthy_life_app.ui.adapter.AlertListAdapter;
import com.blue_stingray.healthy_life_app.ui.adapter.LifelineRequestListAdapter;
import com.google.inject.Inject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.fragment.RoboFragment;
import java.util.List;

public class LifelineRequestFragment extends RoboFragment {

    private ListView lifelineRequestList;
    @Inject private RestInterface rest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lifeline_requests, container,false);
        getActivity().setTitle(R.string.title_lifeline_request);
        lifelineRequestList = (ListView) view.findViewById(R.id.lifeline_request_list);
        createList();
        return view;
    }

    public void createList() {
        rest.getLifeline(
            new RetrofitDialogCallback<List<Lifeline>>(
                getActivity(),
                null
            ) {
                @Override
                public void onSuccess(List<Lifeline> lifelines, Response response) {
                    final ArrayList<Lifeline> lifelineSeq = new ArrayList<Lifeline>(lifelines);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final LifelineRequestListAdapter adapter = new LifelineRequestListAdapter(getActivity(), lifelineSeq, rest);
                            if(lifelineRequestList != null) {
                                lifelineRequestList.setAdapter(adapter);
                            }
                        }
                    } );
                }

                @Override
                public void onFailure(RetrofitError retrofitError) {
                    Log.d("Lifeline", retrofitError.toString());
                }
            }
        );
    }
}
