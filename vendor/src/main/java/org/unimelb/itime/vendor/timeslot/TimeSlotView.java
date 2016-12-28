package org.unimelb.itime.vendor.timeslot;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.helper.DensityUtil;
import org.unimelb.itime.vendor.helper.MyCalendar;
import org.unimelb.itime.vendor.helper.VendorAnimation;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;
import org.unimelb.itime.vendor.listener.ITimeTimeSlotInterface;

import java.sql.Time;
import java.util.Calendar;

/**
 * Created by yuhaoliu on 26/08/2016.
 */
public class TimeSlotView extends ViewGroup {
    public static int TYPE_NORMAL = 0;
    public static int TYPE_TEMP = 1;

    private int type = 0;
    private int indexInView = 0;
    private long startTime = 0;
    private long endTime = 0;
    private long duration;
    private boolean isSelect = false;

    private ImageView icon;
    private MyCalendar calendar = new MyCalendar(Calendar.getInstance());

    private ITimeTimeSlotInterface timeslot;

    public TimeSlotView(Context context, @Nullable ITimeTimeSlotInterface timeslot) {
        super(context);
        this.timeslot = timeslot;
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
        LayoutParams params = new LayoutParams(50, 50);

        if (!isSelect){
            icon.setImageResource(R.drawable.icon_event_timeslot_unselected);
        }else{
            icon.setImageResource(R.drawable.icon_event_attendee_selected);
        }

        this.addView(icon,params);
    }

    public void setTimes(long startTime, long endTime){
        this.startTime = startTime;
        this.endTime = endTime;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startTime);
        this.calendar.cloneFromCalendar(cal);
    }

    public void setStatus(boolean isSelect){
        this.isSelect = isSelect;
        updateIcon();
    }

    public long getStartTime() {
        return timeslot.getStartTime();
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return endTime - startTime == 0 ? duration : (endTime - startTime);
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getStartTimeM(){
        return this.calendar.getCalendar().getTimeInMillis();
    }

    public long getEndTimeM(){
        return this.getStartTimeM() + getDuration();
    }

    public boolean isSelect() {
        return isSelect;
    }

    private void updateIcon(){
        if (isSelect){
            icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_event_attendee_selected));
        } else {
            icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_event_timeslot_unselected));
        }
    }

    public MyCalendar getCalendar() {
        return calendar;
    }

    public void setCalendar(MyCalendar calendar) {
        this.calendar = calendar;
    }

    public void setIndexInView(int indexInView) {
        this.indexInView = indexInView;
    }

    public int getIndexInView() {
        return indexInView;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public MyCalendar getNewCalendar() {
        return calendar;
    }

    public void showAlphaAnim(){
        ValueAnimator animator = VendorAnimation.getInstance().getAlphaAnim(255,125,this);
        animator.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationStart(Animator animation) {
//                super.onAnimationStart(animation);
                TimeSlotView.this.setBackground(getResources().getDrawable(R.drawable.icon_timeslot_bg));
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                TimeSlotView.this.setBackground(getResources().getDrawable(R.drawable.time_block_background));
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int cCount = getChildCount();
        int width = r - l;
        int margin = DensityUtil.dip2px(getContext(),5);
        for (int i = 0; i < cCount; i++) {
            int cW = getChildAt(i).getLayoutParams().width;
            int cH = getChildAt(i).getLayoutParams().height;
            getChildAt(i).layout(width - cW - margin,margin, width, cH+margin);
        }
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        public int left = 0;
        public int top = 0;

        public LayoutParams(Context arg0, AttributeSet arg1) {
            super(arg0, arg1);
        }

        public LayoutParams(int arg0, int arg1) {
            super(arg0, arg1);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams arg0) {
            super(arg0);
        }

    }
}
