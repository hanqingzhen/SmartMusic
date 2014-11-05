package com.smarttalk.smartmusic.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by panl on 14/10/31.
 */
public class LyricView extends View {

    private static TreeMap<Integer,LyricObject> lrc_map;
    private float myX;        //view X轴的中点，此值固定，保持歌词在X中显示
    private float offsetY;    //歌词在Y轴上的偏移量，此值会根据歌词的滚动变小
    private static boolean blLrc = false;
    private float touchY;     //当触摸歌词时，保存为当前触点的Y轴坐标
    private float touchX;
    private boolean blScrollView = false;
    private int lrcIndex = 0;   //保存歌词TreeMap的下标
    private int wordSize = 0;   //显示歌词文字大小的值
    private int interval = 45;  //歌词每行的间隔
    Paint paint = new Paint();  //用于画不是高亮歌词的画笔
    Paint hPaint = new Paint(); //用于画高亮歌词的画笔

    private String lrcNone = "歌词加载中";


    public LyricView(Context context) {
        super(context);
        init();
    }

    public LyricView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (blLrc){
            hPaint.setTextSize(wordSize);
            paint.setTextSize(wordSize);
            LyricObject temp = lrc_map.get(lrcIndex);
            if (temp!= null)
                canvas.drawText(temp.singleLineLrc,myX,offsetY+(wordSize + interval)*lrcIndex,hPaint);
            //画当前歌词之前的歌词
            for (int i = lrcIndex -1;i >= 0;i --){
                temp = lrc_map.get(i);
                if (offsetY+(wordSize + interval)*i < 0)
                    break;
                canvas.drawText(temp.singleLineLrc,myX,offsetY+(wordSize + interval)*i,paint);
            }
            //画当前歌词之后的歌词
            for (int i = lrcIndex + 1;i < lrc_map.size();i ++){
                temp = lrc_map.get(i);
                if (offsetY+(wordSize + interval)*i > 1200)
                    break;
                canvas.drawText(temp.singleLineLrc,myX,offsetY+(wordSize + interval)*i,paint);
            }
        }else {
            paint.setTextSize(50);
            canvas.drawText(lrcNone,myX,640,paint);
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float tt = event.getY();
        if (!blLrc)
            return super.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                touchX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                touchY = tt - touchY;
                offsetY = offsetY + touchY;
                break;
            case MotionEvent.ACTION_UP:
                blScrollView = false;
                break;
        }
        touchY = tt;
        return true;
    }
    public void setText(String string){
        lrcNone = string;
    }

    public void init(){
        lrc_map = new TreeMap<Integer, LyricObject>();
        offsetY = 640;

        paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setAlpha(180);

        hPaint = new Paint();
        hPaint.setTextAlign(Paint.Align.CENTER);
        hPaint.setColor(Color.RED);
        hPaint.setAntiAlias(true);
        hPaint.setAlpha(255);
    }
    /**
     * 根据歌词里面最长的一句确定歌词字体的大小
     */
    public void setTextSize(){
        if(!blLrc)
            return;
        int max = lrc_map.get(0).singleLineLrc.length();
        for (int i = 0;i < lrc_map.size();i ++){
            LyricObject lrcStrLength = lrc_map.get(i);
            if (max < lrcStrLength.singleLineLrc.length())
                max = lrcStrLength.singleLineLrc.length();
        }
        wordSize = 850/max;
    }
    protected void onSizeChanged(int W,int H,int oldW,int oldH){
        myX = W * 0.5f;
        super.onSizeChanged(W,H,oldW,oldH);
    }

    /**
     * 歌词滚动速度
     * @return speed
     */
    public Float speedLrc(){
        float speed = 0;
        if (offsetY+(wordSize+interval)*lrcIndex > 440)
            speed=((offsetY+(wordSize+interval)*lrcIndex-440)/20);
        else if (offsetY+(wordSize+interval)*lrcIndex < 240)
            speed = 0;

        return speed;
    }


    /**
     * 按照当前歌曲的播放时间，从歌词里获得那一句
     * @param time 当前歌曲的播放时间
     * @return 返回当前歌词的索引值
     */
    public int selectIndex(int time){
        if (!blLrc)
            return 0;
        int index = 0;
        for (int i = 0;i < lrc_map.size();i ++){
            LyricObject temp = lrc_map.get(i);
            if (temp.beginTime < time)
                ++index;
        }
        lrcIndex = index - 1;
        if (lrcIndex < 0)
            lrcIndex = 0;

        return lrcIndex;
    }

    /**
     * 读取歌词文件
     * @param lrcPath
     */
    public static void readLrc(String lrcPath){
        TreeMap<Integer,LyricObject> lrc_read = new TreeMap<Integer, LyricObject>();
        String data = "";
        try {
            File savedFile = new File(lrcPath);
            if (!savedFile.isFile()){
                blLrc = false;
                return;
            }
            blLrc = true;
            //创建一个文件输入流对象
            FileInputStream stream = new FileInputStream(savedFile);
            InputStreamReader streamReader = new InputStreamReader(stream,"utf-8");
            BufferedReader bufferedReader = new BufferedReader(streamReader);
            int i = 0;
            Pattern pattern = Pattern.compile("\\d{2}");
            while ((data = bufferedReader.readLine()) != null){
                data = data.replace("[","");//将前面的替换为后面的
                data = data.replace("]","@");
                String splitData[] = data.split("@");//分隔
                if(data.endsWith("@")){
                    for (int k = 0;k < splitData.length;k++){
                        String str = splitData[k];
                        str = str.replace(":",".");
                        str = str.replace(".","@");
                        String timeData[] = str.split("@");
                        Matcher matcher = pattern.matcher(timeData[0]);
                        if (timeData.length == 3 && matcher.matches()){
                            int m = Integer.parseInt(timeData[0]);//minute
                            int s = Integer.parseInt(timeData[1]);//second
                            int ms = Integer.parseInt(timeData[2]);//毫秒
                            int currentTime = (m*60+s)*1000+ms*10;
                            LyricObject lyricObject = new LyricObject();
                            lyricObject.beginTime = currentTime;
                            lyricObject.singleLineLrc = "";
                            lrc_read.put(currentTime,lyricObject);
                        }

                    }
                }else {
                    String lrcContent = splitData[splitData.length - 1];
                    for (int j=0; j < splitData.length-1;j++){
                        String tmStr = splitData[j];

                        tmStr = tmStr.replace(":",".");
                        tmStr = tmStr.replace(".","@");
                        String timeData[] = tmStr.split("@");
                        Matcher matcher = pattern.matcher(timeData[0]);
                        if(timeData.length==3 && matcher.matches()){
                            int m = Integer.parseInt(timeData[0]);  //分
                            int s = Integer.parseInt(timeData[1]);  //秒
                            int ms = Integer.parseInt(timeData[2]); //毫秒
                            int currentTime = (m*60+s)*1000+ms*10;
                            LyricObject lyricObject= new LyricObject();
                            lyricObject.beginTime = currentTime;
                            lyricObject.singleLineLrc = lrcContent;
                            lrc_read.put(currentTime,lyricObject);// 将currTime当标签  item1当数据 插入TreeMap里
                            i++;
                        }
                    }
                }
            }
            stream.close();
        }catch (FileNotFoundException e){

        }catch (IOException e){

        }
        /**
         * 遍历hashmap，计算每句歌词需要的时间
         */
        lrc_map.clear();
        data = "";
        Iterator<Integer> iterator = lrc_read.keySet().iterator();
        LyricObject oldVal = null;
        int i = 0;
        while (iterator.hasNext()){
            Object object = iterator.next();
            LyricObject val = lrc_read.get(object);
            if (oldVal == null)
                oldVal = val;
            else{
                LyricObject lyricObject;
                lyricObject  = oldVal;
                lyricObject.sigleLineTime = val.beginTime-oldVal.beginTime;
                lrc_map.put(new Integer(i),lyricObject);
                i++;
                oldVal = val;
            }
            if (!iterator.hasNext()) {
                lrc_map.put(new Integer(i), val);
            }


        }


    }



    public static boolean isBlLrc(){
        return blLrc;
    }

    public float getOffsetY(){
        return offsetY;
    }

    public void setOffsetY(float offsetY){
        this.offsetY = offsetY;
    }

    public int getWordSize(){
        return wordSize;
    }

    public void setWordSize(int sWordSize){
        wordSize = sWordSize;
    }
}
