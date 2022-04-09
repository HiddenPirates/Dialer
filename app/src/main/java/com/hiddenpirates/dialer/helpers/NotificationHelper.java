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
import android.telecom.Call;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.hiddenpirates.dialer.R;
import com.hiddenpirates.dialer.activities.CallActivity;
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


        Intent incomingCallIntent = new Intent(context, CallActivity.class);
        incomingCallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        incomingCallIntent.putExtra("phoneNumber", callerPhoneNumber);
        incomingCallIntent.putExtra("callState", Constant.HP_CALL_STATE_INCOMING);
        incomingCallIntent.putExtra("callerName", callerName);

        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent incomingCallPendingIntent = PendingIntent.getActivity(context, 0, incomingCallIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent answerCallIntent = new Intent(context, ActionReceiver.class);
        answerCallIntent.putExtra("pickUpCall", "YES");
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pickUpCallYesPendingIntent = PendingIntent.getBroadcast(context, 1, answerCallIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent rejectCallIntent = new Intent(context, ActionReceiver.class);
        rejectCallIntent.putExtra("pickUpCall", "NO");
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pickUpCallNoPendingIntent = PendingIntent.getBroadcast(context, 2, rejectCallIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setOngoing(true);
        builder.setPriority(Notification.PRIORITY_DEFAULT);
        builder.setContentIntent(incomingCallPendingIntent);
        builder.setFullScreenIntent(incomingCallPendingIntent, true);
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

//    _____________________________________________________________________________________________________________
//    _____________________________________________________________________________________________________________

    public static void createIngoingCallNotification(Context context, Call call, String callDuration, String speakerBtnTxt, String muteBtnTxt) {

        String callerPhoneNumber = call.getDetails().getHandle().getSchemeSpecificPart();
        String callerName = ContactsHelper.getContactNameByPhoneNumber(callerPhoneNumber, context);
        String CHANNEL_ID = "Hidden_Pirates_Phone_App";

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Ingoing Call Notification", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        channel.setSound(null, null);

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);


        Intent ingoingCallIntent = new Intent(context, CallActivity.class);
        ingoingCallIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        ingoingCallIntent.putExtra("callState", Constant.HP_CALL_STATE_INGOING_CALL);
        ingoingCallIntent.putExtra("callNumberPosition", CallManager.NUMBER_OF_CALLS);
        ingoingCallIntent.putExtra("callDuration", callDuration);

        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent ingoingCallPendingIntent = PendingIntent.getActivity(context, 0, ingoingCallIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent endCallIntent = new Intent(context, ActionReceiver.class);
        endCallIntent.putExtra("endCall", "YES");
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent endCallPendingIntent = PendingIntent.getBroadcast(context, 1, endCallIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent speakerCallIntent = new Intent(context, ActionReceiver.class);
        speakerCallIntent.putExtra("speakerCall", "YES");
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent speakerCallPendingIntent = PendingIntent.getBroadcast(context, 2, speakerCallIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent muteCallIntent = new Intent(context, ActionReceiver.class);
        muteCallIntent.putExtra("muteCall", "YES");
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent muteCallPendingIntent = PendingIntent.getBroadcast(context, 3, muteCallIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setOngoing(true);
        builder.setPriority(Notification.PRIORITY_DEFAULT);
        builder.setContentIntent(ingoingCallPendingIntent);
        builder.setFullScreenIntent(ingoingCallPendingIntent, true);
        builder.setSmallIcon(R.drawable.ic_call_green);
        builder.setContentInfo(callDuration);
        builder.setOnlyAlertOnce(true);
        builder.setContentTitle(callerName);
        builder.setContentText(callerPhoneNumber);
        builder.setCategory(Notification.CATEGORY_CALL);
        builder.setChannelId(CHANNEL_ID);
        builder.addAction(R.drawable.ic_call_end_red, "End Call", endCallPendingIntent);
        builder.addAction(R.drawable.ic_volume_up, speakerBtnTxt, speakerCallPendingIntent);
        builder.addAction(R.drawable.ic_volume_up, muteBtnTxt, muteCallPendingIntent);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

//    _____________________________________________________________________________________________________________
//    _____________________________________________________________________________________________________________

    public static void createOutgoingNotification(Context context, String callerName, String callerPhoneNumber) {

        String CHANNEL_ID = "Hidden_Pirates_Phone_App";

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Outgoing Call Notification", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);


        Intent outingCallIntent = new Intent(context, CallActivity.class);
        outingCallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        outingCallIntent.putExtra("phoneNumber", callerPhoneNumber);
        outingCallIntent.putExtra("callState", Constant.HP_CALL_STATE_OUTGOING);
        outingCallIntent.putExtra("callerName", callerName);

        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent outgoingCallPendingIntent = PendingIntent.getActivity(context, 0, outingCallIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent cancelCallIntent = new Intent(context, ActionReceiver.class);
        cancelCallIntent.putExtra("cancelCall", "YES");
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pickUpCallYesPendingIntent = PendingIntent.getBroadcast(context, 1, cancelCallIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setOngoing(true);
        builder.setPriority(Notification.PRIORITY_DEFAULT);
        builder.setContentIntent(outgoingCallPendingIntent);
        builder.setFullScreenIntent(outgoingCallPendingIntent, true);
        builder.setSmallIcon(R.drawable.ic_call_green);
        builder.setContentTitle(callerName);
        builder.setContentText(callerPhoneNumber);
        builder.setCategory(Notification.CATEGORY_CALL);
        builder.setChannelId(CHANNEL_ID);
        builder.addAction(R.drawable.ic_call_end_red, "Cancel", pickUpCallYesPendingIntent);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
