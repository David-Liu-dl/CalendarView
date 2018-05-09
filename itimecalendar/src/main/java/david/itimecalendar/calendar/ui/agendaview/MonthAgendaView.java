package david.itimecalendar.calendar.ui.agendaview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.Calendar;
import java.util.Date;

import david.itimecalendar.R;
import david.itimecalendar.calendar.listeners.ITimeCalendarMonthAgendaViewListener;
import david.itimecalendar.calendar.ui.CalendarConfig;
import david.itimecalendar.calendar.ui.monthview.DayViewHeader;
import david.itimecalendar.calendar.listeners.ITimeEventInterface;
import david.itimecalendar.calendar.listeners.ITimeEventPackageInterface;
import david.itimecalendar.calendar.util.MyCalendar;

/**
 * Created by David Liu on 31/08/16.
 * ${COMPANY}
 * lyhmelbourne@gmail.com
 */

public class MonthAgendaView extends RelativeLayout{
    private ITimeCalendarMonthAgendaViewListener iTimeCalendarInterface;
    private int upperBoundsOffset = 1;
    private int init_height;
    private int scroll_height;

    private LinearLayout container;
    private RelativeLayout agendaViewBodyContainer;

    private AgendaBodyHeader bodyFloatHeader;
    private RecyclerView agendaViewHeader;
    private AgendaBodyRecyclerView agendaViewBody;

    private LinearLayoutManager headerLinearLayoutManager;
    private LinearLayoutManager bodyLinearLayoutManager;

    private AgendaHeaderViewRecyclerAdapter headerRecyclerAdapter;
    private AgendaBodyViewRecyclerAdapter bodyRecyclerAdapter;

    private MyCalendar monthAgendaViewCalendar;
    private ITimeEventPackageInterface eventPackage;
    private Context context;
    private CalendarConfig config = new CalendarConfig();

    public MonthAgendaView(Context context) {
        super(context);
        this.context = context;
        initView();
        setCalendarConfig(config);
    }

