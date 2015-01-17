package com.blue_stingray.healthy_life_app.ui.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.Lifeline;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.RetrofitDialogCallback;
import com.blue_stingray.healthy_life_app.ui.adapter.LifelineRequestListAdapter;
import com.google.inject.Inject;
import java.util.ArrayList;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

import java.util.List;

public class LifelineRequestFragment extends RoboFragment {

    @Inject private RestInterface rest;

    @InjectView(R.id.lifeline_request_list)
    private ListView lifelineRequestList;

    @InjectView(R.id.blank_message)
    private LinearLayout blankMessage;

    private ProgressDialog loading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        loading = ProgressDialog.show(getActivity(), "Lifelines", "Loading...");
        return inflater.inflate(R.layout.fragment_lifeline_requests, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.title_lifeline_request);
        createList();
    }

    public void createList() {
        rest.getLifeline(
            new RetrofitDialogCallback<List<Lifeline>>(
                getActivity(),
                null
            ) {
                @Override
                public void onSuccess(final List<Lifeline> lifelines, Response response) {
                    final ArrayList<Lifeline> lifelineSeq = new ArrayList<>(lifelines);

                    if(lifelines.size() > 0) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                blankMessage.setVisibility(View.GONE);
                                final LifelineRequestListAdapter adapter = new LifelineRequestListAdapter(getActivity(), lifelineSeq, rest);
                                lifelineRequestList.setAdapter(adapter);
                                loading.cancel();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(RetrofitError retrofitError) {
                    Log.d("Lifeline", retrofitError.toString());
                    loading.cancel();
                }
            }
        );
    }
}
