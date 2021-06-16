package com.celerii.celerii.helperClasses;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
//import android.support.v7.app.NotificationManagericationCompat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.amulyakhare.textdrawable.TextDrawable;
import com.celerii.celerii.Activities.Comment.CommentStoryActivity;
import com.celerii.celerii.Activities.Events.EventDetailActivity;
import com.celerii.celerii.Activities.Home.NotificationDetailActivity;
import com.celerii.celerii.Activities.Home.Parent.ParentHomeNotification;
import com.celerii.celerii.Activities.Home.Parent.ParentMainActivityTwo;
import com.celerii.celerii.Activities.Home.Parent.ParentsRequestActivity;
import com.celerii.celerii.Activities.Home.Teacher.TeacherMainActivityTwo;
import com.celerii.celerii.Activities.Home.Teacher.TeacherRequestActivity;
import com.celerii.celerii.Activities.Inbox.ChatActivity;
import com.celerii.celerii.Activities.Newsletters.NewsletterDetailActivity;
import com.celerii.celerii.Activities.Profiles.ParentProfileActivity;
import com.celerii.celerii.Activities.Profiles.SchoolProfile.SchoolProfileActivity;
import com.celerii.celerii.Activities.StudentAttendance.AttendanceDetailActivity;
import com.celerii.celerii.Activities.StudentAttendance.ParentAttendanceActivity;
import com.celerii.celerii.Activities.StudentBehaviouralPerformance.BehaviouralResultActivity;
import com.celerii.celerii.Activities.StudentPerformance.StudentPerformanceForParentsActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.models.NotificationBadgeModel;
import com.celerii.celerii.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.net.URL;
import java.util.HashMap;

