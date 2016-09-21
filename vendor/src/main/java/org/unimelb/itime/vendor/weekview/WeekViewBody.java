package org.unimelb.itime.vendor.weekview;

import android.content.ClipData;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.eventview.DayDraggableEventView;
import org.unimelb.itime.vendor.eventview.WeekDraggableEventView;
import org.unimelb.itime.vendor.helper.DensityUtil;
import org.unimelb.itime.vendor.helper.MyCalendar;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by yinchuandong on 22/08/2016.
 */

public class WeekViewBody extends RelativeLayout{
    private static final String TAG = "MyAPP";
    private int totalHeight = 0;
    private int totalWidth = 0;
    private int hourHeight = 0;
    private int hourWidth = 0;
    private int dayHeight = 0;
    private int dayWidth = 0;
    private int oneWeekWidth = 0;
    private int numOfHourShowInScreen = 8;
    private int dottedLineHeight = 0;
    private int lineHeight = 50;
    private int timeTextSize = 20;

    private TreeMap<Integer,String> timeSlotTreeMap = new TreeMap<>();
    private TreeMap<Integer, String> daySlotTreeMap = new TreeMap<>();

    private TreeMap<Integer, String> positionToTimeTreeMap = new TreeMap<>();
    private TreeMap<Float, Integer> timeToPositionTreeMap = new TreeMap<>();

    private ScrollView scrollView;
    private RelativeLayout backgroundRelativeLayout;
    private LinearLayout topAllDayLayout;
    private LinearLayout topAllDayEventLayout;
    private RelativeLayout leftTimeRelativeLayout;
    private RelativeLayout rightBgRelativeLayout;
    private RelativeLayout rightContainerRelativeLayout;
    private RelativeLayout eventRelativeLayout;

    private TextView msgWindow;

    private MyDragListener myDragListener;
    private MyCalendar myCalendar;
    private List<ITimeEventInterface> eventList = new ArrayList<>();
    private ArrayList<WeekDraggableEventView> eventViewArrayList = new ArrayList<>();

    private TextView currentTimeLineTextView;
    private TextView currentTimeTextView;

    private OnWeekBodyListener onWeekBodyListener;

    private Context context;

    private float tapX;
    private float tapY;

