package org.unimelb.itime.vendor.agendaview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.helper.MyCalendar;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class AgendaBodyViewRecyclerAdapter extends RecyclerView.Adapter<AgendaBodyViewRecyclerAdapter.MyViewHolder> {
    public String TAG = "AgendaBodyViewRecyclerAdapter";
    private LayoutInflater inflater;
    private int upperBoundsOffset;
    private int startPosition;
    private ArrayList<MyViewHolder> holds = new ArrayList<>();
    private AgendaViewBody.OnEventClickListener onEventClickListener;

    public int rowPst;
    public int todayOfWeek;
    public int indexInRow = 0;



    private Map<Long, List<ITimeEventInterface>> dayEventMap;

    public AgendaBodyViewRecyclerAdapter(Context context, int upperBoundsOffset) {
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

    public void setOnEventClickListener(AgendaViewBody.OnEventClickListener onEventClickListener){
        this.onEventClickListener = onEventClickListener;
    }

    public void setDayEventMap(Map<Long, List<ITimeEventInterface>> dayEventMap) {
        this.dayEventMap = dayEventMap;
    }

    @Override
    public AgendaBodyViewRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.itime_agenda_view_body_view, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        holds.add(holder);

        return holder;
    }

    @Override
    public void onBindViewHolder(AgendaBodyViewRecyclerAdapter.MyViewHolder holder, int position) {
        holder.bodyRow.getCalendar().setOffset(position-startPosition);
        holder.bodyRow.updateHeaderView();

        long startTime = holder.bodyRow.getCalendar().getBeginOfDayMilliseconds();

        if (this.dayEventMap.containsKey(startTime)){
            holder.bodyRow.setEventList(this.dayEventMap.get(startTime));
        }else{
            holder.bodyRow.setEventList(new ArrayList<ITimeEventInterface>());
        }

        holder.bodyRow.postInvalidate();
    }

    @Override
    public int getItemCount() {
        return this.upperBoundsOffset*2+1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        AgendaViewBody bodyRow;
        public MyViewHolder(View itemView) {
            super(itemView);
            bodyRow = (AgendaViewBody) itemView.findViewById(R.id.agendaViewBody);
            bodyRow.setCalendar(new MyCalendar(Calendar.getInstance()));
            if (onEventClickListener != null){
                bodyRow.setOnEventClickListener(onEventClickListener);
            }
        }
    }
}