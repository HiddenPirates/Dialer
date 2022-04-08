package com.hiddenpirates.dialer.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.telecom.Call;
import android.telecom.CallAudioState;
import android.telecom.InCallService;
import android.telecom.VideoProfile;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import com.hiddenpirates.dialer.R;
import com.hiddenpirates.dialer.activities.MainActivity;
import com.hiddenpirates.dialer.activities.CallActivity;

public class CallManager {

    public static Call call;
    public Context context;
    @SuppressLint("StaticFieldLeak")
    public static InCallService inCallService;
    public static int HP_CALL_STATE;

    Call.Callback callback = new Call.Callback(){
        @SuppressLint("SetTextI18n")
        @Override
        public void onStateChanged(Call call, int newState) {

            Log.d(MainActivity.TAG, "onStateChanged: " + newState);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            HP_CALL_STATE = newState;

            if (newState == Call.STATE_ACTIVE){

                Intent broadCastIntent = new Intent("call_answered");
                context.sendBroadcast(broadCastIntent);

                notificationManager.cancel(NotificationHelper.NOTIFICATION_ID);
                NotificationHelper.createIngoingCallNotification(context, CallActivity.CALLER_NAME, CallActivity.PHONE_NUMBER, "00:12:45", CallActivity.speakerBtnName, CallActivity.muteBtnName);
            }
            else if (newState == Call.STATE_DISCONNECTED){

                call.unregisterCallback(callback);

                Intent intent = new Intent("call_ended");
                context.sendBroadcast(intent);

                CallActivity.isSpeakerOn = false;

                notificationManager.cancel(NotificationHelper.NOTIFICATION_ID);
            }
            else if (newState == Call.STATE_DISCONNECTING){

                CallActivity.callingStatusTV.setText("Disconnected");
                CallActivity.callingStatusTV.setTextColor(context.getColor(R.color.red));

                CallActivity.ringingStatusTV.setText("Rejected");
                CallActivity.ringingStatusTV.setTextColor(context.getColor(R.color.red));
            }
            else if (newState == Call.STATE_HOLDING){

                CallActivity.callingStatusTV.setText("Call on hold");
                CallActivity.callingStatusTV.setTextColor(context.getColor(R.color.red));
            }
        }
    };

    public void setValues(Call call, Context context) {
        CallManager.call = call;
        call.registerCallback(callback);

        this.context = context;
    }

    public static void answerCall(){
        call.answer(VideoProfile.STATE_AUDIO_ONLY);
    }

    public static void hangUpCall(){
        call.disconnect();
    }

    public static void holdCall(){
        call.hold();
        Toast.makeText(inCallService, "Call on hold", Toast.LENGTH_SHORT).show();
    }

    public static void unholdCall(){
        call.unhold();
        Toast.makeText(inCallService, "Call on unhold", Toast.LENGTH_SHORT).show();
    }

    public static void muteCall(boolean isMuted) {

        inCallService.setMuted(isMuted);

        if (isMuted){
            Toast.makeText(inCallService, "Call muted", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(inCallService, "Call unmuted", Toast.LENGTH_SHORT).show();
        }
    }

    public static void speakerCall(boolean isSpeakerOn) {

        if (isSpeakerOn){
            inCallService.setAudioRoute(CallAudioState.ROUTE_SPEAKER);
            Toast.makeText(inCallService, "Speaker on", Toast.LENGTH_SHORT).show();
        }
        else {
            inCallService.setAudioRoute(CallAudioState.ROUTE_EARPIECE);
            Toast.makeText(inCallService, "Speaker off", Toast.LENGTH_SHORT).show();
        }
    }
}
