package com.example.androidapp.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.androidapp.R;
import com.example.androidapp.ui.main.MainActivity;

import java.util.Random;

public class NotificationHelper {

    private static final String CHANNEL_ID = "general_notifications";
    private static final String CHANNEL_NAME = "General Notifications";
    private static final String CHANNEL_DESCRIPTION = "Notifications for general app activities";

    private Context context;
    private NotificationManager notificationManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void sendNotification(String title, String message, Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification) // Make sure you have an ic_notification drawable
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        // Generate a unique notification ID
        int notificationId = new Random().nextInt(100000);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    public void sendSimpleNotification(String title, String message) {
        Intent intent = new Intent(context, MainActivity.class);
        sendNotification(title, message, intent);
    }

    public void sendNotificationWithAction(String title, String message, String action, String actionText, Intent actionIntent) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, actionIntent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .addAction(0, actionText, pendingIntent); // Add an action button

        int notificationId = new Random().nextInt(100000);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    // You might need to create an ic_notification.xml in drawable folder
    // For example: res/drawable/ic_notification.xml
    // <vector xmlns:android="http://schemas.android.com/apk/res/android"
    //    android:width="24dp"
    //    android:height="24dp"
    //    android:viewportWidth="24.0"
    //    android:viewportHeight="24.0">
    //    <path
    //        android:fillColor="#FF000000"
    //        android:pathData="M12,22c1.1,0 2,-0.9 2,-2h-4c0,1.1 0.9,2 2,2zM18,16v-5c0,-3.07 -1.63,-5.64 -4.5,-6.32L13.5,4c0,-0.83 -0.67,-1.5 -1.5,-1.5S10.5,3.17 10.5,4l0,0.68C7.63,5.36 6,7.93 6,11v5l-2,2v1h16v-1l-2,-2z"/>
    // </vector>
}

