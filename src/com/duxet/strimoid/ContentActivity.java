package com.duxet.strimoid;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.duxet.strimoid.models.Comment;
import com.duxet.strimoid.models.Voting;
import com.duxet.strimoid.ui.CommentsAdapter;
import com.duxet.strimoid.utils.HTTPClient;
import com.duxet.strimoid.utils.Parser;
import com.duxet.strimoid.utils.Session;
import com.duxet.strimoid.utils.UIHelper;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class ContentActivity extends SherlockActivity {
    
    WebView webView;
    ListView listView;
    ProgressBar progressBar;

    ArrayList<Comment> comments = new ArrayList<Comment>();
    String url, commentsUrl, title, addCommentsToken, externalContent;
    CommentsAdapter commentsAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        
        webView = (WebView) findViewById(R.id.webView);
        listView = (ListView) findViewById(R.id.comments);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        commentsUrl = intent.getStringExtra("commentsUrl");
        title = intent.getStringExtra("title");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);
        
        commentsAdapter = new CommentsAdapter(this, comments);
        listView.setAdapter(commentsAdapter);

        showPage();
    }
    
    private void showPage() {
        webView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
        
        if (url.startsWith("/"))
            url = "http://strims.pl" + url;

        StrimsWebViewClient webClient = new StrimsWebViewClient();

        webView.setWebViewClient(webClient);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);   
    }
    
    private void showComments() {
        webView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.VISIBLE);

        HTTPClient.get(commentsUrl, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                new drawComments().execute(response);
                
                /* Getting commentsAddNew token */
                addCommentsToken = Parser.getToken(response);
                externalContent = Parser.getFirstValue(response, "_external[content]");
            }
        });
    }
    
    public void vote(final View v) {
        final Voting vote;
        final Button button = (Button) v;
        final String action;
        String url;
        
        if (!Session.getUser().isLogged()) {
            Toast.makeText(this, "Zaloguj się aby móc głosować.", Toast.LENGTH_SHORT).show();
            return;
        }

        vote = comments.get((Integer) v.getTag());

        if (v.getId() == R.id.upvote) {
            url = vote.getLikeUrl();
            
            if (vote.isDownvoted()) {
                HTTPClient.get(vote.getDislikeUrl() + "&akcja=usun", null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            if (response.getString("status").equals("OK")) {
                                View row = listView.getChildAt((Integer) v.getTag());
                                Button downBtn = (Button) row.findViewById(R.id.downvote);
                                vote.setDownvoted(false);
                                vote.setDownvotes(response.getJSONObject("content").getInt("dislikes"));
                                UIHelper.updateVoteButton(downBtn, vote);
                                vote(v);
                            }
                        } catch (JSONException e) { return; }
                    }
                });
                return;
            } else if(vote.isUpvoted()) {
                action = "usun";
            } else {
                action = "dodaj";
            }
        } else {
            url = vote.getDislikeUrl();
            
            if (vote.isUpvoted()) {
                HTTPClient.get(vote.getLikeUrl() + "&akcja=usun", null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            if (response.getString("status").equals("OK")) {
                                View row = listView.getChildAt((Integer) v.getTag());
                                Button upBtn = (Button) row.findViewById(R.id.upvote);
                                vote.setUpvoted(false);
                                vote.setUpvotes(response.getJSONObject("content").getInt("likes"));
                                UIHelper.updateVoteButton(upBtn, vote);
                                
                                vote(v);
                            }
                        } catch (JSONException e) { return; }
                    }
                });
                return;
            } else if (vote.isDownvoted()) {
                action = "usun";
            } else {
                action = "dodaj";
            }
        }
        
        HTTPClient.get(url + "&akcja=" + action, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getString("status").equals("OK")) {
                        vote.setUpvotes(response.getJSONObject("content").getInt("likes"));
                        vote.setDownvotes(response.getJSONObject("content").getInt("dislikes"));
                        
                        if (action.equals("dodaj")) {
                            if (v.getId() == R.id.upvote) 
                                vote.setUpvoted(true);
                            else
                                vote.setDownvoted(true);
                        } else {
                            if (v.getId() == R.id.upvote) 
                                vote.setUpvoted(false);
                            else
                                vote.setDownvoted(false);
                        }
                        
                        UIHelper.updateVoteButton(button, vote);
                    }
                } catch (JSONException e) { return; }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        getSupportMenuInflater().inflate(R.menu.view_content, menu);
        return true;
    }

    public void addNewComment(String comment){
    	RequestParams params = new RequestParams();
		params.put("token", addCommentsToken);
		params.put("_external[content]", externalContent);
		params.put("_external[parent]", "");
		params.put("text", comment + " [(Strimoid)](http://strims.pl/s/strimoid)");

        HTTPClient.post("ajax/komentarze/dodaj", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
            	progressBar.setVisibility(View.GONE);
            	//TODO: Pokazanie nowego komentarza
            }
            
            @Override
            public void onFailure(Throwable arg0) {
            	errorLogin();
            }
        });
    }
    
    public void errorLogin(){
    	progressBar.setVisibility(LinearLayout.GONE);
    	
		Toast toast = Toast.makeText(getApplicationContext(), "Wystąpił błąd. Serwer zajęty.", Toast.LENGTH_SHORT);
		toast.show();
    }
    
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        int itemId = item.getItemId();
        switch (itemId) {
        case android.R.id.home:
            finish();
            break;
        case R.id.action_comments:
            showComments();
            break;
	    case R.id.action_new_comment:
	    	final EditText input = new EditText(this);
	    	new AlertDialog.Builder(ContentActivity.this)
	        .setTitle("Dodaj komentarz")
	        .setMessage("Wpisz treść komentarza")
	        .setView(input)
	        .setPositiveButton("Dodaj", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	                Editable value = input.getText(); 
	                progressBar.setVisibility(View.VISIBLE);
	                addNewComment(value.toString());
	            }
	        }).setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            	//Zamknięcie okienka
	            }
	        }).show();
	    	
	    	break;
        }

        return true;
    }
    
    private class drawComments extends AsyncTask<String, Void, Void>{
        ArrayList<Comment> newComments;

        protected Void doInBackground(String... params) {
            newComments = Parser.getComments(params[0]);
            
            return null;
        }

        protected void onPostExecute(Void arg) {
            comments.addAll(newComments);
            progressBar.setVisibility(View.GONE);
            commentsAdapter.notifyDataSetChanged();
        }
    }

    private class StrimsWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

}
