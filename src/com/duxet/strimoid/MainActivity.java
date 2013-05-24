package com.duxet.strimoid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.duxet.strimoid.models.*;
import com.duxet.strimoid.ui.ContentsAdapter;
import com.duxet.strimoid.ui.EntriesAdapter;
import com.duxet.strimoid.ui.StrimsAdapter;
import com.duxet.strimoid.utils.*;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.*;

public class MainActivity extends SherlockActivity implements SearchView.OnQueryTextListener,
	SearchView.OnSuggestionListener, TabListener  {

    ListView list, listStrims;
    ArrayList<Content> contents = new ArrayList<Content>();
    ArrayList<Entry> entries = new ArrayList<Entry>();
    ArrayList<Strim> strims = new ArrayList<Strim>();

    ContentsAdapter contentsAdapter;
    EntriesAdapter entriesAdapter;
    StrimsAdapter strimsAdapter;

    ProgressBar progressBar, progressBarBottom;
    
    String currentContentType = "";
    String currentStrim = "";
    
    public static final List<String> TABS = Arrays.asList(
    		new String[] {"Ważne", "Najnowsze", "Wschodzące", "Najlepsze", "Wpisy"}
    );
    
    public static final List<String> TABS_VALUES = Arrays.asList(
            new String[] {"", "najnowsze", "wschodzące", "najlepsze", "wpisy"}
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
                
        getSupportActionBar().setTitle("");
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
   
        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.pref_general, false);
        HTTPClient.setupCookieStore(getApplicationContext());

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        progressBarBottom = (ProgressBar) findViewById(R.id.progressBarBottom);
        
        SlidingMenu menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.menu_strimslist);

        list = (ListView) findViewById(R.id.contentsList);
        listStrims = (ListView) findViewById(R.id.strimsList);
        
        strimsAdapter = new StrimsAdapter(this, strims);
        contentsAdapter = new ContentsAdapter(this, contents);
        entriesAdapter = new EntriesAdapter(this, entries);

        list.setAdapter(contentsAdapter);
        list.setOnItemClickListener(onItemClicked);
        listStrims.setAdapter(strimsAdapter);
        listStrims.setOnItemClickListener(onStrimChoosed);
        
        for(String t: TABS){
        	Tab tab = getSupportActionBar()
                      .newTab()
                      .setText(t)
                      .setTabListener(this);

        	getSupportActionBar().addTab(tab);
        }
        
        Intent i=new Intent(this, NotificationService.class);        
        startService(i);
        
       
        loadContents(currentStrim, currentContentType, 1, true);
        
        loadStrimsList();
    }
    

    protected void loadStrimsList() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.bringToFront();
        
        String url;
        if (Session.getUser().isLogged()){
        	url = "ajax/utility/submenu?section_type=s&section_name=Subskrybowane";
        }else{
        	url = "ajax/utility/submenu?section_type=s&section_name=Glowny";
        }
        
        HTTPClient.get(url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
            	new drawStrims().execute(response);
            }
        });

        strims.add(new Strim("s/Glowny", "Główny", ""));
        strims.add(new Strim("", "Subskrybowane", ""));
        strims.add(new Strim("s/Moderowane", "Moderowane", ""));
    }

    public void loadContents(String strim, final String type, int page, boolean clear) {
        currentStrim = strim;
        currentContentType = type;
        
        progressBar.setVisibility(View.VISIBLE);
        progressBar.bringToFront();

        if (clear) {
            contents.clear();
            entries.clear();
        }

        String url = "";
        
        if(strim.length() > 0)
            url = strim + "/";
        
        url = url + type + "?strona=" + page;

        HTTPClient.get(url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                if (type.equals("wpisy"))
                    new drawEntries().execute(response);
                else
                    new drawContents().execute(response);
                
                if (Parser.checkIsLogged(response)){
                	//TODO: Wczytywanie z sharedPref, ukrycie buttona zaloguj sie
                	Session.getUser().setUser("$", "$");
                }
            }
        });

        if (clear) {
            EndlessScrollListener scrollListener = new EndlessScrollListener();
            list.setOnScrollListener(scrollListener);
        }
    }
    
    public void loadMoreEntries(String url, final int pos) {
        HTTPClient.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    new drawMoreEntries().execute(response.getString("content"), Integer.toString(pos));
                } catch (JSONException e) { }
            }
        });
    }

    public void vote(final View v) {
        final int firstPos = list.getFirstVisiblePosition() - list.getHeaderViewsCount();
        final int pos = list.getPositionForView(v);
        final Voting vote;
        final Button button = (Button) v;
        final String action;
        String url;

        if (!Session.getUser().isLogged()) {
            Toast.makeText(this, "Zaloguj się aby móc głosować.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentContentType.equals("wpisy"))
            vote = entries.get(pos);
        else
            vote = contents.get(pos);

        if (v.getId() == R.id.upvote) {
            url = vote.getLikeUrl();
            
            if (vote.isDownvoted()) {
                HTTPClient.get(vote.getDislikeUrl() + "&akcja=usun", null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            if (response.getString("status").equals("OK")) {
                                View row = list.getChildAt(pos - firstPos);
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
                                View row = list.getChildAt(pos - firstPos);
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
    
    OnItemClickListener onItemClicked = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
            if (currentContentType.equals("wpisy") && entries.get(pos).isLoadMore()) {
                TextView text = (TextView) v;
                text.setText("Ładowanie...");
                loadMoreEntries(entries.get(pos).getMoreUrl(), pos);
            }
        }
    };
    
    OnItemClickListener onStrimChoosed = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
            loadContents(strims.get(pos).getName(), currentContentType, 1, true);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	/*
    	 * Dynamiczne menu
    	 * potrzebne na potrzeby logowania
    	 * 
    	 */
    	
    	if (!Session.getUser().isLogged()){
	        menu.add(1, 1, 0, "Zaloguj się")
	            .setIcon(R.drawable.action_accounts)
	        	.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    	}else{
        
    	EditText textView = new EditText(getSupportActionBar().getThemedContext());
        textView.setHint("Dodaj wpis...");
        textView.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&(keyCode == KeyEvent.KEYCODE_ENTER)) {
                   //TODO: Dodawanie wpisów
                   return true;
                }
                return false;
            }
        });
        
	    menu.add("Dodaj wpis")
		    .setIcon(R.drawable.action_add)
		    .setActionView(textView)
		    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
	    
    	}
        
        SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
        searchView.setQueryHint("Szukaj…");
        searchView.setOnQueryTextListener(this);
        searchView.setOnSuggestionListener(this);
    	
        menu.add("Szukaj")
        	.setIcon(R.drawable.search_inverse)
        	.setActionView(searchView)
        	.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
             
	    menu.add(2, R.id.action_settings, 0, "Ustawienia")
		    .setIcon(R.drawable.action_settings)
		    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	
	    menu.add(3, 2, 0, "Odśwież")
	    	.setIcon(R.drawable.action_refresh)
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_settings:
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(settingsIntent);
            break;
        case 1:
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
            break;
        case 2:
        	loadContents(currentStrim, currentContentType, 1, true);
        	break;
        default:
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private class drawContents extends AsyncTask<String, Void, Void>{
        ArrayList<Content> newContents;

        protected Void doInBackground(String... params) {
            newContents = Parser.getContents(params[0]);
            return null;
        }

        protected void onPostExecute(Void arg) {
            contents.addAll(newContents);
            progressBar.setVisibility(View.GONE);
            contentsAdapter.notifyDataSetChanged();
        }
    }

    private class drawStrims extends AsyncTask<String, Void, Void>{
        ArrayList<Strim> newStrims;

        protected Void doInBackground(String... params) {
        	newStrims = Parser.getStrims(params[0]);
            return null;
        }

        protected void onPostExecute(Void arg) {
            strims.addAll(newStrims);
            strimsAdapter.notifyDataSetChanged();
        }
    }

    private class drawEntries extends AsyncTask<String, Void, Void>{
        ArrayList<Entry> newEntries;

        protected Void doInBackground(String... params) {
            newEntries = Parser.getEntries(params[0]);
            return null;
        }

        protected void onPostExecute(Void arg) {
            entries.addAll(newEntries);
            progressBar.setVisibility(View.GONE);
            entriesAdapter.notifyDataSetChanged();
        }
    }
    
    private class drawMoreEntries extends AsyncTask<String, Void, Void>{
        ArrayList<Entry> newEntries;  
        int position;

        protected Void doInBackground(String... params) {
            newEntries = Parser.getMoreEntries(params[0]);
            position = Integer.parseInt(params[1]);
            return null;
        }

        protected void onPostExecute(Void arg) {  
            ArrayList<Entry> oldEntries = new ArrayList<Entry>(entries);
            entries.clear();
            entries.addAll(oldEntries.subList(0, position));
            entries.addAll(newEntries.subList(3, newEntries.size()));
            entries.addAll(oldEntries.subList(position + 1, oldEntries.size()));
            entriesAdapter.notifyDataSetChanged();
        }
    }

    private class EndlessScrollListener implements OnScrollListener {

        private int visibleThreshold = 5;
        private int currentPage = 1;
        private int previousTotal = 0;
        private boolean loading = true;

        public EndlessScrollListener() {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                loadContents(currentStrim, currentContentType, currentPage + 1, false);
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        int itemPosition = tab.getPosition();
        String newContentType = TABS_VALUES.get(itemPosition);

         if (itemPosition == 4)
             list.setAdapter(entriesAdapter);
         else
             list.setAdapter(contentsAdapter);
    
         loadContents(currentStrim, newContentType, 1, true);
    }


    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }


    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

	@Override
	public boolean onSuggestionSelect(int position) {
		return false;
	}

	@Override
	public boolean onSuggestionClick(int position) {
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(list.getWindowToken(), 0);
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}

}
