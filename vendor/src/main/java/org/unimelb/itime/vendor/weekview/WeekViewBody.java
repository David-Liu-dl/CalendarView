package org.unimelb.itime.vendor.weekview;

import android.content.Context;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.eventview.Event;
import org.unimelb.itime.vendor.eventview.WeekDraggableEventView;
import org.unimelb.itime.vendor.helper.MyCalendar;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
    private RelativeLayout eventWidgetsRelativeLayout;
    private RelativeLayout eventRelativeLayout;
    private TextView msgWindow;
    private TextView currentTimeView;
    private MyDragListener myDragListener;
    private MyCalendar myCalendar;
    Calendar calendar = Calendar.getInstance();
    private ArrayList<ITimeEventInterface> eventArrayList;

//    private ArrayList<WeekDraggableEventView> eventViewArrayList = new ArrayList<>();
    private WeekView.OnClickEventInterface onClickEventInterface;

    public WeekViewBody(Context context) {
        super(context);
        this.setOrientation(HORIZONTAL);
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

        eventWidgetsRelativeLayout = new RelativeLayout(getContext());

        eventRelativeLayout = new RelativeLayout(getContext());
    }

    public void initAll(){
        initWidgets();
        cleanView();
        this.requestLayout();
        eventWidgetsRelativeLayout.requestLayout();
        initLayoutParams();
        initTimeText(getHours());
        initDottedLineDivider(getHours());
        initTimeSlot(getHours());
        initDaySlot();
        initCurrentTimeLine(myCalendar);
        initMsgWindow();
        initEvents();
        eventWidgetsRelativeLayout.invalidate();
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
        eventWidgetsRelativeLayout.removeAllViews();

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

        RelativeLayout.LayoutParams eventBackgroundRelativeLayoutParams = new RelativeLayout.LayoutParams(
                totalWidth - hourWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        eventWidgetsRelativeLayout.setLayoutParams(eventBackgroundRelativeLayoutParams);

        RelativeLayout.LayoutParams eventRelativeLayoutParams = new RelativeLayout.LayoutParams(
                totalWidth - hourWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        eventRelativeLayout.setLayoutParams(eventRelativeLayoutParams);

        eventRelativeLayout.addView(eventWidgetsRelativeLayout);
        weekBodyLinearLayout.addView(timeRelativeLayout);
        weekBodyLinearLayout.addView(eventRelativeLayout);
        backGroundRelativeLayout.addView(weekBodyLinearLayout);
        scrollView.addView(backGroundRelativeLayout);
        this.addView(scrollView);

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
            eventWidgetsRelativeLayout.addView(dottedLine);
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
        msgWindow.setText("msgWindow");
        msgWindow.setVisibility(View.INVISIBLE);
        eventWidgetsRelativeLayout.addView(msgWindow);
    }

    public void initCurrentTimeLine(MyCalendar myCalendar){
        Calendar todayCalendar = Calendar.getInstance(Locale.getDefault());
        todayCalendar.setTime(new Date());
        if (isShowingToday(myCalendar, todayCalendar)) {
            // set the current time line
            ImageView timeLine = new ImageView(getContext());
            timeLine.setImageResource(R.drawable.itime_dotted_line);
            timeLine.setBackgroundColor(Color.RED);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5);

            int currentHour = todayCalendar.get(Calendar.HOUR_OF_DAY);
            int currentMinute = todayCalendar.get(Calendar.MINUTE);
            int currentTimePaddingTop = (int)(currentHour* hourHeight) + (int)(currentMinute * hourHeight / 60) + (int)hourHeight/2 ;

            params.setMargins(hourWidth, currentTimePaddingTop, 0, 0);
            timeLine.setLayoutParams(params);
            backGroundRelativeLayout.addView(timeLine);

            // set the showing time
            currentTimeView = new TextView(getContext());
            String stringCurrentHour = currentHour < 10? "0" +String.valueOf(currentHour) : String.valueOf(currentHour);
            String stringCurrentMinute = currentMinute < 10? "0"+String.valueOf(currentMinute) : String.valueOf(currentMinute);
            String AMPM = currentHour> 12 ? "PM" :"AM";
            currentTimeView.setText(String.format("%s:%s %s", stringCurrentHour, stringCurrentMinute, AMPM));
            currentTimeView.setTextSize(8);
            currentTimeView.setTextColor(Color.RED);
            RelativeLayout.LayoutParams showCurrentTimeLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            showCurrentTimeLayout.topMargin = currentTimePaddingTop - hourHeight/3 ;
            currentTimeView.setLayoutParams(showCurrentTimeLayout);
            backGroundRelativeLayout.addView(currentTimeView);
        }
//        eventRL.invalidate();
    }

    public boolean isShowingToday(MyCalendar myCalendar, Calendar todayCalendar){
        for (int i = 0 ; i < 7 ; i ++){
            if (myCalendar.getDay() == todayCalendar.get(Calendar.DATE) &&
                    myCalendar.getMonth() == todayCalendar.get(Calendar.MONTH) &&
                    myCalendar.getYear() == todayCalendar.get(Calendar.YEAR)){
                return true;
            }else{
                todayCalendar.set(Calendar.DATE,todayCalendar.get(Calendar.DATE) - 1);
            }
        }
        return false;
    }

    public void initEvents(){
        if (this.eventArrayList!=null){
            for (ITimeEventInterface event:eventArrayList){
                Date eventDate = new Date(event.getStartTime());
                Calendar eventCalendar = Calendar.getInstance();
                eventCalendar.setTime(eventDate);
                if (isInCurrentWeek(eventCalendar,myCalendar)){

                    final WeekDraggableEventView eventView = new WeekDraggableEventView(getContext(),event);
                    int eventDayOfWeek = eventCalendar.get(Calendar.DAY_OF_WEEK);
                    int eventStartHour = eventCalendar.get(Calendar.HOUR_OF_DAY);
                    int eventStartMinute = eventCalendar.get(Calendar.MINUTE);

                    int leftOffset = dayWidth * (eventDayOfWeek -1);
                    int topOffSet = (int)((float)hourHeight/2 + ((float)hourHeight/4) *
                            (eventStartHour * 4 + (float)eventStartMinute / 15));
                    long duration = (event.getEndTime() - event.getStartTime())/1000/60;

                    int eventHeight = (int)(duration * hourHeight / 60);
                    RelativeLayout.LayoutParams eventViewParams = new RelativeLayout.LayoutParams(
                            dayWidth,eventHeight);
                    eventViewParams.setMargins(leftOffset,topOffSet , 0, 0);
                    eventView.setLayoutParams(eventViewParams);
                    eventRelativeLayout.addView(eventView);


                    eventView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ITimeEventInterface iTimeEventInterface = eventView.getEvent();
                            onClickEventInterface.editEvent(iTimeEventInterface);
                        }
                    });
                }
            }
        }
    }
    public boolean isInCurrentWeek(Calendar timeSlotCalendar,MyCalendar myCalendar){
        Calendar firstSundayCalendar = Calendar.getInstance();
        firstSundayCalendar.set(Calendar.YEAR, myCalendar.getYear());
        firstSundayCalendar.set(Calendar.MONTH, myCalendar.getMonth());
        firstSundayCalendar.set(Calendar.DAY_OF_MONTH, myCalendar.getDay());
        return (timeSlotCalendar.get(Calendar.WEEK_OF_YEAR) == firstSundayCalendar.get(Calendar.WEEK_OF_YEAR)
                && timeSlotCalendar.get(Calendar.YEAR) == firstSundayCalendar.get(Calendar.YEAR));
    }



    public void setEvents(ArrayList<ITimeEventInterface> eventArrayList){
        this.eventArrayList = eventArrayList;
    }

    public void initDragListener(){
        if (myDragListener == null){
            myDragListener = new MyDragListener();
            eventRelativeLayout.setOnDragListener(myDragListener);
        }else{
            eventRelativeLayout.setOnDragListener(myDragListener);
        }
    }

    public void setOnClickEventInterface(WeekView.OnClickEventInterface onClickEventInterface){
        this.onClickEventInterface = onClickEventInterface;
    }

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

    public WeekView.OnClickEventInterface getOnCLickEventInterface() {
        return onClickEventInterface;
    }

