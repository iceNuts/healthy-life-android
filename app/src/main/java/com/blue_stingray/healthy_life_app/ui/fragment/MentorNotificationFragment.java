package com.blue_stingray.healthy_life_app.ui.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.MentorRequest;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.RetrofitDialogCallback;
import com.blue_stingray.healthy_life_app.ui.adapter.MentorRequestListAdapter;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * Created by BillZeng on 2/9/15.
 */
public class MentorNotificationFragment extends RoboFragment{

    @Inject
    private RestInterface rest;

    @InjectView(R.id.mentor_notification_list)
    private ListView mentorRequestList;

    @InjectView(R.id.blank_message)
    private LinearLayout blankMessage;

    private ProgressDialog loading;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        loading = ProgressDialog.show(getActivity(), "Mentor Request", "loading...");
        return inflater.inflate(R.layout.fragment_mentor_notification, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Mentor Requests");
        createList();
    }

    public void createList() {
        rest.getMentorRequest(
            new RetrofitDialogCallback<List<MentorRequest>>(
                    getActivity(),
                    null
            ) {
                @Override
                public void onSuccess(List<MentorRequest> mentorRequests, Response response) {
                    loading.cancel();
                    final ArrayList<MentorRequest> mentorRequestArray = new ArrayList<MentorRequest>(mentorRequests);

                    if (mentorRequestArray.size() > 0) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                blankMessage.setVisibility(View.GONE);
                                final MentorRequestListAdapter adapter = new MentorRequestListAdapter(getActivity(), mentorRequestArray, rest);
                                mentorRequestList.setAdapter(adapter);
                            }
                        });
                    }
                }

                @Override
                public void onFailure(RetrofitError error) {
                    loading.cancel();
                }
            }
        );
    }
}














