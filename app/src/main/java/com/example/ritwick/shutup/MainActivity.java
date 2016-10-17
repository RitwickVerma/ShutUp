package com.example.ritwick.shutup;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    Switch masterswitch;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        masterswitch=(Switch) findViewById(R.id.masterswitch);


        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.READ_PHONE_STATE},0);

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        preferences = getSharedPreferences("switchstatepref",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("switchstate",masterswitch.isChecked());
        editor.apply();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        preferences = getSharedPreferences("switchstatepref",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("switchstate",masterswitch.isChecked());
        editor.apply();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        preferences = getSharedPreferences("switchstatepref",MODE_PRIVATE);
        if(masterswitch.isChecked()!=preferences.getBoolean("switchstate",false))
            masterswitch.toggle();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted() && masterswitch.isChecked())
        {
            masterswitch.toggle();
        }
    }


    public void MasterSwitchClicked(View v)
    {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted())
        {
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
            Toast.makeText(this,"Do not disturb access required for newer android versions :)",Toast.LENGTH_LONG).show();
        }
        else
        {
            preferences = getSharedPreferences("switchstatepref", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("switchstate", masterswitch.isChecked());
            editor.apply();
            if(masterswitch.isChecked())
            {
                PhoneStateReceiver r=new PhoneStateReceiver();
                registerReceiver(r,new IntentFilter("android.intent.action.PHONE_STATE"));
            }
            else
            {
                PhoneStateReceiver r = new PhoneStateReceiver();
                unregisterReceiver(r);
            }
        }
    }

}
