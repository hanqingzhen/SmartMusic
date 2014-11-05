package com.smarttalk.smartmusic.ui;

import android.app.Activity;
import android.os.Bundle;
import com.smarttalk.smartmusic.R;

/**
 * Created by panl on 14/11/3.
 */
public class AboutActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setTitle("关于SmartMusic");


    }
}
