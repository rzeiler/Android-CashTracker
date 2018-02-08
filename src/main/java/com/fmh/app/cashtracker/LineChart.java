package com.fmh.app.cashtracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ralf on 08.02.18.
 */

public class LineChart extends View {
    Paint paint = new Paint();
    private int w, h;
    private float max;
    private List<Item> mData = new ArrayList<Item>();

    private void init() {
        paint.setColor(getResources().getColor(R.color.colorTextWhite));
        paint.setStrokeWidth(5);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setDither(true);
    }

    public LineChart(Context context) {
        super(context);
        init();
    }

    public LineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mData.size() > 0 && max > 0) {
            /*
             * x = horizontal
             * y = vertical
             */
            Path path = new Path();
            float lastY = 0;
            float xStep = w / (mData.size() - 1);
            Item _item = mData.get(0);
            path.moveTo(0, h - (_item.fY / max * h));
            canvas.drawCircle(0, h / 2, 2, paint);
            for (int index = 1; index < mData.size(); index++) {
                _item = mData.get(index);
                canvas.drawCircle((xStep * index), h / 2, 2, paint);
                path.lineTo((xStep * index), h - (_item.fY / max * h));
            }
            PathEffect effect = new CornerPathEffect(3);
            paint.setPathEffect(effect);
            canvas.drawPath(path, paint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        w = MeasureSpec.getSize(widthMeasureSpec);
        h = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(w, h);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        w = MeasureSpec.getSize(w);
        h = MeasureSpec.getSize(h);
        setMeasuredDimension(w, h);
    }

    public void addItem(float y) {
        Item it = new Item();
        it.fY = y;
        mData.add(it);
    }

    public void setMax(float max) {
        this.max = max;
    }

    private class Item {
        public float fY;
    }

}
