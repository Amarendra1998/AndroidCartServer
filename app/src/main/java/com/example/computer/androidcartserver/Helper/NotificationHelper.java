package com.example.computer.androidcartserver.Helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.computer.androidcartserver.R;

public class NotificationHelper extends ContextWrapper {
    private static final String FoodCart_Chanel_Id = "com.example.computer.androidcartserver.MadeFreez";
    private static final String FoodCart_Chanel_Name = "foodcart";
    private NotificationManager manager;
    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(FoodCart_Chanel_Id,
                FoodCart_Chanel_Name,
                NotificationManager.IMPORTANCE_DEFAULT
        );
        notificationChannel.enableLights(false);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(notificationChannel);
    }

    public NotificationManager getManager() {
        if (manager == null)
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;

    }
    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    public android.app.Notification.Builder getFoodCartChannelNotification(String title, String body, PendingIntent pendingIntent,
                                                               Uri uri)
    {
        return new Notification.Builder(getApplicationContext(),FoodCart_Chanel_Id)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(uri)
                .setAutoCancel(false);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    public android.app.Notification.Builder getFoodCartChannelNotification(String title, String body,
                                                                           Uri uri)
    {
        return new Notification.Builder(getApplicationContext(),FoodCart_Chanel_Id)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(uri)
                .setAutoCancel(false);
    }
}
