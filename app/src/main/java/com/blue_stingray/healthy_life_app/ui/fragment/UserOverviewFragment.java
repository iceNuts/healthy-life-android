package com.blue_stingray.healthy_life_app.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blue_stingray.healthy_life_app.R;

import roboguice.fragment.RoboFragment;

public class UserOverviewFragment extends RoboFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_overview, container, false);
        getActivity().setTitle(R.string.title_user_overview);
        return view;
    }

}
