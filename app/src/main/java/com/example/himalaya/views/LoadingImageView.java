package com.example.himalaya.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LoadingImageView extends androidx.appcompat.widget.AppCompatImageView {
    private int mDegree=0;
    private boolean isNeedRotate=false;
    public LoadingImageView(@NonNull Context context) {
        this(context,null);
    }

    public LoadingImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isNeedRotate=true;
        post(new Runnable() {
            @Override
            public void run() {
                mDegree+=15;
                mDegree=mDegree<=360?mDegree:15;
                invalidate();
                if (isNeedRotate) {
                    postDelayed(this,100);
                }

            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isNeedRotate=false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(mDegree,getWidth()/2,getHeight()/2);
        super.onDraw(canvas);
    }
}
