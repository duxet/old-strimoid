package com.duxet.strimoid.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.duxet.strimoid.R;
import com.duxet.strimoid.models.Notification;

public class NotificationsAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<Notification> data;
    private LayoutInflater inflater = null;

    static class ViewHolder {
        TextView header, text, time;
        ImageView avatar;
    }
    
    public NotificationsAdapter(Activity a, ArrayList<Notification> d) {
        this.activity = a;
        this.data = d;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        if (vi == null) {
            vi = inflater.inflate(R.layout.activity_notifications_element, null);
            ViewHolder holder = new ViewHolder();
            
            holder.avatar = (ImageView) vi.findViewById(R.id.avatar);
            holder.header = (TextView) vi.findViewById(R.id.header);
            holder.text = (TextView) vi.findViewById(R.id.text);
            holder.time = (TextView) vi.findViewById(R.id.time);
            
            vi.setTag(holder);
        }
            
        Notification notification = data.get(position);
        ViewHolder holder = (ViewHolder) vi.getTag();

        holder.avatar.setVisibility(View.GONE);
        holder.header.setText(notification.getType());
        holder.text.setText(notification.getText());
        holder.time.setText(notification.getTime());

        return vi;
    }
}
