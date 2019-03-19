package com.laughingstock.ritwick.shutup.Activities;

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
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.laughingstock.ritwick.shutup.BroadcastReceivers.AutoStart;
import com.laughingstock.ritwick.shutup.BroadcastReceivers.PhoneStateReceiver;
import com.laughingstock.ritwick.shutup.BroadcastReceivers.SchedAlarmReciever;
import com.laughingstock.ritwick.shutup.Fragments.CCandAFragment;
import com.laughingstock.ritwick.shutup.Fragments.CSFragment;
import com.laughingstock.ritwick.shutup.MessageEvent;
import com.laughingstock.ritwick.shutup.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends IntroActivity implements NavigationView.OnNavigationItemSelectedListener
{
    SharedPreferences preferences;
    BroadcastReceiver phonestaterecevier;
    NotificationManager notificationManager;

    CCandAFragment cCandAFragment;
    CSFragment csFragment;
    int versionCode = 0;
    String versionName = "", CCandA = "com.laughingstock.ritwick.shutup.CCandA", CS = "com.laughingstock.ritwick.shutup.CS";
    ViewGroup v;

    boolean checktel = false, checkdnd = false;

    @BindView(R.id.masterswitch)
    Switch masterswitch;
    @BindView(R.id.fragmentcontainer)
    RelativeLayout fragmentcontainer;
    @BindView(R.id.welcome1)
    TextView welcome1;
    @BindView(R.id.welcome2)
    TextView welcome2;
    @BindView(R.id.navigation_view)
    NavigationView leftnav;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;


    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_drawer);
        v = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        ButterKnife.bind(this);

        preferences = getSharedPreferences("switchstatepref", MODE_PRIVATE);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (CCandA.equals(getIntent().getAction()))
        {
            cCandAFragment = new CCandAFragment();
            getFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, cCandAFragment, "settingsFragment").commit();

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("fragmenttoinflate", "Call control and automation");
            editor.apply();
        }

        if (CS.equals(getIntent().getAction()))
        {
            csFragment = new CSFragment();
            getFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, csFragment, "settingsFragment").commit();

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("fragmenttoinflate", "Call Scheduling");
            editor.apply();
        }

        if (findViewById(R.id.fragmentcontainer) != null)
        {
            if (savedInstanceState == null)
            {
                cCandAFragment = new CCandAFragment();
                csFragment = new CSFragment();
                getFragmentManager().beginTransaction().replace(R.id.fragmentcontainer,
                        (preferences.getString("fragmenttoinflate", "Call control and automation")
                                .equals("Call control and automation")) ? cCandAFragment : csFragment, "settingsFragment").commit();

            } else
            {
                if (preferences.getString("fragmenttoinflate", "Call control and automation").equals("Call control and automation"))
                    cCandAFragment = (CCandAFragment) getFragmentManager().findFragmentByTag("settingsFragment");
                else
                    csFragment = (CSFragment) getFragmentManager().findFragmentByTag("settingsFragment");
            }

            leftnav.getMenu().getItem((preferences.getString("fragmenttoinflate", "Call control and automation")
                    .equals("Call control and automation")) ? 0 : 1).setChecked(true);

        }

        phonestaterecevier = new PhoneStateReceiver();

        masterswitch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (permissionmanage())
                {
                    ComponentName componenta = new ComponentName(MainActivity.this, PhoneStateReceiver.class);
                    ComponentName componentb = new ComponentName(MainActivity.this, SchedAlarmReciever.class);
                    ComponentName componentc = new ComponentName(MainActivity.this, AutoStart.class);


                    preferences = getSharedPreferences("switchstatepref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("switchstate", masterswitch.isChecked());


                    PackageManager pm = MainActivity.this.getPackageManager();
                    pm.setComponentEnabledSetting(componenta,
                            masterswitch.isChecked() ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                    pm.setComponentEnabledSetting(componentb,
                            masterswitch.isChecked() ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                    pm.setComponentEnabledSetting(componentc,
                            masterswitch.isChecked() ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

                    editor.apply();

                    fragmentmanage();
                    masterswitch.setBackgroundColor(Color.parseColor((masterswitch.isChecked()) ? "#26A69A" : "#EF5350"));

                    if (masterswitch.isChecked())
                        setTitle(preferences.getString("fragmenttoinflate", "Call control and automation").equals("Call control and automation") ? "Automation" : "Scheduling");
                    else
                        setTitle("ShutUp!");

                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted()
                        || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
                {
                    masterswitch.toggle();
                }
            }
        });

        leftnav.setNavigationItemSelectedListener(this);
        View headerview = leftnav.getHeaderView(0);
        ImageView headerimage=headerview.findViewById(R.id.headerimage);
        headerimage.setOnClickListener((v)->startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://laughingstockcodes.wordpress.com/"))));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    protected void onPause()
    {
        super.onPause();
        preferences = getSharedPreferences("switchstatepref", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("switchstate", masterswitch.isChecked());
        editor.apply();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        preferences = getSharedPreferences("switchstatepref", MODE_PRIVATE);
        masterswitch.setChecked(preferences.getBoolean("switchstate", false));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted()
                || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            masterswitch.setChecked(false);
        }

        masterswitch.setBackgroundColor(Color.parseColor(masterswitch.isChecked() ? "#26A69A" : "#EF5350"));
        fragmentmanage();


        if (masterswitch.isChecked())
            setTitle(preferences.getString("fragmenttoinflate", "Call control and automation").equals("Call control and automation") ? "Automation" : "Scheduling");
        else
            setTitle("ShutUp!");

        SharedPreferences.Editor editor = preferences.edit();

        try
        {
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;

        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        if (preferences.getInt("VC", 0) != versionCode)
        {
            if (Build.MANUFACTURER.equals("Xiaomi"))
            {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(this);
                builder.setTitle("Xiaomi phone detected")
                        .setMessage("I noticed that you are using a Xiaomi phone. I most probably will not work properly because of the heavy modifications in the software used by Xiaomi. I am sorry for the trouble. Please don't rate me bad.")
                        .setPositiveButton("I understand", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        }

        editor.putInt("VC", versionCode).apply();
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event)
    {
        if (event.getMessage().equals("schedcallperformed"))
            ProcessPhoenix.triggerRebirth(this);
    }

    public boolean permissionmanage()
    {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS}, 0);

        return checktel && (checkdnd || (Build.VERSION.SDK_INT < 23));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && requestCode == 0)
        {
            checktel = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted())
            {

                Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
                Toast.makeText(this, "Won't misuse. Pinky promise.", Toast.LENGTH_SHORT).show();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && notificationManager.isNotificationPolicyAccessGranted())
                checkdnd = true;
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        if (item.getTitle().equals("Call control and automation") || item.getTitle().equals("Call Scheduling"))
        {
            if (findViewById(R.id.fragmentcontainer) != null)
            {
                cCandAFragment = new CCandAFragment();
                csFragment = new CSFragment();
                if (masterswitch.isChecked())
                {
                    preferences = getSharedPreferences("switchstatepref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("fragmenttoinflate", item.getTitle().toString());
                    editor.apply();

                    getFragmentManager().beginTransaction().replace(R.id.fragmentcontainer,
                            (item.getTitle().equals("Call control and automation")) ? cCandAFragment : csFragment, "settingsFragment").commit();

                    item.setChecked(true);

                    setTitle(item.getTitle().equals("Call control and automation") ? "Automation" : "Scheduling");
                } else
                    Toast.makeText(MainActivity.this, "Master Switch is off", Toast.LENGTH_SHORT).show();

            }
        } else if (item.getTitle().equals("Settings"))
        {
            if (masterswitch.isChecked())
            {
                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
            } else
            {
                Toast.makeText(MainActivity.this, "Master switch is off", Toast.LENGTH_SHORT).show();
            }
        }


        drawerLayout.closeDrawers();

        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.overflow, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getTitle().equals("Help"))
        {
            final Dialog dialog = new Dialog(MainActivity.this);
            if (preferences.getString("fragmenttoinflate", "Call control and automation").equals("Call control and automation"))
                dialog.setContentView(R.layout.help_dialog_ccanda);
            else
                dialog.setContentView(R.layout.help_dialog_cs);
            dialog.setTitle("Instructions:");
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            Button dialogButton = dialog.findViewById(R.id.helpclosebutton);
            dialogButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    dialog.dismiss();
                }
            });

            dialog.show();
        } else if (item.getTitle().equals("Rate"))
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + getPackageName()));
            startActivity(intent);
            //Toast.makeText(this,"Thank you!",Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable()
            {

                @Override
                public void run()
                {
                    Toast.makeText(MainActivity.this, "Thank you!", Toast.LENGTH_SHORT).show();
                }
            }, 1000);
        } else if (item.getTitle().equals("About"))
        {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("About:");
            alertDialog.setMessage("Created by: Ritwick Verma\nbecause he likes coding (still learning) and is a college student and is mostly free as he doesn't study.\n\nApp version: v" + versionName);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Donate!", (DialogInterface dialog, int which)->
                {
                    Intent intent =new Intent(MainActivity.this,Donatepage.class);
                    startActivity(intent);
                }
            );
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Rate", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                    startActivity(intent);

                    new Handler().postDelayed(new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            Toast.makeText(MainActivity.this, "Thank you!", Toast.LENGTH_SHORT).show();
                        }
                    }, 1000);
                }
            });
            alertDialog.show();
        }
        else if (item.getTitle().equals("Donate"))
        {
            startActivity(new Intent(MainActivity.this, Donatepage.class));
        }
        else if (item.getTitle().equals("Privacy Policy"))
        {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://laughingstockcodes.wordpress.com/shutup-privacy-policy/")));
        }
        else if(item.getTitle().equals("Contribute"))
        {
            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://github.com/RitwickVerma/ShutUp")));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        if (drawerLayout.isDrawerOpen(leftnav))
            drawerLayout.closeDrawers();
        else
            super.onBackPressed();
    }

    public void fragmentmanage()
    {
        if (masterswitch.isChecked())
        {
            fragmentcontainer.setVisibility(View.VISIBLE);
            welcome2.setVisibility(View.INVISIBLE);
            welcome1.setVisibility(View.INVISIBLE);
        } else
        {
            fragmentcontainer.setVisibility(View.GONE);
            welcome2.setVisibility(View.VISIBLE);
            welcome1.setVisibility(View.VISIBLE);
        }
    }


    public void silentonpickcheckboxclicked(View v)
    {
        cCandAFragment.silentonpickcheckboxclicked(v);
    }

    public void speakeroncheckboxclicked(View v)
    {
        cCandAFragment.speakeroncheckboxclicked(v);
    }

    public void blacklistwhitelisttextclicked(View v)
    {
        cCandAFragment.blacklistwhitelisttextclicked(v);
    }

    public void blacklistwhitelistswitchclicked(View v)
    {
        cCandAFragment.blacklistwhitelistswitchclicked(v);
    }

    public void addschedulebuttonclicked(View v)
    {
        csFragment.addschedulebuttonclicked(v);
    }
}


