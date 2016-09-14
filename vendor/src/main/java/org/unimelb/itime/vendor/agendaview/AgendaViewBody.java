package org.unimelb.itime.vendor.agendaview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.unimelb.itime.vendor.agendaview.AgendaBodyHeader;
import org.unimelb.itime.vendor.helper.DensityUtil;
import org.unimelb.itime.vendor.helper.MyCalendar;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by yuhaoliu on 31/08/16.
 */
public class AgendaViewBody extends LinearLayout{
    private final String TAG = "MyAPP2";

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
        this.events.clear();
        if (events != null){
            this.events = eventList;
        }
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
        LinearLayout.LayoutParams rowBodyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        this.addView(rowBody, rowBodyParams);
    }

    private void displayEvents(final List<ITimeEventInterface> events){
        this.rowBody.removeAllViews();

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
                LinearLayout.LayoutParams rowBodyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                this.rowBody.addView(rowBody, rowBodyParams);
                if (i != events.size() -1){
                    this.rowBody.addView(getDivider());
                }

            }
        }else{
            noEvent = new TextView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(context,50));
            noEvent.setText("No Event");
            noEvent.setPadding(DensityUtil.dip2px(context,10),0,0,0);
            noEvent.setGravity(Gravity.CENTER_VERTICAL);
            noEvent.setTextSize(titleSize);
            noEvent.setTextColor(getResources().getColor(org.unimelb.itime.vendor.R.color.text_enable));
            this.rowBody.addView(noEvent, params);
        }
    }

    private ImageView getDivider(){
        ImageView dividerImgV;
        //divider
        dividerImgV = new ImageView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dividerImgV.setLayoutParams(params);
        dividerImgV.setImageDrawable(getResources().getDrawable(org.unimelb.itime.vendor.R.drawable.itime_header_divider_line));
        dividerImgV.setPadding(DensityUtil.dip2px(context, 5),0,0,0);

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

}
