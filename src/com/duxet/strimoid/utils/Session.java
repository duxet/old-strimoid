package com.duxet.strimoid.utils;

import com.duxet.strimoid.models.Account;

public class Session {
	private static Account user = new Account();
	
	public static Account getUser(){
		return user;
	}
}
