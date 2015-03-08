package com.blue_stingray.healthy_life_app.ui.adapter;

import android.app.Activity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.IconTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.Lifeline;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.RetrofitDialogCallback;
import com.blue_stingray.healthy_life_app.net.form.LifelineUpdateForm;
import com.google.inject.Inject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LifelineRequestListAdapter extends BaseListAdapter<Lifeline> {

    Activity activity;
    private List<Lifeline> lifelines;
    private Lifeline currentLifeline;
    private RestInterface rest;

    public LifelineRequestListAdapter(Activity activity, List<Lifeline> data, RestInterface rest) {
        super(activity, data, R.layout.lifeline_request_list_row);
        this.activity = activity;
        this.lifelines = data;
        this.rest = rest;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);
        currentLifeline = this.lifelines.get(position);

        String localZoneTime = getLocalZoneTime(currentLifeline.requested_at);

        ((TextView) convertView.findViewById(R.id.user)).setText(currentLifeline.user_name);
        ((TextView) convertView.findViewById(R.id.app_name)).setText(currentLifeline.app_name);
        ((TextView) convertView.findViewById(R.id.last_stat)).setText(localZoneTime);

        View denyButton = convertView.findViewById(R.id.deny);
        View approveButton = convertView.findViewById(R.id.approve);

        denyButton.setTag(position);
        approveButton.setTag(position);

        convertView.findViewById(R.id.deny).setOnClickListener(new DenyClickListener(this, currentLifeline.id));
        convertView.findViewById(R.id.approve).setOnClickListener(new AcceptClickListener(this, currentLifeline.id));

        return convertView;
    }

    public class DenyClickListener implements View.OnClickListener {

        private LifelineRequestListAdapter adapter;
        private int lifelineId;

        public DenyClickListener(LifelineRequestListAdapter adapter, String id) {
            this.lifelineId = Integer.valueOf(id);
            this.adapter = adapter;
        }

        @Override
        public void onClick(final View v) {
            rest.updateLifeline(
                lifelineId,
                new LifelineUpdateForm(
                    null,
                    getCurrentDatetime()
                ),
                new RetrofitDialogCallback<Lifeline>(
                    activity,
                    null
                ) {
                    @Override
                    public void onSuccess(Lifeline lifeline, Response response) {

                        rest.destroyLifeline(
                                lifelineId,
                                new RetrofitDialogCallback<Object>(
                                        activity,
                                        null
                                ) {
                            @Override
                            public void onSuccess(Object o, Response response) {
                                lifelines.remove((int) v.getTag());
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(RetrofitError error) {}
                        });

                    }
                    @Override
                    public void onFailure(RetrofitError retrofitError) {}
                }
            );
            Toast.makeText(activity, "Request Denied", Toast.LENGTH_LONG).show();
        }
    }

    public class AcceptClickListener implements View.OnClickListener {

        private LifelineRequestListAdapter adapter;
        private int lifelineId;

        public AcceptClickListener(LifelineRequestListAdapter adapter, String id) {
            this.lifelineId = Integer.valueOf(id);
            this.adapter = adapter;
        }


        @Override
        public void onClick(final View v) {
            rest.updateLifeline(
                    lifelineId,
                    new LifelineUpdateForm(
                        getCurrentDatetime(),
                        null
                    ),
                    new RetrofitDialogCallback<Lifeline>(
                        activity,
                        null
                    ) {
                        @Override
                        public void onSuccess(Lifeline lifeline, Response response) {

                            rest.destroyLifeline(
                                    lifelineId,
                                    new RetrofitDialogCallback<Object>(
                                            activity,
                                            null
                                    ) {
                                @Override
                                public void onSuccess(Object o, Response response) {
                                    lifelines.remove((int) v.getTag());
                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onFailure(RetrofitError error) {}
                            });

                        }
                        @Override
                        public void onFailure(RetrofitError retrofitError) {/*not much to do*/}
                    }
            );
            Toast.makeText(activity, "Request Accepted", Toast.LENGTH_LONG).show();
        }
    }

    private String getCurrentDatetime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(c.getTime());
        return strDate;
    }

    private String getLocalZoneTime(String formattedDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = formatter.parse(formattedDate);
            formatter.setTimeZone(TimeZone.getDefault());
            return formatter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return formattedDate;
        }
    }

}
