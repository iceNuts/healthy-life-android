package com.blue_stingray.healthy_life_app.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blue_stingray.healthy_life_app.App;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.ui.ViewHelper;
import com.blue_stingray.healthy_life_app.ui.activity.MainActivity;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;
import org.w3c.dom.Text;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * Provides a single users profile information.
 */
public class ProfileFragment extends RoboFragment {

    @InjectView(R.id.current_score)
    private TextView currentScore;

    @InjectView(R.id.rating_history)
    private ValueLineChart ratingHistory;

    private User authUser;

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
        ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);

        series.addPoint(new ValueLinePoint("", 0f));
        series.addPoint(new ValueLinePoint("J", 2.4f));
        series.addPoint(new ValueLinePoint("F", 3.4f));
        series.addPoint(new ValueLinePoint("M", .4f));
        series.addPoint(new ValueLinePoint("A", 1.2f));
        series.addPoint(new ValueLinePoint("M", 2.6f));
        series.addPoint(new ValueLinePoint("J", 1.0f));
        series.addPoint(new ValueLinePoint("J", 3.5f));
        series.addPoint(new ValueLinePoint("A", 2.4f));
        series.addPoint(new ValueLinePoint("S", 2.4f));
        series.addPoint(new ValueLinePoint("O", 3.4f));
        series.addPoint(new ValueLinePoint("N", .4f));
        series.addPoint(new ValueLinePoint("D", 1.3f));
        series.addPoint(new ValueLinePoint("", 0f));

        ratingHistory.addSeries(series);
        ratingHistory.startAnimation();
    }

}
