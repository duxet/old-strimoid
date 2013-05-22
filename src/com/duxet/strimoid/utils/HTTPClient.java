package com.duxet.strimoid.utils;

import android.content.Context;

import com.loopj.android.http.*;

public class HTTPClient {
    private static final String BASE_URL = "http://strims.pl/";

    private static AsyncHttpClient client = new AsyncHttpClient();
    
    public static void setupCookieStore(Context context) {
        PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
        client.setCookieStore(myCookieStore);
    }
    
    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
    
    public static AsyncHttpClient getInstance(){
    	return client;
    }
}
