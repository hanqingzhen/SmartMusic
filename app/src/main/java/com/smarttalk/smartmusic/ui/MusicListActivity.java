package com.smarttalk.smartmusic.ui;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.smarttalk.smartmusic.R;
import com.smarttalk.smartmusic.service.MusicService;
import com.smarttalk.smartmusic.utils.AppConstant;
import com.umeng.analytics.MobclickAgent;


public class MusicListActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private SharedPreferences sharedPreferences;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        sharedPreferences = getSharedPreferences(AppConstant.APP_DATE,MODE_PRIVATE);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        position = 1;
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.inflateMenu(R.menu.music_list);
            // Set Navigation Toggle
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            //throw new NullPointerException("Toolbar must be <include> in activity's layout!");
        }
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.app_name);
            }

            @Override
            public void onDrawerClosed(View drawerView) {

                super.onDrawerClosed(drawerView);
                switch (position){
                    case 1:
                        getSupportActionBar().setTitle(R.string.title_all_song);
                        break;
                    case 2:
                        getSupportActionBar().setTitle(R.string.title_my_song);
                        break;
                }
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new MusicMenuFragment()).commit();
        getSupportActionBar().setTitle(R.string.title_all_song);

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
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        changeFragment(position);
        if(mDrawerLayout != null){
            mDrawerLayout.closeDrawers();
        }
        this.position = position;
    }


    //创建一个actionbar的菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.music_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_exit) {
            //退出程序，将播放状态保存为false
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isPlaying",false);
            editor.commit();
            Intent intent = new Intent(MusicListActivity.this, MusicService.class);
            stopService(intent);
            System.exit(0);
        }
        return super.onOptionsItemSelected(item);
    }
    private void changeFragment(int position) {
        FragmentManager manager = getSupportFragmentManager();
        switch (position) {
            case 1:
                manager.beginTransaction().replace(R.id.container ,new MusicMenuFragment()).commit();
                break;
            case 2:
                manager.beginTransaction().replace(R.id.container , new MusicMenuFragment()).commit();
                break;
            case 3:
                startActivity(new Intent(this,AboutActivity.class));
                break;
        }

    }
}
