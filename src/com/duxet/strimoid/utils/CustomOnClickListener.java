package com.duxet.strimoid.utils;

import android.view.View;
import android.view.View.OnClickListener;

public class CustomOnClickListener implements OnClickListener {
    private int position;
    private OnCustomClickListener callback;

    public CustomOnClickListener(OnCustomClickListener callback, int pos) {
        position = pos;
        this.callback = callback;
    }

    @Override
    public void onClick(View v) {
        callback.onCustomClick(v, position);
    }
}