/**
 * Created by DELL on 6/1/2019.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    private static final String TAG = "MyFirebaseMsgService";
    private static final int BROADCAST_NOTIFICATION_ID = 1;
    String dataType, accountType, title, fromAccountType, fromID, from, objectID, object, notificationImageURL, message, activityID;

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        dataType = remoteMessage.getData().get("data_type");
        if (dataType == null) {
            return;
        }

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        if(!dataType.equals("")){
            Log.d(TAG, "onMessageReceived: new incoming message.");
            accountType = remoteMessage.getData().get("account_type");
//            title = remoteMessage.getData().get("title");
            fromAccountType = remoteMessage.getData().get("fromAccountType");
            fromID = remoteMessage.getData().get("fromID");
            from = remoteMessage.getData().get("fromName");
            objectID = remoteMessage.getData().get("object");
            object = remoteMessage.getData().get("objectName");
            notificationImageURL = remoteMessage.getData().get("notificationImageURL");
            message = remoteMessage.getData().get("message");
            activityID = remoteMessage.getData().get("activityID");



            sendMessageNotification(dataType, accountType, title, from, object, notificationImageURL, message, activityID);
        }
    }

    /**
     * Build a push notification for a chat message
     * @param title
     * @param message
     */
    private void sendMessageNotification(String dataType, String accountType, String title, String from, String object, String notificationImageURL, String message, String activityID){

        new PushNotificationTask().execute();

    }

    private int buildNotificationId(String dataType){
        int notificationId;
        switch(dataType) {
            case "ClassPost":
                notificationId = 1;
                break;
            case "Comment":
                notificationId = 2;
                break;
            case "Like":
                notificationId = 3;
                break;
            case "Event":
                notificationId = 4;
                break;
            case "EventReminder":
                notificationId = 14;
                break;
            case "Newsletter":
                notificationId = 5;
                break;
            case "EClassroom":
                notificationId = 6;
                break;
            case "ELibraryAssignment":
                notificationId = 7;
                break;
            case "ConnectionRequest":
                notificationId = 8;
                break;
            case "ConnectionRequestDeclined":
                notificationId = 9;
                break;
            case "Disconnection":
                notificationId = 10;
                break;
            case "Connection":
                notificationId = 11;
                break;
            case "NewResultPost":
                notificationId = 12;
                break;
            case "NewBehaviouralPost":
                notificationId = 13;
                break;
            case "NewAttendancePost":
                notificationId = 14;
                break;
            case "Message":
                notificationId = 15;
                break;
            default:
                notificationId = 0;
        }

        return notificationId;
    }

    class PushNotificationTask extends AsyncTask<Void, Void, Void> {
        NotificationCompat.Builder builder;
        int notificationId;

        @Override
        protected Void doInBackground(Void... voids) {
            notificationId = buildNotificationId(dataType); //TODO: Modify

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("Notification", "Notification", NotificationManager.IMPORTANCE_HIGH);
                channel.setShowBadge(true);
                NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(channel);
            }

            builder = new NotificationCompat.Builder(getBaseContext(), "Notification");
            Intent resultIntent;
            Bundle bundle;
            TaskStackBuilder stackBuilder;
            PendingIntent resultPendingIntent;

            SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getBaseContext());
            String activeAccount = sharedPreferencesManager.getActiveAccount();
            String fullName = sharedPreferencesManager.getMyFirstName() + " " + sharedPreferencesManager.getMyLastName();

            switch(dataType) {
                case "ClassPost":
                case "Comment":
                case "Like":
                    resultIntent = new Intent(getBaseContext(), CommentStoryActivity.class);
                    bundle = new Bundle();
                    bundle.putString("postKey", activityID);
                    bundle.putString("parentActivity", accountType);
                    resultIntent.putExtras(bundle);
                    stackBuilder = TaskStackBuilder.create(getBaseContext());
                    stackBuilder.addNextIntentWithParentStack(resultIntent);
                    resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    try {
                        URL myURL = new URL(notificationImageURL);
                        Bitmap bitmap = BitmapFactory.decodeStream(myURL.openConnection().getInputStream());
                        builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                                .setLargeIcon(bitmap)
                                .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryPurpleNotification))
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setSubText(fullName)
                                .setContentTitle(from)
                                .setContentText(message)
                                .setAutoCancel(true)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                .setStyle(new NotificationCompat.BigPictureStyle()
                                        .bigPicture(bitmap))
                                .setOnlyAlertOnce(true)
                                .setContentIntent(resultPendingIntent);
                    } catch(Exception e) {
                        System.out.println(e);
                        builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                                .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryPurpleNotification))
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setSubText(fullName)
                                .setContentTitle(from)
                                .setContentText(message)
                                .setAutoCancel(true)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                .setOnlyAlertOnce(true)
                                .setContentIntent(resultPendingIntent);
                    }
                    return null;
                case "Event":
                case "EventReminder":
                    resultIntent = new Intent(getBaseContext(), EventDetailActivity.class);
                    bundle = new Bundle();
                    bundle.putString("Event ID", activityID);
                    bundle.putString("Color Number", "0");
                    bundle.putString("parentActivity", accountType);
                    resultIntent.putExtras(bundle);
                    stackBuilder = TaskStackBuilder.create(getBaseContext());
                    stackBuilder.addNextIntentWithParentStack(resultIntent);
                    resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    Bitmap bitmap = drawableToBitmap(CreateTextDrawable.createTextDrawable(getBaseContext(), from));

                    builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                            .setLargeIcon(bitmap)
                            .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryPurpleNotification))
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setSubText("Event")
                            .setContentTitle(from)
                            .setContentText(message)
                            .setAutoCancel(true)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                            .setOnlyAlertOnce(true)
                            .setContentIntent(resultPendingIntent);

                    return null;
                case "Newsletter":
                    resultIntent = new Intent(getBaseContext(), NewsletterDetailActivity.class);
                    bundle = new Bundle();
                    bundle.putString("Event ID", activityID);
                    resultIntent.putExtras(bundle);
                    stackBuilder = TaskStackBuilder.create(getBaseContext());
                    stackBuilder.addNextIntentWithParentStack(resultIntent);
                    resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    bitmap = drawableToBitmap(CreateTextDrawable.createTextDrawable(getBaseContext(), from));

                    builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                            .setLargeIcon(bitmap)
                            .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryPurpleNotification))
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setSubText("Newsletter")
                            .setContentTitle(from)
                            .setContentText(message)
                            .setAutoCancel(true)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                            .setOnlyAlertOnce(true)
                            .setContentIntent(resultPendingIntent);
                    return null;
                case "EClassroom":
