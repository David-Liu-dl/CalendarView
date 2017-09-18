package david.itimecalendar.calendar.ui.unitviews;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.Calendar;

import david.itimecalendar.R;
import david.itimecalendar.calendar.util.BaseUtil;
import david.itimecalendar.calendar.util.DensityUtil;
import david.itimecalendar.calendar.util.MyCalendar;
import david.itimecalendar.calendar.wrapper.WrapperTimeSlot;

/**
 * Created by yuhaoliu on 1/05/2017.
 */

public class RcdRegularTimeSlotView extends FrameLayout {
    private TextView label;
    private TextView title;
    private ImageView icon;
    private WrapperTimeSlot wrapper;
    private boolean isAllday;

    public RcdRegularTimeSlotView(@NonNull Context context, WrapperTimeSlot wrapper, boolean isAllday) {
        super(context);
        this.isAllday = isAllday;
        this.wrapper = wrapper;
        init();
    }

    public RcdRegularTimeSlotView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RcdRegularTimeSlotView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        this.setBackgroundResource(R.drawable.icon_timeslot_rcd);
        this.getBackground().setAlpha(217);
        label = new TextView(getContext());
//        label.setText(isAllday ? "All Day":"Recommended");
//        label.setTextColor(getResources().getColor(R.color.timeslot_rcd_label));
//        label.setGravity(Gravity.CENTER);
//        label.setTextSize(isAllday?12:9);
//        label.setId(generateViewId());
//        LayoutParams labelPrams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        labelPrams.topMargin = isAllday?DensityUtil.dip2px(getContext(),10):0;
//        labelPrams.addRule(ALIGN_PARENT_TOP);
//        labelPrams.addRule(CENTER_HORIZONTAL);
////        this.addView(label,labelPrams);
//
//        title = new TextView(getContext());
//        title.setText(isAllday ? "":getTimeText());
//        title.setGravity(Gravity.CENTER);
//        title.setTextColor(getResources().getColor(R.color.timeslot_rcd_title));
//        title.setTextSize(12);
//        title.setId(View.generateViewId());
//        LayoutParams titlePrams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, isAllday ? 0 : ViewGroup.LayoutParams.WRAP_CONTENT);
//        titlePrams.addRule(BELOW,label.getId());
//        titlePrams.addRule(CENTER_HORIZONTAL);
//        this.addView(title, titlePrams);

//        FrameLayout frameLayout = new FrameLayout(getContext());
//        LayoutParams frameLayoutPrams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        frameLayoutPrams.addRule(BELOW,title.getId());
//        this.addView(frameLayout,frameLayoutPrams);

        icon = new ImageView(getContext());
//        int padding = DensityUtil.dip2px(getContext(),10);
//        icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//        icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_timeslot_plus));
//        icon.setPadding(0,padding,0,padding);

//        int size = getPlusIconSize(isAllday ?
//                BaseUtil.getAllDayLong(Calendar.getInstance().getTimeInMillis())
//                : wrapper.getTimeSlot().getEndTime() - wrapper.getTimeSlot().getStartTime());
//        FrameLayout.LayoutParams iconPrams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        iconPrams.gravity = Gravity.CENTER;
//        icon.setLayoutParams(iconPrams);
//        frameLayout.addView(icon);
    }

    private int getPlusIconSize(long duration){
        long oneHour = 3600 * 1000;
        long halfHour = 1800 * 1000;
        if (duration > oneHour){
            return DensityUtil.dip2px(getContext(),50);
        }

        if (duration > halfHour){
            return DensityUtil.dip2px(getContext(),20);
        }

        return 0;
    }

    private String getTimeText(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(wrapper.getTimeSlot().getStartTime());
        String starTime = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE));
        cal.setTimeInMillis(wrapper.getTimeSlot().getEndTime());
        String endTime = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE));
        return starTime + "-" + endTime;
    }

    public WrapperTimeSlot getWrapper() {
        return wrapper;
    }

    public void setWrapper(WrapperTimeSlot wrapper) {
        this.wrapper = wrapper;
    }

    private MyCalendar myCalendar;

    public long getAllDayBeginTime(){
        return myCalendar.getBeginOfDayMilliseconds();
    }

    public void setMyCalendar(MyCalendar myCalendar) {
        this.myCalendar = myCalendar;
    }
}
