package com.celerii.celerii.helperClasses;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
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
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.amulyakhare.textdrawable.TextDrawable;
import com.celerii.celerii.Activities.Comment.CommentStoryActivity;
import com.celerii.celerii.Activities.EClassroom.Parent.ParentEClassroomMessageBoardActivity;
import com.celerii.celerii.Activities.ELibrary.Parent.ELibraryParentAssignmentActivity;
import com.celerii.celerii.Activities.EMeeting.Parent.ParentEMeetingMessageBoardActivity;
import com.celerii.celerii.Activities.EMeeting.Teacher.TeacherEMeetingMessageBoardActivity;
import com.celerii.celerii.Activities.Events.EventDetailActivity;
import com.celerii.celerii.Activities.Home.NotificationDetailActivity;
import com.celerii.celerii.Activities.Home.Parent.ParentHomeNotification;
import com.celerii.celerii.Activities.Home.Parent.ParentMainActivityTwo;
import com.celerii.celerii.Activities.Home.Parent.ParentsRequestActivity;
import com.celerii.celerii.Activities.Home.Teacher.TeacherMainActivityTwo;
import com.celerii.celerii.Activities.Home.Teacher.TeacherRequestActivity;
import com.celerii.celerii.Activities.Inbox.ChatActivity;
import com.celerii.celerii.Activities.Newsletters.NewsletterDetailActivity;
import com.celerii.celerii.Activities.Newsletters.NewsletterRowActivity;
import com.celerii.celerii.Activities.Profiles.ParentProfileActivity;
import com.celerii.celerii.Activities.Profiles.SchoolProfile.SchoolProfileActivity;
import com.celerii.celerii.Activities.StudentAttendance.AttendanceDetailActivity;
import com.celerii.celerii.Activities.StudentAttendance.ParentAttendanceActivity;
import com.celerii.celerii.Activities.StudentBehaviouralPerformance.BehaviouralResultActivity;
import com.celerii.celerii.Activities.StudentPerformance.StudentPerformanceForParentsActivity;
import com.celerii.celerii.Activities.Utility.ApplicationLaunchActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.models.EClassroomScheduledClassesListModel;
import com.celerii.celerii.models.ELibraryMyAssignmentModel;
import com.celerii.celerii.models.EMeetingScheduledMeetingsListModel;
import com.celerii.celerii.models.EventsRow;
import com.celerii.celerii.models.NotificationBadgeModel;
import com.celerii.celerii.models.ReminderModel;
import com.celerii.celerii.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

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

            prepareTeacherEventReminders();

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
            case "Newsletter":
                notificationId = 5;
                break;
            case "EClassroom":
                notificationId = 6;
                break;
            case "ELibraryAssignment":
                notificationId = 7;
                break;
            case "EMeeting":
                notificationId = 8;
                break;
            case "ConnectionRequest":
                notificationId = 9;
                break;
            case "ConnectionRequestDeclined":
                notificationId = 10;
                break;
            case "Disconnection":
                notificationId = 11;
                break;
            case "Connection":
                notificationId = 12;
                break;
            case "NewResultPost":
                notificationId = 13;
                break;
            case "NewBehaviouralPost":
                notificationId = 14;
                break;
            case "NewAttendancePost":
                notificationId = 15;
                break;
            case "Message":
                notificationId = 16;
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
                    resultIntent = new Intent(getBaseContext(), NewsletterRowActivity.class);
                    sharedPreferencesManager.setActiveAccount(activeAccount);
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
                case "EMeeting":
                    bundle = new Bundle();

                    if (accountType.equals("Parent")) {
                        notificationId += 100;
                        resultIntent = new Intent(getBaseContext(), ParentMainActivityTwo.class);

                        bundle.putString("Fragment Int", "2");
                    } else {
                        notificationId += 200;
                        resultIntent = new Intent(getBaseContext(), TeacherMainActivityTwo.class);

                        bundle.putString("Fragment Int", "3");
                    }

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
                                .setSubText("New Meeting")
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
                                .setSubText("New Meeting")
                                .setContentTitle(from)
                                .setContentText(message)
                                .setAutoCancel(true)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                .setOnlyAlertOnce(true)
                                .setContentIntent(resultPendingIntent);
                    }
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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setChannelId("com.celerii.celerii");
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        "com.celerii.celerii",
                        "Celerii",
                        NotificationManager.IMPORTANCE_DEFAULT);

                if (mNotificationManager != null) {
                    mNotificationManager.createNotificationChannel(channel);
                }
            }

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

    int idCounter;
    int teacherEventCounter;
    int parentEventCounter;
    int studentCounter;
    ArrayList<Integer> reminderIDs;
    HashMap<Integer, ReminderModel> reminderModels;
    ArrayList<Student> children;
    SharedPreferencesManager sharedPreferencesManager;
    public void prepareTeacherEventReminders() {
        idCounter = 0;
        teacherEventCounter = 0;
        parentEventCounter = 0;
        studentCounter = 0;
        sharedPreferencesManager = new SharedPreferencesManager(getBaseContext());

        Gson gson = new Gson();
        String reminderIDsJSON = sharedPreferencesManager.getReminderIDs();
        Type type = new TypeToken<ArrayList<Integer>>() {}.getType();
        reminderModels = new HashMap<>();
        reminderIDs = gson.fromJson(reminderIDsJSON, type);

        if (reminderIDs != null) {
            for (int reminderID : reminderIDs) {
                deleteAlarm(reminderID);
            }
            reminderIDs.clear();
        } else {
            reminderIDs = new ArrayList<>();
        }

        sharedPreferencesManager.deleteReminderIDs();
        sharedPreferencesManager.deleteReminderDetails();

        gson = new Gson();
        String myChildrenJSON = sharedPreferencesManager.getMyChildren();
        type = new TypeToken<ArrayList<Student>>() {}.getType();
        children = gson.fromJson(myChildrenJSON, type);

        try {
            mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher Events").child(mFirebaseUser.getUid());
            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            int teacherEventChildrenCount = (int) dataSnapshot.getChildrenCount();
                            String eventKey = postSnapshot.getKey();

                            mDatabaseReference = mFirebaseDatabase.getReference().child("Event").child(eventKey);
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    teacherEventCounter++;
                                    if (dataSnapshot.exists()) {
                                        EventsRow eventRow = dataSnapshot.getValue(EventsRow.class);

                                        String todaysDate = (Date.getDate());
                                        if (Date.compareDates(eventRow.getEventDate(), todaysDate)) {
                                            ReminderModel reminderModel = new ReminderModel();
                                            reminderModel.setActivityID(eventKey);
                                            reminderModel.setAccountType("Teacher");
                                            reminderModel.setReminderType("Event");
                                            reminderModel.setEventTitle(eventRow.getEventTitle());
                                            reminderModel.setEventSender(eventRow.getSchoolID());
                                            reminderModel.setOriginalScheduledDate(eventRow.getEventDate());

                                            ReminderModel zeroMinutes = getTimeInMilliSeconds(reminderModel);
                                            createAlarm(idCounter, zeroMinutes.getTimeInMilliseconds());
                                            reminderIDs.add(idCounter);
                                            reminderModels.put(idCounter, zeroMinutes);
                                            idCounter++;

                                            ReminderModel tenMinutes = getMinusTenMinutesInMilliSeconds(reminderModel);
                                            createAlarm(idCounter, tenMinutes.getTimeInMilliseconds());
                                            reminderIDs.add(idCounter);
                                            reminderModels.put(idCounter, tenMinutes);
                                            idCounter++;

                                            ReminderModel oneHour = getMinusOneHourInMilliSeconds(reminderModel);
                                            createAlarm(idCounter, oneHour.getTimeInMilliseconds());
                                            reminderIDs.add(idCounter);
                                            reminderModels.put(idCounter, oneHour);
                                            idCounter++;

                                            ReminderModel oneDay = getMinusOneDayInMilliSeconds(reminderModel);
                                            createAlarm(idCounter, oneDay.getTimeInMilliseconds());
                                            reminderIDs.add(idCounter);
                                            reminderModels.put(idCounter, oneDay);
                                            idCounter++;
                                        }
                                    }

                                    if (teacherEventCounter == teacherEventChildrenCount) {
                                        prepareParentEventReminders();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    } else {
                        prepareParentEventReminders();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            return;
        }
    }

    private void prepareParentEventReminders() {
        mDatabaseReference = mFirebaseDatabase.getReference().child("Parent Events").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        int parentEventChildrenCount = (int) dataSnapshot.getChildrenCount();
                        String eventKey = postSnapshot.getKey();

                        mDatabaseReference = mFirebaseDatabase.getReference().child("Event").child(eventKey);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                parentEventCounter++;
                                if (dataSnapshot.exists()) {
                                    EventsRow eventRow = dataSnapshot.getValue(EventsRow.class);

                                    String todaysDate = (Date.getDate());
                                    if (Date.compareDates(eventRow.getEventDate(), todaysDate)) {
                                        ReminderModel reminderModel = new ReminderModel();
                                        reminderModel.setActivityID(eventKey);
                                        reminderModel.setAccountType("Parent");
                                        reminderModel.setReminderType("Event");
                                        reminderModel.setEventTitle(eventRow.getEventTitle());
                                        reminderModel.setEventSender(eventRow.getSchoolID());
                                        reminderModel.setOriginalScheduledDate(eventRow.getEventDate());

                                        ReminderModel zeroMinutes = getTimeInMilliSeconds(reminderModel);
                                        createAlarm(idCounter, zeroMinutes.getTimeInMilliseconds());
                                        reminderIDs.add(idCounter);
                                        reminderModels.put(idCounter, zeroMinutes);
                                        idCounter++;

                                        ReminderModel tenMinutes = getMinusTenMinutesInMilliSeconds(reminderModel);
                                        createAlarm(idCounter, tenMinutes.getTimeInMilliseconds());
                                        reminderIDs.add(idCounter);
                                        reminderModels.put(idCounter, tenMinutes);
                                        idCounter++;

                                        ReminderModel oneHour = getMinusOneHourInMilliSeconds(reminderModel);
                                        createAlarm(idCounter, oneHour.getTimeInMilliseconds());
                                        reminderIDs.add(idCounter);
                                        reminderModels.put(idCounter, oneHour);
                                        idCounter++;

                                        ReminderModel oneDay = getMinusOneDayInMilliSeconds(reminderModel);
                                        createAlarm(idCounter, oneDay.getTimeInMilliseconds());
                                        reminderIDs.add(idCounter);
                                        reminderModels.put(idCounter, oneDay);
                                        idCounter++;
                                    }
                                }

                                if (parentEventCounter == parentEventChildrenCount) {
                                    prepareTeacherEMeetingReminders();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    prepareTeacherEMeetingReminders();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void prepareTeacherEMeetingReminders() {
        mDatabaseReference = mFirebaseDatabase.getReference().child("E Meeting Scheduled Meeting").child("Teacher").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        EMeetingScheduledMeetingsListModel eMeetingScheduledMeetingsListModel = postSnapshot.getValue(EMeetingScheduledMeetingsListModel.class);

                        if (eMeetingScheduledMeetingsListModel.getOpen() == null) {
                            eMeetingScheduledMeetingsListModel.setOpen(true);
                        }

                        String todaysDate = (Date.getDate());
                        if (Date.compareDates(eMeetingScheduledMeetingsListModel.getDateScheduled(), todaysDate)) {
                            if (eMeetingScheduledMeetingsListModel.getOpen()) {
                                ReminderModel reminderModel = new ReminderModel();
                                reminderModel.setActivityID(postSnapshot.getKey());
                                reminderModel.setAccountType("Teacher");
                                reminderModel.setReminderType("TeacherEMeeting");
                                reminderModel.setMeetingTitle(eMeetingScheduledMeetingsListModel.getScheduledMeetingTitle());
                                reminderModel.setMeetingSchoolID(eMeetingScheduledMeetingsListModel.getSchoolID());
                                reminderModel.setMeetingSchoolName(eMeetingScheduledMeetingsListModel.getSchoolName());
                                reminderModel.setMeetingLink(eMeetingScheduledMeetingsListModel.getMeetingLink());
                                reminderModel.seteClassroomState("Scheduled");
                                reminderModel.setOriginalScheduledDate(eMeetingScheduledMeetingsListModel.getDateScheduled());

                                ReminderModel zeroMinutes = getTimeInMilliSeconds(reminderModel);
                                createAlarm(idCounter, zeroMinutes.getTimeInMilliseconds());
                                reminderIDs.add(idCounter);
                                reminderModels.put(idCounter, zeroMinutes);
                                idCounter++;

                                ReminderModel tenMinutes = getMinusTenMinutesInMilliSeconds(reminderModel);
                                createAlarm(idCounter, tenMinutes.getTimeInMilliseconds());
                                reminderIDs.add(idCounter);
                                reminderModels.put(idCounter, tenMinutes);
                                idCounter++;

                                ReminderModel oneHour = getMinusOneHourInMilliSeconds(reminderModel);
                                createAlarm(idCounter, oneHour.getTimeInMilliseconds());
                                reminderIDs.add(idCounter);
                                reminderModels.put(idCounter, oneHour);
                                idCounter++;

                                ReminderModel oneDay = getMinusOneDayInMilliSeconds(reminderModel);
                                createAlarm(idCounter, oneDay.getTimeInMilliseconds());
                                reminderIDs.add(idCounter);
                                reminderModels.put(idCounter, oneDay);
                                idCounter++;
                            }
                        }
                    }
                }

                prepareParentEMeetingReminders();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void prepareParentEMeetingReminders() {
        mDatabaseReference = mFirebaseDatabase.getReference().child("E Meeting Scheduled Meeting").child("Parent").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        EMeetingScheduledMeetingsListModel eMeetingScheduledMeetingsListModel = postSnapshot.getValue(EMeetingScheduledMeetingsListModel.class);

                        if (eMeetingScheduledMeetingsListModel.getOpen() == null) {
                            eMeetingScheduledMeetingsListModel.setOpen(true);
                        }

                        String todaysDate = (Date.getDate());
                        if (Date.compareDates(eMeetingScheduledMeetingsListModel.getDateScheduled(), todaysDate)) {
                            if (eMeetingScheduledMeetingsListModel.getOpen()) {
                                ReminderModel reminderModel = new ReminderModel();
                                reminderModel.setActivityID(postSnapshot.getKey());
                                reminderModel.setAccountType("Parent");
                                reminderModel.setReminderType("ParentEMeeting");
                                reminderModel.setMeetingTitle(eMeetingScheduledMeetingsListModel.getScheduledMeetingTitle());
                                reminderModel.setMeetingSchoolID(eMeetingScheduledMeetingsListModel.getSchoolID());
                                reminderModel.setMeetingSchoolName(eMeetingScheduledMeetingsListModel.getSchoolName());
                                reminderModel.setMeetingLink(eMeetingScheduledMeetingsListModel.getMeetingLink());
                                reminderModel.seteClassroomState("Scheduled");
                                reminderModel.setOriginalScheduledDate(eMeetingScheduledMeetingsListModel.getDateScheduled());

                                ReminderModel zeroMinutes = getTimeInMilliSeconds(reminderModel);
                                createAlarm(idCounter, zeroMinutes.getTimeInMilliseconds());
                                reminderIDs.add(idCounter);
                                reminderModels.put(idCounter, zeroMinutes);
                                idCounter++;

                                ReminderModel tenMinutes = getMinusTenMinutesInMilliSeconds(reminderModel);
                                createAlarm(idCounter, tenMinutes.getTimeInMilliseconds());
                                reminderIDs.add(idCounter);
                                reminderModels.put(idCounter, tenMinutes);
                                idCounter++;

                                ReminderModel oneHour = getMinusOneHourInMilliSeconds(reminderModel);
                                createAlarm(idCounter, oneHour.getTimeInMilliseconds());
                                reminderIDs.add(idCounter);
                                reminderModels.put(idCounter, oneHour);
                                idCounter++;

                                ReminderModel oneDay = getMinusOneDayInMilliSeconds(reminderModel);
                                createAlarm(idCounter, oneDay.getTimeInMilliseconds());
                                reminderIDs.add(idCounter);
                                reminderModels.put(idCounter, oneDay);
                                idCounter++;
                            }
                        }
                    }
                }

                prepareStudentELibraryAssignmentReminders();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void prepareStudentELibraryAssignmentReminders() {
        studentCounter = 0;
        if (children != null) {
            if (children.size() > 0) {
                for (Student child : children) {
                    mDatabaseReference = mFirebaseDatabase.getReference().child("E Library Assignment").child("Student").child(child.getStudentID());
                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            studentCounter++;
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    ELibraryMyAssignmentModel eLibraryMyAssignmentModel = postSnapshot.getValue(ELibraryMyAssignmentModel.class);

                                    String todaysDate = (Date.getDate());
                                    if (Date.compareDates(eLibraryMyAssignmentModel.getDueDate(), todaysDate)) {
                                        if (!eLibraryMyAssignmentModel.getSubmitted()) {
                                            ReminderModel reminderModel = new ReminderModel();
                                            reminderModel.setActivityID(postSnapshot.getKey());
                                            reminderModel.setAccountType("Parent");
                                            reminderModel.setReminderType("ELibraryAssignment");
                                            reminderModel.setAssignmentChildName(child.getFirstName() + " " + child.getLastName());
                                            reminderModel.setAssignmentChildID(child.getStudentID());
                                            reminderModel.setAssignmentChildProfilePictureURL(child.getImageURL());
                                            reminderModel.setAssignmentID(eLibraryMyAssignmentModel.getAssignmentID());
                                            reminderModel.setAssignmentTitle(eLibraryMyAssignmentModel.getMaterialTitle());
                                            reminderModel.setAssignmentMaterialID(eLibraryMyAssignmentModel.getMaterialID());

                                            ReminderModel zeroMinutes = getTimeInMilliSeconds(reminderModel);
                                            createAlarm(idCounter, zeroMinutes.getTimeInMilliseconds());
                                            reminderIDs.add(idCounter);
                                            reminderModels.put(idCounter, zeroMinutes);
                                            idCounter++;

                                            ReminderModel tenMinutes = getMinusTenMinutesInMilliSeconds(reminderModel);
                                            createAlarm(idCounter, tenMinutes.getTimeInMilliseconds());
                                            reminderIDs.add(idCounter);
                                            reminderModels.put(idCounter, tenMinutes);
                                            idCounter++;

                                            ReminderModel oneHour = getMinusOneHourInMilliSeconds(reminderModel);
                                            createAlarm(idCounter, oneHour.getTimeInMilliseconds());
                                            reminderIDs.add(idCounter);
                                            reminderModels.put(idCounter, oneHour);
                                            idCounter++;

                                            ReminderModel oneDay = getMinusOneDayInMilliSeconds(reminderModel);
                                            createAlarm(idCounter, oneDay.getTimeInMilliseconds());
                                            reminderIDs.add(idCounter);
                                            reminderModels.put(idCounter, oneDay);
                                            idCounter++;
                                        }
                                    }
                                }

                                if (studentCounter == children.size()) {
                                    prepareStudentEClassroomReminders();
                                }
                            } else {
                                prepareStudentEClassroomReminders();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            } else {
                prepareStudentEClassroomReminders();
            }
        } else {
            prepareStudentEClassroomReminders();
        }
    }

    private void prepareStudentEClassroomReminders() {
        studentCounter = 0;
        if (children != null) {
            if (children.size() > 0) {
                for (Student child : children) {
                    mDatabaseReference = mFirebaseDatabase.getReference().child("E Classroom Scheduled Class").child("Student").child(child.getStudentID());
                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            studentCounter++;
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    EClassroomScheduledClassesListModel eClassroomScheduledClassesListModel = postSnapshot.getValue(EClassroomScheduledClassesListModel.class);

                                    if (eClassroomScheduledClassesListModel.getOpen() == null) {
                                        eClassroomScheduledClassesListModel.setOpen(true);
                                    }

                                    String todaysDate = (Date.getDate());
                                    if (Date.compareDates(eClassroomScheduledClassesListModel.getDateScheduled(), todaysDate)) {
                                        if (eClassroomScheduledClassesListModel.getOpen()) {
                                            ReminderModel reminderModel = new ReminderModel();
                                            reminderModel.setActivityID(postSnapshot.getKey());
                                            reminderModel.setAccountType("Parent");
                                            reminderModel.setReminderType("EClassroom");
                                            reminderModel.seteClassroomChildName(child.getFirstName() + " " + child.getLastName());
                                            reminderModel.seteClassroomChildID(child.getStudentID());
                                            reminderModel.seteClassroomChildProfilePictureURL(child.getImageURL());
                                            reminderModel.seteClassroomLink(eClassroomScheduledClassesListModel.getClassLink());
                                            reminderModel.seteClassroomState("Scheduled");
                                            reminderModel.setOriginalScheduledDate(eClassroomScheduledClassesListModel.getDateScheduled());

                                            ReminderModel zeroMinutes = getTimeInMilliSeconds(reminderModel);
                                            createAlarm(idCounter, zeroMinutes.getTimeInMilliseconds());
                                            reminderIDs.add(idCounter);
                                            reminderModels.put(idCounter, zeroMinutes);
                                            idCounter++;

                                            ReminderModel tenMinutes = getMinusTenMinutesInMilliSeconds(reminderModel);
                                            createAlarm(idCounter, tenMinutes.getTimeInMilliseconds());
                                            reminderIDs.add(idCounter);
                                            reminderModels.put(idCounter, tenMinutes);
                                            idCounter++;

                                            ReminderModel oneHour = getMinusOneHourInMilliSeconds(reminderModel);
                                            createAlarm(idCounter, oneHour.getTimeInMilliseconds());
                                            reminderIDs.add(idCounter);
                                            reminderModels.put(idCounter, oneHour);
                                            idCounter++;

                                            ReminderModel oneDay = getMinusOneDayInMilliSeconds(reminderModel);
                                            createAlarm(idCounter, oneDay.getTimeInMilliseconds());
                                            reminderIDs.add(idCounter);
                                            reminderModels.put(idCounter, oneDay);
                                            idCounter++;
                                        }
                                    }
                                }

                                if (studentCounter == children.size()) {
                                    Gson gson = new Gson();
                                    String json = gson.toJson(reminderIDs);
                                    sharedPreferencesManager.setReminderIDs(json);

                                    gson = new Gson();
                                    json = gson.toJson(reminderModels);
                                    sharedPreferencesManager.setReminderDetails(json);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            } else {
                Gson gson = new Gson();
                String json = gson.toJson(reminderIDs);
                sharedPreferencesManager.setReminderIDs(json);

                gson = new Gson();
                json = gson.toJson(reminderModels);
                sharedPreferencesManager.setReminderDetails(json);
            }
        } else {
            Gson gson = new Gson();
            String json = gson.toJson(reminderIDs);
            sharedPreferencesManager.setReminderIDs(json);

            gson = new Gson();
            json = gson.toJson(reminderModels);
            sharedPreferencesManager.setReminderDetails(json);
        }
    }

    public void createAlarm(int reminderID, long timeInMilliseconds) {
        AlarmManager alarmManager = (AlarmManager) getSystemService( ALARM_SERVICE ) ;
        Intent intent = new Intent(this, ApplicationLaunchActivity.MyBroadcastReceiver.class);
        intent.putExtra("reminderID", reminderID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), reminderID, intent, 0);

        long currentTime = System.currentTimeMillis();
        long timeDifference = timeInMilliseconds - currentTime;

        if (timeDifference > 0) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, currentTime + timeDifference, pendingIntent);
        }
    }

    public void deleteAlarm(int reminderID) {
        AlarmManager alarmManager = (AlarmManager) getSystemService( ALARM_SERVICE ) ;
        Intent intent = new Intent(this, ApplicationLaunchActivity.MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), reminderID, intent, 0);
        alarmManager.cancel(pendingIntent) ;
    }

    private ReminderModel getMinusOneDayInMilliSeconds(ReminderModel reminderModel) {
        ReminderModel newReminderModel = new ReminderModel(reminderModel);

        Calendar calendar = Calendar.getInstance();
        int year = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[0].split("/")[0]);
        int month = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[0].split("/")[1]) - 1;
        int day = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[0].split("/")[2]);
        int hourOfDay = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[1].split(":")[0]);
        int minute = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[1].split(":")[1]);
        int seconds = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[1].split(":")[2]);
        calendar.set(year, month, day, hourOfDay, minute, seconds);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        long milliseconds = calendar.getTimeInMillis();
        newReminderModel.setTimeInMilliseconds(milliseconds);
        newReminderModel.setTimeToEvent("1 day");
        newReminderModel.setScheduledDate(String.format("%s/%s/%s %s:%s:%s:000", String.valueOf(calendar.get(Calendar.YEAR)),
                Date.makeTwoDigits(String.valueOf(Month.MonthBase1(calendar.get(Calendar.MONTH)))), Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))),
                Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))), Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.MINUTE))),
                Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.SECOND)))));

        return newReminderModel;
    }

    private ReminderModel getMinusOneHourInMilliSeconds(ReminderModel reminderModel) {
        ReminderModel newReminderModel = new ReminderModel(reminderModel);

        Calendar calendar = Calendar.getInstance();
        int year = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[0].split("/")[0]);
        int month = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[0].split("/")[1]) - 1;
        int day = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[0].split("/")[2]);
        int hourOfDay = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[1].split(":")[0]);
        int minute = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[1].split(":")[1]);
        int seconds = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[1].split(":")[2]);
        calendar.set(year, month, day, hourOfDay, minute, seconds);
        calendar.add(Calendar.HOUR, -1);
        long milliseconds = calendar.getTimeInMillis();
        newReminderModel.setTimeInMilliseconds(milliseconds);
        newReminderModel.setTimeToEvent("1 hour");
        newReminderModel.setScheduledDate(String.format("%s/%s/%s %s:%s:%s:000", String.valueOf(calendar.get(Calendar.YEAR)),
                Date.makeTwoDigits(String.valueOf(Month.MonthBase1(calendar.get(Calendar.MONTH)))), Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))),
                Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))), Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.MINUTE))),
                Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.SECOND)))));

        return newReminderModel;
    }

    private ReminderModel getMinusTenMinutesInMilliSeconds(ReminderModel reminderModel) {
        ReminderModel newReminderModel = new ReminderModel(reminderModel);

        Calendar calendar = Calendar.getInstance();
        int year = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[0].split("/")[0]);
        int month = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[0].split("/")[1]) - 1;
        int day = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[0].split("/")[2]);
        int hourOfDay = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[1].split(":")[0]);
        int minute = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[1].split(":")[1]);
        int seconds = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[1].split(":")[2]);
        calendar.set(year, month, day, hourOfDay, minute, seconds);
        calendar.add(Calendar.MINUTE, -10);
        long milliseconds = calendar.getTimeInMillis();
        newReminderModel.setTimeInMilliseconds(milliseconds);
        newReminderModel.setTimeToEvent("10 minutes");
        newReminderModel.setScheduledDate(String.format("%s/%s/%s %s:%s:%s:000", String.valueOf(calendar.get(Calendar.YEAR)),
                Date.makeTwoDigits(String.valueOf(Month.MonthBase1(calendar.get(Calendar.MONTH)))), Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))),
                Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))), Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.MINUTE))),
                Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.SECOND)))));

        return newReminderModel;
    }

    private ReminderModel getTimeInMilliSeconds(ReminderModel reminderModel) {
        ReminderModel newReminderModel = new ReminderModel(reminderModel);

        Calendar calendar = Calendar.getInstance();
        int year = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[0].split("/")[0]);
        int month = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[0].split("/")[1]) - 1;
        int day = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[0].split("/")[2]);
        int hourOfDay = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[1].split(":")[0]);
        int minute = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[1].split(":")[1]);
        int seconds = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[1].split(":")[2]);
        calendar.set(year, month, day, hourOfDay, minute, seconds);
        long milliseconds = calendar.getTimeInMillis();
        newReminderModel.setTimeInMilliseconds(milliseconds);
        newReminderModel.setTimeToEvent("less than a minute");
        newReminderModel.setScheduledDate(String.format("%s/%s/%s %s:%s:%s:000", String.valueOf(calendar.get(Calendar.YEAR)),
                Date.makeTwoDigits(String.valueOf(Month.MonthBase1(calendar.get(Calendar.MONTH)))), Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))),
                Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))), Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.MINUTE))),
                Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.SECOND)))));

        return newReminderModel;
    }

    public static class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int reminderID = intent.getIntExtra("reminderID", -1);
            Gson gson = new Gson();
            SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
            String reminderDetailsJSON = sharedPreferencesManager.getReminderDetails();
            Type type = new TypeToken<HashMap<Integer, ReminderModel>>() {}.getType();
            HashMap<Integer, ReminderModel> reminderModels = gson.fromJson(reminderDetailsJSON, type);
            ReminderModel reminderModel = new ReminderModel();

            String reminderIDsJSON = sharedPreferencesManager.getReminderIDs();
            type = new TypeToken<ArrayList<Integer>>() {}.getType();
            ArrayList<Integer> reminderIDs = gson.fromJson(reminderIDsJSON, type);

            if (reminderModels != null) {
                if (reminderModels.size() > 0) {
                    if (reminderID >= 0) {
                        reminderModel = reminderModels.get(reminderID);
                    }
                }
            }

            Intent resultIntent;
            PendingIntent resultPendingIntent;
            Bundle bundle = new Bundle();
            Spanned message;
            String subText;
            String contentTitle;
            int notificationID;

            if (reminderModel != null) {
                if (!reminderModel.getActivityID().trim().equals("")) {
                    if (reminderModel.getReminderType().equals("Event")) {
                        resultIntent = new Intent(context, EventDetailActivity.class);
                        bundle.putString("Event ID", reminderModel.getActivityID());
                        bundle.putString("Color Number", String.valueOf(0));
                        bundle.putString("parentActivity", reminderModel.getAccountType());
                        resultIntent.putExtras(bundle);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addNextIntentWithParentStack(resultIntent);
                        resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                        message = Html.fromHtml(String.format(Locale.getDefault(), "Your event <strong>%s</strong> is scheduled " +
                                        "to hold in <strong>%s</strong>. Tap this notification to view.", reminderModel.getEventTitle(),
                                reminderModel.getTimeToEvent()));
                        subText = "Event Reminder";
                        contentTitle = "You have an upcoming event";
                        notificationID = 100;
                    } else if (reminderModel.getReminderType().equals("TeacherEMeeting")) {
                        resultIntent = new Intent(context, TeacherEMeetingMessageBoardActivity.class);
                        bundle.putString("Scheduled Meeting ID", reminderModel.getActivityID());
                        bundle.putString("Scheduled Meeting Link", reminderModel.getMeetingLink());
                        bundle.putString("Scheduled Meeting State", reminderModel.geteClassroomState());
                        bundle.putString("Scheduled Meeting Scheduled Date", reminderModel.getOriginalScheduledDate());
                        bundle.putString("parentActivity", reminderModel.getAccountType());
                        resultIntent.putExtras(bundle);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addNextIntentWithParentStack(resultIntent);
                        resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                        message = Html.fromHtml(String.format(Locale.getDefault(), "Your meeting <strong>%s</strong> with <strong>%s</strong> is scheduled " +
                                        "to hold in <strong>%s</strong>. Tap this notification to view.", reminderModel.getMeetingTitle(), reminderModel.getMeetingSchoolName(),
                                reminderModel.getTimeToEvent()));
                        subText = "Meeting Reminder";
                        contentTitle = "You have an upcoming meeting";
                        notificationID = 101;
                    } else if (reminderModel.getReminderType().equals("ParentEMeeting")) {
                        resultIntent = new Intent(context, ParentEMeetingMessageBoardActivity.class);
                        bundle.putString("Scheduled Meeting ID", reminderModel.getActivityID());
                        bundle.putString("Scheduled Meeting Link", reminderModel.getMeetingLink());
                        bundle.putString("Scheduled Meeting State", reminderModel.geteClassroomState());
                        bundle.putString("Scheduled Meeting Scheduled Date", reminderModel.getOriginalScheduledDate());
                        bundle.putString("parentActivity", reminderModel.getAccountType());
                        resultIntent.putExtras(bundle);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addNextIntentWithParentStack(resultIntent);
                        resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                        message = Html.fromHtml(String.format(Locale.getDefault(), "Your meeting <strong>%s</strong> with <strong>%s</strong> is scheduled " +
                                        "to hold in <strong>%s</strong>. Tap this notification to view.", reminderModel.getMeetingTitle(), reminderModel.getMeetingSchoolName(),
                                reminderModel.getTimeToEvent()));
                        subText = "Meeting Reminder";
                        contentTitle = "You have an upcoming meeting";
                        notificationID = 102;
                    } else if (reminderModel.getReminderType().equals("ELibraryAssignment")) {
                        Student student = new Student(reminderModel.geteClassroomChildName(), reminderModel.geteClassroomChildID(),
                                reminderModel.geteClassroomChildProfilePictureURL());
                        gson = new Gson();
                        String activeKid = gson.toJson(student);

                        resultIntent = new Intent(context, ELibraryParentAssignmentActivity.class);
                        bundle.putString("materialId", reminderModel.getAssignmentMaterialID());
                        bundle.putString("assignmentID", reminderModel.getAssignmentID());
                        bundle.putString("Child ID", activeKid);
                        bundle.putString("parentActivity", reminderModel.getAccountType());
                        resultIntent.putExtras(bundle);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addNextIntentWithParentStack(resultIntent);
                        resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                        message = Html.fromHtml(String.format(Locale.getDefault(), "<strong>%s</strong>'s eLibrary assignment on <strong>%s</strong> is due " +
                                        "in <strong>%s</strong>. Tap this notification to view.", reminderModel.geteClassroomChildName(), reminderModel.getAssignmentTitle(),
                                reminderModel.getTimeToEvent()));
                        subText = "E-Library Assignment Reminder";
                        contentTitle = reminderModel.geteClassroomChildName() + "'s eLibrary assignment is due soon";
                        notificationID = 103;
                    } else {
                        Student student = new Student(reminderModel.geteClassroomChildName(), reminderModel.geteClassroomChildID(),
                                reminderModel.geteClassroomChildProfilePictureURL());
                        gson = new Gson();
                        String activeKid = gson.toJson(student);

                        resultIntent = new Intent(context, ParentEClassroomMessageBoardActivity.class);
                        bundle.putString("Child ID", activeKid);
                        bundle.putString("Scheduled Class ID", reminderModel.getActivityID());
                        bundle.putString("Scheduled Class Link", reminderModel.geteClassroomLink());
                        bundle.putString("Scheduled Class State", reminderModel.geteClassroomState());
                        bundle.putString("Scheduled Class Scheduled Date", reminderModel.getOriginalScheduledDate());
                        bundle.putString("parentActivity", reminderModel.getAccountType());
                        resultIntent.putExtras(bundle);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addNextIntentWithParentStack(resultIntent);
                        resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                        message = Html.fromHtml(String.format(Locale.getDefault(), "<strong>%s</strong> has a remote class scheduled " +
                                        "to hold in <strong>%s</strong>. Tap this notification to view.", reminderModel.geteClassroomChildName(),
                                reminderModel.getTimeToEvent()));
                        subText = "Scheduled E-Classroom Reminder";
                        contentTitle = reminderModel.geteClassroomChildName() + " has a remote class coming up soon";
                        notificationID = 104;
                    }

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Reminder");
                    builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                            .setColor(ContextCompat.getColor(context, R.color.colorSecondaryPurple))
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setSubText(subText)
                            .setContentTitle(contentTitle)
                            .setContentText(message)
                            .setAutoCancel(true)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                            .setOnlyAlertOnce(true)
                            .setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        builder.setChannelId("com.celerii.celerii");
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(
                                "com.celerii.celerii",
                                "Celerii",
                                NotificationManager.IMPORTANCE_DEFAULT);

                        if (mNotificationManager != null) {
                            mNotificationManager.createNotificationChannel(channel);
                        }
                    }

                    try {
                        mNotificationManager.notify(notificationID, builder.build());
                    } catch (NullPointerException e) {
                        return;
                    }

                    try {
                        reminderIDs.remove(reminderID);
                        reminderModels.remove(reminderID);

                        gson = new Gson();
                        String json = gson.toJson(reminderIDs);
                        sharedPreferencesManager.setReminderIDs(json);

                        gson = new Gson();
                        json = gson.toJson(reminderModels);
                        sharedPreferencesManager.setReminderDetails(json);
                    } catch (NullPointerException e) {
                        return;
                    }
                }
            }
        }
    }
}
