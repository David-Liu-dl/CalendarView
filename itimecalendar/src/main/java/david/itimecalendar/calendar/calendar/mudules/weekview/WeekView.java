package david.itimecalendar.calendar.calendar.mudules.weekview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.developer.paul.itimerecycleviewgroup.ITimeRecycleViewGroup;

import java.util.Calendar;
import java.util.Date;

import david.itimecalendar.R;
import david.itimecalendar.calendar.calendar.mudules.monthview.DayViewBody;
import david.itimecalendar.calendar.calendar.mudules.monthview.DayViewBodyCell;
import david.itimecalendar.calendar.calendar.mudules.monthview.EventController;
import david.itimecalendar.calendar.listeners.ITimeEventPackageInterface;
import david.itimecalendar.calendar.util.BaseUtil;
import david.itimecalendar.calendar.util.MyCalendar;

import static android.widget.LinearLayout.VERTICAL;

/**
 * Created by yuhaoliu on 10/05/2017.
 */

public class WeekView extends RelativeLayout{
    protected LinearLayout container;

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
    }

    public WeekView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.viewAttrs = attrs;
        this.loadAttributes(attrs, context);
        initView();
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

    protected float headerHeight = 200;
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
        headerRG = new ITimeRecycleViewGroup(context, NUM_CELL);
        headerRG.setOnSetting(new ITimeRecycleViewGroup.OnSetting() {
            @Override
            public int getItemHeight(int i) {
                int childMaxHeight = MeasureSpec.getSize(i);
                return childMaxHeight;
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) headerHeight);
        params.leftMargin = (int)leftBarWidth;

        headerRG.setLayoutParams(params);
        headerAdapter = new WeekViewHeaderAdapter(context);
        headerRG.setAdapter(headerAdapter);
//        headerRG.setDisableScroll(true);
        container.addView(headerRG);
    }

    private void setUpDivider(){
        ImageView divider = BaseUtil.getDivider(context, R.drawable.itime_header_divider_line);
        container.addView(divider);
    }

    private void setUpBody(){
        dayViewBodyContainer = new FrameLayout(getContext());
        container.addView(dayViewBodyContainer, new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        dayViewBody = new DayViewBody(context, viewAttrs);
        dayViewBody.setOnScrollListener(new ITimeRecycleViewGroup.OnScroll<DayViewBodyCell>() {
            @Override
            public void onPageSelected(DayViewBodyCell view) {

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

//    public void setScrollInterface(ITimeRecycleViewGroup.ScrollInterface scrollInterface){
//        dayViewBody.setScrollInterface(scrollInterface);
//    }

    public void scrollToDate(Date date){
        this.headerScrollToDate(date);
        dayViewBody.scrollToDate(date);
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

    public void setOnBodyListener(EventController.OnEventListener onEventListener) {
        this.dayViewBody.setOnBodyListener(onEventListener);
    }

    public void smoothMoveWithOffset(int moveOffset){
        dayViewBody.smoothMoveWithOffset(moveOffset);
    }

    public void refresh(){
        dayViewBody.refresh();
    }


//    public void setDisableCellScroll(boolean isDisabled){
//        headerRG.setDisableCellScroll(isDisabled);
//        dayViewBody.setDisableCellScroll(isDisabled);
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
