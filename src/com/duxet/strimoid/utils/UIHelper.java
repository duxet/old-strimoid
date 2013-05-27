package com.duxet.strimoid.utils;

import android.graphics.PorterDuff;
import android.widget.Button;

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
}
