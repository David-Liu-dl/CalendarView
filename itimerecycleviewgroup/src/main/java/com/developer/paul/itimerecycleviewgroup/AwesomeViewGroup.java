package com.developer.paul.itimerecycleviewgroup;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by Paul on 30/5/17.
 */

public class AwesomeViewGroup extends ViewGroup {
    private String TAG = "AwesomeViewGroup";
    private int inRecycledViewIndex;
    private TextView top, bottom;
    private View item;

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

    public View getItem() {
        return item;
    }

    public void setItem(View item) {
        this.removeAllViews();
        this.item = item;
        this.addView(item);
    }

    // for test easier, print this view will show relative info
    @Override
    public String toString() {
        AwesomeLayoutParams lp = (AwesomeLayoutParams) getLayoutParams();
        String str = "inRecycledViewIndex : " + inRecycledViewIndex + " ,l : " + lp.left + " ,t : " + lp.top +
                " ,r : " + lp.right + " ,b : " + lp.bottom;
        return str;
    }

    /**
     * for test only
     */
    private void initView() {
//        top = new TextView(getContext());
//        top.setText("topppp");
//        top.setTextSize(20);
//        top.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//        addView(top);
//
//        bottom = new TextView(getContext());
//        bottom.setText("bottom");
//        bottom.setTextSize(20);
//        bottom.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//        addView(bottom);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int tolerance = 2;
    private int width, height;
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int newW = r - l;
        int newH = b - t;

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

    public boolean isRightOutOfParentLeft(){
        AwesomeLayoutParams lp = (AwesomeLayoutParams) getLayoutParams();
        return lp.right<0;
    }

    // TODO: 30/5/17 change here, not only parent.getWidth, but why tinAdjust always has a value of 2?
    public boolean isLeftOutOfParentRight(){
        AwesomeLayoutParams lp = (AwesomeLayoutParams) getLayoutParams();
        View parent = (View) getParent();
        return lp.left>parent.getWidth();
    }




    public boolean isLeftOutOfParent() {
        AwesomeLayoutParams lp = (AwesomeLayoutParams) getLayoutParams();
        if (lp.left < 0) {
            return true;
        }
        return false;
    }

    public boolean isRightOutOfParent() {
        View parent = (View) getParent();
        AwesomeLayoutParams lp = (AwesomeLayoutParams) getLayoutParams();
        if (lp.right > parent.getWidth()) {
            return true;
        }
        return false;
    }

    /**
     * if left scroll or right scroll make the view out of its parent
     *
     * @return
     */
    public boolean isOutOfParent() {
        View parent = (View) getParent();
        if (parent == null) {
            return false;
        }

        AwesomeLayoutParams lp = (AwesomeLayoutParams) getLayoutParams();
        if (lp.left < 0) {
            return true;
        }

        if (lp.right > parent.getWidth()) {
            return true;
        }

        return false;
    }

    public boolean isVisibleInParent() {
        View parent = (View) getParent();
        if (parent == null) {
            return false;
        }

        AwesomeLayoutParams lp = (AwesomeLayoutParams) getLayoutParams();
        if (lp.right < 0) {
            return false;
        }

        if (lp.left > parent.getWidth()) {
            return false;
        }
        return true;
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

    public static class AwesomeLayoutParams extends LayoutParams {

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