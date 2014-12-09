package com.blue_stingray.healthy_life_app.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.blue_stingray.healthy_life_app.R;

import java.util.Collections;
import java.util.List;

public abstract class BaseListAdapter<T> extends BaseAdapter {

    protected LayoutInflater inflater;
    protected List<T> data;
    protected int layout;

    public BaseListAdapter(Activity activity, List<T> list, int layout) {
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        data = list;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
        }
        return convertView;
    }

}
