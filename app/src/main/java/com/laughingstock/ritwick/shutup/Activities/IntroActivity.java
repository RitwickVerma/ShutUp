package com.laughingstock.ritwick.shutup.Activities;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.laughingstock.ritwick.shutup.R;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragmentBuilder;

public class IntroActivity extends MaterialIntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(new IntroSlideCustom1());


        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorintro2background)
                .buttonsColor(R.color.colorintro2button)
                .image(R.drawable.blog)
                .neededPermissions(new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CONTACTS})
                .title("ShutUp!")
                .description("Stay Lazy")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorintro3background)
                .buttonsColor(R.color.colorintro3button)
                .image(R.drawable.blog)
                .title("ShutUp!")
                .description("Stay Lazy")
                .build());
       // hideBackButton();


    }
}
