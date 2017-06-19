package com.laughingstock.ritwick.shutup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class PhoneStateReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, final Intent intent)
    {
        Intent ringinglistenerservice=new Intent(context,RingingListenerService.class);
        Intent offhooklistenerservice=new Intent(context,OffhookListenerService.class);

        offhooklistenerservice.putExtra("callnumber",intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER));

        if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING))
        {
            context.startService(ringinglistenerservice);
        }
        else if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
        {
            context.stopService(ringinglistenerservice);
            context.startService(offhooklistenerservice);
        }
        else if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE))
        {

            context.stopService(offhooklistenerservice);
            context.stopService(ringinglistenerservice);
            //Toast.makeText(context,"Call ended",Toast.LENGTH_LONG).show();
        }

    }

}