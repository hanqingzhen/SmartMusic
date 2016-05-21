package com.smarttalk.smartmusic.ui;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.smarttalk.smartmusic.R;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by panl on 14/11/3.
 */
public class AboutActivity extends BaseActivity {
    private Toolbar mToolbar;
    private TextView versionText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        versionText = (TextView)findViewById(R.id.version_text);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            // Set Navigation Toggle
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        versionText.setText(getVersion());

    }
    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return "当前版本：" + version;
        } catch (Exception e) {
            e.printStackTrace();
            return "当前版本：1.0";
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
