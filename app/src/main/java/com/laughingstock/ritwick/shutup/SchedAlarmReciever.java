package com.laughingstock.ritwick.shutup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SchedAlarmReciever extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent serviceintent = new Intent(context, CallSchedulerService.class);
        serviceintent.putExtra("position",intent.getIntExtra("position",0));
        context.startService(serviceintent);
    }

    public void setAlarm(Context context,long timeinmills,int p, boolean calldaily)
    {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent("com.laughingstock.ritwick.shutup.START_SCHED_ALARM");
        i.putExtra("position",p);
        PendingIntent pi = PendingIntent.getBroadcast(context, p, i, PendingIntent.FLAG_UPDATE_CURRENT);

       // if(calldaily)
        //    am.setRepeating(AlarmManager.RTC_WAKEUP,timeinmills,(long)(86400000),pi);
        //else
            am.setExact(AlarmManager.RTC_WAKEUP,timeinmills,pi);

        System.out.println("Alarm set");
    }

    public void cancelAlarm(Context context,int p)
    {
        Intent intent = new Intent("com.laughingstock.ritwick.shutup.START_SCHED_ALARM");
        PendingIntent sender = PendingIntent.getBroadcast(context, p, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        System.out.println("Alarm clear");
    }
}
