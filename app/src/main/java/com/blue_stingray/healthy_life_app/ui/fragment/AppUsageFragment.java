package com.blue_stingray.healthy_life_app.ui.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.support.v4.app.Fragment;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blue_stingray.healthy_life_app.App;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.AppUsage;
import com.blue_stingray.healthy_life_app.model.Application;
import com.blue_stingray.healthy_life_app.model.Goal;
import com.blue_stingray.healthy_life_app.model.Stat;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.RetrofitDialogCallback;
import com.blue_stingray.healthy_life_app.net.form.StatForm;
import com.blue_stingray.healthy_life_app.storage.db.DataHelper;
import com.blue_stingray.healthy_life_app.storage.db.SharedPreferencesHelper;
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

    @Inject
    private SharedPreferencesHelper prefs;

    private User user;
    private View view;
    private Application app;
    private Goal goal;
    private DataHelper dataHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_app_usage, container,false);
        dataHelper = DataHelper.getInstance(getActivity().getApplicationContext());
        user = (User) getArguments().getSerializable("user");
        app = (Application) getArguments().getSerializable("appinfo");
        goal = app.getGoal();

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

        User authUser = ((App) getActivity().getApplication()).getAuthUser(getActivity());
        if(!authUser.canEdit()) {
            ViewGroup layout = (ViewGroup) editGoal.getParent();
            layout.removeView(editGoal);
        }

        setup();
    }

    public void setup() {

        // toggle data based on if goal exists
        if(app.hasGoal()) {

            createGoal.setVisibility(View.GONE);

            final String start = String.valueOf(Time.getStartOfDay(new Date()).getTime());
            String end = String.valueOf(Time.getEndOfDay(new Date()).getTime());

            final ProgressDialog progress = ProgressDialog.show(getActivity(), "", "Loading...", true);

            rest.getStatsByDate(
                    new StatForm(app, start, end),
                    new RetrofitDialogCallback<Stat[]>(
                            getActivity(),
                            progress
                    ) {
                @Override
                public void onSuccess(Stat[] stats, Response response) {
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
                }

                @Override
                public void onFailure(RetrofitError error) {

                }
            });

            if(app.getDeviceId() == null) {
                rest.getApp(app.getPackageName(), new GetAppCallback());
            } else {
                rest.getApp(app.getPackageName(), prefs.getDeviceId(), new GetAppCallback());
            }

        } else {
            editGoal.setVisibility(View.GONE);
            userLayout.setVisibility(View.GONE);
            usageLayout.setVisibility(View.GONE);
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

            if(user != null) {
                bundle.putSerializable("user", user);
            }

            Fragment fragment = new EditGoalFragment();
            fragment.setArguments(bundle);

            ViewHelper.injectFragment(fragment, getFragmentManager(), R.id.frame_container);
        }
    }

    private class GetAppCallback implements Callback<Application> {

        @Override
        public void success(Application application, Response response) {
            rest.getAppUsage(
                    application.getId(),
                    new RetrofitDialogCallback<AppUsage>(
                            getActivity(),
                            null
                    ) {
                @Override
                public void onSuccess(AppUsage usage, Response response) {
                    setupChart(usage.getData());
                    percentUsage.setText(usage.getPercentageUseFormatted());
                }

                @Override
                public void onFailure(RetrofitError error) {}
            });
        }

        @Override
        public void failure(RetrofitError error) {

        }

    }

}
