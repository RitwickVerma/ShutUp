package com.laughingstock.ritwick.shutup;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Donatepage extends AppCompatActivity
{
    RadioGroup donaterg;
    TextView neededinfo,needinfo2;
    ClipboardManager clipboard;
    long rbid;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTitle("Donate");
        setContentView(R.layout.activity_donatepage);

        donaterg=(RadioGroup) findViewById(R.id.donaterg);
        neededinfo=(TextView) findViewById(R.id.neededinfotext);
        needinfo2=(TextView) findViewById(R.id.needinfo2text);

        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        donaterg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId)
            {
                rbid=checkedId;
                if(rbid==R.id.paypalrb)
                {
                    neededinfo.setText("You'll need a PayPal account for that. I hope that's not a problem.");
                    needinfo2.setText("");
                }

                else if(rbid==R.id.paytmrb)
                {
                    neededinfo.setText("Donate money on number 9791145969 through Paytm.");
                    needinfo2.setText("");

                }

                else if(rbid==R.id.upirb)
                {
                    neededinfo.setText("Use apps like BHIM or PhonePe\nYou can use any of the following virtual payment addresses:");
                    needinfo2.setText("\n1. ritwick@upi\n2. 9791145969@upi\n3. ritwick7@ybl");

                }
            }
        });


    }

    public void donatebuttonclicked(View v)
    {
        if(rbid==R.id.paypalrb)
        {
            String url = "https://www.paypal.me/RitwickVerma/20inr";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            try
            {
                startActivity(i);
            }catch(Exception e){
                Toast.makeText(Donatepage.this,"No Browser installed",Toast.LENGTH_SHORT).show();
            }
        }

        else if(rbid==R.id.paytmrb)
        {
            try
            {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setComponent(new ComponentName("net.one97.paytm", "net.one97.paytm.AJRJarvisSplash"));
                startActivity(intent);

                ClipData clip = ClipData.newPlainText("paytm", "9791145969");
                clipboard.setPrimaryClip(clip);
                Toast.makeText(Donatepage.this, "Number copied to clipboard", Toast.LENGTH_SHORT).show();

            }
            catch(Exception e)
            {
                Snackbar.make(findViewById(R.id.donatepage),"Paytm not installed",Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.parseColor("#2196F3"))
                        .setAction("Install", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("market://details?id=net.one97.paytm"));
                        startActivity(intent);
                    }
                }).show();
            }
        }

        else if(rbid==R.id.upirb)
        {
            try
            {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setComponent(new ComponentName("in.org.npci.upiapp", "in.org.npci.upiapp.HomeActivity"));
                startActivity(intent);
                ClipData clip = ClipData.newPlainText("upi", "ritwick@upi");
                clipboard.setPrimaryClip(clip);
                Toast.makeText(Donatepage.this, "Payment address copied to clipboard", Toast.LENGTH_SHORT).show();
            }
            catch(Exception e)
            {
                Snackbar.make(findViewById(R.id.donatepage),"BHIM not installed. Use other upi app.",Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.parseColor("#2196F3"))
                        .setAction("Install", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("market://details?id=in.org.npci.upiapp"));
                                startActivity(intent);
                            }
                        }).show();
            }
        }

        else
        {
            Toast.makeText(Donatepage.this,"No donation method selected",Toast.LENGTH_SHORT).show();
        }
    }

}
