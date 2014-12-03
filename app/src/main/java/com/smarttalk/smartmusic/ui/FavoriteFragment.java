package com.smarttalk.smartmusic.ui;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.smarttalk.smartmusic.R;
import com.smarttalk.smartmusic.adapter.RecyclerAdapter;
import com.smarttalk.smartmusic.service.MusicService;
import com.smarttalk.smartmusic.utils.AppConstant;
import com.smarttalk.smartmusic.utils.MediaUtil;
import com.smarttalk.smartmusic.utils.MusicInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment {
    private RecyclerView favoriteRecycleView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<MusicInfo> favoriteMusicInfoList;
    private int position;
    private MusicReceiver musicReceiver;


    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musicReceiver = new MusicReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstant.UPDATE_VIEW);

        //sharedPreferences = getActivity().getSharedPreferences(AppConstant.APP_DATE,getActivity().MODE_PRIVATE);

        getActivity().registerReceiver(musicReceiver, filter);

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View favoriteView = inflater.inflate(R.layout.fragment_favorite, container, false);
        favoriteMusicInfoList = MediaUtil.getFavoriteMusicInfo(getActivity());
        favoriteRecycleView = (RecyclerView)favoriteView.findViewById(R.id.favorite_Recycler_view);
        favoriteRecycleView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        favoriteRecycleView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerAdapter(favoriteMusicInfoList,getActivity());
        favoriteRecycleView.setAdapter(mAdapter);
        favoriteRecycleView.setItemAnimator(new DefaultItemAnimator());
        Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left);
        fadeIn.setDuration(150);
        LayoutAnimationController layoutAnimationController = new LayoutAnimationController(fadeIn);
        favoriteRecycleView.setLayoutAnimation(layoutAnimationController);
        favoriteRecycleView.startLayoutAnimation();

        return favoriteView;
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
                //updateView(position);

            }
        }
    }
//    private void playService(int i){
//        Intent serviceIntent = new Intent(getActivity(), MusicService.class);
//        serviceIntent.putExtra("position",position);
//        serviceIntent.putCharSequenceArrayListExtra("musicInfoList", (ArrayList) favoriteMusicInfoList);
//        serviceIntent.putExtra("MSG",i);
//        getActivity().startService(serviceIntent);
//    }
//    public void playFavoriteMusic(ActionBarActivity context,int i,List<MusicInfo> favoriteMusicInfoList){
//        if(favoriteMusicInfoList!=null){
//            //updateView(selectPosition);
//            //MusicInfo musicInfo = favoriteMusicInfoList.get(i);
//            //Log.i("musicInfo---->", musicInfo.toString());
//            Intent intent = new Intent(context, MusicPlayingActivity.class);
//            position = i;
//            intent.putExtra("position",position);
//            intent.putCharSequenceArrayListExtra("musicInfoList",(ArrayList)favoriteMusicInfoList);
//            playService(AppConstant.MEDIA_PLAY);
//            context.startActivity(intent);
//            context.overridePendingTransition(R.anim.activity_open, 0);
//        }
//    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(musicReceiver);
    }


}
