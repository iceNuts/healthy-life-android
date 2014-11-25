package com.blue_stingray.healthy_life_app.ui.fragment;

import android.graphics.drawable.GradientDrawable;
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
import com.blue_stingray.healthy_life_app.model.Application;
import com.blue_stingray.healthy_life_app.net.form.validation.FormValidationManager;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.form.FormSubmitClickListener;
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

    @InjectView(R.id.app_spinner)
    private Spinner appSpinner;

    @InjectView(R.id.time_seek_bar)
    private SeekBar timeLimitSeekBar;

    @InjectView(R.id.time)
    private TextView currentTime;

    @InjectView(R.id.create_goal)
    private Button createGoalButton;

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

    @Inject
    private RestInterface rest;

    private FormValidationManager validationManager;

    private HashMap<Integer, Boolean> days;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_goal, container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.title_create_goal);

        validationManager = new FormValidationManager();
        days = new HashMap<>();

        // app spinner
        ArrayList<String> keys = new ArrayList<>(((App) getActivity().getApplication()).appCache.snapshot().keySet());
        Collections.sort(keys);
        ArrayAdapter<String> appAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, keys);
        appSpinner.setAdapter(appAdapter);

        timeLimitSeekBar.setOnSeekBarChangeListener(new TimeLimitSeekBarListener());
        createGoalButton.setOnClickListener(new CreateGoalButtonListener());

        monday.setOnClickListener(new DayPickerListener());
        tuesday.setOnClickListener(new DayPickerListener());
        wednesday.setOnClickListener(new DayPickerListener());
        thursday.setOnClickListener(new DayPickerListener());
        friday.setOnClickListener(new DayPickerListener());
        saturday.setOnClickListener(new DayPickerListener());
        sunday.setOnClickListener(new DayPickerListener());

        setDay(monday, R.color.white, R.color.blue_primary);
        setDay(tuesday, R.color.white, R.color.blue_primary);
        setDay(wednesday, R.color.white, R.color.blue_primary);
        setDay(thursday, R.color.white, R.color.blue_primary);
        setDay(friday, R.color.white, R.color.blue_primary);
        setDay(saturday, R.color.white, R.color.blue_primary);
        setDay(sunday, R.color.white, R.color.blue_primary);
    }

    private class CreateGoalButtonListener extends FormSubmitClickListener {

        public CreateGoalButtonListener() {
            super(getActivity(), validationManager, R.string.creating_goal);
        }

        @Override
        protected void submit() {

            // TODO
            ArrayList<Integer> selectedDays = getSelectedDays();
            int hours = timeLimitSeekBar.getProgress() + 1;
            int appId = ((App) getActivity().getApplication()).appCache.get((String) appSpinner.getSelectedItem()).id;

            Toast.makeText(getActivity(), "todo", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
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

    private class DayPickerListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(days.get(v.getId()) != null && days.get(v.getId())) {
                setDay(v, R.color.white, R.color.blue_primary);
                days.put(v.getId(), false);
            } else {
                setDay(v, R.color.blue_primary, R.color.white);
                days.put(v.getId(), true);
            }
        }
    }

    /**
     * Set a single view day element colors
     * @param v View
     * @param background int
     * @param textColor int
     */
    private void setDay(View v, int background, int textColor) {
        GradientDrawable shape = (GradientDrawable) v.getBackground();
        shape.setColor(getResources().getColor(background));
        ((TextView) v).setTextColor(getResources().getColor(textColor));
    }

    /**
     * Get a list of calendars day constants based on selected days in the UI
     * @return ArrayList<Integer> selected days
     */
    private ArrayList<Integer> getSelectedDays() {
        TextView[] dayViews = new TextView[]{
                monday,
                tuesday,
                wednesday,
                thursday,
                friday,
                saturday,
                sunday
        };
        ArrayList<Integer> daysSelected = new ArrayList<>();

        for(int i = 0; i < dayViews.length; i++) {
            TextView day = dayViews[i];

            if(days.get(day.getId()) != null && days.get(day.getId())) {
                switch(i) {
                    case 0:
                        daysSelected.add(Calendar.MONDAY);
                        break;
                    case 1:
                        daysSelected.add(Calendar.TUESDAY);
                        break;
                    case 2:
                        daysSelected.add(Calendar.WEDNESDAY);
                        break;
                    case 3:
                        daysSelected.add(Calendar.THURSDAY);
                        break;
                    case 4:
                        daysSelected.add(Calendar.FRIDAY);
                        break;
                    case 5:
                        daysSelected.add(Calendar.SATURDAY);
                        break;
                    case 6:
                        daysSelected.add(Calendar.SUNDAY);
                        break;
                }
            }
        }

        return daysSelected;
    }

}
