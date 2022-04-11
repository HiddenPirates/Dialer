package com.hiddenpirates.dialer.helpers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.telecom.Call;
import android.telecom.CallAudioState;
import android.telecom.InCallService;
import android.telecom.VideoProfile;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import com.hiddenpirates.dialer.R;
import com.hiddenpirates.dialer.activities.CallActivity;
import com.hiddenpirates.dialer.activities.MainActivity;

public class CallManager {

    public static int NUMBER_OF_CALLS = 0;

    @SuppressLint("StaticFieldLeak")
    public static InCallService inCallService;

    public static int HP_CALL_STATE = 0;

    public static Call.Callback callback = new Call.Callback(){
        @SuppressLint("SetTextI18n")
        @Override
        public void onStateChanged(Call call, int newState) {

            Log.d(MainActivity.TAG, "onStateChanged: " + newState);

            HP_CALL_STATE = newState;

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(inCallService);

            if (newState == Call.STATE_ACTIVE){

                Intent broadCastIntent = new Intent("call_answered");
                inCallService.sendBroadcast(broadCastIntent);

                notificationManager.cancel(NotificationHelper.NOTIFICATION_ID);
                NotificationHelper.createIngoingCallNotification(inCallService, CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS - 1), "00:12:45", CallActivity.speakerBtnName, CallActivity.muteBtnName);
            }
            else if (newState == Call.STATE_DISCONNECTING){

                CallActivity.callingStatusTV.setText("Disconnected");
                CallActivity.callingStatusTV.setTextColor(inCallService.getColor(R.color.red));

                CallActivity.ringingStatusTV.setText("Rejected");
                CallActivity.ringingStatusTV.setTextColor(inCallService.getColor(R.color.red));
            }
            else if (newState == Call.STATE_DISCONNECTED){

                call.unregisterCallback(callback);

                Intent intent = new Intent("call_ended");
                inCallService.sendBroadcast(intent);

                CallActivity.isSpeakerOn = false;

                notificationManager.cancel(NotificationHelper.NOTIFICATION_ID);

                try {
                    Log.d(MainActivity.TAG, "TTT " + CallListHelper.callList.size());
                    CallListHelper.callList.remove(NUMBER_OF_CALLS - 1);
                }
                catch (Exception e){
                    e.printStackTrace();
                    Log.d(MainActivity.TAG, "Disconnect error: "+e.getMessage());
                }

                NUMBER_OF_CALLS = NUMBER_OF_CALLS - 1;

                CallActivity.isMuted = false;
                CallActivity.isSpeakerOn = false;

                Log.d(MainActivity.TAG, "Call disconnect event.");
            }
            else if (newState == Call.STATE_HOLDING){

                CallActivity.callingStatusTV.setText("Call on hold");
                CallActivity.callingStatusTV.setTextColor(inCallService.getColor(R.color.red));
            }
        }
    };

    public static void answerCall(Call mCall){
        mCall.answer(VideoProfile.STATE_AUDIO_ONLY);
    }

    public static void hangUpCall(Call mCall){
        mCall.disconnect();
    }

    public static void playDtmfTone(Call call, char c ){
        call.playDtmfTone(c);
        call.stopDtmfTone();
    }

    public static void holdCall(Call mCall){
        mCall.hold();
        Toast.makeText(inCallService, "Call on hold", Toast.LENGTH_SHORT).show();
    }

    public static void unholdCall(Call mCall){
        mCall.unhold();
        Toast.makeText(inCallService, "Call unhold", Toast.LENGTH_SHORT).show();
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
