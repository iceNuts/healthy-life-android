package com.blue_stingray.healthy_life_app.ui.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blue_stingray.healthy_life_app.App;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.ui.ViewHelper;
import com.blue_stingray.healthy_life_app.ui.adapter.UserListAdapter;
import com.blue_stingray.healthy_life_app.ui.widget.LinearList;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class LeaderboardFragment extends RoboFragment {

    @Inject
    private RestInterface rest;

    @InjectView(R.id.users)
    private LinearList userList;

    @InjectView(R.id.current_score)
    private TextView currentScore;

    @InjectView(R.id.percentile_ranking)
    private TextView percentileRanking;

    private final ArrayList<User> users = new ArrayList<User>();

    private User authUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container,false);

        authUser = ((App) getActivity().getApplication()).getAuthUser(getActivity());
        if(authUser == null) {
            ViewHelper.unauthorized(getActivity());
            return null;
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.title_leaderboard);
        currentScore.setText(String.valueOf(authUser.getScore()));
        percentileRanking.setText("You rank in the top " + authUser.getPercentileFormatted() + " of healthy life users.");
        createList();
    }

    public void createList() {
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "", "Loading...");

        rest.getLeaderboard(new Callback<List<User>>() {
            @Override
            public void success(List<User> users, Response response) {
                final UserListAdapter adapter = new UserListAdapter(getActivity(), users, R.layout.user_list_row_simple);
                userList.setAdapter(adapter);

                loading.cancel();
            }

            @Override
            public void failure(RetrofitError error) {
                loading.cancel();
            }
        });
    }

}
