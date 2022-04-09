package com.hiddenpirates.dialer.services;

import android.content.Intent;
import android.os.Build;
import android.telecom.Call;
import android.telecom.InCallService;
import android.util.Log;
import android.widget.Toast;

import com.hiddenpirates.dialer.activities.CallActivity;
import com.hiddenpirates.dialer.activities.MainActivity;
import com.hiddenpirates.dialer.helpers.CallListHelper;
import com.hiddenpirates.dialer.helpers.CallManager;
import com.hiddenpirates.dialer.helpers.Constant;

public class CallService extends InCallService {

    int call_state;

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);

        CallListHelper.callList.add(call);
        CallManager.inCallService = this;
        CallManager.NUMBER_OF_CALLS = CallManager.NUMBER_OF_CALLS + 1;
        call.registerCallback(CallManager.callback);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            call_state = call.getDetails().getState();
        }
        else{
            call_state = call.getState();
        }

        if (call_state == Call.STATE_RINGING){

            Intent intent = new Intent(this, CallActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("callNumberPosition", CallManager.NUMBER_OF_CALLS);
            intent.putExtra("callState", Constant.HP_CALL_STATE_INCOMING);
            startActivity(intent);

            Toast.makeText(this, "Incoming call from " + call.getDetails().getHandle().getSchemeSpecificPart(), Toast.LENGTH_SHORT).show();
        }
        else if (call_state == Call.STATE_CONNECTING || call_state == Call.STATE_DIALING){

            Intent intent = new Intent(this, CallActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("callNumberPosition", CallManager.NUMBER_OF_CALLS);
            intent.putExtra("callState", Constant.HP_CALL_STATE_OUTGOING);
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
