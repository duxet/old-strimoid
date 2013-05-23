package com.duxet.strimoid.utils;

import android.graphics.PorterDuff;
import android.widget.Button;

import com.duxet.strimoid.R;
import com.duxet.strimoid.models.Voting;

public class UIHelper {
    private static int COLOR_GREEN = 0xFF00FF00;
    private static int COLOR_RED = 0xFFFF0000;
    
    public static void updateVoteButton(Button button, Voting vote) {
        // Update color
        if (button.getId() == R.id.upvote && vote.isUpvoted())
            button.getBackground().setColorFilter(COLOR_GREEN, PorterDuff.Mode.MULTIPLY);
        else if(button.getId() == R.id.downvote && vote.isDownvoted())
            button.getBackground().setColorFilter(COLOR_RED, PorterDuff.Mode.MULTIPLY);
        else
            button.getBackground().setColorFilter(null);
        
        // Update vote count
        if (button.getId() == R.id.upvote)
            button.setText("▲ " + Integer.toString(vote.getUpvotes()));
        else
            button.setText("▼ " + Integer.toString(vote.getDownvotes()));
    }
}
