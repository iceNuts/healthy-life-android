package com.blue_stingray.healthy_life_app.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.blue_stingray.healthy_life_app.App;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.net.form.validation.FormValidationManager;
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

/**
 * Provides a form to create a goal.
 */
public class CreateGoalFragment extends RoboFragment {

    @InjectView(R.id.app_spinner)
    private Spinner appSpinner;

    @InjectView(R.id.goal_type_spinner)
    private Spinner goalTypeSpinner;

    @InjectView(R.id.day_seek_bar)
    private SeekBar daySeekBar;

    @InjectView(R.id.day)
    private TextView currentDay;

    @InjectView(R.id.time_seek_bar)
    private SeekBar timeLimitSeekBar;

    @InjectView(R.id.time)
    private TextView currentTime;

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
        getActivity().setTitle(R.string.title_create_goal);

        validationManager = new FormValidationManager();

        // app spinner
        ArrayList<String> keys = new ArrayList<>(((App) getActivity().getApplication()).appCache.snapshot().keySet());
        Collections.sort(keys);
        ArrayAdapter<String> appAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, keys);
        appSpinner.setAdapter(appAdapter);

        // goal type spinner
        goalTypeSpinner.setAdapter(ArrayAdapter.createFromResource(getActivity(), R.array.goal_type, android.R.layout.simple_list_item_1));

        timeLimitSeekBar.setOnSeekBarChangeListener(new TimeLimitSeekBarListener());
        daySeekBar.setOnSeekBarChangeListener(new DaySeekBarListener());
        createGoalButton.setOnClickListener(new CreateGoalButtonListener());
    }

    private class CreateGoalButtonListener extends FormSubmitClickListener {

        public CreateGoalButtonListener() {
            super(getActivity(), validationManager, R.string.creating_goal);
        }

        @Override
        protected void submit() {
            rest.createGoal(new GoalForm(timeLimitSeekBar.getProgress() + 1, 0), new RetrofitDialogCallback<Goal>(getActivity(), progressDialog) {

                @Override
                public void onSuccess(Goal goal, Response response) {
                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(RetrofitError retrofitError) {
                    Toast.makeText(getActivity(), "Fail", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private class TimeLimitSeekBarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            currentTime.setText( (String.valueOf(progress + 1) + " hour") + (progress > 0 ? "s" : "") );
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }

    private class DaySeekBarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch(progress) {
                case 0:
                    currentDay.setText("Monday");
                    break;
                case 1:
                    currentDay.setText("Tuesday");
                    break;
                case 2:
                    currentDay.setText("Wednesday");
                    break;
                case 3:
                    currentDay.setText("Thursday");
                    break;
                case 4:
                    currentDay.setText("Friday");
                    break;
                case 5:
                    currentDay.setText("Saturday");
                    break;
                case 6:
                    currentDay.setText("Sunday");
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }

}
