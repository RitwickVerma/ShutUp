package com.laughingstock.ritwick.shutup.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.laughingstock.ritwick.shutup.Activities.BlacklistWhitelist;
import com.laughingstock.ritwick.shutup.R;

public class CCandAFragment extends Fragment
{

    SharedPreferences preferences;
    Spinner singlelist,doublelist,flipdownlist;
    TextView singlewave,doublewave,flipdowntext,blacklistwhitelisttext1,blacklistwhitelisttext2;
    ArrayAdapter<CharSequence> waveadapter,flipdownadapter;
    Context context;
    CheckBox silentonpick,speakeron;
    Switch blacklistwhitelistswitch;
    ConstraintLayout blacklistexpandinglayout;
    View linebwblandsp;
    RelativeLayout.LayoutParams params;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view= inflater.inflate(R.layout.fragment_ccanda,container,false);
        context=getActivity();

        singlelist= view.findViewById(R.id.singlelist);
        doublelist= view.findViewById(R.id.doublelist);
        flipdownlist= view.findViewById(R.id.flipdownlist);
        singlewave= view.findViewById(R.id.singlewave);
        doublewave= view.findViewById(R.id.doublewave);
        flipdowntext= view.findViewById(R.id.flipdowntext);
        silentonpick= view.findViewById(R.id.silentonpickcheckbox);
        speakeron= view.findViewById(R.id.speakeroncheckbox);
        blacklistwhitelistswitch= view.findViewById(R.id.blacklistwhitelistswitch);
        blacklistexpandinglayout= view.findViewById(R.id.blacklistexpandinglayout);
        blacklistwhitelisttext1= view.findViewById(R.id.blacklistwhitelisttext1);
        blacklistwhitelisttext2= view.findViewById(R.id.blacklistwhitelisttext2);
        linebwblandsp=view.findViewById(R.id.view7);

        //params = (RelativeLayout.LayoutParams) imptechview.getLayoutParams();

        waveadapter = ArrayAdapter.createFromResource(context,R.array.wavelistoptions,  R.layout.spinner_item);//.R.layout.simple_spinner_item);
        waveadapter.setDropDownViewResource(R.layout.spinner_dropdown_item);//android.R.layout.simple_spinner_dropdown_item);

        flipdownadapter=ArrayAdapter.createFromResource(context,R.array.facedownlistoptions,R.layout.spinner_item);
        flipdownadapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        singlelist.setAdapter(waveadapter);
        doublelist.setAdapter(waveadapter);

        flipdownlist.setAdapter(flipdownadapter);


        singlelist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                preferences = context.getSharedPreferences("shutupsharedpref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("singlewaveselection",parent.getItemAtPosition(position).toString());

                if(parent.getItemAtPosition(position).toString().equals("End call") || parent.getItemAtPosition(position).toString().equals("Answer call"))
                {
                    editor.putString("doublewaveselection","lol");
                    doublelist.setVisibility(View.INVISIBLE);
                    doublewave.setText("Double wave function not available");
                    if(parent.getItemAtPosition(position).toString().equals("Answer call"))
                    {
                        singlewave.setText("Single wave or :\nPut on ear");
                    }
                    else    singlewave.setText(R.string.single_wave);
                }
                else
                {
                    editor.putString("doublewaveselection",doublelist.getSelectedItem().toString());
                    doublelist.setVisibility(View.VISIBLE);
                    doublewave.setText(R.string.double_wave);
                }

                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Toast.makeText(context,"Choice not changed",Toast.LENGTH_SHORT).show();
            }

        });

        doublelist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {

                preferences = context.getSharedPreferences("shutupsharedpref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("doublewaveselection",parent.getItemAtPosition(position).toString());
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Toast.makeText(context,"Choice not changed",Toast.LENGTH_SHORT).show();
            }
        });


        flipdownlist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                preferences = context.getSharedPreferences("shutupsharedpref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("flipdownlistselection",parent.getItemAtPosition(position).toString());
                editor.apply();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
                Toast.makeText(context,"Choice not changed",Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }


    @Override
    public void onResume()
    {
        super.onResume();
        preferences = context.getSharedPreferences("shutupsharedpref",Context.MODE_PRIVATE);
        singlelist.setSelection(waveadapter.getPosition(preferences.getString("singlewaveselection","")));
        doublelist.setSelection(waveadapter.getPosition(preferences.getString("doublewaveselection","")));
        silentonpick.setChecked(preferences.getBoolean("silentonpickcheckboxstate",false));
        speakeron.setChecked(preferences.getBoolean("speakeroncheckboxstate",false));
        blacklistwhitelistswitch.setChecked(preferences.getBoolean("blacklistwhitelistswitchstate",false));
        flipdownlist.setSelection(flipdownadapter.getPosition(preferences.getString("flipdownlistselection","")));
        checkspeakeronchecked();
    }


    public void silentonpickcheckboxclicked(View v)
    {
        preferences = context.getSharedPreferences("shutupsharedpref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("silentonpickcheckboxstate", silentonpick.isChecked());
        editor.apply();
    }

    public void speakeroncheckboxclicked(View v)
    {
        preferences = context.getSharedPreferences("shutupsharedpref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("speakeroncheckboxstate", speakeron.isChecked());
        editor.apply();
        checkspeakeronchecked();
    }


    public void blacklistwhitelisttextclicked(View v)
    {
        if(blacklistwhitelistswitch.isChecked())
        {
            Intent i = new Intent(context, BlacklistWhitelist.class);
            startActivity(i);
        }
        else
        {
            Toast.makeText(context,"Blacklist/ Whitelist switch is off",Toast.LENGTH_SHORT).show();
        }
    }

    public void blacklistwhitelistswitchclicked(View v)
    {
        preferences = context.getSharedPreferences("shutupsharedpref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("blacklistwhitelistswitchstate",blacklistwhitelistswitch.isChecked());
        editor.apply();
    }

    public void checkspeakeronchecked()
    {
        if(speakeron.isChecked())
        {
            blacklistexpandinglayout.setVisibility(View.VISIBLE);
            linebwblandsp.setVisibility(View.VISIBLE);
        }
        else
        {
            blacklistexpandinglayout.setVisibility(View.GONE);
            linebwblandsp.setVisibility(View.GONE);
        }
    }
}