//                    notificationId += DumbNumericals.generateIntIDFromStringID(objectID);
                    resultIntent = new Intent(getBaseContext(), ParentMainActivityTwo.class);

                    Student student = new Student(object, objectID);
                    Gson gson = new Gson();
                    String studentJSON = gson.toJson(student);

                    bundle = new Bundle();
                    bundle.putString("Fragment Int", "2");
                    resultIntent.putExtras(bundle);

                    stackBuilder = TaskStackBuilder.create(getBaseContext());
                    stackBuilder.addNextIntentWithParentStack(resultIntent);
                    resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    try {
                        if (!notificationImageURL.isEmpty()) {
                            URL myURL = new URL(notificationImageURL);
                            bitmap = getCircleBitmap(BitmapFactory.decodeStream(myURL.openConnection().getInputStream()));
                        } else {
                            if (from != null && !from.isEmpty())
                                bitmap = drawableToBitmap(CreateTextDrawable.createTextDrawable(getBaseContext(), object));
                            else
                                throw new Exception();
                        }

                        builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                                .setLargeIcon(bitmap)
                                .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryPurpleNotification))
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setSubText("New E Classroom")
                                .setContentTitle(object)
                                .setContentText(message)
                                .setAutoCancel(true)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                .setOnlyAlertOnce(true)
                                .setContentIntent(resultPendingIntent);
                    } catch(Exception e) {
                        System.out.println(e);
                        builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                                .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryPurpleNotification))
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setSubText("New E Classroom")
                                .setContentTitle(object)
                                .setContentText(message)
                                .setAutoCancel(true)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                .setOnlyAlertOnce(true)
                                .setContentIntent(resultPendingIntent);
                    }
                    return null;
                case "ELibraryAssignment":
