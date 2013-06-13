package com.duxet.strimoid;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.duxet.strimoid.fragments.ContentFragment;
import com.duxet.strimoid.models.Content;

import android.content.Intent;
import android.os.Bundle;

public class ContentActivity extends SherlockFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Content content = intent.getParcelableExtra("content");
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(content.getTitle());

        setContentView(R.layout.activity_content);
        
        ContentFragment contentFragment = (ContentFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_content);
        
        contentFragment.changeContent(content);
    }

}
