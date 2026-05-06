package com.example.therapets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavCurved extends BottomNavigationView {

    private Path mPath;
    private Paint mPaint;
    private int mCurveMargin = 10;
    private int mCurveSize = 90;

    private int mNavBarHeight;

    public BottomNavCurved(Context context) {
        super(context);
        init();
    }

    public BottomNavCurved(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BottomNavCurved(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        setBackgroundColor(Color.TRANSPARENT);
        setItemIconTintList(null);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mNavBarHeight = h;
        createPath(w, h);
    }

    private void createPath(int w, int h) {
        mPath.reset();

        int centerX = w / 2;
        int curveDepth = mCurveSize;

        mPath.moveTo(0, 0);
        mPath.lineTo(centerX - mCurveSize - mCurveMargin, 0);
        mPath.cubicTo(
                centerX - mCurveSize, 0,
                centerX - mCurveSize / 2, curveDepth,
                centerX, curveDepth
        );
        mPath.cubicTo(
                centerX + mCurveSize / 2, curveDepth,
                centerX + mCurveSize, 0,
                centerX + mCurveSize + mCurveMargin, 0
        );
        mPath.lineTo(w, 0);
        mPath.lineTo(w, h);
        mPath.lineTo(0, h);
        mPath.close();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.drawPath(mPath, mPaint);
        super.dispatchDraw(canvas);
    }
}