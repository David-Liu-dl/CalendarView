package org.unimelb.itime.vendor.dayview;

import android.content.ClipData;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.eventview.DayDraggableEventView;
import org.unimelb.itime.vendor.eventview.Event;
import org.unimelb.itime.vendor.helper.CalendarEventOverlapHelper;
import org.unimelb.itime.vendor.helper.DensityUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by yuhaoliu on 3/08/16.
 */
public class DayViewBodyController {
    public final String TAG = "MyAPP";

    private RelativeLayout parent;

    @NonNull
    private RelativeLayout timeRLayout;
    @NonNull
    private RelativeLayout dividerRLayout;

    public ScrollContainerView scrollContainerView;

    private Context context;

    private ArrayList<Event> eventModules = new ArrayList<>();

    private TextView msgWindow;
    private ImageView nowTimeLine;
    private TextView nowTime;

    private TreeMap< Integer, String> positionToTimeTreeMap = new TreeMap<>();
    private TreeMap<Float, Integer> timeToPositionTreeMap = new TreeMap<>();
    private Map<Event, Integer> event_view_map = new HashMap<>();

    private CalendarEventOverlapHelper xHelper = new CalendarEventOverlapHelper();

    private int lineHeight = 50;
    private int timeTextSize = 20;

    public DayViewBodyController(AttributeSet attrs,
                                 Context context) {
        this.context = context;
        loadAttributes(attrs, context);
//        this.eventModules = eventModules;
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

    public void onFinishInflate(ScrollContainerView scrollContainerView, RelativeLayout timelineRL, RelativeLayout dividerRL, RelativeLayout parent){
        this.parent = parent;
        this.timeRLayout = timelineRL;
        this.dividerRLayout = dividerRL;

        this.scrollContainerView = scrollContainerView;
        dividerRLayout.setOnDragListener(new MyDragListener());
    }

    public void initBackgroundView(){
        initTimeSlot();
        initMsgWindow();
        initTimeText(getHours());
        initDividerLine(getHours());
    }

    public void resetViews(){
        this.eventModules.clear();
        this.event_view_map.clear();

        if (this.dividerRLayout != null){
            dividerRLayout.removeAllViews();
        }

        if (this.parent != null){
            parent.removeView(nowTime);
            parent.removeView(nowTimeLine);
        }

    }

    public void addEvent(Event event){
        this.eventModules.add(event);

        SimpleDateFormat sdf= new SimpleDateFormat("HH:mm");
        String hourWithMinutes = sdf.format(new Date(event.getStartTime()));
        String[] components = hourWithMinutes.split(":");
        float trickTime = Integer.valueOf(components[0]) + (float) Integer.valueOf(components[1])/100;
        int margin_top = nearestTimeSlotValue(trickTime);

        DayDraggableEventView new_dgEvent = this.createDayDraggableEventView(event);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) new_dgEvent.getLayoutParams();
        params.topMargin = margin_top;

//        int id = event_view_map.size();
        new_dgEvent.setId(View.generateViewId());
        this.event_view_map.put(event,new_dgEvent.getId());
        this.dividerRLayout.addView(new_dgEvent, params);
    }

    public void removeEvent(Event event){
        this.event_view_map.remove(event);
        this.eventModules.remove(event);
        this.dividerRLayout.removeView(dividerRLayout.findViewById(event_view_map.get(event)));
    }

    public void updateEvent(Event old_event, Event new_event){
        int index = this.eventModules.indexOf(old_event);
        this.eventModules.add(index, new_event);

        int tag = this.event_view_map.get(old_event);
        this.event_view_map.remove(old_event);
        this.event_view_map.put(new_event, tag);
    }

    private DayDraggableEventView createDayDraggableEventView(Event event){
        SimpleDateFormat sdf= new SimpleDateFormat("HH:mm");
        String hourWithMinutes = sdf.format(new Date(event.getStartTime()));

        long duration = event.getEndTime()-event.getStartTime();
        int eventHeight = (int) (duration/(3600*1000))*lineHeight;

        String[] components = hourWithMinutes.split(":");
        float trickTime = Integer.valueOf(components[0]) + Integer.valueOf(components[1])/(float)100;
        int getStartY = nearestTimeSlotValue(trickTime);

        DayDraggableEventView event_view = new DayDraggableEventView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0, eventHeight);
        event_view.setTop(getStartY);
        event_view.setLayoutParams(params);
        event_view.setOnLongClickListener(new MyTouchListener());
        event_view.setSummary(event.getTitle());
        event_view.setTypeAndStatus(event.getEventType(),event.getStatus());
        event_view.setTag(event);

