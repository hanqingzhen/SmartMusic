package com.smarttalk.smartmusic.ui;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.WindowManager;
import android.widget.TextView;

import com.smarttalk.smartmusic.R;
import com.smarttalk.smartmusic.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by panl on 14/11/3.
 */
public class AboutActivity extends ActionBarActivity {
    private Toolbar mToolbar;
    private TextView versionText;
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
        versionText = (TextView)findViewById(R.id.version_text);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            // Set Navigation Toggle
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            //throw new NullPointerException("Toolbar must be <include> in activity's layout!");
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
