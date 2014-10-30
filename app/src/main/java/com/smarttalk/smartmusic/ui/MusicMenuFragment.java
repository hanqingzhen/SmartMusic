package com.smarttalk.smartmusic.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.smarttalk.smartmusic.R;
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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_list,container,false);
        allMusicListView = (ListView)view.findViewById(R.id.all_music_list_view);
        musicInfoList = new ArrayList<MusicInfo>();
        musicInfoList = MediaUtil.getMusicInfo(getActivity());

        SimpleAdapter adapter = new SimpleAdapter(getActivity(), MediaUtil.getMusicList(getActivity()),
                R.layout.content_music_list,
                new String[]{"title","artist"},
                new int[]{R.id.music_title,R.id.music_artist});
        allMusicListView.setAdapter(adapter);
        allMusicListView.setOnItemClickListener(new AllMusicListListener());
        return view;
    }

    public class AllMusicListListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
           if(musicInfoList!=null){
               MusicInfo musicInfo = musicInfoList.get(position);
               Log.i("musicInfo---->",musicInfo.toString());
           }
        }
    }
}
