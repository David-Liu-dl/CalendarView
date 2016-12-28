package org.unimelb.itime.vendor.weekview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.helper.MyCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by yuhaoliu on 23/09/16.
 */
public class WeekViewHeader extends LinearLayout {
    MyCalendar myCalendar;

    private int displayLen = 7;

    private Context context;

    private List<SingleHeaderDayView> singleHeaderDayViews = new ArrayList<>();

    public WeekViewHeader(Context context) {
        super(context);
        this.context = context;
        this.init();
        this.initDays();
    }

    public WeekViewHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.init();
        this.initDays();
    }

    private void init(){
        this.setOrientation(HORIZONTAL);
    }

    private void initDays(){
        for (int i = 0; i < displayLen; i++) {
            SingleHeaderDayView singleHeaderDayView = new SingleHeaderDayView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1f);
            this.addView(singleHeaderDayView,params);
            singleHeaderDayViews.add(singleHeaderDayView);
        }
    }

    public void setMyCalendar(MyCalendar calendar){
        this.myCalendar = calendar;
        this.updateHeaders();
    }

    private void updateHeaders(){
        MyCalendar cal = new MyCalendar(this.myCalendar);
        int color;

        for (int i = 0; i < this.singleHeaderDayViews.size(); i++) {
            SingleHeaderDayView singleHeaderDayView = this.singleHeaderDayViews.get(i);

            if (cal.isToday()){
                Drawable drawable = getResources().getDrawable(R.drawable.itime_day_rectangle);
                singleHeaderDayView.getContainer().setBackground(drawable);
                ((GradientDrawable) singleHeaderDayView.getContainer().getBackground()).setColor(getResources().getColor(R.color.today_circle_color));
                color = getResources().getColor(R.color.text_in_circle_color);
            }else{
                singleHeaderDayView.getContainer().setBackgroundResource(0);
                color = getResources().getColor(R.color.text_enable);
            }

            String dayOfWeek = cal.getCalendar().getDisplayName(Calendar.DAY_OF_WEEK,Calendar.SHORT, Locale.getDefault());
            String nthDay = cal.getCalendar().get(Calendar.DAY_OF_MONTH) + "";
            singleHeaderDayView.updateText(dayOfWeek, nthDay,color);
            cal.setOffsetByDate(1);
        }
    }
}
