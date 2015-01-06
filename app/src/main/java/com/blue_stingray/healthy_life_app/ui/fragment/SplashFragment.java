package com.blue_stingray.healthy_life_app.ui.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.google.inject.Inject;

import org.w3c.dom.Text;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class SplashFragment extends RoboFragment {

    @Inject
    private RestInterface rest;

    private Typeface delius;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);
        getActivity().setTitle(R.string.app_name);
        delius = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Delius-Regular.ttf");
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((TextView) view.findViewById(R.id.healthy_one)).setTypeface(delius);
        ((TextView) view.findViewById(R.id.healthy_two)).setTypeface(delius);
        ((TextView) view.findViewById(R.id.app)).setTypeface(delius);
        ((TextView) view.findViewById(R.id.life)).setTypeface(delius);
    }

}
