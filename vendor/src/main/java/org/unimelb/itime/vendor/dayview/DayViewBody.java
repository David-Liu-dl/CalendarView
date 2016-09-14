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
public class DayViewBody extends RelativeLayout {
    public final String TAG = "MyAPP";

    private final long allDayMilliseconds = 24 * 60 * 60 * 1000;

    private ScrollContainerView scrollContainerView;
    private RelativeLayout bodyContainerLayout;

    private LinearLayout topAllDayLayout;
    private LinearLayout topAllDayEventLayout;

    private RelativeLayout leftSideRLayout;
    private RelativeLayout rightContentLayout;

    private RelativeLayout dividerBgRLayout;
    private RelativeLayout eventLayout;

    public MyCalendar myCalendar;
    private Context context;

    private List<ITimeEventInterface> eventList = new ArrayList<>();

    private ArrayList<ITimeEventInterface> regularEventModules = new ArrayList<>();
    private ArrayList<DayDraggableEventView> regularDayDgEventViews = new ArrayList<>();
    private ArrayList<ITimeEventInterface> allDayEventModules = new ArrayList<>();
    private ArrayList<DayDraggableEventView> allDayDgEventViews = new ArrayList<>();

    private TreeMap<Integer, String> positionToTimeTreeMap = new TreeMap<>();
    private TreeMap<Float, Integer> timeToPositionTreeMap = new TreeMap<>();
    private Map<ITimeEventInterface, Integer> regularEventViewMap = new HashMap<>();

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

    private OnBodyTouchListener onBodyTouchListener;
    private OnBodyListener onBodyListener;

    public DayViewBody(Context context) {
        super(context);
        this.context = context;
        this.overlapGapHeight = DensityUtil.dip2px(context, 1);
        init();
        initBackgroundView();
    }

    public DayViewBody(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.overlapGapHeight = DensityUtil.dip2px(context, 1);
        loadAttributes(attrs, context);
        init();
        initBackgroundView();
    }

    public DayViewBody(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.overlapGapHeight = DensityUtil.dip2px(context, 1);
        loadAttributes(attrs, context);
        init();
        initBackgroundView();
    }

    private void init() {
        scrollContainerView = new ScrollContainerView(context);
        scrollContainerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.addView(scrollContainerView);

        bodyContainerLayout = new RelativeLayout(context);
        bodyContainerLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        scrollContainerView.addView(bodyContainerLayout);

        topAllDayLayout = new LinearLayout(getContext());
        topAllDayLayout.setId(View.generateViewId());
        RelativeLayout.LayoutParams topAllDayLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        topAllDayLayout.setLayoutParams(topAllDayLayoutParams);

        ImageView divider = getDivider();
        ((RelativeLayout.LayoutParams) divider.getLayoutParams()).addRule(BELOW, topAllDayLayout.getId());
        bodyContainerLayout.addView(divider);

        TextView allDayTitleTv = new TextView(context);
        LinearLayout.LayoutParams allDayTitleTvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        int allDayTitleTvPadding = DensityUtil.dip2px(context, 3);
        allDayTitleTv.setPadding(allDayTitleTvPadding, allDayTitleTvPadding, allDayTitleTvPadding, allDayTitleTvPadding);
        allDayTitleTv.setTextSize(10);
        allDayTitleTv.setText("All Day");
        allDayTitleTv.setTextColor(context.getResources().getColor(R.color.text_enable));
        allDayTitleTv.setGravity(Gravity.CENTER_VERTICAL);
        allDayTitleTv.setLayoutParams(allDayTitleTvParams);
        topAllDayLayout.addView(allDayTitleTv);

        topAllDayEventLayout = new LinearLayout(getContext());
        topAllDayEventLayout.setId(View.generateViewId());
        RelativeLayout.LayoutParams topAllDayEventLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(context, 40));
        topAllDayEventLayout.setLayoutParams(topAllDayEventLayoutParams);
        topAllDayLayout.addView(topAllDayEventLayout);

