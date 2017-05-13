package com.github.sundeepk.compactcalendarview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by yuhaoliu on 3/05/2017.
 */

public class ITimeInnerCalendar extends RelativeLayout {

    private RelativeLayout calTitleBar;
    private CompactCalendarView calendarView;
    private TextView titleTv;
    private Context context;
    private int targetHeight;
    private CompactCalendarView.CompactCalendarViewListener outListener;

    public ITimeInnerCalendar(@NonNull Context context) {
        super(context);
        this.calendarView = new CompactCalendarView(context);
        this.context = context;
        this.init();
    }

    public ITimeInnerCalendar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.calendarView = new CompactCalendarView(context, attrs, 0);
        this.loadAttributes(attrs, context);
        this.context = context;
        this.init();
    }

    public ITimeInnerCalendar(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.calendarView = new CompactCalendarView(context, attrs, defStyleAttr);
        this.loadAttributes(attrs, context);
        this.init();
    }

    private void loadAttributes(AttributeSet attrs, Context context) {
        if (attrs != null && context != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ITimeInnerCalendar, 0, 0);
            try {
                targetHeight = typedArray.getDimensionPixelSize(R.styleable.ITimeInnerCalendar_compactCalendarTargetHeight,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, targetHeight, context.getResources().getDisplayMetrics()));
            } finally {
                typedArray.recycle();
            }
        }
    }

    private void init(){
        initSelf();
        initCalendarTitleBar();
        initCalendar();
        initDivider();
    }

    private void initSelf(){
        this.setBackgroundColor(getResources().getColor(R.color.calendar_alpha_bg));
    }

    public void setOnBgClickListener(OnClickListener listener){
        this.setOnClickListener(listener);
    }

    public void setSlotNumMap(HashMap<String, Integer> slotNumMap) {
        this.calendarView.setSlotNumMap(slotNumMap);
    }

    public void setCurrentDate(Date date){
        this.calendarView.setCurrentDate(date);
    }

    private void initCalendarTitleBar(){
        this.calTitleBar = new RelativeLayout(getContext());
        this.calTitleBar.setBackgroundColor(Color.WHITE);
        int barPad = DensityUtil.dip2px(context,20);
        this.calTitleBar.setPadding(0,barPad,0,barPad);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.calTitleBar.setId(View.generateViewId());
        this.addView(calTitleBar, params);

        titleTv = new TextView(getContext());
        titleTv.setId(View.generateViewId());
        titleTv.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams titleTvParams = new RelativeLayout.LayoutParams(DensityUtil.dip2px(context,150), ViewGroup.LayoutParams.WRAP_CONTENT);
        titleTvParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        calTitleBar.addView(titleTv,titleTvParams);
        updateTitle(new Date());

        int indicatorSize = DensityUtil.dip2px(context,20);

        ImageView leftIcon = new ImageView(getContext());
        leftIcon.setImageDrawable(getResources().getDrawable(R.drawable.indicator_more_left));
        RelativeLayout.LayoutParams leftIconParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, indicatorSize);
        leftIconParams.addRule(RelativeLayout.CENTER_VERTICAL);
        leftIconParams.addRule(RelativeLayout.LEFT_OF,titleTv.getId());
        calTitleBar.addView(leftIcon,leftIconParams);
        leftIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarView.showPreviousMonth();
            }
        });

        ImageView rightIcon = new ImageView(getContext());
        rightIcon.setImageDrawable(getResources().getDrawable(R.drawable.indicator_more_right));
        RelativeLayout.LayoutParams rightIconParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, indicatorSize);
        rightIconParams.addRule(RelativeLayout.CENTER_VERTICAL);
        rightIconParams.addRule(RelativeLayout.RIGHT_OF,titleTv.getId());
        rightIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarView.showNextMonth();
            }
        });
        calTitleBar.addView(rightIcon,rightIconParams);
    }

    private void initCalendar(){
        RelativeLayout.LayoutParams calParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, targetHeight);
        calendarView.setId(generateViewId());
        calParams.addRule(BELOW, calTitleBar.getId());
        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                if (outListener != null){
                    outListener.onDayClick(dateClicked);
                }
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                updateTitle(firstDayOfNewMonth);
                if (outListener != null){
                    outListener.onMonthScroll(firstDayOfNewMonth);
                }
            }
        });
        this.addView(calendarView,calParams);
    }

    private void initDivider(){
        ImageView dividerImgV;
        //divider
        dividerImgV = new ImageView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_BOTTOM);
        params.bottomMargin = 50;
        dividerImgV.setLayoutParams(params);
        dividerImgV.setImageDrawable(getResources().getDrawable(R.drawable.divider_with_shadow));
        this.addView(dividerImgV);
    }

    private void updateTitle(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String monthName = cal.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.getDefault()).toUpperCase();
        String yearName = String.valueOf(cal.get(Calendar.YEAR));
        String dateStr = monthName + " " + yearName;
        titleTv.setText(dateStr);
    }

    public void setBodyListener(CompactCalendarView.CompactCalendarViewListener listener){
        this.outListener = listener;
    }

    public void refreshSlotNum(){
        calendarView.invalidate();
    }
}
