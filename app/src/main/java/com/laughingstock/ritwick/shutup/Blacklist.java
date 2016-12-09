package com.laughingstock.ritwick.shutup;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;



public class Blacklist extends AppCompatActivity
{

    ListView blacklistcontactslistview;
    contactsAdapter adapter;
    ArrayList<String> contactnames, contactnumbers;

    String name,number;

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

        Set<String> tempcontactnameset = preferences.getStringSet("blacklistcontactnamespref", null);
        Set<String> tempcontactnumberset = preferences.getStringSet("blacklistcontactnumberspref", null);

        if(tempcontactnameset!=null && tempcontactnumberset!=null)
        {
            contactnames = new ArrayList<String>(tempcontactnameset);
            contactnumbers = new ArrayList<String>(tempcontactnumberset);
        }
        else
        {
            contactnames = new ArrayList<String>();
            contactnumbers = new ArrayList<String>();
        }

        adapter = new contactsAdapter(this,contactnames,contactnumbers);
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
                Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
                cursor.moveToFirst();
                String hasPhone = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                if (hasPhone.equals("1"))
                {
                    Cursor phones = getContentResolver().query
                            (ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                            + " = " + contactId, null, null);
                    while (phones.moveToNext())
                    {
                        number = phones.getString(phones.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.NUMBER));//.replaceAll("[-() ]", "");
                        name= phones.getString(phones.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY));//.replaceAll("[-() ]","");
                    }
                    phones.close();
                    //Do something with number
                    contactnames.add(name);
                    contactnumbers.add(number);
                    adapter.notifyDataSetChanged();

                    savetosharedpreference();
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

        Set<String> contactnameset = new HashSet<String>();
        Set<String> contactnumberset = new HashSet<String>();
        contactnameset.addAll(contactnames);
        contactnumberset.addAll(contactnumbers);
        editor.putStringSet("blacklistcontactnamespref", contactnameset);
        editor.putStringSet("blacklistcontactnumberspref", contactnumberset);
        editor.apply();
    }


}
