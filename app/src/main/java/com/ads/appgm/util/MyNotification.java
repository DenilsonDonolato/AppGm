package com.ads.appgm.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ads.appgm.R;
import com.ads.appgm.SplashActivity;
import com.ads.appgm.service.ForegroundLocationService;

public class MyNotification {
    private static MyNotification instance;
    private final NotificationManagerCompat notificationManager;

    private MyNotification(Context context) {
        notificationManager = NotificationManagerCompat.from(context);
    }

    public static MyNotification getInstance(Context context) {
        return instance == null ? instance = new MyNotification(context) : instance;
    }

    public void show(String title, String content, int id, Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launch)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(id, builder.build());
    }

    public void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    Constants.NOTIFICATION_CHANNEL_ID,
                    Constants.NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(Constants.NOTIFICATION_CHANNEL_DESCRIPTION);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void cancelAll() {
        notificationManager.cancelAll();
    }

    public void cancel(int id){
        notificationManager.cancel(id);
    }

    public void turnOnGps(Context context) {
        CharSequence text = context.getText(R.string.app_name);
        Intent intent = new Intent(context, ForegroundLocationService.class);
        PendingIntent servicePendingIntent = PendingIntent.getService(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent turnOnGps = PendingIntent.getActivity(context, 0,
                new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
        Notification notification = new NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
                .addAction(R.drawable.ic_launch, "Ligar GPS", turnOnGps)
                .addAction(R.drawable.ic_cancel, "Cancelar", servicePendingIntent)
                .setContentText(text)
                .setContentTitle("SOS Maria precisa ler sua localização")
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setTicker(text)
                .build();
        notificationManager.notify(Constants.NOTIFICATION_ID_LIGAR_GPS,notification);
    }

    public Notification foregroundNotification(Context context) {
        Intent intent = new Intent(context, ForegroundLocationService.class);

        CharSequence text = context.getText(R.string.app_name);

        // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
        intent.putExtra(Constants.EXTRA_STARTED_FROM_NOTIFICATION, true);

        // The PendingIntent that leads to a call to onStartCommand() in this service.
        PendingIntent servicePendingIntent = PendingIntent.getService(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // The PendingIntent to launch activity.
        PendingIntent activityPendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, SplashActivity.class), 0);

        return new NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
                .addAction(R.drawable.ic_launch, "Abrir app", activityPendingIntent)
                .addAction(R.drawable.ic_cancel, "Cancelar", servicePendingIntent)
                .setContentText(text)
                .setContentTitle("Sua encomenda está a caminho")
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setTicker(text)
                .build();
    }

    public void openApp(Context context) {
        PendingIntent activityPendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, SplashActivity.class), 0);
        CharSequence text = context.getText(R.string.app_name);
        Log.d("BackJOB", "Sending Permission");
        Notification permission = new NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
                .addAction(R.drawable.ic_launch, "Abrir app", activityPendingIntent)
                .addAction(R.drawable.ic_cancel, "Cancelar", null)
                .setContentText(text)
                .setContentTitle("SOS Maria precisa de sua atenção")
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setTicker(text)
                .build();
        notificationManager.notify(Constants.GPS_PERMISSION_REQUEST,permission);
    }
}