//    ********************************************************************

    private final class MyDragListener implements View.OnDragListener{
        float actionStartX = 0;
        float actionStartY = 0;

        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            switch (dragEvent.getAction()){
                case DragEvent.ACTION_DRAG_STARTED:
                    View currentEventView = (View) dragEvent.getLocalState();
                    actionStartX = dragEvent.getX();
                    actionStartY = dragEvent.getY();
                    msgWindow.setVisibility(View.VISIBLE);
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    scrollViewAutoScroll(dragEvent);
                    msgWindowFollow((int)dragEvent.getX(),(int)dragEvent.getY(),(View)dragEvent.getLocalState());
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:
                    float actionStopX = dragEvent.getX();
                    float actionStopY = dragEvent.getY();
                    // reassign view to viewGroup
                    View currentDragView = (View) dragEvent.getLocalState();
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
                            currentDragView.getLayoutParams();

                    int newX = (int) actionStopX - currentDragView.getWidth()/2;
                    int newY = (int) actionStopY - currentDragView.getHeight()/2;
                    int[] reComputeResult = reComputePositionToSet(newX, newY,currentDragView,view);

                    params.leftMargin = reComputeResult[0];
                    params.topMargin = reComputeResult[1];
                    currentDragView.setLayoutParams(params);
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    View finalView = (View) dragEvent.getLocalState();
                    finalView.getBackground().setAlpha(150);
                    msgWindow.setVisibility(View.INVISIBLE);
                    break;
                default:
                    break;
            }
            return true;
        }
    }
    private  int[] reComputePositionToSet(int actualX, int actualY, View draggableObj, View container){
        int containerWidth = container.getWidth();
        int containerHeight = container.getHeight();

        int objWidth = draggableObj.getWidth();
        int objHeight = draggableObj.getHeight();

        int finalX = actualX;
        int finalY = actualY;

        if (actualX < 0){
            finalX = 0;
        }else if (actualX + objWidth > containerWidth){
            finalX = containerWidth - objWidth;
        }

        if (actualY < 0){
            finalY = 0;
        }else if(actualY + objHeight > containerHeight){
            finalY = containerHeight - objHeight;
        }
        int findNearestPositionY = nearestTimeSlotKey(finalY);
        int findNearestPositionX = nearestDaySlotKey(finalX);
        if (findNearestPositionY != -1){
            finalY = findNearestPositionY;
        }else{
            Log.d("TAG", "reComputePositionToSet: "+ "Error no such position");

        }

        if (findNearestPositionX != -1){
            finalX = findNearestPositionX;
        }else{
            Log.d("TAG", "reComputePositionToSet: "+ "Error no such position");
        }
        return new int[] {finalX, finalY};
    }

    private int nearestTimeSlotKey(int tapY){
        int key = tapY;
        Map.Entry<Integer, String> low = timeSlotTreeMap.floorEntry(key);
        Map.Entry<Integer, String> high = timeSlotTreeMap.ceilingEntry(key);
        if (low != null && high != null){
            return Math.abs(key - low.getKey()) < Math.abs(key - high.getKey())
                    ? low.getKey()
                    : high.getKey();
        }else if (low != null || high != null){
            return low != null ? low.getKey() : high.getKey();
        }
        return -1;
    }

    private int nearestDaySlotKey(int tapX){
        int key = tapX;
        Map.Entry<Integer, String> low = daySlotTreeMap.floorEntry(key);
        Map.Entry<Integer, String> high = daySlotTreeMap.ceilingEntry(key);
        if (low != null && high != null){
            return Math.abs(key - low.getKey()) < Math.abs(key - high.getKey())
                    ? low.getKey()
                    : high.getKey();
        }else if(low != null || high != null){
            return low != null? low.getKey() : high.getKey();
        }
        return -1;
    }

    public void scrollViewAutoScroll(DragEvent event){
        Rect scrollBounds = new Rect();
        scrollView.getDrawingRect(scrollBounds);
        float heightOfView = ((View)event.getLocalState()).getHeight();
        float needPositionY_top = event.getY() - heightOfView/2;
        float needPositionY_bottom = event.getY() + heightOfView/2;

        if (scrollBounds.top > needPositionY_top){
            int offsetY = (int)(scrollView.getScrollY() + needPositionY_top - scrollBounds.top);
            scrollView.scrollTo(scrollView.getScrollX(), offsetY);
        } else if(scrollBounds.bottom < needPositionY_bottom){
            int offsetY = (int)(scrollView.getScrollY() + needPositionY_bottom - scrollBounds.bottom);
            scrollView.scrollTo(scrollView.getScrollX(), offsetY);
        }
    }

    private void msgWindowFollow(int tapX, int tapY, View followView){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)msgWindow.getLayoutParams();
        params.topMargin = tapY - followView.getHeight()/2 - msgWindow.getHeight();

        if(tapX + msgWindow.getWidth()/2 > eventRelativeLayout.getWidth()){
            params.leftMargin = eventRelativeLayout.getWidth() - msgWindow.getWidth();
        }else if(tapX - msgWindow.getWidth()/2 < 0){
            params.leftMargin = 0;
        }else{
            params.leftMargin = tapX - msgWindow.getWidth()/2;
        }
        int nearestProperPosition = nearestTimeSlotKey(tapY - followView.getHeight()/2);
        int nearestProperDay = nearestDaySlotKey(tapX - followView.getWidth()/2);
        if (nearestProperPosition != -1 && nearestProperDay != -1){
//            Log.d("day",daySlotTreeMap.get(nearestProperDay));
//            Log.d("time",timeSlotTreeMap.get(nearestProperPosition));
            msgWindow.setVisibility(View.VISIBLE);
            msgWindow.setText(daySlotTreeMap.get(nearestProperDay) + " "+ timeSlotTreeMap.get(nearestProperPosition) );

        }else{
            Log.d("TAG","msgWindowFollow: "+ "Error, text not found in Map");
        }
        msgWindow.setLayoutParams(params);
        //msgWindow.setVisibility(View.VISIBLE);
    }


}
