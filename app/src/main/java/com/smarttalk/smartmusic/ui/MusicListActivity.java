package com.smarttalk.smartmusic.ui;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.smarttalk.smartmusic.R;
import com.smarttalk.smartmusic.service.MusicService;
import com.smarttalk.smartmusic.utils.AppConstant;
import com.smarttalk.smartmusic.utils.MusicInfo;
import com.smarttalk.smartmusic.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;


public class MusicListActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        MusicMenuFragment.AllMusicCallbacks{

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private SharedPreferences sharedPreferences;
    private TextView playingTitle;
    private ImageButton playAndPause;
    private int position;
    private int playPosition;
    private boolean isPlaying = false;
    private MusicReceiver musicReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            //UIUtils.setSystemBarTintColor(this);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            UIUtils.setSystemBarTintColor(this);
//
//        }
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

        playingTitle = (TextView)findViewById(R.id.title_playing);
        playAndPause = (ImageButton)findViewById(R.id.playing_button);
        //注册Receiver
        musicReceiver = new MusicReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstant.UPDATE_VIEW);

        sharedPreferences = this.getSharedPreferences(AppConstant.APP_DATE,this.MODE_PRIVATE);

        this.registerReceiver(musicReceiver, filter);
        playAndPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying){
                    playService(AppConstant.MEDIA_PAUSE);
                    playAndPause.setBackgroundResource(R.drawable.btn_play_normal);
                }
                else{
                    playService(AppConstant.MEDIA_CONTINUE);
                    playAndPause.setBackgroundResource(R.drawable.btn_pause_normal);
                }

                isPlaying = isPlaying?false:true;

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        isPlaying = sharedPreferences.getBoolean("isPlaying",false);
        if (isPlaying)
            playAndPause.setBackgroundResource(R.drawable.btn_pause_normal);
        else
            playAndPause.setBackgroundResource(R.drawable.btn_play_normal);
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

    @Override
    public void onListItemCliceked(int position, List<MusicInfo> musicInfoList) {
        playingTitle.setText(musicInfoList.get(position).getMusicTitle());
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
                manager.beginTransaction().replace(R.id.container , new FavoriteFragment()).commit();
                break;
            case 3:
                startActivity(new Intent(this,AboutActivity.class));
                break;
        }

    }
    /**
     * 接受service发出的广播
     */
    public class MusicReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(AppConstant.UPDATE_VIEW)){
                playPosition = intent.getIntExtra("position",0);
                updateView(playPosition);

            }
        }
    }
    public void updateView(int position){
        //playingTitle.setText(musicInfoList.get(position).getMusicTitle());
        if (isPlaying)
            playAndPause.setBackgroundResource(R.drawable.btn_pause_normal);
        else
            playAndPause.setBackgroundResource(R.drawable.btn_play_normal);
    }
    private void playService(int i){
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("position",playPosition);
        //serviceIntent.putCharSequenceArrayListExtra("musicInfoList", (ArrayList) musicInfoList);
        serviceIntent.putExtra("MSG",i);
        this.startService(serviceIntent);
    }
}
