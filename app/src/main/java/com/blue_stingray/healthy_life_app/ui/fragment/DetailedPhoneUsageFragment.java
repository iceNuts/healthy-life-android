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

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.storage.db.DataHelper;
import com.blue_stingray.healthy_life_app.ui.adapter.DetailedPhoneUsageListAdapter;

import java.util.List;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * Created by BillZeng on 2/16/15.
 */
public class DetailedPhoneUsageFragment extends RoboFragment {

    private DataHelper dataHelper;
    private List<DataHelper.DetailPhoneUsageTuple> detailedPhoneUsage;
    private ProgressDialog loading;

    @InjectView(R.id.apps_usage_list)
    private ListView appUsageList;

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
        createList();
    }

    private void createList() {
        Bundle bundle = getArguments();
        int dayCount = bundle.getInt("DayCount");
        dataHelper = DataHelper.getInstance(getActivity());
        detailedPhoneUsage = dataHelper.getDetailedPhoneUsage(dayCount);
        DetailedPhoneUsageListAdapter adapter = new DetailedPhoneUsageListAdapter(getActivity(), detailedPhoneUsage, null);
        appUsageList.setAdapter(adapter);
        loading.cancel();
    }
}













