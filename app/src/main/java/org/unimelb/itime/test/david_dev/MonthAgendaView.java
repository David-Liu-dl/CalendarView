package org.unimelb.itime.test.david_dev;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.unimelb.itime.test.R;
import org.unimelb.itime.vendor.dayview.DayViewHeader;
import org.unimelb.itime.vendor.dayview.DayViewHeaderRecyclerAdapter;
import org.unimelb.itime.vendor.dayview.DayViewHeaderRecyclerDivider;

/**
 * Created by yuhaoliu on 31/08/16.
 */
public class MonthAgendaView extends LinearLayout{
    private final String TAG = "MyAPP";

    private LinearLayout parent;
    private LinearLayoutManager headerLinearLayoutManager;
    private BodyRecyclerViewManager bodyLinearLayoutManager;

    private RecyclerView headerRecyclerView;
    private AgendaBodyRecyclerView bodyRecyclerView;

    private DayViewHeaderRecyclerAdapter headerRecyclerAdapter;
    private AgendaViewRecyclerAdapter bodyRecyclerAdapter;

    private Context context;

    private int upperBoundsOffset = 1;
    private int init_height;
    private int scroll_height;

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

        this.setUpHeader();
        this.setUpBody();
    }

    private void setUpHeader(){
        headerRecyclerAdapter = new DayViewHeaderRecyclerAdapter(context, upperBoundsOffset);
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
        bodyRecyclerAdapter = new AgendaViewRecyclerAdapter(context, upperBoundsOffset);
//        setOnCheckIfHasEvent(this.onCheckIfHasEvent);
        setOnLoadEvents(this.onLoadEvents);
        bodyRecyclerView.setFlingScale(0.3f);
        bodyRecyclerView.setHasFixedSize(false);
        bodyRecyclerView.setAdapter(bodyRecyclerAdapter);
        bodyLinearLayoutManager = new BodyRecyclerViewManager(context);
//        bodyLinearLayoutManager.
        bodyRecyclerView.setLayoutManager(bodyLinearLayoutManager);
        bodyRecyclerView.addItemDecoration(new AgendaViewRecyclerDivider(context));
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
        private int last_pst = upperBoundsOffset;
        @Override
        public void onScrolled(RecyclerView v, int dx, int dy) {
            super.onScrolled(v, dx, dy);
            //update header selected date
            int fst_visible_pst = bodyLinearLayoutManager.findFirstVisibleItemPosition();

            if (fst_visible_pst != -1 && fst_visible_pst != last_pst){
                final boolean slideToNext = (fst_visible_pst > last_pst);
                int skip = Math.abs(fst_visible_pst - last_pst);

                //avoid skip by body
                headerRecyclerAdapter.notifyDataSetChanged();
                for (int i = 0; i < skip; i++) {
                    slide(slideToNext);
                }

                //update last_pst
                last_pst = fst_visible_pst;
            }
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

//            times = 0;
        }

        private void slide(boolean slideToNext){
            DayViewHeader headerView =
                    (DayViewHeader) headerLinearLayoutManager.findViewByPosition(headerRecyclerAdapter.rowPst);
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
            }else {
                Log.i(TAG, "header none: " + "123" );
            }

        }
    }

    class BodyRecyclerViewManager extends LinearLayoutManager{
        private static final float MILLISECONDS_PER_INCH = 50f;

        public BodyRecyclerViewManager(Context context) {
            super(context);
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
            super.smoothScrollToPosition(recyclerView, state, position);
            final LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {

                @Override
                public PointF computeScrollVectorForPosition(int targetPosition) {
                    return BodyRecyclerViewManager.this.computeScrollVectorForPosition(targetPosition);
                }

                @Override
                protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                    return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
                }
            };

            linearSmoothScroller.setTargetPosition(position);
            startSmoothScroll(linearSmoothScroller);
        }
    }
}
