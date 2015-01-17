package com.blue_stingray.healthy_life_app.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.User;

import org.w3c.dom.Text;

import java.util.List;

public class UserListAdapter extends BaseListAdapter<User> {

    public UserListAdapter(Activity activity, List<User> users) {
        super(activity, users, R.layout.user_list_row);
    }

    public UserListAdapter(Activity activity, List<User> users, int layout) {
        super(activity, users, layout);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);

        User user = data.get(position);
        TextView name = ((TextView) convertView.findViewById(R.id.user_name));
        TextView score = ((TextView) convertView.findViewById(R.id.score));
        TextView email = ((TextView) convertView.findViewById(R.id.email));

        if(name != null) {
            name.setText(user.getName());
        }

        if(score != null) {
            score.setText(String.valueOf(user.getScore()));
        }

        if(email != null) {
            email.setText(user.getEmail());
        }

        return convertView;
    }

}
