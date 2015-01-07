package com.blue_stingray.healthy_life_app.ui.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.support.v4.app.Fragment;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.AppUsage;
import com.blue_stingray.healthy_life_app.model.Application;
import com.blue_stingray.healthy_life_app.model.Goal;
import com.blue_stingray.healthy_life_app.model.Stat;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.form.StatForm;
import com.blue_stingray.healthy_life_app.storage.db.DataHelper;
import com.blue_stingray.healthy_life_app.ui.ViewHelper;
import com.blue_stingray.healthy_life_app.util.Time;
import com.google.inject.Inject;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;
import java.util.Date;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class AppUsageFragment extends RoboFragment {

    @Inject
    private RestInterface rest;

    @InjectView(R.id.current_goal)
    private TextView currentGoal;

    @InjectView(R.id.time_used)
    private TextView timeUsed;

    @InjectView(R.id.create_goal)
    private Button createGoal;

    @InjectView(R.id.edit_goal)
    private Button editGoal;

    @InjectView(R.id.usage)
    private LinearLayout usageLayout;

    @InjectView(R.id.user)
    private LinearLayout userLayout;

    @InjectView(R.id.usage_list)
    private LinearLayout usageList;

    @InjectView(R.id.barchart_container)
    private LinearLayout barchartContainer;

    @InjectView(R.id.percent_usage)
    private TextView percentUsage;

    private View view;
    private Application app;
    private Goal goal;
    private DataHelper dataHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_app_usage, container,false);

        dataHelper = DataHelper.getInstance(getActivity().getApplicationContext());
        app = (Application) getArguments().getSerializable("appinfo");
        goal = app.getGoal();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(app.getName());
        setup();
    }

    public void setup() {

        // toggle data based on if goal exists
        if(app.hasGoal()) {

            createGoal.setVisibility(View.GONE);

            final String start = String.valueOf(Time.getStartOfDay(new Date()).getTime());
            String end = String.valueOf(Time.getEndOfDay(new Date()).getTime());

            final ProgressDialog progress = ProgressDialog.show(getActivity(), "", "Loading...", true);

            rest.getStatsByDate(new StatForm(app.getPackageName(), start, end), new Callback<Stat[]>() {
                @Override
                public void success(Stat[] stats, Response response) {
                    usageList.removeAllViews();

                    currentGoal.setText(String.valueOf(goal.getGoalTime()) + " hours");
                    timeUsed.setText(String.format("%2.2f", goal.getTimeUsedHours()) + " hours");

                    if(stats.length > 0) {
                        for(Stat stat : stats) {
                            TextView view = new TextView(getActivity());
                            view.setText(Time.getPrettyTime(stat.getStart()) + " - " + stat.getElapsed() + " s");
                            usageList.addView(view);
                        }
                    } else {
                        TextView empty = new TextView(getActivity());
                        empty.setText("No usage statistics.");
                        usageList.addView(empty);
                    }

                    progress.cancel();
                }

                @Override
                public void failure(RetrofitError error) {
                    progress.cancel();
                }
            });

            rest.getApp(app.getPackageName(), new Callback<Application>() {
                @Override
                public void success(Application application, Response response) {
                    rest.getAppUsage(application.getId(), new Callback<AppUsage>() {
                        @Override
                        public void success(AppUsage usage, Response response) {
                            setupChart(usage.getData());
                            percentUsage.setText(usage.getPercentageUseFormatted());
                        }

                        @Override
                        public void failure(RetrofitError error) {}
                    });
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });

        } else {
            editGoal.setVisibility(View.GONE);
            userLayout.setVisibility(View.GONE);
            usageLayout.setVisibility(View.GONE);
            barchartContainer.setVisibility(View.GONE);
        }

        // set create goal button listener
        Button createButton = (Button) view.findViewById(R.id.create_goal);
        createButton.setOnClickListener(new CreateGoalButtonListener());

        // set edit goal button listener
        Button editButton = (Button) view.findViewById(R.id.edit_goal);
        editButton.setOnClickListener(new EditGoalButtonListener());
    }

    public void setupChart(Integer[] data) {

        // setup bar chart
        ValueLineChart lineChart = (ValueLineChart) view.findViewById(R.id.usage_chart);
        ValueLineSeries points = new ValueLineSeries();

        for (int i = 0; i < data.length; i+=2) {
            points.addPoint(new ValueLinePoint(String.valueOf(i), data[i]));
        }

        lineChart.addSeries(points);
        lineChart.startAnimation();
    }

    private class CreateGoalButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putString("appName", app.getName());

            Fragment fragment = new CreateGoalFragment();
            fragment.setArguments(bundle);

            ViewHelper.injectFragment(fragment, getFragmentManager(), R.id.frame_container);
        }
    }

    private class EditGoalButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("appinfo", app);

            Fragment fragment = new EditGoalFragment();
            fragment.setArguments(bundle);

            ViewHelper.injectFragment(fragment, getFragmentManager(), R.id.frame_container);
        }
    }

}