//                    notificationId += DumbNumericals.generateIntIDFromStringID(objectID);
                    resultIntent = new Intent(getBaseContext(), ParentMainActivityTwo.class);

                    student = new Student(object, objectID);
                    gson = new Gson();
                    studentJSON = gson.toJson(student);

                    bundle = new Bundle();
                    bundle.putString("Fragment Int", "2");
                    resultIntent.putExtras(bundle);

                    stackBuilder = TaskStackBuilder.create(getBaseContext());
                    stackBuilder.addNextIntentWithParentStack(resultIntent);
                    resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    try {
                        if (!notificationImageURL.isEmpty()) {
                            URL myURL = new URL(notificationImageURL);
                            bitmap = getCircleBitmap(BitmapFactory.decodeStream(myURL.openConnection().getInputStream()));
                        } else {
                            if (from != null && !from.isEmpty())
                                bitmap = drawableToBitmap(CreateTextDrawable.createTextDrawable(getBaseContext(), object));
                            else
                                throw new Exception();
                        }

                        builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                                .setLargeIcon(bitmap)
                                .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryPurpleNotification))
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setSubText("New E Library Assignment")
                                .setContentTitle(object)
                                .setContentText(message)
                                .setAutoCancel(true)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                .setOnlyAlertOnce(true)
                                .setContentIntent(resultPendingIntent);
                    } catch(Exception e) {
                        System.out.println(e);
                        builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                                .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryPurpleNotification))
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setSubText("New E Library Assignment")
                                .setContentTitle(object)
                                .setContentText(message)
                                .setAutoCancel(true)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                .setOnlyAlertOnce(true)
                                .setContentIntent(resultPendingIntent);
                    }
                    return null;
                case "ConnectionRequest":
                    if (accountType.equals("Parent")) {
                        notificationId += 100;
                        resultIntent = new Intent(getBaseContext(), ParentsRequestActivity.class);
                        bundle = new Bundle();
                        bundle.putString("parentActivity", accountType);
                        resultIntent.putExtras(bundle);
                        stackBuilder = TaskStackBuilder.create(getBaseContext());
                        stackBuilder.addNextIntentWithParentStack(resultIntent);
                        resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                        try {
                            if (!notificationImageURL.isEmpty()) {
                                URL myURL = new URL(notificationImageURL);
                                bitmap = getCircleBitmap(BitmapFactory.decodeStream(myURL.openConnection().getInputStream()));
                            } else {
                                if (from != null && !from.isEmpty())
                                    bitmap = drawableToBitmap(CreateTextDrawable.createTextDrawable(getBaseContext(), object));
                                else
                                    throw new Exception();
                            }

                            builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                                    .setLargeIcon(bitmap)
                                    .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryPurpleNotification))
                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                    .setSubText("Connection Request")
                                    .setContentTitle(object)
                                    .setContentText(message)
                                    .setAutoCancel(true)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                    .setOnlyAlertOnce(true)
                                    .setContentIntent(resultPendingIntent);
                        } catch(Exception e) {
                            System.out.println(e);
                            builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                                    .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryPurpleNotification))
                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                    .setSubText("Connection Request")
                                    .setContentTitle(object)
                                    .setContentText(message)
                                    .setAutoCancel(true)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                    .setOnlyAlertOnce(true)
                                    .setContentIntent(resultPendingIntent);
                        }
                    } else {
                        notificationId += 200;
                        resultIntent = new Intent(getBaseContext(), TeacherRequestActivity.class);
                        bundle = new Bundle();
                        bundle.putString("parentActivity", accountType);
                        resultIntent.putExtras(bundle);
                        stackBuilder = TaskStackBuilder.create(getBaseContext());
                        stackBuilder.addNextIntentWithParentStack(resultIntent);
                        resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                        try {
                            if (!notificationImageURL.isEmpty()) {
                                URL myURL = new URL(notificationImageURL);
                                bitmap = getCircleBitmap(BitmapFactory.decodeStream(myURL.openConnection().getInputStream()));
                            } else {
                                if (from != null && !from.isEmpty())
                                    bitmap = drawableToBitmap(CreateTextDrawable.createTextDrawable(getBaseContext(), from));
                                else
                                    throw new Exception();
                            }

                            builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                                    .setLargeIcon(bitmap)
                                    .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryPurpleNotification))
                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                    .setSubText("Connection Request")
                                    .setContentTitle(from)
                                    .setContentText(message)
                                    .setAutoCancel(true)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                    .setOnlyAlertOnce(true)
                                    .setContentIntent(resultPendingIntent);
                        } catch(Exception e) {
                            System.out.println(e);
                            builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                                    .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryPurpleNotification))
                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                    .setSubText("Connection Request")
                                    .setContentTitle(from)
                                    .setContentText(message)
                                    .setAutoCancel(true)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                    .setOnlyAlertOnce(true)
                                    .setContentIntent(resultPendingIntent);
                        }
                    }
                    return null;
                case "ConnectionRequestDeclined":
                case "Disconnection":
                    if (accountType.equals("Parent")) {
                        notificationId += 100;
                        resultIntent = new Intent(getBaseContext(), ParentMainActivityTwo.class);
                        bundle = new Bundle();
                        bundle.putString("Fragment Int", "2");
                        resultIntent.putExtras(bundle);
                        stackBuilder = TaskStackBuilder.create(getBaseContext());
                        stackBuilder.addNextIntentWithParentStack(resultIntent);
                        resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                        try {
                            if (!notificationImageURL.isEmpty()) {
                                URL myURL = new URL(notificationImageURL);
                                bitmap = getCircleBitmap(BitmapFactory.decodeStream(myURL.openConnection().getInputStream()));
                            } else {
                                if (from != null && !from.isEmpty())
                                    bitmap = drawableToBitmap(CreateTextDrawable.createTextDrawable(getBaseContext(), object));
                                else
                                    throw new Exception();
                            }

                            builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                                    .setLargeIcon(bitmap)
                                    .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryPurpleNotification))
                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                    .setSubText(fullName)
                                    .setContentTitle(object)
                                    .setContentText(message)
                                    .setAutoCancel(true)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                    .setOnlyAlertOnce(true)
                                    .setContentIntent(resultPendingIntent);
                        } catch(Exception e) {
                            System.out.println(e);
                            builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                                    .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryPurpleNotification))
                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                    .setSubText(fullName)
                                    .setContentTitle(object)
                                    .setContentText(message)
                                    .setAutoCancel(true)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                    .setOnlyAlertOnce(true)
                                    .setContentIntent(resultPendingIntent);
                        }
                    } else {
                        notificationId += 200;
                        resultIntent = new Intent(getBaseContext(), TeacherMainActivityTwo.class);
                        bundle = new Bundle();
                        bundle.putString("Fragment Int", "3");
                        resultIntent.putExtras(bundle);
                        stackBuilder = TaskStackBuilder.create(getBaseContext());
                        stackBuilder.addNextIntentWithParentStack(resultIntent);
                        resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                        try {
                            if (!notificationImageURL.isEmpty()) {
                                URL myURL = new URL(notificationImageURL);
                                bitmap = getCircleBitmap(BitmapFactory.decodeStream(myURL.openConnection().getInputStream()));
                            } else {
                                if (from != null && !from.isEmpty())
                                    bitmap = drawableToBitmap(CreateTextDrawable.createTextDrawable(getBaseContext(), from));
                                else
                                    throw new Exception();
                            }

                            builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                                    .setLargeIcon(bitmap)
                                    .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryPurpleNotification))
                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                    .setSubText(fullName)
                                    .setContentTitle(from)
                                    .setContentText(message)
                                    .setAutoCancel(true)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                    .setOnlyAlertOnce(true)
                                    .setContentIntent(resultPendingIntent);
                        } catch(Exception e) {
                            System.out.println(e);
                            builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                                    .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryPurpleNotification))
                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                    .setSubText(fullName)
                                    .setContentTitle(from)
                                    .setContentText(message)
                                    .setAutoCancel(true)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                    .setOnlyAlertOnce(true)
                                    .setContentIntent(resultPendingIntent);
                        }
                    }
                    return null;
                case "Connection":
                    if (accountType.equals("Parent")) {
                        notificationId += 100;
                        if (fromAccountType.equals("Parent")) {
                            resultIntent = new Intent(getBaseContext(), ParentProfileActivity.class);
                            bundle = new Bundle();
                            bundle.putString("parentID", fromID);
                        } else {
                            resultIntent = new Intent(getBaseContext(), SchoolProfileActivity.class);
                            bundle = new Bundle();
                            bundle.putString("schoolID", fromID);
                        }

                        bundle.putString("parentActivity", accountType);
                        resultIntent.putExtras(bundle);
                        stackBuilder = TaskStackBuilder.create(getBaseContext());
                        stackBuilder.addNextIntentWithParentStack(resultIntent);
                        resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                        try {
                            if (!notificationImageURL.isEmpty()) {
                                URL myURL = new URL(notificationImageURL);
                                bitmap = getCircleBitmap(BitmapFactory.decodeStream(myURL.openConnection().getInputStream()));
                            } else {
                                if (from != null && !from.isEmpty())
                                    bitmap = drawableToBitmap(CreateTextDrawable.createTextDrawable(getBaseContext(), object));
                                else
                                    throw new Exception();
                            }

                            builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                                    .setLargeIcon(bitmap)
                                    .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryPurpleNotification))
                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                    .setSubText("New Connection")
                                    .setContentTitle(object)
                                    .setContentText(message)
                                    .setAutoCancel(true)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                    .setOnlyAlertOnce(true)
                                    .setContentIntent(resultPendingIntent);
                        } catch(Exception e) {
                            System.out.println(e);
                            builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                                    .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryPurpleNotification))
                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                    .setSubText("New Connection")
                                    .setContentTitle(object)
                                    .setContentText(message)
                                    .setAutoCancel(true)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                    .setOnlyAlertOnce(true)
                                    .setContentIntent(resultPendingIntent);
                        }
                    } else {
                        notificationId += 200;
                        resultIntent = new Intent(getBaseContext(), SchoolProfileActivity.class);
                        bundle = new Bundle();
                        bundle.putString("schoolID", fromID);
                        bundle.putString("parentActivity", accountType);
                        resultIntent.putExtras(bundle);
                        stackBuilder = TaskStackBuilder.create(getBaseContext());
                        stackBuilder.addNextIntentWithParentStack(resultIntent);
                        resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                        try {
                            if (!notificationImageURL.isEmpty()) {
                                URL myURL = new URL(notificationImageURL);
                                bitmap = getCircleBitmap(BitmapFactory.decodeStream(myURL.openConnection().getInputStream()));
                            } else {
                                if (from != null && !from.isEmpty())
                                    bitmap = drawableToBitmap(CreateTextDrawable.createTextDrawable(getBaseContext(), from));
                                else
                                    throw new Exception();
                            }

                            builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                                    .setLargeIcon(bitmap)
                                    .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryPurpleNotification))
                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                    .setSubText("New Connection")
                                    .setContentTitle(from)
                                    .setContentText(message)
                                    .setAutoCancel(true)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                    .setOnlyAlertOnce(true)
                                    .setContentIntent(resultPendingIntent);
                        } catch(Exception e) {
                            System.out.println(e);
                            builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                                    .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryPurpleNotification))
                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                    .setSubText("New Connection")
                                    .setContentTitle(from)
                                    .setContentText(message)
                                    .setAutoCancel(true)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                    .setOnlyAlertOnce(true)
                                    .setContentIntent(resultPendingIntent);
                        }
                    }
                    return null;
                case "NewResultPost":
