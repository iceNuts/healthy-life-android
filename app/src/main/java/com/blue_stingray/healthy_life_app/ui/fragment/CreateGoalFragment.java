package com.blue_stingray.healthy_life_app.ui.fragment;

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
import com.blue_stingray.healthy_life_app.net.form.validation.FormValidationManager;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.form.FormSubmitClickListener;
import com.blue_stingray.healthy_life_app.storage.db.DataHelper;
import com.google.inject.Inject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import android.util.Log;

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
            HashMap<Integer, Integer> dayMap = getDayHours();
            dataHelper.createNewGoal(app, dayMap);
            Map<Integer, Integer> conDayMap = new ConcurrentHashMap<Integer, Integer>(dayMap);
            final Iterator it = conDayMap.entrySet().iterator();
            progressDialog.show();
            rest.createApp(
                new AppForm(
                    app.getPackageName(),
                    app.getName(),
                    appVersion,
                    iconData == null? "" : iconData.toString()
                ),
                new RetrofitDialogCallback<Application>(
                    getActivity(),
                    null
                ) {
                    @Override
                    public void onSuccess(Application application, Response response) {
                        while(it.hasNext()) {
                            Map.Entry data = (Map.Entry) it.next();
                            String dayString = DayTranslate((Integer)data.getKey());
                            Integer hours = (Integer)data.getValue();
                            rest.createGoal(
                                    new GoalForm(
                                            app.getPackageName(),
                                            hours,
                                            dayString
                                    ),
                                    new RetrofitDialogCallback<Goal>(
                                            getActivity(),
                                            progressDialog) {
                                        @Override
                                        public void onSuccess(Goal goal, Response response) {Log.d("REST", String.valueOf(response.getStatus()));}
                                        @Override
                                        public void onFailure(RetrofitError retrofitError) {Log.d("REST", retrofitError.toString());}
                                    }
                            );
                            it.remove();
                        }
                    }
                    @Override
                    public void onFailure(RetrofitError retrofitError) {/*nothing much to do*/}
                }
            );
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
