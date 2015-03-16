package com.blue_stingray.healthy_life_app.ui.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.IconTextView;
import android.widget.ShareActionProvider;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blue_stingray.healthy_life_app.App;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.Tip;
import com.blue_stingray.healthy_life_app.model.UsageReport;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.RetrofitDialogCallback;
import com.blue_stingray.healthy_life_app.net.form.UserForm;
import com.blue_stingray.healthy_life_app.storage.db.DataHelper;
import com.blue_stingray.healthy_life_app.ui.ViewHelper;
import com.blue_stingray.healthy_life_app.ui.activity.MainActivity;
import com.google.inject.Inject;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.communication.IOnBarClickedListener;
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
import com.blue_stingray.healthy_life_app.R;

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

    @InjectView(R.id.view_type_spinner)
    private Spinner viewTypeSpinner;

    @InjectView(R.id.tip_info)
    private TextView tipInfo;

    private User authUser;

    private Integer[] scoresByMonth;

    private List<DataHelper.PhoneUsageTuple> totalUseHours;

    private List<DataHelper.PhoneUsageTuple> wakeupTimes;

    private DataHelper dataHelper;

    private int viewOption;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


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
        viewOption = 0;
        currentScore.setText(String.valueOf(authUser.getScore()));
        percentileRanking.setText("You rank in the top " + authUser.getPercentileFormatted() + " of healthy life users.");
        detailsButton.setOnClickListener(new OnDetailsClickListener());
        setupLineChart();
        // setup phone usage graph
        setupPhoneUsageChart();
        setupRandomTip();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_fragment_actions, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        ShareActionProvider shareActionProvider = (ShareActionProvider) item.getActionProvider();
        shareActionProvider.setOnShareTargetSelectedListener(new ShareActionProvider.OnShareTargetSelectedListener() {
            @Override
            public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {
                rest.updateUser(
                        authUser.getId(),
                        new UserForm(true),
                        new RetrofitDialogCallback<User>(
                                getActivity(),
                                null
                        ) {
                    @Override
                    public void onSuccess(User user, Response response) {}
                    @Override
                    public void onFailure(RetrofitError error) {}
                });

                return false;
            }
        });

        // Create the share Intent
        String shareLink = "http://healthy.wherewedev.com/user/" + authUser.getId();
        String shareText = "Check out my Healthy App profile! " + shareLink;
        Intent shareIntent = ShareCompat.IntentBuilder.from(getActivity()).setType("text/plain").setText(shareText).getIntent();

        // Set the share Intent
        shareActionProvider.setShareIntent(shareIntent);
    }

    private void setupLineChart() {
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "", "Loading...");

        rest.getMyReport(new RetrofitDialogCallback<UsageReport>(
                getActivity(),
                null
        ) {
            @Override
            public void onSuccess(UsageReport o, Response response) {
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
            public void onFailure(RetrofitError error) {

            }
        });
    }

    private void setupPhoneUsageChart() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataHelper = DataHelper.getInstance(getActivity());
                wakeupTimes = dataHelper.getRecentPhoneWakeUpTimes(0);
                totalUseHours = dataHelper.getRecentPhoneUsageHours(0);
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
                                wakeupTimes.clear();
                                totalUseHours.clear();
                                viewOption = position;
                                wakeupTimes = dataHelper.getRecentPhoneWakeUpTimes(position);
                                totalUseHours = dataHelper.getRecentPhoneUsageHours(position);
                                showLineChart(position);
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        showLineChart(0);
                    }
                });
            }
        }).start();
    }

    private void showLineChart(int option) {
        // reset
        PhoneUsageTimeChart.clearChart();
        PhoneWakeUpTimeChart.clearChart();
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

        // setup clickable graph
        PhoneUsageTimeChart.setOnBarClickedListener(new IOnBarClickedListener() {
            @Override
            public void onBarClicked(int i) {
                showDetailedUsageInfo(i);
            }
        });
    }

    private void showDetailedUsageInfo(int dayCount) {
        Bundle bundle = new Bundle();
        bundle.putInt("DayCount", dayCount);
        bundle.putInt("Option", viewOption);
        DetailedPhoneUsageFragment fragment = new DetailedPhoneUsageFragment();
        fragment.setArguments(bundle);
        ViewHelper.injectFragment(fragment, getFragmentManager(), R.id.frame_container);
    }

    private class OnDetailsClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            ViewHelper.injectFragment(new LeaderboardFragment(), getActivity().getSupportFragmentManager(), R.id.frame_container);
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
