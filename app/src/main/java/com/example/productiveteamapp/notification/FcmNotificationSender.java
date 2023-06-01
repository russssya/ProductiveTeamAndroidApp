package com.example.productiveteamapp.notification;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class FcmNotificationSender {
    static DatabaseReference mDataBase;
    public static void sendNotification(String topic, String title, String body) {
        Thread thread = new Thread(() -> {
            try {
                mDataBase= FirebaseDatabase.getInstance().getReference();
                String FCM_API_URL = "https://fcm.googleapis.com/fcm/send";
                String SERVER_KEY="AAAAOImkGIg:APA91bGP0wiQb-Rgt4r5tHd2_-dhjLKTIkOTSeQJWpTrw4rxRlGgTwUfUKWjI1jyS9UE2naahD8NdbdPTs1FYDfAdqabJfEod-08yJy5oxCAzrZ6aqCSxxwh_5kf6z217MScPdlevcFD";
                URL url = new URL(FCM_API_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "key=" + SERVER_KEY);
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject notification = new JSONObject();
                notification.put("title", title);
                notification.put("body", body);

                JSONObject data = new JSONObject();
                data.put("key1", "value1");
                data.put("key2", "value2");

                JSONObject payload = new JSONObject();
                payload.put("notification", notification);
                payload.put("data", data);
                payload.put("to", "/topics/" + topic);

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(payload.toString());
                writer.flush();

                int responseCode = conn.getResponseCode();
                Log.d("PushNotificationSender", "Response Code: " + responseCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}
