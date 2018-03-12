package com.laughingstock.ritwick.shutup.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.laughingstock.ritwick.shutup.R;

import agency.tango.materialintroscreen.SlideFragment;

/**
 * Created by ritwick on 3/10/18.
 */

public class IntroSlideCustom1 extends SlideFragment
{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_intro_slide_custom1, container, false);


        return view;
    }

    @Override
    public int backgroundColor() {
        return R.color.colorintro1background;
    }

    @Override
    public int buttonsColor() {
        return R.color.colorintro1background;
    }

    @Override
    public boolean canMoveFurther() {
        return true;
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return "lost de way";
    }
}
