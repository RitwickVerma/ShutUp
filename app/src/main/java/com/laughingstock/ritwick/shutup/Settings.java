package com.laughingstock.ritwick.shutup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends AppCompatActivity
{

    Switch blacklistswitch;
    SeekBar consecutivewavethreshold;
    TextView consecutivewavethresholdtext;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        blacklistswitch=(Switch) findViewById(R.id.blacklistswitch);
        consecutivewavethresholdtext=(TextView) findViewById(R.id.consecutivewavethresholdtext);
        consecutivewavethreshold=(SeekBar) findViewById(R.id.consecutivewavethresholdseekbar);


        consecutivewavethreshold.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                if(i<2)
                {
                    i=2;
                    consecutivewavethreshold.setProgress(2);
                }

                String temp="Consecutive wave threshold:  "+(float)i/10+" sec";
                consecutivewavethresholdtext.setText(temp);

                preferences = getSharedPreferences("switchstatepref",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("consecutivewavethresholdseekbarprogress",i);
                editor.apply();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {            }
        });
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        preferences = getSharedPreferences("switchstatepref",MODE_PRIVATE);
        blacklistswitch.setChecked(preferences.getBoolean("blacklistswitchstate",false));
        consecutivewavethreshold.setProgress(preferences.getInt("consecutivewavethresholdseekbarprogress",12));
    }


    public void blacklisttextclicked(View v)
    {
        if(blacklistswitch.isChecked())
        {
            Intent i = new Intent(this, Blacklist.class);
            startActivity(i);
        }
        else
        {
            Toast.makeText(this,"Blacklist is off",Toast.LENGTH_SHORT).show();
        }
    }

    public void blacklistswitchclicked(View v)
    {
        preferences = getSharedPreferences("switchstatepref",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("blacklistswitchstate",blacklistswitch.isChecked());
        editor.apply();
    }

}
