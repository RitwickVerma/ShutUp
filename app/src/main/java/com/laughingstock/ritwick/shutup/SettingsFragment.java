package com.laughingstock.ritwick.shutup;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsFragment extends Fragment
{

    SharedPreferences preferences;
    Spinner singlelist,doublelist;
    TextView singlewave,doublewave,protip;
    ArrayAdapter<CharSequence> adapter;
    Context context;
    CheckBox silentonpick;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        View view= inflater.inflate(R.layout.fragment_settings,container,false);

        context=getActivity();
        singlelist=(Spinner) view.findViewById(R.id.singlelist);
        doublelist=(Spinner) view.findViewById(R.id.doublelist);
        singlewave=(TextView) view.findViewById(R.id.singlewave);
        doublewave=(TextView) view.findViewById(R.id.doublewave);
        protip=(TextView) view.findViewById(R.id.protipinfo);
        silentonpick=(CheckBox) view.findViewById(R.id.silentonpickcheckbox);

        adapter = ArrayAdapter.createFromResource(context,R.array.listoptions,  R.layout.spinner_item);//.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);//android.R.layout.simple_spinner_dropdown_item);

        singlelist.setAdapter(adapter);
        doublelist.setAdapter(adapter);

        singlelist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                preferences = context.getSharedPreferences("switchstatepref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("singlewaveselection",parent.getItemAtPosition(position).toString());

                if(parent.getItemAtPosition(position).toString().equals("End call") || parent.getItemAtPosition(position).toString().equals("Answer call"))
                {
                    editor.putString("doublewaveselection","lol");
                    doublelist.setVisibility(View.INVISIBLE);
                    doublewave.setVisibility(View.INVISIBLE);
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
                    doublewave.setVisibility(View.VISIBLE);
                }

                checkfornothingselected();

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

                preferences = context.getSharedPreferences("switchstatepref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("doublewaveselection",parent.getItemAtPosition(position).toString());
                editor.apply();

                checkfornothingselected();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
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
        preferences = context.getSharedPreferences("switchstatepref",Context.MODE_PRIVATE);
        singlelist.setSelection(adapter.getPosition(preferences.getString("singlewaveselection","")));
        doublelist.setSelection(adapter.getPosition(preferences.getString("doublewaveselection","")));
        silentonpick.setChecked(preferences.getBoolean("silentonpickcheckboxstate",false));
        if(silentonpick.isChecked()) protip.setVisibility(View.VISIBLE);
        else protip.setVisibility(View.GONE);
    }


    public void silentonpickcheckboxclicked(View v)
    {
        preferences = context.getSharedPreferences("switchstatepref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("silentonpickcheckboxstate", silentonpick.isChecked());
        editor.apply();

        if(silentonpick.isChecked()) protip.setVisibility(View.VISIBLE);
        else protip.setVisibility(View.GONE);

        checkfornothingselected();
    }

    public void checkfornothingselected()
    {
        if (singlelist.getSelectedItem().equals("Do nothing") && doublelist.getSelectedItem().equals("Do nothing") && !silentonpick.isChecked())
        {
            Toast.makeText(context, "I'd rather turn off master control if I were you", Toast.LENGTH_SHORT).show();
        }
    }

}
