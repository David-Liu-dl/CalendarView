package org.unimelb.itime.test.david_dev;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.unimelb.itime.test.R;
import org.unimelb.itime.vendor.dayview.DayViewHeader;
import org.unimelb.itime.vendor.dayview.DayViewHeaderRecyclerDivider;

/**
 * Created by yuhaoliu on 31/08/16.
 */
public class MonthAgendaView extends LinearLayout{
    private final String TAG = "AgendaHeader";

    private LinearLayout parent;
    private LinearLayoutManager headerLinearLayoutManager;
    private LinearLayoutManager bodyLinearLayoutManager;

    private RecyclerView headerRecyclerView;
    private AgendaBodyRecyclerView bodyRecyclerView;

    private AgendaHeaderViewRecyclerAdapter headerRecyclerAdapter;
    private AgendaBodyViewRecyclerAdapter bodyRecyclerAdapter;

    private Context context;

    private int upperBoundsOffset = 1;
    private int init_height;
    private int scroll_height;

    private int last_pst;

    private DayViewHeader.OnCheckIfHasEvent onCheckIfHasEvent;
    private AgendaViewBody.OnLoadEvents onLoadEvents;

    public MonthAgendaView(Context context) {
        super(context);
        initView();
    }

    public MonthAgendaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MonthAgendaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        this.context = getContext();

        parent = (LinearLayout) LayoutInflater.from(context).inflate(org.unimelb.itime.vendor.R.layout.itime_month_agenda_view, null);
        this.addView(parent);

        headerRecyclerView = (RecyclerView) parent.findViewById(R.id.headerRowList);
        bodyRecyclerView = (AgendaBodyRecyclerView) parent.findViewById(R.id.bodyRowList);

        upperBoundsOffset = 10000;
        last_pst = upperBoundsOffset;

