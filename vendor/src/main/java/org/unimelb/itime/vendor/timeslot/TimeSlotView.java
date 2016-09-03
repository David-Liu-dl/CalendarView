package org.unimelb.itime.vendor.timeslot;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.unimelb.itime.vendor.R;

/**
 * Created by Paul on 26/08/2016.
 */
public class TimeSlotView extends RelativeLayout {
    private int duration;
    private Long startTime;
    private int width =0;
    private int height =0;

    private ImageView icon;

    private boolean isSelect;

    public TimeSlotView(Context context, Long startTime, int duration, boolean isSelect) {
        super(context);
        this.startTime = startTime;
        this.duration = duration;
        this.isSelect = isSelect;
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
        if (!isSelect)
            icon.setImageResource(R.drawable.icon_event_timeslot_unselected);
        else
            icon.setImageResource(R.drawable.icon_event_attendee_selected);
        this.addView(icon);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.width = (View.MeasureSpec.getSize(widthMeasureSpec));
        this.height = (View.MeasureSpec.getSize(heightMeasureSpec));
        setMeasuredDimension(width,height);
    }


    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        this.width = i2 - i;
        int iconWidth = this.width/3;
        int top = iconWidth/3;
        int left = this.width - iconWidth - iconWidth/3;
        icon.layout(left , top, left + iconWidth, top + iconWidth);
//        icon.layout(left, 20 ,110, 60);
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


    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
        updateIcon();
    }

    private void updateIcon(){
        if (isSelect)
            icon.setImageResource(R.drawable.icon_event_attendee_selected);
        else
            icon.setImageResource(R.drawable.icon_event_timeslot_unselected);
        icon.invalidate();
    }
}
