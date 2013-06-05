package com.duxet.strimoid.utils;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.widget.Button;

import com.actionbarsherlock.view.MenuItem;
import com.duxet.strimoid.R;
import com.duxet.strimoid.models.Voting;

public class UIHelper {
    private static int COLOR_GREEN = 0xFF00FF00;
    private static int COLOR_RED = 0xFFFF0000;
    
    public static void updateVoteButtons(Button up, Button down, Voting vote) {
        // Update color
        if (vote.isUpvoted()) {
            up.getBackground().setColorFilter(COLOR_GREEN, PorterDuff.Mode.MULTIPLY);
        } else if(vote.isDownvoted()) {
            down.getBackground().setColorFilter(COLOR_RED, PorterDuff.Mode.MULTIPLY);
        } else {
            up.getBackground().setColorFilter(null);
            down.getBackground().setColorFilter(null);
        }

        // Update vote count
        up.setText("▲ " + Integer.toString(vote.getUpvotes()));
        down.setText("▼ " + Integer.toString(vote.getDownvotes()));
    }
    
    public static void updateVoteActionBarButtons(MenuItem up, MenuItem down, Voting vote) {
        // Update color
        int blue = Color.parseColor("#3272aa");

        if (vote.isUpvoted()) {
            up.getIcon().setColorFilter(blue, PorterDuff.Mode.SRC_ATOP);
        } else if (vote.isDownvoted()) {
            down.getIcon().setColorFilter(blue, PorterDuff.Mode.SRC_ATOP);
        } else {
            up.setIcon(R.drawable.action_good);
            down.setIcon(R.drawable.action_bad);
        } 
    }
}
