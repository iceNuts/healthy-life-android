package com.blue_stingray.healthy_life_app.ui.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blue_stingray.healthy_life_app.App;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.UsageReport;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.ui.ViewHelper;
import com.blue_stingray.healthy_life_app.ui.activity.MainActivity;
import com.google.inject.Inject;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * Provides a single users profile information.
 */
public class ProfileFragment extends RoboFragment {

    @Inject
    private RestInterface rest;

    @InjectView(R.id.current_score)
    private TextView currentScore;

    @InjectView(R.id.rating_history)
    private ValueLineChart ratingHistory;

    private User authUser;

    private Integer[] scoresByMonth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container,false);

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
        getActivity().setTitle(authUser.getName());
        currentScore.setText(String.valueOf(authUser.getScore()));
        setupLineChart();
    }

    private void setupLineChart() {
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "", "Loading...");

        rest.getMyReport(new Callback<UsageReport>() {
            @Override
            public void success(UsageReport o, Response response) {
                scoresByMonth = o.getScoreHistoryByMonth();

                ValueLineSeries series = new ValueLineSeries();
                series.setColor(0xFF56B7F1);
                series.addPoint(new ValueLinePoint("", 0f));
                series.addPoint(new ValueLinePoint("Ja", scoresByMonth[0]));
                series.addPoint(new ValueLinePoint("Fe", scoresByMonth[1]));
                series.addPoint(new ValueLinePoint("Ma", scoresByMonth[2]));
                series.addPoint(new ValueLinePoint("Ap", scoresByMonth[3]));
                series.addPoint(new ValueLinePoint("Ma", scoresByMonth[4]));
                series.addPoint(new ValueLinePoint("Ju", scoresByMonth[5]));
                series.addPoint(new ValueLinePoint("Ju", scoresByMonth[6]));
                series.addPoint(new ValueLinePoint("Au", scoresByMonth[7]));
                series.addPoint(new ValueLinePoint("Se", scoresByMonth[8]));
                series.addPoint(new ValueLinePoint("Oc", scoresByMonth[9]));
                series.addPoint(new ValueLinePoint("No", scoresByMonth[10]));
                series.addPoint(new ValueLinePoint("De", scoresByMonth[11]));
                series.addPoint(new ValueLinePoint("", 0f));

                ratingHistory.addSeries(series);
                ratingHistory.startAnimation();

                loading.cancel();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

}
