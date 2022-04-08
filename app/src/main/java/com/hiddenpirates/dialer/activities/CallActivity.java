package com.hiddenpirates.dialer.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hiddenpirates.dialer.R;
import com.hiddenpirates.dialer.helpers.CallManager;

public class CallActivity extends AppCompatActivity {

    FloatingActionButton endCallBtn;
    Button muteBtn, keypadBtn, speakerBtn, holdBtn, recordBtn, addCallBtn;
    Button callAnswerBtn, callRejectBtn;

    @SuppressLint("StaticFieldLeak")
    public static TextView callerNameTV, callerPhoneNumberTV, callDurationTV, callingStatusTV;

    @SuppressLint("StaticFieldLeak")
    public static TextView incomingCallerPhoneNumberTV, incomingCallerNameTV;

    RelativeLayout inProgressCallRLView, incomingRLView;

    boolean isMuted, isSpeakerOn, isCallOnHold, isRecordingCall, isKeypadShown;

    @SuppressLint("UseCompatTextViewDrawableApis")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        initializeValues();
        addLockScreenFlags();
//        ______________________________________________________________________________
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

                    if (!isMuted){
                        muteBtn.setEnabled(true);
                        muteBtn.setClickable(true);
                        muteBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.my_theme)));
                        muteBtn.setTextColor(getColor(R.color.my_theme));
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

        String phoneNumber;

        if (intentExtras.containsKey("phoneNumber"))
            phoneNumber = intent.getStringExtra("phoneNumber");
        else
            phoneNumber = "Hidden Number";

        if (intentExtras.containsKey("dialing")){
            if (intent.getBooleanExtra("dialing", false)){
                inProgressCallRLView.setVisibility(View.VISIBLE);
                incomingRLView.setVisibility(View.GONE);
            }
        }

        callerPhoneNumberTV.setText(phoneNumber);
        incomingCallerPhoneNumberTV.setText(phoneNumber);
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
                isMuted = false;
            }
            else{
                CallManager.muteCall(true);
                muteBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.feature_on_color)));
                muteBtn.setTextColor(getColor(R.color.feature_on_color));
                isMuted = true;
            }
        });

        speakerBtn.setOnClickListener(v -> {
            if (isSpeakerOn){
                CallManager.speakerCall(false);
                speakerBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.my_theme)));
                speakerBtn.setTextColor(getColor(R.color.my_theme));
                isSpeakerOn = false;
            }
            else{
                CallManager.speakerCall(true);
                speakerBtn.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.feature_on_color)));
                speakerBtn.setTextColor(getColor(R.color.feature_on_color));
                isSpeakerOn = true;
            }
        });
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