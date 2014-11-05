package com.smarttalk.smartmusic.ui;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.smarttalk.smartmusic.R;
import com.smarttalk.smartmusic.service.MusicService;
import com.smarttalk.smartmusic.utils.AppConstant;
import com.smarttalk.smartmusic.utils.MediaUtil;
import com.smarttalk.smartmusic.utils.MusicInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by panl on 14/10/28.
 */
public class MusicMenuFragment extends Fragment {
    private ListView allMusicListView;
    private List<MusicInfo> musicInfoList;
    private int position;
    private MusicReceiver musicReceiver;
    private TextView playingTitle;
    private Button playAndPause;
    private boolean isPlaying = false;
    private SharedPreferences sharedPreferences;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册Receiver
        musicReceiver = new MusicReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstant.UPDATE_VIEW);

        sharedPreferences = getActivity().getSharedPreferences(AppConstant.APP_DATE,getActivity().MODE_PRIVATE);

        getActivity().registerReceiver(musicReceiver, filter);
    }

    @Override
    public void onStart() {
        super.onStart();
        isPlaying = sharedPreferences.getBoolean("isPlaying",false);
        if (isPlaying)
            playAndPause.setBackgroundResource(R.drawable.btn_pause_normal);
        else
            playAndPause.setBackgroundResource(R.drawable.btn_play_normal);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_list,container,false);
        allMusicListView = (ListView)view.findViewById(R.id.all_music_list_view);
        playingTitle = (TextView)view.findViewById(R.id.title_playing);
        playAndPause = (Button)view.findViewById(R.id.playing_button);

        musicInfoList = new ArrayList<MusicInfo>();
        if (checkSDCard()) {
            position = sharedPreferences.getInt("lastPosition",0);
            musicInfoList = MediaUtil.getMusicInfo(getActivity());
            playingTitle.setText(musicInfoList.get(position).getMusicTitle());
            SimpleAdapter adapter = new SimpleAdapter(getActivity(), MediaUtil.getMusicList(getActivity()),
                    R.layout.content_music_list,
                    new String[]{"title", "artist"},
                    new int[]{R.id.music_title, R.id.music_artist});
            allMusicListView.setAdapter(adapter);
            allMusicListView.setOnItemClickListener(new AllMusicListListener());
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
        }else {
            Toast.makeText(getActivity(),"没有SD卡哦！！！",Toast.LENGTH_SHORT).show();
        }
        return view;
    }
    public void updateView(int position){
        playingTitle.setText(musicInfoList.get(position).getMusicTitle());
        if (isPlaying)
            playAndPause.setBackgroundResource(R.drawable.btn_pause_normal);
        else
            playAndPause.setBackgroundResource(R.drawable.btn_play_normal);
    }

    public class AllMusicListListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int selectPosition, long id) {
           if(musicInfoList!=null){
               updateView(selectPosition);
               MusicInfo musicInfo = musicInfoList.get(selectPosition);
               Log.i("musicInfo---->",musicInfo.toString());
               Intent intent = new Intent(getActivity(), MusicPlayingActivity.class);
               position = selectPosition;
               intent.putExtra("position",position);
               intent.putCharSequenceArrayListExtra("musicInfoList",(ArrayList)musicInfoList);
               playService(AppConstant.MEDIA_PLAY);
               getActivity().startActivity(intent);
               getActivity().overridePendingTransition(R.anim.activity_open,0);
           }
        }
    }

    /**
     * 检查SD卡是否存在
     * @return
     */
    private boolean checkSDCard()
    {
        if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }
    /**
     * 接受service发出的广播
     */
    public class MusicReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(AppConstant.UPDATE_VIEW)){
                position = intent.getIntExtra("position",0);
                updateView(position);

            }
        }
    }
    private void playService(int i){
        Intent serviceIntent = new Intent(getActivity(), MusicService.class);
        serviceIntent.putExtra("position",position);
        serviceIntent.putCharSequenceArrayListExtra("musicInfoList", (ArrayList) musicInfoList);
        serviceIntent.putExtra("MSG",i);
        getActivity().startService(serviceIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(musicReceiver);
    }
}
