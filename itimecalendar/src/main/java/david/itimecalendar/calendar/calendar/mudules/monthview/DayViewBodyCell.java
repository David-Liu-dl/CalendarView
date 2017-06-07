package david.itimecalendar.calendar.calendar.mudules.monthview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

import david.itimecalendar.R;
import david.itimecalendar.calendar.listeners.ITimeEventPackageInterface;
import david.itimecalendar.calendar.util.DensityUtil;
import david.itimecalendar.calendar.util.MyCalendar;
import david.itimecalendar.calendar.util.OverlapHelper;
import david.itimecalendar.calendar.wrapper.WrapperTimeSlot;

/**
 * Created by yuhaoliu on 10/05/2017.
 */

public class DayViewBodyCell extends FrameLayout{
    public final String TAG = "DayViewBodyCell";
    //FOR item inner type
    public static final int UNDEFINED = -1;
    public static final int REGULAR = 0;
    public static final int DAY_CROSS_BEGIN = 1;
    public static final int DAY_CROSS_ALL_DAY = 2;
    public static final int DAY_CROSS_END = 3;
    /**
     * Color category
     */
    /*************************** Start of Color Setting **********************************/
    private int color_allday_bg = Color.parseColor("#EBEBEB");
    private int color_allday_title = R.color.black;
    private int color_bg_day_even = R.color.color_f2f2f5;
    private int color_bg_day_odd = R.color.color_75white;
    private int color_msg_window_text = R.color.text_enable;
    private int color_time_text = R.color.text_enable;
    private int color_nowtime = R.color.text_today_color;
    private int color_nowtime_bg = R.color.whites;
    private int color_vertical_dct = R.color.divider_calbg;
    private int color_vertical_page_dct = R.color.divider_nav;
    /*************************** End of Color Setting **********************************/

    /*************************** Start of Resources Setting ****************************/
    private int rs_divider_line = R.drawable.itime_day_view_dotted;
    private int rs_vertical_line = R.drawable.itime_day_view_decoration_line;
    private int rs_nowtime_line = R.drawable.itime_now_time_full_line;
    /*************************** End of Resources Setting ****************************/

    protected boolean isTimeSlotEnable = false;
    protected boolean isRemoveOptListener = false;
    protected OverlapHelper xHelper = new OverlapHelper();

    FrameLayout dividerBgRLayout;
    DayInnerBodyLayout eventLayout;

    public MyCalendar myCalendar = new MyCalendar(Calendar.getInstance());
    protected Context context;

    protected TreeMap<Integer, String> positionToTimeTreeMap = new TreeMap<>();
    protected TreeMap<Integer, String> positionToTimeQuarterTreeMap = new TreeMap<>();
    protected TreeMap<Float, Integer> timeToPositionTreeMap = new TreeMap<>();

    protected SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    private TextView nowTime;
    private ImageView nowTimeLine;

    //tag: false-> moving, true, done
    protected View tempDragView = null;
    private View dctView;
    //dp
    protected int hourHeight = 30;
    private int spaceTop = 30;

    //dp
    protected int unitViewLeftMargin = 3;
    protected int unitViewRightMargin = 0;

    private int timeTextSize = 20;
    protected int topAllDayHeight;

    protected int layoutWidthPerDay;
    protected int layoutHeightPerDay;


    protected float nowTapX = 0;
    protected float nowTapY = 0;

    protected float heightPerMillisd = 0;

    final Handler uiHandler= new Handler();
    private Thread uiUpdateThread;

    private TimeSlotController timeSlotController = new TimeSlotController(this);
    private EventController eventController = new EventController(this);

    public DayViewBodyCell(@NonNull Context context) {
        super(context);
        init();
    }

    public DayViewBodyCell(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.loadAttributes(attrs,context);
        init();
    }

    public DayViewBodyCell(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.loadAttributes(attrs,context);
        init();
    }

