package com.hiddenpirates.dialer.helpers;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.hiddenpirates.dialer.R;
import com.hiddenpirates.dialer.activities.callingactivities.CallActivity;
import com.hiddenpirates.dialer.activities.callingactivities.IncomingCallActivity;
import com.hiddenpirates.dialer.receivers.ActionReceiver;

public class NotificationHelper {

    public static int NOTIFICATION_ID = 834831;

    public static void createIncomingNotification(Context context, String callerName, String callerPhoneNumber) {
        String CHANNEL_ID = "Hidden_Pirates_Phone_App";


        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Incoming Call Notification", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        channel.setSound(ringtoneUri, new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build());

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);


        Intent incomingCallActivityIntent = new Intent(context, CallActivity.class);
        incomingCallActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        incomingCallActivityIntent.putExtra("phoneNumber", callerPhoneNumber);

        PendingIntent incomingCallActivityPendingIntent = PendingIntent.getActivity(context, 0, incomingCallActivityIntent, PendingIntent.FLAG_IMMUTABLE);


        Intent answerCallIntent = new Intent(context, ActionReceiver.class);
        answerCallIntent.putExtra("pickUpCall", "YES");
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pickUpCallYesPendingIntent = PendingIntent.getBroadcast(context, 1, answerCallIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent rejectCallIntent = new Intent(context, ActionReceiver.class);
        rejectCallIntent.putExtra("pickUpCall", "NO");
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pickUpCallNoPendingIntent = PendingIntent.getBroadcast(context, 2, rejectCallIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setOngoing(true);
        builder.setPriority(Notification.PRIORITY_DEFAULT);
        builder.setContentIntent(incomingCallActivityPendingIntent);
        builder.setFullScreenIntent(incomingCallActivityPendingIntent, true);
        builder.setSmallIcon(R.drawable.ic_call_green);
        builder.setContentTitle(callerName);
        builder.setContentText(callerPhoneNumber);
        builder.setCategory(Notification.CATEGORY_CALL);
        builder.setChannelId(CHANNEL_ID);
        builder.addAction(R.drawable.ic_call_green, "Answer", pickUpCallYesPendingIntent);
        builder.addAction(R.drawable.ic_call_end_red, "Reject", pickUpCallNoPendingIntent);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
