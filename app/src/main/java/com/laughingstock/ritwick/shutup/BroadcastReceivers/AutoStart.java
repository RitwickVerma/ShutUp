package com.laughingstock.ritwick.shutup.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.laughingstock.ritwick.shutup.Fragments.CSFragment;

import java.util.ArrayList;

public class AutoStart extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(!intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))   return;
        ArrayList<Bundle> schedinfo;
        CSFragment csFragment=new CSFragment();
        schedinfo=csFragment.readFromInternalStorage(context);
        SchedAlarmReciever schedAlarmReciever=new SchedAlarmReciever();

        for(int j=0;j<schedinfo.size();j++)
        {
            Bundle b;
            b=schedinfo.get(j);
            long timeinmills = b.getLong("timeinmills");
            schedAlarmReciever.setAlarm(context,timeinmills,j);
        }
    }
}
