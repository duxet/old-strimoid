package com.duxet.strimoid.models;

/*
 * Do zapisywania ustawień konta strims
 * Potem można zrobić sqlita z ustawieniami a przy okazji cache strimów
 */

public class NotificationStatus {

    private int messages_count, notifications_count;
    
    public NotificationStatus() {
    	messages_count = 0;
    	notifications_count = 0;
    }
    
    public NotificationStatus(int m, int n) {
    	messages_count = m;
    	notifications_count = n;
    }
    
    public void setMessages(int i) {
        messages_count = i;
    }

    public void setNotifications(int i) {
    	notifications_count = i;
    }

    public int getMessages() {
        return this.messages_count;
    }
    
    public int getNotifications(){
		return this.notifications_count;
    }
    
}
