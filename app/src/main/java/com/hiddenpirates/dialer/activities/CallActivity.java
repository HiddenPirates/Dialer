package com.hiddenpirates.dialer.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hiddenpirates.dialer.R;
import com.hiddenpirates.dialer.helpers.CallListHelper;
import com.hiddenpirates.dialer.helpers.CallManager;
import com.hiddenpirates.dialer.helpers.ContactsHelper;
import com.hiddenpirates.dialer.helpers.NotificationHelper;

public class CallActivity extends AppCompatActivity {

    FloatingActionButton endCallBtn;

    @SuppressLint("StaticFieldLeak")
    public static Button muteBtn, keypadBtn, speakerBtn, holdBtn, recordBtn, addCallBtn, mergeCallBtn;

    Button callAnswerBtn, callRejectBtn;

    @SuppressLint("StaticFieldLeak")
    public static TextView callerNameTV, callerPhoneNumberTV, callDurationTV, callingStatusTV;

    @SuppressLint("StaticFieldLeak")
    public static TextView incomingCallerPhoneNumberTV, incomingCallerNameTV, ringingStatusTV;

    RelativeLayout inProgressCallRLView, incomingRLView;

    public static boolean isMuted, isSpeakerOn, isCallOnHold, isRecordingCall;

    public static String PHONE_NUMBER, CALLER_NAME;

    public  static String muteBtnName = "Mute", speakerBtnName = "Speaker On";

    @SuppressLint({"UseCompatTextViewDrawableApis", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        initializeValues();
        addLockScreenFlags();

        Log.d(MainActivity.TAG, "TOTAL_NUMBER_OF_CALLS: " + CallManager.NUMBER_OF_CALLS);
        Log.d(MainActivity.TAG, "TOTAL_NUMBER_OF_CALL_OBJECT: " + CallListHelper.callList.size());
//        ______________________________________________________________________________
        setButtonsDisabled();
//        ______________________________________________________________________________
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onReceive(Context arg0, Intent intent) {

                String action = intent.getAction();

                if (action.equals("call_ended")) {
                    finishAndRemoveTask();
                }
                else if (action.equals("call_answered")) {

                    inProgressCallRLView.setVisibility(View.VISIBLE);
                    incomingRLView.setVisibility(View.GONE);

                    if (CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS -1).getDetails().hasProperty(Call.Details.PROPERTY_CONFERENCE)){
                        PHONE_NUMBER = "Conference";
                        CALLER_NAME = "Conference";
                    }
                    else{
                        PHONE_NUMBER = CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS -1).getDetails().getHandle().getSchemeSpecificPart();
                        CALLER_NAME = ContactsHelper.getContactNameByPhoneNumber(PHONE_NUMBER, CallActivity.this);
                    }

                    callerPhoneNumberTV.setText(PHONE_NUMBER);
                    callerNameTV.setText(CALLER_NAME);

                    Log.d(MainActivity.TAG, PHONE_NUMBER + "  " + CALLER_NAME);
//                    ------------------------------------------------------------------------------

                    if (isMuted){
                        muteBtnName = "Unmute";
                    }
                    else {
                        muteBtnName = "Mute";
                    }

                    if (isSpeakerOn){
                        speakerBtnName = "Speaker Off";
                    }
                    else{
                        speakerBtnName = "Speaker On";
                    }
//                    ------------------------------------------------------------------------------
                    if (isSpeakerOn){
                        speakerBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.feature_on_color)));
                        speakerBtn.setTextColor(getColor(R.color.feature_on_color));
                        NotificationHelper.createIngoingCallNotification(CallActivity.this, CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS - 1), "01:12:00", speakerBtnName, muteBtnName);
                    }
//                    ------------------------------------------------------------------------------

                    if (!isMuted){
                        muteBtn.setEnabled(true);
                        muteBtn.setClickable(true);
                        muteBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.my_theme)));
                        muteBtn.setTextColor(getColor(R.color.my_theme));
                        muteBtn.setText("Mute");
                        NotificationHelper.createIngoingCallNotification(CallActivity.this, CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS - 1), "01:12:00", speakerBtnName, muteBtnName);
                    }
                    else{
                        muteBtn.setEnabled(true);
                        muteBtn.setClickable(true);
                        muteBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.feature_on_color)));
                        muteBtn.setTextColor(getColor(R.color.feature_on_color));
                        muteBtn.setText("Unmute");
                        NotificationHelper.createIngoingCallNotification(CallActivity.this, CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS - 1), "01:12:00", speakerBtnName, muteBtnName);
                    }



                    if (!isRecordingCall){
                        recordBtn.setEnabled(true);
                        recordBtn.setClickable(true);
                        recordBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.my_theme)));
                        recordBtn.setTextColor(getColor(R.color.my_theme));
                    }

                    callingStatusTV.setText("Call in progress...");
                    callingStatusTV.setTextColor(getColor(R.color.green));
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("call_ended");
        filter.addAction("call_answered");

        registerReceiver(broadcastReceiver, filter);
