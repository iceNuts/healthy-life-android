package com.blue_stingray.healthy_life_app.ui.adapter;

import android.app.Activity;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

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

        ((TextView) convertView.findViewById(R.id.user)).setText(currentLifeline.user_name);
        ((TextView) convertView.findViewById(R.id.app_name)).setText(currentLifeline.app_name);
        ((TextView) convertView.findViewById(R.id.last_stat)).setText(currentLifeline.requested_at);

        convertView.findViewById(R.id.deny).setOnClickListener(new DenyClickListener(currentLifeline.id));
        convertView.findViewById(R.id.approve).setOnClickListener(new AcceptClickListener(currentLifeline.id));

        return convertView;
    }

    public class DenyClickListener implements View.OnClickListener {

        private String id;

        public DenyClickListener(String id) {
            this.id = id;
        }

        @Override
        public void onClick(View v) {
            rest.updateLifeline(
                new Integer(id),
                new LifelineUpdateForm(
                    null,
                    getCurrentDatetime()
                ),
                new RetrofitDialogCallback<Lifeline>(
                    activity,
                    null
                ) {
                    @Override
                    public void onSuccess(Lifeline lifeline, Response response) {/*not much to do*/}
                    @Override
                    public void onFailure(RetrofitError retrofitError) {/*not much to do*/}
                }
            );
            Toast.makeText(activity, "Deny", Toast.LENGTH_SHORT).show();
        }
    }

    public class AcceptClickListener implements View.OnClickListener {

        private String id;

        public AcceptClickListener(String id) {
            this.id = id;
        }


        @Override
        public void onClick(View v) {
            rest.updateLifeline(
                    new Integer(id),
                    new LifelineUpdateForm(
                        getCurrentDatetime(),
                        null
                    ),
                    new RetrofitDialogCallback<Lifeline>(
                        activity,
                        null
                    ) {
                        @Override
                        public void onSuccess(Lifeline lifeline, Response response) {/*not much to do*/}
                        @Override
                        public void onFailure(RetrofitError retrofitError) {/*not much to do*/}
                    }
            );
            Toast.makeText(activity, "Accept", Toast.LENGTH_SHORT).show();
        }
    }

    private String getCurrentDatetime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(c.getTime());
        return strDate;
    }

}
