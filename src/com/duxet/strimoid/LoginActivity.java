package com.duxet.strimoid;

import com.actionbarsherlock.app.SherlockActivity;
import android.os.Bundle;

public class LoginActivity extends SherlockActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void attemptLogin() {

    }

}
