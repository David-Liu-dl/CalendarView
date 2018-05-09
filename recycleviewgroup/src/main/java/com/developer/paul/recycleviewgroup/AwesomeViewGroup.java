package com.developer.paul.recycleviewgroup;

/**
 * Created by Paul on 22/5/17.
 */


import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by Paul on 9/5/17.
 */

public class AwesomeViewGroup extends ViewGroup {
    private String TAG = "AwesomeViewGroup";

    private View item;

    private int width, height;
    private Calendar calendar;
    private int inRecycledViewIndex;
    private int startX;

    public AwesomeViewGroup(Context context) {
        super(context);
        initView();
    }

    public AwesomeViewGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public int getInRecycledViewIndex() {
        return inRecycledViewIndex;
    }

    public void setInRecycledViewIndex(int inRecycledViewIndex) {
        this.inRecycledViewIndex = inRecycledViewIndex;
    }

    /**
     * for test only
     */
    private void initView(){
//        TextView textView = new TextView(getContext());
//        textView.setText("helloooo");
//        textView.setTextSize(20);
//        textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//        addView(textView);
    }

    public void setCalendar(Calendar calendar){
        this.calendar = calendar;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
//                Log.i(TAG, "onTouchEvent: " + "down");
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.i(TAG, "onTouchEvent: " + "move");
                break;
            case MotionEvent.ACTION_UP:
//                Log.i(TAG, "onTouchEvent: " + "up");
                break;
            case MotionEvent.ACTION_CANCEL:
//                Log.i(TAG, "onTouchEvent: " + "cancel");
                break;
        }

        boolean value = super.onTouchEvent(event);
//        Log.i(TAG, "onTouchEvent:  " + value);
        return value;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
//                Log.i(TAG, "onInterceptTouchEvent: " + "down");
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.i(TAG, "onInterceptTouchEvent: " + "move");
                break;
            case MotionEvent.ACTION_UP:
//                Log.i(TAG, "onInterceptTouchEvent: " + "up");
                break;
            case MotionEvent.ACTION_CANCEL:
//                Log.i(TAG, "onInterceptTouchEvent: " + "cancel");
                break;
        }
        boolean value = super.onInterceptTouchEvent(ev);
//        Log.i(TAG, "onInterceptTouchEvent: " + value);
        return value;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec,heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int tolerance = 2;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int newW = r - l;
        int newH = b - t;

//        TextView textView = (TextView) getChildAt(0);
//        textView.layout(0, 0, 100, 100);

        if (changed && item != null){
            if (!isInRange(newH, height, tolerance) || !isInRange(newW, width, tolerance)){
                item.layout(0,0,newW,newH);
                width = newW;
                height = newH;
            }
        }
    }

    private boolean isInRange(int newN, int oldN, int tolerance){
        return (oldN + tolerance) > newN && (oldN - tolerance) < newN;
    }

    public boolean isLeftOutOfParent(){
        View parent = (View) getParent();
        if (parent==null){
            return false;
        }
        AwesomeLayoutParams lp = (AwesomeLayoutParams) getLayoutParams();
        if (lp.left < 0){
            return true;
        }
        return false;
    }

    public boolean isRightOutOfParent(){
        View parent = (View) getParent();
        if (parent==null){
            return false;
        }
        AwesomeLayoutParams lp = (AwesomeLayoutParams) getLayoutParams();
        if (lp.right > parent.getWidth()){
            return true;
        }
        return false;
    }

    /**
     * if left scroll or right scroll make the view out of its parent
     * @return
     */
    public boolean isOutOfParent(){
        View parent = (View) getParent();
        if (parent==null){
            return false;
        }

        AwesomeLayoutParams lp = (AwesomeLayoutParams) getLayoutParams();
        if (lp.left < 0){
            return true;
        }

        if (lp.right > parent.getWidth()){
            return true;
        }

        return false;
    }

    public boolean isVisibleInParent(){
        View parent = (View) getParent();
        if (parent == null){
            return false;
        }

        AwesomeLayoutParams lp = (AwesomeLayoutParams) getLayoutParams();
        if (lp.right < 0){
            return false;
        }

        if (lp.left > parent.getWidth()){
            return false;
        }

        return true;
    }

    /**
     * this method will automatically re-layout based on its layout params
     */
    public void reLayoutByLp(){
        AwesomeLayoutParams lp = (AwesomeLayoutParams) getLayoutParams();
        layout(lp.left, lp.top, lp.right, lp.bottom);
    }

    public View getItem() {
        return item;
    }

    public void setItem(View item) {
        this.removeAllViews();
        this.item = item;
        this.addView(item);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new AwesomeLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new AwesomeLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new AwesomeLayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof AwesomeLayoutParams;
    }

    public static class AwesomeLayoutParams extends LayoutParams{

        public int left;
        public int top;
        public int right;
        public int bottom;

        public int parentHeight;


        public AwesomeLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public AwesomeLayoutParams(int width, int height) {
            super(width, height);
        }

        public AwesomeLayoutParams(LayoutParams source) {
            super(source);
        }
    }
}
