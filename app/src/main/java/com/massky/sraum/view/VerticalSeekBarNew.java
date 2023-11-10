package com.massky.sraum.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class VerticalSeekBarNew extends androidx.appcompat.widget.AppCompatSeekBar {

    public VerticalSeekBarNew(Context context) {
        super(context);
    }

    public VerticalSeekBarNew(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalSeekBarNew(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }


    public interface OnSeekBarChangeListener {
        void onProgressChanged(VerticalSeekBarNew VerticalSeekBar, int progress, boolean fromUser);


        void onStartTrackingTouch(VerticalSeekBarNew VerticalSeekBar);


        void onStopTrackingTouch(VerticalSeekBarNew VerticalSeekBar);

    }


    private OnSeekBarChangeListener mOnSeekBarChangeListener;

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        mOnSeekBarChangeListener = l;

    }


    void onStartTrackingTouch() {
        if (mOnSeekBarChangeListener != null) {

            mOnSeekBarChangeListener.onStartTrackingTouch(this);

        }

    }


    void onStopTrackingTouch() {
        if (mOnSeekBarChangeListener != null) {

            mOnSeekBarChangeListener.onStopTrackingTouch(this);

        }

    }


    protected void onDraw(Canvas c) {
        //将SeekBar转转90度
        c.rotate(-90);
        //将旋转后的视图移动回来
        c.translate(-getHeight(), 0);
        Log.i("getHeight()", getHeight() + "");
        super.onDraw(c);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onStartTrackingTouch();
                common_code(event);
                break;
            case MotionEvent.ACTION_MOVE:
                common_code(event);
                break;
            case MotionEvent.ACTION_UP:
                common_code(event);
                onStopTrackingTouch();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    private void common_code(MotionEvent event) {
        int i = 0;
        //获取滑动的距离
        i = getMax() - (int) (getMax() * event.getY() / getHeight());
        //设置进度
        setProgress(i);
        Log.i("Progress", getProgress() + "");
        //每次拖动SeekBar都会调用
        onSizeChanged(getWidth(), getHeight(), 0, 0);
        Log.i("getWidth()", getWidth() + "");
        Log.i("getHeight()", getHeight() + "");
    }

}
