package com.yalantis.glatademo.advanced;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.yalantis.glatademo.R;

import java.util.ArrayList;

public class SnappingHorizontalScrollView extends HorizontalScrollView {
    private static final int SWIPE_MIN_DISTANCE = 5;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;

    private ArrayList<View> mItems = null;
    private GestureDetector mGestureDetector;
    private int mActiveFeature = 0;

    private int itemWidth = 0;
    private LinearLayout internalWrapper;

    public SnappingHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SnappingHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SnappingHorizontalScrollView(Context context) {
        super(context);
    }

    public void initItems() {
        internalWrapper = findViewById(R.id.innerGroup);
        ArrayList<View> items = new ArrayList<View>();
        for (int i = 0; i < internalWrapper.getChildCount(); i++) {
            items.add(internalWrapper.getChildAt(i));
        }
        mItems = items;

        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //If the user swipes
                if (mGestureDetector.onTouchEvent(event)) {
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    snap();
                    return true;
                } else {
                    return false;
                }
            }
        });
        mGestureDetector = new GestureDetector(getContext(), new MyGestureDetector());
    }

    void snap() {
        int scrollX = getScrollX();
        int fullSize = internalWrapper.getMeasuredWidth();
        int featureWidth = fullSize / mItems.size();
        mActiveFeature = ((scrollX + (featureWidth / 2)) / featureWidth);
        int scrollTo = mActiveFeature * featureWidth;
        smoothScrollTo(scrollTo, 0);
    }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                //right to left
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    int featureWidth = internalWrapper.getMeasuredWidth() / mItems.size();
                    mActiveFeature = (mActiveFeature < (mItems.size() - 1)) ? mActiveFeature + 1 : mItems.size() - 1;
                    smoothScrollTo(mActiveFeature * featureWidth, 0);
                    return true;
                }
                //left to right
                else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    int featureWidth = internalWrapper.getMeasuredWidth() / mItems.size();
                    mActiveFeature = (mActiveFeature > 0) ? mActiveFeature - 1 : 0;
                    smoothScrollTo(mActiveFeature * featureWidth, 0);
                    return true;
                }
            } catch (Exception e) {
                Log.e("Fling", "There was an error processing the Fling event:" + e.getMessage());
            }
            return false;
        }
    }
}