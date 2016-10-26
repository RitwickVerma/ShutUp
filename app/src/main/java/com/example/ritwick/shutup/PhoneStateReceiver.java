package com.example.ritwick.shutup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

/**
 * Created by ritwick on 10/7/16.
 */

public class PhoneStateReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        //Toast.makeText(context, "Broadcast Received", Toast.LENGTH_SHORT).show();
        Intent service=new Intent(context,ListenerService.class);

        if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING))
        {
            context.startService(service);
        }
        else
        {
            context.stopService(service);
        }

    }

}