package com.smarttalk.smartmusic.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by panl on 14/10/28.
 * 用于从数据库查询歌曲信息的工具类
 */
public class MediaUtil {
    private static SharedPreferences sharedPreferences;
    /**
     * 获取SD卡的音乐信息添加到List中
     * @param context
     * @return musicInfoList
     */
    public static List<MusicInfo> getMusicInfo(Context context){
        List<MusicInfo> musicInfoList = new ArrayList<MusicInfo>();
        /**
         * 用于获取SD卡上的音乐文件
         * Cursor  query(Uri uri, String[] projection,
         * String selection, String[] selectionArgs, String sortOrder)；
         * Uri：指明要查询的数据库名称加上表的名称，从MediaStore中我们可以找到相应信息的参数，具体请参考开发文档。
         * Projection: 指定查询数据库表中的哪几列，返回的游标中将包括相应的信息。Null则返回所有信息。
         * selection: 指定查询条件
         * selectionArgs：参数selection里有 ？这个符号是，这里可以以实际值代替这个问号。如果selection这个没有？的话，
         * 那么这个String数组可以为null。
         * SortOrder：指定查询结果的排列顺序
         */
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,null,null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        for(int i = 0;i < cursor.getCount();i++){
            cursor.moveToNext();
            MusicInfo musicInfo = new MusicInfo();
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
            if( isMusic!=0 && size> 1000000 ){
                musicInfo.setMusicId(id);
                musicInfo.setMusicTitle(title);
                musicInfo.setMusicArtist(artist);
                musicInfo.setMusicPath(path);
                musicInfo.setMusicSize(size);
                musicInfo.setMusicDuration(duration);
                musicInfoList.add(musicInfo);
            }
        }
        return musicInfoList;
    }
    public static List<MusicInfo> getFavoriteMusicInfo(Context context){
        List<MusicInfo> favoriteMusicInfoList = new ArrayList<MusicInfo>();
        /**
         * 用于获取SD卡上的收藏的音乐文件
         * Cursor  query(Uri uri, String[] projection,
         * String selection, String[] selectionArgs, String sortOrder)；
         * Uri：指明要查询的数据库名称加上表的名称，从MediaStore中我们可以找到相应信息的参数，具体请参考开发文档。
         * Projection: 指定查询数据库表中的哪几列，返回的游标中将包括相应的信息。Null则返回所有信息。
         * selection: 指定查询条件
         * selectionArgs：参数selection里有 ？这个符号是，这里可以以实际值代替这个问号。如果selection这个没有？的话，
         * 那么这个String数组可以为null。
         * SortOrder：指定查询结果的排列顺序
         */
        sharedPreferences = context.getSharedPreferences(AppConstant.APP_DATE,context.MODE_PRIVATE);
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,null,null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        for(int i = 0;i < cursor.getCount();i++){
            cursor.moveToNext();
            MusicInfo musicInfo = new MusicInfo();
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
            boolean favoriteState = sharedPreferences.getBoolean(AppConstant.FAVORITE_STATE+id,false);
            if( isMusic!=0 && size> 1000000 && favoriteState){
                musicInfo.setMusicId(id);
                musicInfo.setMusicTitle(title);
                musicInfo.setMusicArtist(artist);
                musicInfo.setMusicPath(path);
                musicInfo.setMusicSize(size);
                musicInfo.setMusicDuration(duration);
                favoriteMusicInfoList.add(musicInfo);
            }
        }
        return favoriteMusicInfoList;
    }

    /**
     * 格式化时间，将毫秒转化为 分：秒 格式
     * @param time
     * @return
     */
    public static String formatTime(long time){
        String min = time / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";
        if (min.length()<2){
            min = "0" + time / (1000 * 60) + "";
        }else {
            min = time / (1000 * 60) + "";
        }
        if (sec.length() == 4) {
            sec = "0" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 3) {
            sec = "00" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 2) {
            sec = "000" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 1) {
            sec = "0000" + (time % (1000 * 60)) + "";
        }
        return min + ":" + sec.trim().substring(0, 2);
    }

    /**
     *
     * 获取音乐文件信息放入List<HashMap>中
     * @param musicInfoList
     * @return
     */
    public static List<HashMap<String,String>> getMusicList(List<MusicInfo> musicInfoList){

        List<HashMap<String,String>> allMusicList = new ArrayList<HashMap<String, String>>();

        for ( Iterator iterator = musicInfoList.iterator();iterator.hasNext();){
            MusicInfo musicInfo = (MusicInfo)iterator.next();
            HashMap<String,String> map = new HashMap<String, String>();
            map.put("id",String.valueOf(musicInfo.getMusicId()));
            map.put("title",musicInfo.getMusicTitle());
            map.put("artist",musicInfo.getMusicArtist());
            map.put("duration",String.valueOf(musicInfo.getMusicDuration()));
            map.put("path",musicInfo.getMusicPath());
            map.put("size",String.valueOf(musicInfo.getMusicSize()));
            allMusicList.add(map);
        }

        return allMusicList;
    }
}
