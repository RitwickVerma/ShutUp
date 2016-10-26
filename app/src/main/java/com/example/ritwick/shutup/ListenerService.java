package com.example.ritwick.shutup;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.lang.reflect.Method;

/**
 * Created by ritwick on 10/23/16.
 */

public class ListenerService extends Service implements SensorEventListener
{

    Context context;
    AudioManager mode;
    int firstwave=(int)System.currentTimeMillis(),secondwave;
    int currmode;
    int pocketthreshold =(int)System.currentTimeMillis();
    SharedPreferences preferences;
    SensorManager sensorManager;
    Sensor proximity;

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        context=getApplicationContext();
        preferences = getSharedPreferences("switchstatepref",Context.MODE_PRIVATE);
        mode = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        currmode=mode.getRingerMode();

        sensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximity=sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorManager.registerListener(ListenerService.this, proximity, SensorManager.SENSOR_DELAY_FASTEST);

        //return START_FLAG_REDELIVERY;
        return super.onStartCommand(intent, START_FLAG_REDELIVERY, startId);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mode.setRingerMode(currmode);
        sensorManager.unregisterListener(ListenerService.this);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        boolean pocketcontrol=true;
        if(((int)(System.currentTimeMillis())- pocketthreshold)< 500)
            pocketcontrol=false;

        if (event.values[0] == 0 && pocketcontrol)// && preferences.getBoolean("switchstate", false))
        {
            secondwave = (int) System.currentTimeMillis();
            if (secondwave - firstwave >= 1500 && !preferences.getString("singlewaveselection", "").equals("Do nothing"))
            {
                if (preferences.getString("singlewaveselection", "").equals("Silent phone"))
                    silentphone(context, mode);
                else if (preferences.getString("singlewaveselection", "").equals("End call"))
                    endcall(context);
            }
            if (secondwave - firstwave < 1500 && !preferences.getString("doublewaveselection", "").equals("Do nothing"))
            {
                if (preferences.getString("doublewaveselection", "").equals("Silent phone"))
                    silentphone(context, mode);
                else if (preferences.getString("doublewaveselection", "").equals("End call"))
                    endcall(context);
            }
            firstwave = secondwave;
        }

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {    }

    public void silentphone(Context context, AudioManager mode)
    {
        try
        {
            mode.setRingerMode(0);
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(1);
        }
        catch (Exception e)
        {
            Toast.makeText(context,"Sorry, Silent call not supported",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void endcall(Context context)
    {
        try
        {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Class c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            Object telephonyService = m.invoke(tm);
            c = Class.forName(telephonyService.getClass().getName());
            m = c.getDeclaredMethod("endCall");
            m.setAccessible(true);
            m.invoke(telephonyService);
        }
        catch (Exception e)
        {
            Toast.makeText(context,"Sorry, End call not supported",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}