    public WeekViewBody(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public WeekViewBody(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        loadAttributes(attrs,context);
        init();
    }

    private void loadAttributes(AttributeSet attrs, Context context) {
        if (attrs != null && context != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.dayStyle, 0, 0);
            try {
                lineHeight = typedArray.getDimensionPixelSize(R.styleable.dayStyle_lineHeight,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, lineHeight, context.getResources().getDisplayMetrics()));
                timeTextSize = typedArray.getDimensionPixelSize(R.styleable.dayStyle_timeTextSize,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, timeTextSize, context.getResources().getDisplayMetrics()));

            } finally {
                typedArray.recycle();
            }
        }
    }

    public void init(){
        initWidgets();

        initTimeSlot();

        initHourTextViews();
        initTimeDottedLine();
        initMsgWindow();
        initDragListener();
        initCurrentTimeLine();
    }

    private void initTimeSlot() {
        double startPoint = timeTextSize * 0.5;
        double timeSlotHeight = lineHeight / 4;
        String[] hours = getHours();
        for (int slot = 0; slot < hours.length; slot++) {
            //add full clock
            positionToTimeTreeMap.put((int) startPoint + lineHeight * slot, hours[slot] + ":00");
            String hourPart = hours[slot].substring(0, 2); // XX
            timeToPositionTreeMap.put((float) Integer.valueOf(hourPart), (int) startPoint + lineHeight * slot);
            for (int miniSlot = 0; miniSlot < 3; miniSlot++) {
                String minutes = String.valueOf((miniSlot + 1) * 15);
                String time = hourPart + ":" + minutes;
                int positionY = (int) (startPoint + lineHeight * slot + timeSlotHeight * (miniSlot + 1));
                positionToTimeTreeMap.put(positionY, time);
                timeToPositionTreeMap.put(Integer.valueOf(hourPart) + (float) Integer.valueOf(minutes) / 100, positionY);
            }
        }
    }

    private void initWidgets() {
        //body container
        RelativeLayout.LayoutParams thisRelativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.setLayoutParams(thisRelativeLayoutParams);

        //scrollview
        scrollView = new ScrollView(getContext());
        RelativeLayout.LayoutParams scrollViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        scrollView.setVerticalScrollBarEnabled(false);
        this.addView(scrollView,scrollViewParams);

        // init background relativeLayout
        backgroundRelativeLayout = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams backgroundRelativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        scrollView.addView(backgroundRelativeLayout,backgroundRelativeLayoutParams);

        //top container
        topAllDayLayout = new LinearLayout(getContext());
        topAllDayLayout.setId(View.generateViewId());
        RelativeLayout.LayoutParams topAllDayLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        backgroundRelativeLayout.addView(topAllDayLayout,topAllDayLayoutParams);

        TextView allDayTitleTv = new TextView(context);
        LinearLayout.LayoutParams allDayTitleTvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        int allDayTitleTvPadding = DensityUtil.dip2px(context, 3);
        allDayTitleTv.setPadding(allDayTitleTvPadding, allDayTitleTvPadding, allDayTitleTvPadding, allDayTitleTvPadding);
        allDayTitleTv.setTextSize(10);
        allDayTitleTv.setText("All Day");
        allDayTitleTv.setTextColor(context.getResources().getColor(R.color.text_enable));
        allDayTitleTv.setGravity(Gravity.CENTER_VERTICAL);
        topAllDayLayout.addView(allDayTitleTv,allDayTitleTvParams);

        //top event container
        topAllDayEventLayout = new LinearLayout(getContext());
        topAllDayEventLayout.setId(View.generateViewId());
        RelativeLayout.LayoutParams topAllDayEventLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(context, 40));
        topAllDayLayout.addView(topAllDayEventLayout, topAllDayEventLayoutParams);

        // init left time
        leftTimeRelativeLayout = new RelativeLayout(getContext());
        leftTimeRelativeLayout.setId(View.generateViewId());
        leftTimeRelativeLayout.setPadding(DensityUtil.dip2px(context, 10), 0, DensityUtil.dip2px(context, 10), 0);
        RelativeLayout.LayoutParams leftTimeRelativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        leftTimeRelativeLayoutParams.addRule(RelativeLayout.BELOW, topAllDayLayout.getId());
        backgroundRelativeLayout.addView(leftTimeRelativeLayout,leftTimeRelativeLayoutParams);

        //right bg and event container
        rightContainerRelativeLayout = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams rightContainerRelativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rightContainerRelativeLayoutParams.addRule(BELOW,topAllDayLayout.getId());
        rightContainerRelativeLayoutParams.addRule(RIGHT_OF,leftTimeRelativeLayout.getId());
        rightContainerRelativeLayoutParams.addRule(ALIGN_TOP,leftTimeRelativeLayout.getId());
        rightContainerRelativeLayoutParams.addRule(ALIGN_BOTTOM,leftTimeRelativeLayout.getId());
        backgroundRelativeLayout.addView(rightContainerRelativeLayout,rightContainerRelativeLayoutParams);

        //right bg
        rightBgRelativeLayout = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams rightBgRelativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rightBgRelativeLayout.setPadding(20,0,0,0);
        rightContainerRelativeLayout.addView(rightBgRelativeLayout,rightBgRelativeLayoutParams);

        //right event
        eventRelativeLayout = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams eventRelativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        eventRelativeLayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tapX = event.getX();
                tapY = event.getY();
                return false;
            }
        });
        eventRelativeLayout.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //create event here;

                return false;
            }
        });
        rightContainerRelativeLayout.addView(eventRelativeLayout,eventRelativeLayoutParams);
    }

    public void initHourTextViews() {
        for (int i = 0; i < getHours().length; i++) {
            TextView tv = new TextView(getContext());
            RelativeLayout.LayoutParams hourParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            hourParams.topMargin = lineHeight * (i);
            tv.setGravity(Gravity.CENTER);
            tv.setText(getHours()[i].substring(0,2));
            tv.setTextSize(12);
            leftTimeRelativeLayout.addView(tv,hourParams);
        }
    }

    public void initTimeDottedLine() {
        for (int i = 0; i < getHours().length; i++) {
            TextView tv = new TextView(getContext());
            RelativeLayout.LayoutParams dottedParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dottedParams.topMargin = lineHeight * (i);
            tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.itime_dotted_line));
            tv.setGravity(Gravity.CENTER);
            rightBgRelativeLayout.addView(tv,dottedParams);
        }
    }

    private void initCurrentTimeLine() {
        currentTimeLineTextView = new TextView(getContext());
        RelativeLayout.LayoutParams currentTimeLineParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        currentTimeLineTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.current_time_red_line));
        backgroundRelativeLayout.addView(currentTimeLineTextView,currentTimeLineParams);

        currentTimeTextView = new TextView(getContext());
        RelativeLayout.LayoutParams currentTimeTextParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        currentTimeTextView.setTextSize(8);
        currentTimeTextView.setTextColor(Color.RED);
        backgroundRelativeLayout.addView(currentTimeTextView,currentTimeTextParams);
    }

    private void updateCurrentTimeLine(){
        Calendar todayCalendar = Calendar.getInstance(Locale.getDefault());
        todayCalendar.setTime(new Date());
        int currentHour = todayCalendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = todayCalendar.get(Calendar.MINUTE);
        String stringCurrentHour = currentHour < 10 ? "0" + String.valueOf(currentHour) : String.valueOf(currentHour);
        String stringCurrentMinute = currentMinute < 10 ? "0" + String.valueOf(currentMinute) : String.valueOf(currentMinute);
        String AMPM = currentHour > 12 ? "PM" : "AM";
        currentTimeTextView.setText(String.format("%s:%s %s", stringCurrentHour, stringCurrentMinute, AMPM));
    }

    private void initMsgWindow() {
        String text = "Sun 00:00";
        msgWindow = new TextView(getContext());
        msgWindow.setTextSize(20);
        msgWindow.setText(text);
        msgWindow.setVisibility(View.INVISIBLE);
        msgWindow.measure(0, 0);       //must call measure!
        int height = msgWindow.getMeasuredHeight(); //get height
        int width = msgWindow.getMeasuredWidth();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width + 20, height);

        rightContainerRelativeLayout.addView(msgWindow,params);
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
        String[] days = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        for (int slot = 0; slot <7; slot++ ){
            int positionX = (int)startPoint + (int)daySlotWidth * slot;
            daySlotTreeMap.put( positionX ,days[slot]);
        }
    }

    private boolean isShowingToday(MyCalendar myCalendar) {
        Calendar todayCalendar = Calendar.getInstance(Locale.getDefault());
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
        if (eventList!=null){
            //first remove contains
            eventRelativeLayout.removeAllViews();
            for (ITimeEventInterface event: eventList){
                Date eventDate = new Date(event.getStartTime());
                Calendar eventCalendar = Calendar.getInstance();
                eventCalendar.setTime(eventDate);
                if (isInCurrentWeek(eventCalendar,myCalendar)) {
                    WeekDraggableEventView eventView = new WeekDraggableEventView(getContext(),event);
                    eventView.setOnLongClickListener(new MyLongClickListener()); // for draggable
                    eventView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onWeekBodyListener != null){
                                onWeekBodyListener.onEventClick((DayDraggableEventView) v);
                            }
                        }
                    });

                    eventViewArrayList.add(eventView);
                    eventRelativeLayout.addView(eventView);
                }
            }
        }
    }

    public void setEvents(List<ITimeEventInterface> eventList){
        this.eventList = eventList;
        initEvents();
        updateEvents();
        requestLayout();
    }

    public void updateEvents(){
        if (eventViewArrayList!=null){
            for (final WeekDraggableEventView eventView:eventViewArrayList){
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

    private ArrayList<DayDraggableEventView> regularDayDgEventViews = new ArrayList<>();
    private ArrayList<ITimeEventInterface> allDayEventModules = new ArrayList<>();
    private ArrayList<ITimeEventInterface> regularEventModules = new ArrayList<>();
    private ArrayList<DayDraggableEventView> allDayDgEventViews = new ArrayList<>();
    private final long allDayMilliseconds = 24 * 60 * 60 * 1000;
    private Map<ITimeEventInterface, Integer> regularEventViewMap = new HashMap<>();

    private void addEvent(ITimeEventInterface event) {
        boolean isAllDayEvent = isAllDayEvent(event);

        if (isAllDayEvent) {
            allDayEventModules.add(event);
            addAllDayEvent(event);
        } else {
            regularEventModules.add(event);
//            addRegularEvent(event);
        }

        this.msgWindow.bringToFront();
    }

    private void addAllDayEvent(ITimeEventInterface event) {
        int eventsContainerWidth =
                topAllDayLayout.getWidth()
                        - topAllDayLayout.findViewById(R.id.allDayContainerTitle).getWidth()
                        - topAllDayLayout.getPaddingLeft() - topAllDayLayout.getPaddingRight();
        int marginLeft = DensityUtil.dip2px(context, 1);
        DayDraggableEventView new_dgEvent = this.createDayDraggableEventView(event, true);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) new_dgEvent.getLayoutParams();
        params.leftMargin = marginLeft;
        allDayDgEventViews.add(new_dgEvent);
        topAllDayEventLayout.addView(new_dgEvent, params);
        //resize all day events width
        resizeAllDayEvents(eventsContainerWidth, marginLeft);
    }

//    private void addRegularEvent(ITimeEventInterface event) {
//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//        String hourWithMinutes = sdf.format(new Date(event.getStartTime()));
//        String[] components = hourWithMinutes.split(":");
//        float trickTime = Integer.valueOf(components[0]) + (float) Integer.valueOf(components[1]) / 100;
//        int topMargin = nearestTimeSlotValue(trickTime);
//        DayDraggableEventView newDragEventView = this.createDayDraggableEventView(event, false);
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) newDragEventView.getLayoutParams();
//        params.topMargin = topMargin;
//
//        newDragEventView.setId(View.generateViewId());
//        this.regularEventViewMap.put(event, newDragEventView.getId());
//        this.eventRelativeLayout.addView(newDragEventView, params);
//        this.regularDayDgEventViews.add(newDragEventView);
//    }

    private void resizeAllDayEvents(int totalWidth, int marginLeft) {
        int singleEventWidth = (totalWidth - this.allDayDgEventViews.size() * marginLeft) / this.allDayDgEventViews.size();
        for (DayDraggableEventView dgEvent : this.allDayDgEventViews
                ) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dgEvent.getLayoutParams();
            params.width = singleEventWidth;
            params.leftMargin = marginLeft;
        }

        topAllDayLayout.invalidate();
    }

    private boolean isAllDayEvent(ITimeEventInterface event) {
        long duration = event.getEndTime() - event.getStartTime();
        boolean isAllDay = duration >= allDayMilliseconds;

        return isAllDay;
    }

    private DayDraggableEventView createDayDraggableEventView(ITimeEventInterface event, boolean isAllDayEvent) {

        DayDraggableEventView event_view = new DayDraggableEventView(context, event, isAllDayEvent);
        event_view.setType(DayDraggableEventView.TYPE_NORMAL);
        event_view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onWeekBodyListener != null) {
                    onWeekBodyListener.onEventClick((DayDraggableEventView) view);
                }
            }
        });
        if (isAllDayEvent) {
            int allDayHeight = topAllDayLayout.getWidth() - topAllDayLayout.getPaddingBottom() - topAllDayLayout.getPaddingTop();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0, allDayHeight);
            event_view.setTag(event);
            event_view.setLayoutParams(params);
        } else {
            long duration = event.getEndTime() - event.getStartTime();
            int eventHeight = (int) (((float) duration / (3600 * 1000)) * lineHeight);
//            int getStartY = getEventY(event);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0, eventHeight);
//            event_view.setTop(getStartY);
//            event_view.setOnLongClickListener(new EventLongClickListener());
            event_view.setTag(event);
            event_view.setLayoutParams(params);
        }

        return event_view;
    }

    public DayDraggableEventView createTempDayDraggableEventView(float tapX, float tapY) {
        ITimeEventInterface event = this.initializeEvent();
        if (event == null) {
            throw new RuntimeException("need Class name in 'setEventClassName()'");
        }
        DayDraggableEventView event_view = new DayDraggableEventView(context, event, false);
        event_view.setType(DayDraggableEventView.TYPE_TEMP);

        int eventHeight = lineHeight;//one hour
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, eventHeight);
        params.topMargin = (int) (tapY - eventHeight / 2);
        event_view.setOnLongClickListener(new MyLongClickListener());
        event_view.setLayoutParams(params);

        return event_view;
    }

    public boolean isInCurrentWeek(Calendar timeSlotCalendar,MyCalendar myCalendar){
        Calendar firstSundayCalendar = Calendar.getInstance();
        firstSundayCalendar.set(Calendar.YEAR, myCalendar.getYear());
        firstSundayCalendar.set(Calendar.MONTH, myCalendar.getMonth());
        firstSundayCalendar.set(Calendar.DAY_OF_MONTH, myCalendar.getDay());
        return (timeSlotCalendar.get(Calendar.WEEK_OF_YEAR) == firstSundayCalendar.get(Calendar.WEEK_OF_YEAR)
                && timeSlotCalendar.get(Calendar.YEAR) == firstSundayCalendar.get(Calendar.YEAR));
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
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        this.totalWidth = MeasureSpec.getSize(widthMeasureSpec);
//        this.totalHeight = MeasureSpec.getSize(heightMeasureSpec);
//        setMeasuredDimension(totalWidth,totalHeight);
//        updateWidthHeight(totalWidth,totalHeight);
//        for (int hour = 0;hour < getHours().length ; hour++){
//            // set hour view
//            RelativeLayout hourParams = (RelativeLayout) hourTextViewArr[hour].getLayoutParams();
//            hourParams.height = hourHeight;
//            hourParams.width = hourWidth;
//            hourParams.top = hourHeight * hour;
//            hourParams.left = 0;
//            hourTextViewArr[hour].setPadding(0,55,0,0); // gravity center has problem
//            hourTextViewArr[hour].setLayoutParams(hourParams);
//
//            // set dotted line
//            RelativeLayout dottedParams = (RelativeLayout) timeLineTextViewArr[hour].getLayoutParams();
//            dottedParams.height = dayHeight;
//            dottedParams.width = oneWeekWidth;
//            dottedParams.top = dayHeight * hour;
//            dottedParams.left = 0;
//            timeLineTextViewArr[hour].setGravity(Gravity.CENTER);
//            timeLineTextViewArr[hour].setLayoutParams(dottedParams);
//        }
//
//        Calendar todayCalendar = Calendar.getInstance(Locale.getDefault());
//        todayCalendar.setTime(new Date());
//        if (isShowingToday(myCalendar)) {
//            int hour = todayCalendar.get(Calendar.HOUR_OF_DAY);
//            int minute = todayCalendar.get(Calendar.MINUTE);
//
//            RelativeLayout currentTimeLineParams = (RelativeLayout) currentTimeLineTextView.getLayoutParams();
//            currentTimeLineParams.height = dayHeight;
//            currentTimeLineParams.width = oneWeekWidth;
//            currentTimeLineParams.top = hour * hourHeight + minute * hourHeight / 60;
//            currentTimeLineParams.left = hourWidth;
//            currentTimeLineTextView.setGravity(Gravity.CENTER);
////            currentTimeLineTextView.setLayoutParams(currentTimeLineParams);
//
//            RelativeLayout currentTimeTextParams = (RelativeLayout) currentTimeTextView.getLayoutParams();
//            currentTimeTextParams.height = hourHeight;
//            currentTimeTextParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
//            currentTimeTextParams.left =0;
//            currentTimeTextParams.top = hour * hourHeight + minute * hourHeight / 60;
//            currentTimeTextView.setPadding(0,55,0,0);
//            currentTimeTextView.setLayoutParams(currentTimeTextParams);
//        }
//    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        // init events
//        if (eventViewArrayList!=null){
//            for (final WeekDraggableEventView eventView:eventViewArrayList){
//                long startTime = eventView.getEvent().getStartTime();
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(startTime);
//                int day = calendar.get(Calendar.DAY_OF_WEEK);
//                int hour = calendar.get(Calendar.HOUR_OF_DAY);
//                int minute = calendar.get(Calendar.MINUTE);
//                int topOffset = hourHeight * hour + hourHeight * minute/60 + hourHeight/2;
//                int leftOffSet = dayWidth * (day-1);
//                int duration = (int) ((eventView.getEvent().getEndTime() - eventView.getEvent().getStartTime())/1000/60);
//                int eventHeight = duration *hourHeight / 60;
//                eventView.layout(leftOffSet, topOffset, leftOffSet + dayWidth, topOffset + eventHeight);
//
//            }
//        }
//        // for drag events
        initTimeSlot(getHours());
        initDaySlot();
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
        this.resetView();

        if (isShowingToday(myCalendar)) {
            currentTimeLineTextView.setVisibility(VISIBLE);
            currentTimeTextView.setVisibility(VISIBLE);
            this.updateCurrentTimeLine();
        }else {
            currentTimeLineTextView.setVisibility(GONE);
            currentTimeTextView.setVisibility(GONE);
        }
    }

    private void resetView(){
        this.topAllDayEventLayout.removeAllViews();
        this.eventRelativeLayout.removeAllViews();
    }

