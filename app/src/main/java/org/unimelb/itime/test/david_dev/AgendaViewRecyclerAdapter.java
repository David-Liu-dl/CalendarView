package org.unimelb.itime.test.david_dev;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.unimelb.itime.test.R;
import org.unimelb.itime.vendor.dayview.DayViewHeader;
import org.unimelb.itime.vendor.helper.MyCalendar;

import java.util.ArrayList;
import java.util.Calendar;

public class AgendaViewRecyclerAdapter extends RecyclerView.Adapter<AgendaViewRecyclerAdapter.MyViewHolder> {
    public String TAG = "MyAPP";
    private LayoutInflater inflater;
    private int upperBoundsOffset;
    private int startPosition;
    private ArrayList<MyViewHolder> holds = new ArrayList<>();
    private AgendaViewBody.OnLoadEvents onLoadEvents;

    public int rowPst;
    public int todayOfWeek;
    public int indexInRow = 0;

    public AgendaViewRecyclerAdapter(Context context, int upperBoundsOffset) {
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

    public void setOnLoadEvents(AgendaViewBody.OnLoadEvents onLoadEvents){
        this.onLoadEvents = onLoadEvents;
    }

    @Override
    public AgendaViewRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate( R.layout.itime_agenda_view_body_view, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        holds.add(holder);

        return holder;
    }

    @Override
    public void onBindViewHolder(AgendaViewRecyclerAdapter.MyViewHolder holder, int position) {
        holder.bodyRow.getCalendar().setOffset(position-startPosition);
        holder.bodyRow.updateHeaderView();
        holder.bodyRow.loadEvents();

        Log.i(TAG, "onBindViewHolder: " + position);
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
            if (onLoadEvents != null){
                bodyRow.setOnLoadEvents(onLoadEvents);
            }
        }
    }
}