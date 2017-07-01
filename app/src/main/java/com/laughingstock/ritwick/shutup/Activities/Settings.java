package com.laughingstock.ritwick.shutup.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.laughingstock.ritwick.shutup.R;

public class Settings extends AppCompatActivity
{

    SeekBar consecutivewavethreshold;
    TextView consecutivewavethresholdtext;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        consecutivewavethresholdtext= findViewById(R.id.consecutivewavethresholdtext);
        consecutivewavethreshold= findViewById(R.id.consecutivewavethresholdseekbar);


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
        consecutivewavethreshold.setProgress(preferences.getInt("consecutivewavethresholdseekbarprogress",12));
    }


}
