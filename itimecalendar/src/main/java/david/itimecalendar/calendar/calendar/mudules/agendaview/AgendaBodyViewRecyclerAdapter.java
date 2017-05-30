package david.itimecalendar.calendar.calendar.mudules.agendaview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import david.itimecalendar.R;
import david.itimecalendar.calendar.listeners.ITimeEventInterface;
import david.itimecalendar.calendar.listeners.ITimeEventPackageInterface;
import david.itimecalendar.calendar.util.MyCalendar;

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

    private ITimeEventPackageInterface eventPackage;

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

    public void setDayEventMap(ITimeEventPackageInterface eventPackage) {
        this.eventPackage = eventPackage;
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

        List<ITimeEventInterface> allEvents = new ArrayList<>();

        //all day
//        for (ITimeEventInterface allDayEvent:this.eventPackage.getAllDayEvents()
//             ) {
//            if (isWithin(allDayEvent, holder.bodyRow.getCalendar().getCalendar())){
//                allEvents.add(allDayEvent);
//            }
//        }

        //regular
        if (this.eventPackage.getRegularEventDayMap().containsKey(startTime)){
            allEvents.addAll(this.eventPackage.getRegularEventDayMap().get(startTime));
        }

        //repeated
        if (this.eventPackage.getRepeatedEventDayMap().containsKey(startTime)){
            allEvents.addAll(this.eventPackage.getRepeatedEventDayMap().get(startTime));
        }

        holder.bodyRow.setEventList(allEvents);

        holder.bodyRow.postInvalidate();
    }

    @Override
    public int getItemCount() {
        return this.upperBoundsOffset*2+1;
    }

    private boolean isWithin(ITimeEventInterface event, Calendar calendar){
        long startTime = event.getStartTime();
        long endTime = event.getEndTime();

        MyCalendar calS = new MyCalendar(calendar);

        MyCalendar calE = new MyCalendar(calendar);
        calE.setHour(23);
        calE.setMinute(59);

        long todayStartTime =  calS.getBeginOfDayMilliseconds();
        long todayEndTime =  calE.getCalendar().getTimeInMillis();

        return todayEndTime >= startTime && todayStartTime <= endTime;
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