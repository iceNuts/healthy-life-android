package com.blue_stingray.healthy_life_app.ui.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.blue_stingray.healthy_life_app.App;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.AppGoal;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.RetrofitDialogCallback;
import com.blue_stingray.healthy_life_app.ui.ViewHelper;
import com.blue_stingray.healthy_life_app.ui.adapter.AppProgressListAdapter;
import com.blue_stingray.healthy_life_app.ui.widget.LinearList;
import com.google.inject.Inject;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class UserOverviewFragment extends RoboFragment {

    @Inject
    private RestInterface rest;

    @InjectView(R.id.app_progress_list)
    private LinearList appProgressList;

    @InjectView(R.id.user_name)
    private TextView userName;

    @InjectView(R.id.percentile_ranking)
    private TextView percentileRanking;

    @InjectView(R.id.current_score)
    private TextView currentScore;

    private View view;

    private User user;

    private ProgressDialog loading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_overview, container, false);
        loading = ProgressDialog.show(getActivity(), "User Overview", "Loading...");

        if(getArguments() != null)
        {
            user = (User) getArguments().getSerializable("user");
        }
        else {

            // Use the authenticated user if no user information is passed in to the fragment
            user = ((App) getActivity().getApplication()).getAuthUser(getActivity());
            if(user == null) {
                ViewHelper.unauthorized(getActivity());
                return null;
            }
        }


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Overview - " + user.getName());
        userName.setText(user.getName());
        percentileRanking.setText("Ranks in the top " + user.getPercentile() + "% of healthy life users.");
        currentScore.setText(String.valueOf(user.getScore()));
        createProgressList();
    }

    /**
     * Create app list with progress bars of percentage of goals completed
     */
    private void createProgressList() {
        rest.getUserAppsUsage(
                user.getId(),
                new RetrofitDialogCallback<List<AppGoal>>(
                        getActivity(),
                        loading
                ) {
            @Override
            public void onSuccess(List<AppGoal> appGoals, Response response) {
                AppProgressListAdapter adapter = new AppProgressListAdapter(getActivity(), appGoals);
                appProgressList.setAdapter(adapter);
            }

            @Override
            public void onFailure(RetrofitError error) {

            }
        });
    }


}
