package org.unimelb.itime.vendor.weekview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.helper.MyCalendar;

import java.util.Calendar;
import java.util.TreeMap;

/**
 * Created by yinchuandong on 22/08/2016.
 */
public class WeekViewBody extends LinearLayout{
    private int totalHeight;
    private int totalWidth;
    private int hourHeight;
    private int hourWidth;
    private int dayHeight;
    private int dayWidth;
    private int numOfHourShowInScreen = 8;
    private int leftTimeBarPortion = 10;

    private TreeMap<Integer,String> timeSlotTreeMap = new TreeMap<>();
    private TreeMap<Integer, String> daySlotTreeMap = new TreeMap<>();

    private ScrollView scrollView;
    private RelativeLayout backGroundRelativeLayout;
    private LinearLayout weekBodyLinearLayout;
    private RelativeLayout timeRelativeLayout;
    private RelativeLayout eventRelativeLayout;
    private TextView msgWindow;
//    private MyDragListener myDragListener;
    private MyCalendar myCalendar;
    Calendar calendar = Calendar.getInstance();

    public WeekViewBody(Context context) {
        super(context);
        this.setOrientation(HORIZONTAL);
//        inflate(context, R.layout.week_view_body, this);
//        initWidgets();
    }

    public WeekViewBody(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOrientation(HORIZONTAL);
//        inflate(context, R.layout.week_view_body, this);
//        initWidgets();
    }

    private void initWidgets(){
        // init scrollView
        scrollView = new ScrollView(getContext());

        // init background relativeLayout
        backGroundRelativeLayout = new RelativeLayout(getContext());
        // init linearLayout which will contain timeRelativeLayout and eventRelativeLayout
        weekBodyLinearLayout = new LinearLayout(getContext());
        weekBodyLinearLayout.setOrientation(HORIZONTAL);

        // init timeRelativeLayout
        timeRelativeLayout = new RelativeLayout(getContext());

        eventRelativeLayout = new RelativeLayout(getContext());
    }

    public void initAll(){
        initWidgets();
        cleanView();
        this.requestLayout();
        eventRelativeLayout.requestLayout();
        initLayoutParams();
        initTimeText(getHours());
        initDottedLineDivider(getHours());
        initTimeSlot(getHours());
        initDaySlot();
//        Log.i("number of children", String.valueOf(eventRelativeLayout.getChildCount()));
//        initMsgWindow();
        eventRelativeLayout.invalidate();
    }

    public void cleanView(){
//        Log.i("weekViewBody", String.valueOf(this.getChildCount()));
        this.removeAllViews();
//        Log.i("scrollView", String.valueOf(scrollView.getChildCount()));
        scrollView.removeAllViews();
//        Log.i("backGroundRelativeLayout", String.valueOf(backGroundRelativeLayout.getChildCount()));
        backGroundRelativeLayout.removeAllViews();
//        Log.i("weekBody", String.valueOf(weekBodyLinearLayout.getChildCount()));
        weekBodyLinearLayout.removeAllViews();
//        Log.i("timeRL", String.valueOf(timeRelativeLayout.getChildCount()));
        timeRelativeLayout.removeAllViews();
//        Log.i("eventRL", String.valueOf(eventRelativeLayout.getChildCount()));
        eventRelativeLayout.removeAllViews();
    }

    private void initLayoutParams(){
        ScrollView.LayoutParams scrollViewParams = new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        scrollView.setLayoutParams(scrollViewParams);

        RelativeLayout.LayoutParams backGroundParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        backGroundRelativeLayout.setLayoutParams(backGroundParams);

        LinearLayout.LayoutParams weekBodyLinearLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        weekBodyLinearLayout.setLayoutParams(weekBodyLinearLayoutParams);

        RelativeLayout.LayoutParams timeRelativeLayoutParams = new RelativeLayout.LayoutParams(
                hourWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        timeRelativeLayout.setLayoutParams(timeRelativeLayoutParams);

        RelativeLayout.LayoutParams eventRelativeLayoutParams = new RelativeLayout.LayoutParams(
                totalWidth - hourWidth, ViewGroup.LayoutParams.MATCH_PARENT);
//        eventRelativeLayout.setTop(0);
        eventRelativeLayout.setLayoutParams(eventRelativeLayoutParams);

        weekBodyLinearLayout.addView(timeRelativeLayout);
        weekBodyLinearLayout.addView(eventRelativeLayout);
        backGroundRelativeLayout.addView(weekBodyLinearLayout);
        scrollView.addView(backGroundRelativeLayout);
        this.addView(scrollView);

//        this.addView(scrollView);
//        scrollView.addView(backGroundRelativeLayout);
//        backGroundRelativeLayout.addView(weekBodyLinearLayout);
//        weekBodyLinearLayout.addView(timeRelativeLayout);
//        weekBodyLinearLayout.addView(eventRelativeLayout);

    }

    private void initTimeText(String[] hours){
        for (int time = 0; time< hours.length; time++){
            TextView timeView = new TextView(getContext());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,hourHeight);
            params.setMargins(0, hourHeight * time , 0, 0);
            timeView.setLayoutParams(params);
            timeView.setText(hours[time].substring(0,2));
            timeView.setTextSize(12);
            timeView.setGravity(Gravity.CENTER);
            timeRelativeLayout.addView(timeView);
        }
    }

