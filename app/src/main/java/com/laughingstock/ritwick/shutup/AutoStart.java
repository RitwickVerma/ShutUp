package com.laughingstock.ritwick.shutup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AutoStart extends BroadcastReceiver
{
    public AutoStart()
    {
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        SharedPreferences preferences = context.getSharedPreferences("switchstatepref",Context.MODE_PRIVATE);
        ArrayList<String> schedinfo=new ArrayList<>();
        Gson gson=new Gson();
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        if(preferences.getString("schedinfopref",null)!=null)
            schedinfo= gson.fromJson(preferences.getString("schedinfopref",null), type);


        for(int j=0;j<schedinfo.size();j++)
        {
            long datentime = Long.parseLong(schedinfo.get(j).split("!~o~!")[6]);
            AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent("com.laughingstock.ritwick.shutup.START_SCHED_ALARM");
            i.putExtra("position", j);
            PendingIntent pi = PendingIntent.getBroadcast(context, j, i, PendingIntent.FLAG_UPDATE_CURRENT);
            am.setExact(AlarmManager.RTC_WAKEUP, datentime, pi);
        }
    }
}
