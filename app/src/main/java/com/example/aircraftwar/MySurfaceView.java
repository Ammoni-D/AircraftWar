package com.example.aircraftwar;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class MySurfaceView extends SurfaceView implements
        SurfaceHolder.Callback,Runnable {
    public float x = 50, y = 50;
    public static int screenWidth = 480, screenHeight = 800;
    boolean mbLoop = false; //控制绘画线程的标志位
    private SurfaceHolder mSurfaceHolder;
    private Canvas canvas;  //绘图的画布
    private Paint mPaint;
    public MySurfaceView(Context context) {
        super(context);
        mbLoop = true;
        mPaint = new Paint();  //设置画笔
        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(this);
        this.setFocusable(true);
    }
    public void draw(){}

    @Override
    public void run() {
        //设置一个循环来绘制，通过标志位来控制开启绘制还是停止
        while (mbLoop){
            synchronized (mSurfaceHolder){
                draw();
            }
        }
    }

    //Surface首次创建成功时调用，创建绘图线程
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        new Thread(this).start();
    }

    //Surface尺寸或格式发生变化时调用，如屏幕旋转，更新画布宽高，重新计算绘图区域
    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        screenWidth = width;
        screenHeight = height;
    }

    //Surface被销毁前调用,停止绘图线程
    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        mbLoop = false;
    }

    public SurfaceHolder getmSurfaceHolder(){
        return mSurfaceHolder;
    }
    public Paint getmPaint(){
        return mPaint;
    }
}
