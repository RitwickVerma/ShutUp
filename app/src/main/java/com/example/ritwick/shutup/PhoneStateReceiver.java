package com.example.ritwick.shutup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * Created by ritwick on 10/7/16.
 */

public class PhoneStateReceiver extends BroadcastReceiver
{
    SharedPreferences preferences;
    SensorManager sensorManager;
    Sensor proximity;

    @Override
    public void onReceive(final Context context, Intent intent)
    {
        preferences = context.getSharedPreferences("switchstatepref",Context.MODE_PRIVATE);
        final AudioManager mode = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        final int currmode=mode.getRingerMode();

        sensorManager=(SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        proximity=sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        final SensorEventListener proximitylistener=new SensorEventListener()
        {
            @Override
            public void onSensorChanged(SensorEvent event)
            {
                if(event.values[0]==0)
                {
                    //if(preferences.getBoolean("switchstate",false))
                    {
                        try
                        {
                            mode.setRingerMode(0);
                            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                            v.vibrate(1);
                            
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy)
            {

            }
        };

        final TelephonyManager telephony=(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        final PhoneStateListener phonelistener=new PhoneStateListener()
        {
            @Override
            public void onCallStateChanged(int state, String incomingNumber)
            {
                super.onCallStateChanged(state, incomingNumber);
                if(state==TelephonyManager.CALL_STATE_RINGING)
                    sensorManager.registerListener(proximitylistener, proximity, SensorManager.SENSOR_DELAY_NORMAL);
                else
                {
                    sensorManager.unregisterListener(proximitylistener, proximity);
                    mode.setRingerMode(currmode);
                    telephony.listen(this, PhoneStateListener.LISTEN_NONE);

                }

            }
        };

        telephony.listen(phonelistener, PhoneStateListener.LISTEN_CALL_STATE);
    }

}

