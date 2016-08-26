package org.unimelb.itime.vendor.timeslot;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.unimelb.itime.vendor.R;

/**
 * Created by Paul on 26/08/2016.
 */
public class TimeSlotView extends ViewGroup {
    private int duration;
    private Long startTime;
    private int width;
    private int height;

    private ImageView icon;

    public TimeSlotView(Context context, Long startTime, int duration) {
        super(context);
        this.startTime = startTime;
        this.duration = duration;
        init();
    }

    public void init(){
        initBackground();
        initIcon();
    }

    public void initBackground(){
        this.setBackgroundResource(R.drawable.time_block_background);
    }

    public void initIcon(){
        icon = new ImageView(getContext());
        icon.setImageResource(R.drawable.icon_tick);
        this.addView(icon);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.width = (int) (MeasureSpec.getSize(widthMeasureSpec));
        this.height = (MeasureSpec.getSize(heightMeasureSpec));
        setMeasuredDimension(width,height);
    }


    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        int iconWidth = this.width/3;
        TimeSlotViewLayoutParams iconParams = (TimeSlotViewLayoutParams) icon.getLayoutParams();
        iconParams.top = this.width/10;
        iconParams.left = this.width/3*2-this.width/10;
        icon.layout(iconParams.left , iconParams.top,
                iconParams.left + iconWidth, iconParams.top + iconWidth);
    }

    @Override
    public android.view.ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new TimeSlotViewLayoutParams(getContext(), attrs);
    }

    @Override
    protected android.view.ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new TimeSlotViewLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected android.view.ViewGroup.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return new TimeSlotViewLayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p instanceof TimeSlotView.LayoutParams;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
