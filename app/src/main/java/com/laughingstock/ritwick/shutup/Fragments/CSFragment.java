package com.laughingstock.ritwick.shutup.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.laughingstock.ritwick.shutup.Activities.DetailedSchedcallActivity;
import com.laughingstock.ritwick.shutup.Activities.MainActivity;
import com.laughingstock.ritwick.shutup.Adapters.ScheduleContactsAdapter;
import com.laughingstock.ritwick.shutup.R;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class CSFragment extends Fragment
{
    Context context;

    ScheduleContactsAdapter adapter;
    ArrayList<Bundle> schedinfo;
    int pos;
    boolean edit = false;
    View view;
    @BindView(R.id.addschedulebutton)
    FloatingActionButton fab;
    @BindView(R.id.listemptytext)
    TextView listemptytext;
    @BindView(R.id.schedulelistview)
    ListView schedulelistview;
    Unbinder unbinder;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_cs, container, false);
        context = getActivity();
        unbinder= ButterKnife.bind(this,view);


        schedinfo = readFromInternalStorage(context);
        adapter = new ScheduleContactsAdapter(context, schedinfo, listemptytext);
        schedulelistview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (schedinfo.size() == 0) listemptytext.setVisibility(View.VISIBLE);
        else listemptytext.setVisibility(View.GONE);


        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                pos = position;
                edit = true;
                Pair<View, String> p1 = Pair.create(view.findViewById(R.id.schlistcontactphoto), "contactphoto");
                Pair<View, String> p2 = Pair.create(view.findViewById(R.id.mainswipe), "scheduledetails");
                Pair<View, String> p3 = Pair.create(fab, "fab");
                Intent i = new Intent(context, DetailedSchedcallActivity.class);
                Bundle b = schedinfo.get(position);
                i.putExtra("sdatabundle", b);
                ActivityOptionsCompat options = ActivityOptionsCompat
                        .makeSceneTransitionAnimation((Activity) context, p1, p2, p3);
                startActivityForResult(i, 0, options.toBundle());
            }
        });
        return view;
    }

    public View addschedulebuttonclicked(View v)
    {
        edit = false;
        Intent i = new Intent(context, DetailedSchedcallActivity.class);
        startActivityForResult(i, 0);
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 0)
        {
            if (resultCode == MainActivity.RESULT_OK)
            {
                Bundle b = data.getBundleExtra("sdatabundle");
                if (edit)
                {
                    schedinfo.set(pos, b);
                } else
                {
                    schedinfo.add(b);
                }
                adapter.notifyDataSetChanged();

                System.out.println(schedinfo);

                if (schedinfo.size() > 0) listemptytext.setVisibility(View.INVISIBLE);
                saveToInternalStorage(context, schedinfo);

            }
        }
    }


    public void saveToInternalStorage(Context context, ArrayList<Bundle> schedinfo)
    {
        try
        {
            FileOutputStream fos = context.openFileOutput("schedinfo", Context.MODE_PRIVATE);
            ObjectOutputStream of = new ObjectOutputStream(fos);
            Bundle temp = new Bundle();
            temp.putParcelableArrayList("test", schedinfo);
            of.writeObject(serializeBundle(temp));
            of.flush();
            of.close();
            fos.close();
        } catch (Exception e)
        {
            Log.e("InternalStorage", e.getMessage());
        }

    }

    public ArrayList<Bundle> readFromInternalStorage(Context context)
    {
        ArrayList<Bundle> toReturn;
        FileInputStream fis;
        try
        {
            fis = context.openFileInput("schedinfo");
            ObjectInputStream oi = new ObjectInputStream(fis);
            String temp;
            Bundle b;
            try
            {
                temp = (String) oi.readObject();
                b = deserializeBundle(temp);
                toReturn = b.getParcelableArrayList("test");
            } catch (Exception e)
            {
                return new ArrayList<>();
            }
            oi.close();
        } catch (FileNotFoundException e)
        {
            Log.e("InternalStorage", e.getMessage());
            return new ArrayList<>();
        } catch (IOException e)
        {
            Log.e("InternalStorage", e.getMessage());
            return new ArrayList<>();
        }
        return toReturn;
    }

    private String serializeBundle(final Bundle bundle)
    {
        String base64 = null;
        final Parcel parcel = Parcel.obtain();
        try
        {
            parcel.writeBundle(bundle);
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final GZIPOutputStream zos = new GZIPOutputStream(new BufferedOutputStream(bos));
            zos.write(parcel.marshall());
            zos.close();
            base64 = Base64.encodeToString(bos.toByteArray(), 0);
        } catch (IOException e)
        {
            e.printStackTrace();
            base64 = null;
        } finally
        {
            parcel.recycle();
        }
        return base64;
    }

    private Bundle deserializeBundle(final String base64)
    {
        Bundle bundle = null;
        final Parcel parcel = Parcel.obtain();
        try
        {
            final ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            final byte[] buffer = new byte[1024];
            final GZIPInputStream zis = new GZIPInputStream(new ByteArrayInputStream(Base64.decode(base64, 0)));
            int len;
            while ((len = zis.read(buffer)) != -1)
            {
                byteBuffer.write(buffer, 0, len);
            }
            zis.close();
            parcel.unmarshall(byteBuffer.toByteArray(), 0, byteBuffer.size());
            parcel.setDataPosition(0);
            bundle = parcel.readBundle(getClass().getClassLoader());
        } catch (IOException e)
        {
            e.printStackTrace();
            bundle = null;
        } finally
        {
            parcel.recycle();
        }

        return bundle;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }
}
