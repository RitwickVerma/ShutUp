package com.laughingstock.ritwick.shutup;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.ArrayList;


class schedulecontactsAdapter extends BaseSwipeAdapter
{

    Context context;
    View convertView;

    int[] listenerdata;
    private ArrayList<Bundle> schedinfo;
    private TextView listemptytext;
    private String number = "",name = "",photo = "",time="",date="",dialnumber="";
    private long timeinmills;
    private ArrayList<String> diffnums;
    private ArrayList<SimpleSwipeListener> swipeListeners;


    private static LayoutInflater inflater = null;

    schedulecontactsAdapter(Context context, ArrayList<Bundle> schedinfo, TextView listemptytext)
    {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.schedinfo=schedinfo;
        this.listemptytext=listemptytext;
        swipeListeners=new ArrayList<>();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getSwipeLayoutResourceId(int position)
    {
        return R.id.swipe;
    }

    @Override
    public View generateView(final int position, final ViewGroup parent)
    {
            //vi = inflater.inflate(R.layout.row_in_schedulelist,parent,false);

        View vi = LayoutInflater.from(context).inflate(R.layout.row_in_schedulelist, parent,false);

        return vi;

    }

    public void fillValues(final int position, View convertView)
    {
        View vi=convertView;
        final SwipeLayout swipeLayout = (SwipeLayout) vi.findViewById(R.id.swipe);

        TextView schedinfotextview=(TextView) vi.findViewById(R.id.schedinfotextview);
        ImageView schcontactphoto=(ImageView) vi.findViewById(R.id.schlistcontactphotopic);

        Bundle b=schedinfo.get(position);
        if(b!=null)
        {
            name = b.getString("name");
            diffnums = b.getStringArrayList("numbers");
            dialnumber = b.getString("dialnumber");
            photo = b.getString("photo");
            time = b.getString("time");
            date = b.getString("date");
            timeinmills = b.getLong("timeinmills");

            String temp = "Call " + name + "\nat " + time + "\nof " + date + "\non number " + dialnumber;
            schedinfotextview.setText(temp);

            schcontactphoto.setImageURI(Uri.parse(photo));
        }


        swipeLayout.close(true);
        SimpleSwipeListener s =  new SimpleSwipeListener()
        {
            @Override
            public void onOpen(SwipeLayout layout)
            {


                final Bundle temp=schedinfo.get(position);
                schedinfo.remove(position);
                notifyDataSetChanged();
                Snackbar.make(swipeLayout,"Schedule cancelled", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Undo", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                schedinfo.add(position,temp);
                                notifyDataSetChanged();
                                if(schedinfo.size()==0) listemptytext.setVisibility(View.VISIBLE);
                                else listemptytext.setVisibility(View.INVISIBLE);
                            }
                        })
                        .setActionTextColor(Color.YELLOW)
                        .show();
                if(schedinfo.size()==0) listemptytext.setVisibility(View.VISIBLE);
            }};


        for (SimpleSwipeListener l : swipeListeners) {
            swipeLayout.removeSwipeListener(l);
        }
        swipeListeners.clear();
        swipeListeners.add(s);
        swipeLayout.addSwipeListener(s);

    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return schedinfo.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return schedinfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }


}
