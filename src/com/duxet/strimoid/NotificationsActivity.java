package com.duxet.strimoid;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.duxet.strimoid.models.Message;
import com.duxet.strimoid.models.Notification;
import com.duxet.strimoid.ui.MessagesAdapter;
import com.duxet.strimoid.ui.NotificationsAdapter;
import com.duxet.strimoid.ui.TabsAdapter;
import com.duxet.strimoid.utils.HTTPClient;
import com.duxet.strimoid.utils.Parser;
import com.duxet.strimoid.utils.Session;
import com.loopj.android.http.AsyncHttpResponseHandler;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.view.ViewPager;

public class NotificationsActivity extends SherlockFragmentActivity {

 // UI elements
    ViewPager viewPager;
    TabsAdapter tabsAdapter;
    Intent intent;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        if (!Session.getUser().isLogged())
            finish();
        
        // Setup swipable tabs
        viewPager = new ViewPager(this);
        tabsAdapter = new TabsAdapter(this, viewPager);
        viewPager.setAdapter(tabsAdapter);
        viewPager.setOnPageChangeListener(tabsAdapter);
        viewPager.setId(0x7F04FFF0);
        setContentView(viewPager);
        
        // Add tabs
        tabsAdapter.addTab("Wiadomości", MessagesFragment.class, null);
        tabsAdapter.addTab("Powiadomienia", NotificationsFragment.class, null);
    }

    public static class MessagesFragment extends SherlockListFragment {

        ArrayList<Message> messages = new ArrayList<Message>();
        
        public MessagesFragment() {
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            
            final MessagesAdapter messagesAdapter = new MessagesAdapter(getActivity(), messages);
            setListAdapter(messagesAdapter);
            
            getSherlockActivity().setProgressBarVisibility(true);
            
            HTTPClient.get("u/" + Session.getUser().getUsername() + "/wiadomosci", null, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    messages.addAll(new Parser(response).getMessages());
                    messagesAdapter.notifyDataSetChanged();
                }
                
                @Override
                public void onFinish() {
                    getSherlockActivity().setProgressBarVisibility(true);
                }
            });
        }

    }
    
    public static class NotificationsFragment extends SherlockListFragment {
        
        ArrayList<Notification> notifications = new ArrayList<Notification>();
        
        public NotificationsFragment() {
        }
        
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            
            final NotificationsAdapter notificationsAdapter = new NotificationsAdapter(getActivity(), notifications);
            setListAdapter(notificationsAdapter);
            
            getSherlockActivity().setProgressBarVisibility(true);
            
            HTTPClient.get("u/" + Session.getUser().getUsername() + "/powiadomienia", null, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    notifications.addAll(new Parser(response).getNotifications());
                    notificationsAdapter.notifyDataSetChanged();
                }
                
                @Override
                public void onFinish() {
                    getSherlockActivity().setProgressBarVisibility(true);
                }
            });
        }
        
    }

}
