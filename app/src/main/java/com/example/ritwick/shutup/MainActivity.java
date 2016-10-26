package com.example.ritwick.shutup;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    Switch masterswitch;
    SharedPreferences preferences;
    Spinner singlelist,doublelist;
    TextView singlewave,doublewave,info,infoproximity;
    ArrayAdapter<CharSequence> adapter;
    BroadcastReceiver phonestaterecevier;
    NotificationManager notificationManager;

    boolean checktel=false,checkdnd=false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        masterswitch=(Switch) findViewById(R.id.masterswitch);
        singlelist=(Spinner) findViewById(R.id.singlelist);
        doublelist=(Spinner) findViewById(R.id.doublelist);
        singlewave=(TextView) findViewById(R.id.singlewave);
        doublewave=(TextView) findViewById(R.id.doublewave);
        info=(TextView) findViewById(R.id.info);
        infoproximity=(TextView) findViewById(R.id.infoproximity);

        adapter = ArrayAdapter.createFromResource(this, R.array.listoptions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        singlelist.setAdapter(adapter);
        doublelist.setAdapter(adapter);

        singlelist.setVisibility(View.INVISIBLE);
        doublelist.setVisibility(View.INVISIBLE);
        singlewave.setVisibility(View.INVISIBLE);
        doublewave.setVisibility(View.INVISIBLE);
        singlelist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                preferences = getSharedPreferences("switchstatepref", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("singlewaveselection",parent.getItemAtPosition(position).toString());
                editor.apply();

                if(parent.getItemAtPosition(position).toString().equals("End call"))
                {
                    doublelist.setSelection(adapter.getPosition("Do nothing"));
                    doublelist.setVisibility(View.INVISIBLE);
                    doublewave.setVisibility(View.INVISIBLE);
                }
                else
                {
                    if(masterswitch.isChecked())
                    {
                        doublelist.setVisibility(View.VISIBLE);
                        doublewave.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Toast.makeText(MainActivity.this,"Choice not changed",Toast.LENGTH_SHORT).show();
            }
        });

        doublelist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {

                preferences = getSharedPreferences("switchstatepref", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("doublewaveselection",parent.getItemAtPosition(position).toString());
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Toast.makeText(MainActivity.this,"Choice not changed",Toast.LENGTH_SHORT).show();
            }
        });

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
        singlelist.setSelection(adapter.getPosition(preferences.getString("singlewaveselection","")));
        doublelist.setSelection(adapter.getPosition(preferences.getString("doublewaveselection","")));
        checkmainswitchforinfoappear();
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

            checkmainswitchforinfoappear();
            if (masterswitch.isChecked())
            {
                getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED , PackageManager.DONT_KILL_APP);
            }
            else
            {
                getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED , PackageManager.DONT_KILL_APP);
            }
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted()
                || ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED)
        {
            masterswitch.toggle();
        }
    }

    public boolean permissionmanage()
    {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
        return checktel && (checkdnd || (Build.VERSION.SDK_INT < 23));
    }

    public void checkmainswitchforinfoappear()
    {
        if(masterswitch.isChecked())
        {
            info.setVisibility(View.GONE);
            infoproximity.setVisibility(View.GONE);
            singlelist.setVisibility(View.VISIBLE);
            doublelist.setVisibility(View.VISIBLE);
            singlewave.setVisibility(View.VISIBLE);
            doublewave.setVisibility(View.VISIBLE);
        }
        else
        {
            info.setVisibility(View.VISIBLE);
            infoproximity.setVisibility(View.VISIBLE);
            singlelist.setVisibility(View.INVISIBLE);
            doublelist.setVisibility(View.INVISIBLE);
            singlewave.setVisibility(View.INVISIBLE);
            doublewave.setVisibility(View.INVISIBLE);
        }
    }

}
