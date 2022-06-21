package com.example.footsapp_android;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.footsapp_android.activities.ChatActivity;
import com.example.footsapp_android.activities.ChatsActivity;
import com.example.footsapp_android.activities.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class FBService extends FirebaseMessagingService {
    public FBService() {
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {

            if (Objects.equals(remoteMessage.getNotification().getTitle(), "ReceiveMessage")) {
                // split to the :
                String[] split = remoteMessage.getNotification().getBody().split(":");
                String to = split[0];
                String from = split[1];
                String message = split[2];
                if (to.equals(MainActivity.CURRENT_USER)) {
                    createNotificationChannel();
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                            .setSmallIcon(R.drawable.footapp_logo)
                            .setContentTitle("New message")
                            .setContentText(remoteMessage.getNotification().getBody())
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                    notificationManager.notify(1, builder.build());
                }

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(ChatActivity.NOTIFY_ACTIVITY_ACTION);
                broadcastIntent.putExtra("content", remoteMessage.getNotification().getBody());

                sendBroadcast(broadcastIntent);
            } else {
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(ChatsActivity.NOTIFY_ACTIVITY_ACTION);

                sendBroadcast(broadcastIntent);
            }
        }
    }

    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BASE) {
////            charsequence name = getstring(r.string.channel_name);
////            string description =
////                    getstring(com.google.android.gms.base.r.string.channel_description);
////
        int importance = 0;
        if (Build.VERSION.SDK_INT >= 26) {
            importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", "Channel", importance);
            channel.setDescription("description");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }
//    }
}



