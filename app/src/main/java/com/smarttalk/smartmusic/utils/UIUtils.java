package com.smarttalk.smartmusic.utils;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;

import com.smarttalk.smartmusic.R;

/**
 * Created by panl on 14/11/19.
 */
public class UIUtils {
    public static void setSystemBarTintColor(Activity activity){
        if(SystemBarTintManager.isKitKat()){
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintDrawable(new ColorDrawable(activity.getResources().
                    getColor(R.color.syetem_bar_color)));
        }
    }
}
