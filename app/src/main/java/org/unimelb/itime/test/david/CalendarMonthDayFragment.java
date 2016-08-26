package org.unimelb.itime.test.david;

import android.animation.ValueAnimator;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.unimelb.itime.test.R;
import org.unimelb.itime.vendor.dayview.DayViewBody;
import org.unimelb.itime.vendor.dayview.DayViewBodyPagerAdapter;
import org.unimelb.itime.vendor.dayview.DayViewHeader;
import org.unimelb.itime.vendor.dayview.DayViewHeaderRecyclerAdapter;
import org.unimelb.itime.vendor.dayview.DayViewHeaderRecyclerDivider;
import org.unimelb.itime.vendor.helper.MyCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yuhaoliu on 10/08/16.
 */
public class CalendarMonthDayFragment extends Fragment {
    private final String TAG = "MyAPP";

    private Handler handler=new Handler();

    private RecyclerView recyclerView;
    private DayViewHeaderRecyclerAdapter recyclerAdapter;
    private int upperBoundsOffset = 1;

    private int init_height;
    private int scroll_height;
    private LinearLayoutManager mLinearLayoutManager;
    DayViewBodyPagerAdapter bodyPagerAdapter;
    ViewPager bodyPager;
    int bodyCurrentPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View v =inflater.inflate(R.layout.itime_month_day_fragment,null,false);
        recyclerView = (RecyclerView) v.findViewById(R.id.headerRowList);
        bodyPager = (ViewPager) v.findViewById(R.id.pager);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        upperBoundsOffset = 100000;

        this.setUpHeader();
        this.setUpBody();
    }

    private void setUpHeader(){
        recyclerAdapter = new DayViewHeaderRecyclerAdapter(getActivity(), upperBoundsOffset);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerAdapter);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.addItemDecoration(new DayViewHeaderRecyclerDivider(getActivity().getApplicationContext()));
        final DisplayMetrics dm = getResources().getDisplayMetrics();
        init_height = (dm.widthPixels / 7) * 2;
        scroll_height = (dm.widthPixels / 7) * 4;

        ViewGroup.LayoutParams recycler_layoutParams = recyclerView.getLayoutParams();
        recycler_layoutParams.height = init_height;
        recyclerView.setLayoutParams(recycler_layoutParams);
        recyclerView.setOnScrollListener(new headerOnScrollListener());
        move(upperBoundsOffset);
    }

    private void move(int n){
        if (n<0 || n>=recyclerAdapter.getItemCount() ){
            return;
        }
        recyclerView.stopScroll();
        recyclerView.scrollToPosition(n);
    }

    private void setUpBody(){
        bodyPagerAdapter = new DayViewBodyPagerAdapter(initBody(), upperBoundsOffset);
        bodyPagerAdapter.notifyDataSetChanged();
        recyclerAdapter.setBodyPager(bodyPager);
        bodyPager.setAdapter(bodyPagerAdapter);
        bodyPager.setOffscreenPageLimit(1);
        bodyPager.setCurrentItem(upperBoundsOffset);

        bodyCurrentPosition = upperBoundsOffset;
        bodyPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private boolean slideByUser = false;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                try{

                    if (slideByUser) {
                        final boolean slideToRight = (position > bodyCurrentPosition);

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                recyclerAdapter.notifyDataSetChanged();

                                Log.i(TAG, "onPageSelected: slide body by user");
                                DayViewHeader headerView =
                                        (DayViewHeader) mLinearLayoutManager.findViewByPosition(recyclerAdapter.getCurrentSelectPst());

                                if (headerView == null){
                                    Log.e(TAG, "onPageSelected: ", new Throwable("current header is null"));
                                }
                                if (slideToRight){
                                    if (headerView.getCurrentSelectedIndex() == 6){
                                        recyclerAdapter.rowPst += 1;
                                        recyclerAdapter.indexInRow = 0;
                                        recyclerView.scrollToPosition(recyclerAdapter.rowPst);

                                        DayViewHeader nextHeaderView =
                                                (DayViewHeader) mLinearLayoutManager.findViewByPosition(recyclerAdapter.getCurrentSelectPst());
                                        if (nextHeaderView != null){
                                            nextHeaderView.performFstDayClick();
                                        }else
                                            Log.i(TAG, "next find null in main: ");
                                    }else {
                                        headerView.nextPerformClick();
                                    }
                                }else{
                                    if (headerView.getCurrentSelectedIndex() == 0){
                                        recyclerAdapter.rowPst -= 1;
                                        recyclerAdapter.indexInRow = 6;
                                        recyclerView.scrollToPosition(recyclerAdapter.rowPst);
                                        DayViewHeader previousHeaderView =
                                                (DayViewHeader) mLinearLayoutManager.findViewByPosition(recyclerAdapter.getCurrentSelectPst());
                                        if (previousHeaderView != null){
                                            previousHeaderView.performLastDayClick();
                                        }else{
                                            Log.i(TAG, "previous find null in main: ");
                                        }
                                    }else {
                                        headerView.previousPerformClick();
                                    }
                                }
                            }
                        },0);

                    }
                }finally {
                    bodyCurrentPosition = position;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 1){
                    //because 1->2->selected->0
                    slideByUser = true;
                }else if (state == 2){
                }else {
                    //after executed selected, reset to false;
                    slideByUser = false;
                }
            }
        });
    }

    private ArrayList<View> initBody(){
        int size = 4;
        ArrayList<View> lists = new ArrayList<>();
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(new Date());
        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH) - day_of_week);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < size; i++) {
            DayViewBody bodyView = (DayViewBody) LayoutInflater.from(this.getActivity().getApplicationContext()).inflate(R.layout.itime_day_view_body_view,null);
            bodyView.setCalendar(new MyCalendar(calendar));
            bodyView.dayViewController.scrollContainerView.setOnTouchListener(new bodyOnTouchListener());
            lists.add(bodyView);
        }

        return lists;
    }

    class headerOnScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView v, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
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

    class bodyOnTouchListener implements View.OnTouchListener{
        private float pointX;
        private float pointY;
        private int tolerance = 50;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch(event.getAction()){
                case MotionEvent.ACTION_MOVE:
                    return false; //This is important, if you return TRUE the action of swipe will not take place.
                case MotionEvent.ACTION_DOWN:
                    pointX = event.getX();
                    pointY = event.getY();
                    final View needChangeView = recyclerView;
                    if (needChangeView.getHeight() == scroll_height){
                        recyclerView.stopScroll();
                        mLinearLayoutManager.scrollToPositionWithOffset(recyclerAdapter.getCurrentSelectPst(), 0);
                        final View view = needChangeView;
                        ValueAnimator va = ValueAnimator.ofInt(scroll_height, init_height);
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
                    break;
                case MotionEvent.ACTION_UP:
                    boolean sameX = pointX + tolerance > event.getX() && pointX - tolerance < event.getX();
                    boolean sameY = pointY + tolerance > event.getY() && pointY - tolerance < event.getY();
                    if(sameX && sameY){
                    }
            }
            return false;
        }
    }


}
