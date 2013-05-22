package com.duxet.strimoid.ui;

import java.util.ArrayList;

import com.androidquery.AQuery;
import com.duxet.strimoid.R;
import com.duxet.strimoid.ContentActivity;
import com.duxet.strimoid.models.Content;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ContentsAdapter extends BaseAdapter implements OnClickListener {
    private Activity activity;
    private ArrayList<Content> data;
    private static LayoutInflater inflater = null;

    public ContentsAdapter(Activity a, ArrayList<Content> d) {
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
        AQuery aq = new AQuery(vi);

        if(convertView==null)
            vi = inflater.inflate(R.layout.activity_main_content, null);

        Content content = data.get(position);

        TextView title = (TextView) vi.findViewById(R.id.title);
        TextView desc = (TextView) vi.findViewById(R.id.desc);
        Button up = (Button) vi.findViewById(R.id.upvote);
        Button down = (Button) vi.findViewById(R.id.downvote);
        ImageView thumb_image = (ImageView) vi.findViewById(R.id.list_image);
        
        boolean thumbnailsEnabled = PreferenceManager.
                getDefaultSharedPreferences(activity).getBoolean("show_thumbnails", true);
        
        if (!content.getImageUrl().equals("") && thumbnailsEnabled) {
            thumb_image.setVisibility(View.VISIBLE);
            aq.id(R.id.list_image).image(content.getImageUrl(), false, true);
        } else {
            thumb_image.setVisibility(View.GONE);
        }

        title.setText(content.getTitle());
        desc.setText(content.getDesc());
        up.setText("▲ " + Integer.toString(content.getUpvotes()));
        up.setTag(content.getLikeUrl());
        down.setText("▼ " + Integer.toString(content.getDownvotes()));
        down.setTag(content.getDislikeUrl());

        vi.setOnClickListener(this);
        vi.setTag(position);
        
        return vi;
    }

    @Override
    public void onClick(View view) {
        int position = (Integer) view.getTag();

        Intent myIntent = new Intent(activity, ContentActivity.class);
        myIntent.putExtra("url", data.get(position).getUrl());
        myIntent.putExtra("commentsUrl", data.get(position).getCommentsUrl());
        myIntent.putExtra("title", data.get(position).getTitle());
        activity.startActivity(myIntent);
    }
}
