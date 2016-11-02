package com.tzq.maintenance.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2016/11/3.
 */

public class MyViewPager  extends ViewPager {
    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            // It seems ViewPager has one bug while it contains scrollable
            // child:
            // VIA: https://github.com/chrisbanes/PhotoView/issues/31
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            // ignore
        }

        return true;
    }
}
