package com.blue_stingray.healthy_life_app.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.MentorRequest;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.form.UpdateMentorRequestForm;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by BillZeng on 2/10/15.
 */
public class MentorRequestListAdapter extends BaseListAdapter<MentorRequest> {

    Activity activity;
    private List<MentorRequest> mentorRequestList;
    private MentorRequest currentMentorRequest;
    private RestInterface rest;

    public MentorRequestListAdapter(Activity activity, List<MentorRequest> list, RestInterface rest) {
        super(activity, list, R.layout.mentor_request_list_row);
        this.activity = activity;
        this.mentorRequestList = list;
        this.rest = rest;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);
        currentMentorRequest = this.mentorRequestList.get(position);

        ((TextView) convertView.findViewById(R.id.user)).setText(currentMentorRequest.child_name);
        ((TextView) convertView.findViewById(R.id.last_stat)).setText(currentMentorRequest.requested_at);

        View denyButton = convertView.findViewById(R.id.deny);
        View approveButton = convertView.findViewById(R.id.approve);

        denyButton.setTag(position);
        approveButton.setTag(position);

        convertView.findViewById(R.id.deny).setOnClickListener(new DenyClickListener(this, currentMentorRequest.id));
        convertView.findViewById(R.id.approve).setOnClickListener(new AcceptClickListener(this, currentMentorRequest.id));

        return convertView;
    }

    public class DenyClickListener implements View.OnClickListener {

        private MentorRequestListAdapter adapter;
        private int mentorRequestId;

        public DenyClickListener(MentorRequestListAdapter adapter, String id) {
            this.mentorRequestId = Integer.valueOf(id);
            this.adapter = adapter;
        }

        @Override
        public void onClick(final View v) {
            rest.updateMentorRequest(
                mentorRequestId,
                new UpdateMentorRequestForm(false),
                new Callback<Object>() {
                    @Override
                    public void success(Object o, Response response) {
                        mentorRequestList.remove((int) v.getTag());
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void failure(RetrofitError error) {}
                }
            );
            Toast.makeText(activity, "Request Denied", Toast.LENGTH_SHORT).show();
        }
    }

    public class AcceptClickListener implements View.OnClickListener {

        private MentorRequestListAdapter adapter;
        private int mentorRequestId;

        public AcceptClickListener(MentorRequestListAdapter adapter, String id) {
            this.mentorRequestId = Integer.valueOf(id);
            this.adapter = adapter;
        }

        @Override
        public void onClick(final View v) {
            rest.updateMentorRequest(
                    mentorRequestId,
                    new UpdateMentorRequestForm(true),
                    new Callback<Object>() {
                        @Override
                        public void success(Object o, Response response) {
                            mentorRequestList.remove((int) v.getTag());
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void failure(RetrofitError error) {}
                    }
            );
            Toast.makeText(activity, "Request Accepted", Toast.LENGTH_SHORT).show();
        }
    }

}
