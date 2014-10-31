package com.smarttalk.smartmusic.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.smarttalk.smartmusic.R;
import com.smarttalk.smartmusic.service.MusicService;
import com.smarttalk.smartmusic.utils.AppConstant;
import com.smarttalk.smartmusic.utils.MediaUtil;
import com.smarttalk.smartmusic.utils.MusicInfo;

import java.util.ArrayList;
import java.util.List;




/**
 * Created by panl on 14/10/30.
 */
public class MusicPlayingActivity extends Activity {
    private TextView artistText,currentTimeText,totalTimeText;
    private Button previousButton,playAndPauseButton,nextButton,repeatStateButton;
    private SeekBar seekBar;
    private List<MusicInfo> musicInfoList;
    private static int musicNum;
    private int position;
    private int repeatState;
    private boolean isPlaying = true;
    private static int seekBarProgress;
    private long offset = 0;
    private long begain = 0;
    private long pauseTimeMills;
    private SharedPreferences sharedPreferences;


    private final static int PREVIOUS = 1;
    private final static int NEXT = 2;


    private UpdateTimeCallback updateTimeCallback = null;
    private Handler handler = new Handler();
    private MusicReceiver musicReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_playing);
        initView();
        setViewText(position);
        setListener();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }
    private void setViewText(int position){
        MusicInfo musicInfo = musicInfoList.get(position);
        artistText.setText(musicInfo.getMusicArtist());
        totalTimeText.setText(MediaUtil.formatTime(musicInfoList.get(position).getMusicDuration()));
        playAndPauseButton.setText("暂停");
        getActionBar().setTitle(musicInfo.getMusicTitle());
    }

    /**
     * 初始化view控件
     */
    private void initView(){
        artistText = (TextView)findViewById(R.id.artist_text);
        previousButton = (Button)findViewById(R.id.previous_button);
        playAndPauseButton = (Button)findViewById(R.id.play_and_pause_button);
        nextButton = (Button)findViewById(R.id.next_button);
        repeatStateButton = (Button)findViewById(R.id.repeat_state_button);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        currentTimeText = (TextView)findViewById(R.id.current_time_text);
        totalTimeText = (TextView)findViewById(R.id.total_time_text);

        sharedPreferences = getSharedPreferences(AppConstant.APP_DATE,MODE_PRIVATE);

        repeatState = sharedPreferences.getInt("repeatState",AppConstant.allRepeat);
        switch (repeatState){
            case AppConstant.allRepeat:
                repeatStateButton.setText("列表循环");
                break;
            case AppConstant.randomRepeat:
                repeatStateButton.setText("随机播放");
                break;
            case AppConstant.singleRepeat:
                repeatStateButton.setText("单曲循环");
                break;
        }

        Intent intent = this.getIntent();
        position = intent.getIntExtra("position",0);
        musicInfoList = (List)intent.getCharSequenceArrayListExtra("musicInfoList");
        musicNum = musicInfoList.size();
        begain = System.currentTimeMillis();
        updateTimeCallback = new UpdateTimeCallback(0);
        handler.post(updateTimeCallback);
        playService(AppConstant.MEDIA_PLAY);
        musicReceiver = new MusicReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstant.UPDATE_VIEW);
        registerReceiver(musicReceiver,filter);
        getActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * 启动service，播放相应的歌曲
     * @param i
     */
    private void playService(int i){
        Intent serviceIntent = new Intent(MusicPlayingActivity.this, MusicService.class);
        serviceIntent.putExtra("position",position);
        serviceIntent.putCharSequenceArrayListExtra("musicInfoList", (ArrayList) musicInfoList);
        serviceIntent.putExtra("MSG",i);
        startService(serviceIntent);
    }

    private void playService(int i,int progress){
        Intent serviceIntent = new Intent(MusicPlayingActivity.this, MusicService.class);
        serviceIntent.putExtra("MSG",i);
        serviceIntent.putExtra("position",position);
        serviceIntent.putCharSequenceArrayListExtra("musicInfoList", (ArrayList) musicInfoList);
        serviceIntent.putExtra("progress",progress);
        startService(serviceIntent);
    }

    /**
     * 获得歌曲的位置，播放对应位置的歌曲
     * @param flag
     */
    private void getMusicPositon(int flag){
        if (flag == PREVIOUS){
            if (position == 0){
                position = musicNum - 1;
            }else {
                position -= 1;
            }
        }else if (flag == NEXT){
            if (position == musicNum -1){
                position = 0;
            }else {
                position += 1;
            }
        }
    }
    /**
     * 给按钮设置监听器，所需要执行的操作
     */
    private void setListener(){
        //上一曲
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMusicPositon(PREVIOUS);
                if (isPlaying)
                    playService(AppConstant.MEDIA_PLAY);
                else
                    playService(AppConstant.MEDIA_NEXT);

                updateTimeCallback = new UpdateTimeCallback(0);
                begain = System.currentTimeMillis();
                handler.post(updateTimeCallback);
                setViewText(position);
            }
        });
        //下一曲
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMusicPositon(NEXT);
                if (isPlaying)
                    playService(AppConstant.MEDIA_PLAY);
                else
                    playService(AppConstant.MEDIA_NEXT);

                updateTimeCallback = new UpdateTimeCallback(0);
                begain = System.currentTimeMillis();
                handler.post(updateTimeCallback);
                setViewText(position);
            }
        });
        //实现音乐的暂停和播放
        playAndPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    playService(AppConstant.MEDIA_PAUSE);
                    handler.removeCallbacks(updateTimeCallback);
                    pauseTimeMills = System.currentTimeMillis();
                    playAndPauseButton.setText("播放");
                }else {
                    playService(AppConstant.MEDIA_CONTINUE);
                    begain = System.currentTimeMillis() - pauseTimeMills + begain;
                    handler.post(updateTimeCallback);
                    playAndPauseButton.setText("暂停");
                }
                isPlaying = isPlaying?false:true;
            }
        });
        //实现进度条随音乐播放移动，实现快进，快退功能
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser == true){
                    Log.i("Progress------>",progress+"");
                    progress = (int)(progress*musicInfoList.get(position).getMusicDuration())/100;
                    seekBarProgress = progress;

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                playService(AppConstant.MEDIA_SEEKTO,seekBarProgress);
                updateTimeCallback = new UpdateTimeCallback(seekBarProgress);
                handler.post(updateTimeCallback);
            }
        });
        //切换歌曲循环状态
        repeatStateButton.setOnClickListener(new View.OnClickListener() {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            @Override
            public void onClick(View v) {
                if (repeatState == AppConstant.allRepeat){
                    repeatState = AppConstant.randomRepeat;
                    repeatStateButton.setText("随机播放");
                    editor.putInt("repeatState",repeatState);
                    editor.commit();
                }else if (repeatState == AppConstant.randomRepeat){
                    repeatState = AppConstant.singleRepeat;
                    repeatStateButton.setText("单曲循环");
                    editor.putInt("repeatState",repeatState);
                    editor.commit();
                }else if (repeatState == AppConstant.singleRepeat){
                    repeatState = AppConstant.allRepeat;
                    repeatStateButton.setText("列表循环");
                    editor.putInt("repeatState",repeatState);
                    editor.commit();
                }

            }
        });
    }

    /**
     * 接受service发出的广播
     */
    public class MusicReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(AppConstant.UPDATE_VIEW)){
                position = intent.getIntExtra("position",0);
                setViewText(position);
                handler.removeCallbacks(updateTimeCallback);
                begain = System.currentTimeMillis();
                updateTimeCallback = new UpdateTimeCallback(0);
                handler.post(updateTimeCallback);
            }
        }
    }

    /**
     * 更新进度条
     */
    class UpdateTimeCallback implements Runnable{

        int seekTo = 0;
        public UpdateTimeCallback(int seekTo){
            this.seekTo = seekTo;
        }
        @Override
        public void run() {
            //计算当前歌曲已经播放的时间
            offset = System.currentTimeMillis() - begain + seekTo;
            try {
                if (offset <= musicInfoList.get(position).getMusicDuration()){
                    //更新进度条
                    seekBarProgress = (int)Math.floor(
                            offset*100/musicInfoList.get(position).getMusicDuration());
                    seekBar.setProgress(seekBarProgress);
                    currentTimeText.setText(MediaUtil.formatTime(offset));
                }
            }catch (Exception e){

            }
            handler.postDelayed(updateTimeCallback,10);

        }
    }



}
