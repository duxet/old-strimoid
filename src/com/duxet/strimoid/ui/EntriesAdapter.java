package com.duxet.strimoid.ui;

import java.util.ArrayList;

import com.duxet.strimoid.R;
import com.duxet.strimoid.models.Entry;
import com.duxet.strimoid.utils.UIHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class EntriesAdapter extends BaseAdapter {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_LOAD_MORE = 1;
    private static final int TYPE_MAX_COUNT = TYPE_LOAD_MORE + 1;
    
    private Activity activity;
    private ArrayList<Entry> data;
    private static LayoutInflater inflater = null;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    static class ViewHolder {
        TextView author, message, time;
        Button up, down;
        ImageView image, reply;
    }
    
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
    
    public int getItemViewType(int position) {
        return data.get(position).isLoadMore() ? TYPE_LOAD_MORE : TYPE_ITEM;
    }

    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        Entry entry = data.get(position);
        int type = getItemViewType(position);

        if (vi == null) {
            switch (type) {
                case TYPE_ITEM:
                    vi = inflater.inflate(R.layout.activity_main_entry, null);
                    ViewHolder holder = new ViewHolder();
                    
                    holder.author = (TextView) vi.findViewById(R.id.author);
                    holder.message = (TextView) vi.findViewById(R.id.message);
                    holder.time = (TextView) vi.findViewById(R.id.time);
                    holder.up = (Button) vi.findViewById(R.id.upvote);
                    holder.down = (Button) vi.findViewById(R.id.downvote);
                    holder.reply = (ImageView) vi.findViewById(R.id.reply);
                    holder.image = (ImageView) vi.findViewById(R.id.list_image);
                    
                    vi.setTag(holder);
                    break;
                case TYPE_LOAD_MORE:
                    TextView loadMore = new TextView(activity);
                    loadMore.setText("Pokaż więcej odpowiedzi.");
                    loadMore.setGravity(Gravity.CENTER);
                    loadMore.setPadding(10, 10, 10, 10);
                    loadMore.setBackgroundColor(Color.parseColor("#ffffff"));
                    vi = loadMore;
                    break;
            }
        }
        
        if (type == TYPE_LOAD_MORE) {
            TextView loadMore = (TextView) vi;
            loadMore.setText("Pokaż więcej odpowiedzi.");
            return vi;
        }

        ViewHolder holder = (ViewHolder) vi.getTag();
        
        boolean avatarsEnabled = PreferenceManager.
                getDefaultSharedPreferences(activity).getBoolean("show_avatars", true);

        holder.image.setImageResource(android.R.color.transparent);
        
        if (!entry.getAvatar().equals("") && avatarsEnabled) {
            holder.image.setVisibility(View.VISIBLE);
            imageLoader.displayImage(entry.getAvatar(), holder.image);
        } else {
            holder.image.setVisibility(View.GONE);
        }

        if (!entry.isReply()) {
            vi.setBackgroundColor(Color.parseColor("#f5f5f5"));
            holder.reply.setVisibility(View.GONE);
            holder.time.setText(entry.getTime() + " w " + entry.getStrim());
        } else {
            vi.setBackgroundColor(Color.parseColor("#e9e9e9"));
            holder.reply.setVisibility(View.VISIBLE);
            holder.time.setText(entry.getTime());
        }

        holder.author.setText(entry.getAuthor());
        holder.author.setTextColor(entry.getAuthorColor());
        holder.message.setText(entry.getMessage());

        holder.up.setTag(position);
        holder.down.setTag(position);
        
        UIHelper.updateVoteButtons(holder.up, holder.down, entry);

        return vi;
    }
    
}
