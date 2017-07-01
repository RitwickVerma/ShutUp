package com.laughingstock.ritwick.shutup.Activities;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;


import java.io.IOException;

public class AnswerCall extends Activity {


    private static final String MANUFACTURER_HTC = "HTC",MANUFACTURER_OP = "ONEPLUS";

    private KeyguardManager keyguardManager;
    private AudioManager audioManager;
    private CallStateReceiver callStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerCallStateReceiver();
        updateWindowFlags();
        acceptCall();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (callStateReceiver != null) {
            unregisterReceiver(callStateReceiver);
            callStateReceiver = null;
        }
    }

    private void registerCallStateReceiver() {
        callStateReceiver = new CallStateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(callStateReceiver, intentFilter);
    }

    private void updateWindowFlags() {
        if (keyguardManager.inKeyguardRestrictedInputMode()) {
            getWindow().addFlags(6815744);
        } else {
            getWindow().clearFlags(4718720);
        }
    }

    private void acceptCall() {

        // for HTC devices we need to broadcast a connected headset
        boolean broadcastConnected = (MANUFACTURER_HTC.equalsIgnoreCase(Build.MANUFACTURER)
                || MANUFACTURER_OP.equalsIgnoreCase(Build.MANUFACTURER))
                &&!audioManager.isWiredHeadsetOn();

        if (broadcastConnected) {
            broadcastHeadsetConnected(false);
        }

        try {
            Runtime.getRuntime().exec("input keyevent " + Integer.toString(79));
        } catch (IOException e) {
            String str = "android.permission.CALL_PRIVILEGED";
            Intent putExtra = new Intent("android.intent.action.MEDIA_BUTTON").putExtra("android.intent.extra.KEY_EVENT", new KeyEvent(0, 79));
            Intent putExtra2 = new Intent("android.intent.action.MEDIA_BUTTON").putExtra("android.intent.extra.KEY_EVENT", new KeyEvent(1, 79));
            sendOrderedBroadcast(putExtra, str);
            sendOrderedBroadcast(putExtra2, str);
        } catch (Throwable th) {
            if (broadcastConnected) {
                broadcastHeadsetConnected(false);
            }
        }
        if (broadcastConnected) {
            broadcastHeadsetConnected(false);
        }
    }

    private void broadcastHeadsetConnected(boolean connected) {
        Intent i = new Intent(Intent.ACTION_HEADSET_PLUG);
        i.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        i.putExtra("state", connected ? 1 : 0);
        i.putExtra("name", "mysms");
        try {
            sendOrderedBroadcast(i, null);
        } catch (Exception e) {
        }
    }

    private class CallStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            finishAndRemoveTask();
        }
    }
}