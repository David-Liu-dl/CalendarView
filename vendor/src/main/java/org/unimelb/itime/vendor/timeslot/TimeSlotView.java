package org.unimelb.itime.vendor.timeslot;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.helper.DensityUtil;
import org.unimelb.itime.vendor.helper.MyCalendar;
import org.unimelb.itime.vendor.listener.ITimeTimeSlotInterface;
import org.unimelb.itime.vendor.wrapper.WrapperTimeSlot;

import java.util.Calendar;

/**
 * Created by yuhaoliu on 26/08/2016.
 */
public class TimeSlotView extends ViewGroup {
    public static int TYPE_NORMAL = 0;
    public static int TYPE_TEMP = 1;

    private int type = 0;
    private int indexInView = 0;
    private long newStartTime = 0;
    private long newEndTime = 0;
    private long duration;

    private ImageView icon;
    private MyCalendar calendar = new MyCalendar(Calendar.getInstance());

    private WrapperTimeSlot wrapper;
    private ITimeTimeSlotInterface timeslot;

    public TimeSlotView(Context context, WrapperTimeSlot wrapper) {
        super(context);
        this.wrapper = wrapper;
        this.timeslot = wrapper.getTimeSlot();
        if (timeslot != null){
            this.newStartTime = timeslot.getStartTime();
            this.newEndTime = timeslot.getEndTime();
            this.duration = this.newEndTime - this.newStartTime;
        }
        init();
    }

    public void init(){
        initBackground();
        initIcon();
    }

    public void initBackground(){
        this.setBackgroundDrawable(getResources().getDrawable(R.drawable.time_block_background));
    }

    public void initIcon(){
        icon = new ImageView(getContext());
        LayoutParams params = new LayoutParams(50, 50);

        if (!wrapper.isSelected()){
            icon.setImageResource(R.drawable.icon_event_timeslot_unselected);
        }else{
            icon.setImageResource(R.drawable.icon_event_attendee_selected);
        }

        this.addView(icon,params);
    }

    public void setTimes(long startTime, long endTime){
        this.newStartTime = startTime;
        this.newEndTime = endTime;
        this.duration = endTime - startTime;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startTime);
        this.calendar.cloneFromCalendar(cal);
    }

    public void setIsSelected(boolean isSelect){
        this.wrapper.setSelected(isSelect);
        updateIcon();
    }

    public void setNewStartTime(Long newStartTime) {
        this.newStartTime = newStartTime;
    }

    public long getDuration() {
        return newEndTime - newStartTime == 0 ? duration : (newEndTime - newStartTime);
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getNewStartTime(){
        return this.calendar.getCalendar().getTimeInMillis();
    }

    public long getNewEndTime(){
        return this.getNewStartTime() + getDuration();
    }

    public boolean isSelect() {
        return this.wrapper.isSelected();
    }

    private void updateIcon(){
        if (wrapper.isSelected()){
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
        ValueAnimator alphaAnimation = ObjectAnimator.ofFloat(this, View.ALPHA, 0,1);
        alphaAnimation.setDuration(2000); // milliseconds
        alphaAnimation.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                TimeSlotView.this.setBackgroundResource(R.drawable.icon_timeslot_bg);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                TimeSlotView.this.setBackgroundResource(R.drawable.time_block_background);
            }
        });
        alphaAnimation.start();
    }

    public ITimeTimeSlotInterface getTimeslot() {
        return timeslot;
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
