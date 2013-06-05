package com.duxet.strimoid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.duxet.strimoid.fragments.ContentsFragment;
import com.duxet.strimoid.fragments.EntriesFragment;
import com.duxet.strimoid.models.*;
import com.duxet.strimoid.ui.StrimsAdapter;
import com.duxet.strimoid.ui.TabsAdapter;
import com.duxet.strimoid.utils.*;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.*;

public class MainActivity extends SherlockFragmentActivity implements SearchView.OnQueryTextListener,
	SearchView.OnSuggestionListener {
    
    // UI elements
    ViewPager viewPager;
    TabsAdapter adapter;
    
    SlidingMenu menu;
    Menu optionsMenu;
    
    ListView listStrims;
    StrimsAdapter strimsAdapter;
    
    ProgressBar progressBar;

    // Data
    ArrayList<Strim> strims = new ArrayList<Strim>();
    String currentStrim = "";
    
    public static final List<String> TABS = Arrays.asList(
    		new String[] { "Ważne", "Najnowsze", "Wschodzące", "Najlepsze", "Wpisy" }
    );

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("");
        
        // Load preferences and cookies
        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.pref_general, false);
        HTTPClient.setupCookieStore(getApplicationContext());

        // progressBar = (ProgressBar) findViewById(R.id.progressBar);
        
        // Setup swipable tabs
        viewPager = new ViewPager(this);
        adapter = new TabsAdapter(this, viewPager);
        viewPager.setAdapter( adapter );
        viewPager.setOnPageChangeListener( adapter );
        viewPager.setId( 0x7F04FFF0 );
        setContentView(viewPager);
        
        // Setup sliding menu
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.menu_strimslist);
        
        // Setup strims list view
        listStrims = (ListView) findViewById(R.id.strimsList);
        strimsAdapter = new StrimsAdapter(this, strims);
        listStrims.setAdapter(strimsAdapter);
        listStrims.setOnItemClickListener(onStrimChoosed);
        
        // Add tabs
        for (String tab : TABS) {
            // Remove polish and uppercase chars to get proper url name
            String contentType = tab.toLowerCase();
            contentType = contentType.replaceAll("ą", "a").replaceAll("ż", "z");
            
            // Create parameters bundle
            Bundle args = new Bundle();
            args.putString("strim", currentStrim);
            args.putString("contentType", contentType);
            
            // Load proper class
            Class fragmentClass = tab.equals("Wpisy") ? EntriesFragment.class : ContentsFragment.class;

            adapter.addTab(tab, fragmentClass, args);
        }
        
        // Start notification service if enabled in settings
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).
                getBoolean("enable_notifications", true))
        {
            Intent i = new Intent(this, NotificationService.class);        
            startService(i);
        }
        
        // Load list of strims
        loadStrimsList();
    }

    protected void loadStrimsList() {
        String url;
        
        if (Session.getUser().isLogged()) {
        	url = "ajax/utility/submenu?section_type=s&section_name=Subskrybowane";
        } else {
        	url = "ajax/utility/submenu?section_type=s&section_name=Glowny";
        }
        
        HTTPClient.get(url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
            	new drawStrims().execute(response);
            }
        });

        // Add static strims
        strims.add(new Strim("s/Glowny", "Główny", ""));
        strims.add(new Strim("", "Subskrybowane", ""));
        strims.add(new Strim("s/Moderowane", "Moderowane", ""));
    }

    OnItemClickListener onStrimChoosed = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
            currentStrim = strims.get(pos).getName();
            
            for (Fragment fragment : adapter.getFragments()) {
                if (fragment.getClass() == EntriesFragment.class)
                    ((EntriesFragment) fragment).loadContents(currentStrim, 1, true);
                else
                    ((ContentsFragment) fragment).loadContents(currentStrim, 1, true);
            }
            
            menu.toggle();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	/*
    	 * Dynamiczne menu
    	 * potrzebne na potrzeby logowania
    	 */
        
        optionsMenu = menu;
    	
        if (!Session.getUser().isLogged()) {
            menu.add(1, 1, 0, "Zaloguj się")
                .setIcon(R.drawable.action_accounts)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        } else {
            menu.add(Menu.NONE, 3, 0, "Dodaj wpis")
                .setIcon(R.drawable.action_add)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
        searchView.setQueryHint("Szukaj…");
        searchView.setOnQueryTextListener(this);
        searchView.setOnSuggestionListener(this);
    	
        menu.add("Szukaj")
        	.setIcon(R.drawable.search_inverse)
        	.setActionView(searchView)
        	.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

	    menu.add(3, 2, 0, "Odśwież")
	    	.setIcon(R.drawable.action_refresh)
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	    
	    menu.add(2, 4, 10, "Ustawienia")
        .setIcon(R.drawable.action_settings)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        return true;
    }
    
    public void updateOptionsMenu() {
        if (optionsMenu != null) {
            optionsMenu.clear();
            onCreateOptionsMenu(optionsMenu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
        case 1:
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
            break;
        case 2:
            if (adapter.getCurrentFragment().getClass() == EntriesFragment.class)
                ((EntriesFragment) adapter.getCurrentFragment()).loadContents(currentStrim, 1, true);
            else
                ((ContentsFragment) adapter.getCurrentFragment()).loadContents(currentStrim, 1, true);
        	break;
        case 3:
            showAddEntryDialog();
            break;
        case 4:
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(settingsIntent);
            break;
        default:
            return super.onOptionsItemSelected(item);
        }

        return true;
    }
    
    private void showAddEntryDialog() {
        if (!Session.getUser().isLogged()) {
            Toast.makeText(this, "Zaloguj się aby móc głosować.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog_add_entry,null);
        final Spinner spinner = (Spinner) layout.findViewById(R.id.strim);
        final EditText text = (EditText) layout.findViewById(R.id.text);
        final EditText strimName = (EditText) layout.findViewById(R.id.strimName);
        final ImageButton button = (ImageButton) layout.findViewById(R.id.edit);

        ArrayList<String> spinnerOptions = new ArrayList<String>();
        for (Strim strim : strims.subList(3, strims.size())) {
            spinnerOptions.add(strim.getTitle());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinnerOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setVisibility(View.GONE);
                button.setVisibility(View.GONE);
                strimName.setVisibility(View.VISIBLE);
            }
        });

        new AlertDialog.Builder(this)
            .setTitle("Dodaj wpis")
            .setIcon(R.drawable.ic_dialog_comment)
            .setView(layout)
            .setPositiveButton("Dodaj", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String strim = strims.get(spinner.getSelectedItemPosition() + 3).getName().replace("/s/", "");
                    progressBar.setVisibility(View.VISIBLE);
                    
                    if (!strimName.getText().toString().equals(""))
                        strim = strimName.getText().toString();
                    
                    addNewEntry(text.getText().toString(), "", strim);
                }
            }).setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.cancel();
                }
            }).show();
    }
    
    protected void addNewEntry(String text, String parent, final String strim) {
        RequestParams params = new RequestParams();
        params.put("token", Session.getToken());
        params.put("_external[parent]", parent);
        params.put("text", text);
        
        if(!strim.equals(""))
            params.put("_external[strim]", strim);

        HTTPClient.post("ajax/wpisy/dodaj", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getString("status").equals("OK"))
                        Toast.makeText(getApplicationContext(), "Wpis został dodany", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(), "Nie udało się dodać wpisu", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) { }
                
                /*if(!strim.equals(""))
                    loadContents("s/" + strim, "wpisy", 1, true);
                else
                    loadContents(currentStrim, "wpisy", 1, true);*/
            }
            
            @Override
            public void onFailure(Throwable arg0) {
                Toast.makeText(getApplicationContext(), "Wystąpił błąd: serwer nie odpowiada.", Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onFinish() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    
    public void vote(View v) {
        Fragment fragment = adapter.getCurrentFragment();
        
        if (fragment.getClass() == EntriesFragment.class)
            ((EntriesFragment) fragment).vote(v);
        else
            ((ContentsFragment) fragment).vote(v);
    }

    private class drawStrims extends AsyncTask<String, Void, Void>{
        ArrayList<Strim> newStrims;

        protected Void doInBackground(String... params) {
        	newStrims = new Parser(params[0]).getStrims();
            return null;
        }

        protected void onPostExecute(Void arg) {
            strims.addAll(newStrims);
            strimsAdapter.notifyDataSetChanged();
        }
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
		// InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.hideSoftInputFromWindow(list.getWindowToken(), 0);
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}

}
