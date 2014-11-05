package com.smarttalk.smartmusic.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by panl on 14/11/4.
 */
public class FileUtil {
    private String SDCardRoot;
    public FileUtil(){
        SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
    }

    /**
     * 将歌词文件写到SD卡中
     * @param path
     * @param inputStream
     * @return
     */

    public File writeToSDFromInput(String path,InputStream inputStream){
        File file = null;
        OutputStream outputStream = null;
        try {
            file = new File(path);
            file.createNewFile();
            outputStream = new FileOutputStream(file);
            byte buffer[] = new byte[4*1024];
            int temp;
            while ((temp = inputStream.read(buffer))!= -1){
                outputStream.write(buffer,0,temp);
            }
            outputStream.flush();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                outputStream.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        Log.i("lrcpath",file.exists()+"");

        return file;
    }

    /**
     * 判断歌词文件是否存在
     * @param lrcPath
     * @return
     */
    public boolean isFileExist(String lrcPath){
        File file = new File(lrcPath);
        return file.exists();
    }
}
