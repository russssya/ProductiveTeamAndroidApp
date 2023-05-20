package com.example.productiveteamapp.notification;

import static com.example.productiveteamapp.notification.NotificationUtils.CHANNEL_DESCRIPTION;
import static com.example.productiveteamapp.notification.NotificationUtils.CHANNEL_ID;
import static com.example.productiveteamapp.notification.NotificationUtils.CHANNEL_NAME;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.productiveteamapp.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMessagingService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        if (message.getData().size() > 0) {
            String title = message.getData().get("title");
            String body = message.getData().get("body");

            if (title != null && body != null) {
                showNotification(title, body);
            }
        }
        if (message.getNotification() != null) {
            String title = message.getNotification().getTitle();
            String body = message.getNotification().getBody();

            if (title != null && body != null) {
                showNotification(title, body);
            }
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);
    }

    @Override
    public void onDeletedMessages() { super.onDeletedMessages(); }

    private void showNotification(String title, String body) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_main)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            // Customize additional channel settings if needed

            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(/*notificationId*/ 0, builder.build());
    }
}