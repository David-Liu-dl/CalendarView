package org.unimelb.itime.vendor.timeslotview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.eventview.WeekDraggableEventView;
import org.unimelb.itime.vendor.helper.MyCalendar;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;
import org.unimelb.itime.vendor.timeslot.TimeSlotView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Paul on 25/08/2016.
 */
public class WeekTimeSlotViewBody extends LinearLayout {
    private int totalHeight = 0;
    private int totalWidth = 0;
    private int hourHeight = 0;
    private int hourWidth = 0;
    private int dayHeight = 0;
    private int dayWidth = 0;
    private int oneWeekWidth = 0;
    private int numOfHourShowInScreen = 8;

    private Map<Long, Boolean> timeSlots;
    private int duration =0;
    private ArrayList<ITimeEventInterface> eventArrayList = new ArrayList<>();
    private ArrayList<WeekDraggableEventView> eventViewArrayList = new ArrayList<>();

    private TreeMap<Integer, String> timeSlotTreeMap = new TreeMap<>();
    private TreeMap<Integer, String> daySlotTreeMap = new TreeMap<>();

    private ScrollView scrollView;
    private RelativeLayout backGroundRelativeLayout;
    private LinearLayout weekBodyLinearLayout;
    private RelativeLayout timeRelativeLayout;
    private RelativeLayout eventWidgetsRelativeLayout;
    private RelativeLayout eventRelativeLayout;
    private RelativeLayout timeSlotRelativeLayout;
    private RelativeLayout eventAndWidgetsRelativeLayout;
    private TextView msgWindow;
    private TextView timeSlotTextView;
    private ArrayList<TimeSlotView> timeSlotViewArrayList = new ArrayList<>();
    private ArrayList<ImageView> timeSlotTimeLineArrayList = new ArrayList<>();
    private ArrayList<TextView> timeSlotTimeTextViewArrayList = new ArrayList<>();
    private MyDragListener myDragListener;
    private MyCalendar myCalendar;
    Calendar calendar = Calendar.getInstance();


    private TextView[] hourTextViewArr = new TextView[getHours().length];
    private TextView[] timeLineTextViewArr = new TextView[getHours().length];
    private TextView currentTimeLineTextView;
    private TextView currentTimeTextView;

    private int dottedLineHeight = 0;

    private WeekTimeSlotView.OnTimeSlotClickListener onTimeSlotClickListener;

    public WeekTimeSlotViewBody(Context context) {
        super(context);
        init();
    }

    public WeekTimeSlotViewBody(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public void init() {
        myCalendar = new MyCalendar(calendar);
        initWidgets();
        initHourTextViews();
        initTimeDottedLine();
        initMsgWindow();
    }



    private void initWidgets() {
        // init scrollView
        this.removeAllViews();
        scrollView = new ScrollView(getContext());
        // init background relativeLayout
        backGroundRelativeLayout = new RelativeLayout(getContext());
        // init linearLayout which will contain timeRelativeLayout and eventRelativeLayout
        weekBodyLinearLayout = new LinearLayout(getContext());
        weekBodyLinearLayout.setOrientation(HORIZONTAL);

        // init timeRelativeLayout
        timeRelativeLayout = new RelativeLayout(getContext());

        eventWidgetsRelativeLayout = new RelativeLayout(getContext());
        eventWidgetsRelativeLayout.setPadding(20,0,0,0);

        eventRelativeLayout = new RelativeLayout(getContext());
        timeSlotRelativeLayout = new RelativeLayout(getContext());

        eventAndWidgetsRelativeLayout = new RelativeLayout(getContext());

        eventAndWidgetsRelativeLayout.addView(timeSlotRelativeLayout);
        eventAndWidgetsRelativeLayout.addView(eventRelativeLayout);
        eventAndWidgetsRelativeLayout.addView(eventWidgetsRelativeLayout);
        weekBodyLinearLayout.addView(timeRelativeLayout);
        weekBodyLinearLayout.addView(eventAndWidgetsRelativeLayout);
        backGroundRelativeLayout.addView(weekBodyLinearLayout);
        scrollView.addView(backGroundRelativeLayout);
        this.addView(scrollView);

    }

    public void initHourTextViews() {
        for (int i = 0; i < getHours().length; i++) {
            hourTextViewArr[i] = new TextView(getContext());
            hourTextViewArr[i].setText(getHours()[i]);
            hourTextViewArr[i].setTextSize(12);
            timeRelativeLayout.addView(hourTextViewArr[i]);
        }
    }

    public void initTimeDottedLine() {
        for (int i = 0; i < getHours().length; i++) {
            timeLineTextViewArr[i] = new TextView(getContext());
            timeLineTextViewArr[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.itime_dotted_line));
            timeLineTextViewArr[i].setLayerType(timeLineTextViewArr[i].LAYER_TYPE_SOFTWARE, null);
            timeLineTextViewArr[i].setGravity(Gravity.CENTER);
            eventWidgetsRelativeLayout.addView(timeLineTextViewArr[i]);
        }
    }

