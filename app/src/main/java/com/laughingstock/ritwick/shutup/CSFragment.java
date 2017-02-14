package com.laughingstock.ritwick.shutup;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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

        schedinfo=new ArrayList<>();
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

            }
        }
    }
}
