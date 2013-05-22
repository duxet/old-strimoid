package com.duxet.strimoid.ui;

import java.util.ArrayList;

import com.duxet.strimoid.R;
import com.duxet.strimoid.models.Strim;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StrimsAdapter extends BaseAdapter implements OnClickListener {
    private Activity activity;
    private ArrayList<Strim> data;
    private static LayoutInflater inflater = null;

    public StrimsAdapter(Activity a, ArrayList<Strim> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;

        if(convertView==null)
            vi = inflater.inflate(R.layout.activity_main_strims, null);

        Strim strim = data.get(position);

        TextView title = (TextView) vi.findViewById(R.id.title);
        title.setText(strim.getTitle());

        //vi.setOnClickListener(this);
        //vi.setTag(position);
        
        return vi;
    }

    @Override
    public void onClick(View v) {
        
    }
}
