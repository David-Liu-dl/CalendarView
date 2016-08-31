package org.unimelb.itime.vendor.timeslotview;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.helper.MyCalendar;
import org.unimelb.itime.vendor.weekview.WeekViewHeader;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Paul on 25/08/2016.
 */
public class WeekTimeSlotViewHeader extends LinearLayout {
    private int totalHeight;
    private int totalWidth;
    private int paddingTop = 0;
    private int paddingLeft = 0;
    private int viewWidth;

    private MyCalendar myCalendar;
    private ArrayList<TextView> textViews = new ArrayList<>();
    private String[] titles= {"SUN","MON","TUE","WED","TUR","FRI","SAT"};

    private RelativeLayout dateLayout;
    private TextView blankLeftTextView;
    private ArrayList<RelativeLayout> dayBackgroundRelativeLayoutArrayList = new ArrayList<>();
    private ArrayList<RelativeLayout> dayRelativeLayoutArrayList = new ArrayList<>();
    private ArrayList<TextView> dayOfWeekArrayList = new ArrayList<>();
    private ArrayList<TextView> dayOfMonthArrayList = new ArrayList<>();

    public WeekTimeSlotViewHeader(Context context) {
        super(context);
        init();
    }

    public WeekTimeSlotViewHeader(Context context, AttributeSet attrs) {
        super(context, attrs);init();
        init();
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        dateLayout.layout(0, 0, totalWidth , viewWidth);
        blankLeftTextView.layout(0, 0, paddingLeft, viewWidth);
        for (int i =0 ; i<7;i++){
            int backGroundLeft = paddingLeft + viewWidth * i;
            int backGroundTop = 0;
            int backGroundRight = backGroundLeft + viewWidth;
            int backGroundBottom = viewWidth;
            dayBackgroundRelativeLayoutArrayList.get(i).layout(backGroundLeft, backGroundTop, backGroundRight, backGroundBottom);
            dayRelativeLayoutArrayList.get(i).layout(0, 0, viewWidth, viewWidth); // same size as background

            int dayOfWeekLeft = viewWidth/4-5; // for align in parent center, need to change from 0 to viewWidth/4
            int dayOfWeekTop = 0;
            int dayOfWeekRight =  viewWidth;
            int dayOfWeekBottom = viewWidth/2;
            dayOfWeekArrayList.get(i).layout(dayOfWeekLeft, dayOfWeekTop, dayOfWeekRight, dayOfWeekBottom);

            int dayOfMonthLeft = viewWidth*2/5-5;
            int dayOfMonthTop = viewWidth/2;
            int dayOfMonthRight = viewWidth;
            int dayOfMonthBottom = viewWidth;
            dayOfMonthArrayList.get(i).layout(dayOfMonthLeft, dayOfMonthTop, dayOfMonthRight, dayOfMonthBottom);
        }
    }


    private void init(){
        this.setOrientation(VERTICAL);
        dateLayout = new RelativeLayout(getContext());
        blankLeftTextView = new TextView(getContext());
        dateLayout.addView(blankLeftTextView);

        //clean arrayList;
        dayBackgroundRelativeLayoutArrayList.clear();
        dayRelativeLayoutArrayList.clear();
        dayOfWeekArrayList.clear();
        dayOfMonthArrayList.clear();

        for (int i = 0; i<7;i++){
            RelativeLayout dayBackgoundRelativeLayout = new RelativeLayout(getContext());//this for set red background
            dayBackgroundRelativeLayoutArrayList.add(dayBackgoundRelativeLayout); // add to arraylist

            RelativeLayout dayRelativeLayout = new RelativeLayout(getContext());
            dayRelativeLayout.setGravity(Gravity.CENTER);
            dayRelativeLayoutArrayList.add(dayRelativeLayout); // add to arrayList

            TextView dayOfWeek = new TextView(getContext());
            dayOfWeek.setTextSize(12);
            TextView dayOfMonth = new TextView(getContext());
            dayOfMonth.setTextSize(12);

            dayOfWeekArrayList.add(dayOfWeek); // add to arrayList
            dayOfMonthArrayList.add(dayOfMonth); // add to arrayList

            dayRelativeLayout.addView(dayOfWeek); // add to view
            dayRelativeLayout.addView(dayOfMonth); // add to view
            dayBackgoundRelativeLayout.addView(dayRelativeLayout);
            dateLayout.addView(dayBackgoundRelativeLayout);
        }
        this.addView(dateLayout);
    }


    private boolean checkEqualDay(Calendar c1, Calendar c2){
        return
                c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                        && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                        && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }


    public void setMyCalendar(MyCalendar myCalendar) {
        this.myCalendar = myCalendar;
        Calendar calendar = Calendar.getInstance();
        calendar.set(myCalendar.getYear(),myCalendar.getMonth(),myCalendar.getDay(),myCalendar.getHour(),myCalendar.getMinute());

        for (int i = 0 ; i < 7; i++){
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            dayOfMonthArrayList.get(i).setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
            calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH)+1);
            dayBackgroundRelativeLayoutArrayList.get(i).setBackground(null);
            dayOfMonthArrayList.get(i).setTextColor(Color.BLACK); //
            dayOfWeekArrayList.get(i).setTextColor(Color.BLACK); //
        }
        dayOfWeekArrayList.get(0).setText(R.string._1th_of_week);
        dayOfWeekArrayList.get(1).setText(R.string._2th_of_week);
        dayOfWeekArrayList.get(2).setText(R.string._3th_of_week);
        dayOfWeekArrayList.get(3).setText(R.string._4th_of_week);
        dayOfWeekArrayList.get(4).setText(R.string._5th_of_week);
        dayOfWeekArrayList.get(5).setText(R.string._6th_of_week);
        dayOfWeekArrayList.get(6).setText(R.string._7th_of_week);

        if (checkIfTodayInCurrentShowingCalendar()!=-1){
            dayBackgroundRelativeLayoutArrayList.get(checkIfTodayInCurrentShowingCalendar() -1).setBackgroundResource(R.drawable.itime_today_red_rectangle);
            dayOfMonthArrayList.get(checkIfTodayInCurrentShowingCalendar() -1).setTextColor(Color.WHITE);
            dayOfWeekArrayList.get(checkIfTodayInCurrentShowingCalendar() -1).setTextColor(Color.WHITE);
        }
    }

    public int checkIfTodayInCurrentShowingCalendar(){
        Calendar todayCalendar = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.set(myCalendar.getYear(), myCalendar.getMonth(), myCalendar.getDay(), myCalendar.getHour(), myCalendar.getMinute());

        for (int i = 0 ; i< 7; i++){
            if (checkEqualDay(calendar,todayCalendar)) {
                return calendar.get(Calendar.DAY_OF_WEEK);
            }
            else {
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
            }
        }
        return -1;
    }

    public MyCalendar getMyCalendar(){
        return this.myCalendar;
    }


//    ****************************************************************



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        totalHeight = MeasureSpec.getSize(heightMeasureSpec);
        totalWidth = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(totalWidth,totalHeight);
        updateWidthHeight(totalWidth,totalHeight);
    }



    public void updateWidthHeight(int width, int height){
        this.paddingLeft = (int)(width * 0.1); // this is for blank?.
        this.paddingTop = (int)(width * 0.05);
        this.viewWidth = (width - paddingLeft)/7;
    }

}
