package com.laughingstock.ritwick.shutup;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity
{
    Switch masterswitch;
    SharedPreferences preferences;
    TextView welcome1,welcome2;
    BroadcastReceiver phonestaterecevier;
    NotificationManager notificationManager;
    RelativeLayout fragmentcontainer;
    OptionsFragment settingsFragment;
    int versionCode=0;
    String versionName="";

    boolean checktel=false,checkdnd=false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        masterswitch=(Switch) findViewById(R.id.masterswitch);
        welcome1=(TextView) findViewById(R.id.welcome1);
        welcome2=(TextView) findViewById(R.id.welcome2);

        fragmentcontainer=(RelativeLayout) findViewById(R.id.fragmentcontainer);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        if (findViewById(R.id.fragmentcontainer) != null)
        {
            if(savedInstanceState == null)
            {
                settingsFragment = new OptionsFragment();
                getFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, settingsFragment,"settingsFragment").commit();
            }
            else
            {
                settingsFragment = (OptionsFragment) getFragmentManager().findFragmentByTag("settingsFragment");
            }
        }
        phonestaterecevier = new PhoneStateReceiver();

        masterswitch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(permissionmanage())
                {
                    ComponentName component = new ComponentName(getApplication(), PhoneStateReceiver.class);
                    preferences = getSharedPreferences("switchstatepref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("switchstate", masterswitch.isChecked());

                    getPackageManager().setComponentEnabledSetting(component,
                            masterswitch.isChecked()?PackageManager.COMPONENT_ENABLED_STATE_ENABLED:
                                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

                    editor.apply();

                    fragmentmanage();
                    masterswitch.setBackgroundColor(Color.parseColor((masterswitch.isChecked())?"#26A69A":"#EF5350"));

                }
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted()
                        || ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_CONTACTS)!=PackageManager.PERMISSION_GRANTED)
                {
                    masterswitch.toggle();
                }
            }
        });


    }

    protected void onPause()
    {
        super.onPause();
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
        masterswitch.setBackgroundColor(Color.parseColor(masterswitch.isChecked()?"#26A69A":"#EF5350"));
        fragmentmanage();

        SharedPreferences.Editor editor = preferences.edit();


        try
        {
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;

        }catch(PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }


        if(preferences.getInt("VC",0)!=versionCode)
        {
            if(versionCode-preferences.getInt("VC",0)==2)
            {
                String tempcontactphotojson = preferences.getString("blacklistcontactphotospref", null);

                Gson gson=new Gson();
                Type type = new TypeToken<ArrayList<String>>(){}.getType();

                ArrayList<String> contactphotos = new ArrayList<String>();

                if(tempcontactphotojson!=null)
                {
                    Random r=new Random();
                    contactphotos= gson.fromJson(tempcontactphotojson, type);
                    for(int i=0;i<contactphotos.size();i++)
                    {
                        if(contactphotos.get(i)==null)
                        {
                            String randimguri = "android.resource://"+getPackageName()+"/drawable/contactphoto"+(r.nextInt(5)+1);
                            contactphotos.set(i,randimguri);
                        }
                    }

                    String contactphotojson = gson.toJson(contactphotos);
                    editor.putString("blacklistcontactphotospref", contactphotojson).apply();
                }
            }
            else if(versionCode-preferences.getInt("VC",0)>2 && preferences.contains("switchstate"))
            {
                editor.remove("blacklistcontactnamespref").apply();
                editor.remove("blacklistcontactnumberspref").apply();
                editor.remove("blacklistcontactphotospref").apply();

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Update:");
                alertDialog.setMessage("Because of the addition of contact photos in blacklist, it has to be cleared. I apologize for discomfort.");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Okay",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            editor.putInt("VC", versionCode).apply();
        }
    }




    public boolean permissionmanage()
    {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CONTACTS}, 0);

        return checktel && (checkdnd || (Build.VERSION.SDK_INT < 23));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED && requestCode==0)
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.overflow_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getTitle().equals("Settings"))
        {
            if(masterswitch.isChecked())
            {
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(this,"Master switch is off",Toast.LENGTH_SHORT).show();
            }
        }
        else if(item.getTitle().equals("Help"))
        {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.help_dialog);
            dialog.setTitle("Instructions:");
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
            Button dialogButton = (Button) dialog.findViewById(R.id.helpclosebutton);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
        else if(item.getTitle().equals("Rate"))
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id="+getPackageName()));
            startActivity(intent);
            //Toast.makeText(this,"Thank you!",Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run()
                {
                    Toast.makeText(MainActivity.this,"Thank you!",Toast.LENGTH_SHORT).show();
                }
            }, 1000);
        }

        else if(item.getTitle().equals("About"))
        {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("About:");
            alertDialog.setMessage("Created by: Ritwick Verma\nbecause he likes coding (still learning) and is a college student and is mostly free as he doesn't study.\n\nApp version: v"+versionName);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Great!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,"Rate", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("market://details?id="+getPackageName()));
                            startActivity(intent);
                            //Toast.makeText(this,"Thank you!",Toast.LENGTH_SHORT).show();

                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run()
                                {
                                    Toast.makeText(MainActivity.this,"Thank you!",Toast.LENGTH_SHORT).show();
                                }
                            }, 1000);
                        }
                    });
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }


    public void fragmentmanage()
    {
        if(masterswitch.isChecked())
        {
            fragmentcontainer.setVisibility(View.VISIBLE);
            welcome2.setVisibility(View.INVISIBLE);
            welcome1.setVisibility(View.INVISIBLE);
        }
        else
        {
            fragmentcontainer.setVisibility(View.GONE);
            welcome2.setVisibility(View.VISIBLE);
            welcome1.setVisibility(View.VISIBLE);
        }

    }


    public void silentonpickcheckboxclicked(View v)
    {
        settingsFragment.silentonpickcheckboxclicked(v);
    }

    public void speakeroncheckboxclicked(View v)
    {
        settingsFragment.speakeroncheckboxclicked(v);
    }

}