//                    notificationId += DumbNumericals.generateIntIDFromStringID(objectID);
                    resultIntent = new Intent(getBaseContext(), StudentPerformanceForParentsActivity.class);

                    student = new Student(object, objectID);
                    gson = new Gson();
                    studentJSON = gson.toJson(student);

                    bundle = new Bundle();
                    bundle.putString("Child ID", studentJSON);
                    bundle.putString("parentActivity", accountType);
                    resultIntent.putExtras(bundle);

                    stackBuilder = TaskStackBuilder.create(getBaseContext());
                    stackBuilder.addNextIntentWithParentStack(resultIntent);
                    resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    try {
                        if (!notificationImageURL.isEmpty()) {
                            URL myURL = new URL(notificationImageURL);
                            bitmap = getCircleBitmap(BitmapFactory.decodeStream(myURL.openConnection().getInputStream()));
                        } else {
                            if (from != null && !from.isEmpty())
                                bitmap = drawableToBitmap(CreateTextDrawable.createTextDrawable(getBaseContext(), object));
                            else
                                throw new Exception();
                        }

                        builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                                .setLargeIcon(bitmap)
                                .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryPurpleNotification))
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setSubText("New Academic Result")
                                .setContentTitle(object)
                                .setContentText(message)
                                .setAutoCancel(true)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                .setOnlyAlertOnce(true)
                                .setContentIntent(resultPendingIntent);
                    } catch(Exception e) {
                        System.out.println(e);
                        builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                                .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryPurpleNotification))
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setSubText("New Academic Result")
                                .setContentTitle(object)
                                .setContentText(message)
                                .setAutoCancel(true)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                .setOnlyAlertOnce(true)
                                .setContentIntent(resultPendingIntent);
                    }
                    return null;
                case "NewBehaviouralPost":
