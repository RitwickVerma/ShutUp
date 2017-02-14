package com.laughingstock.ritwick.shutup;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Gravity;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class detailedSchedcallActivity extends AppCompatActivity
{
    ArrayList<String> schedinfo;
    ArrayAdapter<String> numspinneradapter;
    String number = "",name = "",photo = "",time="",date="",dialnumber="";
    long timeinmills;
    int simnumber=0;
    Boolean ring=true;
    ArrayList<String> diffnums;
    ImageView photoimage;
    TextView nametext,timetext,datetext,numtext;
    CheckBox ringcb;
    RadioGroup simrg;
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
        simrg=(RadioGroup) findViewById(R.id.simrg) ;


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
            simnumber=b.getInt("simnumber");
            ring=b.getBoolean("ring");

            nametext.setText(name);
            photoimage.setImageURI(Uri.parse(photo));
            datetext.setText("Date:  "+date);
            datetext.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            datetext.setTextSize(16);
            timetext.setText("Time:  "+time);
            timetext.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            timetext.setTextSize(16);
            ringcb.setChecked(ring);
            simrg.check(simnumber);

            numspinner.setVisibility(View.VISIBLE);
            numtext.setVisibility(View.VISIBLE);
            botcv.setVisibility(View.VISIBLE);

        }
        else
        {
            numspinner.setVisibility(View.GONE);
            numtext.setVisibility(View.GONE);
            botcv.setVisibility(View.GONE);
            diffnums=new ArrayList<>();
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

        simrg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                simnumber = checkedId;
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
    }

    public void donebuttonpressed(View v)
    {

        if(name.equals("")||time.equals("")||date.equals(""))
        {
            Snackbar.make(findViewById(R.id.activity_detailed_schedcall),"Information incomplete",Snackbar.LENGTH_LONG).show();
        }
        else
        {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");
            try {
                Date mDate = sdf.parse(time+" "+date);
                timeinmills = mDate.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
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
            b.putInt("simnumber",simnumber);
            b.putBoolean("ring",ring);
            ri.putExtra("sdatabundle",b);
            setResult(RESULT_OK, ri);
            finish();
        }
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
                    //Do something with number
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
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
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
        Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

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
