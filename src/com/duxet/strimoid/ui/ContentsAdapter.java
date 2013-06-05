package com.duxet.strimoid.ui;

import java.util.ArrayList;

import com.duxet.strimoid.R;
import com.duxet.strimoid.ContentActivity;
import com.duxet.strimoid.models.Content;
import com.duxet.strimoid.utils.UIHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
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
    private ImageLoader imageLoader = ImageLoader.getInstance();

    static class ViewHolder {
        TextView title, desc;
        Button up, down;
        ImageView image;
    }
    
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

        if (vi == null) {
            vi = inflater.inflate(R.layout.activity_main_content, null);
            ViewHolder holder = new ViewHolder();
            
            holder.title = (TextView) vi.findViewById(R.id.title);
            holder.desc = (TextView) vi.findViewById(R.id.desc);
            holder.up = (Button) vi.findViewById(R.id.upvote);
            holder.down = (Button) vi.findViewById(R.id.downvote);
            holder.image = (ImageView) vi.findViewById(R.id.list_image);
            
            vi.setTag(holder);
        }

        Content content = data.get(position);
        ViewHolder holder = (ViewHolder) vi.getTag();

        boolean thumbnailsEnabled = PreferenceManager.
                getDefaultSharedPreferences(activity).getBoolean("show_thumbnails", true);
        
        holder.image.setImageResource(android.R.color.transparent);
        
        if (!content.getImageUrl().equals("") && thumbnailsEnabled) {
            holder.image.setVisibility(View.VISIBLE);
            imageLoader.displayImage(content.getImageUrl(), holder.image);
        } else {
            holder.image.setVisibility(View.GONE);
        }

        holder.title.setText(content.getTitle());

        {
        	   final SpannableStringBuilder sb = new SpannableStringBuilder("Dodane przez "+content.getAuthor()+" "+content.getTime()+" do "+content.getStrim());
        	   final ForegroundColorSpan fcs = new ForegroundColorSpan(content.getAuthorColor()); 

        	   final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD); 

        	   sb.setSpan(fcs, 13, (13+content.getAuthor().length()), Spannable.SPAN_INCLUSIVE_INCLUSIVE); 
        	   sb.setSpan(bss, 13, (13+content.getAuthor().length()), Spannable.SPAN_INCLUSIVE_INCLUSIVE); 
        	   
        	   ImageSpan is = new ImageSpan(activity, R.drawable.ic_text_comments);
        	   sb.append("   " + Integer.toString(content.getComments()) + "\u00A0 ");
               sb.setSpan(is, sb.length()-1, sb.length(), 0);
               
        	   holder.desc.setText(sb);
        }
        
        holder.up.setTag(position);
        holder.down.setTag(position);
        
        UIHelper.updateVoteButtons(holder.up, holder.down, content);
        
        vi.setOnClickListener(this);
        vi.setTag(R.id.TAG_POSITION, position);
        
        return vi;
    }

    @Override
    public void onClick(View view) {
        int position = (Integer) view.getTag(R.id.TAG_POSITION);

        Intent intent = new Intent(activity, ContentActivity.class);
        intent.putExtra("content", data.get(position));
        activity.startActivity(intent);
    }
}