    private void initCurrentTimeLine() {
        Calendar todayCalendar = Calendar.getInstance(Locale.getDefault());
        todayCalendar.setTime(new Date());
        if (isShowingToday(myCalendar, todayCalendar)) {
            // set the current time line
            currentTimeLineTextView = new TextView(getContext());
            currentTimeLineTextView.setBackgroundResource(R.drawable.itime_dotted_line);
            currentTimeLineTextView.setBackgroundColor(Color.RED);
            backGroundRelativeLayout.addView(currentTimeLineTextView);

            // set the showing time
            int currentHour = todayCalendar.get(Calendar.HOUR_OF_DAY);
            int currentMinute = todayCalendar.get(Calendar.MINUTE);
            currentTimeTextView = new TextView(getContext());
            String stringCurrentHour = currentHour < 10 ? "0" + String.valueOf(currentHour) : String.valueOf(currentHour);
            String stringCurrentMinute = currentMinute < 10 ? "0" + String.valueOf(currentMinute) : String.valueOf(currentMinute);
            String AMPM = currentHour > 12 ? "PM" : "AM";
            currentTimeTextView.setText(String.format("%s:%s %s", stringCurrentHour, stringCurrentMinute, AMPM));
            currentTimeTextView.setTextSize(8);
            currentTimeTextView.setTextColor(Color.RED);
            backGroundRelativeLayout.addView(currentTimeTextView);
        }
    }

