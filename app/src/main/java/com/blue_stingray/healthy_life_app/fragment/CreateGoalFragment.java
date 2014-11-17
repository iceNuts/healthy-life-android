package com.blue_stingray.healthy_life_app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import com.blue_stingray.healthy_life_app.App;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.misc.FormValidationManager;
import com.blue_stingray.healthy_life_app.model.Goal;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.RetrofitDialogCallback;
import com.blue_stingray.healthy_life_app.net.form.FormSubmitClickListener;
import com.blue_stingray.healthy_life_app.net.form.GoalForm;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class CreateGoalFragment extends RoboFragment {

    @InjectView(R.id.app_spinner)
    private Spinner appSpinner;

    @InjectView(R.id.goal_type_spinner)
    private Spinner goalTypeSpinner;

    @InjectView(R.id.time_spinner)
    private Spinner timeLimitSpinner;

    @InjectView(R.id.create_goal)
    private Button createGoalButton;

    @Inject
    private RestInterface rest;

    private FormValidationManager validationManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_goal, container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Create Goal");

        validationManager = new FormValidationManager();

        // app spinner
        ArrayList<String> keys = new ArrayList<>(((App) getActivity().getApplication()).appCache.snapshot().keySet());
        Collections.sort(keys);
        ArrayAdapter<String> appAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, keys);
        appSpinner.setAdapter(appAdapter);

        // goal type spinner
        ArrayAdapter<CharSequence> goalTypeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.goal_type, android.R.layout.simple_list_item_1);
        goalTypeSpinner.setAdapter(goalTypeAdapter);

        // time limit spinner
        ArrayAdapter<CharSequence> timeLimitAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.time_limit, android.R.layout.simple_list_item_1);
        timeLimitSpinner.setAdapter(timeLimitAdapter);

        createGoalButton.setOnClickListener(new CreateGoalButtonListener());
    }

    private class CreateGoalButtonListener extends FormSubmitClickListener {

        public CreateGoalButtonListener() {
            super(getActivity(), validationManager, R.string.creating_goal);
        }

        @Override
        protected void submit() {
            rest.createGoal(new GoalForm(Integer.parseInt(timeLimitSpinner.getSelectedItem().toString().split(" ")[0]), 0), new RetrofitDialogCallback<Goal>(getActivity(), progressDialog) {

                @Override
                public void onSuccess(Goal goal, Response response) {
                    Toast.makeText(getActivity(), "SUCCESS", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(RetrofitError retrofitError) {
                    Toast.makeText(getActivity(), "FAIL", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
