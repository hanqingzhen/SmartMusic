package com.smarttalk.smartmusic.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.smarttalk.smartmusic.ui.MusicMenuFragment;
import com.smarttalk.smartmusic.ui.MusicPlayingActivity;
import com.smarttalk.smartmusic.utils.AppConstant;
import com.smarttalk.smartmusic.utils.MediaUtil;
import com.smarttalk.smartmusic.utils.MusicInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by panl on 14/10/29.
 */
public class MusicService extends Service {

    private MediaPlayer mediaPlayer;
    private int position;
    private List<MusicInfo> musicInfoList;
    private MusicInfo musicInfo;
    private int repeatState;
    private SharedPreferences sharedPreferences;


    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MusicPlayCompleteListener());
        sharedPreferences = getSharedPreferences(AppConstant.APP_DATE,MODE_PRIVATE);

        //MusicInfo musicInfo = musicInfoList.get(position);
        //playMusic(musicInfo);
        //Toast.makeText(this,"service start",Toast.LENGTH_SHORT).show();
    }


    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        repeatState = sharedPreferences.getInt("repeatState",AppConstant.allRepeat);
        position = intent.getIntExtra("position",0);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putInt("lastPosition",position);
        editor.commit();
        Log.i("repeatState---->",repeatState+"");
        musicInfoList = (List)intent.getCharSequenceArrayListExtra("musicInfoList");
        musicInfo = musicInfoList.get(position);
        if (musicInfo != null){
            int MSG = intent.getIntExtra("MSG",0);
            Log.i("MSG--->",MSG+"");
            switch (MSG){
                case AppConstant.MEDIA_PLAY:
                    playMusic(musicInfo);
                    break;
                case AppConstant.MEDIA_PAUSE:
                    pauseMusic();
                    break;
                case AppConstant.MEDIA_NEXT:
                    playMusic(musicInfo);
                    pauseMusic();
                    break;
                case AppConstant.MEDIA_SEEKTO:
                    int progress = intent.getIntExtra("progress",0);
                    mediaPlayer.seekTo(progress);
                    continueMusic();
                    break;
                case AppConstant.MEDIA_CONTINUE:
                    continueMusic();
                    break;
            }
        }
        //playMusic(musicInfo);
        Log.i("posituon--->",position+"");
        Log.i("musicInfoList--->",musicInfo.toString());
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMusic();
        mediaPlayer.release();
    }

    public void playMusic(MusicInfo musicInfo) {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(musicInfo.getMusicPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void pauseMusic(){
        mediaPlayer.pause();
    }

    public void stopMusic(){
        mediaPlayer.stop();
    }

    public void continueMusic(){mediaPlayer.start();}

    public class MusicPlayCompleteListener implements MediaPlayer.OnCompletionListener{
        @Override
        public void onCompletion(MediaPlayer mp) {
            switch (repeatState) {
                case AppConstant.allRepeat:
                    if (position == musicInfoList.size() - 1) {
                        position = 0;
                    } else {
                        position += 1;
                    }
                    break;
                case AppConstant.randomRepeat:
                    position = (int)((musicInfoList.size()-1)*Math.random());
                    break;
                case AppConstant.singleRepeat:
                    break;
            }


            musicInfo = musicInfoList.get(position);
            playMusic(musicInfo);
            Intent sendIntent = new Intent(AppConstant.UPDATE_VIEW);
            sendIntent.putExtra("position",position);
            SharedPreferences.Editor editor= sharedPreferences.edit();
            editor.putInt("lastPosition",position);
            editor.commit();
            sendBroadcast(sendIntent);
            //Log.i("broadcast---->","发送成功");
        }
    }
}
