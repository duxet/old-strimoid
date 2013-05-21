package com.duxet.strimoid;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.actionbarsherlock.app.SherlockActivity;
import com.duxet.strimoid.utils.HTTPClient;
import com.duxet.strimoid.utils.Parser;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class LoginActivity extends SherlockActivity {
	Button sign_in;
	LinearLayout login_status;
	ScrollView login_form;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        login_status = (LinearLayout)findViewById(R.id.login_status);
        login_form =  (ScrollView)findViewById(R.id.login_form);
        sign_in = (Button)findViewById(R.id.sign_in_button);
        
        sign_in.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {				
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(login_status.getWindowToken(), 0);
				
				login_status.setVisibility(LinearLayout.VISIBLE);
				login_form.setVisibility(ScrollView.GONE);
				
				EditText username = (EditText)findViewById(R.id.email);
				EditText password = (EditText)findViewById(R.id.password);
				
				signIn(username.getText().toString(),password.getText().toString());
			}
        });
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
    public boolean signIn(final String username, final String password){
        HTTPClient.get("zaloguj", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
            	signIn(username, password, Parser.getToken(response));
            }
            
            @Override
            public void onFailure(Throwable arg0) {
            	errorLogin();
            }
        });
        return false;
    }
    
    public void errorLogin(){
    	login_status.setVisibility(LinearLayout.GONE);
    	login_form.setVisibility(ScrollView.VISIBLE);
    	
		Toast toast = Toast.makeText(getApplicationContext(), "Wystąpił błąd podczas logowania. Serwer zajęty.", Toast.LENGTH_SHORT);
		toast.show();
    }
    
    public void signIn(final String username, final String password, final String token){
    	List<NameValuePair> post_data = new ArrayList<NameValuePair>(2);
    	post_data.add(new BasicNameValuePair("token", token));
    	post_data.add(new BasicNameValuePair("name", username));
    	post_data.add(new BasicNameValuePair("password", password));
    	post_data.add(new BasicNameValuePair("_external[remember]", "0"));  

        HTTPClient.post("zaloguj", (RequestParams) post_data, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
            	login_status.setVisibility(LinearLayout.GONE);
            	login_form.setVisibility(ScrollView.GONE);
            	
            	/* Redirect to main activity */
            	//TODO
            	
				Toast toast = Toast.makeText(getApplicationContext(), "Niezalogowany", Toast.LENGTH_SHORT);
				toast.show();
            }
            
            @Override
            public void onFailure(Throwable arg0) {
            	errorLogin();
            }
        });
    } 
    
    public void attemptLogin() {

    }

}
