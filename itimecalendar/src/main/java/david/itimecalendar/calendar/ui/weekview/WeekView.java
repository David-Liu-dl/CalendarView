package david.itimecalendar.calendar.ui.weekview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.developer.paul.itimerecycleviewgroup.ITimeRecycleViewGroup;

import java.util.Calendar;
import java.util.Date;

import david.itimecalendar.R;
import david.itimecalendar.calendar.listeners.ITimeCalendarMonthDayViewListener;
import david.itimecalendar.calendar.listeners.ITimeCalendarWeekDayViewListener;
import david.itimecalendar.calendar.ui.CalendarConfig;
import david.itimecalendar.calendar.ui.monthview.DayViewBody;
import david.itimecalendar.calendar.ui.monthview.DayViewBodyCell;
import david.itimecalendar.calendar.listeners.ITimeEventPackageInterface;
import david.itimecalendar.calendar.util.BaseUtil;
import david.itimecalendar.calendar.util.MyCalendar;

import static android.widget.LinearLayout.VERTICAL;

/**
 * Created by yuhaoliu on 10/05/2017.
 */

public class WeekView extends RelativeLayout{

    protected LinearLayout container;
    private ITimeCalendarWeekDayViewListener iTimeCalendarInterface;
    CalendarConfig calendarConfig = new CalendarConfig();

    public WeekView(@NonNull Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public WeekView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.viewAttrs = attrs;
        this.loadAttributes(attrs, context);
        initView();
        setCalendarConfig(calendarConfig);
    }

    public WeekView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.viewAttrs = attrs;
        this.loadAttributes(attrs, context);
        initView();
        setCalendarConfig(calendarConfig);
    }

    private void loadAttributes(AttributeSet attrs, Context context) {
        if (attrs != null && context != null) {
            TypedArray typedArrayHead = context.getTheme().obtainStyledAttributes(attrs, R.styleable.viewHeader, 0, 0);
            try {
                headerHeight = typedArrayHead.getDimension(R.styleable.viewHeader_headerHeight, headerHeight);
            } finally {
                typedArrayHead.recycle();
            }

            TypedArray typedArrayBody = context.getTheme().obtainStyledAttributes(attrs, R.styleable.viewBody, 0, 0);
            try {
                leftBarWidth = typedArrayBody.getDimension(R.styleable.viewBody_leftBarWidth, leftBarWidth);
                cellHeight = typedArrayBody.getDimension(R.styleable.viewBody_hourHeight, cellHeight);
                NUM_CELL = typedArrayBody.getInteger(R.styleable.viewBody_cellNum, NUM_CELL);
            } finally {
                typedArrayBody.recycle();
            }
        }
    }

    private AttributeSet viewAttrs;
    private FrameLayout dayViewBodyContainer;
    private WeekViewHeaderAdapter headerAdapter;
    protected Context context;
    protected DayViewBody dayViewBody;
    protected ITimeRecycleViewGroup headerRG;

    protected float headerHeight = 45;
    protected float leftBarWidth = 50;
    private float cellHeight = 50;
    private int NUM_CELL = 7;


    private void initView(){
        this.context = getContext();
        container = new LinearLayout(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container.setLayoutParams(params);
        container.setOrientation(VERTICAL);
        this.addView(container);

        this.setUpHeader();
        this.setUpDivider();
        this.setUpBody();
    }

    private void setUpHeader(){
        FrameLayout headerContainer = new FrameLayout(getContext());
        LinearLayout.LayoutParams headerContainerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) headerHeight);
        headerContainer.setLayoutParams(headerContainerParams);
        container.addView(headerContainer);

        View gradientView = new FrameLayout(getContext());
//        gradientView.setBackgroundColor(Color.RED);
        gradientView.setBackground(getResources().getDrawable(R.drawable.icon_calendar_bg_3daybar));
        FrameLayout.LayoutParams gradParams = new FrameLayout.LayoutParams((int)(leftBarWidth + 30), ViewGroup.LayoutParams.MATCH_PARENT);
        gradientView.setLayoutParams(gradParams);
        headerContainer.addView(gradientView);

        headerRG = new ITimeRecycleViewGroup(context, NUM_CELL);
        headerRG.setAllowScroll(false);
        headerRG.setOnSetting(new ITimeRecycleViewGroup.OnSetting() {
            @Override
            public int getItemHeight(int i) {
                int childMaxHeight = MeasureSpec.getSize(i);
                return childMaxHeight;
            }
        });
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.leftMargin = (int)leftBarWidth;
        headerRG.setLayoutParams(params);
        headerAdapter = new WeekViewHeaderAdapter(context);
        headerRG.setAdapter(headerAdapter);
        headerContainer.addView(headerRG);

        gradientView.bringToFront();
    }

    private void setUpDivider(){
        ImageView divider = BaseUtil.getDivider(context, R.drawable.itime_header_divider_line);
        container.addView(divider);
    }

    private void setUpBody(){
        dayViewBodyContainer = new FrameLayout(getContext());
        container.addView(dayViewBodyContainer, new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        dayViewBody = new DayViewBody(context, viewAttrs, calendarConfig);
        dayViewBody.setOnScrollListener(new ITimeRecycleViewGroup.OnScroll<DayViewBodyCell>() {
            @Override
            public void onPageSelected(DayViewBodyCell view) {
                //calling date changed
                if (iTimeCalendarInterface != null){
                    iTimeCalendarInterface.onDateChanged(view.getCalendar().getCalendar().getTime());
                }
            }

            @Override
            public void onHorizontalScroll(int dx, int preOffsetX) {
                headerRG.followScrollByX(dx);
            }

            @Override
            public void onVerticalScroll(int dy, int preOffsetY) {

            }
        });

        FrameLayout.LayoutParams bodyParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.dayViewBodyContainer.addView(dayViewBody, bodyParams);
    }

    public void scrollToDate(Date date, boolean toTime){
        this.headerScrollToDate(date);
        this.dayViewBody.scrollToDate(date,toTime);
    }

    private void headerScrollToDate(Date date){
        MyCalendar currentFstShowDay = ((WeekViewHeaderCell) headerRG.getFirstShowItem()).getCalendar();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int offset = BaseUtil.getDatesDifference(currentFstShowDay.getCalendar().getTimeInMillis(), cal.getTimeInMillis());
        headerRG.moveWithOffsetX(offset);
    }

    public void setEventPackage(ITimeEventPackageInterface eventPackage){
        this.dayViewBody.setEventPackage(eventPackage);
    }

    public void setITimeCalendarWeekDayViewListener(ITimeCalendarWeekDayViewListener weekDayViewListener) {
        this.iTimeCalendarInterface = weekDayViewListener;
        this.dayViewBody.setOnBodyEventListener(weekDayViewListener);
    }

    public void smoothMoveWithOffset(int moveOffset){
        dayViewBody.smoothMoveWithOffset(moveOffset);
    }

    public void refresh(){
        dayViewBody.refresh();
    }

    private void setCalendarConfig(CalendarConfig calendarConfig) {
        this.calendarConfig = calendarConfig;
        this.dayViewBody.setCalendarConfig(calendarConfig);
    }

    public CalendarConfig getCalendarConfig() {
        return calendarConfig;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
