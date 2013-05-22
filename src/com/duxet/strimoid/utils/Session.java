package com.duxet.strimoid.utils;

import com.duxet.strimoid.models.Account;

public class Session {
	private static Account user = new Account();
	private static String token = "";
	
	public static Account getUser(){
		return user;
	}
	
	public static String getToken() {
	    return token;
	}
}
