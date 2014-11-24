package com.blue_stingray.healthy_life_app.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.blue_stingray.healthy_life_app.R;

/**
 * Provides a single users ratings.
 */
public class RatingsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_ratings, container,false);
        getActivity().setTitle("My Rating");
        return view;
    }
}
