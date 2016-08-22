package org.unimelb.itime.vendor.weekview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.unimelb.itime.vendor.R;

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
    private int numOfHourShowInScreen = 12;
    private int leftTimeBarPortion = 10;

    private TreeMap<Integer,String> timeSlotTreeMap = new TreeMap<>();
    private TreeMap<Integer, String> daySlotTreeMap = new TreeMap<>();

    private RelativeLayout backGroundRelativeLayout;
    private LinearLayout weekBodyLinearLayout;
    private RelativeLayout timeRelativeLayout;
    private RelativeLayout eventRelativeLayout;
    private TextView msgWindow;
//    private MyDragListener myDragListener;

    Calendar calendar = Calendar.getInstance();

    public WeekViewBody(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOrientation(HORIZONTAL);
        inflate(context, R.layout.week_view_body, this);
        initWidgets();
    }

    private void initWidgets(){
        backGroundRelativeLayout = (RelativeLayout)findViewById(R.id._itime_week_body_background_relativeLayout);
        weekBodyLinearLayout = (LinearLayout)findViewById(R.id._itime_week_body_linearLayout);
        timeRelativeLayout = (RelativeLayout)findViewById(R.id._itime_time_relativeLayout);
        eventRelativeLayout = (RelativeLayout)findViewById(R.id._itime_event_relativeLayout);
    }

    private void initAll(){
        initTimeText(getHours());
        initDottedLineDivider(getHours());
        initTimeSlot(getHours());
        initDaySlot();
        initMsgWindow();
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
            ImageView dottedLine = new ImageView(getContext());
            RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,dayHeight);
            params.setMargins(0, hourHeight * time, 0, 0);
            dottedLine.setImageResource(R.drawable.itime_dotted_line);
            dottedLine.setLayoutParams(params);
            dottedLine.setLayerType(dottedLine.LAYER_TYPE_SOFTWARE,null);
            eventRelativeLayout.addView(dottedLine);
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
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,0,0);
        msgWindow.setLayoutParams(params);
        msgWindow.setTextSize(20);
        msgWindow.setVisibility(View.INVISIBLE);
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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initAll();
    }

    public void updateWidthHeight(int totalWidth, int totalHeight){
        this.hourHeight = totalHeight/numOfHourShowInScreen;
        this.hourWidth = totalWidth / 10;
        this.dayHeight = hourHeight;
        this.dayWidth = (int)(totalWidth * 0.9) / 7;
    }

//    ********************************************************************

}
