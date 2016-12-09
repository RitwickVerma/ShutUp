package com.laughingstock.ritwick.shutup;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class OffhookListenerService extends Service implements SensorEventListener
{
    Context context;
    SensorManager sensorManager;
    Sensor proximity,accelerometer;
    AudioManager audioManager;
    SharedPreferences preferences;
    String callnumber,callname;
    int currmode;
    boolean firstonear=false,iscovered,numberblacklisted=false;

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        context=getApplicationContext();

        callnumber=intent.getStringExtra("callnumber");
        callname=getContactName(context,callnumber);

        preferences = getSharedPreferences("switchstatepref",Context.MODE_PRIVATE);
        sensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximity=sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        accelerometer=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(OffhookListenerService.this, proximity, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(OffhookListenerService.this,accelerometer,SensorManager.SENSOR_DELAY_FASTEST);
        audioManager=(AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        currmode=audioManager.getMode();

        numberblacklisted=checkblacklistednumber();

        return super.onStartCommand(intent, flags, startId);
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
            if ( !numberblacklisted && preferences.getBoolean("speakeroncheckboxstate", false) && !audioManager.isWiredHeadsetOn() && !audioManager.isBluetoothA2dpOn())
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

        else if(sensor.getType()==Sensor.TYPE_ACCELEROMETER && preferences.getBoolean("endonflipdowncheckboxstate", false))
        {
            if(iscovered && Math.abs(event.values[0])<=5 && Math.abs(event.values[1])<=5 && event.values[2]<=-7)
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
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                audioManager.setMicrophoneMute(false);
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


    public boolean checkblacklistednumber()
    {
        if(!preferences.getBoolean("blacklistswitchstate",false))
            return false;

        Set<String> tempcontactnameset = preferences.getStringSet("blacklistcontactnamespref", null);
        Set<String> tempcontactnumberset = preferences.getStringSet("blacklistcontactnumberspref", null);
        if(tempcontactnumberset!=null && tempcontactnameset!=null)
        {
            ArrayList<String> contactnames = new ArrayList<String>(tempcontactnameset);
            ArrayList<String> contactnumbers = new ArrayList<String>(tempcontactnumberset);

            for(int i=0;i<contactnumbers.size();i++)
            {
                if(contactnumbers.get(i).equals(callnumber) || contactnames.get(i).equals(callname))
                    return true;
            }
        }
        return false;

    }

    public static String getContactName(Context context, String phoneNumber)
    {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }
}
