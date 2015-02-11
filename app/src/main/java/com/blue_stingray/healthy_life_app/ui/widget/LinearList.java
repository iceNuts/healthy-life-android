package com.blue_stingray.healthy_life_app.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.blue_stingray.healthy_life_app.ui.adapter.BaseListAdapter;

public class LinearList extends LinearLayout {

    public LinearList(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LinearList(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public LinearList(Context context) {
        super(context);
    }

    public void setAdapter(BaseListAdapter adapter) {
        setAdapter(adapter, null);
    }

    public void setAdapter(BaseListAdapter adapter, OnClickListener listener) {
        final int adapterCount = adapter.getCount();

        for (int i = 0; i < adapterCount; i++) {
            View item = adapter.getView(i, null, null);
            item.setTag(i);

            if(listener != null) {
                item.setOnClickListener(listener);
            }

            this.addView(item);
        }
    }
}
