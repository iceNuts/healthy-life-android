package com.blue_stingray.healthy_life_app.ui.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.IconTextView;
import android.widget.TextView;

import com.blue_stingray.healthy_life_app.App;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.UsageReport;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.storage.db.DataHelper;
import com.blue_stingray.healthy_life_app.ui.ViewHelper;
import com.blue_stingray.healthy_life_app.ui.activity.MainActivity;
import com.google.inject.Inject;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
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

    @InjectView(R.id.details_button)
    private IconTextView detailsButton;

    @InjectView(R.id.percentile_ranking)
    private TextView percentileRanking;

    @InjectView(R.id.usage_time)
    private BarChart PhoneUsageTimeChart;

    @InjectView(R.id.wake_up_time)
    private BarChart PhoneWakeUpTimeChart;

    private User authUser;

    private Integer[] scoresByMonth;

    private List<DataHelper.PhoneUsageTuple> totalUseHours;

    private List<DataHelper.PhoneUsageTuple> wakeupTimes;

    private DataHelper dataHelper;


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
        percentileRanking.setText("You rank in the top " + authUser.getPercentileFormatted() + " of healthy life users.");
        detailsButton.setOnClickListener(new OnDetailsClickListener());
        setupLineChart();
        // setup phone usage graph
        setupPhoneUsageChart();
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

    private void setupPhoneUsageChart() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataHelper = DataHelper.getInstance(getActivity());
                wakeupTimes = dataHelper.getRecentPhoneWakeUpTimes();
                totalUseHours = dataHelper.getRecentPhoneUsageHours();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // parsing total time
                        for (int i = 0; i < totalUseHours.size(); i++) {
                            DataHelper.PhoneUsageTuple tuple = totalUseHours.get(i);
                            BarModel model = new BarModel((Float)tuple.value, 0xFF1FF4AC);
                            if (i == 0) {
                                model.setLegendLabel("Today");
                            }
                            else {
                                model.setLegendLabel((String)tuple.key);
                            }
                            PhoneUsageTimeChart.addBar(model);
                        }
                        // parsing wake up times
                        for (int i = 0; i < wakeupTimes.size(); i++) {
                            DataHelper.PhoneUsageTuple tuple = wakeupTimes.get(i);
                            BarModel model = new BarModel((Integer)tuple.value, 0xFF123456);
                            if (i == 0) {
                                model.setLegendLabel("Today");
                            }
                            else {
                                model.setLegendLabel((String)tuple.key);
                            }
                            PhoneWakeUpTimeChart.addBar(model);
                        }
                        PhoneUsageTimeChart.startAnimation();
                        PhoneWakeUpTimeChart.startAnimation();
                    }
                });
            }
        }).start();
    }

    private class OnDetailsClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            ViewHelper.injectFragment(new LeaderboardFragment(), getActivity().getSupportFragmentManager(), R.id.frame_container);
        }
    }

}
