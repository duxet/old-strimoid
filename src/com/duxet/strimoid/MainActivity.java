package com.duxet.strimoid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.ProgressBar;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
                
        //SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.ic_modes,
        //        android.R.layout.simple_spinner_dropdown_item);
        //getSupportActionBar().setListNavigationCallbacks(mSpinnerAdapter, this);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        
        //TODO:
        //Zmniejszyc wysokosc tab
   
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

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

        list = (ListView)findViewById(R.id.contentsList);
        listStrims = (ListView)findViewById(R.id.strimsList);
        
        strimsAdapter = new StrimsAdapter(this, strims);
        contentsAdapter = new ContentsAdapter(this, contents);
        entriesAdapter = new EntriesAdapter(this, entries);

        list.setAdapter(contentsAdapter);
        listStrims.setAdapter(strimsAdapter);
        
        for(String t: TABS){
        	Tab tab = getSupportActionBar()
                      .newTab()
                      .setText(t)
                      .setTabListener(this);

        	getSupportActionBar().addTab(tab);
        }

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
    }

    protected void loadContents(String strim, final String type, int page, boolean clear) {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.bringToFront();

        if (clear)
            contents.clear();
        
        String url = "";
        
        if(strim.length() > 0)
            url = "s/" + strim + "/";
        
        url = url + type + "?strona=" + page;

        HTTPClient.get(url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                if (type.equals("wpisy"))
                    new drawEntries().execute(response);
                else
                    new drawContents().execute(response);
                
            }
        });


        //new drawStrims().execute(response);
        if (clear) {
            EndlessScrollListener scrollListener = new EndlessScrollListener();
            list.setOnScrollListener(scrollListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	/*
    	 * Dynamiczne menu
    	 * potrzebne na potrzeby logowania
    	 */
    	
    	if (!Session.getUser().isLogged()){
	        menu.add(1, 1, 0, "Zaloguj się")
	        	.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	        	//.setIcon(R.drawable.ic_action_accounts)
    	}
        
        SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
        searchView.setQueryHint("Szukaj…");
        searchView.setOnQueryTextListener(this);
        searchView.setOnSuggestionListener(this);
        
        menu.add("Szukaj")
        	.setIcon(R.drawable.ic_search_inverse)
        	.setActionView(searchView)
        	.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        
	    menu.add(2, R.id.action_settings, 0, "Ustawienia")
		    .setIcon(R.drawable.ic_action_settings)
		    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	
	    menu.add(3, 2, 0, "Odśwież")
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
    
    // Method used to parse contents
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
    
    // Method used to parse contents
    private class drawStrims extends AsyncTask<String, Void, Void>{

        ArrayList<Strim> newStrims;

        protected Void doInBackground(String... params) {
        	newStrims = Parser.getStrims(params[0]);
            return null;
        }

        protected void onPostExecute(Void arg) {
            strims.addAll(newStrims);
            //progressBar.setVisibility(View.GONE);
            strimsAdapter.notifyDataSetChanged();
        }

    }

    // Method used to parse entries
    public class drawEntries extends AsyncTask<String, Void, Void>{

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

    // Class used for endless loading
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
	public boolean onSuggestionSelect(int position) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onSuggestionClick(int position) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		int itemPosition = tab.getPosition();
		switch (itemPosition) {
	        case 0:
	            currentContentType = "";
	            break;
	        case 1:
	            currentContentType = "najnowsze";
	            break;
	        case 2:
	            currentContentType = "wschodzace";
	            break;
	        case 3:
	            currentContentType = "najlepsze";
	            break;
	        case 4:
	            currentContentType = "wpisy";
	            break;
	        case 5:
	            currentContentType = "komentarze";
	            break;
	     }

		 if (itemPosition == 4)
		     list.setAdapter(entriesAdapter);
		 else
		     list.setAdapter(contentsAdapter);
	
		 loadContents(currentStrim, currentContentType, 1, true);
	}


	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	
	}


	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	
	}

}