    private void initDottedLineDivider(String[] hours){
        for (int time = 0; time < hours.length; time++ ){
            TextView dottedLine = new TextView(getContext());
            dottedLine.setBackgroundDrawable(getResources().getDrawable(R.drawable.itime_dotted_line));
            RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, hourHeight / 10);
            params.setMargins(0, hourHeight * time + (int)(0.5 * hourHeight)
                    - (int)(hourHeight /20), 0, 0);
            dottedLine.setLayerType(dottedLine.LAYER_TYPE_SOFTWARE, null);
            dottedLine.setLayoutParams(params);
            eventRelativeLayout.addView(dottedLine);
//            ImageView dottedLine = new ImageView(getContext());
//            RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            params.setMargins(0, hourHeight * time, 0, 0);
//            dottedLine.setImageResource(R.drawable.itime_dotted_line);
//            dottedLine.setLayoutParams(params);
//            dottedLine.setLayerType(dottedLine.LAYER_TYPE_SOFTWARE,null);
//            eventRelativeLayout.addView(dottedLine);




        }
    }

    private void initTimeSlot(String[] hours){
        timeSlotTreeMap.clear();
        double startPoint = hourHeight /2;
        double timeSlotHeight = hourHeight /4;
        for(int time = 0; time < hours.length; time++){
            timeSlotTreeMap.put((int)startPoint + hourHeight * time, hours[time]);
            String hourPart = hours[time].substring(0,2);
            for (int quarterSlot = 0 ; quarterSlot < 3; quarterSlot ++){
                String minute = String.valueOf((quarterSlot+1)*15);
                String thisTime = hourPart + ":" + minute;
                int PositionY = (int)(startPoint + hourHeight * time + timeSlotHeight * (quarterSlot +1));
                timeSlotTreeMap.put(PositionY,thisTime);
            }
        }
    }

    private void initDaySlot(){
        double startPoint =0;
        double daySlotWidth = dayWidth;
        String[] days = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
        for (int slot = 0; slot <7; slot++ ){
            int positionX = (int)startPoint + (int)daySlotWidth * slot;
            daySlotTreeMap.put( positionX ,days[slot]);
        }
    }

    private void initMsgWindow(){
        msgWindow = new TextView(getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        msgWindow.setLayoutParams(params);
        msgWindow.setPadding(0,0,0,0);
        msgWindow.setTextSize(20);
        msgWindow.setBackgroundColor(Color.GREEN);
        msgWindow.setText("msgWindow");
        msgWindow.setVisibility(View.VISIBLE);
        eventRelativeLayout.addView(msgWindow);
    }

//    public void initDragListener(){
//        if (myDragListener == null){
//            myDragListener = new MyDragListener();
//            eventRL.setOnDragListener(myDragListener);
//        }else{
//            eventRL.setOnDragListener(myDragListener);
//        }
//    }

    private String[] getHours(){
        String[] HOURS = new String[]{
                "00:00","01:00","02:00","03:00","04:00","05:00","06:00","07:00",
                "08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00",
                "16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00",
                "24:00"
        };
        return  HOURS;
    }

    public void setNumOfHourShowInScreen(int number){
        this.numOfHourShowInScreen = number;
    }

//    *******************************************************************

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.totalWidth = MeasureSpec.getSize(widthMeasureSpec);
        this.totalHeight = MeasureSpec.getSize(heightMeasureSpec);
        updateWidthHeight(totalWidth,totalHeight);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        Log.i("on finishInflate","here here");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
//        Log.i("on Layout","here here");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initAll();
    }

    public void updateWidthHeight(int totalWidth, int totalHeight){
        this.totalHeight = totalHeight;
        this.totalWidth = totalWidth;
        this.hourHeight = totalHeight/numOfHourShowInScreen;
        this.hourWidth = totalWidth / 10;
        this.dayHeight = hourHeight;
        this.dayWidth = (int)(totalWidth * 0.9) / 7;
    }

    public MyCalendar getMyCalendar() {
        return myCalendar;
    }

    public void setMyCalendar(MyCalendar myCalendar) {
        this.myCalendar = myCalendar;
    }
//    ********************************************************************

}
