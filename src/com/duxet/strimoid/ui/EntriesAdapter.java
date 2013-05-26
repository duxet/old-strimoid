package com.duxet.strimoid.ui;

import java.util.ArrayList;

import com.androidquery.AQuery;
import com.duxet.strimoid.R;
import com.duxet.strimoid.models.Entry;
import com.duxet.strimoid.utils.UIHelper;

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
        AQuery aq = new AQuery(vi);
        Entry entry = data.get(position);
        int type = getItemViewType(position);

        if (convertView == null) {
            switch (type) {
                case TYPE_ITEM:
                    vi = inflater.inflate(R.layout.activity_main_entry, null);
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

        TextView author = (TextView) vi.findViewById(R.id.author);
        TextView message = (TextView) vi.findViewById(R.id.message);
        TextView time = (TextView) vi.findViewById(R.id.time);
        Button up = (Button) vi.findViewById(R.id.upvote);
        Button down = (Button) vi.findViewById(R.id.downvote);
        ImageView reply = (ImageView) vi.findViewById(R.id.reply);
        ImageView thumb_image = (ImageView) vi.findViewById(R.id.list_image);
        
        boolean avatarsEnabled = PreferenceManager.
                getDefaultSharedPreferences(activity).getBoolean("show_avatars", true);

        if (!entry.getAvatar().equals("") && avatarsEnabled) {
            thumb_image.setVisibility(View.VISIBLE);
            aq.id(R.id.list_image).image(entry.getAvatar(), false, true);
        } else {
            thumb_image.setVisibility(View.GONE);
        }

        if (!entry.isReply()) {
            vi.setBackgroundColor(Color.parseColor("#f5f5f5"));
            reply.setVisibility(View.GONE);
            time.setText(entry.getTime() + " w " + entry.getStrim());
        } else {
            vi.setBackgroundColor(Color.parseColor("#e9e9e9"));
            reply.setVisibility(View.VISIBLE);
            time.setText(entry.getTime());
        }

        author.setText(entry.getAuthor());
        author.setTextColor(entry.getAuthorColor());
        message.setText(entry.getMessage());

        up.setTag(position);
        down.setTag(position);
        
        UIHelper.updateVoteButton(up, entry);
        UIHelper.updateVoteButton(down, entry);
        
        vi.setTag(position);
        
        return vi;
    }
    
}
