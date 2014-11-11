package com.blue_stingray.healthy_life_app.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.blue_stingray.healthy_life_app.R;

public class CreateGoalFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_create_goal, container,false);
        getActivity().setTitle("Create Goal");
        return view;
    }
}
