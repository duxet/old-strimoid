package com.duxet.strimoid.ui;

import java.util.ArrayList;

import com.duxet.strimoid.R;
import com.duxet.strimoid.models.Comment;
import com.duxet.strimoid.utils.UIHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentsAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<Comment> data;
    private static LayoutInflater inflater = null;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    static class ViewHolder {
        TextView author, text, time;
        Button up, down;
        ImageView image, reply;
    }
    
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

        if (vi == null) {
            vi = inflater.inflate(R.layout.activity_content_comment, null);
            ViewHolder holder = new ViewHolder();
            
            holder.author = (TextView) vi.findViewById(R.id.author);
            holder.text = (TextView) vi.findViewById(R.id.text);
            holder.time = (TextView) vi.findViewById(R.id.time);
            holder.up = (Button) vi.findViewById(R.id.upvote);
            holder.down = (Button) vi.findViewById(R.id.downvote);
            holder.reply = (ImageView) vi.findViewById(R.id.reply);
            holder.image = (ImageView) vi.findViewById(R.id.list_image);
            
            vi.setTag(holder);
        }
            
        Comment comment = data.get(position);
        ViewHolder holder = (ViewHolder) vi.getTag();

        boolean avatarsEnabled = PreferenceManager.
                getDefaultSharedPreferences(activity).getBoolean("show_avatar", true);
        
        holder.image.setImageResource(android.R.color.transparent);
        
        if (!comment.getAvatar().equals("") && avatarsEnabled)
        {
            holder.image.setVisibility(View.VISIBLE);
            imageLoader.displayImage(comment.getAvatar(), holder.image);
        } else {
            holder.image.setVisibility(View.GONE);
        }

        if (!comment.isReply())
            holder.reply.setVisibility(View.GONE);
        else
            holder.reply.setVisibility(View.VISIBLE);

        holder.author.setText(comment.getAuthor());
        holder.author.setTextColor(comment.getAuthorColor());
        holder.text.setText(comment.getText());
        holder.time.setText(comment.getTime());

        holder.up.setTag(position);
        holder.down.setTag(position);

        UIHelper.updateVoteButtons(holder.up, holder.down, comment);
        
        return vi;
    }
}
