package com.blue_stingray.healthy_life_app.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.IconTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.storage.db.SharedPreferencesHelper;
import com.blue_stingray.healthy_life_app.ui.widget.DrawerItem;
import java.util.List;

public class DrawerAdapter extends BaseListAdapter<DrawerItem> {

    private SharedPreferencesHelper prefs;
    int magicNumber;

    public DrawerAdapter(Activity activity, List<DrawerItem> list, int layout, int magicNumber, SharedPreferencesHelper prefs) {
        super(activity, list, layout);
        this.magicNumber = magicNumber;
        this.prefs = prefs;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);

        DrawerItem item = data.get(position);
        ((TextView) convertView.findViewById(R.id.title)).setText(item.title);
        ((IconTextView) convertView.findViewById(R.id.icon)).setText("{" + item.icon + "}");

        if (position == magicNumber) {
            if (prefs.getNewLifelineRequest() == true)
                ((ImageView) convertView.findViewById(R.id.red_dot)).setVisibility(View.VISIBLE);
            else
                ((ImageView) convertView.findViewById(R.id.red_dot)).setVisibility(View.GONE);
        }

        return convertView;
    }

}
