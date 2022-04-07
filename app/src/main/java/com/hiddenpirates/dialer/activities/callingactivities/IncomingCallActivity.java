package com.hiddenpirates.dialer.activities.callingactivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.hiddenpirates.dialer.R;
import com.hiddenpirates.dialer.helpers.CallManager;

public class IncomingCallActivity extends AppCompatActivity {

    Button pickUpCallBtn, hangUpCallBtn;
    TextView phoneNumberTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);

        addLockScreenFlags();
        initializeVal();

//        ______________________________________________________________________________
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals("call_ended")) {
                    finishAndRemoveTask();
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("call_ended");
        filter.addAction("call_answered");

        registerReceiver(broadcastReceiver, filter);
//        ______________________________________________________________________________

        Intent intent = getIntent();

        String phoneNumber;

        if (intent != null) {
            phoneNumber = intent.getStringExtra("phoneNumber");
        }
        else {
            phoneNumber = "Hidden Number";
        }

        phoneNumberTV.setText(phoneNumber);

        pickUpCallBtn.setOnClickListener(v -> CallManager.answerCall());

        hangUpCallBtn.setOnClickListener(v -> CallManager.hangUpCall());
    }

    private void initializeVal() {
        pickUpCallBtn = findViewById(R.id.pickUpCall);
        hangUpCallBtn = findViewById(R.id.hangUpCall);
        phoneNumberTV = findViewById(R.id.phoneNumberTV);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}