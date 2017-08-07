package david.itimecalendar.calendar.ui.monthview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.developer.paul.itimerecycleviewgroup.ITimeRecycleViewGroup;

import java.util.Calendar;
import java.util.Date;

import david.itimecalendar.R;
import david.itimecalendar.calendar.listeners.ITimeCalendarMonthDayViewListener;
import david.itimecalendar.calendar.listeners.ITimeEventPackageInterface;
import david.itimecalendar.calendar.util.BaseUtil;
import david.itimecalendar.calendar.util.MyCalendar;

/**
 * Created by yuhaoliu on 10/05/2017.
 */

public class MonthView extends LinearLayout{
    private ITimeCalendarMonthDayViewListener iTimeCalendarInterface;

    private static final String TAG = "lifecycle";
    private Context context;

    private ITimeEventPackageInterface eventPackage;

    private RecyclerView headerRecyclerView;
    private DayViewHeaderRecyclerAdapter headerRecyclerAdapter;

    private FrameLayout dayViewBodyContainer;
    private DayViewBody dayViewBody;

    private LinearLayoutManager headerLinearLayoutManager;
    private int upperBoundsOffset;

    private final DisplayMetrics dm = getResources().getDisplayMetrics();
    private int headerCollapsedHeight;
    private int headerExpandedHeight;

    private AttributeSet viewAttrs;

    public MonthView(Context context) {
        super(context);
        initView();
    }

