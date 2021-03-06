package com.duxet.strimoid;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.duxet.strimoid.fragments.StrimChooserFragment;
import com.duxet.strimoid.fragments.StrimChooserFragment.onStrimSelectedListener;
import com.duxet.strimoid.models.Data;
import com.duxet.strimoid.models.Strim;
import com.duxet.strimoid.ui.TabsAdapter;
import com.duxet.strimoid.utils.HTTPClient;
import com.duxet.strimoid.utils.Parser;
import com.duxet.strimoid.utils.Session;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class AddContentActivity extends SherlockFragmentActivity {

    // UI elements
    ViewPager viewPager;
    TabsAdapter tabsAdapter;
    Intent intent;
    
    // Data
    ArrayList<String> strimsList;
    ArrayAdapter<String> spinnerAdapter;
    static String defaultStrim = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();

        // Setup swipable tabs
        viewPager = new ViewPager(this);
        tabsAdapter = new TabsAdapter(this, viewPager);
        viewPager.setAdapter(tabsAdapter);
        viewPager.setOnPageChangeListener(tabsAdapter);
        viewPager.setId(0x7F04FFF0);
        setContentView(viewPager);
        
        // Add tabs
        Bundle args = new Bundle();
        
        // Set default strim name if provided
        if (intent.getStringExtra("strim") != null)
            defaultStrim = intent.getStringExtra("strim");
        
        // Pass URL to tab fragments
        if (intent.getStringExtra(Intent.EXTRA_TEXT) != null)
            args.putString("text", intent.getStringExtra(Intent.EXTRA_TEXT));
 
        tabsAdapter.addTab("Link", AddLinkFragment.class, args);
        tabsAdapter.addTab("Treść własna", AddTextFragment.class, args);
        
        // Switch to add text tab if received text is not link
        if(intent.getStringExtra(Intent.EXTRA_TEXT) != null 
                && !intent.getStringExtra(Intent.EXTRA_TEXT).startsWith("http"))
            viewPager.setCurrentItem(1);

        // Load strims list if empty
        if (Data.getStrims().isEmpty())
            loadStrimsList();
        
        // User may need to log in or we may need token
        if (Session.getToken().equals("") || !Session.getUser().isLogged())
            getToken();
    }
    
    private void getToken() {
        // We will get cookies page, as it's only 4.5kb
        HTTPClient.get("pomoc/cookies", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                Parser parser = new Parser(response);
                
                if (parser.checkIsLogged()) {
                    Session.setToken(parser.getToken());
                    Session.getUser().setUser(parser.getUsername(), "");
                } else {
                    // Show login form
                    Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(loginIntent);
                }
            }
        });
    }
    
    private void loadStrimsList() {
        HTTPClient.get("", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                Data.getStrims().addAll(new Parser(response).getStrims());
            }
        });
    }
    
    public void addNewContent(String title, String kind, String value, String strim,
            Boolean mature, Boolean foreign, Boolean thumbnail) {
        RequestParams params = new RequestParams();
        params.put("token", Session.getToken());
        params.put("kind", kind);
        params.put("title", title);
        
        if (kind.equals("text")) {
            params.put("text", value);
            params.put("url", "");
        } else {
            params.put("text", "");
            params.put("url", value);
        }
        
        params.put("_external[strim]", strim);
        
        if (mature)
            params.put("nfsw", "1");
        
        if (foreign)
            params.put("eng", "1");
        
        if (thumbnail)
            params.put("media", "1");

        setSupportProgressBarIndeterminateVisibility(true);

        HTTPClient.post("dodaj", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                finish();
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
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            onBackPressed();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    public static class AddLinkFragment extends SherlockFragment {

        public AddLinkFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_add_content_link, container, false);
            
            if (getArguments().getString("text") != null)
                ((TextView) rootView.findViewById(R.id.url)).setText(getArguments().getString("text"));
            
            final EditText strimName = (EditText) rootView.findViewById(R.id.strim_name);
            final EditText title = (EditText) rootView.findViewById(R.id.title);
            final EditText url = (EditText) rootView.findViewById(R.id.url);
            final CheckBox mature = (CheckBox) rootView.findViewById(R.id.mature);
            final CheckBox foreign = (CheckBox) rootView.findViewById(R.id.foreign);
            final CheckBox thumbnail = (CheckBox) rootView.findViewById(R.id.thumbnail);
            final ImageButton editButton = (ImageButton) rootView.findViewById(R.id.edit);
            final Button addButton = (Button) rootView.findViewById(R.id.add);
            
            strimName.setText(defaultStrim);
            
            editButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                    StrimChooserFragment fragment = new StrimChooserFragment();
                    fragment.setListener(new onStrimSelectedListener() {
                        @Override
                        public void onStrimSelected(Strim strim) {
                            EditText strimName = (EditText) getView().findViewById(R.id.strim_name);
                            strimName.setText(strim.getName().replace("s/", ""));
                        }
                    });
                    fragment.show(ft, "dialog");
                }
            });

            addButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    String strim = strimName.getText().toString();
                    
                    ((AddContentActivity) getActivity()).addNewContent(title.getText().toString(), "link",
                            url.getText().toString(), strim, mature.isChecked(), foreign.isChecked(),
                            thumbnail.isChecked());
                }
            });
            
            return rootView;
        }

    }
    
    public static class AddTextFragment extends SherlockFragment {
        
        public AddTextFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_add_content_text, container, false);
            
            if (getArguments().getString("text") != null)
                ((TextView) rootView.findViewById(R.id.text)).setText(getArguments().getString("text"));

            final EditText strimName = (EditText) rootView.findViewById(R.id.strim_name);
            final EditText title = (EditText) rootView.findViewById(R.id.title);
            final EditText text = (EditText) rootView.findViewById(R.id.text);
            final CheckBox mature = (CheckBox) rootView.findViewById(R.id.mature);
            final CheckBox foreign = (CheckBox) rootView.findViewById(R.id.foreign);
            final CheckBox thumbnail = (CheckBox) rootView.findViewById(R.id.thumbnail);
            final ImageButton editButton = (ImageButton) rootView.findViewById(R.id.edit);
            final Button addButton = (Button) rootView.findViewById(R.id.add);

            strimName.setText(defaultStrim);
            
            editButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                    StrimChooserFragment fragment = new StrimChooserFragment();
                    fragment.setListener(new onStrimSelectedListener() {
                        @Override
                        public void onStrimSelected(Strim strim) {
                            EditText strimName = (EditText) getView().findViewById(R.id.strim_name);
                            strimName.setText(strim.getName().replace("s/", ""));
                        }
                    });
                    fragment.show(ft, "dialog");
                }
            });

            addButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    String strim = strimName.getText().toString();
                    
                    ((AddContentActivity) getActivity()).addNewContent(title.getText().toString(), "text",
                            text.getText().toString(), strim, mature.isChecked(), foreign.isChecked(),
                            thumbnail.isChecked());
                }
            });
            
            return rootView;
        }

    }

}
