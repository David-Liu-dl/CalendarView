package org.unimelb.itime.vendor.weekview;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.helper.MyCalendar;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by yinchuandong on 22/08/2016.
 */
public class WeekViewHeader extends LinearLayout{
    private int totalHeight;
    private int totalWidth;
    private int paddingTop = 0;
    private int paddingLeft = 0;
    private int paddingBottom = 0;
    private int viewWidth;
    private int textSize = 12;
    private int titleTextSize = 20;

    private MyCalendar myCalendar;
    private LinearLayout dateLayout;
//    private RelativeLayout titleLayout;
    private ArrayList<TextView> textViews = new ArrayList<>();
    ImageView newEventCreateImageView;
    private String[] titles= {"SUN","MON","TUE","WED","TUR","FRI","SAT"};

    public WeekViewHeader(Context context) {
        super(context);
        this.setOrientation(VERTICAL);
    }

    public WeekViewHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOrientation(VERTICAL);
    }


    public void initCurrentWeekHeaders(){
        this.removeAllViews();
        textViews.clear();

//        titleLayout = new RelativeLayout(getContext());
        dateLayout = new LinearLayout(getContext());
//        this.addView(titleLayout);
        this.addView(dateLayout);
        dateLayout.removeAllViews();
//        titleLayout.removeAllViews();

//        titleLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,totalHeight/3));
        dateLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,totalHeight - totalHeight/3));
        Calendar todayCalendar = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, myCalendar.getYear());
        calendar.set(Calendar.MONTH, myCalendar.getMonth());
        calendar.set(Calendar.DATE, myCalendar.getDay());

        //****************************************************
//        TextView calendarTitleText = new TextView(getContext());
//        RelativeLayout.LayoutParams calendarTitleTextParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        calendarTitleTextParams.addRule(RelativeLayout.CENTER_IN_PARENT);
//        calendarTitleText.setLayoutParams(calendarTitleTextParams);
//        calendarTitleText.setText(new StringBuilder().append(getMonthName(calendar.get(Calendar.MONTH))).append("  ").append(calendar.get(Calendar.YEAR)).toString());
//        calendarTitleText.setGravity(Gravity.CENTER);
//        calendarTitleText.setTextSize(titleTextSize);
//        titleLayout.addView(calendarTitleText);
//
//
//
//        newEventCreateImageView = new ImageView(getContext());
//        newEventCreateImageView.setImageResource(R.drawable.itime_create_new_event);
//        RelativeLayout.LayoutParams newEventCreateImageViewParams = new RelativeLayout.LayoutParams(totalHeight/4, totalHeight/4);
//        newEventCreateImageViewParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        newEventCreateImageViewParams.addRule(RelativeLayout.CENTER_VERTICAL);
//        newEventCreateImageViewParams.setMarginEnd(totalHeight/10);
//        newEventCreateImageView.setLayoutParams(newEventCreateImageViewParams);
//        titleLayout.addView(newEventCreateImageView);

        //*************************************************

        TextView blankView = new TextView(getContext());
        RelativeLayout.LayoutParams blankParams = new RelativeLayout.LayoutParams(paddingLeft, ViewGroup.LayoutParams.MATCH_PARENT);
        blankView.setLayoutParams(blankParams);
        dateLayout.addView(blankView);

        for (int day = 0; day < 7; day++){
            LinearLayout dateLinearLayout = new LinearLayout(getContext());
            dateLinearLayout.setOrientation(LinearLayout.VERTICAL);
            LayoutParams params = new LayoutParams(viewWidth, ViewGroup.LayoutParams.MATCH_PARENT);
            int dateLinearLayoutHeight = params.height;
            TextView dayOfWeek = new TextView(getContext());
            TextView dayOfMonth = new TextView(getContext());
            dayOfMonth.setGravity(Gravity.CENTER);
            dayOfWeek.setGravity(Gravity.CENTER);

            dateLinearLayout.setLayoutParams(params);
            dateLayout.addView(dateLinearLayout);

            if(checkEqualDay( todayCalendar, calendar)){
                LinearLayout todayBackGroundLayout = new LinearLayout(getContext());
                LayoutParams todayBackGroundLP = new LayoutParams(viewWidth, viewWidth);
                todayBackGroundLP.topMargin = (totalHeight*2/3 - viewWidth)/2;
                todayBackGroundLayout.setLayoutParams(todayBackGroundLP);
                todayBackGroundLayout.setOrientation(LinearLayout.VERTICAL);
                todayBackGroundLayout.setBackgroundResource(R.drawable.itime_today_red_rectangle);
                dayOfWeek.setTextColor(Color.WHITE);
                dayOfMonth.setTextColor(Color.WHITE);
                dayOfWeek.setTextSize(textSize);
                dayOfMonth.setTextSize(textSize);
                dayOfWeek.setText(getWeekOfDay(day));
                dayOfMonth.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));

                LayoutParams dayOfWeekParams = new LayoutParams(viewWidth,viewWidth /2);
                dayOfWeek.setLayoutParams(dayOfWeekParams);
                LayoutParams dayOfMonthParams = new LayoutParams(viewWidth, viewWidth /2);
                dayOfMonth.setLayoutParams(dayOfMonthParams);
                todayBackGroundLayout.addView(dayOfWeek);
                todayBackGroundLayout.addView(dayOfMonth);
                dateLinearLayout.addView(todayBackGroundLayout);
                textViews.add(dayOfMonth);
            }else{
                dayOfWeek.setTextSize(textSize);
                dayOfMonth.setTextSize(textSize);
                dayOfWeek.setText(getWeekOfDay(day));
                dayOfMonth.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
                LayoutParams dayOfWeekParams = new LayoutParams(viewWidth, viewWidth /2);
                dayOfWeekParams.topMargin = (totalHeight * 2 / 3 - viewWidth)/2;
                dayOfWeek.setLayoutParams(dayOfWeekParams);
                LayoutParams dayOfMonthParams = new LayoutParams(viewWidth, viewWidth/2);
                dayOfMonth.setLayoutParams(dayOfMonthParams);
                dateLinearLayout.addView(dayOfWeek);
                dateLinearLayout.addView(dayOfMonth);
                textViews.add(dayOfMonth);
            }
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        }
        dateLayout.invalidate();
//        titleLayout.invalidate();
    }

    private boolean checkEqualDay(Calendar c1, Calendar c2){
        return
                c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                        && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                        && c1.get(Calendar.DATE) == c2.get(Calendar.DATE);
    }

    private String getMonthName(int index){
        String[] Months = {"January","February","March","April","May","June",
                "July","August","September","October","November","December"};
        return Months[index];
    }

    private String getWeekOfDay(int day){
        return titles[day];
    }

    public void setMyCalendar(MyCalendar myCalendar) {
        this.myCalendar = myCalendar;
    }

    public MyCalendar getMyCalendar(){
        return this.myCalendar;
    }


//    ****************************************************************

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initCurrentWeekHeaders();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        totalHeight = MeasureSpec.getSize(heightMeasureSpec);
        totalWidth = MeasureSpec.getSize(widthMeasureSpec);
        updateWidthHeight(totalWidth,totalHeight);
    }

    public void updateWidthHeight(int width, int height){
        this.totalWidth = width;
        this.totalHeight=height;
        this.paddingLeft = (int)(width * 0.1); // this is for blank?.
        this.paddingTop = (int)(width * 0.05);
        this.viewWidth = (width - paddingLeft)/7;
    }


}
