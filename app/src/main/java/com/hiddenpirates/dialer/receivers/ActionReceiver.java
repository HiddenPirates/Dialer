package com.hiddenpirates.dialer.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hiddenpirates.dialer.activities.MainActivity;
import com.hiddenpirates.dialer.helpers.CallManager;

public class ActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getStringExtra("pickUpCall");
        Log.d(MainActivity.TAG, "onReceive: "+ action);

        if(action.equalsIgnoreCase("YES")){
            CallManager.answerCall();
        }
        else if(action.equalsIgnoreCase("NO")){
            CallManager.hangUpCall();
        }
    }
}
