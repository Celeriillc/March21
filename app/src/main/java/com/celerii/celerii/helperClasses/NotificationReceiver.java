package com.celerii.celerii.helperClasses;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");
        int notificationID = intent.getIntExtra("notificationID", 0);

        if (message.equals("Accept")) {
            Toast.makeText(context, "This is accept", Toast.LENGTH_SHORT).show();
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(notificationID);
        } else if (message.equals("Decline")) {
            Toast.makeText(context, "This is decline", Toast.LENGTH_SHORT).show();
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(notificationID);
        }
    }
}
