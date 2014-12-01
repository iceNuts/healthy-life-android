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
import com.blue_stingray.healthy_life_app.App;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.Application;
import com.blue_stingray.healthy_life_app.net.form.validation.FormValidationManager;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.form.FormSubmitClickListener;
import com.blue_stingray.healthy_life_app.storage.db.DataHelper;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * Provides a form to create a goal.
 */
public class CreateGoalFragment extends RoboFragment {

    @InjectView(R.id.create_goal)
    private Button createGoalButton;

    @InjectView(R.id.app_spinner)
    private Spinner appSpinner;

    @InjectView(R.id.monday)
    private TextView monday;

    @InjectView(R.id.tuesday)
    private TextView tuesday;

    @InjectView(R.id.wednesday)
    private TextView wednesday;

    @InjectView(R.id.thursday)
    private TextView thursday;

    @InjectView(R.id.friday)
    private TextView friday;

    @InjectView(R.id.saturday)
    private TextView saturday;

    @InjectView(R.id.sunday)
    private TextView sunday;

    @InjectView(R.id.monday_seek_bar)
    private SeekBar mondaySeekBar;

    @InjectView(R.id.tuesday_seek_bar)
    private SeekBar tuesdaySeekBar;

    @InjectView(R.id.wednesday_seek_bar)
    private SeekBar wednesdaySeekBar;

    @InjectView(R.id.thursday_seek_bar)
    private SeekBar thursdaySeekBar;

    @InjectView(R.id.friday_seek_bar)
    private SeekBar fridaySeekBar;

    @InjectView(R.id.saturday_seek_bar)
    private SeekBar saturdaySeekBar;

    @InjectView(R.id.sunday_seek_bar)
    private SeekBar sundaySeekBar;

    @Inject
    private RestInterface rest;

    private FormValidationManager validationManager;

    private DataHelper dataHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_goal, container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.title_create_goal);

        validationManager = new FormValidationManager();
        dataHelper = DataHelper.getInstance(getActivity().getApplicationContext());

        // app spinner
        ArrayList<String> keys = new ArrayList<>(((App) getActivity().getApplication()).appCache.snapshot().keySet());
        Collections.sort(keys);
        appSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, keys));

        // set default spinner app
        Bundle args = getArguments();
        if(args != null) {
            String appName = args.getString("appName");
            if(appName != null) {
                appSpinner.setSelection(keys.indexOf(args.getString("appName")));
            }
        }

        createGoalButton.setOnClickListener(new CreateGoalButtonListener());

        mondaySeekBar.setOnSeekBarChangeListener(new TimeLimitSeekBarListener());
        tuesdaySeekBar.setOnSeekBarChangeListener(new TimeLimitSeekBarListener());
        wednesdaySeekBar.setOnSeekBarChangeListener(new TimeLimitSeekBarListener());
        thursdaySeekBar.setOnSeekBarChangeListener(new TimeLimitSeekBarListener());
        fridaySeekBar.setOnSeekBarChangeListener(new TimeLimitSeekBarListener());
        saturdaySeekBar.setOnSeekBarChangeListener(new TimeLimitSeekBarListener());
        sundaySeekBar.setOnSeekBarChangeListener(new TimeLimitSeekBarListener());
    }

    private class CreateGoalButtonListener extends FormSubmitClickListener {

        public CreateGoalButtonListener() {
            super(getActivity(), validationManager, R.string.creating_goal);
        }

        @Override
        protected void submit() {

            Application app = ((App) getActivity().getApplication()).appCache.get(appSpinner.getSelectedItem().toString());
            HashMap<Integer, Integer> dayMap = getDayHours();

            dataHelper.createNewGoal(app, dayMap);

            progressDialog.dismiss();

            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private class TimeLimitSeekBarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            String hours = (String.valueOf(progress + 1) + " hour") + (progress > 0 ? "s" : "");

            switch(getResources().getResourceName(seekBar.getId())) {
                case "com.blue_stingray.healthy_life_app:id/monday_seek_bar":
                    monday.setText(hours);
                    break;
                case "com.blue_stingray.healthy_life_app:id/tuesday_seek_bar":
                    tuesday.setText(hours);
                    break;
                case "com.blue_stingray.healthy_life_app:id/wednesday_seek_bar":
                    wednesday.setText(hours);
                    break;
                case "com.blue_stingray.healthy_life_app:id/thursday_seek_bar":
                    thursday.setText(hours);
                    break;
                case "com.blue_stingray.healthy_life_app:id/friday_seek_bar":
                    friday.setText(hours);
                    break;
                case "com.blue_stingray.healthy_life_app:id/saturday_seek_bar":
                    saturday.setText(hours);
                    break;
                case "com.blue_stingray.healthy_life_app:id/sunday_seek_bar":
                    sunday.setText(hours);
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }

    public HashMap<Integer, Integer> getDayHours() {
        HashMap<Integer, Integer> dayMap = new HashMap<>();
        dayMap.put(Calendar.MONDAY, mondaySeekBar.getProgress() + 1);
        dayMap.put(Calendar.TUESDAY, tuesdaySeekBar.getProgress() + 1);
        dayMap.put(Calendar.WEDNESDAY, wednesdaySeekBar.getProgress() + 1);
        dayMap.put(Calendar.THURSDAY, thursdaySeekBar.getProgress() + 1);
        dayMap.put(Calendar.FRIDAY, fridaySeekBar.getProgress() + 1);
        dayMap.put(Calendar.SATURDAY, saturdaySeekBar.getProgress() + 1);
        dayMap.put(Calendar.SUNDAY, sundaySeekBar.getProgress() + 1);
        return dayMap;
    }

}
