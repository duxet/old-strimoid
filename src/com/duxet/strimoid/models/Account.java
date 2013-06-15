package com.duxet.strimoid.models;

/*
 * Do zapisywania ustawień konta strims
 * Potem można zrobić sqlita z ustawieniami a przy okazji cache strimów
 */

public class Account {

    private String username, password, avatar;
    
    public Account() {
    	setUser("", "");
    }

    public Account(String username, String password) {
        setUser(username, password);
    }
    
    public void setUser(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    public String getAvatar() {
        return avatar;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
    
    public boolean isLogged(){
		return !this.username.equals("") ? true : false;
    }
    
}