    public MonthView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.viewAttrs = attrs;
        initView();
    }

    private void initView(){
        this.context = getContext();
        this.setOrientation(VERTICAL);
        this.upperBoundsOffset = 5000;

        this.setUpHeader();
        this.setUpDivider();
        this.setUpBody();
    }

    private void setUpHeader(){
        LayoutInflater inflater = LayoutInflater.from(context);
        final View dayOfWeek = inflater.inflate(R.layout.day_of_week, null, false);
        this.addView(dayOfWeek);

        headerRecyclerView = new RecyclerView(context);
        headerRecyclerAdapter = new DayViewHeaderRecyclerAdapter(context, upperBoundsOffset);

        headerRecyclerAdapter.setOnCheckIfHasEvent(new DayViewHeader.OnCheckIfHasEvent() {
            @Override
            public boolean todayHasEvent(long startOfDay) {
                boolean hasRegular = eventPackage.getRegularEventDayMap().containsKey(startOfDay) && (eventPackage.getRegularEventDayMap().get(startOfDay).size() != 0);
                boolean hasRepeated = eventPackage.getRepeatedEventDayMap().containsKey(startOfDay) && (eventPackage.getRepeatedEventDayMap().get(startOfDay).size() != 0);

                return hasRegular || hasRepeated;
            }
        });
        headerRecyclerAdapter.setOnHeaderListener(new DayViewHeaderRecyclerAdapter.OnHeaderListener() {
            @Override
            public void onClick(MyCalendar myCalendar) {

            }

            @Override
            public void onDateSelected(Date date) {
                if (dayViewBody != null){
                    dayViewBody.scrollToDate(date);
                }
            }

            @Override
            public void onHeaderFlingDateChanged(Date newestDate) {
                if (iTimeCalendarInterface != null){
                    iTimeCalendarInterface.onHeaderFlingDateChanged(newestDate);
                }
            }
        });
        headerRecyclerView.setHasFixedSize(true);
        headerRecyclerView.setAdapter(headerRecyclerAdapter);
        headerLinearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        headerRecyclerView.setLayoutManager(headerLinearLayoutManager);
//        headerRecyclerView.addItemDecoration(new DayViewHeaderRecyclerDivider(context));
        headerCollapsedHeight = (dm.widthPixels / 7 - 20) * 2;
        headerExpandedHeight = (dm.widthPixels / 7 - 20) * 4;

        ViewGroup.LayoutParams headerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, headerCollapsedHeight);
        headerRecyclerView.setLayoutParams(headerParams);
        headerRecyclerView.scrollToPosition(upperBoundsOffset);
        this.headerRecyclerView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                expandHeader();
                return false;
            }
        });
        this.addView(headerRecyclerView);
    }

    private void setUpDivider(){
        ImageView divider = BaseUtil.getDivider(context, R.drawable.itime_header_divider_line);

        this.addView(divider);
    }

    private void setUpBody(){
        dayViewBodyContainer = new FrameLayout(getContext()){
            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return headerStatus == HeaderStatus.EXPANDED || super.onInterceptTouchEvent(ev);
            }
        };
        dayViewBodyContainer.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                collapseHeader(null);
                return false;
            }
        });
        this.addView(dayViewBodyContainer, new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        dayViewBody = new DayViewBody(context, viewAttrs);
        dayViewBody.setOnScrollListener(new ITimeRecycleViewGroup.OnScroll<DayViewBodyCell>() {
            @Override
            public void onPageSelected(DayViewBodyCell v) {
                MyCalendar fstItemDate = v.getCalendar();
                headerScrollToDate(fstItemDate.getCalendar(), false);
                //calling date changed
                if (iTimeCalendarInterface != null){
                    iTimeCalendarInterface.onDateChanged(fstItemDate.getCalendar().getTime());
                }
            }

            @Override
            public void onHorizontalScroll(int dx, int preOffsetX) {

            }

            @Override
            public void onVerticalScroll(int dy, int preOffsetY) {

            }

        });

        FrameLayout.LayoutParams bodyParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.dayViewBodyContainer.addView(dayViewBody, bodyParams);
    }

    private void headerScrollToDate(final Calendar body_fst_cal, final boolean toTime){
        DayViewHeader headerView =
                (DayViewHeader) headerLinearLayoutManager.findViewByPosition(headerRecyclerAdapter.rowPst);

        if (headerView != null){
            MyCalendar tempH = new MyCalendar(headerView.getCalendar());
            tempH.setHour(0);
            MyCalendar tempB = new MyCalendar(body_fst_cal);
            tempB.setHour(0);

            tempH.setOffsetByDate(headerRecyclerAdapter.indexInRow);

            int date_offset = Math.round((float)(tempB.getCalendar().getTimeInMillis() - tempH.getCalendar().getTimeInMillis()) / (float)(1000*60*60*24));
            int row_diff = date_offset/7;
            int day_diff = ((headerRecyclerAdapter.indexInRow+1) + date_offset%7);

            if (date_offset > 0){
                row_diff = row_diff + (day_diff > 7 ? 1:0);
                day_diff = day_diff > 7 ? day_diff%7 : day_diff;
            }else if(date_offset < 0){
                row_diff = row_diff + (day_diff <= 0 ? -1:0);
                day_diff = day_diff <= 0 ? (7 + day_diff):day_diff;
            }

            if ((row_diff != 0 || day_diff != 0)){
                if (row_diff != 0){
                    int newRowPst = row_diff + headerRecyclerAdapter.rowPst;
                    headerRecyclerView.stopScroll();
                    headerRecyclerView.scrollToPosition(newRowPst);
                    headerRecyclerAdapter.rowPst = newRowPst;
                }
                if (day_diff != 0){
                    int new_index = day_diff - 1;
                    headerRecyclerAdapter.indexInRow = new_index;
                }
            }
            headerRecyclerAdapter.notifyDataSetChanged();
        }else {
            headerRecyclerView.stopScroll();
            headerLinearLayoutManager.scrollToPosition(headerRecyclerAdapter.rowPst);
//            headerRecyclerView.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    headerScrollToDate(body_fst_cal,toTime);
//                }
//            },10);
        }
    }

    private ValueAnimator vaCollapse;
    private ValueAnimator vaExpand;
    private enum HeaderStatus {
        EXPANDED, COLLAPSED
    }
    private HeaderStatus headerStatus = HeaderStatus.COLLAPSED;

    public void scrollToDate(final Date date){
        collapseHeader(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                headerScrollToDate(calendar,false);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    private void collapseHeader(Animator.AnimatorListener callback){
        headerRecyclerView.stopScroll();
        headerLinearLayoutManager.scrollToPositionWithOffset(headerRecyclerAdapter.getCurrentSelectPst(), 0);

        final View view = headerRecyclerView;
        vaCollapse = ValueAnimator.ofInt(view.getHeight(), headerCollapsedHeight);
        vaCollapse.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                view.getLayoutParams().height = value.intValue();
                view.requestLayout();
            }
        });

        if(callback != null){
            vaCollapse.addListener(callback);
        }

        vaCollapse.setDuration(200);

        if(headerStatus != HeaderStatus.COLLAPSED){
            headerStatus = HeaderStatus.COLLAPSED;
            vaCollapse.start();
        }else {
            vaCollapse.end();
        }
    }

    private void expandHeader(){
        final View view = headerRecyclerView;
        vaExpand = ValueAnimator.ofInt(view.getHeight(), headerExpandedHeight);
        vaExpand.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                view.getLayoutParams().height = value.intValue();
                view.requestLayout();
            }
        });
        vaExpand.setDuration(200);

        if (headerStatus != HeaderStatus.EXPANDED){
            headerStatus = HeaderStatus.EXPANDED;
            vaExpand.start();
        }
    }

    public void setEventPackage(ITimeEventPackageInterface eventPackage){
        this.eventPackage = eventPackage;
        this.dayViewBody.setEventPackage(eventPackage);
    }

    public void setITimeCalendarMonthDayViewListener(ITimeCalendarMonthDayViewListener monthDayViewListener) {
        this.iTimeCalendarInterface = monthDayViewListener;
        this.dayViewBody.setOnBodyEventListener(monthDayViewListener);
    }

    public void smoothMoveWithOffset(int moveOffset){
        dayViewBody.smoothMoveWithOffset(moveOffset);
    }

    public void refresh(){
        dayViewBody.refresh();
        headerRecyclerAdapter.notifyDataSetChanged();
    }
}