        leftSideRLayout = new RelativeLayout(getContext());
        leftSideRLayout.setId(View.generateViewId());
        RelativeLayout.LayoutParams leftSideRLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        leftSideRLayoutParams.addRule(RelativeLayout.BELOW, topAllDayLayout.getId());
        leftSideRLayout.setPadding(DensityUtil.dip2px(context, 10), 0, DensityUtil.dip2px(context, 10), 0);
        leftSideRLayout.setLayoutParams(leftSideRLayoutParams);

        rightContentLayout = new RelativeLayout(getContext());
        rightContentLayout.setId(View.generateViewId());
        RelativeLayout.LayoutParams rightContentLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rightContentLayoutParams.addRule(RelativeLayout.BELOW, topAllDayLayout.getId());
        rightContentLayoutParams.addRule(RelativeLayout.RIGHT_OF, leftSideRLayout.getId());
        rightContentLayoutParams.addRule(RelativeLayout.ALIGN_TOP, leftSideRLayout.getId());
        rightContentLayoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, leftSideRLayout.getId());
        rightContentLayout.setLayoutParams(rightContentLayoutParams);

        dividerBgRLayout = new RelativeLayout(getContext());
        dividerBgRLayout.setId(View.generateViewId());
        RelativeLayout.LayoutParams dividerBgRLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dividerBgRLayout.setLayoutParams(dividerBgRLayoutParams);
        rightContentLayout.addView(dividerBgRLayout);

        eventLayout = new RelativeLayout(getContext());
        eventLayout.setId(View.generateViewId());
        RelativeLayout.LayoutParams eventLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        eventLayout.setLayoutParams(eventLayoutParams);
        eventLayout.setOnDragListener(new EventDragListener());
        eventLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                nowTapX = event.getX();
                nowTapY = event.getY();
                if (onBodyTouchListener != null) {
                    onBodyTouchListener.bodyOnTouchListener(nowTapX, nowTapY);
                } else {
                    Log.i(TAG, "controller:  onBodyTouchListener null ");
                }
                return false;
            }
        });
        eventLayout.setOnLongClickListener(new CreateEventListener());
        rightContentLayout.addView(eventLayout);

        bodyContainerLayout.addView(topAllDayLayout);
        bodyContainerLayout.addView(leftSideRLayout);
        bodyContainerLayout.addView(rightContentLayout);

    }

    private ImageView getDivider() {
        ImageView dividerImgV;
        //divider
        dividerImgV = new ImageView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dividerImgV.setLayoutParams(params);
        dividerImgV.setImageDrawable(getResources().getDrawable(org.unimelb.itime.vendor.R.drawable.itime_header_divider_line));

        return dividerImgV;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        dividerBgRLayout.measure(widthMeasureSpec, heightMeasureSpec);

        int eventCount = eventLayout.getChildCount();
        int layoutWidth = dividerBgRLayout.getMeasuredWidth();

        for (int i = 0; i < eventCount; i++) {
            if (!(eventLayout.getChildAt(i) instanceof DayDraggableEventView)) {
                continue;
            }
            DayDraggableEventView eventView = (DayDraggableEventView) eventLayout.getChildAt(i);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) eventView.getLayoutParams();
            DayDraggableEventView.PosParam pos = eventView.getPosParam();
            if (pos == null) {
                // for creating a new event
                // the pos parameter is null, because we just mock it
                continue;
            }
            int eventWidth = layoutWidth / pos.widthFactor;
            int leftMargin = eventWidth * pos.startX;
            params.width = eventWidth;
            params.leftMargin = leftMargin + 5 * pos.startX;
            params.topMargin = pos.topMargin;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
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

    public void initBackgroundView() {
        initTimeSlot();
        initMsgWindow();
        initTimeText(getHours());
        initDividerLine(getHours());
        addNowTimeLine();
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

    private void initMsgWindow() {
        msgWindow = new TextView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 0);
        msgWindow.setLayoutParams(params);
        msgWindow.setTextColor(context.getResources().getColor(R.color.text_enable));
        msgWindow.setText("00:00");
        msgWindow.setTextSize(20);
        msgWindow.setGravity(Gravity.CENTER);
        msgWindow.setVisibility(View.INVISIBLE);
        eventLayout.addView(msgWindow);
    }

    private void initTimeText(String[] HOURS) {
        for (int time = 0; time < HOURS.length; time++) {
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
            leftSideRLayout.addView(timeView);
        }
    }


    private void initDividerLine(String[] HOURS) {
        int offsetY = (int) (timeTextSize * 0.5);
        for (int numOfDottedLine = 0; numOfDottedLine < HOURS.length; numOfDottedLine++) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            ImageView dividerImageView = new ImageView(context);
            dividerImageView.setImageResource(R.drawable.itime_day_view_dotted);
            params.setMargins(0, lineHeight * numOfDottedLine + offsetY, 0, 0);
            dividerImageView.setLayoutParams(params);
            dividerImageView.setLayerType(dividerImageView.LAYER_TYPE_SOFTWARE, null);
            dividerImageView.setPadding(0, 0, 0, 0);
            dividerBgRLayout.addView(dividerImageView);
        }
    }

    public void setCalendar(MyCalendar myCalendar) {
        this.myCalendar = myCalendar;
    }

    public MyCalendar getCalendar() {
        return myCalendar;
    }

    private String[] getHours() {
        String[] HOURS = new String[]{
                "00", "01", "02", "03", "04", "05", "06", "07",
                "08", "09", "10", "11", "12", "13", "14", "15",
                "16", "17", "18", "19", "20", "21", "22", "23",
                "24"
        };

        return HOURS;
    }

    /**
     * call it when your dataset changed
     */
