package com.duxet.strimoid.fragments;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.duxet.strimoid.ContentActivity;
import com.duxet.strimoid.MainActivity;
import com.duxet.strimoid.R;
import com.duxet.strimoid.models.Content;
import com.duxet.strimoid.models.Data;
import com.duxet.strimoid.models.Voting;
import com.duxet.strimoid.ui.ContentsAdapter;
import com.duxet.strimoid.utils.HTTPClient;
import com.duxet.strimoid.utils.OnCustomClickListener;
import com.duxet.strimoid.utils.Parser;
import com.duxet.strimoid.utils.Session;
import com.duxet.strimoid.utils.UIHelper;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ContentsListFragment extends SherlockListFragment implements OnCustomClickListener {
    
    // UI elements
    ProgressBar progressBar, progressBarBottom;
    ContentsAdapter contentsAdapter;

    // Data
    String strim, contentType;
    ArrayList<Content> contents = new ArrayList<Content>();

    public ContentsListFragment() {
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        contentsAdapter = new ContentsAdapter(getActivity(), contents, this);

        setListAdapter(contentsAdapter);
        registerForContextMenu(getListView());

        if (contents.isEmpty())
            loadContents(strim, 1, true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (getArguments() != null) {
            strim = getArguments().getString("strim");
            contentType = getArguments().getString("contentType");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_main, container, false);

        progressBar = (ProgressBar) layout.findViewById(R.id.progressBar);
        progressBarBottom = (ProgressBar) layout.findViewById(R.id.progressBarBottom);

        return layout;
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCustomClick(View view, int position) {
        ContentFragment fragment = (ContentFragment) getActivity()
                .getSupportFragmentManager().findFragmentByTag("content");
        
        if (fragment == null) {
            Intent intent = new Intent(getActivity(), ContentActivity.class);
            intent.putExtra("content", contents.get(position));
            startActivity(intent); 
        } else {
            fragment.changeContent(contents.get(position));
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, 
       ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.add(Menu.NONE, 1, Menu.NONE, "Otwórz w przeglądarce");
    }
    
    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        
        Content content = contents.get(info.position);
        
        switch (item.getItemId()) {
            case 1:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(content.getUrl()));
                startActivity(browserIntent);
                break;
        }
        
        return true;
    }
    
    public void loadContents(String newStrim, int page, boolean clear) {
        strim = newStrim;
        
        if (!isAdded())
            return;
        
        progressBar.setVisibility(View.VISIBLE);
        progressBar.bringToFront();

        if (clear)
            contents.clear();

        String url = strim.length() > 0 ? strim + "/" : "";
        url = url + contentType + "?strona=" + page;

        HTTPClient.get(url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                new drawContents().execute(response);
            }
        });

        if (clear) {
            EndlessScrollListener scrollListener = new EndlessScrollListener();
            getListView().setOnScrollListener(scrollListener);
        }
    }

    public void vote(final View v) {
        ListView list = getListView();
        int firstPos = list.getFirstVisiblePosition() - list.getHeaderViewsCount();
        int pos = list.getPositionForView(v);
        View row = list.getChildAt(pos - firstPos);
        
        final Button downBtn = (Button) row.findViewById(R.id.downvote);
        final Button upBtn = (Button) row.findViewById(R.id.upvote);
        
        final String action;
        String url;

        if (!Session.getUser().isLogged()) {
            Toast.makeText(getActivity(), "Zaloguj się aby móc głosować.", Toast.LENGTH_SHORT).show();
            return;
        }

        final Voting vote = contents.get(pos);

        if (v.getId() == R.id.upvote) {
            url = vote.getLikeUrl();
            
            if (vote.isDownvoted()) {
                HTTPClient.get(vote.getDislikeUrl() + "&akcja=usun", null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            if (response.getString("status").equals("OK")) {
                                vote.setDownvoted(false);
                                vote.setDownvotes(response.getJSONObject("content").getInt("dislikes"));
                                UIHelper.updateVoteButtons(upBtn, downBtn, vote);
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
                                vote.setUpvoted(false);
                                vote.setUpvotes(response.getJSONObject("content").getInt("likes"));
                                UIHelper.updateVoteButtons(upBtn, downBtn, vote);
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
                        
                        UIHelper.updateVoteButtons(upBtn, downBtn, vote);
                    }
                } catch (JSONException e) { return; }
            }
        });
    }
    
    private class drawContents extends AsyncTask<String, Void, Void>{
        ArrayList<Content> newContents;
        boolean updateMenu = false;
        boolean updateStrimsList = false;

        protected Void doInBackground(String... params) {
            Parser parser = new Parser(params[0]);
            
            newContents = parser.getContents();
            
            // Update login state
            if (!Session.getUser().isLogged() && parser.checkIsLogged()){
                Session.setToken(parser.getToken());
                Session.getUser().setUser(parser.getUsername(), "");
                updateMenu = true;
                
                // Update strims menu
                Data.getStrims().addAll(parser.getStrims());
                updateStrimsList = true;
            }
            
            // Load strims list if empty
            if (Data.getStrims().isEmpty()) {
                Data.getStrims().addAll(parser.getStrims());
                updateStrimsList = true;
            }
            
            return null;
        }

        protected void onPostExecute(Void arg) {
            contents.addAll(newContents);
            progressBar.setVisibility(View.GONE);
            contentsAdapter.notifyDataSetChanged();
            
            if (updateMenu)
                ((MainActivity) getActivity()).updateOptionsMenu();
            
            if (updateStrimsList)
                ((MainActivity) getActivity()).updateStrimsList();
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
                loadContents(strim, currentPage + 1, false);
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }
}
