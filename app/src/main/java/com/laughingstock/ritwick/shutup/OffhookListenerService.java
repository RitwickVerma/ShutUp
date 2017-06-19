package com.laughingstock.ritwick.shutup;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class OffhookListenerService extends Service implements SensorEventListener
{
    Context context;
    SensorManager sensorManager;
    Sensor proximity,accelerometer;
    AudioManager audioManager;
    SharedPreferences preferences;
    String callnumber;
    int currmode;
    boolean firstonear=false,iscovered,numberinlist=false,speakeron;

    @Override
    public IBinder onBind(Intent intent)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        context=getApplicationContext();

        try
        {
            callnumber = intent.getStringExtra("callnumber").replaceAll("[-() ]", "");
        }catch(Exception e)
        {
            e.printStackTrace();
            callnumber="1234567890";
        }

        preferences = getSharedPreferences("switchstatepref",Context.MODE_PRIVATE);
        sensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximity=sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        accelerometer=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(OffhookListenerService.this, proximity, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(OffhookListenerService.this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        audioManager=(AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        currmode=audioManager.getMode();
        speakeron=audioManager.isSpeakerphoneOn();

        numberinlist=checklistednumber();

        return START_REDELIVER_INTENT;
    }

    public void onDestroy()
    {
        super.onDestroy();
        audioManager.setMode(currmode);
        sensorManager.unregisterListener(OffhookListenerService.this);
    }


    @Override
    public void onSensorChanged(SensorEvent event)
    {
        Sensor sensor=event.sensor;
        if(sensor.getType()==Sensor.TYPE_PROXIMITY)
        {
            if (!(((preferences.getInt("blacklistwhitelistradiogrouppref",R.id.blacklistradiobutton)==R.id.blacklistradiobutton && numberinlist)
                    ||(preferences.getInt("blacklistwhitelistradiogrouppref",R.id.blacklistradiobutton)==R.id.whitelistradiobutton && !numberinlist))
                    && preferences.getBoolean("blacklistwhitelistswitchstate",false))
                    && preferences.getBoolean("speakeroncheckboxstate", false)
                    && !audioManager.isWiredHeadsetOn() && !audioManager.isBluetoothA2dpOn())
            {
                if (event.values[0] == proximity.getMaximumRange() && firstonear)
                {
                    audioManager.setMode(AudioManager.MODE_IN_CALL);
                    audioManager.setSpeakerphoneOn(true);
                }
                else
                {
                    audioManager.setMode(AudioManager.MODE_IN_CALL);
                    audioManager.setSpeakerphoneOn(false);
                    firstonear = true;
                }
            }

            iscovered=event.values[0]!=proximity.getMaximumRange();
        }

        else if(sensor.getType()==Sensor.TYPE_ACCELEROMETER)
        {
            if(iscovered && Math.abs(event.values[0])<=3 && Math.abs(event.values[1])<=3 && event.values[2]<=-9
                    && !audioManager.isWiredHeadsetOn() && !audioManager.isBluetoothA2dpOn())
            {
                if (preferences.getString("flipdownlistselection", "").equals("End call"))
                {
                    endcall();
                }
                else if (preferences.getString("flipdownlistselection", "").equals("Mute microphone"))
                {
                    audioManager.setMode(AudioManager.MODE_IN_CALL);
                    audioManager.setMicrophoneMute(true);
                }
            }
            else
            {
                new Thread(new Runnable()
                {
                    public void run()
                    {
                        audioManager.setMode(AudioManager.MODE_IN_CALL);
                        audioManager.setMicrophoneMute(false);
                    }
                }).start();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {    }

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


    public boolean checklistednumber()
    {

        String tempcontactnumberjson = preferences.getString("listcontactnumberspref",null);
        Gson gson=new Gson();
        Type type = new TypeToken<ArrayList<String>>(){}.getType();

        if(tempcontactnumberjson!=null)
        {
            ArrayList<String> contactnumbers= gson.fromJson(tempcontactnumberjson, type);

            for(int i=0;i<contactnumbers.size();i++)
            {
                if(contactnumbers.get(i).contains(callnumber))
                    return true;
            }
        }
        return false;

    }

}