    public MonthAgendaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
        setCalendarConfig(config);
    }

    public MonthAgendaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    /***************************************************************************
     * Public methods Part, providing the methods of controlling for MonthDayView
     ***************************************************************************/

    /**
     * Set the item data package
     * @param eventPackage
     */
    public void setEventPackage(ITimeEventPackageInterface eventPackage){
        this.eventPackage = eventPackage;
        this.bodyRecyclerAdapter.setDayEventMap(eventPackage);
        this.headerRecyclerAdapter.notifyDataSetChanged();
        this.bodyRecyclerAdapter.notifyDataSetChanged();
    }

    public void refresh(){
        headerRecyclerAdapter.notifyDataSetChanged();
        bodyRecyclerAdapter.notifyDataSetChanged();
    }

    /**
     * scroll to date of today.
     */
    public void backToToday(){
        this.agendaViewHeader.stopScroll();
        this.agendaViewBody.stopScroll();
        if (agendaViewHeader.getHeight() != init_height){
            collapseHeader(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    headerScrollToDate(Calendar.getInstance());
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }else{
            headerScrollToDate(Calendar.getInstance());
        }
        this.bodyLinearLayoutManager.scrollToPosition(0);
    }

    public void scrollToDate(final Date date){
        collapseHeader(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                final Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                if (agendaViewHeader.getHeight() == 0){
                    ViewTreeObserver vto = agendaViewHeader.getViewTreeObserver();
                    vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            agendaViewHeader.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            agendaViewHeader.stopScroll();
                            agendaViewBody.stopScroll();
                            headerScrollToDate(calendar);
                        }
                    });
                }else{
                    agendaViewBody.stopScroll();
                    headerScrollToDate(calendar);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * hide the head part of MonthDayView
     */
    public void hideHewader(){
        if (this.agendaViewHeader != null){
            this.agendaViewHeader.setVisibility(View.GONE);
        }
    }

    /***************************************************************************
     * Inner private methods block, including function of setting up MonthDayView
     ***************************************************************************/

    private void initView(){
        this.context = getContext();
        this.container = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.itime_month_agenda_view, null);
        this.addView(container);

        agendaViewHeader = (RecyclerView) container.findViewById(R.id.headerRowList);
        agendaViewBodyContainer = (RelativeLayout) container.findViewById(R.id.bodyRL);
        agendaViewBody = (AgendaBodyRecyclerView) container.findViewById(R.id.bodyRowList);

        upperBoundsOffset = 10000;

        this.setUpHeader();
        this.setUpBody();
        this.setUpFloatHeader();
    }

    private void setUpHeader(){
        headerRecyclerAdapter = new AgendaHeaderViewRecyclerAdapter(context, upperBoundsOffset);
        headerRecyclerAdapter.setOnHeaderListener(new AgendaHeaderViewRecyclerAdapter.OnHeaderListener() {
            @Override
            public void onClick(MyCalendar myCalendar) {
                if (iTimeCalendarInterface != null){
                    iTimeCalendarInterface.onDateChanged(myCalendar.getCalendar().getTime());
                }
            }

            @Override
            public void onDateSelected(Date date) {
                if (agendaViewBody.getScrollState() == 0){
                    bodyLinearLayoutManager.scrollToPositionWithOffset(headerRecyclerAdapter.getCurrentDayOffset(), 0);
                }
            }

            @Override
            public void onHeaderFlingDateChanged(Date newestDate) {
                if (iTimeCalendarInterface != null){
                    iTimeCalendarInterface.onHeaderFlingDateChanged(newestDate);
                }
            }
        });
        headerRecyclerAdapter.setOnCheckIfHasEvent(new DayViewHeader.OnCheckIfHasEvent() {

            @Override
            public boolean todayHasEvent(long startOfDay) {
                boolean hasRegular = eventPackage.getRegularEventDayMap().containsKey(startOfDay) && (eventPackage.getRegularEventDayMap().get(startOfDay).size() != 0);
                boolean hasRepeated = eventPackage.getRepeatedEventDayMap().containsKey(startOfDay) && (eventPackage.getRepeatedEventDayMap().get(startOfDay).size() != 0);
                return hasRegular || hasRepeated;
            }
        });
        agendaViewHeader.setHasFixedSize(true);
        agendaViewHeader.setAdapter(headerRecyclerAdapter);
        headerLinearLayoutManager = new LinearLayoutManager(context);
        agendaViewHeader.setLayoutManager(headerLinearLayoutManager);
        final DisplayMetrics dm = getResources().getDisplayMetrics();
        init_height = (dm.widthPixels / 7 - 20) * 2;
        scroll_height = (dm.widthPixels / 7 - 20) * 4;

        ViewGroup.LayoutParams recycler_layoutParams = agendaViewHeader.getLayoutParams();
        recycler_layoutParams.height = init_height;
        agendaViewHeader.setLayoutParams(recycler_layoutParams);
        agendaViewHeader.addOnScrollListener(new HeaderOnScrollListener());
        agendaViewHeader.setLayoutParams(recycler_layoutParams);
        agendaViewHeader.stopScroll();
        agendaViewHeader.scrollToPosition(upperBoundsOffset);
    }

    private void setUpBody(){
        bodyRecyclerAdapter = new AgendaBodyViewRecyclerAdapter(context, upperBoundsOffset);
        setITimeCalendarMonthAgendaViewListener(this.iTimeCalendarInterface);
        agendaViewBody.setFlingScale(0.6f);
        agendaViewBody.setHasFixedSize(false);
        agendaViewBody.setAdapter(bodyRecyclerAdapter);
        bodyLinearLayoutManager = new LinearLayoutManager(context);
        agendaViewBody.setLayoutManager(bodyLinearLayoutManager);
        agendaViewBody.addOnScrollListener(new BodyOnScrollListener());

        ViewGroup.LayoutParams recycler_layoutParams = agendaViewBody.getLayoutParams();
        recycler_layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        recycler_layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        agendaViewBody.setLayoutParams(recycler_layoutParams);
        agendaViewBody.stopScroll();
        agendaViewBody.scrollToPosition(upperBoundsOffset);
    }

    private void setUpFloatHeader(){
        bodyFloatHeader = new AgendaBodyHeader(context);
        bodyFloatHeader.updateHeaderView();
        agendaViewBodyContainer.addView(bodyFloatHeader);
    }

    private void collapseHeader(Animator.AnimatorListener callback){
        agendaViewHeader.stopScroll();
        headerLinearLayoutManager.scrollToPositionWithOffset(headerRecyclerAdapter.rowPst,0);
        final View view = agendaViewHeader;
        ValueAnimator va = ValueAnimator.ofInt(view.getHeight(), init_height);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                view.getLayoutParams().height = value.intValue();
                view.requestLayout();
            }
        });

        if(callback != null){
            va.addListener(callback);
        }

        va.setDuration(200);
        va.start();
    }

    private void expandHeader(){
        final View view = agendaViewHeader;
        ValueAnimator va = ValueAnimator.ofInt(view.getHeight(), scroll_height);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                view.getLayoutParams().height = value.intValue();
                view.requestLayout();
            }
        });
        va.setDuration(200);
        va.start();
    }

    private boolean isWithin(ITimeEventInterface event, long dayOfBegin, int index){
        long startTime = event.getStartTime();
        long endTime = event.getEndTime();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dayOfBegin);

        MyCalendar calS = new MyCalendar(calendar);
        calS.setOffsetByDate(index);

        MyCalendar calE = new MyCalendar(calendar);
        calE.setOffsetByDate(index);
        calE.setHour(23);
        calE.setMinute(59);

        long todayStartTime =  calS.getBeginOfDayMilliseconds();
        long todayEndTime =  calE.getCalendar().getTimeInMillis();

        return
                todayEndTime >= startTime && todayStartTime <= endTime;
    }

    private void headerScrollToDate(Calendar body_fst_cal){
        DayViewHeader headerView =
                (DayViewHeader) headerLinearLayoutManager.findViewByPosition(headerRecyclerAdapter.rowPst);

        if (headerView != null){
            MyCalendar tempH = new MyCalendar(headerView.getCalendar());
            tempH.setHour(0);
            MyCalendar tempB = new MyCalendar(body_fst_cal);
            tempB.setHour(0);
            tempH.setOffsetByDate(headerRecyclerAdapter.indexInRow);

            int date_offset =  Math.round((float)(tempB.getCalendar().getTimeInMillis() - tempH.getCalendar().getTimeInMillis()) / (float)(1000*60*60*24));
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
                    headerLinearLayoutManager.scrollToPosition(newRowPst);
                    headerRecyclerAdapter.rowPst = newRowPst;
                }
                if (day_diff != 0){
                    // update selected index when onBindViewHolder is called
                    final int new_index = day_diff - 1;
                    headerRecyclerAdapter.indexInRow = new_index;
                }
            }

            headerRecyclerAdapter.notifyDataSetChanged();
        }else {
            agendaViewHeader.stopScroll();
            agendaViewHeader.scrollToPosition(headerRecyclerAdapter.rowPst);
        }
    }

    /***************************************************************************
     * Listener Part, Including onHeaderListener, OnBodyOuterListener
     ***************************************************************************/

    private class HeaderOnScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView v, int newState) {
            super.onScrollStateChanged(agendaViewHeader, newState);
            if (newState == 1){
                if (v.getHeight() == init_height){
                    final View view = v;
                    ValueAnimator va = ValueAnimator.ofInt(init_height, scroll_height);
                    va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator animation) {
                            Integer value = (Integer) animation.getAnimatedValue();
                            view.getLayoutParams().height = value.intValue();
                            view.requestLayout();
                        }
                    });
                    va.setDuration(200);
                    va.start();
                }
            }

            //for now header date
            int index = headerLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
            DayViewHeader fstVisibleHeader = (DayViewHeader) headerLinearLayoutManager.findViewByPosition(index);
            monthAgendaViewCalendar = fstVisibleHeader.getCalendar();
            if (iTimeCalendarInterface != null){
                iTimeCalendarInterface.onDateChanged(monthAgendaViewCalendar.getCalendar().getTime());
            }
        }
    }

    private class BodyOnScrollListener extends RecyclerView.OnScrollListener{
        private boolean slideByUser = false;
        private int last_pst = upperBoundsOffset;

        @Override
        public void onScrolled(RecyclerView v, int dx, int dy) {
            super.onScrolled(v, dx, dy);
            int fst_visible_pst = bodyLinearLayoutManager.findFirstVisibleItemPosition();
            if ((fst_visible_pst != last_pst) && (fst_visible_pst != -1)) {
                MyCalendar bodyMyCalendar = ((AgendaViewBody) bodyLinearLayoutManager.findViewByPosition(fst_visible_pst)).getCalendar();
                Calendar body_fst_cal = bodyMyCalendar.getCalendar();

                //update bodyFloatHeader
                bodyFloatHeader.setMyCalendar(bodyMyCalendar);
                bodyFloatHeader.updateHeaderView();
                bodyFloatHeader.setTranslationY(0);

                //update header
                if (slideByUser){
                    headerScrollToDate(body_fst_cal);
                }
                last_pst = fst_visible_pst;
            }

            if (slideByUser && (fst_visible_pst != -1)){
                AgendaViewBody body = ((AgendaViewBody) bodyLinearLayoutManager.findViewByPosition(fst_visible_pst));
                if (body.getBottom() <= bodyFloatHeader.getHeight()){
                    bodyFloatHeader.setTranslationY(body.getBottom() - bodyFloatHeader.getHeight());
                }else {
                    bodyFloatHeader.setTranslationY(0);
                }
                last_pst = fst_visible_pst;
            }

        }

        @Override
        public void onScrollStateChanged(RecyclerView v, int newState) {
            super.onScrollStateChanged(v, newState);
            //update header height
            final View needChangeView = agendaViewHeader;
            if (needChangeView.getHeight() == scroll_height){
                agendaViewHeader.stopScroll();
                headerLinearLayoutManager.scrollToPositionWithOffset(headerRecyclerAdapter.getCurrentSelectPst(), 0);
                ValueAnimator va = ValueAnimator.ofInt(scroll_height, init_height);
                va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Integer value = (Integer) animation.getAnimatedValue();
                        needChangeView.getLayoutParams().height = value.intValue();
                        needChangeView.requestLayout();
                    }
                });
                va.setDuration(200);
                va.start();
            }

            int index = bodyLinearLayoutManager.findFirstVisibleItemPosition();
            AgendaViewBody fstVisibleBody = (AgendaViewBody) bodyLinearLayoutManager.findViewByPosition(index);
            monthAgendaViewCalendar = fstVisibleBody.getCalendar();
            if (iTimeCalendarInterface != null){
                iTimeCalendarInterface.onDateChanged(monthAgendaViewCalendar.getCalendar().getTime());
            }

            if (newState == 1){
                //because 1->2->selected->0
                slideByUser = true;
            }else if (newState == 2){
            }else {
                //after executed selected, reset to false;
                //for now header date

                slideByUser = false;

            }
        }
    }

    public void setITimeCalendarMonthAgendaViewListener(ITimeCalendarMonthAgendaViewListener agendaViewListener){
        this.iTimeCalendarInterface = agendaViewListener;
        if (bodyRecyclerAdapter != null){
            bodyRecyclerAdapter.setOnEventClickListener(this.iTimeCalendarInterface);
        }
    }

    private void setCalendarConfig(CalendarConfig calendarConfig) {
        this.bodyRecyclerAdapter.setCalendarConfig(calendarConfig);
        this.bodyRecyclerAdapter.notifyDataSetChanged();
    }

    public CalendarConfig getConfig() {
        return config;
    }
}
