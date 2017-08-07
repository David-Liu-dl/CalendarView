package david.itimecalendar.calendar.ui.agendaview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import david.itimecalendar.R;
import david.itimecalendar.calendar.ui.monthview.DayViewHeader;
import david.itimecalendar.calendar.util.MyCalendar;

public class AgendaHeaderViewRecyclerAdapter extends RecyclerView.Adapter<AgendaHeaderViewRecyclerAdapter.MyViewHolder> {
    public String TAG = "AgendaHeader";
    private LayoutInflater inflater;

    private int upperBoundsOffset;
    private int startPosition;

    private ArrayList<MyViewHolder> holds = new ArrayList<>();

    private DayViewHeader.OnCheckIfHasEvent onCheckIfHasEvent;

    private int todayOffSet = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;

    public int rowPst;
    public int todayOfWeek;
    public int indexInRow;


    public AgendaHeaderViewRecyclerAdapter(Context context, int upperBoundsOffset) {
        inflater = LayoutInflater.from(context);
        this.upperBoundsOffset = upperBoundsOffset;
        startPosition = upperBoundsOffset;
        rowPst = startPosition;
        todayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        indexInRow = todayOfWeek;
    }

    public int getCurrentSelectPst(){
        return this.rowPst;
    }

    public void setOnCheckIfHasEvent(DayViewHeader.OnCheckIfHasEvent onCheckIfHasEvent){
        this.onCheckIfHasEvent = onCheckIfHasEvent;
    }

    public int getCurrentDayOffset(){
        int offsetRow = rowPst - startPosition;
        int indexOffset = indexInRow;
        int totalOffset = offsetRow*7 + indexOffset;
        final int dayOffset = startPosition + totalOffset - todayOffSet;
        return dayOffset;
    }
    @Override
    public AgendaHeaderViewRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.itime_day_view_header, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        holds.add(holder);

        return holder;
    }

    @Override
    public void onBindViewHolder(AgendaHeaderViewRecyclerAdapter.MyViewHolder holder, int position) {
        MyCalendar calendar = holder.headerRow.getCalendar();
        calendar.setOffset((position-startPosition)*7 - todayOfWeek);
        holder.headerRow.rowPst = position;
        holder.headerRow.updateDate();
        if (position == rowPst){
            holder.headerRow.performNthDayClick(indexInRow);
        }
        holder.headerRow.invalidate();

        // update scrolling max & min date
        if (onHeaderListener != null){
            onHeaderListener.onHeaderFlingDateChanged(calendar.getCalendar().getTime());
        }
    }

    @Override
    public int getItemCount() {
        return this.upperBoundsOffset*2+1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        DayViewHeader headerRow;

        public MyViewHolder(View itemView) {
            super(itemView);
            headerRow = (DayViewHeader) itemView;
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
                public void onDateSelected(Date date) {
                    if (onHeaderListener!=null){
                        onHeaderListener.onDateSelected(date);
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
        void onDateSelected(Date date);
        void onHeaderFlingDateChanged(Date newestDate);
    }
}