//    ********************************************************************

    private final class MyLongClickListener implements View.OnLongClickListener{
        @Override
        public boolean onLongClick(View view) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                    view);
            view.startDrag(data, shadowBuilder, view, 0);
            view.setVisibility(View.VISIBLE);
            view.getBackground().setAlpha(255);
            return false;
        }
    }


    private final class MyDragListener implements View.OnDragListener{

        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            DayDraggableEventView currentEventView = (DayDraggableEventView) dragEvent.getLocalState();
            switch (dragEvent.getAction()){
                case DragEvent.ACTION_DRAG_STARTED:
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    scrollViewAutoScroll(dragEvent);
                    msgWindowFollow((int)dragEvent.getX(),(int)dragEvent.getY(),currentEventView);
                    if (onWeekBodyListener != null){
                        onWeekBodyListener.onEventDragging(currentEventView, (int) dragEvent.getX(), (int) dragEvent.getY());
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    msgWindowFollow((int)dragEvent.getX(),(int)dragEvent.getY(),currentEventView);
                    msgWindow.setVisibility(View.VISIBLE);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:
                    if (onWeekBodyListener != null){
                        onWeekBodyListener.onEventDragDrop(currentEventView);
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    currentEventView.getBackground().setAlpha(150);
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

    private void msgWindowFollow(int tapX, int tapY, View followView) {
        float toX;
        float toY;

        toY = tapY - followView.getHeight() / 2 - msgWindow.getHeight();
        if (tapX + msgWindow.getWidth() / 2 > eventRelativeLayout.getWidth()) {
            toX = eventRelativeLayout.getWidth() - msgWindow.getWidth();
        } else if (tapX - msgWindow.getWidth() / 2 < 0) {
            toX = 0;
        } else {
            toX = tapX - msgWindow.getWidth() / 2;
        }
        int nearestProperPosition = nearestTimeSlotKey(tapY - followView.getHeight() / 2);
        int nearestProperDay = nearestDaySlotKey(tapX - followView.getWidth()/2);
        if (nearestProperPosition != -1  && nearestProperDay != -1) {
            String oldText = msgWindow.getText().toString();
            String newText = daySlotTreeMap.get(nearestProperDay) + " " + timeSlotTreeMap.get(nearestProperPosition);
            if (!newText.equals(oldText)){
                msgWindow.setText(newText);
            }
        } else {
            Log.i(TAG, "msgWindowFollow: " + "Error, text not found in Map");
        }

        msgWindow.setTranslationX(toX);
        msgWindow.setTranslationY(toY);
    }

    public interface OnWeekBodyListener{
        void onEventCreate(DayDraggableEventView eventView);
        void onEventClick(DayDraggableEventView eventView);
        void onEventDragStart(DayDraggableEventView eventView);
        void onEventDragging(DayDraggableEventView eventView, int x, int y);
        void onEventDragDrop(DayDraggableEventView eventView);
    }

    public void setOnWeekBodyListener(OnWeekBodyListener onWeekBodyListener){
        this.onWeekBodyListener = onWeekBodyListener;
    }

    Class<?> eventClassName;

    public <E extends ITimeEventInterface> void setEventClassName(Class<E> className) {
        eventClassName = className;
    }

    /**
     * @return
     */
    private ITimeEventInterface initializeEvent() {

        try {
            ITimeEventInterface t = (ITimeEventInterface) eventClassName.newInstance();
            return t;
        } catch (Exception e) {
            return null;
        }
    }
}
