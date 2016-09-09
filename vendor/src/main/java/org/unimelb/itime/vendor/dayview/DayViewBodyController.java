package org.unimelb.itime.vendor.dayview;

import android.content.ClipData;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.eventview.DayDraggableEventView;
import org.unimelb.itime.vendor.helper.CalendarEventOverlapHelper;
import org.unimelb.itime.vendor.helper.DensityUtil;
import org.unimelb.itime.vendor.helper.MyCalendar;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;

import java.text.DateFormat;
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
 * Created by yuhaoliu on 3/08/16.
 */
public class DayViewBodyController {
    public final String TAG = "MyAPP";

    private final long allDayMilliseconds = 24 * 60 * 60 * 1000;

    private RelativeLayout parent;
    private RelativeLayout timeRLayout;
    private RelativeLayout dividerRLayout;
    private LinearLayout allDayContainer;

    public ScrollContainerView scrollContainerView;
    public MyCalendar myCalendar;
    private Context context;

    private ArrayList<ITimeEventInterface> regularEventModules = new ArrayList<>();
    private ArrayList<DayDraggableEventView> regularDayDgEventViews = new ArrayList<>();
    private ArrayList<ITimeEventInterface> allDayEventModules = new ArrayList<>();
    private ArrayList<DayDraggableEventView> allDayDgEventViews = new ArrayList<>();

    private TreeMap< Integer, String> positionToTimeTreeMap = new TreeMap<>();
    private TreeMap<Float, Integer> timeToPositionTreeMap = new TreeMap<>();
    private Map<ITimeEventInterface, Integer> regular_event_view_map = new HashMap<>();

    private CalendarEventOverlapHelper xHelper = new CalendarEventOverlapHelper();

    private TextView msgWindow;
    private TextView nowTime;
    private ImageView nowTimeLine;
    //tag: false-> moving, true, done
    private DayDraggableEventView tempDragView = null;

    private int lineHeight = 50;
    private int timeTextSize = 20;
    private int overlapGapHeight;

    private float nowTapX = 0;
    private float nowTapY = 0;

    private OnCreateNewEvent onCreateNewEvent;
    private OnLoadEvents onLoadEvents;

    private BodyOnTouchListener bodyOnTouchListener;

