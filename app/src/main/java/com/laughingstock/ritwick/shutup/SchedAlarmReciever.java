package com.laughingstock.ritwick.shutup;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.PowerManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class SchedAlarmReciever extends BroadcastReceiver
{
    public SchedAlarmReciever()
    {
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        SharedPreferences preferences = context.getSharedPreferences("switchstatepref",Context.MODE_PRIVATE);
        ArrayList<String> schedinfo=new ArrayList<>();
        int position=intent.getIntExtra("position",0);

        Gson gson=new Gson();
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        if(preferences.getString("schedinfopref",null)!=null)
            schedinfo= gson.fromJson(preferences.getString("schedinfopref",null), type);

        String[] parts=schedinfo.get(position).split("!~o~!");
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+parts[2]));
        try
        {
            context.startActivity(callIntent);
            Toast.makeText(context, "Calling " + parts[0], Toast.LENGTH_LONG).show(); // For example
        }
        catch (Exception e)
        {
            Toast.makeText(context,"Hi I am ShutUp. Permission required for scheduled call not given, hence, I can't call.", Toast.LENGTH_LONG).show();
        }

        wl.release();
    }

    public void setAlarm(Context context,long datentime,int p)
    {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent("com.laughingstock.ritwick.shutup.START_SCHED_ALARM");
        i.putExtra("position",p);
        PendingIntent pi = PendingIntent.getBroadcast(context, p, i, PendingIntent.FLAG_UPDATE_CURRENT);
        am.setExact(AlarmManager.RTC_WAKEUP,datentime,pi);
    }

    public void cancelAlarm(Context context,int p)
    {
        Intent intent = new Intent("com.laughingstock.ritwick.shutup.START_SCHED_ALARM");
        PendingIntent sender = PendingIntent.getBroadcast(context, p, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
