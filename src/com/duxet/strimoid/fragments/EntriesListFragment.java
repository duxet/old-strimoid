package com.duxet.strimoid.fragments;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.duxet.strimoid.R;
import com.duxet.strimoid.models.Entry;
import com.duxet.strimoid.models.Voting;
import com.duxet.strimoid.ui.EntriesAdapter;
import com.duxet.strimoid.utils.HTTPClient;
import com.duxet.strimoid.utils.Parser;
import com.duxet.strimoid.utils.Session;
import com.duxet.strimoid.utils.UIHelper;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class EntriesListFragment extends SherlockListFragment {

    // UI elements
    ProgressBar progressBar, progressBarBottom;
    EntriesAdapter entriesAdapter;
    
    ArrayList<Entry> entries = new ArrayList<Entry>();
    
    String strim;

    public EntriesListFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        entriesAdapter = new EntriesAdapter(getActivity(), entries);
        setListAdapter(entriesAdapter);

        //getListView().setOnItemClickListener(onItemClicked);
        registerForContextMenu(getListView());
        
        if (entries.isEmpty())
            loadContents(strim, 1, true);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (getArguments() != null) {
            strim = getArguments().getString("strim");
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
    public void onCreateContextMenu(ContextMenu menu, View v, 
       ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        
        Entry entry = entries.get(info.position);

        menu.add(Menu.NONE, 1, Menu.NONE, "Odpowiedz");

        if (entry.getAuthor().equals(Session.getUser().getUsername()))
            menu.add(Menu.NONE, 2, Menu.NONE, "Usuń");

        // Find URLs in text
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(entry.getMessage());
        while(m.find()) {
            menu.add(100, Menu.NONE, Menu.NONE, m.group());
        }
    }
    
    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        
        Entry entry = entries.get(info.position);

        switch (item.getItemId()) {
        case 1:
            showAddReplyDialog(info.position);
            break;
        case 2:
            showRemoveEntryDialog(entry);
            break;
        default:
            if(item.getGroupId() == 100) {
                String url = (String) item.getTitle();

                if (!url.startsWith("http://") || !url.startsWith("https://"))
                    url = "http://" + url;

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        }
        
        return true;
    }

    public void loadContents(String newStrim, int page, boolean clear) {
        strim = newStrim;
        
        if (isDetached())
            return;
        
        progressBar.setVisibility(View.VISIBLE);
        progressBar.bringToFront();

        if (clear)
            entries.clear();

        String url = strim.length() > 0 ? strim + "/" : "";
        url = url + "wpisy?strona=" + page;

        HTTPClient.get(url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                new drawEntries().execute(response);
            }
        });

        if (clear) {
            EndlessScrollListener scrollListener = new EndlessScrollListener();
            getListView().setOnScrollListener(scrollListener);
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
    
    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        if (entries.get(pos).isLoadMore()) {
            TextView text = (TextView) v;
            text.setText("Ładowanie...");
            loadMoreEntries(entries.get(pos).getMoreUrl(), pos);
        }
    }
    
    OnItemClickListener onItemClicked = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
            if (entries.get(pos).isLoadMore()) {
                TextView text = (TextView) v;
                text.setText("Ładowanie...");
                loadMoreEntries(entries.get(pos).getMoreUrl(), pos);
            }
        }
    };

    private void showAddReplyDialog(int pos) {
        if (!Session.getUser().isLogged()) {
            Toast.makeText(getActivity(), "Zaloguj się aby móc głosować.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        final EditText input = new EditText(getActivity());
        input.setHint("Treść odpowiedzi");
        input.setText("@" + entries.get(pos).getAuthor() + ": ");
        input.setSelection(input.getText().length());
        
        // Find entry parent
        Entry currentEntry = entries.get(pos);
        
        while(currentEntry.isReply()) {
            currentEntry = entries.get(--pos);
        }
        
        final String parentId = currentEntry.getId();

        new AlertDialog.Builder(getActivity())
            .setTitle("Dodaj odpowiedź")
            .setIcon(R.drawable.ic_dialog_comment)
            .setView(input)
            .setPositiveButton("Dodaj", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);
                    addNewEntry(input.getText().toString(), parentId, "");
                }
            }).setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.cancel();
                }
            }).show();
    }
    
    private void showRemoveEntryDialog(final Entry entry) {
        new AlertDialog.Builder(getActivity())
        .setTitle("Usuń odpowiedź")
        .setMessage("Czy na pewno chcesz usunąć odpowiedź?")
        .setPositiveButton("Usuń", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);
                removeEntry(entry.getId());
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
                        Toast.makeText(getActivity(), "Wpis został dodany", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getActivity(), "Nie udało się dodać wpisu", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) { }

                loadContents(strim, 1, true);
            }
            
            @Override
            public void onFailure(Throwable arg0) {
                Toast.makeText(getActivity(), "Wystąpił błąd: serwer nie odpowiada.", Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onFinish() {
                getSherlockActivity().setSupportProgressBarIndeterminateVisibility(false);
            }
        });
    }
    
    protected void removeEntry(String id) {
        HTTPClient.get("ajax/w/" + id + "/usun?token=" + Session.getToken(), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getString("status").equals("OK"))
                        Toast.makeText(getActivity(), "Wpis został usunięty.", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getActivity(), "Nie udało się usunąć wpisu.", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) { return; }
            }
            
            @Override
            public void onFailure(Throwable arg0) {
                Toast.makeText(getActivity(), "Wystąpił błąd: serwer nie odpowiada.", Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onFinish() {
                getSherlockActivity().setSupportProgressBarIndeterminateVisibility(false);
            }
        });
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

        final Voting vote = entries.get(pos);

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
    
    private class drawEntries extends AsyncTask<String, Void, Void>{
        ArrayList<Entry> newEntries;

        protected Void doInBackground(String... params) {
            newEntries = new Parser(params[0]).getEntries();
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
            newEntries = new Parser(params[0]).getMoreEntries();
            position = Integer.parseInt(params[1]);
            return null;
        }

        protected void onPostExecute(Void arg) {  
            ArrayList<Entry> oldEntries = new ArrayList<Entry>(entries);
            entries.clear();
            entries.addAll(oldEntries.subList(0, position));
            entries.addAll(newEntries);
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
                loadContents(strim, currentPage + 1, false);
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }

}
