package com.blue_stingray.healthy_life_app.ui.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.support.v4.app.Fragment;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.blue_stingray.healthy_life_app.App;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.AppUsage;
import com.blue_stingray.healthy_life_app.model.Application;
import com.blue_stingray.healthy_life_app.model.Goal;
import com.blue_stingray.healthy_life_app.model.Stat;
import com.blue_stingray.healthy_life_app.model.Tip;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.RetrofitDialogCallback;
import com.blue_stingray.healthy_life_app.net.form.StatForm;
import com.blue_stingray.healthy_life_app.storage.db.DataHelper;
import com.blue_stingray.healthy_life_app.storage.db.SharedPreferencesHelper;
import com.blue_stingray.healthy_life_app.ui.ViewHelper;
import com.blue_stingray.healthy_life_app.util.Time;
import com.google.inject.Inject;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

    @InjectView(R.id.textView7)
    private TextView appUsageEndingTextView;

    @InjectView(R.id.time_used)
    private TextView appTimeUsed;

    @InjectView(R.id.create_goal)
    private Button createGoal;

    @InjectView(R.id.edit_goal)
    private Button editGoal;

    @InjectView(R.id.user)
    private LinearLayout userLayout;

    @InjectView(R.id.barchart_container)
    private LinearLayout barchartContainer;

    @InjectView(R.id.percent_usage)
    private TextView percentUsage;

    @InjectView(R.id.view_type_spinner)
    private Spinner viewTypeSpinner;

    @InjectView(R.id.app_usage_graph)
    private BarChart AppUsageTimeChart;

    @InjectView(R.id.today_app_usage_chart)
    private ValueLineChart TodayAppUsageTimeChart;

    @InjectView(R.id.tip_info)
    private TextView tipInfo;

    @Inject
    private SharedPreferencesHelper prefs;

    private User user;
    private View view;
    private Application app;
    private Goal goal;
    private DataHelper dataHelper;
    private HashMap<String, String> appUsage;
    private List<DataHelper.DBAppUsage> AppUsage;
    private List <DataHelper.DBAppUsage> TodayAppUsage;
    private int viewOption;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_app_usage, container, false);
        dataHelper = DataHelper.getInstance(getActivity());
        user = (User) getArguments().getSerializable("user");
        app = (Application) getArguments().getSerializable("appinfo");
        goal = app.getGoal();
        appUsage = dataHelper.getAppUsageTodayByPackageName(app.getPackageName());

        if(user != null) {
            getActivity().setTitle(app.getName() + " - " + user.getName());
        } else {
            getActivity().setTitle(app.getName());
            user = ((App) getActivity().getApplication()).getAuthUser(getActivity());
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setup();
        setupRandomTip();
    }

    public void setup() {

        // toggle data based on if goal exists
        if(app.hasGoal()) {
            createGoal.setVisibility(View.GONE);
        } else {
            editGoal.setVisibility(View.GONE);
            userLayout.setVisibility(View.GONE);
            barchartContainer.setVisibility(View.GONE);
        }

        // set create goal button listener
        if(createGoal != null) {
            createGoal.setOnClickListener(new CreateGoalButtonListener());
        }

        // set edit goal button listener
        if(editGoal != null) {
            editGoal.setOnClickListener(new EditGoalButtonListener());
        }

        int totalSec = Integer.valueOf(appUsage.get("totalSec"));
        int usedTime = Integer.valueOf(appUsage.get("usedTime"));
        int usageRatio = (int)(usedTime/(double)totalSec*100);
        if (totalSec == 0 || usedTime == 0) {
            percentUsage.setVisibility(View.GONE);
            appUsageEndingTextView.setText("Need More Data");
        }
        else {
            percentUsage.setVisibility(View.VISIBLE);
            if (usageRatio < 1) {
                percentUsage.setText("< 1%");
            }
            else {
                percentUsage.setText(String.valueOf(usageRatio)+"%");
            }
            appUsageEndingTextView.setText("of total app usage");
        }

        String timeUsed;
        if (totalSec < 60) {
            timeUsed = String.valueOf(totalSec)+"s";
        }
        else if (totalSec < 60*60) {
            timeUsed = String.valueOf(totalSec/60)+"min"+String.valueOf(totalSec%60)+"s";
        }
        else {
            timeUsed = String.valueOf(totalSec/3600)+"h";
            int remain = totalSec%3600;
            timeUsed += String.valueOf(remain/60)+"min"+String.valueOf(remain%60)+"s";
        }
        appTimeUsed.setText(timeUsed);
        int hour = (int)goal.getGoalTime();
        int minute = (int)((goal.getGoalTime()-(int)goal.getGoalTime())*60);
        currentGoal.setText(String.valueOf(hour)+"h"+String.valueOf(minute)+"min");

        viewOption = 0;
        setupAppUsageChart(viewOption);
        setupTodayAppUsageChart();
    }

    public void setupAppUsageChart(final int option) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppUsage = dataHelper.getRecentAppUsageByPackageName(app.getPackageName(), option);
                // draw graph
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // setup dropdown window
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                                getActivity(),
                                R.array.view_type_array,
                                android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        viewTypeSpinner.setAdapter(adapter);
                        viewTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                AppUsage.clear();
                                viewOption = position;
                                AppUsage = dataHelper.getRecentAppUsageByPackageName(app.getPackageName(), position);
                                showAppUsageBarChart(position);
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        showAppUsageBarChart(0);
                    }
                });
            }
        }).start();
    }

    private void setupTodayAppUsageChart() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                TodayAppUsage = dataHelper.getTodayAppUsageByPackageName(app.getPackageName());
                // draw graph
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showTodayAppUsageLineChart();
                    }
                });
            }
        }).start();
    }

    private void showAppUsageBarChart(int option) {
        AppUsageTimeChart.clearChart();
        // parsing
        for (int i = 0; i < AppUsage.size(); i++) {
            DataHelper.DBAppUsage tuple = AppUsage.get(i);
            BarModel model = new BarModel((Float)tuple.value, 0xFF1FF4AC);
            if (i == 0) {
                model.setLegendLabel("Today");
            }
            else {
                model.setLegendLabel((String)tuple.key);
            }
            AppUsageTimeChart.addBar(model);
        }
        AppUsageTimeChart.startAnimation();
    }

    private void showTodayAppUsageLineChart() {
        TodayAppUsageTimeChart.clearChart();
        ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);
        // parsing
        for (int i = 0; i < TodayAppUsage.size(); i++) {
            DataHelper.DBAppUsage tuple = TodayAppUsage.get(i);
            ValueLinePoint point = new ValueLinePoint(String.valueOf(i), (Float)tuple.value);
            series.addPoint(point);
        }
        TodayAppUsageTimeChart.addSeries(series);
        TodayAppUsageTimeChart.startAnimation();
    }

    private class CreateGoalButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putString("appName", app.getName());
            bundle.putString("userID", String.valueOf(user.getId()));

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
            bundle.putString("userID", String.valueOf(user.getId()));

            if(user != null) {
                bundle.putSerializable("user", user);
            }

            Fragment fragment = new EditGoalFragment();
            fragment.setArguments(bundle);

            ViewHelper.injectFragment(fragment, getFragmentManager(), R.id.frame_container);
        }
    }

    private void setupRandomTip() {
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "", "Loading...");
        rest.getRandomTip(new RetrofitDialogCallback<Tip>(
                getActivity(),
                loading
        ) {
            @Override
            public void onSuccess(Tip tip, Response response) {
                tipInfo.setText(tip.content);
            }

            @Override
            public void onFailure(RetrofitError retrofitError) {
            }
        });
    }

}