    private void loadAttributes(AttributeSet attrs, Context context) {
        if (attrs != null && context != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.viewBody, 0, 0);
            try {
                spaceTop = (int) typedArray.getDimension(R.styleable.viewBody_topSpace, spaceTop);
                hourHeight = (int) typedArray.getDimension(R.styleable.viewBody_hourHeight, hourHeight);
            } finally {
                typedArray.recycle();
            }
        }
    }

    private void init(){
        this.context = getContext();
        initLayoutParams();
        initTimeSlot();
        initView();
    }

    private void initLayoutParams(){
        this.heightPerMillisd = (float) hourHeight /(3600*1000);
        this.unitViewLeftMargin = DensityUtil.dip2px(context,this.unitViewLeftMargin);
        this.unitViewRightMargin = DensityUtil.dip2px(context,this.unitViewRightMargin);
    }

    private void initView(){
        initBgView();
        initContentView();
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent item) {
//        boolean value = super.onTouchEvent(item);
//        Log.i(TAG, "DayViewBodyCell onTouchEvent:Action: " + item.getAction() + " R:" + value);
//        return super.onTouchEvent(item);
//    }
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        boolean value = super.onInterceptTouchEvent(ev);
//        Log.i(TAG, "onInterceptTouchEvent: " + value);
//        return super.onInterceptTouchEvent(ev);
//    }

    private void initContentView(){
        eventLayout = new DayInnerBodyLayout(context);
//        eventLayout.setBackgroundColor(getResources().getColor(displayLen == 1 ? color_bg_day_odd : (i%2 == 0 ? color_bg_day_even : color_bg_day_odd)));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        this.addView(eventLayout,params);

        eventLayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                nowTapX = event.getX();
                nowTapY = event.getY();
                return false;
            }
        });

        if (!isTimeSlotEnable){
            eventLayout.setOnDragListener(eventController.new EventDragListener());
            eventLayout.setOnLongClickListener(eventController.new CreateEventListener());
        }else {
            eventLayout.setOnDragListener(this.timeSlotController.new TimeSlotDragListener());
            eventLayout.setOnLongClickListener(this.timeSlotController.new CreateTimeSlotListener());
        }
    }

    private void initBgView(){
        initDividerLine(getHours());
//        initBubbleView();
    }


    private void initTimeSlot() {
        double startPoint = spaceTop;
        double minuteHeight = hourHeight / 60;
        String[] hours = getHours();
        for (int slot = 0; slot < hours.length; slot++) {
            //add full clock
            //for single minute map
            positionToTimeTreeMap.put((int) startPoint + hourHeight * slot, hours[slot] + ":00");
            //for quarter map
            positionToTimeQuarterTreeMap.put((int) startPoint + hourHeight * slot, hours[slot] + ":00");
            String hourPart = hours[slot].substring(0, 2); // XX
            timeToPositionTreeMap.put((float) Integer.valueOf(hourPart), (int) startPoint + hourHeight * slot);
            //if not 24, add minutes
            if (slot != hours.length - 1){
                for (int miniSlot = 0; miniSlot < 59; miniSlot++) {
                    String minutes = String.format("%02d", miniSlot + 1);
                    String time = hourPart + ":" + minutes;
                    int positionY = (int) (startPoint + hourHeight * slot + minuteHeight * (miniSlot + 1));
                    positionToTimeTreeMap.put(positionY, time);
                    timeToPositionTreeMap.put(Integer.valueOf(hourPart) + (float) Integer.valueOf(minutes) / 100, positionY);
                    //for quarter map
                    if ((miniSlot + 1) % 15 == 0){
                        positionToTimeQuarterTreeMap.put(positionY, time);
                    }
                }
            }
        }
    }

    private void initDividerLine(String[] HOURS) {
        dividerBgRLayout = new FrameLayout(getContext());
        dividerBgRLayout.setId(View.generateViewId());
        FrameLayout.LayoutParams dividerBgRLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(dividerBgRLayout, dividerBgRLayoutParams);

        //add divider
        for (int numOfLine = 0; numOfLine < HOURS.length; numOfLine++) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            ImageView dividerImageView = new ImageView(context);
            dividerImageView.setImageResource(rs_divider_line);
            params.topMargin = this.nearestTimeSlotValue(numOfLine);
            dividerImageView.setLayoutParams(params);
            dividerImageView.setPadding(0, 0, 0, 0);
            dividerBgRLayout.addView(dividerImageView);
        }

        //add right side decoration
        FrameLayout.LayoutParams dctParams = new FrameLayout.LayoutParams(DensityUtil.dip2px(context,1), ViewGroup.LayoutParams.MATCH_PARENT);
        dctParams.gravity = Gravity.END;
        dctParams.topMargin = spaceTop;
        dctView = new View(context);
        dctView.setBackgroundColor(getResources().getColor(color_vertical_dct));
        dctView.setLayoutParams(dctParams);
        dctView.setPadding(0, 0, 0, 0);
        dividerBgRLayout.addView(dctView);
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

    public void setEventList(ITimeEventPackageInterface eventPackage) {
        this.eventController.setEventList(eventPackage);
    }

    public void highlightCellBorder(){
        if (dctView != null){
            dctView.setBackgroundColor(getResources().getColor(color_vertical_page_dct));
            dctView.invalidate();
        }
    }

    public void resetCellBorder(){
        if (dctView != null){
            dctView.setBackgroundColor(getResources().getColor(color_vertical_dct));
            dctView.invalidate();
        }
    }

    public MyCalendar getCalendar() {
        return myCalendar;
    }

    public void setCalendar(MyCalendar myCalendar) {
        this.myCalendar = myCalendar;
    }

    protected int[] reComputePositionToSet(int actualX, int actualY, View draggableObj, View container) {
        int containerWidth = container.getWidth();
        int containerHeight = container.getHeight();
        int objWidth = draggableObj.getWidth();
        int objHeight = draggableObj.getHeight();

        int finalX = (int) (timeTextSize * 1.5);
        int finalY = actualY;

        if (actualY < 0) {
            finalY = 0;
        } else if (actualY > containerHeight) {
            finalY = containerHeight;
        }
        int findNearestPosition = nearestQuarterTimeSlotKey(finalY);
        if (findNearestPosition != -1) {
            finalY = findNearestPosition;
        } else {
            Log.i(TAG, "reComputePositionToSet: " + "ERROR NO SUCH POSITION");
        }

        return new int[]{finalX, finalY};
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // only support width-match parent | height-wrap-content
        final int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        final int heightResult = getViewHeight();

        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec( widthMeasureSpec, MeasureSpec.EXACTLY );
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec( heightResult, MeasureSpec.EXACTLY );
        measureChildren(childWidthMeasureSpec, childHeightMeasureSpec);

        setMeasuredDimension(widthSize, heightResult);
    }

    protected void measureChildWithMargins(View child,
                                           int parentWidthMeasureSpec, int widthUsed,
                                           int parentHeightMeasureSpec, int heightUsed) {
        int mPaddingLeft = getPaddingLeft();
        int mPaddingRight = getPaddingRight();
        int mPaddingTop = getPaddingTop();
        int mPaddingBottom = getPaddingBottom();

        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                mPaddingLeft + mPaddingRight + lp.leftMargin + lp.rightMargin
                        + widthUsed, lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                mPaddingTop + mPaddingBottom + lp.topMargin + lp.bottomMargin
                        + heightUsed, lp.height);
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    private int getViewHeight(){
        int timeHeight = hourHeight * 25;
        int topSpaceHeight = getPaddingTop();
        int bottomSpaceHeight = getPaddingBottom();
        return timeHeight + topSpaceHeight + bottomSpaceHeight;
    }

    public void setOnBodyListener(EventController.OnEventListener onEventListener) {
        this.eventController.setOnEventListener(onEventListener);
    }

    public void resetView() {
        eventController.clearEvents();
        timeSlotController.clearTimeslots();
    }

    /**
     *
     * @param positionY
     * @return
     */
    private int nearestQuarterTimeSlotKey(int positionY) {
        int key = positionY;
        Map.Entry<Integer, String> low = positionToTimeQuarterTreeMap.floorEntry(key);
        Map.Entry<Integer, String> high = positionToTimeQuarterTreeMap.ceilingEntry(key);
        if (low != null && high != null) {
            return Math.abs(key - low.getKey()) < Math.abs(key - high.getKey())
                    ? low.getKey()
                    : high.getKey();
        } else if (low != null || high != null) {
            return low != null ? low.getKey() : high.getKey();
        }

        return -1;
    }

    /**
     *
     * @param time
     * @return nearest position
     */
    protected int nearestTimeSlotValue(float time) {
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

    /*** for time slot ***/
    public void clearTimeSlots(){
        timeSlotController.clearTimeslots();
    }

    public void resetTimeSlotViews(){
        timeSlotController.clearTimeslots();
    }

    public void addSlot(WrapperTimeSlot wrapper, boolean animate){
        timeSlotController.addSlot(wrapper,animate);
    }

    public void addRcdSlot(WrapperTimeSlot wrapper){
        timeSlotController.addRecommended(wrapper);
    }

    public void updateTimeSlotsDuration(long duration, boolean animate){
        timeSlotController.updateTimeSlotsDuration(duration, animate);
    }

    public void enableTimeSlot(){
        eventController.enableBgMode();
        timeSlotController.enableTimeSlot();
    }

    public void setOnTimeSlotListener(TimeSlotController.OnTimeSlotListener onTimeSlotListener) {
        timeSlotController.setOnTimeSlotListener(onTimeSlotListener);
    }

}
