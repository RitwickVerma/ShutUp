package com.laughingstock.ritwick.shutup;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;


public class Blacklist extends AppCompatActivity
{

    ListView blacklistcontactslistview;
    contactsAdapter adapter;
    ArrayList<String> contactnames, contactnumbers,contactphotos;

    String name,number,photo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist);

        blacklistcontactslistview = (ListView) findViewById(R.id.blacklistview);


        if(ContextCompat.checkSelfPermission(Blacklist.this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(Blacklist.this, new String[]{Manifest.permission.READ_CONTACTS}, 0);
        }


        SharedPreferences preferences = getSharedPreferences("switchstatepref",MODE_PRIVATE);

        String tempcontactnamejson = preferences.getString("blacklistcontactnamespref",null);
        String tempcontactnumberjson = preferences.getString("blacklistcontactnumberspref",null);
        String tempcontactphotojson = preferences.getString("blacklistcontactphotospref", null);

        Gson gson=new Gson();
        Type type = new TypeToken<ArrayList<String>>(){}.getType();

        contactnames = new ArrayList<String>();
        contactnumbers = new ArrayList<String>();
        contactphotos = new ArrayList<String>();

        if(tempcontactnamejson!=null && tempcontactnumberjson!=null)
        {
            contactnames= gson.fromJson(tempcontactnamejson, type);
            contactnumbers= gson.fromJson(tempcontactnumberjson, type);
            contactphotos= gson.fromJson(tempcontactphotojson, type);
        }

        adapter = new contactsAdapter(this,contactnames,contactnumbers,contactphotos);
        blacklistcontactslistview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults[0]!=PackageManager.PERMISSION_GRANTED && requestCode==0)
        {
            Toast.makeText(this, "Can't work without permission", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void addcontactbuttonclicked(View v)
    {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
        {
            if (resultCode == RESULT_OK)
            {
                Uri contactData = data.getData();
                number = "";
                name = "";
                photo = "";
                Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
                cursor.moveToFirst();
                String hasPhone = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                if (Integer.parseInt(hasPhone) > 0)
                {
                    Cursor phones = getContentResolver().query
                            (ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                            + " = " + contactId, null, null);
                    while (phones.moveToNext())
                    {
                        String tempnum = phones.getString(phones.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("[-() ]", "");

                        number = number + (number.equals("") || number.contains(tempnum) ? "" : "\n") + (number.contains(tempnum) ? "" : tempnum);
                        name = phones.getString(phones.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY));//.replaceAll("[-() ]","");

                        photo = phones.getString(phones.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                    }
                    phones.close();
                    //Do something with number

                    if (contactnames.contains(name) && contactnumbers.get(contactnames.lastIndexOf(name)).equals(number))
                    {
                        Toast.makeText(this, "Contact already added!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Random r=new Random();
                        String randimguri = "android.resource://"+getPackageName()+"/drawable/contactphoto"+(r.nextInt(5)+1);

                        contactnames.add(name);
                        contactnumbers.add(number);
                        if(photo==null)
                            photo=randimguri;
                        contactphotos.add(photo);
                        adapter.notifyDataSetChanged();
                        savetosharedpreference();
                    }
                }
                else
                {
                    Toast.makeText(this, "This contact has no phone number", Toast.LENGTH_LONG).show();
                }
                cursor.close();
            }
        }
    }



    public void savetosharedpreference()
    {
        SharedPreferences preferences = getSharedPreferences("switchstatepref",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String contactnamejson = gson.toJson(contactnames);
        String contactnumberjson = gson.toJson(contactnumbers);
        String contactphotojson = gson.toJson(contactphotos);

        editor.putString("blacklistcontactnamespref", contactnamejson);
        editor.putString("blacklistcontactnumberspref", contactnumberjson);
        editor.putString("blacklistcontactphotospref", contactphotojson);
        editor.apply();
    }


}
