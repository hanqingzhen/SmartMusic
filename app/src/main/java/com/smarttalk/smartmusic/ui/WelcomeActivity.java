package com.smarttalk.smartmusic.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.smarttalk.smartmusic.R;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by panl on 14/10/31.
 */
public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        MobclickAgent.setDebugMode( true );
        MobclickAgent.updateOnlineConfig(this);
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
