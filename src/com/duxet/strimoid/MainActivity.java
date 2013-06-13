package com.duxet.strimoid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.widget.SearchView;
import com.duxet.strimoid.fragments.ContentFragment;
import com.duxet.strimoid.fragments.ContentsListFragment;
import com.duxet.strimoid.fragments.EntriesListFragment;
import com.duxet.strimoid.models.*;
import com.duxet.strimoid.ui.StrimsAdapter;
import com.duxet.strimoid.ui.TabsAdapter;
import com.duxet.strimoid.utils.*;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.*;

public class MainActivity extends SherlockFragmentActivity implements SearchView.OnQueryTextListener,
	SearchView.OnSuggestionListener {
    
    // UI elements
    ContentFragment contentFragment;
    ViewPager viewPager;
    TabsAdapter adapter;
    
    SlidingMenu menu;
    Menu optionsMenu;
    
    ExpandableListView listStrims;
    StrimsAdapter strimsAdapter;

    // Data
    String currentStrim = "";
    
    public static final List<String> TABS = Arrays.asList(
    		new String[] { "Ważne", "Najnowsze", "Wschodzące", "Najlepsze", "Wpisy" }
    );

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("");
        
        // Load preferences and cookies
        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.pref_general, false);
        HTTPClient.setupCookieStore(getApplicationContext());

        // Setup swipable tabs
        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new TabsAdapter(this, viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(adapter);
        viewPager.setId(0x7F04FFF0);
        
        RelativeLayout fragmentLayout = (RelativeLayout) findViewById(R.id.fragment_layout);
        
        if (fragmentLayout != null &&
                getSupportFragmentManager().findFragmentByTag("content") == null) {
            contentFragment = new ContentFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();  
            ft.add(R.id.fragment_layout, contentFragment, "content");
            ft.commit();
        }

        // Setup sliding menu
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindWidthRes(R.dimen.slidingmenu_width);
        menu.setFadeDegree(0.35f);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.menu_strimslist);
        
        // Setup strims list view
        listStrims = (ExpandableListView) findViewById(R.id.strimsList);
        strimsAdapter = new StrimsAdapter(this, Data.getStrims());
        listStrims.setAdapter(strimsAdapter);
        listStrims.setOnGroupClickListener(onStrimChoosed);
        listStrims.setOnChildClickListener(onChildStrimChoosed);
        listStrims.setGroupIndicator(null);
        
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
            Class<?> fragmentClass = tab.equals("Wpisy")
                    ? EntriesListFragment.class : ContentsListFragment.class;
            
            

            adapter.addTab(tab, fragmentClass, args);
        }
        
        // Start notification service if enabled in settings
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).
                getBoolean("enable_notifications", true))
        {
            Intent i = new Intent(this, NotificationService.class);        
            startService(i);
        }
    }
    
    public void updateContentFragment(Content content) {
        contentFragment.changeContent(content);
    }

    OnGroupClickListener onStrimChoosed = new OnGroupClickListener() {
        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            final Strim strim = Data.getStrims().get(groupPosition);
            changeStrim(strim.getName());
            
            if (strim.isGroup()) {
                if (!strim.getChildrens().isEmpty())
                    return false;

                String[] name = strim.getName().split("/");
                
                // Fix for subscribbed strims
                if (name == null || name.length < 2) {
                    name = new String[2];
                    name[0] = "s";
                    name[1] = "Subskrybowane";
                }

                Strim loading = new Strim("", "Ładowanie...", "", false);
                strim.addChildren(loading);
                
                HTTPClient.get("ajax/utility/submenu?section_type=" + name[0] + "&section_name=" + name[1],
                        null, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        strim.getChildrens().clear();
                        strim.addChildrens(new Parser(response).getSubstrims());
                        strimsAdapter.notifyDataSetChanged();
                    }
                });
                
                return false;
            }

            menu.toggle();

            return true;
        }
    };
    
    OnChildClickListener onChildStrimChoosed = new OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            Strim strim = Data.getStrims().get(groupPosition);
            changeStrim(strim.getName());
            menu.toggle();
            
            return true;
        }
    };
    
    public void changeStrim(String newStrimName) {
        if(newStrimName.equals(currentStrim))
            return;
        
        currentStrim = newStrimName;
        
        for (Fragment fragment : adapter.getFragments()) {
            if (fragment == null)
                continue;
            
            if (fragment.getClass() == EntriesListFragment.class)
                ((EntriesListFragment) fragment).loadContents(currentStrim, 1, true);
            else
                ((ContentsListFragment) fragment).loadContents(currentStrim, 1, true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        optionsMenu = menu;
        getSupportMenuInflater().inflate(R.menu.main, menu);

        updateOptionsMenu();

        SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
        searchView.setQueryHint("Szukaj…");
        searchView.setOnQueryTextListener(this);
        searchView.setOnSuggestionListener(this);
    	
        menu.findItem(R.id.action_search).setActionView(searchView);

        return true;
    }
    
    public void updateStrimsList() {
        strimsAdapter.notifyDataSetChanged();
    }
    
    public void updateOptionsMenu() {
        if (optionsMenu != null) {
            if (!Session.getUser().isLogged()) {
                optionsMenu.setGroupVisible(R.id.logged_in, false);
                optionsMenu.setGroupVisible(R.id.not_logged_in, true);
            } else {
                optionsMenu.setGroupVisible(R.id.logged_in, true);
                optionsMenu.setGroupVisible(R.id.not_logged_in, false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_login:
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
            break;
        case R.id.action_refresh:
            if (isEntriesTabSelected())
                ((EntriesListFragment) adapter.getCurrentFragment()).loadContents(currentStrim, 1, true);
            else
                ((ContentsListFragment) adapter.getCurrentFragment()).loadContents(currentStrim, 1, true);
        	break;
        case R.id.action_add:
            if (isEntriesTabSelected()) {
                showAddEntryDialog();
            } else {
                Intent addContentIntent = new Intent(this, AddContentActivity.class);
                startActivity(addContentIntent);
            }
            break;
        case R.id.action_settings:
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
        for (Strim strim : Data.getStrims().subList(3, Data.getStrims().size())) {
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
                    String strim = Data.getStrims().get(spinner.getSelectedItemPosition() + 3).getName().replace("/s/", "");
                    setSupportProgressBarIndeterminateVisibility(true);
                    
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

                ((EntriesListFragment) adapter.getCurrentFragment()).loadContents(currentStrim, 1, true);
            }
            
            @Override
            public void onFailure(Throwable arg0) {
                Toast.makeText(getApplicationContext(), "Wystąpił błąd: serwer nie odpowiada.", Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onFinish() {
                setSupportProgressBarIndeterminateVisibility(false);
            }
        });
    }
    
    public void vote(View v) {
        if (isEntriesTabSelected())
            ((EntriesListFragment) adapter.getCurrentFragment()).vote(v);
        else
            ((ContentsListFragment) adapter.getCurrentFragment()).vote(v);
    }
    
    private Boolean isEntriesTabSelected() {
        return (adapter.getCurrentFragment().getClass() == EntriesListFragment.class);
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
