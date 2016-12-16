package com.laughingstock.ritwick.shutup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class PhoneStateReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        //Toast.makeText(context, "Broadcast Received", Toast.LENGTH_SHORT).show();
        Intent ringinglistenerservice=new Intent(context,RingingListenerService.class);
        Intent offhooklistenerservice=new Intent(context,OffhookListenerService.class);

        offhooklistenerservice.putExtra("callnumber",intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER));

        if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING))
        {
            context.startService(ringinglistenerservice);
        }
        else if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
        {
            try
            {
                context.stopService(ringinglistenerservice);
            }catch (Exception e){

            }
                context.startService(offhooklistenerservice);
        }
        else if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE))
        {
            try{
                context.stopService(offhooklistenerservice);
            }catch (Exception e){

            }
            try{
                context.stopService(ringinglistenerservice);
            }catch (Exception e){

            }
        }

    }

}