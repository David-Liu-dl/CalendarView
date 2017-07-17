package com.github.sundeepk.compactcalendarview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by yuhaoliu on 3/05/2017.
 */

public class ITimeTimeslotCalendar extends RelativeLayout {

    private CompactCalendarView calendarView;
    private Context context;
    private int targetHeight;
    private CompactCalendarView.CompactCalendarViewListener outListener;

    public ITimeTimeslotCalendar(@NonNull Context context) {
        super(context);
        this.calendarView = new CompactCalendarView(context);
        this.context = context;
        this.init();
    }

    public ITimeTimeslotCalendar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.calendarView = new CompactCalendarView(context, attrs, 0);
        this.loadAttributes(attrs, context);
        this.context = context;
        this.init();
    }

    public ITimeTimeslotCalendar(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.calendarView = new CompactCalendarView(context, attrs, defStyleAttr);
        this.loadAttributes(attrs, context);
        this.init();
    }

    private void loadAttributes(AttributeSet attrs, Context context) {
        if (attrs != null && context != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ITimeTimeslotCalendar, 0, 0);
            try {
                targetHeight = typedArray.getDimensionPixelSize(R.styleable.ITimeTimeslotCalendar_compactCalendarTargetHeight,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, targetHeight, context.getResources().getDisplayMetrics()));
            } finally {
                typedArray.recycle();
            }
        }
    }

    private void init(){
        initSelf();
        initCalendar();
        initDivider();
    }

    private void initSelf(){
        this.setBackgroundColor(getResources().getColor(R.color.calendar_alpha_bg));
    }

    public void setOnBgClickListener(OnClickListener listener){
        this.setOnClickListener(listener);
    }

    public void setSlotNumMap(InnerCalendarTimeslotPackage innerSlotPackage) {
        this.calendarView.setSlotNumMap(innerSlotPackage);
    }

    public void setCurrentDate(Date date){
        this.calendarView.setCurrentDate(date);
    }

    public void showMonth(Date date){
        this.calendarView.showMonth(date);
    }

    private void initCalendar(){
        RelativeLayout.LayoutParams calParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, targetHeight);
        calendarView.setId(generateViewId());
        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                if (outListener != null){
                    outListener.onDayClick(dateClicked);
                }
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                if (outListener != null){
                    outListener.onMonthScroll(firstDayOfNewMonth);
                }
            }
        });
        this.addView(calendarView,calParams);
    }

    private void initDivider(){
        View dividerView;
        //divider
        dividerView = new View(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(context,1));
        params.addRule(ALIGN_PARENT_TOP);
        dividerView.setLayoutParams(params);
        dividerView.setBackgroundColor(getResources().getColor(R.color.divider_nav));
        this.addView(dividerView);
    }

    public void setBodyListener(CompactCalendarView.CompactCalendarViewListener listener){
        this.outListener = listener;
    }

    public void refreshSlotNum(){
        calendarView.invalidate();
    }

    public static class InnerCalendarTimeslotPackage{
        private OnUpdate onUpdate;

        public SimpleDateFormat slotFmt = new SimpleDateFormat("yyyyMMdd");
        public HashMap<String, Integer> numSlotMap = new HashMap<>();

        public void add(String strDate){
            if (numSlotMap.containsKey(strDate)){
                numSlotMap.put(strDate, numSlotMap.get(strDate) + 1);
            }else {
                numSlotMap.put(strDate,1);
            }

            if (onUpdate != null){
                onUpdate.onUpdate();
            }
        }

        public void remove(){
            if (onUpdate != null){
                onUpdate.onUpdate();
            }
        }

        public void clear(){
            this.numSlotMap.clear();
            if (onUpdate != null){
                onUpdate.onUpdate();
            }
        }

        public void setOnUpdate(OnUpdate onUpdate) {
            this.onUpdate = onUpdate;
        }

        interface OnUpdate{
            void onUpdate();
        }
    }

    public CompactCalendarView getCalendarView() {
        return calendarView;
    }
}
