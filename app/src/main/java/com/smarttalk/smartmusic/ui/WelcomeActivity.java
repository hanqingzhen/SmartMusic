package com.smarttalk.smartmusic.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.smarttalk.smartmusic.R;

/**
 * Created by panl on 14/10/31.
 */
public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this,MusicListActivity.class);
                startActivity(intent);
                WelcomeActivity.this.finish();
            }
        },1500);
    }
}