//        ______________________________________________________________________________

        callRejectBtn.setOnClickListener(v -> CallManager.hangUpCall(CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS - 1)));
        callAnswerBtn.setOnClickListener(v -> CallManager.answerCall(CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS - 1)));
//        ______________________________________________________________________________

        endCallBtn.setOnClickListener(v -> CallManager.hangUpCall(CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS - 1)));

        holdBtn.setOnClickListener(v -> {
            if (isCallOnHold){
                CallManager.unholdCall(CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS - 1));
                holdBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.my_theme)));
                holdBtn.setTextColor(getColor(R.color.my_theme));
                isCallOnHold = false;
            }
            else{
                CallManager.holdCall(CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS - 1));
                holdBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.feature_on_color)));
                holdBtn.setTextColor(getColor(R.color.feature_on_color));
                isCallOnHold = true;
            }
        });

        muteBtn.setOnClickListener(v -> {
            if (isMuted){
                CallManager.muteCall(false);
                muteBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.my_theme)));
                muteBtn.setTextColor(getColor(R.color.my_theme));
                muteBtn.setText("Mute");
                isMuted = false;

                NotificationHelper.createIngoingCallNotification(CallActivity.this, CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS - 1), "01:12:00", speakerBtnName, "Mute");
            }
            else{
                CallManager.muteCall(true);
                muteBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.feature_on_color)));
                muteBtn.setTextColor(getColor(R.color.feature_on_color));
                muteBtn.setText("Unmute");
                isMuted = true;

                NotificationHelper.createIngoingCallNotification(CallActivity.this, CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS - 1), "01:12:00", speakerBtnName, "Unmute");
            }
        });

        speakerBtn.setOnClickListener(v -> {
            if (isSpeakerOn){
                CallManager.speakerCall(false);
                speakerBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.my_theme)));
                speakerBtn.setTextColor(getColor(R.color.my_theme));
                isSpeakerOn = false;

                NotificationHelper.createIngoingCallNotification(CallActivity.this, CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS - 1), "01:12:00", "Speaker On", muteBtnName);
            }
            else{
                CallManager.speakerCall(true);
                speakerBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.feature_on_color)));
                speakerBtn.setTextColor(getColor(R.color.feature_on_color));
                isSpeakerOn = true;

                NotificationHelper.createIngoingCallNotification(CallActivity.this, CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS - 1), "01:12:00", "Speaker Off", muteBtnName);
            }
        });

        keypadBtn.setOnClickListener(v -> {

            BottomSheetDialog keypadDialog = new BottomSheetDialog(CallActivity.this);
            keypadDialog.setContentView(R.layout.in_progress_call_dialpad);
            keypadDialog.setCanceledOnTouchOutside(true);

            ImageButton keypadCancelBtn = keypadDialog.findViewById(R.id.keypadCancelBtn);
            assert keypadCancelBtn != null;
            keypadCancelBtn.setOnClickListener(v1 -> keypadDialog.cancel());

            FloatingActionButton endCallBottomSheet = keypadDialog.findViewById(R.id.endCallBtnBottomSheet);
            assert endCallBottomSheet != null;
            endCallBottomSheet.setOnClickListener(v1 -> CallManager.hangUpCall(CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS - 1)));

            keypadDialog.show();
        });

        addCallBtn.setOnClickListener(v -> startActivity(new Intent(this, DialerActivity.class)));

        mergeCallBtn.setOnClickListener(v -> {
            CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS - 2).conference(CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS - 1));
            CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS - 1).mergeConference();
        });
    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    @Override
    protected void onResume() {
        super.onResume();

        if (CallListHelper.callList.size() >= 2){

            mergeCallBtn.setVisibility(View.VISIBLE);

            holdBtn.setEnabled(false);
            holdBtn.setClickable(false);
            holdBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.light_grey)));
            holdBtn.setTextColor(getColor(R.color.light_grey));

            addCallBtn.setEnabled(false);
            addCallBtn.setClickable(false);
            addCallBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.light_grey)));
            addCallBtn.setTextColor(getColor(R.color.light_grey));
        }
        else{

            holdBtn.setEnabled(true);
            holdBtn.setClickable(true);

            if (CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS - 1).getDetails().getState() == Call.STATE_HOLDING){
                holdBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.feature_on_color)));
                holdBtn.setTextColor(getColor(R.color.feature_on_color));
            }
            else{
                holdBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.my_theme)));
                holdBtn.setTextColor(getColor(R.color.my_theme));
            }

            addCallBtn.setEnabled(true);
            addCallBtn.setClickable(true);
            addCallBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.my_theme)));
            addCallBtn.setTextColor(getColor(R.color.my_theme));
        }



        int call_state = CallManager.HP_CALL_STATE;

        if (call_state == Call.STATE_CONNECTING || call_state == Call.STATE_DIALING){

            inProgressCallRLView.setVisibility(View.VISIBLE);
            incomingRLView.setVisibility(View.GONE);

            if (CallManager.NUMBER_OF_CALLS > 0 && CallListHelper.callList.size() > 0){

                PHONE_NUMBER = CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS -1).getDetails().getHandle().getSchemeSpecificPart();
                CALLER_NAME = ContactsHelper.getContactNameByPhoneNumber(PHONE_NUMBER, this);

                callerPhoneNumberTV.setText(PHONE_NUMBER);
                callerNameTV.setText(CALLER_NAME);

                NotificationHelper.createOutgoingNotification(this, CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS - 1));
            }
        }
        else if (call_state == Call.STATE_ACTIVE || call_state == Call.STATE_HOLDING){

            if (CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS -1).getDetails().hasProperty(Call.Details.PROPERTY_CONFERENCE)){
                PHONE_NUMBER = "Conference";
                CALLER_NAME = "Conference";
            }
            else{
                PHONE_NUMBER = CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS -1).getDetails().getHandle().getSchemeSpecificPart();
                CALLER_NAME = ContactsHelper.getContactNameByPhoneNumber(PHONE_NUMBER, this);
            }

            Intent broadCastIntent = new Intent("call_answered");
            sendBroadcast(broadCastIntent);
        }
        else if (call_state == Call.STATE_RINGING){

            inProgressCallRLView.setVisibility(View.GONE);
            incomingRLView.setVisibility(View.VISIBLE);

            if (CallManager.NUMBER_OF_CALLS > 0 && CallListHelper.callList.size() > 0){

                PHONE_NUMBER = CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS -1).getDetails().getHandle().getSchemeSpecificPart();
                CALLER_NAME = ContactsHelper.getContactNameByPhoneNumber(PHONE_NUMBER, this);

                incomingCallerPhoneNumberTV.setText(PHONE_NUMBER);
                incomingCallerNameTV.setText(CALLER_NAME);

                NotificationHelper.createIncomingNotification(this, CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS - 1));
            }
        }
    }

    private void initializeValues() {
        endCallBtn = findViewById(R.id.endCallBtn);

        muteBtn = findViewById(R.id.muteBtn);
        keypadBtn = findViewById(R.id.keyPadBtn);
        speakerBtn = findViewById(R.id.speakerBtn);
        holdBtn = findViewById(R.id.holdBtn);
        recordBtn = findViewById(R.id.recordBtn);
        addCallBtn = findViewById(R.id.addCallBtn);
        mergeCallBtn = findViewById(R.id.mergeCallBtn);

        callAnswerBtn = findViewById(R.id.answerCallBtn);
        callRejectBtn = findViewById(R.id.rejectCallBtn);

        callerNameTV = findViewById(R.id.callerName);
        callerPhoneNumberTV = findViewById(R.id.callerPhoneNumber);
        callDurationTV = findViewById(R.id.callingDuration);
        callingStatusTV = findViewById(R.id.callingStatus);

        inProgressCallRLView = findViewById(R.id.inProgressCallRLView);
        incomingRLView = findViewById(R.id.incomingRLView);

        incomingCallerPhoneNumberTV = findViewById(R.id.incomingCallerPhoneNumberTV);
        incomingCallerNameTV = findViewById(R.id.incomingCallerNameTV);
        ringingStatusTV = findViewById(R.id.ringingStatus);
    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    private void setButtonsDisabled() {
        muteBtn.setEnabled(false);
        muteBtn.setClickable(false);
        muteBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.light_grey)));
        muteBtn.setTextColor(getColor(R.color.light_grey));

        holdBtn.setEnabled(false);
        holdBtn.setClickable(false);
        holdBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.light_grey)));
        holdBtn.setTextColor(getColor(R.color.light_grey));

        recordBtn.setEnabled(false);
        recordBtn.setClickable(false);
        recordBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.light_grey)));
        recordBtn.setTextColor(getColor(R.color.light_grey));

        addCallBtn.setEnabled(false);
        addCallBtn.setClickable(false);
        addCallBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.light_grey)));
        addCallBtn.setTextColor(getColor(R.color.light_grey));
    }

    private void addLockScreenFlags() {
        setShowWhenLocked(true);
        setTurnScreenOn(true);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        );
    }
}