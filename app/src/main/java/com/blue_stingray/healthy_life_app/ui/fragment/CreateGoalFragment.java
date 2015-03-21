package com.blue_stingray.healthy_life_app.ui.fragment;

import android.app.ProgressDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.blue_stingray.healthy_life_app.model.Goal;
import com.blue_stingray.healthy_life_app.net.RetrofitDialogCallback;
import com.blue_stingray.healthy_life_app.net.form.AppForm;
import com.blue_stingray.healthy_life_app.net.form.GoalForm;
import com.blue_stingray.healthy_life_app.net.form.ManyGoalForm;
import com.blue_stingray.healthy_life_app.net.form.validation.FormValidationManager;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.form.FormSubmitClickListener;
import com.blue_stingray.healthy_life_app.storage.db.DataHelper;
import com.blue_stingray.healthy_life_app.storage.db.SharedPreferencesHelper;
import com.blue_stingray.healthy_life_app.util.Time;
import com.google.inject.Inject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import android.util.Log;
import android.widget.Toast;

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

    @Inject
    private SharedPreferencesHelper prefs;

    private FormValidationManager validationManager;

    private DataHelper dataHelper;

    final private int magicStep = 15;

    private String viewUserID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewUserID = getArguments().getString("userID");
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

        // Set increment step
        mondaySeekBar.incrementProgressBy(magicStep);
        tuesdaySeekBar.incrementProgressBy(magicStep);
        wednesdaySeekBar.incrementProgressBy(magicStep);
        thursdaySeekBar.incrementProgressBy(magicStep);
        fridaySeekBar.incrementProgressBy(magicStep);
        saturdaySeekBar.incrementProgressBy(magicStep);
        sundaySeekBar.incrementProgressBy(magicStep);

        mondaySeekBar.setProgress(0);
        tuesdaySeekBar.setProgress(0);
        wednesdaySeekBar.setProgress(0);
        thursdaySeekBar.setProgress(0);
        fridaySeekBar.setProgress(0);
        saturdaySeekBar.setProgress(0);
        sundaySeekBar.setProgress(0);
    }

    private class CreateGoalButtonListener extends FormSubmitClickListener {

        public CreateGoalButtonListener() {
            super(getActivity(), validationManager, R.string.creating_goal);
        }

        @Override
        protected void submit() {
            final Application app = ((App) getActivity().getApplication()).appCache.get(appSpinner.getSelectedItem().toString());
            PackageManager manager = getActivity().getPackageManager();
            String appVersion = "1.0.0";
            byte[] iconData = null;
            try {
                PackageInfo info = manager.getPackageInfo(app.getPackageName(), 0);
                appVersion = info.versionName;
                Drawable appIcon = manager.getApplicationIcon(app.getPackageName());
                Bitmap bitmap = ((BitmapDrawable) appIcon).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                iconData = stream.toByteArray();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            HashMap<Integer, Double> dayMap = getDayHours();
            dataHelper.createNewGoal(app.getPackageName(), dayMap, true);
            final Map<Integer, Double> conDayMap = new ConcurrentHashMap<Integer, Double>(dayMap);
            final Iterator it = conDayMap.entrySet().iterator();
            progressDialog = ProgressDialog.show(getActivity(), "", "Loading...");
            rest.createApp(
                    new AppForm(
                            app.getPackageName(),
                            app.getName(),
                            appVersion,
                            iconData == null ? "" : iconData.toString()
                    ),
                    new RetrofitDialogCallback<Application>(
                            getActivity(),
                            null
                    ) {
                        @Override
                        public void onSuccess(Application application, Response response) {
                            final Iterator it = conDayMap.entrySet().iterator();
                            ArrayList<GoalForm> goalForms = new ArrayList<>();
                            while(it.hasNext()) {
                                Map.Entry data = (Map.Entry) it.next();
                                String dayString = DayTranslate((Integer)data.getKey());
                                Double hours = (Double)data.getValue();
                                goalForms.add(
                                        new GoalForm(
                                                app,
                                                hours,
                                                dayString,
                                                prefs.getDeviceId()
                                        )
                                );
                                it.remove();
                            }
                            int deviceID = app.getDeviceId();
                            if (-1 == app.getDeviceId()) {
                                deviceID = prefs.getDeviceId();
                            }
                            ManyGoalForm goalForm = new ManyGoalForm(deviceID, goalForms.toArray(new GoalForm[goalForms.size()]));
                            rest.createGoalMany(goalForm, new CreateManyGoalsCallback(progressDialog, app));
                        }

                        @Override
                        public void onFailure(RetrofitError retrofitError) {
                        /*nothing much to do*/
                            Toast.makeText(getActivity(), "Create Goal Failed", Toast.LENGTH_LONG);
                        }
                    }
            );
        }
    }

    private class CreateManyGoalsCallback implements Callback<List<Goal>> {

        private ProgressDialog progressDialog;
        private Application app;

        public CreateManyGoalsCallback(ProgressDialog progressDialog, Application app) {
            this.progressDialog = progressDialog;
            this.app = app;
        }

        @Override
        public void success(List<Goal> goals, Response response) {
            if(viewUserID.equals(String.valueOf(prefs.getCurrentUser().getId()))) {
                for(Goal goal : goals) {
                    HashMap<Integer, Double> newGoalMap = new HashMap<>();
                    newGoalMap.put(Time.dayTranslate(goal.getDay()), goal.getGoalTime());
                    dataHelper.createNewGoal(goal.getApp().getPackageName(), newGoalMap, true);
                }
            } else {
                app.setActiveGoals(goals.toArray(new Goal[goals.size()]));
            }

            getFragmentManager().popBackStack();
            Toast.makeText(getActivity(), "Successful Edit", Toast.LENGTH_LONG).show();

            progressDialog.cancel();
        }

        @Override
        public void failure(RetrofitError error) {
            progressDialog.cancel();
        }
    }

    private class TimeLimitSeekBarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            String time;
            progress *= 15;
            if (progress < 60) {
                time = String.valueOf(progress)+" minutes";
            }
            else {
                time = String.valueOf(progress/60)+" hours "+String.valueOf(progress%60)+" min";
            }

            switch(getResources().getResourceName(seekBar.getId())) {
                case "com.blue_stingray.healthy_life_app:id/monday_seek_bar":
                    monday.setText(time);
                    break;
                case "com.blue_stingray.healthy_life_app:id/tuesday_seek_bar":
                    tuesday.setText(time);
                    break;
                case "com.blue_stingray.healthy_life_app:id/wednesday_seek_bar":
                    wednesday.setText(time);
                    break;
                case "com.blue_stingray.healthy_life_app:id/thursday_seek_bar":
                    thursday.setText(time);
                    break;
                case "com.blue_stingray.healthy_life_app:id/friday_seek_bar":
                    friday.setText(time);
                    break;
                case "com.blue_stingray.healthy_life_app:id/saturday_seek_bar":
                    saturday.setText(time);
                    break;
                case "com.blue_stingray.healthy_life_app:id/sunday_seek_bar":
                    sunday.setText(time);
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }

    public String DayTranslate(Integer day) {
        if(day == Calendar.MONDAY) {
            return "Mon";
        }
        else if (day == Calendar.TUESDAY) {
            return "Tue";
        }
        else if (day == Calendar.WEDNESDAY) {
            return "Wed";
        }
        else if (day == Calendar.THURSDAY) {
            return "Thu";
        }
        else if (day == Calendar.FRIDAY) {
            return "Fri";
        }
        else if (day == Calendar.SATURDAY) {
            return "Sat";
        }
        else {
            return "Sun";
        }
    }

    public HashMap<Integer, Double> getDayHours() {
        HashMap<Integer, Double> dayMap = new HashMap<>();
        dayMap.put(Calendar.MONDAY, (double)mondaySeekBar.getProgress()/4);
        dayMap.put(Calendar.TUESDAY, (double)tuesdaySeekBar.getProgress()/4);
        dayMap.put(Calendar.WEDNESDAY, (double)wednesdaySeekBar.getProgress()/4);
        dayMap.put(Calendar.THURSDAY, (double)thursdaySeekBar.getProgress()/4);
        dayMap.put(Calendar.FRIDAY, (double)fridaySeekBar.getProgress()/4);
        dayMap.put(Calendar.SATURDAY, (double)saturdaySeekBar.getProgress()/4);
        dayMap.put(Calendar.SUNDAY, (double)sundaySeekBar.getProgress()/4);
        return dayMap;
    }

}
