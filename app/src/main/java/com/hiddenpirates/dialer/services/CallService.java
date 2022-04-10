package com.hiddenpirates.dialer.services;

import android.content.Intent;
import android.telecom.Call;
import android.telecom.InCallService;
import android.util.Log;
import android.widget.Toast;

import com.hiddenpirates.dialer.activities.CallActivity;
import com.hiddenpirates.dialer.activities.MainActivity;
import com.hiddenpirates.dialer.helpers.CallListHelper;
import com.hiddenpirates.dialer.helpers.CallManager;

public class CallService extends InCallService {

    int call_state;

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);

        CallListHelper.callList.add(call);
        CallManager.inCallService = this;
        CallManager.NUMBER_OF_CALLS = CallManager.NUMBER_OF_CALLS + 1;
        call.registerCallback(CallManager.callback);


        call_state = call.getDetails().getState();

        CallManager.HP_CALL_STATE = call_state;

        if (call_state == Call.STATE_RINGING){

            Intent intent = new Intent(this, CallActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            Toast.makeText(this, "Incoming call from " + call.getDetails().getHandle().getSchemeSpecificPart(), Toast.LENGTH_SHORT).show();
        }
        else if (call_state == Call.STATE_CONNECTING || call_state == Call.STATE_DIALING){

            Intent intent = new Intent(this, CallActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            Toast.makeText(this, "Dialing to " + call.getDetails().getHandle().getSchemeSpecificPart(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCallRemoved(Call call) {
        Toast.makeText(this, "Call ended "+call.getDetails().getHandle().getSchemeSpecificPart(), Toast.LENGTH_SHORT).show();
        Log.d(MainActivity.TAG, "onCallRemoved: " + call.getDetails().getDisconnectCause().getReason());
    }
}
