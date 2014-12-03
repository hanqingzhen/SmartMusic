package com.smarttalk.smartmusic.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;

import com.smarttalk.smartmusic.R;
import com.smarttalk.smartmusic.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by panl on 14/11/3.
 */
public class AboutActivity extends ActionBarActivity {
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            //UIUtils.setSystemBarTintColor(this);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            UIUtils.setSystemBarTintColor(this);
//
//        }
        setContentView(R.layout.activity_about);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            // Set Navigation Toggle
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            //throw new NullPointerException("Toolbar must be <include> in activity's layout!");
        }


    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
