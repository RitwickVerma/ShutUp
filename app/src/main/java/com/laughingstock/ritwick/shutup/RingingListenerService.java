package com.laughingstock.ritwick.shutup;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.lang.reflect.Method;

public class RingingListenerService extends Service implements SensorEventListener
{

    Context context;
    AudioManager mode;
    int firstwave=(int)System.currentTimeMillis(),secondwave,consecutivewavethreshold;
    int currringermode;
    int pocketthreshold,stationarythreshold;
    SharedPreferences preferences;
    SensorManager sensorManager;
    Sensor proximity,linearacceleration;
    double initialz;
    boolean silentstatus=true,wasflat=false;


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
        currringermode=mode.getRingerMode();

        consecutivewavethreshold=preferences.getInt("consecutivewavethresholdseekbarprogress",12);
        consecutivewavethreshold*=100;

        pocketthreshold =(int)System.currentTimeMillis();
        stationarythreshold=pocketthreshold;

        sensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximity=sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        linearacceleration=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(RingingListenerService.this, proximity, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(RingingListenerService.this, linearacceleration, SensorManager.SENSOR_DELAY_FASTEST);

        return super.onStartCommand(intent, START_FLAG_REDELIVERY, startId);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mode.setRingerMode(currringermode);
        sensorManager.unregisterListener(RingingListenerService.this);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        Sensor sensor=event.sensor;

        if(sensor.getType()==Sensor.TYPE_PROXIMITY)
        {
            boolean pocketcontrol = true;

            if (((int) (System.currentTimeMillis()) - pocketthreshold) < 500)
                pocketcontrol = false;

            if (event.values[0] >= 0 && event.values[0]<proximity.getMaximumRange() && pocketcontrol)
            {
                secondwave = (int) System.currentTimeMillis();
                if (secondwave - firstwave >= consecutivewavethreshold && !preferences.getString("singlewaveselection", "").equals("Do nothing"))
                {
                    if (preferences.getString("singlewaveselection", "").equals("Silent phone"))
                        silentphone();
                    else if (preferences.getString("singlewaveselection", "").equals("Answer call"))
                        answercall();
                    else if (preferences.getString("singlewaveselection", "").equals("End call"))
                        endcall();

                }
                if (secondwave - firstwave < consecutivewavethreshold && !preferences.getString("doublewaveselection", "").equals("Do nothing"))
                {
                    if (preferences.getString("doublewaveselection", "").equals("Silent phone"))
                        silentphone();
                    else if (preferences.getString("doublewaveselection", "").equals("Answer call"))
                        answercall();
                    else if (preferences.getString("doublewaveselection", "").equals("End call"))
                        endcall();
                }
                firstwave = secondwave;
            }
        }
        else if(sensor.getType()==Sensor.TYPE_ACCELEROMETER && preferences.getBoolean("silentonpickcheckboxstate",false))
        {
            if(Math.abs(event.values[0])<=3 && Math.abs(event.values[1])<=3 && Math.abs(event.values[2])>=9 && ((int) (System.currentTimeMillis()) -stationarythreshold) < 500)
            {
                wasflat = true;
                initialz=event.values[2];
            }

            if (wasflat && Math.abs(event.values[2]-initialz)>=3 && silentstatus && ((int) (System.currentTimeMillis()) -stationarythreshold) >= 500)
            {
                silentphone();
                silentstatus=false;
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {    }

    public void silentphone()
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

    public void endcall()
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void answercall()
    {
        Intent intent = new Intent(context, AnswerCall.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);

    }
}