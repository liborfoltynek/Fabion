package com.fotolibb.fabion;

import android.content.ClipData;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Libb on 11.11.2017.
 */

public class OnSwipeTouchListener implements View.OnTouchListener {

    EventsByMonthsScrollingActivity mainActivity;
    View activeView;
    private GestureDetector gestureDetector;

    public OnSwipeTouchListener(EventsByMonthsScrollingActivity c) {
        gestureDetector = new GestureDetector(c, new GestureListener());
        mainActivity = c;
    }

    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        activeView = view;
        boolean b = false;
        try {
            //mainActivity.findViewById(R.id.scrollView1).setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
            //mainActivity.findViewById(R.id.scrollView2).setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
            b = gestureDetector.onTouchEvent(motionEvent);
            //mainActivity.findViewById(R.id.scrollView1).setOverScrollMode(ScrollView.OVER_SCROLL_ALWAYS);
            //mainActivity.findViewById(R.id.scrollView2).setOverScrollMode(ScrollView.OVER_SCROLL_ALWAYS);
        } catch (Exception ex) {
            Log.e("EX", ex.getMessage());
        }

        return b;
    }

    public void onSwipeRight() {

    }

    public void onSwipeLeft() {
    }

    public void onSwipeUp() {
    }

    public void onSwipeDown() {
    }

    public void onClick() {
        mainActivity.cellOnClick(activeView);
    }

    public void onDoubleClick() {

    }

    public void onLongClick() {

    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            onClick();
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            onDoubleClick();
            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            onLongClick();
            super.onLongPress(e);
        }

        // Determines the fling velocity and then fires the appropriate swipe event accordingly
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();

                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                            activeView);
                    activeView.startDrag(data, shadowBuilder, activeView, 0);

                } else {
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        ClipData data = ClipData.newPlainText("", "");
                        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                                activeView);
                        activeView.startDrag(data, shadowBuilder, activeView, 0);
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }
}