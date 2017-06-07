package david.itimecalendar.calendar.unitviews;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Calendar;

import david.itimecalendar.R;
import david.itimecalendar.calendar.listeners.ITimeTimeSlotInterface;
import david.itimecalendar.calendar.util.DensityUtil;
import david.itimecalendar.calendar.wrapper.WrapperTimeSlot;

/**
 * Created by yuhaoliu on 26/08/2016.
 */
public class DraggableTimeSlotView extends FrameLayout {
    public static int TYPE_NORMAL = 0;
    public static int TYPE_TEMP = 1;

    public boolean onScreen = false;
    public boolean isAllday = false;
    private int type = 0;
    private long newStartTime = 0;
    private long newEndTime = 0;
    private long duration;

    private WrapperTimeSlot wrapper;
    private ITimeTimeSlotInterface timeslot;

    private ValueAnimator bgAlphaAnimation;
    private ValueAnimator frameAlphaAnimation;
    private TextView title;

    private PosParam posParam;

    public DraggableTimeSlotView(Context context, WrapperTimeSlot wrapper, boolean isAllday) {
        super(context);
        this.wrapper = wrapper;
        this.isAllday = isAllday;
        this.timeslot = wrapper.getTimeSlot();
        if (timeslot != null){
            this.newStartTime = timeslot.getStartTime();
            this.newEndTime = timeslot.getEndTime();
            this.duration = this.newEndTime - this.newStartTime;
        }
        init();
    }

    public void init(){
        initViews();
        initBackground();
//        initIcon();
        initAnimation();
    }

    public void initViews(){
        this.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                final int width = right - left;
                title.measure(0,0);
                if (width < title.getMeasuredWidth()){
                    title.setText(getTimeText(false));
                }
            }
        });
        title = new TextView(getContext());
        title.setText(isAllday?"All Day":getTimeText(true));
        title.setGravity(Gravity.CENTER);
        title.setTextColor(getResources().getColor(R.color.event_as_bg_title));
        title.setTextSize(12);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = DensityUtil.dip2px(getContext(),5);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        this.addView(title,layoutParams);
    }

    public void initBackground(){
        this.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_timeslot_selected));
    }

    public void setTimes(long startTime, long endTime){
        this.newStartTime = startTime;
        this.newEndTime = endTime;
        this.duration = endTime - startTime;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startTime);
    }

    public void setIsSelected(boolean isSelect){
        this.wrapper.setSelected(isSelect);
    }

    public void setNewStartTime(long newStartTime) {
        this.newStartTime = newStartTime;
    }

    public long getDuration() {
//        return newEndTime - newStartTime == 0 ? duration : (newEndTime - newStartTime);
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getNewStartTime(){
        return newStartTime;
    }

    public long getNewEndTime(){
        return this.getNewStartTime() + getDuration();
    }

    public boolean isSelect() {
        return this.wrapper.isSelected();
    }

    public WrapperTimeSlot getWrapper() {
        return wrapper;
    }
    
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void showAlphaAnim(){
        if (bgAlphaAnimation != null && !bgAlphaAnimation.isRunning() && frameAlphaAnimation != null && !frameAlphaAnimation.isRunning()){
            bgAlphaAnimation.start();
        }
    }

    private String getTimeText(boolean oneLine){
        if (wrapper == null || wrapper.getTimeSlot() == null){
            return "";
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(wrapper.getTimeSlot().getStartTime());
        String starTime = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE));
        cal.setTimeInMillis(wrapper.getTimeSlot().getEndTime());
        String endTime = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE));
        return starTime + (oneLine?"-":"\n-\n") + endTime;
    }

    private void initAnimation(){
        bgAlphaAnimation = ObjectAnimator.ofFloat(this, View.ALPHA, 0,1);
        frameAlphaAnimation = ObjectAnimator.ofFloat(this, View.ALPHA, 0,1);
        frameAlphaAnimation.setDuration(200);
        bgAlphaAnimation.setDuration(300); // milliseconds
        bgAlphaAnimation.setRepeatCount(1);
        bgAlphaAnimation.setRepeatMode(ValueAnimator.REVERSE);
        bgAlphaAnimation.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                DraggableTimeSlotView.this.setBackgroundResource(R.drawable.icon_timeslot_empty);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                DraggableTimeSlotView.this.setBackgroundResource(R.drawable.icon_timeslot_selected);
                frameAlphaAnimation.start();
            }
        });
        bgAlphaAnimation.setStartDelay(500);
    }

    public ITimeTimeSlotInterface getTimeslot() {
        return timeslot;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
//        return false;
    }

    /**
     * the display position of draggable item,
     * for overlapping algorithm
     *
     */
    public static class PosParam{
        public int startY;
        public int startX;
        public int widthFactor;
        public int topMargin;

        public PosParam(int startY, int startX, int widthFactor, int topMargin) {
            this.startY = startY;
            this.startX = startX;
            this.widthFactor = widthFactor;
            this.topMargin = topMargin;
        }
    }

    public PosParam getPosParam() {
        return posParam;
    }

    public void setPosParam(PosParam posParam) {
        this.posParam = posParam;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
//        int labelWidth = width
    }
}
