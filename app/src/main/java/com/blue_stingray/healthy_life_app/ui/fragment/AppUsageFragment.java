package com.blue_stingray.healthy_life_app.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.support.v4.app.Fragment;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.Application;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

public class AppUsageFragment extends Fragment {

    private View view;
    private Application app;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_app_usage, container,false);
        app = (Application) getArguments().getSerializable("appinfo");
        getActivity().setTitle(app.getName());
        if(app.hasGoal()) {
            view.findViewById(R.id.create_goal).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.current_goal).setVisibility(View.GONE);
        }
        setupChart();
        Button button = (Button) view.findViewById(R.id.create_goal);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new CreateGoalFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return view;
    }

    public void setupChart() {
        PieChart mPieChart = (PieChart) view.findViewById(R.id.piechart);

        mPieChart.addPieSlice(new PieModel(app.getName(), 50, getResources().getColor(R.color.blue_primary)));
        mPieChart.addPieSlice(new PieModel("Twitter", 25, getResources().getColor(R.color.orange_primary)));
        mPieChart.addPieSlice(new PieModel("Facebook", 35, getResources().getColor(R.color.green_primary)));

        mPieChart.startAnimation();
    }

}
