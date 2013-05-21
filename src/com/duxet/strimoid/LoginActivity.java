package com.duxet.strimoid;

import com.actionbarsherlock.app.SherlockActivity;
import com.duxet.strimoid.utils.HTTPClient;
import com.duxet.strimoid.utils.Parser;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Context;
import android.content.Intent;
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
    
    public SherlockActivity getInstance(){
    	return this;
    }
    
    public void signIn(final String username, final String password, final String token){		
		RequestParams params = new RequestParams();
		params.put("token", token);
		params.put("name", username);
		params.put("password", password);
		params.put("_external[remember]", "0");

        HTTPClient.post("zaloguj", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
            	login_status.setVisibility(LinearLayout.GONE);
            	login_form.setVisibility(ScrollView.GONE);
            	
            	// TODO: Moze jakas lepsza metoda sprawdzania wyniku logowania?
            	boolean isLoggedIn = false;
            	
            	if (response.contains("page_template.logged_in = true"))
            	        isLoggedIn = true;

            	if (isLoggedIn){
                    Intent mainIntent = new Intent(getInstance(), MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mainIntent.putExtra("isLoggedIn", true);
                    startActivity(mainIntent);
            	}else{
					Toast toast = Toast.makeText(getApplicationContext(), "Niezalogowano, błędny login lub hasło", Toast.LENGTH_SHORT);
					toast.show();
            	}
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