//                    notificationId += DumbNumericals.generateIntIDFromStringID(objectID);
                    resultIntent = new Intent(getBaseContext(), BehaviouralResultActivity.class);

                    student = new Student(object, objectID);
                    gson = new Gson();
                    studentJSON = gson.toJson(student);

                    bundle = new Bundle();
                    bundle.putString("ChildID", studentJSON);
                    bundle.putString("parentActivity", accountType);
                    resultIntent.putExtras(bundle);

                    stackBuilder = TaskStackBuilder.create(getBaseContext());
                    stackBuilder.addNextIntentWithParentStack(resultIntent);
                    resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    try {
                        if (!notificationImageURL.isEmpty()) {
                            URL myURL = new URL(notificationImageURL);
                            bitmap = getCircleBitmap(BitmapFactory.decodeStream(myURL.openConnection().getInputStream()));
                        } else {
                            if (from != null && !from.isEmpty())
                                bitmap = drawableToBitmap(CreateTextDrawable.createTextDrawable(getBaseContext(), object));
                            else
                                throw new Exception();
                        }

                        builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                                .setLargeIcon(bitmap)
                                .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryPurpleNotification))
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setSubText("New Behavioural Result")
                                .setContentTitle(object)
                                .setContentText(message)
                                .setAutoCancel(true)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                .setOnlyAlertOnce(true)
                                .setContentIntent(resultPendingIntent);
                    } catch(Exception e) {
                        System.out.println(e);
                        builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                                .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryPurpleNotification))
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setSubText("New Behavioural Result")
                                .setContentTitle(object)
                                .setContentText(message)
                                .setAutoCancel(true)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                .setOnlyAlertOnce(true)
                                .setContentIntent(resultPendingIntent);
                    }
                    return null;
                case "NewAttendancePost":
