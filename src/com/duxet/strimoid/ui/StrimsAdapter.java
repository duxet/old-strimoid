package com.duxet.strimoid.ui;

import java.util.ArrayList;

import com.duxet.strimoid.R;
import com.duxet.strimoid.models.Strim;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StrimsAdapter extends BaseExpandableListAdapter implements OnClickListener {
    private ArrayList<Strim> data;
    private static LayoutInflater inflater = null;

    public StrimsAdapter(LayoutInflater i, ArrayList<Strim> d) {
        inflater = i;
        data = d; 
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return data.get(groupPosition).getChildrens().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
        View vi = convertView;

        if(convertView == null)
            vi = inflater.inflate(R.layout.menu_strimslist_group, null);

        Strim strim = data.get(groupPosition).getChildrens().get(childPosition);

        ImageView indicator = (ImageView) vi.findViewById(R.id.indicator);
        indicator.setVisibility(View.GONE);
        
        TextView title = (TextView) vi.findViewById(R.id.title);
        title.setText(strim.getTitle());
        
        TextView count = (TextView) vi.findViewById(R.id.count);
        
        if (strim.getNewContents() > 0) {
            count.setVisibility(View.VISIBLE);
            count.setText(Integer.toString(strim.getNewContents()));
        } else {
            count.setVisibility(View.GONE);
        }

        vi.setBackgroundColor(Color.parseColor("#356691"));
        
        return vi;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (data.get(groupPosition).isGroup())
            return data.get(groupPosition).getChildrens().size();
        else
            return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return data.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return data.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        View vi = convertView;

        if(convertView == null)
            vi = inflater.inflate(R.layout.menu_strimslist_group, null);

        Strim strim = data.get(groupPosition);
        
        ImageView indicator = (ImageView) vi.findViewById(R.id.indicator);
        
        if (strim.isGroup() && isExpanded) {
            indicator.setVisibility(View.VISIBLE);
            indicator.setImageResource(R.drawable.selector_collapse);
        } else if (strim.isGroup() && !isExpanded) {
            indicator.setVisibility(View.VISIBLE);
            indicator.setImageResource(R.drawable.selector_expand);
        } else {
            indicator.setVisibility(View.INVISIBLE);
            indicator.setImageResource(R.drawable.selector_expand);
        }
            
        TextView title = (TextView) vi.findViewById(R.id.title);
        title.setText(strim.getTitle());

        return vi;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
