package com.laughingstock.ritwick.shutup;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    Switch masterswitch;
    SharedPreferences preferences;
    TextView info,infoproximity;
    BroadcastReceiver phonestaterecevier;
    NotificationManager notificationManager;
    RelativeLayout fragmentcontainer;
    SettingsFragment settingsFragment;

    boolean checktel=false,checkdnd=false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        masterswitch=(Switch) findViewById(R.id.masterswitch);
        info=(TextView) findViewById(R.id.info);
        infoproximity=(TextView) findViewById(R.id.infoproximity);

        fragmentcontainer=(RelativeLayout) findViewById(R.id.fragmentcontainer);

        if (findViewById(R.id.fragmentcontainer) != null)
        {
            if(savedInstanceState == null)
            {
                settingsFragment = new SettingsFragment();
                getFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, settingsFragment,"settingsFragment").commit();
            }
            else
            {
                settingsFragment = (SettingsFragment) getFragmentManager().findFragmentByTag("settingsFragment");
            }
        }
        phonestaterecevier = new PhoneStateReceiver();
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
        masterswitch.setChecked(preferences.getBoolean("switchstate",false));
        fragmentmanage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults[0]==PackageManager.PERMISSION_GRANTED && requestCode==0)
        {
            checktel=true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted())
            {

                Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
                Toast.makeText(this,"Won't misuse. Pinky promise.",Toast.LENGTH_SHORT).show();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && notificationManager.isNotificationPolicyAccessGranted() )
                checkdnd=true;
        }
    }


    public void MasterSwitchClicked(View v)
    {
        if(permissionmanage())
        {
            ComponentName component = new ComponentName(getApplication(), PhoneStateReceiver.class);
            preferences = getSharedPreferences("switchstatepref", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("switchstate", masterswitch.isChecked());
            editor.apply();

            if (masterswitch.isChecked())
                getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            else
                getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

            fragmentmanage();
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted()
                || ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED)
        {
            masterswitch.toggle();
        }
    }

    public void silentonpickcheckboxclicked(View v)
    {
        settingsFragment.silentonpickcheckboxclicked(v);
    }

    public boolean permissionmanage()
    {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
        return checktel && (checkdnd || (Build.VERSION.SDK_INT < 23));
    }

    public void fragmentmanage()
    {
        if(masterswitch.isChecked())
        {
            fragmentcontainer.setVisibility(View.VISIBLE);
            infoproximity.setVisibility(View.INVISIBLE);
            info.setVisibility(View.INVISIBLE);
        }
        else
        {
            fragmentcontainer.setVisibility(View.INVISIBLE);
            infoproximity.setVisibility(View.VISIBLE);
            info.setVisibility(View.VISIBLE);
        }

    }

}
