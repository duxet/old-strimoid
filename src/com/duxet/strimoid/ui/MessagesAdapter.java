package com.duxet.strimoid.ui;

import java.util.ArrayList;

import com.duxet.strimoid.R;
import com.duxet.strimoid.models.Message;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MessagesAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<Message> data;
    private LayoutInflater inflater = null;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    static class ViewHolder {
        TextView user, entry, time;
        ImageView avatar;
    }
    
    public MessagesAdapter(Activity a, ArrayList<Message> d) {
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
            holder.user = (TextView) vi.findViewById(R.id.header);
            holder.entry = (TextView) vi.findViewById(R.id.text);
            holder.time = (TextView) vi.findViewById(R.id.time);

            vi.setTag(holder);
        }
            
        Message message = data.get(position);
        ViewHolder holder = (ViewHolder) vi.getTag();

        boolean avatarsEnabled = PreferenceManager.
                getDefaultSharedPreferences(activity).getBoolean("show_avatar", true);
        
        holder.avatar.setImageResource(android.R.color.transparent);
        
        if (!message.getUserAvatar().equals("") && avatarsEnabled)
        {
            holder.avatar.setVisibility(View.VISIBLE);
            imageLoader.displayImage(message.getUserAvatar(), holder.avatar);
        } else {
            holder.avatar.setVisibility(View.GONE);
        }


        holder.user.setText(message.getUser());
        // TODO: holder.user.setTextColor(message.getUserColor());
        holder.entry.setText(message.getEntry());
        holder.time.setText(message.getTime());

        return vi;
    }
}