    public void initMsgWindow() {
        msgWindow = new TextView(getContext());
        msgWindow.setTextSize(20);
        msgWindow.setText("msgWindow");
        msgWindow.setVisibility(View.INVISIBLE);
        eventWidgetsRelativeLayout.addView(msgWindow);
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

    public boolean isShowingToday(MyCalendar myCalendar, Calendar todayCalendar) {
        for (int i = 0; i < 7; i++) {
            if (myCalendar.getDay() == todayCalendar.get(Calendar.DATE) &&
                    myCalendar.getMonth() == todayCalendar.get(Calendar.MONTH) &&
                    myCalendar.getYear() == todayCalendar.get(Calendar.YEAR)) {
                return true;
            } else {
                todayCalendar.set(Calendar.DATE, todayCalendar.get(Calendar.DATE) - 1);
            }
        }
        return false;
    }


    public void initDragListener(){
        if (myDragListener == null){
            myDragListener = new MyDragListener();
            eventRelativeLayout.setOnDragListener(myDragListener);
        }else{
            eventRelativeLayout.setOnDragListener(myDragListener);
        }
    }


    public void initEvents(){
        if (eventArrayList!=null){
            //first remove contains
            eventRelativeLayout.removeAllViews();
            for (ITimeEventInterface event: eventArrayList){
                Date eventDate = new Date(event.getStartTime());
                Calendar eventCalendar = Calendar.getInstance();
                eventCalendar.setTime(eventDate);
                if (isInCurrentWeek(eventCalendar,myCalendar)) {
                    WeekDraggableEventView eventView = new WeekDraggableEventView(getContext(),event);
                    eventViewArrayList.add(eventView);
                    eventRelativeLayout.addView(eventView);
                }
            }
        }
    }

    public void updateEvents(){
        if (eventViewArrayList!=null){
            for (WeekDraggableEventView eventView:eventViewArrayList){
                long startTime = eventView.getEvent().getStartTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(startTime);
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int topOffset = hourHeight * hour + hourHeight * minute/60 + dottedLineHeight/2;
                int leftOffSet = dayWidth * (day-1);
                int duration = (int) ((eventView.getEvent().getEndTime() - eventView.getEvent().getStartTime())/1000/60);
                int eventHeight = duration *hourHeight / 60;
                eventView.layout(leftOffSet, topOffset, leftOffSet + dayWidth, topOffset + eventHeight);
            }
        }
    }




    public void initTimeSlots(){
        if (timeSlots!=null){
            timeSlotRelativeLayout.removeAllViews();
            for (Long startTime: timeSlots.keySet()){
                boolean isChoose = timeSlots.get(startTime);
                Date timeSlotDate = new Date(startTime);
                Calendar timeSlotCalendar = Calendar.getInstance();
                timeSlotCalendar.setTime(timeSlotDate);
                Log.i("myCalendar",myCalendar.toString());
                String str = String.valueOf(timeSlotCalendar.get(Calendar.DAY_OF_MONTH)) +" " +  String.valueOf(timeSlotCalendar.get(Calendar.MONTH));
                Log.i("timeSlotCalendar",str);
                if (isInCurrentWeek(timeSlotCalendar, myCalendar)) {
                    TimeSlotView timeSlotView = new TimeSlotView(
                            getContext(), startTime, duration,isChoose);
                    timeSlotRelativeLayout.addView(timeSlotView);

                    // add time line
                    ImageView timeSlotTimeLine = new ImageView(getContext());
                    timeSlotTimeLine.setImageResource(R.drawable.itime_dotted_line);
                    timeSlotTimeLine.setBackgroundColor(getResources().getColor(R.color.deeppink));
                    backGroundRelativeLayout.addView(timeSlotTimeLine);

                    // show the time text
                    int timeSlotDayOfWeek = timeSlotCalendar.get(Calendar.DAY_OF_WEEK);
                    int timeSlotStartHour = timeSlotCalendar.get(Calendar.HOUR_OF_DAY);
                    int timeSlotStartMinute = timeSlotCalendar.get(Calendar.MINUTE);
                    timeSlotTextView = new TextView(getContext());
                    String stringCurrentHour = timeSlotStartHour < 10? "0" +String.valueOf(timeSlotStartHour) : String.valueOf(timeSlotStartHour);
                    String stringCurrentMinute = timeSlotStartMinute < 10? "0"+String.valueOf(timeSlotStartMinute) : String.valueOf(timeSlotStartMinute);
                    String AMPM = timeSlotStartHour> 12 ? "PM" :"AM";
                    timeSlotTextView.setText(String.format("%s:%s %s", stringCurrentHour, stringCurrentMinute, AMPM));
                    timeSlotTextView.setTextSize(8);
                    timeSlotTextView.setTextColor(Color.RED);
                    backGroundRelativeLayout.addView(timeSlotTextView);

                    addTimeSlotAndTimeLineAndTimeText(timeSlotView, timeSlotTimeLine, timeSlotTextView);
                }
            }
        }
    }

    public void addTimeSlotAndTimeLineAndTimeText(TimeSlotView timeSlotView, ImageView timeLine, TextView timeText){
        timeSlotViewArrayList.add(timeSlotView);
        timeSlotTimeLineArrayList.add(timeLine);
        timeSlotTimeTextViewArrayList.add(timeText);
    }

    public void deleteTimeSlotAndTimeLineAndTimeText(TimeSlotView timeSlotView, ImageView timeLine, TextView timeText){
        timeSlotViewArrayList.remove(timeSlotView);
        timeSlotTimeLineArrayList.remove(timeLine);
        timeSlotTimeTextViewArrayList.remove(timeText);
    }


    public boolean isInCurrentWeek(Calendar timeSlotCalendar, MyCalendar myCalendar) {
        Calendar firstSundayCalendar = Calendar.getInstance();
        firstSundayCalendar.set(Calendar.YEAR, myCalendar.getYear());
        firstSundayCalendar.set(Calendar.MONTH, myCalendar.getMonth());
        firstSundayCalendar.set(Calendar.DAY_OF_MONTH, myCalendar.getDay());
        return (timeSlotCalendar.get(Calendar.WEEK_OF_YEAR) == firstSundayCalendar.get(Calendar.WEEK_OF_YEAR)
                && timeSlotCalendar.get(Calendar.YEAR) == firstSundayCalendar.get(Calendar.YEAR));
    }



    public void setTimeSlots(Map<Long,Boolean> timeSlots, int duration){
        this.timeSlots = timeSlots;
        this.duration = duration;
        initTimeSlots();
        updateTimeSlot();
        requestLayout();
        invalidate();
    }



    public void setEvents(ArrayList<ITimeEventInterface> eventArrayList){
        this.eventArrayList = eventArrayList;
        initEvents();
        updateEvents();
        requestLayout();
        invalidate();
    }

    private String[] getHours() {
        String[] HOURS = new String[]{
                "00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00",
                "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00",
                "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00",
                "24:00"
        };
        return HOURS;
    }
//


//    *******************************************************************

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.totalWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        this.totalHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(totalWidth,totalHeight);
        updateWidthHeight(totalWidth,totalHeight);
    }


    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        updateWidthHeight(totalWidth, totalHeight);
        scrollView.layout(0, 0, totalWidth, totalHeight);
        backGroundRelativeLayout.layout(0, 0, totalWidth, hourHeight * getHours().length);
        weekBodyLinearLayout.layout(0, 0, totalWidth, hourHeight * getHours().length);
        eventAndWidgetsRelativeLayout.layout(hourWidth, 0, hourWidth + oneWeekWidth, hourHeight * getHours().length);
        timeRelativeLayout.layout(0, 0, hourWidth, hourHeight * getHours().length);
        eventWidgetsRelativeLayout.layout(0, 0, hourWidth + oneWeekWidth, hourHeight * getHours().length);
        eventRelativeLayout.layout(0, 0, hourWidth + oneWeekWidth, hourHeight * getHours().length);
        timeSlotRelativeLayout.layout(0, 0, hourWidth + oneWeekWidth, hourHeight * getHours().length);
        // set 00:00, 01:00 ...
        for (int hour = 0; hour < getHours().length; hour++) {
            int hourLeft = 0;
            int hourTop = hourHeight * hour ;
            int hourRight = hourWidth;
            int hourBottom = (int) (hourHeight * hour +  hourHeight );
            hourTextViewArr[hour].layout( hourLeft, hourTop, hourRight, hourBottom);
        }
        // set dotted line
        for (int hour = 0; hour < getHours().length; hour++) {
            timeLineTextViewArr[hour].layout(0, dayHeight * hour, oneWeekWidth, (int) (dayHeight * hour + dottedLineHeight));
        }
        Calendar todayCalendar = Calendar.getInstance(Locale.getDefault());
        todayCalendar.setTime(new Date());
        if (isShowingToday(myCalendar, todayCalendar)) {
            int hour = todayCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = todayCalendar.get(Calendar.MINUTE);
            int currentTimePaddingTop = (int) (hour * hourHeight + (int) (minute * hourHeight / 60) + dottedLineHeight/2);
            currentTimeLineTextView.layout(hourWidth + 20, currentTimePaddingTop, totalWidth, currentTimePaddingTop + 2);
            currentTimeTextView.layout(0, currentTimePaddingTop, hourWidth,currentTimePaddingTop+ hourHeight);
        }

//         init timeslots
        if (timeSlots!=null){
            for (final TimeSlotView timeSlotView:timeSlotViewArrayList){
                int index = timeSlotViewArrayList.indexOf(timeSlotView);
                long startTime = timeSlotView.getStartTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(startTime);
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int topOffSet = hourHeight * hour + hourHeight*minute/60 + dottedLineHeight/2;
                int leftOffSet = dayWidth * (day-1);
                int timeSlotHeight = timeSlotView.getDuration() * hourHeight/60;
                timeSlotView.layout(leftOffSet, topOffSet, leftOffSet + dayWidth,topOffSet + timeSlotHeight); // set time slot layout
                TextView timeSlotText = timeSlotTimeTextViewArrayList.get(index);
                timeSlotText.layout(0, topOffSet,hourWidth, topOffSet + hourHeight); // set time text
                ImageView timeLineImage = timeSlotTimeLineArrayList.get(index);
                timeLineImage.layout( hourWidth + 20, topOffSet, hourWidth + oneWeekWidth, topOffSet + 3); // set time line
                timeSlotView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if ( ((TimeSlotView)view).isSelect())
                            ((TimeSlotView)view).setSelect(false);
                        else
                            ((TimeSlotView)view).setSelect(true);
                        onTimeSlotClickListener.onTimeSlotClick(timeSlotView.getStartTime());
                    }
                });

            }
        }
         // init events
        if (eventViewArrayList!=null){
            for (WeekDraggableEventView eventView:eventViewArrayList){
                long startTime = eventView.getEvent().getStartTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(startTime);
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int topOffset = hourHeight * hour + hourHeight * minute/60 + dottedLineHeight/2;
                int leftOffSet = dayWidth * (day-1);
                int duration = (int) ((eventView.getEvent().getEndTime() - eventView.getEvent().getStartTime())/1000/60);
                int eventHeight = duration *hourHeight / 60;
                eventView.layout(leftOffSet, topOffset, leftOffSet + dayWidth, topOffset + eventHeight);
            }
        }

    }

    public void updateTimeSlot(){
        if (timeSlots!=null){
            for (final TimeSlotView timeSlotView:timeSlotViewArrayList){
                int index = timeSlotViewArrayList.indexOf(timeSlotView);
                long startTime = timeSlotView.getStartTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(startTime);
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int topOffSet = hourHeight * hour + hourHeight*minute/60 + dottedLineHeight/2;
                int leftOffSet = dayWidth * (day-1);
                int timeSlotHeight = timeSlotView.getDuration() * hourHeight/60;

                timeSlotView.layout(leftOffSet, topOffSet, leftOffSet + dayWidth,topOffSet + timeSlotHeight); // set time slot layout
                TextView timeSlotText = timeSlotTimeTextViewArrayList.get(index);
                timeSlotText.layout(0, topOffSet,hourWidth, topOffSet + hourHeight); // set time text
                ImageView timeLineImage = timeSlotTimeLineArrayList.get(index);
                timeLineImage.layout( hourWidth + 20, topOffSet, hourWidth + oneWeekWidth, topOffSet + 3); // set time line
                timeSlotView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if ( ((TimeSlotView)view).isSelect())
                                ((TimeSlotView)view).setSelect(false);
                            else
                                ((TimeSlotView)view).setSelect(true);
                        onTimeSlotClickListener.onTimeSlotClick(timeSlotView.getStartTime());
                    }
                });
            }
        }
    }


    public void updateWidthHeight(int totalWidth, int totalHeight) {
        this.hourHeight = totalHeight / numOfHourShowInScreen;
        this.hourWidth = totalWidth / 10;
        this.dayHeight = hourHeight;
        this.dayWidth = (int) (totalWidth * 0.9) / 7;
        this.oneWeekWidth = (int) (totalWidth * 0.9);
        this.dottedLineHeight = hourHeight/3;
    }

    public MyCalendar getMyCalendar() {
        return myCalendar;
    }

    public void setMyCalendar(MyCalendar myCalendar) {
        this.myCalendar = myCalendar;
        if (backGroundRelativeLayout.getChildCount()>1){
            backGroundRelativeLayout.removeAllViews();
            backGroundRelativeLayout.addView(weekBodyLinearLayout);
        }
        initCurrentTimeLine();
    }

    public WeekTimeSlotView.OnTimeSlotClickListener getOnTimeSlotClickListener() {
        return onTimeSlotClickListener;
    }

    public void setOnTimeSlotClickListener(WeekTimeSlotView.OnTimeSlotClickListener onTimeSlotClickListener) {
        this.onTimeSlotClickListener = onTimeSlotClickListener;
    }
