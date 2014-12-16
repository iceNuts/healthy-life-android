package com.blue_stingray.healthy_life_app.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.IconTextView;
import android.widget.TextView;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.ui.widget.DrawerItem;
import java.util.List;

public class DrawerAdapter extends BaseListAdapter<DrawerItem> {

    public DrawerAdapter(Activity activity, List<DrawerItem> list, int layout) {
        super(activity, list, layout);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);

        DrawerItem item = data.get(position);
        ((TextView) convertView.findViewById(R.id.title)).setText(item.title);
        ((IconTextView) convertView.findViewById(R.id.icon)).setText("{" + item.icon + "}");

        return convertView;
    }

}
