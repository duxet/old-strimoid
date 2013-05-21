package com.duxet.strimoid;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("SetJavaScriptEnabled")
public class ViewContentActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_content);

		Intent intent = getIntent();
		String url = intent.getStringExtra("url");
		String title = intent.getStringExtra("title");

		if (url.startsWith("/"))
			url = "http://strims.pl" + url;
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(title);
		
		WebView webView = (WebView) findViewById(R.id.webView);
		StrimsWebViewClient webClient = new StrimsWebViewClient();
		
		webView.setWebViewClient(webClient);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(url);	
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		getSupportMenuInflater().inflate(R.menu.view_content, menu);
		return true;
	}
	
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

	    int itemId = item.getItemId();
	    switch (itemId) {
	    	case android.R.id.home:
	    		finish();
	    		break;

	    }

	    return true;
	}
	
	private class StrimsWebViewClient extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        view.loadUrl(url);
	        return true;
	    }
	}

}
