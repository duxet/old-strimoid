package com.duxet.strimoid.models;

/*
 * Do zapisywania ustawień konta strims
 * Potem można zrobić sqlita z ustawieniami a przy okazji cache strimów
 */

public class Account {

    private String username, password;
    
    public Account(){
    	setUser("","");
    }

    public Account(String username, String password) {
        setUser(username, password);
    }
    
    public void setUser(String username, String password){
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
    
    public boolean isLogged(){
		return (this.username!="")?true:false;
    }
    
}
