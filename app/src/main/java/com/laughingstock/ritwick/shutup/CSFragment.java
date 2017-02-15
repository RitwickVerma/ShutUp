package com.laughingstock.ritwick.shutup;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;


public class CSFragment extends Fragment
{
    Context context;

    TextView listemptytext;
    ListView schedulelistview;
    schedulecontactsAdapter adapter;
    ArrayList<Bundle> schedinfo;
    int pos;
    Boolean edit=false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view= inflater.inflate(R.layout.fragment_cs,container,false);
        context=getActivity();

        schedulelistview=(ListView) view.findViewById(R.id.schedulelistview);
        listemptytext=(TextView) view.findViewById(R.id.listemptytext);

        schedinfo=readFromInternalStorage();
        adapter = new schedulecontactsAdapter(context,schedinfo,listemptytext);
        schedulelistview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if(schedinfo.size()==0) listemptytext.setVisibility(View.VISIBLE);
        else listemptytext.setVisibility(View.GONE);


        schedulelistview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                pos=position;
                edit=true;
                Intent i=new Intent(context,detailedSchedcallActivity.class);
                Bundle b=schedinfo.get(position);
                i.putExtra("sdatabundle",b);
                startActivityForResult(i,0);
            }
        });
        return view;
    }

    public void addschedulebuttonclicked(View v)
    {
        edit=false;
        Intent i=new Intent(context,detailedSchedcallActivity.class);
        startActivityForResult(i,0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode==0)
        {
            if(resultCode==MainActivity.RESULT_OK)
            {
                Bundle b=data.getBundleExtra("sdatabundle");
                if(edit)
                    schedinfo.set(pos,b);
                else
                    schedinfo.add(b);
                adapter.notifyDataSetChanged();

                if(schedinfo.size()>0) listemptytext.setVisibility(View.INVISIBLE);
                saveToInternalStorage();

            }
        }
    }

    public void saveToInternalStorage()
    {/*
        try
        {
            FileOutputStream fos = context.openFileOutput("schedinfo", Context.MODE_PRIVATE);
            ObjectOutputStream of = new ObjectOutputStream(fos);
            Gson gson=new Gson();
            of.writeObject(gson.toJson(schedinfo));
            of.flush();
            of.close();
            fos.close();
        } catch (Exception e)
        {
            Log.e("InternalStorage", e.getMessage());
        }*/
        SharedPreferences preferences = context.getSharedPreferences("switchstatepref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        //editor.putString("schedinfo", gson.toJson(schedinfo)).apply();


    }

    public ArrayList<Bundle> readFromInternalStorage()
    {/*
        ArrayList<Bundle> toReturn;
        FileInputStream fis;
        try {
            fis = context.openFileInput("schedinfo");
            ObjectInputStream oi = new ObjectInputStream(fis);
            Gson gson=new Gson();
            Type type = new TypeToken<ArrayList<Bundle>>(){}.getType();
            String temp;
            try{
            temp =(String) oi.readObject();
                toReturn=gson.fromJson(temp,type);
            }catch(Exception e)
            {return new ArrayList<Bundle>();}
            oi.close();
        } catch (FileNotFoundException e) {
            Log.e("InternalStorage", e.getMessage());
            return new ArrayList<Bundle>();
        } catch (IOException e) {
            Log.e("InternalStorage", e.getMessage());
            return new ArrayList<Bundle>();
        }
        return toReturn;*/
        SharedPreferences preferences = context.getSharedPreferences("switchstatepref",Context.MODE_PRIVATE);
        Gson gson=new Gson();
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        if(preferences.getString("schedinfo",null)==null)
        schedinfo= new ArrayList<Bundle>();
        else
        schedinfo=gson.fromJson(preferences.getString("schedinfo",null),type);
        return schedinfo;

    }
}