//    public void notifyDatasetChanged() {
//        calculateEventLayout();
//    }

    /**
     * reset all the layouts and views in body
     */
    public void resetViews() {
        clearAllEvents();
        if (this.myCalendar.isToday()) {
            nowTime.setVisibility(VISIBLE);
            nowTimeLine.setVisibility(VISIBLE);
        } else {
            nowTime.setVisibility(GONE);
            nowTimeLine.setVisibility(GONE);
        }

    }

    /**
     * just clear the events in the layout
     */
    public void clearAllEvents() {
        if (this.topAllDayEventLayout != null) {
            this.topAllDayEventLayout.removeAllViews();
        }

        if (this.eventLayout != null) {
            this.eventLayout.removeAllViews();
            this.initMsgWindow();
        }

        this.regularEventModules.clear();
        this.regularDayDgEventViews.clear();
        this.regularEventViewMap.clear();
        this.allDayDgEventViews.clear();
        this.allDayEventModules.clear();
    }

    public void addNowTimeLine() {
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
        nowTime.setPadding(textPadding / 2, 0, textPadding / 2, 0);
        nowTime.setLayoutParams(paramsText);
        nowTime.setTextColor(context.getResources().getColor(R.color.text_today_color));
        nowTime.setBackgroundColor(context.getResources().getColor(R.color.whites));
        bodyContainerLayout.addView(nowTime);

        RelativeLayout.LayoutParams nowTimeLineParams =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT
                        , ViewGroup.LayoutParams.WRAP_CONTENT);
        nowTimeLine.setImageResource(R.drawable.itime_now_time_full_line);
        nowTimeLineParams.topMargin = lineMarin_top;
        nowTimeLineParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        nowTimeLineParams.addRule(RelativeLayout.RIGHT_OF, nowTime.getId());
        nowTimeLine.setLayoutParams(nowTimeLineParams);
        bodyContainerLayout.addView(nowTimeLine);
    }

    private int getNowTimeLinePst() {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HH:mm");
        String localTime = date.format(currentLocalTime);
        String[] converted = localTime.split(":");
        int hour = Integer.valueOf(converted[0]);
        int minutes = Integer.valueOf(converted[1]);
        int nearestPst = nearestTimeSlotValue(hour + (float) minutes / 100); //
        int correctPst = (minutes % 15) * ((lineHeight / 4) / 15);
        return nearestPst + correctPst;
    }

    /**
     * add one event, only called in this class
     *
     * @param event
     */
    private void addEvent(ITimeEventInterface event) {
        boolean isAllDayEvent = isAllDayEvent(event);

        if (isAllDayEvent) {
            allDayEventModules.add(event);
            addAllDayEvent(event);
        } else {
            regularEventModules.add(event);
            addRegularEvent(event);
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

    private void addRegularEvent(ITimeEventInterface event) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String hourWithMinutes = sdf.format(new Date(event.getStartTime()));
        String[] components = hourWithMinutes.split(":");
        float trickTime = Integer.valueOf(components[0]) + (float) Integer.valueOf(components[1]) / 100;
        int topMargin = nearestTimeSlotValue(trickTime);
        DayDraggableEventView newDragEventView = this.createDayDraggableEventView(event, false);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) newDragEventView.getLayoutParams();
        params.topMargin = topMargin;

        newDragEventView.setId(View.generateViewId());
        this.regularEventViewMap.put(event, newDragEventView.getId());
        this.eventLayout.addView(newDragEventView, params);
        this.regularDayDgEventViews.add(newDragEventView);
    }

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

    private int getEventY(ITimeEventInterface event) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String hourWithMinutes = sdf.format(new Date(event.getStartTime()));

        String[] components = hourWithMinutes.split(":");
        float trickTime = Integer.valueOf(components[0]) + Integer.valueOf(components[1]) / (float) 100;
        int getStartY = nearestTimeSlotValue(trickTime);

        return getStartY;
    }


    /**
     * set publc for other
     *
     * @param eventList
     */
    public void setEventList(List<ITimeEventInterface> eventList) {
        this.eventList = eventList;
        this.clearAllEvents();

        for (ITimeEventInterface event : eventList) {
            this.addEvent(event);
        }

        calculateEventLayout();
        this.requestLayout();
    }

    /**
     * calculate the position of event
     * it needs to be called when setting event or event position changed
     */
    private void calculateEventLayout() {
        Log.i(TAG, "calculateEventLayout: ");
        List<ArrayList<Pair<Pair<Integer, Integer>, ITimeEventInterface>>> overlapGroups
                = xHelper.computeOverlapXForEvents(this.regularEventModules);
        int previousGroupExtraY = 0;
        for (ArrayList<Pair<Pair<Integer, Integer>, ITimeEventInterface>> overlapGroup : overlapGroups
                ) {
            for (int i = 0; i < overlapGroup.size(); i++) {
                int startY = getEventY(overlapGroup.get(i).second);
                int widthFactor = overlapGroup.get(i).first.first;
                int startX = overlapGroup.get(i).first.second;
                int topMargin = startY + overlapGapHeight * i + previousGroupExtraY;
                DayDraggableEventView eventView = (DayDraggableEventView) eventLayout.findViewById(regularEventViewMap.get(overlapGroup.get(i).second));
                eventView.setPosParam(new DayDraggableEventView.PosParam(startY, startX, widthFactor, topMargin));
            }
            previousGroupExtraY += overlapGapHeight * overlapGroup.size();
        }
    }

    private DayDraggableEventView createDayDraggableEventView(ITimeEventInterface event, boolean isAllDayEvent) {

        DayDraggableEventView event_view = new DayDraggableEventView(context, event, isAllDayEvent);
        event_view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onBodyListener != null) {
                    onBodyListener.onEventClick((DayDraggableEventView) view);
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
            int getStartY = getEventY(event);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0, eventHeight);
            event_view.setTop(getStartY);
            event_view.setOnLongClickListener(new EventLongClickListener());
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
        int eventHeight = 1 * lineHeight;//one hour
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, eventHeight);
        params.topMargin = (int) (tapY - eventHeight / 2);
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
            if (tempDragView != null) {
                view.setVisibility(View.INVISIBLE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
            view.getBackground().setAlpha(255);
            return false;
        }
    }

    private class EventDragListener implements View.OnDragListener {
        float actionStartX = 0;
        float actionStartY = 0;
        int currentEventNewHour = -1;
        int currentEventNewMinutes = -1;

        @Override
        public boolean onDrag(View v, DragEvent event) {
            DayDraggableEventView dgView = (DayDraggableEventView) event.getLocalState();

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    actionStartX = event.getX();
                    actionStartY = event.getY();
                    msgWindow.setVisibility(View.VISIBLE);
                    Log.i(TAG, "start: " + myCalendar.toString());
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    scrollViewAutoScroll(event);
                    if (onBodyListener != null) {
                        onBodyListener.onEventDragging(dgView, (int) event.getX(), (int) event.getY());
                    } else {
                        Log.i(TAG, "onDrag: null onEventDragListener");
                    }
                    msgWindowFollow((int) event.getX(), (int) event.getY(), (View) event.getLocalState());
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:
                    float actionStopX = event.getX();
                    float actionStopY = event.getY();
                    // Dropped, reassign View to ViewGroup

                    int newX = (int) actionStopX - dgView.getWidth() / 2;
                    int newY = (int) actionStopY - dgView.getHeight() / 2;
                    int[] reComputeResult = reComputePositionToSet(newX, newY, dgView, v);

                    //update the event time
                    String new_time = positionToTimeTreeMap.get(reComputeResult[1]);
                    //important! update event time after drag
                    String[] time_parts = new_time.split(":");
                    currentEventNewHour = Integer.valueOf(time_parts[0]);
                    currentEventNewMinutes = Integer.valueOf(time_parts[1]);

                    if (tempDragView == null) {
                        //if not the new drag event, then update event instance
                        if (onBodyListener != null) {
//                            onBodyListener.onEventDragDrop(dgView);
                        }
                    }
                    //update Y position

                    dgView.getBackground().setAlpha(128);

                    View finalView = (View) event.getLocalState();
                    finalView.getBackground().setAlpha(128);
                    finalView.setVisibility(View.VISIBLE);
                    msgWindow.setVisibility(View.INVISIBLE);

                    dgView.getNewCalendar().setHour(currentEventNewHour);
                    dgView.getNewCalendar().setMinute(currentEventNewMinutes);

                    if (tempDragView == null && onBodyListener != null) {
                        if ((currentEventNewHour != -1) && (currentEventNewMinutes != -1)) {

                            onBodyListener.onEventDragDrop(dgView);
                            Log.i(TAG, "on drag: ");
                        }
                    } else {
                        Log.i(TAG, "onDrag: null");
                    }

                    if (tempDragView != null) {
                        ViewGroup parent = (ViewGroup) tempDragView.getParent();
                        parent.removeView(tempDragView);
                        //important! update event time after drag via listener
                        if (onBodyListener != null) {
                            onBodyListener.onEventCreate(dgView);
                        }
                        //finally reset tempDragView to NULL.
                        tempDragView = null;
                        Log.i(TAG, "create: ");
                    }

                    currentEventNewHour = -1;
                    currentEventNewMinutes = -1;

                    break;
                case DragEvent.ACTION_DRAG_ENDED:

                default:
                    break;
            }

            return true;
        }
    }

    private class CreateEventListener implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {
            if (tempDragView == null) {
                tempDragView = createTempDayDraggableEventView(nowTapX, nowTapY);
                eventLayout.addView(tempDragView);

                tempDragView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "tempDragView: " + tempDragView.getHeight());
                        tempDragView.performLongClick();
                    }
                }, 100);
            }

            return true;
        }
    }

    /****************************************************************************************/

    private int[] reComputePositionToSet(int actualX, int actualY, View draggableObj, View container) {
        int containerWidth = container.getWidth();
        int containerHeight = container.getHeight();

        int objWidth = draggableObj.getWidth();
        int objHeight = draggableObj.getHeight();

        int finalX = (int) (timeTextSize * 1.5);
        int finalY = actualY;

        if (actualY < 0) {
            finalY = 0;
        } else if (actualY + objHeight > containerHeight) {
            finalY = containerHeight - objHeight;
        }
        int findNearestPosition = nearestTimeSlotKey(finalY);
        if (findNearestPosition != -1) {
            finalY = findNearestPosition;
        } else {
            Log.i(TAG, "reComputePositionToSet: " + "ERROR NO SUCH POSITION");
        }

        return new int[]{finalX, finalY};
    }

    private int nearestTimeSlotKey(int tapY) {
        int key = tapY;
        Map.Entry<Integer, String> low = positionToTimeTreeMap.floorEntry(key);
        Map.Entry<Integer, String> high = positionToTimeTreeMap.ceilingEntry(key);
        if (low != null && high != null) {
            return Math.abs(key - low.getKey()) < Math.abs(key - high.getKey())
                    ? low.getKey()
                    : high.getKey();
        } else if (low != null || high != null) {
            return low != null ? low.getKey() : high.getKey();
        }

        return -1;
    }

    private int nearestTimeSlotValue(float time) {
        float key = time;
        Map.Entry<Float, Integer> low = timeToPositionTreeMap.floorEntry(key);
        Map.Entry<Float, Integer> high = timeToPositionTreeMap.ceilingEntry(key);
        if (low != null && high != null) {
            return Math.abs(key - low.getKey()) < Math.abs(key - high.getKey())
                    ? low.getValue()
                    : high.getValue();
        } else if (low != null || high != null) {
            return low != null ? low.getValue() : high.getValue();
        }

        return -1;
    }

    private void scrollViewAutoScroll(DragEvent event) {
        Rect scrollBounds = new Rect();
        scrollContainerView.getDrawingRect(scrollBounds);
        float heightOfView = ((View) event.getLocalState()).getHeight();
        float needPositionY_top = event.getY() - heightOfView / 2;
        float needPositionY_bottom = event.getY() + heightOfView / 2;

        if (scrollBounds.top > needPositionY_top) {
            int offsetY = (int) (scrollContainerView.getScrollY() - DensityUtil.dip2px(context, 10));//(needPositionY_top-scrollBounds.top)
            scrollContainerView.scrollTo(scrollContainerView.getScrollX(), offsetY);
        } else if (scrollBounds.bottom < needPositionY_bottom) {
            int offsetY = (int) (scrollContainerView.getScrollY() + DensityUtil.dip2px(context, 10));//(needPositionY_bottom-scrollBounds.bottom)
            scrollContainerView.scrollTo(scrollContainerView.getScrollX(), offsetY);
        }
    }

    private void msgWindowFollow(int tapX, int tapY, View followView) {
        float toX;
        float toY;

        toY = tapY - followView.getHeight() / 2 - msgWindow.getHeight();
        if (tapX + msgWindow.getWidth() / 2 > dividerBgRLayout.getWidth()) {
            toX = dividerBgRLayout.getWidth() - msgWindow.getWidth();
        } else if (tapX - msgWindow.getWidth() / 2 < 0) {
            toX = 0;
        } else {
            toX = tapX - msgWindow.getWidth() / 2;
        }
        int nearestProperPosition = nearestTimeSlotKey(tapY - followView.getHeight() / 2);
        if (nearestProperPosition != -1) {
            Log.i(TAG, "msgWindowFollow: " + positionToTimeTreeMap.get(nearestProperPosition));
            msgWindow.setText(positionToTimeTreeMap.get(nearestProperPosition));
        } else {
            Log.i(TAG, "msgWindowFollow: " + "Error, text not found in Map");
        }

        msgWindow.setTranslationX(toX);
        msgWindow.setTranslationY(toY);
    }

    private long[] changeDateFromString(ITimeEventInterface event, int hour, int minute) {
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
        long[] param = {new_start, new_end};

        return param;
    }


    /***************************
     * Interface
     ******************************************/

    public interface OnBodyTouchListener {
        void bodyOnTouchListener(float tapX, float tapY);
    }


    public void setOnBodyTouchListener(OnBodyTouchListener onBodyTouchListener) {
        this.onBodyTouchListener = onBodyTouchListener;
    }

//    public interface OnEventDragListener {
//        void onEventDragging(DayDraggableEventView eventView, int x, int y);
//        void onEventDragDrop(DayDraggableEventView eventView, int x, int y);
//    }

//    public void setOnEventDragListener(OnEventDragListener onEventDragListener) {
//        this.onEventDragListener = onEventDragListener;
//    }

    public interface OnBodyListener {
        void onEventCreate(DayDraggableEventView eventView);

        void onEventClick(DayDraggableEventView eventView);

        void onEventDragStart(DayDraggableEventView eventView);

        void onEventDragging(DayDraggableEventView eventView, int x, int y);

        void onEventDragDrop(DayDraggableEventView eventView);
    }

    public void setOnBodyListener(OnBodyListener onBodyListener) {
        this.onBodyListener = onBodyListener;
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
            Log.d(TAG, "setEventClassName: " + t.getTitle());
            return t;
        } catch (Exception e) {
            return null;
        }
    }


}
