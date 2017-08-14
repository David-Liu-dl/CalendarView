package david.itimecalendar.calendar.ui.agendaview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import david.itimecalendar.R;
import david.itimecalendar.calendar.listeners.ITimeEventInterface;
import david.itimecalendar.calendar.ui.CalendarConfig;
import david.itimecalendar.calendar.util.DensityUtil;
import david.itimecalendar.calendar.util.MyCalendar;

/**
 * Created by yuhaoliu on 31/08/16.
 */
public class AgendaViewBody extends LinearLayout{
    private CalendarConfig calendarConfig;
    private final String TAG = "MyAPP2";

    /*************************** Start of Color Setting **********************************/
    private int color_no_event = R.color.text_enable;
    /*************************** End of Color Setting **********************************/
    private int rs_divider = R.drawable.itime_light_divider_line;

    private MyCalendar myCalendar;
    private AgendaBodyHeader rowHeader;
    private LinearLayout rowBody;

    private List<ITimeEventInterface> events = new ArrayList<>();

    private TextView noEvent;

    private int titleSize = 12;

    private Context context;
    private OnEventClickListener onEventClickListener;

    private int currentDayType = -2;

    public AgendaViewBody(Context context) {
        super(context);
        this.context = context;
        this.setOrientation(LinearLayout.VERTICAL);
        initLayouts();
    }

    public AgendaViewBody(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.setOrientation(LinearLayout.VERTICAL);
        initLayouts();
    }

    public MyCalendar getCalendar() {
        return myCalendar;
    }

    public void setCalendar(MyCalendar myCalendar) {
        this.myCalendar = myCalendar;
        this.rowHeader.setMyCalendar(this.myCalendar);
    }

    public void setEventList(List<ITimeEventInterface> eventList){
        this.setCurrentDayType();
        this.events = filterInputEvents(eventList);
        displayEvents(this.events);
    }

    private void setCurrentDayType(){
        Calendar todayCal = Calendar.getInstance();
        long current_day_milliseconds = this.myCalendar.getBeginOfDayMilliseconds();
        long today_milliseconds = (new MyCalendar(todayCal)).getBeginOfDayMilliseconds();
        this.currentDayType = getDatesRelationType(today_milliseconds, current_day_milliseconds);
    }

    public void updateHeaderView(){
        this.rowHeader.updateHeaderView();
    }

    private void initLayouts(){
        //header
        rowHeader = new AgendaBodyHeader(context);
        this.addView(rowHeader);

        //body
        rowBody = new LinearLayout(context);
        rowBody.setOrientation(LinearLayout.VERTICAL);
        LayoutParams rowBodyParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        this.addView(rowBody, rowBodyParams);
    }

    private void displayEvents(final List<ITimeEventInterface> events){
        this.rowBody.removeAllViews();
        Collections.sort(events);
        if (events.size() != 0){

            for (int i = 0; i < events.size(); i++) {
                final ITimeEventInterface currentEvent = events.get(i);

                AgendaViewInnerBody rowBody = new AgendaViewInnerBody(context, currentEvent, this.currentDayType);
                rowBody.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onEventClickListener != null){
                            onEventClickListener.onEventClick(currentEvent);
                        }
                    }
                });
                LayoutParams rowBodyParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                int paddingLeft  = DensityUtil.dip2px(context,0);
                int paddingRight  = DensityUtil.dip2px(context,7.6f);
                int paddingTop  = DensityUtil.dip2px(context,18);
                int paddingBottom  = DensityUtil.dip2px(context,18);

                rowBody.setPadding(paddingLeft,paddingTop,paddingRight,paddingBottom);
                this.rowBody.addView(rowBody, rowBodyParams);
                if (i != events.size() -1){
                    this.rowBody.addView(getDivider());
                }

            }
        }else{
            noEvent = new TextView(context);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(context,25));
            noEvent.setText("(No Event)");
            noEvent.setPadding(DensityUtil.dip2px(context,10),0,0,0);
            noEvent.setGravity(Gravity.CENTER_VERTICAL);
            noEvent.setTextSize(titleSize);
            noEvent.setTextColor(getResources().getColor(color_no_event));
            this.rowBody.addView(noEvent, params);
        }
    }

    private List<ITimeEventInterface> filterInputEvents(List<ITimeEventInterface> events){
        boolean unconfirmedIncluded = calendarConfig.unconfirmedIncluded;
        if (unconfirmedIncluded){
            return events;
        }

        List<ITimeEventInterface> results = new ArrayList<>();
        for (ITimeEventInterface event:events
             ) {
            if (!event.isConfirmed()){
                continue;
            }
            results.add(event);
        }

        return results;
    }

    private ImageView getDivider(){
        ImageView dividerImgV;
        //divider
        dividerImgV = new ImageView(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dividerImgV.setLayoutParams(params);
        dividerImgV.setImageDrawable(getResources().getDrawable(rs_divider));
        dividerImgV.setPadding(DensityUtil.dip2px(context, 10),0,0,0);

        return  dividerImgV;
    }

    public interface OnEventClickListener{
        void onEventClick(ITimeEventInterface event);
    }

    public void setOnEventClickListener(OnEventClickListener onEventClickListener){
        this.onEventClickListener = onEventClickListener;
    }
    private int getDatesRelationType(long todayM, long currentDayM){
        // -2 no relation, 1 tomorrow, 0 today, -1 yesterday
        int type = -2;
        int dayM = 24 * 60 * 60 * 1000;
        long diff = (currentDayM - todayM);
        if (diff >0 && diff <= dayM){
            type = 1;
        }else if(diff < 0 && diff >= -dayM){
            type = -1;
        }else if (diff == 0){
            type = 0;
        }

        return type;
    }

    public void setCalendarConfig(CalendarConfig calendarConfig) {
        this.calendarConfig = calendarConfig;
    }
}
