package com.example.workouttimer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class ForegroundService extends Service {

    SharedPreferences sharedPref;

    public ForegroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Create notification channel
        final String CHANNEL_ID = "N_ID";

        sharedPref = getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);
        int progress = sharedPref.getInt("progress", 0);
        int max = sharedPref.getInt("max", 60);

        NotificationManager manager = getSystemService(NotificationManager.class);
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Notification Channel", NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription("Description");
        manager.createNotificationChannel(notificationChannel);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentText("0:00")
                .setContentTitle("Timer")
                .setProgress(max, progress, false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        startForeground(1001, notification.build());

        new Thread(
                new Runnable() {

                    @Override
                    public void run() {
                        while(true) {
                            String id = sharedPref.getString("id", "Timer");
                            String timer = sharedPref.getString("timer", "0:00");
                            int progress = sharedPref.getInt("progress", 0);
                            int max = sharedPref.getInt("max", 60);
                            notification.setContentText(timer);
                            notification.setContentTitle(id);
                            notification.setProgress(max, progress, false);
                            startForeground(1001, notification.build());
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
        ).start();

        return super.onStartCommand(intent, flags, startId);
    }
}