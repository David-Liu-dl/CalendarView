package org.unimelb.itime.vendor.dayview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.helper.MyCalendar;

import java.util.ArrayList;
import java.util.Calendar;

public class DayViewHeaderRecyclerAdapter extends RecyclerView.Adapter<DayViewHeaderRecyclerAdapter.MyViewHolder> {
    public String TAG = "MyAPP";
    private LayoutInflater inflater;
    private int upperBoundsOffset;
    private int startPosition;
    private ArrayList<MyViewHolder> holds = new ArrayList<>();
    private DayViewHeader.OnCheckIfHasEvent onCheckIfHasEvent;

    public int rowPst;
    public int todayOfWeek;
    public int indexInRow = 0;
    public ViewPager bodyPager;

    public DayViewHeaderRecyclerAdapter(Context context, int upperBoundsOffset) {
        inflater = LayoutInflater.from(context);
        this.upperBoundsOffset = upperBoundsOffset;
        startPosition = upperBoundsOffset;
        rowPst = startPosition;
        todayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        indexInRow = todayOfWeek;
    }

    public void setBodyPager(ViewPager bodyPager){
        this.bodyPager = bodyPager;
    }

    public int getCurrentSelectPst(){
        return this.rowPst;
    }

    public void setOnCheckIfHasEvent(DayViewHeader.OnCheckIfHasEvent onCheckIfHasEvent){
        this.onCheckIfHasEvent = onCheckIfHasEvent;
    }

    @Override
    public DayViewHeaderRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.itime_day_view_header_view, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        holds.add(holder);

        return holder;
    }

    @Override
    public void onBindViewHolder(DayViewHeaderRecyclerAdapter.MyViewHolder holder, int position) {
        holder.headerRow.rowPst = position;
        holder.headerRow.getCalendar().setOffset((position-startPosition)*7 - todayOfWeek);
        holder.headerRow.updateDate();
        if (position == rowPst){
            holder.headerRow.performNthDayClick(indexInRow);
        }
        Log.i(TAG, "onBindViewHolder: " + position);
    }

    @Override
    public int getItemCount() {
        return this.upperBoundsOffset*2+1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        DayViewHeader headerRow;
        public MyViewHolder(View itemView) {
            super(itemView);
            headerRow = (DayViewHeader) itemView.findViewById(R.id.calendarDayViewHeader);
            headerRow.setCalendar(new MyCalendar(Calendar.getInstance()));
            headerRow.resizeCurrentWeekHeaders();
            headerRow.setOnCalendarHeaderDayClickListener(new DayViewHeader.OnCalendarHeaderDayClickListener() {
                @Override
                public void onClick(View v) {
                    for (MyViewHolder holder:holds
                         ) {
                        holder.headerRow.clearAllBg();
                        holder.headerRow.updateDate();
                    }
                }

                @Override
                public void setCurrentSelectPst(int rowPstIn) {
                    rowPst = rowPstIn;
                }

                @Override
                public void setCurrentSelectIndexInRow(int indexInRowIn) {
                    indexInRow = indexInRowIn;
                    if (onHeaderListener != null){
                        MyCalendar calendar = new MyCalendar(headerRow.getCalendar());
                        calendar.setOffsetByDate(indexInRowIn);
                        onHeaderListener.onClick(calendar);
                    }
                }

                @Override
                public void synBodyPart(int rowPst, int indexInRow) {
                    if (bodyPager != null){
                        int offsetRow = rowPst - startPosition;
                        int indexOffset = indexInRow;
                        int totalOffset = offsetRow*7 + indexOffset;
                        final int scrollTo = startPosition + totalOffset;

                        bodyPager.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                boolean needUpdate =
                                        ((FlexibleLenBodyViewPagerAdapter) bodyPager.getAdapter()).currentDayPos
                                        != scrollTo;
                                if (needUpdate){
                                    Log.i(TAG, "needUpdate: ");
                                    bodyPager.setCurrentItem(scrollTo, false);
                                    ((FlexibleLenBodyViewPagerAdapter) bodyPager.getAdapter()).currentDayPos = scrollTo;
                                }
                            }
                        },50);
                    }else {
                        Log.i(TAG, "synBodyPart: " + "Fail, pager == null");
                    }
                }
            });
            headerRow.setOnCheckIfHasEvent(onCheckIfHasEvent);
        }
    }

    private OnHeaderListener onHeaderListener;

    public void setOnHeaderListener(OnHeaderListener onHeaderListener){
        this.onHeaderListener = onHeaderListener;
    }
    public interface OnHeaderListener{
        void onClick(MyCalendar myCalendar);
    }
}