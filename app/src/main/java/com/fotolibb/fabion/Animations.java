package com.fotolibb.fabion;

import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

/**
 * Created by Libb on 26.10.2017.
 */

public class Animations {
    public static Animation animZprava() {
        Animation zprava = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        zprava.setDuration(250);
        zprava.setInterpolator(new LinearInterpolator());
        return zprava;
    }

    public static Animation animZlava() {
        Animation zlava = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        zlava.setDuration(250);
        zlava.setInterpolator(new LinearInterpolator());
        return zlava;
    }

    public static Animation animZprava1() {
        Animation zprava = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        zprava.setDuration(250);
        zprava.setInterpolator(new LinearInterpolator());
        return zprava;
    }

    public static Animation animZlava1() {
        Animation zlava = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        zlava.setDuration(250);
        zlava.setInterpolator(new LinearInterpolator());
        return zlava;
    }
}