    public DayViewBodyController(AttributeSet attrs,
                                 Context context) {
        this.context = context;
        this.overlapGapHeight = DensityUtil.dip2px(context, 1);
        loadAttributes(attrs, context);
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

    public void onFinishInflate(
            ScrollContainerView scrollContainerView,
            RelativeLayout timelineRL,
            RelativeLayout dividerRL,
            RelativeLayout parent,
            LinearLayout allDayContainer){
        this.parent = parent;
        this.timeRLayout = timelineRL;
        this.dividerRLayout = dividerRL;
        this.allDayContainer = allDayContainer;

        this.scrollContainerView = scrollContainerView;
        dividerRLayout.setOnDragListener(new EventDragListener());
        dividerRLayout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                nowTapX = event.getX();
                nowTapY = event.getY();
                if (bodyOnTouchListener != null){
                    bodyOnTouchListener.bodyOnTouchListener(nowTapX, nowTapY);
                }else {
                    Log.i(TAG, "controller:  bodyOnTouchListener null ");
                }

                return false;
            }
        });
        dividerRLayout.setOnLongClickListener(new CreateEventListener());
    }

    public void initBackgroundView(){
        initTimeSlot();
        initMsgWindow();
        initTimeText(getHours());
        initDividerLine(getHours());
    }

    private void initTimeSlot(){
        double startPoint = timeTextSize * 0.5;
        double timeSlotHeight = lineHeight/4;
        String[] hours = getHours();
        for (int slot = 0; slot < hours.length; slot++) {
            //add full clock
            positionToTimeTreeMap.put((int)startPoint + lineHeight*slot, hours[slot] + ":00");
            String hourPart = hours[slot].substring(0,2); // XX
            timeToPositionTreeMap.put((float) Integer.valueOf(hourPart), (int)startPoint + lineHeight*slot);
            for (int miniSlot = 0; miniSlot < 3; miniSlot++) {
                String minutes = String.valueOf((miniSlot+1)*15);
                String time = hourPart + ":" + minutes;
                int positionY = (int) (startPoint + lineHeight*slot + timeSlotHeight*(miniSlot+1));
                positionToTimeTreeMap.put(positionY, time);
                timeToPositionTreeMap.put(Integer.valueOf(hourPart) + (float) Integer.valueOf(minutes)/100,positionY);
            }
        }
    }

    private void initMsgWindow(){
        msgWindow = new TextView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 0);
        msgWindow.setLayoutParams(params);
        msgWindow.setTextColor(context.getResources().getColor(R.color.text_enable));
        msgWindow.setText("00");
        msgWindow.setTextSize(20);
        msgWindow.setVisibility(View.INVISIBLE);
        dividerRLayout.addView(msgWindow);
    }

    private void initTimeText(String[] HOURS){
        for (int time = 0; time < HOURS.length; time++){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView timeView = new TextView(context);
            params.setMargins(0, lineHeight * time, 0, 0);
            timeView.setLayoutParams(params);
            timeView.setTextColor(context.getResources().getColor(R.color.text_enable));
            timeView.setText(HOURS[time]);
            timeView.setTextSize(12);
            timeView.setGravity(Gravity.CENTER);
            timeView.setIncludeFontPadding(false);
            timeTextSize = (int) timeView.getTextSize() + timeView.getPaddingTop();
            timeRLayout.addView(timeView);
        }
    }

    private void initDividerLine(String[] HOURS){
        int offsetY =  (int)(timeTextSize * 0.5);
        for (int numOfDottedLine = 0 ; numOfDottedLine < HOURS.length ; numOfDottedLine ++){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            ImageView dividerImageView = new ImageView(context);
            dividerImageView.setImageResource(R.drawable.itime_day_view_dotted);
            params.setMargins(0 , lineHeight * numOfDottedLine + offsetY, 0, 0);
            dividerImageView.setLayoutParams(params);
            dividerImageView.setLayerType(dividerImageView.LAYER_TYPE_SOFTWARE,null);
            dividerImageView.setPadding(0,0,0,0);
            dividerRLayout.addView(dividerImageView);
        }
    }

    private String[] getHours(){
        String[] HOURS = new String[]{
                "00","01","02","03","04","05","06","07",
                "08","09","10","11","12","13","14","15",
                "16","17","18","19","20","21","22","23",
                "24"
        };

        return  HOURS;
    }

    public void resetViews(){
        this.regularEventModules.clear();
        this.regularDayDgEventViews.clear();
        this.regular_event_view_map.clear();
        this.allDayEventModules.clear();

        if(this.allDayContainer != null){
            for (DayDraggableEventView dgEventView: this.allDayDgEventViews
                 ) {
                allDayContainer.removeView(dgEventView);
            }
            this.allDayDgEventViews.clear();
        }

        if (this.dividerRLayout != null){
            dividerRLayout.removeAllViews();
        }

        if (this.dividerRLayout != null){
            timeRLayout.removeAllViews();
        }

        if (this.parent != null){
            parent.removeView(nowTime);
            parent.removeView(nowTimeLine);
        }

    }

    public void clearAllEvents(){
        if(this.allDayContainer != null){
            for (DayDraggableEventView dgEventView: this.allDayDgEventViews
                    ) {
                allDayContainer.removeView(dgEventView);
            }
            this.allDayDgEventViews.clear();
        }

        if (this.dividerRLayout != null){
            for (DayDraggableEventView dgView : regularDayDgEventViews
                 ) {
                this.dividerRLayout.removeView(dgView);
            }
        }

        this.regularEventModules.clear();
        this.regularDayDgEventViews.clear();
        this.regular_event_view_map.clear();
        this.allDayEventModules.clear();
    }

    public void addNowTimeLine(){
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HH:mm");
        String localTime = date.format(currentLocalTime);

        nowTime = new TextView(context);
        nowTime.setId(View.generateViewId());
        nowTimeLine = new ImageView(context);
        nowTimeLine.setId(View.generateViewId());

        int lineMarin_top = getNowTimeLinePst() + (int) context.getResources().getDimension(R.dimen.all_day_height);

        nowTime.setText(localTime);
        nowTime.setTextSize(10);
        RelativeLayout.LayoutParams paramsText = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsText.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        paramsText.addRule(RelativeLayout.ALIGN_BOTTOM, nowTimeLine.getId());
        int textPadding = DensityUtil.dip2px(context, 5);
        nowTime.setPadding(textPadding,0,textPadding,0);
        nowTime.setLayoutParams(paramsText);
        nowTime.setTextColor(context.getResources().getColor(R.color.text_today_color));
        nowTime.setBackgroundColor(context.getResources().getColor(R.color.whites));
        this.parent.addView(nowTime);

        RelativeLayout.LayoutParams nowTimeLineParams =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT
                        , ViewGroup.LayoutParams.WRAP_CONTENT);
        nowTimeLine.setImageResource(R.drawable.itime_now_time_full_line);
        nowTimeLineParams.topMargin = lineMarin_top;
        nowTimeLineParams.addRule(RelativeLayout.ALIGN_RIGHT, dividerRLayout.getId());
        nowTimeLineParams.addRule(RelativeLayout.RIGHT_OF, nowTime.getId());
        nowTimeLine.setLayoutParams(nowTimeLineParams);
        this.parent.addView(nowTimeLine);

        this.parent.invalidate();
    }

    private int getNowTimeLinePst(){
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HH:mm");
        String localTime = date.format(currentLocalTime);
        String[] converted = localTime.split(":");
        int hour = Integer.valueOf(converted[0]);
        int minutes = Integer.valueOf(converted[1]);
        int nearestPst = nearestTimeSlotValue(hour + (float)minutes/100); //
        int correctPst = (minutes%15) * ((lineHeight/4)/15);
        return nearestPst + correctPst;
    }

    public void addEvent(ITimeEventInterface event){
        boolean isAllDayEvent = isAllDayEvent(event);
        if (isAllDayEvent){
            allDayEventModules.add(event);
            addAllDayEvent(event);
        }else {
            regularEventModules.add(event);
            addRegularEvent(event);
        }
    }

    public void removeEvent(ITimeEventInterface event){
        this.regular_event_view_map.remove(event);
        this.regularEventModules.remove(event);
        this.dividerRLayout.removeView(dividerRLayout.findViewById(regular_event_view_map.get(event)));
    }

    public void updateEvent(ITimeEventInterface old_event, ITimeEventInterface new_event){
        int index = this.regularEventModules.indexOf(old_event);
        this.regularEventModules.add(index, new_event);

        int tag = this.regular_event_view_map.get(old_event);
        this.regular_event_view_map.remove(old_event);
        this.regular_event_view_map.put(new_event, tag);
    }

    private void addAllDayEvent(ITimeEventInterface event){
        int eventsContainerWidth =
                allDayContainer.getWidth()
                        - allDayContainer.findViewById(R.id.allDayContainerTitle).getWidth()
                        - allDayContainer.getPaddingLeft() - allDayContainer.getPaddingRight();
        int marginLeft = DensityUtil.dip2px(context,1);
        DayDraggableEventView new_dgEvent = this.createDayDraggableEventView(event, true);
//        new_dgEvent.
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) new_dgEvent.getLayoutParams();
        params.leftMargin = marginLeft;
        allDayDgEventViews.add(new_dgEvent);
        allDayContainer.addView(new_dgEvent, params);
        //resize all day events width
        resizeAllDayEvents(eventsContainerWidth, marginLeft);
    }

    private void addRegularEvent(ITimeEventInterface event){
        SimpleDateFormat sdf= new SimpleDateFormat("HH:mm");
        String hourWithMinutes = sdf.format(new Date(event.getStartTime()));
        String[] components = hourWithMinutes.split(":");
        float trickTime = Integer.valueOf(components[0]) + (float) Integer.valueOf(components[1])/100;
        int margin_top = nearestTimeSlotValue(trickTime);

        DayDraggableEventView new_dgEvent = this.createDayDraggableEventView(event, false);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) new_dgEvent.getLayoutParams();
        params.topMargin = margin_top;

        new_dgEvent.setId(View.generateViewId());
        this.regular_event_view_map.put(event,new_dgEvent.getId());
        this.dividerRLayout.addView(new_dgEvent, params);
        this.regularDayDgEventViews.add(new_dgEvent);
    }

    private void resizeAllDayEvents(int totalWidth, int marginLeft){
        int singleEventWidth = (totalWidth - this.allDayDgEventViews.size() * marginLeft)/this.allDayDgEventViews.size();
        for (DayDraggableEventView dgEvent:this.allDayDgEventViews
             ) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dgEvent.getLayoutParams();
            params.width = singleEventWidth;
            params.leftMargin = marginLeft;
        }

        allDayContainer.invalidate();
    }

    private boolean isAllDayEvent(ITimeEventInterface event){
        long duration = event.getEndTime() - event.getStartTime();
        boolean isAllDay = duration >= allDayMilliseconds;

        return isAllDay;
    }

    private int getEventY(ITimeEventInterface event){
        SimpleDateFormat sdf= new SimpleDateFormat("HH:mm");
        String hourWithMinutes = sdf.format(new Date(event.getStartTime()));

        String[] components = hourWithMinutes.split(":");
        float trickTime = Integer.valueOf(components[0]) + Integer.valueOf(components[1])/(float)100;
        int getStartY = nearestTimeSlotValue(trickTime);

        return  getStartY;
    }

    public void reDrawEvents(){
        int layoutWidth = dividerRLayout.getWidth();
        List<ArrayList<Pair<Pair<Integer,Integer>,ITimeEventInterface>>> overlapGroups
                = xHelper.computeOverlapXForEvents(this.regularEventModules);

        int previousGroupExtraY = 0;

        for (ArrayList<Pair<Pair<Integer,Integer>,ITimeEventInterface>> overlapGroup : overlapGroups
             ) {
            for (int i = 0; i < overlapGroup.size(); i++) {
                int getStartY = getEventY(overlapGroup.get(i).second);
                int width_factor = overlapGroup.get(i).first.first;
                int x_pst = overlapGroup.get(i).first.second;
                int eventWidth = layoutWidth/width_factor;
                int margin_left = eventWidth * x_pst;

                DayDraggableEventView event_view =
                        (DayDraggableEventView) dividerRLayout.findViewById(regular_event_view_map.get(overlapGroup.get(i).second));// find by tag

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) event_view.getLayoutParams();
                params.width = eventWidth;
                params.leftMargin = margin_left + 5 * x_pst;
                params.topMargin = getStartY + overlapGapHeight * i + previousGroupExtraY;
                event_view.setLayoutParams(params);
                event_view.invalidate();
            }
            previousGroupExtraY += overlapGapHeight * overlapGroup.size();
        }
        dividerRLayout.requestLayout();
    }

    public void reLoadEvents(){
        this.clearAllEvents();
        if (this.onLoadEvents != null){
            Calendar calendar = myCalendar.getCalendar();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long beginOfDayMilliseconds = calendar.getTimeInMillis();
            List<ITimeEventInterface> events = this.onLoadEvents.loadEvents(beginOfDayMilliseconds);
            if (events != null){
                for (ITimeEventInterface event: events
                        ) {
                    this.addEvent(event);
                }
            }
            this.reDrawEvents();
        }else {
            Log.i(TAG, "reLoadEvents onLoadEvents: null");
        }
        
    }

    public void bringDgViewsToFront(){
        for (DayDraggableEventView dgView: this.regularDayDgEventViews
             ) {
            dgView.bringToFront();
        }
    }

    private DayDraggableEventView createDayDraggableEventView(ITimeEventInterface event, boolean isAllDayEvent){
        DayDraggableEventView event_view = new DayDraggableEventView(context);
        if (isAllDayEvent){
            int allDayHeight = allDayContainer.getWidth() - allDayContainer.getPaddingBottom() - allDayContainer.getPaddingTop();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0, allDayHeight);
            event_view.setSummary(event.getTitle());
            event_view.setTypeAndStatus(event.getEventType(),event.getStatus(), true);
            event_view.setTag(event);
            event_view.setLayoutParams(params);
        }else{
            long duration = event.getEndTime()-event.getStartTime();
            int eventHeight = (int) (((float)duration/(3600*1000))*lineHeight);
            int getStartY = getEventY(event);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0, eventHeight);
            event_view.setTop(getStartY);
            event_view.setOnLongClickListener(new EventLongClickListener());
            event_view.setSummary(event.getTitle());
            event_view.setTypeAndStatus(event.getEventType(),event.getStatus(), duration <= (15 * 60 * 1000));
            event_view.setTag(event);
            event_view.setLayoutParams(params);
        }

        return event_view;
    }

    public DayDraggableEventView createTempDayDraggableEventView(float tapX, float tapY){
        DayDraggableEventView event_view = new DayDraggableEventView(context);
        int eventHeight = 1 * lineHeight;//one hour
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,eventHeight);
        params.topMargin = (int)(tapY - eventHeight/2);
        event_view.setOnLongClickListener(new EventLongClickListener());
        event_view.setLayoutParams(params);

        return event_view;
    }

    /****************************************************************************************/

    private class EventLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View view) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                    view);
            view.startDrag(data, shadowBuilder, view, 0);
            if (tempDragView != null){
                view.setVisibility(View.INVISIBLE);
            }else {
                view.setVisibility(View.VISIBLE);
            }
            view.getBackground().setAlpha(255);
            return false;
        }
    }

    private class EventDragListener implements View.OnDragListener {
        float actionStartX = 0;
        float actionStartY = 0;
        int currentEventNewHour;
        int currentEventNewMinutes;

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    actionStartX = event.getX();
                    actionStartY = event.getY();
                    msgWindow.setVisibility(View.VISIBLE);
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    scrollViewAutoScroll(event);
                    msgWindowFollow((int)event.getX(),(int)event.getY(),(View) event.getLocalState());
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:
                    float actionStopX = event.getX();
                    float actionStopY = event.getY();
                    // Dropped, reassign View to ViewGroup
                    View view = (View) event.getLocalState();
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams(); //WRAP_CONTENT param can be FILL_PARENT

                    int newX = (int) actionStopX - view.getWidth()/2;
                    int newY = (int) actionStopY - view.getHeight()/2;
                    int[] reComputeResult = reComputePositionToSet(newX,newY,view,v);

                    //update the event time
                    String new_time = positionToTimeTreeMap.get(reComputeResult[1]);
                    //important! update event time after drag
                    String[] time_parts = new_time.split(":");
                    currentEventNewHour = Integer.valueOf(time_parts[0]);
                    currentEventNewMinutes = Integer.valueOf(time_parts[1]);

                    if (tempDragView == null){
                        //if not the new drag event, then update event instance
                        ITimeEventInterface dragging_event = (ITimeEventInterface) view.getTag();
                        long[] new_date = changeDateFromString(dragging_event, currentEventNewHour, currentEventNewMinutes);
                        dragging_event.setStartTime(new_date[0]);
                        dragging_event.setEndTime(new_date[1]);
                        Log.i(TAG, "changed: ");
                    }

                    //update Y position
                    params.topMargin = reComputeResult[1];

                    view.setLayoutParams(params);
                    view.getBackground().setAlpha(128);
                    reDrawEvents();

                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    View finalView = (View) event.getLocalState();
                    finalView.getBackground().setAlpha(128);
                    finalView.setVisibility(View.VISIBLE);
                    msgWindow.setVisibility(View.INVISIBLE);

                    if (tempDragView != null){
                        ViewGroup parent = (ViewGroup)tempDragView.getParent();
                        parent.removeView(tempDragView);
                        //important! update event time after drag via listener
                        if (onCreateNewEvent != null){
                            MyCalendar newEventCalendar = new MyCalendar(myCalendar);
                            newEventCalendar.setHour(currentEventNewHour);
                            newEventCalendar.setMinute(currentEventNewMinutes);
                            onCreateNewEvent.createNewEvent(newEventCalendar);
                        }else {
                            Log.i(TAG, "onCreateNewEvent null: ");
                        }
                        //finally reset tempDragView to NULL.
                        tempDragView = null;
                    }
                default:
                    break;
            }
            return true;
        }
    }

    private class CreateEventListener implements View.OnLongClickListener{

        @Override
        public boolean onLongClick(View v) {
            if (tempDragView == null){
                tempDragView = createTempDayDraggableEventView(nowTapX, nowTapY);
                dividerRLayout.addView(tempDragView);
                tempDragView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tempDragView.performLongClick();
                    }
                },100);
            }

            return true;
        }
    }
    /****************************************************************************************/

    private int[] reComputePositionToSet(int actualX, int actualY, View draggableObj, View container){
        int containerWidth = container.getWidth();
        int containerHeight = container.getHeight();

        int objWidth = draggableObj.getWidth();
        int objHeight = draggableObj.getHeight();

        int finalX = (int)(timeTextSize * 1.5);
        int finalY = actualY;

        if (actualY < 0){
            finalY = 0;
        }else if (actualY + objHeight > containerHeight){
            finalY = containerHeight - objHeight;
        }
        int findNearestPosition = nearestTimeSlotKey(finalY);
        if (findNearestPosition != -1){
            finalY = findNearestPosition;
        }else{
            Log.i(TAG, "reComputePositionToSet: " + "ERROR NO SUCH POSITION");
        }

        return new int[] {finalX,finalY};
    }

    private int nearestTimeSlotKey(int tapY){
        int key = tapY;
        Map.Entry<Integer,String> low = positionToTimeTreeMap.floorEntry(key);
        Map.Entry<Integer,String> high = positionToTimeTreeMap.ceilingEntry(key);
        if (low != null && high != null) {
            return Math.abs(key-low.getKey()) < Math.abs(key-high.getKey())
                    ?   low.getKey()
                    :   high.getKey();
        } else if (low != null || high != null) {
            return low != null ? low.getKey() : high.getKey();
        }

        return -1;
    }

    private int nearestTimeSlotValue(float time){
        float key = time;
        Map.Entry<Float,Integer> low = timeToPositionTreeMap.floorEntry(key);
        Map.Entry<Float,Integer> high = timeToPositionTreeMap.ceilingEntry(key);
        if (low != null && high != null) {
            return Math.abs(key-low.getKey()) < Math.abs(key-high.getKey())
                    ?   low.getValue()
                    :   high.getValue();
        } else if (low != null || high != null) {
            return low != null ? low.getValue() : high.getValue();
        }

        return -1;
    }

    private void scrollViewAutoScroll(DragEvent event){
        Rect scrollBounds = new Rect();
        scrollContainerView.getDrawingRect(scrollBounds);
        float heightOfView = ((View) event.getLocalState()).getHeight();
        float needPositionY_top = event.getY() - heightOfView/2;
        float needPositionY_bottom = event.getY() + heightOfView/2;

        if (scrollBounds.top > needPositionY_top) {
            int offsetY = (int)(scrollContainerView.getScrollY() +(needPositionY_top-scrollBounds.top) );
            scrollContainerView.scrollTo(scrollContainerView.getScrollX(),offsetY);
        } else if(scrollBounds.bottom < needPositionY_bottom){
            int offsetY = (int) (scrollContainerView.getScrollY() + (needPositionY_bottom-scrollBounds.bottom));
            scrollContainerView.scrollTo(scrollContainerView.getScrollX(),offsetY);
        }
    }

    private void msgWindowFollow(int tapX, int tapY, View followView){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) msgWindow.getLayoutParams(); //WRAP_CONTENT param can be FILL_PARENT
        params.topMargin = tapY - followView.getHeight()/2 - msgWindow.getHeight();

        if (tapX + msgWindow.getWidth()/2 > dividerRLayout.getWidth()){
            params.leftMargin = dividerRLayout.getWidth() - msgWindow.getWidth();
        }else if(tapX - msgWindow.getWidth()/2 < 0){
            params.leftMargin = 0;
        } else{
            params.leftMargin = tapX - msgWindow.getWidth()/2;
        }
        int nearestProperPosition = nearestTimeSlotKey(tapY - followView.getHeight()/2);
        if (nearestProperPosition != -1){
            msgWindow.setText(positionToTimeTreeMap.get(nearestProperPosition));
        }else{
            Log.i(TAG, "msgWindowFollow: " + "Error, text not found in Map");
        }
        msgWindow.setLayoutParams(params);
    }

    private long[ ] changeDateFromString(ITimeEventInterface event, int hour, int minute){
        long startTime = event.getStartTime();
        long endTime = event.getEndTime();
        long duration = endTime - startTime;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.MILLISECOND, 0);

        long new_start = calendar.getTimeInMillis();
        calendar.setTimeInMillis(new_start + duration);
        long new_end = calendar.getTimeInMillis();
        long[] param = {new_start,new_end};

        return param;
    }

    /*************************** Interface ******************************************/
    public interface BodyOnTouchListener{
        void bodyOnTouchListener(float tapX, float tapY);
    }

    public interface OnCreateNewEvent{
        void createNewEvent(MyCalendar newEventCalendar);
    }

    public interface OnLoadEvents{
        List<ITimeEventInterface> loadEvents(long beginOfDayM);
    }

    public void setBodyOnTouchListener(BodyOnTouchListener bodyOnTouchListener) {
        this.bodyOnTouchListener = bodyOnTouchListener;
    }


    public void setOnCreateNewEvent(OnCreateNewEvent onCreateNewEvent) {
        this.onCreateNewEvent = onCreateNewEvent;
    }

    public void setOnLoadEvents(OnLoadEvents onLoadEvents) {
        this.onLoadEvents = onLoadEvents;
    }
    /****************************************************************************************/

    private void printAllEventViewInfo(){
        for(Map.Entry<ITimeEventInterface, Integer> entry : regular_event_view_map.entrySet()){
            int id = entry.getValue();
            DayDraggableEventView view = (DayDraggableEventView) dividerRLayout.findViewById(id);
            Log.i(TAG, "\n Event Info: ");
            Log.i(TAG, "width: " + view.getWidth());
            Log.i(TAG, "X pst: " + view.getX());
            Log.i(TAG, "Y pst: " + view.getY());
            Log.i(TAG, "Margin-Left pst: " + view.getLeft());
            Log.i(TAG, "Margin-Top pst: " + view.getTop());
        }
    }

}
