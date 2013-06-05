package com.duxet.strimoid;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.duxet.strimoid.R.id;
import com.duxet.strimoid.models.Comment;
import com.duxet.strimoid.models.Content;
import com.duxet.strimoid.models.Voting;
import com.duxet.strimoid.ui.CommentsAdapter;
import com.duxet.strimoid.utils.*;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.util.Patterns;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

@SuppressLint("SetJavaScriptEnabled")
public class ContentActivity extends SherlockActivity {
    
    // UI elements
    Menu optionsMenu;
    WebView webView;
    ListView list;
    ProgressBar progressBar;

    // Data
    Content content;
    ArrayList<Comment> comments = new ArrayList<Comment>();
    CommentsAdapter commentsAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        
        webView = (WebView) findViewById(R.id.webView);
        list = (ListView) findViewById(R.id.comments);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        
        Intent intent = getIntent();
        content = intent.getParcelableExtra("content");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(content.getTitle());

        commentsAdapter = new CommentsAdapter(this, comments);
        list.setAdapter(commentsAdapter);
        
        registerForContextMenu(list);

        showPage();
    }

    private void showPage() {
        webView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        list.setVisibility(View.GONE);
        
        String url = content.getUrl();
        
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
        list.setVisibility(View.VISIBLE);

        HTTPClient.get(content.getCommentsUrl(), null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                new drawComments().execute(response);
                
                /* Getting commentsAddNew token */
                Parser parser = new Parser(response);
                Session.setToken(parser.getToken());
            }
        });
    }
    
    private void showAddReplyDialog(int pos) {
        if (!Session.getUser().isLogged()) {
            Toast.makeText(this, "Zaloguj się aby móc głosować.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        final EditText input = new EditText(this);
        input.setHint("Treść odpowiedzi");
        input.setText("@" + comments.get(pos).getAuthor() + ": ");
        input.setSelection(input.getText().length());
        
        // Find entry parent
        Comment currentComment = comments.get(pos);
        
        while(currentComment.isReply()) {
            currentComment = comments.get(--pos);
        }
        
        final String parentId = currentComment.getId();

        new AlertDialog.Builder(this)
            .setTitle("Dodaj odpowiedź")
            .setIcon(R.drawable.ic_dialog_comment)
            .setView(input)
            .setPositiveButton("Dodaj", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    progressBar.setVisibility(View.VISIBLE);
                    addNewComment(input.getText().toString(), parentId);
                }
            }).setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.cancel();
                }
            }).show();
    }
    
    public void vote(final MenuItem m) {
        final String action;
        String url ;

        if (!Session.getUser().isLogged()) {
            Toast.makeText(this, "Zaloguj się aby móc głosować.", Toast.LENGTH_SHORT).show();
            return;
        }

        final Voting vote = content;
        final MenuItem up = optionsMenu.findItem(R.id.action_good);
        final MenuItem down = optionsMenu.findItem(R.id.action_bad);

        if (m.getItemId() == R.id.action_good) {
            url = vote.getLikeUrl();
            
            if (vote.isDownvoted()) {
                HTTPClient.get(vote.getDislikeUrl() + "&akcja=usun", null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            if (response.getString("status").equals("OK")) {
                                vote.setDownvoted(false);
                                vote.setDownvotes(response.getJSONObject("content").getInt("dislikes"));
                                UIHelper.updateVoteActionBarButtons(up, down, vote);
                                vote(m);
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
                                vote.setUpvoted(false);
                                vote.setUpvotes(response.getJSONObject("content").getInt("likes"));
                                UIHelper.updateVoteActionBarButtons(up, down, vote);
                                vote(m);
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
                            if (m.getItemId() == R.id.action_good) 
                                vote.setUpvoted(true);
                            else
                                vote.setDownvoted(true);
                        } else {
                            if (m.getItemId() == R.id.action_bad) 
                                vote.setUpvoted(false);
                            else
                                vote.setDownvoted(false);
                        }
                        
                        UIHelper.updateVoteActionBarButtons(up, down, vote);
                    }
                } catch (JSONException e) { return; }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        getSupportMenuInflater().inflate(R.menu.view_content, menu);
        optionsMenu = menu;
        
        UIHelper.updateVoteActionBarButtons(optionsMenu.findItem(R.id.action_good),
                optionsMenu.findItem(R.id.action_bad), content);
        
        return true;
    }
    
    public void addNewComment(String comment, String parent){
    	RequestParams params = new RequestParams();
		params.put("token", Session.getToken());
		params.put("_external[content]", content.getId());
		params.put("_external[parent]", parent);
		params.put("text", comment);

        HTTPClient.post("ajax/komentarze/dodaj", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
            	progressBar.setVisibility(View.GONE);
            	
            	try {
                    if (response.getString("status").equals("OK"))
                        Toast.makeText(ContentActivity.this, "Komentarz został dodany.", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(ContentActivity.this, "Nie udało się dodać komentarza.", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) { }
            	
            	showComments();
            }
            
            @Override
            public void onFailure(Throwable arg0) {
                progressBar.setVisibility(LinearLayout.GONE);
                Toast.makeText(getApplicationContext(), "Wystąpił błąd: serwer nie odpowiada.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void showRemoveCommentDialog(final Comment comment) {
        new AlertDialog.Builder(this)
        .setTitle("Usuń komentarz")
        .setMessage("Czy na pewno chcesz usunąć komentarz?")
        .setPositiveButton("Usuń", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                progressBar.setVisibility(View.VISIBLE);
                removeComment(comment.getId());
            }
        }).setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        }).show();
    }
    
    public void removeComment(String id) {
        HTTPClient.get("ajax/k/" + id + "/usun?token=" + Session.getToken(), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getString("status").equals("OK"))
                        Toast.makeText(ContentActivity.this, "Komentarz został usunięty.", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(ContentActivity.this, "Nie udało się usunąć komentarza.", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) { return; }
            }
            
            @Override
            public void onFailure(Throwable arg0) {
                Toast.makeText(ContentActivity.this, "Wystąpił błąd: serwer nie odpowiada.", Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onFinish() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, 
       ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        Comment comment = comments.get(info.position);
        
        menu.add(Menu.NONE, 1, Menu.NONE, "Odpowiedz");
        
        if (comment.getAuthor().equals(Session.getUser().getUsername()))
            menu.add(Menu.NONE, 2, Menu.NONE, "Usuń");
        
        // Find URLs in text
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(comment.getText());
        while(m.find()) {
            menu.add(100, Menu.NONE, Menu.NONE, m.group());
        }
    }
    
    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        Comment comment = comments.get(info.position);
        
        switch (item.getItemId()) {
            case 1:
                showAddReplyDialog(info.position);
                return true;
            case 2:
                showRemoveCommentDialog(comment);
                return true;
            default:
                if(item.getGroupId() == 100) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse((String) item.getTitle()));
                    startActivity(browserIntent);
                }
                return true;
        }
    }
    
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        int itemId = item.getItemId();
        switch (itemId) {
        case android.R.id.home:
            finish();
            break;
        case R.id.action_good:
        case R.id.action_bad:
            vote(item);
            break;
        case R.id.action_comments:
            showComments();
            break;
	    case R.id.action_new_comment:
	    	final EditText input = new EditText(this);
	    	new AlertDialog.Builder(ContentActivity.this)
	            .setTitle("Dodaj komentarz")
	            .setIcon(R.drawable.ic_dialog_comment)
	            .setMessage("Wpisz treść komentarza")
	            .setView(input)
	            .setPositiveButton("Dodaj", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                    Editable value = input.getText(); 
	                    progressBar.setVisibility(View.VISIBLE);
	                    addNewComment(value.toString(), "");
	                }
	            }).setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                    dialog.cancel();
	                }
	            }).show();
	    	break;
	    case R.id.action_open_in_browser:
	        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(content.getUrl()));
            startActivity(browserIntent);
            break;
        }

        return true;
    }
    
    private class drawComments extends AsyncTask<String, Void, Void>{
        ArrayList<Comment> newComments;

        protected Void doInBackground(String... params) {
            newComments = new Parser(params[0]).getComments();
            
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
