package com.laughingstock.ritwick.shutup.Activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.laughingstock.ritwick.shutup.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Donatepage extends AppCompatActivity
{
    ClipboardManager clipboard;
    long rbid;

    @BindView(R.id.neededinfotext)
    TextView neededinfo;
    @BindView(R.id.needinfo2text)
    TextView needinfo2;
    @BindView(R.id.donaterg)
    RadioGroup donaterg;
    @BindView(R.id.donatepage)
    ConstraintLayout viewroot;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTitle("Donate");
        setContentView(R.layout.activity_donatepage1);
        ButterKnife.bind(this);

        //viewroot = (ConstraintLayout) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);

        ConstraintSet animatedview = new ConstraintSet();
        animatedview.clone(this, R.layout.activity_donatepage2);

        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        donaterg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId)
            {
                TransitionManager.beginDelayedTransition(viewroot);
                animatedview.applyTo(viewroot);
                rbid = checkedId;
                if (rbid == R.id.paypalrb)
                {
                    neededinfo.setText("You'll need a PayPal account for that. I hope that's not a problem.");
                    needinfo2.setText("");
                } else if (rbid == R.id.paytmrb)
                {
                    neededinfo.setText("Donate money on number 9791145969 through Paytm.");
                    needinfo2.setText("");

                } else if (rbid == R.id.upirb)
                {
                    neededinfo.setText("Use apps like BHIM or PhonePe\nYou can use any of the following virtual payment addresses:");
                    needinfo2.setText("\n1. ritwick@upi\n2. 9791145969@upi\n3. ritwick7@ybl");

                }
            }
        });


    }

    public void donatebuttonclicked(View v)
    {
        if (rbid == R.id.paypalrb)
        {
            String url = "https://www.paypal.me/RitwickVerma/20inr";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            try
            {
                startActivity(i);
            } catch (Exception e)
            {
                Toast.makeText(Donatepage.this, "No Browser installed", Toast.LENGTH_SHORT).show();
            }
        } else if (rbid == R.id.paytmrb)
        {
            try
            {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setComponent(new ComponentName("net.one97.paytm", "net.one97.paytm.AJRJarvisSplash"));
                startActivity(intent);

                ClipData clip = ClipData.newPlainText("paytm", "9791145969");
                clipboard.setPrimaryClip(clip);
                Toast.makeText(Donatepage.this, "Number copied to clipboard", Toast.LENGTH_SHORT).show();

            } catch (Exception e)
            {
                Snackbar.make(findViewById(R.id.donatepage), "Paytm not installed", Snackbar.LENGTH_LONG)
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
        } else if (rbid == R.id.upirb)
        {
            try
            {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setComponent(new ComponentName("in.org.npci.upiapp", "in.org.npci.upiapp.HomeActivity"));
                startActivity(intent);
                ClipData clip = ClipData.newPlainText("upi", "ritwick@upi");
                clipboard.setPrimaryClip(clip);
                Toast.makeText(Donatepage.this, "Payment address copied to clipboard", Toast.LENGTH_SHORT).show();
            } catch (Exception e)
            {
                Snackbar.make(findViewById(R.id.donatepage), "BHIM not installed. Use other upi app.", Snackbar.LENGTH_LONG)
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
        } else
        {
            Toast.makeText(Donatepage.this, "No donation method selected", Toast.LENGTH_SHORT).show();
        }
    }

}
