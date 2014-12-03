package com.smarttalk.smartmusic.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.smarttalk.smartmusic.R;
import com.smarttalk.smartmusic.service.MusicService;
import com.smarttalk.smartmusic.ui.FavoriteFragment;
import com.smarttalk.smartmusic.ui.MusicPlayingActivity;
import com.smarttalk.smartmusic.utils.AppConstant;
import com.smarttalk.smartmusic.utils.MusicInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by panl on 14/11/18.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private List<MusicInfo> favoriteMusicList;
    private Context context;

    public RecyclerAdapter(List<MusicInfo> favoriteMusicList,Context context){
        this.favoriteMusicList = favoriteMusicList;
        this.context = context;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView titleText,artistText;
        public ViewHolder(View itemView) {
            super(itemView);
            titleText = (TextView)itemView.findViewById(R.id.music_title);
            artistText = (TextView)itemView.findViewById(R.id.music_artist);

        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        viewHolder.titleText.setText(favoriteMusicList.get(i).getMusicTitle());
        viewHolder.artistText.setText(favoriteMusicList.get(i).getMusicArtist());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"viewholder clicked",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, MusicPlayingActivity.class);
                intent.putExtra("position",i);
                intent.putCharSequenceArrayListExtra("musicInfoList",(ArrayList)favoriteMusicList);
                playService(AppConstant.MEDIA_PLAY,i);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return favoriteMusicList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.content_music_list,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }
    private void playService(int i,int position){
        Intent serviceIntent = new Intent(context, MusicService.class);
        serviceIntent.putExtra("position",position);
        serviceIntent.putCharSequenceArrayListExtra("musicInfoList", (ArrayList) favoriteMusicList);
        serviceIntent.putExtra("MSG",i);
        context.startService(serviceIntent);
    }
}
