package com.duxet.strimoid.ui;

import java.util.ArrayList;

import com.androidquery.AQuery;
import com.duxet.strimoid.R;
import com.duxet.strimoid.models.Entry;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class EntriesAdapter extends BaseAdapter implements OnClickListener {
    private Activity activity;
    private ArrayList<Entry> data;
    private static LayoutInflater inflater = null;

    public EntriesAdapter(Activity a, ArrayList<Entry> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        AQuery aq = new AQuery(vi);
        Entry entry = data.get(position);

        if(convertView==null)
            vi = inflater.inflate(R.layout.activity_main_entry, null);

        TextView author = (TextView) vi.findViewById(R.id.author);
        TextView message = (TextView) vi.findViewById(R.id.message);
        TextView time = (TextView) vi.findViewById(R.id.time);
        Button up = (Button) vi.findViewById(R.id.upvote);
        Button down = (Button) vi.findViewById(R.id.downvote);
        ImageView reply = (ImageView) vi.findViewById(R.id.reply);
        ImageView thumb_image = (ImageView) vi.findViewById(R.id.list_image);
        
        boolean thumbnailsEnabled = PreferenceManager.
                getDefaultSharedPreferences(activity).getBoolean("show_thumbnails", true);

        if (!entry.getAvatar().equals("") && thumbnailsEnabled) {
            thumb_image.setVisibility(View.VISIBLE);
            aq.id(R.id.list_image).image(entry.getAvatar(), false, true);
        } else {
            thumb_image.setVisibility(View.GONE);
        }

        if (!entry.isReply()) {
            reply.setVisibility(View.GONE);
            time.setText(entry.getTime() + " w " + entry.getStrim());
        } else {
            reply.setVisibility(View.VISIBLE);
            time.setText(entry.getTime());
        }

        author.setText(entry.getAuthor());
        message.setText(entry.getMessage());

        up.setText("▲ " + Integer.toString(entry.getUpvotes()));
        down.setText("▼ " + Integer.toString(entry.getDownvotes()));

        vi.setOnClickListener(this);
        vi.setTag(position);
        
        return vi;
    }

    @Override
    public void onClick(View view) {
    }
}
