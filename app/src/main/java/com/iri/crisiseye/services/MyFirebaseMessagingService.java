package com.iri.crisiseye.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.iri.crisiseye.CameraActivity;
import com.iri.crisiseye.CameraActivityBase;
import com.iri.crisiseye.MainActivity;
import com.iri.crisiseye.R;

import java.util.Map;

/**
 * Created by tsarkar on 06/10/17.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        Log.d(TAG, "Notification Message Title: " + remoteMessage.getNotification().getTitle());
        Map rm_data =  (Map)remoteMessage.getData();
        String data_framerate = (String) rm_data.get("framerate");
        String data_k_frame_interval = (String) rm_data.get("k_frame_interval");
        String data_bitrate = (String) rm_data.get("bitrate");
        System.out.println("framerate "+ data_framerate);
        System.out.println("k_frame_interval "+ data_k_frame_interval);
        System.out.println("bitrate "+ data_bitrate);


        CameraActivityBase.updated_values = true;
        CameraActivityBase.framerate_str = data_framerate;
        CameraActivityBase.bitRate_str = data_bitrate;
        CameraActivityBase.keyframe_interval_str = data_k_frame_interval;

        /*Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1410,
                intent, PendingIntent.FLAG_ONE_SHOT);*/



        /*NotificationCompat.Builder notificationBuilder = new
                NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_copy)
                .setContentTitle("Message")
                .setContentText(remoteMessage.getNotification().getBody())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);*/

        /*NotificationManager notificationManager =
                (NotificationManager)
                        getSystemService(Context.NOTIFICATION_SERVICE);*/

        /*notificationManager.notify(1410, notificationBuilder.build());*/
    }







}