//    ********************************************************************

    private final class MyDragListener implements View.OnDragListener {
        float actionStartX = 0;
        float actionStartY = 0;

        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    View currentEventView = (View) dragEvent.getLocalState();
                    actionStartX = dragEvent.getX();
                    actionStartY = dragEvent.getY();
                    msgWindow.setVisibility(View.VISIBLE);
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    scrollViewAutoScroll(dragEvent);
                    msgWindowFollow((int) dragEvent.getX(), (int) dragEvent.getY(), (View) dragEvent.getLocalState());
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

                    int newX = (int) actionStopX - currentDragView.getWidth() / 2;
                    int newY = (int) actionStopY - currentDragView.getHeight() / 2;
                    int[] reComputeResult = reComputePositionToSet(newX, newY, currentDragView, view);

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

    private int[] reComputePositionToSet(int actualX, int actualY, View draggableObj, View container) {
        int containerWidth = container.getWidth();
        int containerHeight = container.getHeight();

        int objWidth = draggableObj.getWidth();
        int objHeight = draggableObj.getHeight();

        int finalX = actualX;
        int finalY = actualY;

        if (actualX < 0) {
            finalX = 0;
        } else if (actualX + objWidth > containerWidth) {
            finalX = containerWidth - objWidth;
        }

        if (actualY < 0) {
            finalY = 0;
        } else if (actualY + objHeight > containerHeight) {
            finalY = containerHeight - objHeight;
        }
        int findNearestPositionY = nearestTimeSlotKey(finalY);
        int findNearestPositionX = nearestDaySlotKey(finalX);
        if (findNearestPositionY != -1) {
            finalY = findNearestPositionY;
        } else {
            Log.d("TAG", "reComputePositionToSet: " + "Error no such position");

        }

        if (findNearestPositionX != -1) {
            finalX = findNearestPositionX;
        } else {
            Log.d("TAG", "reComputePositionToSet: " + "Error no such position");
        }
        return new int[]{finalX, finalY};
    }

    private int nearestTimeSlotKey(int tapY) {
        int key = tapY;
        Map.Entry<Integer, String> low = timeSlotTreeMap.floorEntry(key);
        Map.Entry<Integer, String> high = timeSlotTreeMap.ceilingEntry(key);
        if (low != null && high != null) {
            return Math.abs(key - low.getKey()) < Math.abs(key - high.getKey())
                    ? low.getKey()
                    : high.getKey();
        } else if (low != null || high != null) {
            return low != null ? low.getKey() : high.getKey();
        }
        return -1;
    }

    private int nearestDaySlotKey(int tapX) {
        int key = tapX;
        Map.Entry<Integer, String> low = daySlotTreeMap.floorEntry(key);
        Map.Entry<Integer, String> high = daySlotTreeMap.ceilingEntry(key);
        if (low != null && high != null) {
            return Math.abs(key - low.getKey()) < Math.abs(key - high.getKey())
                    ? low.getKey()
                    : high.getKey();
        } else if (low != null || high != null) {
            return low != null ? low.getKey() : high.getKey();
        }
        return -1;
    }

    public void scrollViewAutoScroll(DragEvent event) {
        Rect scrollBounds = new Rect();
        scrollView.getDrawingRect(scrollBounds);
        float heightOfView = ((View) event.getLocalState()).getHeight();
        float needPositionY_top = event.getY() - heightOfView / 2;
        float needPositionY_bottom = event.getY() + heightOfView / 2;

        if (scrollBounds.top > needPositionY_top) {
            int offsetY = (int) (scrollView.getScrollY() + needPositionY_top - scrollBounds.top);
            scrollView.scrollTo(scrollView.getScrollX(), offsetY);
        } else if (scrollBounds.bottom < needPositionY_bottom) {
            int offsetY = (int) (scrollView.getScrollY() + needPositionY_bottom - scrollBounds.bottom);
            scrollView.scrollTo(scrollView.getScrollX(), offsetY);
        }
    }

    private void msgWindowFollow(int tapX, int tapY, View followView) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) msgWindow.getLayoutParams();
        params.topMargin = tapY - followView.getHeight() / 2 - msgWindow.getHeight();

        if (tapX + msgWindow.getWidth() / 2 > eventRelativeLayout.getWidth()) {
            params.leftMargin = eventRelativeLayout.getWidth() - msgWindow.getWidth();
        } else if (tapX - msgWindow.getWidth() / 2 < 0) {
            params.leftMargin = 0;
        } else {
            params.leftMargin = tapX - msgWindow.getWidth() / 2;
        }
        int nearestProperPosition = nearestTimeSlotKey(tapY - followView.getHeight() / 2);
        int nearestProperDay = nearestDaySlotKey(tapX - followView.getWidth() / 2);
        if (nearestProperPosition != -1 && nearestProperDay != -1) {
//            Log.d("day",daySlotTreeMap.get(nearestProperDay));
//            Log.d("time",timeSlotTreeMap.get(nearestProperPosition));
            msgWindow.setVisibility(View.VISIBLE);
            msgWindow.setText(daySlotTreeMap.get(nearestProperDay) + " " + timeSlotTreeMap.get(nearestProperPosition));

        } else {
            Log.d("TAG", "msgWindowFollow: " + "Error, text not found in Map");
        }
        msgWindow.setLayoutParams(params);
        //msgWindow.setVisibility(View.VISIBLE);
    }

//    public void setTimeSlot(ArrayList<Long> timeSlots){
//        this.timeSlots = timeSlots;
//    }
}
