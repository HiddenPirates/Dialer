package com.hiddenpirates.dialer.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hiddenpirates.dialer.R;
import com.hiddenpirates.dialer.helpers.CallManager;
import com.hiddenpirates.dialer.helpers.Constant;
import com.hiddenpirates.dialer.helpers.NotificationHelper;

public class CallActivity extends AppCompatActivity {

    FloatingActionButton endCallBtn;

    @SuppressLint("StaticFieldLeak")
    public static Button muteBtn, keypadBtn, speakerBtn, holdBtn, recordBtn, addCallBtn;

    Button callAnswerBtn, callRejectBtn;

    @SuppressLint("StaticFieldLeak")
    public static TextView callerNameTV, callerPhoneNumberTV, callDurationTV, callingStatusTV;

    @SuppressLint("StaticFieldLeak")
    public static TextView incomingCallerPhoneNumberTV, incomingCallerNameTV, ringingStatusTV;

    RelativeLayout inProgressCallRLView, incomingRLView;

    public static boolean isMuted, isSpeakerOn, isCallOnHold, isRecordingCall, isKeypadShown;

    public static String PHONE_NUMBER, CALLER_NAME;

    public  static String muteBtnName = "Mute", speakerBtnName = "Speaker On";

    @SuppressLint({"UseCompatTextViewDrawableApis", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        initializeValues();
        addLockScreenFlags();
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
                        NotificationHelper.createIngoingCallNotification(CallActivity.this, CALLER_NAME, PHONE_NUMBER, "01:12:00", speakerBtnName, muteBtnName);
                    }
//                    ------------------------------------------------------------------------------

                    if (!isMuted){
                        muteBtn.setEnabled(true);
                        muteBtn.setClickable(true);
                        muteBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.my_theme)));
                        muteBtn.setTextColor(getColor(R.color.my_theme));
                        muteBtn.setText("Mute");
                        NotificationHelper.createIngoingCallNotification(CallActivity.this, CALLER_NAME, PHONE_NUMBER, "01:12:00", speakerBtnName, muteBtnName);
                    }
                    else{
                        muteBtn.setEnabled(true);
                        muteBtn.setClickable(true);
                        muteBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.feature_on_color)));
                        muteBtn.setTextColor(getColor(R.color.feature_on_color));
                        muteBtn.setText("Unmute");
                        NotificationHelper.createIngoingCallNotification(CallActivity.this, CALLER_NAME, PHONE_NUMBER, "01:12:00", speakerBtnName, muteBtnName);
                    }


                    holdBtn.setEnabled(true);
                    holdBtn.setClickable(true);
                    holdBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.my_theme)));
                    holdBtn.setTextColor(getColor(R.color.my_theme));

                    if (!isRecordingCall){
                        recordBtn.setEnabled(true);
                        recordBtn.setClickable(true);
                        recordBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.my_theme)));
                        recordBtn.setTextColor(getColor(R.color.my_theme));
                    }

                    addCallBtn.setEnabled(true);
                    addCallBtn.setClickable(true);
                    addCallBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.my_theme)));
                    addCallBtn.setTextColor(getColor(R.color.my_theme));

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

        callRejectBtn.setOnClickListener(v -> CallManager.hangUpCall());
        callAnswerBtn.setOnClickListener(v -> CallManager.answerCall());
//        ______________________________________________________________________________

        Intent intent = getIntent();

        Bundle intentExtras = intent.getExtras();

/**       for (String k: intentExtras.keySet()) {
            Log.d(MainActivity.TAG, "intent: " + k);
          }
 **/


        if (intentExtras.containsKey("phoneNumber"))
            PHONE_NUMBER = intent.getStringExtra("phoneNumber");
        else
            PHONE_NUMBER = "Hidden Number";

        if (intentExtras.containsKey("callerName")) {
            CALLER_NAME = intent.getStringExtra("callerName");
        }
        else {
            CALLER_NAME = "Private Caller";
        }

        if (intentExtras.containsKey("callState")){

            switch (intent.getStringExtra("callState")) {
                case Constant.HP_CALL_STATE_OUTGOING:
                    inProgressCallRLView.setVisibility(View.VISIBLE);
                    incomingRLView.setVisibility(View.GONE);
                    break;
                case Constant.HP_CALL_STATE_INCOMING:
                    inProgressCallRLView.setVisibility(View.GONE);
                    incomingRLView.setVisibility(View.VISIBLE);
                    break;
                case Constant.HP_CALL_STATE_INGOING_CALL:
                    Intent broadCastIntent = new Intent("call_answered");
                    sendBroadcast(broadCastIntent);
                    break;
            }

            Log.d(MainActivity.TAG, "onCreate: " + CallManager.HP_CALL_STATE);
        }

        callerPhoneNumberTV.setText(PHONE_NUMBER);
        callerNameTV.setText(CALLER_NAME);
        incomingCallerPhoneNumberTV.setText(PHONE_NUMBER);
        incomingCallerNameTV.setText(CALLER_NAME);
//        ______________________________________________________________________________

        endCallBtn.setOnClickListener(v -> CallManager.hangUpCall());

        holdBtn.setOnClickListener(v -> {
            if (isCallOnHold){
                CallManager.unholdCall();
                holdBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.my_theme)));
                holdBtn.setTextColor(getColor(R.color.my_theme));
                isCallOnHold = false;
            }
            else{
                CallManager.holdCall();
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

                NotificationHelper.createIngoingCallNotification(CallActivity.this, CALLER_NAME, PHONE_NUMBER, "01:12:00", speakerBtnName, "Mute");
            }
            else{
                CallManager.muteCall(true);
                muteBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.feature_on_color)));
                muteBtn.setTextColor(getColor(R.color.feature_on_color));
                muteBtn.setText("Unmute");
                isMuted = true;

                NotificationHelper.createIngoingCallNotification(CallActivity.this, CALLER_NAME, PHONE_NUMBER, "01:12:00", speakerBtnName, "Unmute");
            }
        });

        speakerBtn.setOnClickListener(v -> {
            if (isSpeakerOn){
                CallManager.speakerCall(false);
                speakerBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.my_theme)));
                speakerBtn.setTextColor(getColor(R.color.my_theme));
                isSpeakerOn = false;

                NotificationHelper.createIngoingCallNotification(CallActivity.this, CALLER_NAME, PHONE_NUMBER, "01:12:00", "Speaker On", muteBtnName);
            }
            else{
                CallManager.speakerCall(true);
                speakerBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.feature_on_color)));
                speakerBtn.setTextColor(getColor(R.color.feature_on_color));
                isSpeakerOn = true;

                NotificationHelper.createIngoingCallNotification(CallActivity.this, CALLER_NAME, PHONE_NUMBER, "01:12:00", "Speaker Off", muteBtnName);
            }
        });

        addCallBtn.setOnClickListener(v -> Toast.makeText(this, "This feature is not implemented yet!", Toast.LENGTH_SHORT).show());
    }


    private void initializeValues() {
        endCallBtn = findViewById(R.id.endCallBtn);

        muteBtn = findViewById(R.id.muteBtn);
        keypadBtn = findViewById(R.id.keyPadBtn);
        speakerBtn = findViewById(R.id.speakerBtn);
        holdBtn = findViewById(R.id.holdBtn);
        recordBtn = findViewById(R.id.recordBtn);
        addCallBtn = findViewById(R.id.addCallBtn);

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