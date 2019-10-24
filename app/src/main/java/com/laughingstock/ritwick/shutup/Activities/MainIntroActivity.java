package com.laughingstock.ritwick.shutup.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.laughingstock.ritwick.shutup.R;



public class MainIntroActivity extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("shutupsharedpref", MODE_PRIVATE);
        if(preferences.getBoolean("first_install",true))
            startIntro();
        else
        {
            Intent launchmainactivity=new Intent(MainIntroActivity.this, MainActivity.class);
            launchmainactivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(launchmainactivity);
            finish();
        }

    }


    public void startIntro() {

        setButtonBackFunction(BUTTON_BACK_FUNCTION_BACK);

        addSlide(new FragmentSlide.Builder()
                .background(R.color.colorintro1background)
                .backgroundDark(R.color.colorintro1button)
                .fragment(R.layout.fragment_intro_slide_custom1)
                .canGoBackward(false)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title("Hey Human!")
                .description("Use me to control your calls with a wave.\nI can schedule calls too if you ask me politely.\nBundle of little useful utilities")
                .image(R.drawable.hello)
                .background(R.color.colorintro2background)
                .backgroundDark(R.color.colorintro2button)
                .scrollable(false)
                //.permissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS})
                .build());

        addSlide(new SimpleSlide.Builder()
                .title("Can I?")
                .description("Control your phone calls\n" + "Read your contacts\n" + "Enslave humans and rule the planet")
                .image(R.drawable.please)
                .background(R.color.colorintro3background)
                .backgroundDark(R.color.colorintro3button)
                .scrollable(false)
                .permissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS,
                        Manifest.permission.ANSWER_PHONE_CALLS})
                .build());


        addSlide(new FragmentSlide.Builder()
                .background(R.color.colorintro4background)
                .backgroundDark(R.color.colorintro4button)
                .fragment(R.layout.fragment_intro_slide_custom4)
                .canGoForward(false)
                .buttonCtaLabel("Lets Go!")
                .buttonCtaClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent launchmainactivity=new Intent(MainIntroActivity.this, MainActivity.class);
                        launchmainactivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(launchmainactivity);
                        finish();
                    }
                })
                .build());


        addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int position) {
            }

            @Override
            public void onPageSelected(int position) {
                setButtonNextVisible(position!=3);
                setButtonBackVisible(position!=0);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
        });
    }
}