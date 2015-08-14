package com.blue_stingray.healthy_life_app.ui.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.Tip;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.RetrofitDialogCallback;
import com.blue_stingray.healthy_life_app.storage.db.DataHelper;
import com.blue_stingray.healthy_life_app.ui.adapter.DetailedPhoneUsageListAdapter;
import com.google.inject.Inject;

import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * Created by BillZeng on 2/16/15.
 */
public class DetailedPhoneUsageFragment extends RoboFragment {

    private DataHelper dataHelper;
    private List<DataHelper.DetailPhoneUsageTuple> detailedPhoneUsage;
    private ProgressDialog loading;

    @Inject
    private RestInterface rest;

    @InjectView(R.id.apps_usage_list)
    private ListView appUsageList;

    @InjectView(R.id.blank_message)
    private LinearLayout blank_message;

    @InjectView(R.id.tip_info)
    private TextView tipInfo;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getActivity().setTitle("Phone Usage Detail");
        loading = ProgressDialog.show(getActivity(), "Phone Usage", "loading...");
        return inflater.inflate(R.layout.fragment_detailed_phone_usage, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        blank_message.setVisibility(View.VISIBLE);
        createList();
        setupRandomTip();
    }

    private void createList() {
        int dayCount, option;
        try {
            Bundle bundle = getArguments();
            dayCount = bundle.getInt("DayCount");
            option = bundle.getInt("Option");
        }catch (Exception e) {
            dayCount = 0;
            option = 0;
        }
        dataHelper = DataHelper.getInstance(getActivity());
        detailedPhoneUsage = dataHelper.getDetailedPhoneUsage(dayCount, option);
        appUsageList.setClickable(false);
        DetailedPhoneUsageListAdapter adapter = new DetailedPhoneUsageListAdapter(getActivity(), detailedPhoneUsage, null);
        appUsageList.setAdapter(adapter);
        loading.cancel();
        if (detailedPhoneUsage.size() > 0) {
            blank_message.setVisibility(View.GONE);
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