        this.setUpHeader();
        this.setUpBody();
    }

    private void setUpHeader(){
        headerRecyclerAdapter = new AgendaHeaderViewRecyclerAdapter(context, upperBoundsOffset);
        headerRecyclerAdapter.setOnSynBodyListener(new AgendaHeaderViewRecyclerAdapter.OnSynBodyListener() {
            @Override
            public void synBody(int scrollTo) {
                last_pst = scrollTo;
                bodyLinearLayoutManager.scrollToPositionWithOffset(scrollTo, -5);
            }
        });
        setOnCheckIfHasEvent(this.onCheckIfHasEvent);
        headerRecyclerView.setHasFixedSize(true);
        headerRecyclerView.setAdapter(headerRecyclerAdapter);
        headerLinearLayoutManager = new LinearLayoutManager(context);
        headerRecyclerView.setLayoutManager(headerLinearLayoutManager);
        headerRecyclerView.addItemDecoration(new DayViewHeaderRecyclerDivider(context));
        final DisplayMetrics dm = getResources().getDisplayMetrics();
        init_height = (dm.widthPixels / 7) * 2;
        scroll_height = (dm.widthPixels / 7) * 4;

        ViewGroup.LayoutParams recycler_layoutParams = headerRecyclerView.getLayoutParams();
        recycler_layoutParams.height = init_height;
        headerRecyclerView.setLayoutParams(recycler_layoutParams);
        headerRecyclerView.addOnScrollListener(new HeaderOnScrollListener());
        headerRecyclerView.setLayoutParams(recycler_layoutParams);
        headerRecyclerView.stopScroll();
        headerRecyclerView.scrollToPosition(upperBoundsOffset);
    }

    private void setUpBody(){
        bodyRecyclerAdapter = new AgendaBodyViewRecyclerAdapter(context, upperBoundsOffset);
//        setOnCheckIfHasEvent(this.onCheckIfHasEvent);
        setOnLoadEvents(this.onLoadEvents);
        bodyRecyclerView.setFlingScale(0.3f);
        bodyRecyclerView.setHasFixedSize(false);
        bodyRecyclerView.setAdapter(bodyRecyclerAdapter);
        bodyLinearLayoutManager = new LinearLayoutManager(context);
        headerRecyclerAdapter.setBodyRecyclerView(bodyRecyclerView);
        headerRecyclerAdapter.setBodyLayoutManager(bodyLinearLayoutManager);
        bodyRecyclerView.setLayoutManager(bodyLinearLayoutManager);
        bodyRecyclerView.addItemDecoration(new AgendaBodyViewRecyclerDivider(context));
        bodyRecyclerView.addOnScrollListener(new BodyOnScrollListener());

        ViewGroup.LayoutParams recycler_layoutParams = bodyRecyclerView.getLayoutParams();
        recycler_layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        recycler_layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        bodyRecyclerView.setLayoutParams(recycler_layoutParams);
        bodyRecyclerView.stopScroll();
        bodyRecyclerView.scrollToPosition(upperBoundsOffset);
    }

    public void setOnCheckIfHasEvent(DayViewHeader.OnCheckIfHasEvent onCheckIfHasEvent){
        this.onCheckIfHasEvent = onCheckIfHasEvent;
        if (headerRecyclerAdapter != null){
            headerRecyclerAdapter.setOnCheckIfHasEvent(this.onCheckIfHasEvent);
        }
    }

    public void setOnLoadEvents(AgendaViewBody.OnLoadEvents onLoadEvents){
        this.onLoadEvents = onLoadEvents;
        if (bodyRecyclerAdapter != null){
            bodyRecyclerAdapter.setOnLoadEvents(this.onLoadEvents);
        }
    }

    class HeaderOnScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView v, int newState) {
            super.onScrollStateChanged(headerRecyclerView, newState);
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
        }
    }

    class BodyOnScrollListener extends RecyclerView.OnScrollListener{
        private boolean slideByUser = false;
        private int error_skip = 0;

        @Override
        public void onScrolled(RecyclerView v, int dx, int dy) {
            super.onScrolled(v, dx, dy);

            //update header selected date
            int fst_visible_pst = bodyLinearLayoutManager.findFirstVisibleItemPosition();

            if (slideByUser) {
                if (fst_visible_pst != -1 && fst_visible_pst != last_pst ) {
                    final boolean slideToNext = (fst_visible_pst > last_pst);
                    int skip = Math.abs(fst_visible_pst - last_pst);
                    int need_slides = skip + error_skip;
                    int current_turn_error = 0;

                    for (int i = 0; i < need_slides; i++) {
                        boolean noError = slide(slideToNext);
                        current_turn_error += (noError ? 0 : (slideToNext ? 1 : -1));
                    }
                    error_skip = current_turn_error;

                    Log.i(TAG, "error_skip: " + error_skip);
                }

            }else {
                bodyRecyclerAdapter.notifyDataSetChanged();
            }
            //update last_pst
            last_pst = fst_visible_pst;
        }

        @Override
        public void onScrollStateChanged(RecyclerView v, int newState) {
            super.onScrollStateChanged(v, newState);
            //update header height
            final View needChangeView = headerRecyclerView;
            if (needChangeView.getHeight() == scroll_height){
                headerRecyclerView.stopScroll();
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

            if (newState == 1){
                //because 1->2->selected->0
                slideByUser = true;
            }else if (newState == 2){
            }else {
                //after executed selected, reset to false;
                slideByUser = false;
            }
        }

        private boolean slide(boolean slideToNext){
            DayViewHeader headerView =
                    (DayViewHeader) headerLinearLayoutManager.findViewByPosition(headerRecyclerAdapter.rowPst);

            if (headerView == null){
                headerLinearLayoutManager.scrollToPosition(headerRecyclerAdapter.rowPst);
            }

            if (headerView != null){
                if (slideToNext){
                    if (headerView.getCurrentSelectedIndex() == 6){
                        headerRecyclerAdapter.rowPst += 1;
                        headerRecyclerAdapter.indexInRow = 0;
                        headerRecyclerView.scrollToPosition(headerRecyclerAdapter.rowPst);

                        DayViewHeader nextHeaderView =
                                (DayViewHeader) headerLinearLayoutManager.findViewByPosition(headerRecyclerAdapter.rowPst);
                        if (nextHeaderView != null){
                            nextHeaderView.performFstDayClick();
                        }
                    }else {
                        headerView.nextPerformClick();
                    }
                }else{
                    if (headerView.getCurrentSelectedIndex() == 0){
                        headerRecyclerAdapter.rowPst -= 1;
                        headerRecyclerAdapter.indexInRow = 6;
                        headerRecyclerView.scrollToPosition(headerRecyclerAdapter.rowPst);
                        DayViewHeader previousHeaderView =
                                (DayViewHeader) headerLinearLayoutManager.findViewByPosition(headerRecyclerAdapter.getCurrentSelectPst());
                        if (previousHeaderView != null){
                            previousHeaderView.performLastDayClick();
                        }
                    }else {
                        headerView.previousPerformClick();
                    }
                }
                return true;
            }else {
                Log.i(TAG, "header none: " + "123" );
                return false;
            }
        }
    }

}
