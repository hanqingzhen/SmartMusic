package com.smarttalk.smartmusic.utils;

import java.io.Serializable;

/**
 * Created by panl on 14/10/28.
 */
public class MusicInfo implements Serializable {
    private static final long serializableVersion = 1L;
    private long musicId;                  //音乐id
    private String musicPath = null;       //音乐路径
    private String musicTitle = null;      //音乐标题
    private String musicArtist = null;     //艺术家
    private long musicDuration;            //音乐时长
    private long musicSize;                //音乐大小
    public MusicInfo(){
        super();
    }

    public void setMusicId(long musicId){
        this.musicId = musicId;
    }
    public void setMusicPath(String musicPath){
        this.musicPath = musicPath;
    }
    public void setMusicTitle(String musicTitle){
        this.musicTitle = musicTitle;
    }
    public void setMusicArtist(String musicArtist){
        this.musicArtist = musicArtist;
    }
    public void setMusicDuration(long musicDuration){
        this.musicDuration = musicDuration;
    }
    public void setMusicSize(long musicSize){
        this.musicSize = musicSize;
    }

    public long getMusicId(){
        return musicId;
    }
    public String getMusicPath(){
        return musicPath;
    }
    public String getMusicTitle(){
        return musicTitle;
    }
    public String getMusicArtist(){
        return musicArtist;
    }
    public long getMusicDuration(){
        return musicDuration;
    }
    public long getMusicSize(){
        return musicSize;
    }

    @Override
    public String toString() {
        return "MusicInfo [musicId="+musicId+",musicPath="+musicPath+",musicTitle="+musicTitle
                +",musicArtist="+musicArtist+",musicDuration="+musicDuration+",musicSize="+musicSize
                +"]";
    }
}
