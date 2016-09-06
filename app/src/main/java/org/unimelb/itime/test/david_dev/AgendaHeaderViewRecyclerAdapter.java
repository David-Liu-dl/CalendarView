package org.unimelb.itime.test.david_dev;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.dayview.DayViewHeader;
import org.unimelb.itime.vendor.helper.MyCalendar;

import java.util.ArrayList;
import java.util.Calendar;

public class AgendaHeaderViewRecyclerAdapter extends RecyclerView.Adapter<AgendaHeaderViewRecyclerAdapter.MyViewHolder> {
    public String TAG = "AgendaHeader";
    private LayoutInflater inflater;

    private int upperBoundsOffset;
    private int startPosition;

    private ArrayList<MyViewHolder> holds = new ArrayList<>();

    private DayViewHeader.OnCheckIfHasEvent onCheckIfHasEvent;

    private OnSynBodyListener onSynBodyListener;

    private int todayOffSet = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;

    public int rowPst;
    public int todayOfWeek;
    public int indexInRow = 0;

    private AgendaBodyRecyclerView bodyRecyclerView;
    private LinearLayoutManager bodyLinearLayoutManager;

    public AgendaHeaderViewRecyclerAdapter(Context context, int upperBoundsOffset) {
        inflater = LayoutInflater.from(context);
        this.upperBoundsOffset = upperBoundsOffset;
        startPosition = upperBoundsOffset;
        rowPst = startPosition;
        todayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        indexInRow = todayOfWeek;
    }

    public void setBodyRecyclerView(AgendaBodyRecyclerView bodyRecyclerView){
        this.bodyRecyclerView = bodyRecyclerView;
    }

    public void setBodyLayoutManager(LinearLayoutManager bodyLinearLayoutManager){
        this.bodyLinearLayoutManager = bodyLinearLayoutManager;
    }

    public void setOnSynBodyListener(OnSynBodyListener onSynBodyListener) {
        this.onSynBodyListener = onSynBodyListener;
    }

    public int getCurrentSelectPst(){
        return this.rowPst;
    }

    public void setOnCheckIfHasEvent(DayViewHeader.OnCheckIfHasEvent onCheckIfHasEvent){
        this.onCheckIfHasEvent = onCheckIfHasEvent;
    }

//    public void setToDate(Calendar body_fst_cal){
//        DayViewHeader headerView =
//                (DayViewHeader) this.get.findViewByPosition(headerRecyclerAdapter.rowPst);
////        //update header selected date
////        int fst_visible_pst = bodyLinearLayoutManager.findFirstVisibleItemPosition();
////        Calendar body_fst_cal =
////                ((AgendaViewBody) bodyLinearLayoutManager.findViewByPosition(fst_visible_pst)).getCalendar().getCalendar();
//
//        Calendar header_current_cal = headerView.getCalendar().getCalendar();
//        int date_offset = body_fst_cal.get(Calendar.DAY_OF_YEAR)
//                - (header_current_cal.get(Calendar.DAY_OF_YEAR) + headerRecyclerAdapter.indexInRow);
//        Log.i(TAG, "date_offset: " + date_offset);
//        int row_diff = date_offset/7;
//        int day_diff = ((headerRecyclerAdapter.indexInRow+1) + date_offset%7)%7;
//        Log.i(TAG, "day_diff: " + day_diff);
//
//        if (date_offset > 0){
//            int current_row_left_days = (7 - (headerRecyclerAdapter.indexInRow + 1));
//            row_diff = row_diff + ((date_offset%7 + current_row_left_days)>7 ? 1:0);
//        }else if(date_offset < 0){
//            int current_row_to_days = (headerRecyclerAdapter.indexInRow + 1);
//            row_diff = row_diff + ((date_offset%7 + current_row_to_days)<=0 ? -1:0);
//        }
//        Log.i(TAG, "row_diff: " + row_diff);
//        if ((row_diff != 0 || day_diff != 0)){
//            if (row_diff != 0){
//                int newRowPst = row_diff + headerRecyclerAdapter.rowPst;
//                headerRecyclerView.scrollToPosition(newRowPst);
//                headerRecyclerAdapter.rowPst = newRowPst;
//            }
//            if (day_diff != 0){
//                ((DayViewHeader) headerLinearLayoutManager.findViewByPosition(headerRecyclerAdapter.rowPst)).performNthDayClick(day_diff - 1);
//                headerRecyclerAdapter.indexInRow = day_diff - 1;
//            }
//        }
//    }

    @Override
    public AgendaHeaderViewRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.itime_day_view_header_view, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        holds.add(holder);

        return holder;
    }

    @Override
    public void onBindViewHolder(AgendaHeaderViewRecyclerAdapter.MyViewHolder holder, int position) {
        holder.headerRow.rowPst = position;
        holder.headerRow.getCalendar().setOffset((position-startPosition)*7 - todayOfWeek);
        holder.headerRow.updateDate();
        if (position == rowPst){
            holder.headerRow.performNthDayClick(indexInRow);
        }
        holder.headerRow.invalidate();
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
                }

                @Override
                public void synBodyPart(int rowPst, int indexInRow) {
                    if (bodyRecyclerView != null && (bodyRecyclerView.getScrollState() == 0)){
                        int offsetRow = rowPst - startPosition;
                        int indexOffset = indexInRow;
                        int totalOffset = offsetRow*7 + indexOffset;
                        final int scrollTo = startPosition + totalOffset - todayOffSet;

                        if (onSynBodyListener != null){
                            onSynBodyListener.synBody(scrollTo);
                        }
                    }else {
//                        Log.i(TAG, "synBodyPart: " + "Fail, pager == null");
                    }
                }
            });
            headerRow.setOnCheckIfHasEvent(onCheckIfHasEvent);
        }
    }

    public interface OnSynBodyListener{
        void synBody(int scrollTo);
    }
}