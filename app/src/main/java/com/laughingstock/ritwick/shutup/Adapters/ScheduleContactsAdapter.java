package com.laughingstock.ritwick.shutup.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.laughingstock.ritwick.shutup.BroadcastReceivers.SchedAlarmReciever;
import com.laughingstock.ritwick.shutup.Fragments.CSFragment;
import com.laughingstock.ritwick.shutup.R;

import java.util.ArrayList;


public class ScheduleContactsAdapter extends BaseAdapter
{

    Context context;

    private ArrayList<Bundle> schedinfo;
    private TextView listemptytext;
    private String number = "", name = "", photo = "", time = "", date = "", dialnumber = "";
    boolean repeatcall=false;
    private long timeinmills;
    private ArrayList<String> diffnums;
    int repeatinterval;


    private static LayoutInflater inflater = null;

    private AdapterView.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ScheduleContactsAdapter(Context context, ArrayList<Bundle> schedinfo, TextView listemptytext)
    {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.schedinfo = schedinfo;
        this.listemptytext = listemptytext;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

   public ScheduleContactsAdapter(Context context, ArrayList<Bundle> schedinfo)
   {
       this.context=context;
       this.schedinfo=schedinfo;
   }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent)
    {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.row_in_schedulelist,parent,false);
        final View vitemp=vi;

        final SwipeRevealLayout swipeLayout = vi.findViewById(R.id.swipe);

        TextView schedinfotextview = vi.findViewById(R.id.schedinfotextview);
        final ImageView schcontactphoto = vi.findViewById(R.id.schlistcontactphotopic);

        Bundle b = schedinfo.get(position);
        if (b != null)
        {
            name = b.getString("name");
            diffnums = b.getStringArrayList("numbers");
            dialnumber = b.getString("dialnumber");
            photo = b.getString("photo");
            time = b.getString("time");
            date = b.getString("date");
            timeinmills = b.getLong("timeinmills");
            repeatcall=b.getBoolean("repeatcall");
            repeatinterval=b.getInt("repeatinterval");

            String temp = "Call " + name + "\n"+((repeatcall)?"next ":"")+"at " + time + ((repeatcall)?(""):("\non " + date)) + "\non number " + dialnumber + ((repeatcall)?("\nand repeat every "+repeatinterval+" hour"+((repeatinterval>1)?"s":"")):"");
            schedinfotextview.setText(temp);

            schcontactphoto.setImageURI(Uri.parse(photo));
        }
        else vi.setVisibility(View.GONE);


        swipeLayout.close(true);
        swipeLayout.setSwipeListener(new SwipeRevealLayout.SwipeListener()
        {
            @Override
            public void onClosed(SwipeRevealLayout view)
            {            }

            @Override
            public void onOpened(SwipeRevealLayout view)
            {
                final Bundle temp = schedinfo.get(position);
                schedinfo.remove(position);
                notifyDataSetChanged();
                Snackbar.make(swipeLayout, "Schedule cancelled", Snackbar.LENGTH_LONG)
                        .setAction("Undo",(v)->
                            {
                                schedinfo.add(position, temp);
                                notifyDataSetChanged();
                                CSFragment csFragment = new CSFragment();
                                csFragment.saveToInternalStorage(context, schedinfo);

                                SchedAlarmReciever schedAlarmReciever = new SchedAlarmReciever();
                                //schedAlarmReciever.setAlarm(context,timeinmills,position);


                                if (schedinfo.size() == 0)
                                    listemptytext.setVisibility(View.VISIBLE);
                                else listemptytext.setVisibility(View.INVISIBLE);
                                swipeLayout.close(true);

                            })
                        .setActionTextColor(Color.parseColor("#2196F3"))
                        .show();
                if (schedinfo.size() == 0) listemptytext.setVisibility(View.VISIBLE);

                CSFragment csFragment=new CSFragment();
                csFragment.saveToInternalStorage(context,schedinfo);

                SchedAlarmReciever schedAlarmReciever=new SchedAlarmReciever();
                //schedAlarmReciever.cancelAlarm(context,position);
            }

            @Override
            public void onSlide(SwipeRevealLayout view, float slideOffset)
            {            }
        });

        RelativeLayout mainswipe= vi.findViewById(R.id.mainswipe);
        mainswipe.setOnClickListener((v)->
                {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(null, vitemp, position, -1);
                }
            }
        );
        return vi;
    }

    @Override
    public void notifyDataSetChanged()
    {
        Thread t=new Thread(){
            @Override
            public void run()
            {
                for(int j=0;j<=50;j++)
                {
                    SchedAlarmReciever schedAlarmReciever=new SchedAlarmReciever();
                    schedAlarmReciever.cancelAlarm(context,j);
                }
                for(Bundle b:schedinfo)
                {
                    SchedAlarmReciever schedAlarmReciever=new SchedAlarmReciever();
                    schedAlarmReciever.setAlarm(context,b.getLong("timeinmills"),schedinfo.indexOf(b));
                }

            }
        };

        t.start();

        super.notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        // TODO Auto-generated method stub
        return schedinfo.size();
    }

    @Override
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub
        return schedinfo.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return position;
    }
}