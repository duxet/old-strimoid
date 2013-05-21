package com.duxet.strimoid;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SpinnerAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.duxet.strimoid.models.*;
import com.duxet.strimoid.ui.ContentsAdapter;
import com.duxet.strimoid.ui.EntriesAdapter;
import com.duxet.strimoid.utils.*;
import com.loopj.android.http.*;

public class MainActivity extends SherlockActivity implements OnNavigationListener  {

    ListView list;
    ArrayList<Content> contents = new ArrayList<Content>();
    ArrayList<Entry> entries = new ArrayList<Entry>();

    ContentsAdapter contentsAdapter;
    EntriesAdapter entriesAdapter;

    ProgressBar progressBar, progressBarBottom;

    String currentContentType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.ic_modes,
                android.R.layout.simple_spinner_dropdown_item);
        getSupportActionBar().setListNavigationCallbacks(mSpinnerAdapter, this);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        list = (ListView)findViewById(R.id.contentsList);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        progressBarBottom = (ProgressBar) findViewById(R.id.progressBarBottom);

        contentsAdapter = new ContentsAdapter(this, contents);
        entriesAdapter = new EntriesAdapter(this, entries);

        list.setAdapter(contentsAdapter);

        loadContents(currentContentType, 1, true);
    }

    protected void loadContents(final String type, int page, boolean clear) {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.bringToFront();

        if (clear)
            contents.clear();

        HTTPClient.get(type + "?strona=" + page, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                if (type.equals("wpisy"))
                    new drawEntries().execute(response);
                else
                    new drawContents().execute(response);
            }
        });

        if (clear) {
            EndlessScrollListener scrollListener = new EndlessScrollListener();
            list.setOnScrollListener(scrollListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);
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
        case R.id.action_login:
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
            break;
        default:
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
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

        // We may need to change adapter
        if (itemPosition == 4)
            list.setAdapter(entriesAdapter);
        else
            list.setAdapter(contentsAdapter);

        loadContents(currentContentType, 1, true);

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
                loadContents(currentContentType, currentPage + 1, false);
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }

}
