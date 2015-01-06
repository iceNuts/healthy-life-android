package com.blue_stingray.healthy_life_app.ui.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.blue_stingray.healthy_life_app.R;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class CreateUserFragment extends RoboFragment {

    @InjectView(R.id.password)
    private TextView passwordField;

    @InjectView(R.id.confirm)
    private TextView confirmField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_user, container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Workaround for monospace passwords and need to support ICS
        passwordField.setTypeface(Typeface.DEFAULT);
        confirmField.setTypeface(Typeface.DEFAULT);
    }

}