        return event_view;
    }

    public void reDrawEvents(){
        int layoutWidth = dividerRLayout.getWidth();
        ArrayList<Pair<Pair<Integer,Integer>,Event>> param_events
                = xHelper.computeOverlapXForEvents(this.eventModules);

        for (int i = 0; i < param_events.size(); i++) {
            int width_factor = param_events.get(i).first.first;
            int x_pst = param_events.get(i).first.second;
            int eventWidth = layoutWidth/width_factor;
            int margin_left = eventWidth * x_pst;

            DayDraggableEventView event_view =
                    (DayDraggableEventView) dividerRLayout.findViewById(event_view_map.get(param_events.get(i).second));// find by tag

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) event_view.getLayoutParams();
            params.width = eventWidth;
            params.leftMargin = margin_left + 5 * x_pst;
            event_view.setLayoutParams(params);
            event_view.invalidate();
        }

        dividerRLayout.requestLayout();
    }

    public void addNowTimeLine(){
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(
                        dividerRLayout.getWidth() + DensityUtil.dip2px(context, 5)
                        , ViewGroup.LayoutParams.WRAP_CONTENT);
        int lineMarin_top = getNowTimeLinePst();

        nowTimeLine = new ImageView(context);
        nowTimeLine.setImageResource(R.drawable.itime_now_time_full_line);
        params.setMargins(0, lineMarin_top, 0, 0);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        nowTimeLine.setLayoutParams(params);
//        nowTimeLine.setLayerType(nowTimeLine.LAYER_TYPE_SOFTWARE,null);
        nowTimeLine.setPadding(0,0,0,0);
        parent.addView(nowTimeLine);

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HH:mm");
        String localTime = date.format(currentLocalTime);
        nowTime = new TextView(context);
        nowTime.setText(localTime);
        nowTime.setTextSize(DensityUtil.sp2px(context, 4));
        RelativeLayout.LayoutParams paramsText = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsText.setMargins(0, lineMarin_top - nowTime.getLineHeight(), 0, 0);
        nowTime.setLayoutParams(paramsText);
        nowTime.setTextColor(context.getResources().getColor(R.color.text_today_color));
        nowTime.setBackgroundColor(context.getResources().getColor(R.color.whites));
        this.parent.addView(nowTime);


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

    private void initMsgWindow(){
        msgWindow = new TextView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 0);
        msgWindow.setLayoutParams(params);
        msgWindow.setText("00");
        msgWindow.setTextSize(20);
        msgWindow.setVisibility(View.INVISIBLE);
        dividerRLayout.addView(msgWindow);
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

    private void initTimeText(String[] HOURS){
        for (int time = 0; time < HOURS.length; time++){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView timeView = new TextView(context);
            params.setMargins(0, lineHeight * time, 0, 0);
            timeView.setLayoutParams(params);
            timeView.setText(HOURS[time]);
            timeView.setTextSize(12);
            timeView.setGravity(Gravity.CENTER);
            timeView.setIncludeFontPadding(false);
            timeTextSize = (int) timeView.getTextSize() + timeView.getPaddingTop();
            timeRLayout.addView(timeView);
        }
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

    /****************************************************************************************/

    private final class MyTouchListener implements View.OnLongClickListener {
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

    private final class MyDragListener implements View.OnDragListener {

        float actionStartX = 0;
        float actionStartY = 0;

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
                    String[] time_parts = new_time.split(":");
                    int hour = Integer.valueOf(time_parts[0]);
                    int minutes = Integer.valueOf(time_parts[1]);
                    Event dragging_event = (Event) view.getTag();
                    long[] new_date = changeDateFromString(dragging_event, hour, minutes);
                    dragging_event.setStartTime(new_date[0]);
                    dragging_event.setEndTime(new_date[1]);
//                    params.leftMargin = reComputeResult[0];
                    params.topMargin = reComputeResult[1];

                    view.setLayoutParams(params);
                    view.getBackground().setAlpha(128);
                    reDrawEvents();

                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    View finalView = (View) event.getLocalState();
                    finalView.getBackground().setAlpha(128);
                    msgWindow.setVisibility(View.INVISIBLE);
                default:
                    break;
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

    private long[ ] changeDateFromString(Event event, int hour, int minute){
        long startTime = event.getStartTime();
        long endTime = event.getEndTime();
        long duration = endTime - startTime;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        long new_start = calendar.getTimeInMillis();
        calendar.setTimeInMillis(new_start + duration);
        long new_end = calendar.getTimeInMillis();
        long[] param = {new_start,new_end};

        return param;
    }

    /****************************************************************************************/

    private void printAllEventViewInfo(){
        for(Map.Entry<Event, Integer> entry : event_view_map.entrySet()){
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
