package com.example.mrr.final_project_mobile_programming.FCM;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.mrr.final_project_mobile_programming.Activity.MainActivity;
import com.example.mrr.final_project_mobile_programming.R;

public class NotificationReceiver extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification_id";
    public static String NOTIFICATION_TITLE = "notification_title";
    public static String NOTIFICATION_HOURS = "notification_hours";
    public static String NOTIFICATION_TYPE = "notification_type";

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        String title = bundle.getString(NOTIFICATION_TITLE, "No title");
        String hours = bundle.getString(NOTIFICATION_HOURS, "no hour");
        int type = bundle.getInt(NOTIFICATION_TYPE, 0);
        int id = bundle.getInt(NOTIFICATION_ID, 0);

        int imageId = R.drawable.ic_people;

        if(type == 1)
            imageId = R.drawable.ic_assignment_ind;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent myIntent = new Intent(context, MainActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(context, id , myIntent, PendingIntent.FLAG_ONE_SHOT);

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notification = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(hours)
                .setContentIntent(contentIntent)
                .setSmallIcon(imageId)
                .setAutoCancel(true)
                .setSound(sound)
                .build();

        notificationManager.notify(id, notification);
        Log.w("mrr", "NOTIFICATION!");
    }
}