//                    notificationId += DumbNumericals.generateIntIDFromStringID(objectID);
                    resultIntent = new Intent(getBaseContext(), ParentAttendanceActivity.class);

                    student = new Student(object, objectID);
                    gson = new Gson();
                    studentJSON = gson.toJson(student);

                    bundle = new Bundle();
                    bundle.putString("Child ID", studentJSON);
                    bundle.putString("parentActivity", accountType);
                    resultIntent.putExtras(bundle);

                    stackBuilder = TaskStackBuilder.create(getBaseContext());
                    stackBuilder.addNextIntentWithParentStack(resultIntent);
                    resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    try {
                        if (!notificationImageURL.isEmpty()) {
                            URL myURL = new URL(notificationImageURL);
                            bitmap = getCircleBitmap(BitmapFactory.decodeStream(myURL.openConnection().getInputStream()));
                        } else {
                            if (from != null && !from.isEmpty())
                                bitmap = drawableToBitmap(CreateTextDrawable.createTextDrawable(getBaseContext(), object));
                            else
                                throw new Exception();
                        }

                        builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                                .setLargeIcon(bitmap)
                                .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryPurpleNotification))
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setSubText("New Attendance")
                                .setContentTitle(object)
                                .setContentText(message)
                                .setAutoCancel(true)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                .setOnlyAlertOnce(true)
                                .setContentIntent(resultPendingIntent);
                    } catch(Exception e) {
                        System.out.println(e);
                        builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                                .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryPurpleNotification))
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setSubText("New Attendance")
                                .setContentTitle(object)
                                .setContentText(message)
                                .setAutoCancel(true)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                .setOnlyAlertOnce(true)
                                .setContentIntent(resultPendingIntent);
                    }
                    return null;
                case "Message":
                    if (!fromID.equals(mFirebaseUser.getUid())) {
                        resultIntent = new Intent(getBaseContext(), ChatActivity.class);
                        bundle = new Bundle();
                        bundle.putString("ID", fromID);
                        bundle.putString("name", from);
                        bundle.putString("parentActivity", activeAccount);
                        resultIntent.putExtras(bundle);
                        stackBuilder = TaskStackBuilder.create(getBaseContext());
                        stackBuilder.addNextIntentWithParentStack(resultIntent);
                        resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                        try {
                            if (!notificationImageURL.isEmpty()) {
                                URL myURL = new URL(notificationImageURL);
                                bitmap = getCircleBitmap(BitmapFactory.decodeStream(myURL.openConnection().getInputStream()));
                            } else {
                                if (from != null && !from.isEmpty())
                                    bitmap = drawableToBitmap(CreateTextDrawable.createTextDrawable(getBaseContext(), from));
                                else
                                    throw new Exception();
                            }

                            builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                                    .setLargeIcon(bitmap)
                                    .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryPurpleNotification))
                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                    .setSubText("New Message")
                                    .setContentTitle(from)
                                    .setContentText(message)
                                    .setAutoCancel(true)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                    .setOnlyAlertOnce(true)
                                    .setContentIntent(resultPendingIntent);
                        } catch (Exception e) {
                            System.out.println(e);
                            builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                                    .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryPurpleNotification))
                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                    .setSubText("New Message")
                                    .setContentTitle(from)
                                    .setContentText(message)
                                    .setAutoCancel(true)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                    .setOnlyAlertOnce(true)
                                    .setContentIntent(resultPendingIntent);
                        }
                    }
                    return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void void_) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(notificationId, builder.build());
            super.onPostExecute(void_);
        }
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        Bitmap output;
        Rect srcRect, dstRect;
        int r;
        final int w = bitmap.getWidth();
        final int h = bitmap.getHeight();

        if (w > h){
            output = Bitmap.createBitmap(h, h, Bitmap.Config.ARGB_8888);
            int left = (w - h) / 2;
            int right = left + h;
            srcRect = new Rect(left, 0, right, h);
            dstRect = new Rect(0, 0, h, h);
            r = h / 2;
        }else{
            output = Bitmap.createBitmap(w, w, Bitmap.Config.ARGB_8888);
            int top = (h - w)/2;
            int bottom = top + w;
            srcRect = new Rect(0, top, w, bottom);
            dstRect = new Rect(0, 0, w, w);
            r = w / 2;
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint);

        bitmap.recycle();

        return output;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 96; // Replaced the 1 by a 96
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 96; // Replaced the 1 by a 96

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
