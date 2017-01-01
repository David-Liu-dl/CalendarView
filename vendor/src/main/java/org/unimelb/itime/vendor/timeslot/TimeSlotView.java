package org.unimelb.itime.vendor.timeslot;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.helper.DensityUtil;
import org.unimelb.itime.vendor.helper.MyCalendar;
import org.unimelb.itime.vendor.helper.VendorAnimation;
import org.unimelb.itime.vendor.listener.ITimeTimeSlotInterface;

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
    private boolean isSelect = false;

    private ImageView icon;
    private MyCalendar calendar = new MyCalendar(Calendar.getInstance());

    private ITimeTimeSlotInterface timeslot;

    public TimeSlotView(Context context, @Nullable ITimeTimeSlotInterface timeslot) {
        super(context);
        this.timeslot = timeslot;
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
        this.setBackgroundResource(R.drawable.icon_timeslot_bg);
//        this.setBackgroundDrawable(getResources().getDrawable(R.drawable.itime_draggable_event_bg));
//        this.setBackgroundResource(R.drawable.icon_timeslot_bg);
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
        this.newStartTime = startTime;
        this.newEndTime = endTime;
        this.duration = endTime - startTime;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startTime);
        this.calendar.cloneFromCalendar(cal);
    }

    public void setStatus(boolean isSelect){
        this.isSelect = isSelect;
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
        ViewGroup a = ((ViewGroup)this.getParent());
        if (a != null)
            a.setBackgroundColor(getResources().getColor(R.color.red));
        TimeSlotView.this.setBackgroundResource(R.drawable.icon_timeslot_bg);
        ValueAnimator animator = VendorAnimation.getInstance().getAlphaAnim(255,125,this);
        animator.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                ViewGroup a = (ViewGroup)TimeSlotView.this.getParent();
                if(a != null) {
                    a.setBackgroundColor(TimeSlotView.this.getResources().getColor(R.color.blue));
                }

//                TimeSlotView.this.setBackgroundResource(R.drawable.time_block_background);
//                TimeSlotView.this.setBackgroundColor(getResources().getColor(R.color.blue));
            }
        });
        animator.start();
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
