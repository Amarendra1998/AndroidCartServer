package com.example.computer.androidcartserver.Service;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.computer.androidcartserver.Common.Common;
import com.example.computer.androidcartserver.Helper.NotificationHelper;
import com.example.computer.androidcartserver.MainActivity;
import com.example.computer.androidcartserver.OrderStatus;
import com.example.computer.androidcartserver.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getData()!=null ){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                sendNotificatioAPI26(remoteMessage);
            } else {
                sendNotification(remoteMessage);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void sendNotificatioAPI26(RemoteMessage remoteMessage) {
        // RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String content = data.get("message");

        PendingIntent pendingIntent;
        NotificationHelper helper;
        android.app.Notification.Builder builder;
        if (Common.currentUser != null) {
            Intent intent = new Intent(this, OrderStatus.class);
            intent.putExtra(Common.PHONE_TEXT, Common.currentUser.getPhone());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            helper = new NotificationHelper(this);
            builder = helper.getFoodCartChannelNotification(title, content, pendingIntent, defaultSoundUri);
            helper.getManager().notify(new Random().nextInt(), builder.build());

        }else
        {
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            helper = new NotificationHelper(this);
            builder = helper.getFoodCartChannelNotification(title, content,defaultSoundUri);
            helper.getManager().notify(new Random().nextInt(), builder.build());

        }

    }
    private void sendNotification(RemoteMessage remoteMessage) {
        //RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String content = data.get("message");
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager noti = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        noti.notify(0,builder.build());
    }
}
