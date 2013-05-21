package com.duxet.strimoid.ui;

import java.util.ArrayList;

import com.androidquery.AQuery;
import com.duxet.strimoid.R;
import com.duxet.strimoid.models.Comment;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CommentsAdapter extends BaseAdapter implements OnClickListener {
    private Activity activity;
    private ArrayList<Comment> data;
    private static LayoutInflater inflater = null;

    public CommentsAdapter(Activity a, ArrayList<Comment> d) {
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
        Comment comment = data.get(position);

        if(convertView==null)
            vi = inflater.inflate(R.layout.activity_content_comment, null);
        
        RelativeLayout layout = (RelativeLayout) vi.findViewById(R.id.comment);
        TextView author = (TextView) vi.findViewById(R.id.author);
        TextView text = (TextView) vi.findViewById(R.id.text);
        TextView time = (TextView) vi.findViewById(R.id.time);
        Button up = (Button) vi.findViewById(R.id.upvote);
        Button down = (Button) vi.findViewById(R.id.downvote);
        ImageView thumb_image = (ImageView) vi.findViewById(R.id.list_image);

        boolean thumbnailsEnabled = PreferenceManager.
                getDefaultSharedPreferences(activity).getBoolean("show_thumbnails", true);
        
        if (!comment.getAvatar().equals("") && thumbnailsEnabled)
        {
            thumb_image.setVisibility(View.VISIBLE);
            aq.id(R.id.list_image).image(comment.getAvatar(), false, true);
        } else {
            thumb_image.setVisibility(View.GONE);
        }

        if (!comment.isReply())
            layout.setPadding(30, 5, 5, 5);
        else
            layout.setPadding(5, 5, 5, 5);

        author.setText(comment.getAuthor());
        text.setText(comment.getText());
        time.setText(comment.getTime());

        up.setText("▲ " + Integer.toString(comment.getUpvotes()));
        down.setText("▼ " + Integer.toString(comment.getDownvotes()));

        vi.setOnClickListener(this);
        vi.setTag(position);
        
        return vi;
    }

    @Override
    public void onClick(View view) {
    }
}
