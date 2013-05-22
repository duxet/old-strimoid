package com.duxet.strimoid.utils;

import android.graphics.PorterDuff;
import android.widget.Button;

import com.duxet.strimoid.R;

public class UIHelper {
    private static int COLOR_GREEN = 0xFF00FF00;
    private static int COLOR_RED = 0xFFFF0000;
    
    public static void colorVoteButton(Button button, boolean voted) {
        if (button.getId() == R.id.upvote && voted)
            button.getBackground().setColorFilter(COLOR_GREEN, PorterDuff.Mode.MULTIPLY);
        else if(button.getId() == R.id.downvote && voted)
            button.getBackground().setColorFilter(COLOR_RED, PorterDuff.Mode.MULTIPLY);
        else
            button.getBackground().setColorFilter(null);
    }
}
