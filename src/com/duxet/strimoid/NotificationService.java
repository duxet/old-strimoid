package com.duxet.strimoid;

import java.util.Timer;
import java.util.TimerTask;

import com.duxet.strimoid.models.NotificationStatus;
import com.duxet.strimoid.utils.HTTPClient;
import com.duxet.strimoid.utils.Parser;
import com.duxet.strimoid.utils.Session;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class NotificationService extends Service {  
    Timer timer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer = new Timer();
        /* Co 5 minut */
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkNotifications();
            }
        }, 01, 1000*60*5);
        
        return(START_STICKY);
    }

    @Override
    public void onDestroy() {
        timer.cancel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return(null);
    }

    private void checkNotifications(){
        if (!Session.getUser().isLogged())
            return;

        HTTPClient.get("ajax/u/" + Session.getUser().getUsername() + "/powiadomienia", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
              NotificationStatus n = new Parser(response).getNotifications();
              
              if (n.getMessages() != 0)
                  Notification(10001, "Nowe wiadomości", "Na Twoim koncie pojawiły się nowe wiadomości.");
              
              if (n.getNotifications() != 0)
                  Notification(10002, "Nowe powiadomienia", "Na Twoim koncie pojawiły się nowe powiadomienia.");
            }
        });
    }

    private void Notification(int nID, String notificationTitle, String notificationMessage) {
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logo_small);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
            .setLargeIcon(largeIcon)
            .setSmallIcon(R.drawable.strims_logo)
            .setContentTitle(notificationTitle)
            .setContentText(notificationMessage)
            .setSound(soundUri)
            .setVibrate(new long[]{100, 200, 100, 500});

        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(nID, mBuilder.build());
    }
}