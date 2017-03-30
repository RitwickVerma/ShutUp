package com.laughingstock.ritwick.shutup;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.*;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class detailedSchedcallActivity extends AppCompatActivity
{
    ArrayAdapter<String> numspinneradapter;
    String number = "",name = "",photo = "",time="",date="",dialnumber="";
    long timeinmills;
    boolean ring=true,vibrate=true,calldaily=false;
    ArrayList<String> diffnums;
    ImageView photoimage;
    TextView nametext,timetext,datetext,numtext;
    CheckBox ringcb,vibratecb,calldailycb;
    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;
    Spinner numspinner;
    boolean backpressedflag=false,edited=false;
    CardView botcv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_schedcall);

        photoimage= (ImageView) findViewById(R.id.schcontactphotopic);
        nametext=(TextView) findViewById(R.id.nametext);
        timetext=(TextView) findViewById(R.id.timetext);
        datetext=(TextView) findViewById(R.id.datetext);
        numtext=(TextView) findViewById(R.id.numtext);
        numspinner=(Spinner) findViewById(R.id.numspinner);
        botcv=(CardView) findViewById(R.id.botcv);
        ringcb=(CheckBox) findViewById(R.id.ringcb);
        vibratecb=(CheckBox) findViewById(R.id.vibratecb);
        calldailycb=(CheckBox) findViewById(R.id.calldailycb);

        Intent intent=getIntent();
        Bundle b=intent.getBundleExtra("sdatabundle");

        if(b!=null)
        {
            name=b.getString("name");
            diffnums=new ArrayList<>(b.getStringArrayList("numbers"));
            dialnumber=b.getString("dialnumber");
            photo=b.getString("photo");
            time=b.getString("time");
            date=b.getString("date");
            timeinmills=b.getLong("timeinmills");
            vibrate=b.getBoolean("vibrate");
            ring=b.getBoolean("ring");
            calldaily=b.getBoolean("calldaily");

            nametext.setText(name);
            photoimage.setImageURI(Uri.parse(photo));
            datetext.setText("Date:  "+date);
            datetext.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            datetext.setTextSize(16);
            timetext.setText("Time:  "+time);
            timetext.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            timetext.setTextSize(16);
            ringcb.setChecked(ring);
            vibratecb.setChecked(vibrate);
            calldailycb.setChecked(calldaily);

            numspinner.setVisibility(View.VISIBLE);
            numtext.setVisibility(View.VISIBLE);
            botcv.setVisibility(View.VISIBLE);
            datetext.setVisibility((calldaily)?View.GONE:View.VISIBLE);

        }
        else
        {
            numspinner.setVisibility(View.GONE);
            numtext.setVisibility(View.GONE);
            botcv.setVisibility(View.GONE);
            diffnums=new ArrayList<>();

            Random r=new Random();
            String randimguri = "android.resource://"+getPackageName()+"/drawable/contactphoto"+(r.nextInt(5)+1);
            photo=randimguri;
            photoimage.setImageURI(Uri.parse(photo));
        }


        numspinneradapter = new ArrayAdapter<>(this, R.layout.spinner_item, diffnums);
        numspinneradapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        numspinner.setAdapter(numspinneradapter);
        numspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                dialnumber=diffnums.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {            }
        });

        vibratecb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                vibrate=isChecked;
            }
        });

        ringcb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                ring=isChecked;
            }
        });

        calldailycb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                calldaily=isChecked;
                datetext.setVisibility((calldaily)?View.GONE:View.VISIBLE);
            }
        });

        View.OnLongClickListener l=new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                diffnums.clear();
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 1);
                return true;
            }
        };
        nametext.setOnLongClickListener(l);
        photoimage.setOnLongClickListener(l);
    }

    public View donebuttonpressed(View v)
    {

        if(name.equals("")||time.equals("")||(date.equals("") && !calldaily))
        {
            Snackbar.make(findViewById(R.id.activity_detailed_schedcall),"Information incomplete",Snackbar.LENGTH_LONG)
            .setAction("I don't care", new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    finish();
                }
            })
            .setActionTextColor(Color.parseColor("#2196F3"))
            .show();

        }
        else
        {
            if(calldaily)
            {
                int mYear,mMonth,mDay;
                Calendar mcurrentDate = Calendar.getInstance();
                mYear = mcurrentDate.get(Calendar.YEAR);
                mMonth = mcurrentDate.get(Calendar.MONTH);
                mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                date=mDay+"/"+(++mMonth)+"/"+mYear;

            }

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");
            try {
                Date mDate = sdf.parse(time+" "+date);
                timeinmills = mDate.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(timeinmills/60000< System.currentTimeMillis()/60000 && !edited)
            {
                Toast.makeText(this, "Calling back in time is not yet possible.\n(o_o)", Toast.LENGTH_SHORT).show();
                return v;
            }
            Intent ri = new Intent();
            Bundle b = new Bundle();
            b.putString("name", name);
            b.putStringArrayList("numbers", diffnums);
            b.putString("dialnumber", dialnumber);
            b.putString("photo", photo);
            b.putString("time", time);
            b.putString("date", date);
            b.putLong("timeinmills", timeinmills);
            b.putBoolean("vibrate",vibrate);
            b.putBoolean("ring",ring);
            b.putBoolean("calldaily",calldaily);

            ri.putExtra("sdatabundle",b);
            setResult(RESULT_OK, ri);
            finish();
        }
        return v;
    }

    public void cancelbuttonpressed(View v)
    {
        finish();
    }

    public void selectcontacttextclicked(View v)
    {
        if(nametext.getText().equals("Add Contact"))
        {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
        {
            if (resultCode == MainActivity.RESULT_OK)
            {

                Uri contactData = data.getData();

                Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
                cursor.moveToFirst();
                String hasPhone = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                if (Integer.parseInt(hasPhone) > 0)
                {
                    name="";
                    number="";
                    photo="";

                    Cursor phones = getContentResolver().query
                            (ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                            + " = " + contactId, null, null);
                    while (phones.moveToNext())
                    {
                        String tempnum = phones.getString(phones.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("[-() ]", "");

                        //number = number + (number.equals("") || number.contains(tempnum) ? "" : "###") + (number.contains(tempnum) ? "" : tempnum);

                        if(!diffnums.contains(tempnum))
                            diffnums.add(tempnum);

                        name = phones.getString(phones.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY));//.replaceAll("[-() ]","");

                        photo = phones.getString(phones.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                    }
                    phones.close();

                    Random r=new Random();
                    String randimguri = "android.resource://"+getPackageName()+"/drawable/contactphoto"+(r.nextInt(5)+1);
                    if(photo==null)
                        photo=randimguri;
                    photoimage.setImageURI(Uri.parse(photo));

                    nametext.setText(name);
                    numspinner.setVisibility(View.VISIBLE);
                    numtext.setVisibility(View.VISIBLE);
                    botcv.setVisibility(View.VISIBLE);
                    numspinneradapter.notifyDataSetChanged();
                    dialnumber=diffnums.get(0);
                    setTitle("Call "+name.split(" ")[0]);
                }
                else
                {
                    Toast.makeText(this, "This contact has no phone number", Toast.LENGTH_LONG).show();
                }
                cursor.close();


            }
        }
    }


    public void settimetextclicked(View v)
    {
        int hour,minute;
        if(time.equals(""))
        {
            Calendar mcurrentTime = Calendar.getInstance();
            hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            minute = mcurrentTime.get(Calendar.MINUTE);
        }
        else
        {
            hour = Integer.parseInt(time.split(":")[0]);
            minute = Integer.parseInt(time.split(":")[1]);
        }

        timePickerDialog=new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker view, final int hourOfDay, final int minute)
            {
                time=hourOfDay+":"+minute;
                timetext.setText("Time:  "+time);
                timetext.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                timetext.setTextSize(16);
            }
        },hour,minute,false);
        timePickerDialog.show();

    }

    public void setdatetextclicked(View v)
    {
        int mYear,mMonth,mDay;
        if(date.equals(""))
        {
            Calendar mcurrentDate = Calendar.getInstance();
            mYear = mcurrentDate.get(Calendar.YEAR);
            mMonth = mcurrentDate.get(Calendar.MONTH);
            mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
        }
        else
        {
            mYear = Integer.parseInt(date.split("/")[2]);
            mMonth = Integer.parseInt(date.split("/")[1]);
            mMonth--;
            mDay = Integer.parseInt(date.split("/")[0]);
        }
        datePickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
                date=dayOfMonth+"/"+(++month)+"/"+year;
                month--;
                datetext.setText("Date:  "+date);
                datetext.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                datetext.setTextSize(16);

            }
        },mYear,mMonth,mDay);
        datePickerDialog.show();
    }

    @Override
    public void onBackPressed()
    {
        if(backpressedflag)
            super.onBackPressed();
        else
        {
            Snackbar.make(findViewById(R.id.activity_detailed_schedcall),"Press back again to cancel",Snackbar.LENGTH_LONG).show();
            backpressedflag=true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}
