package org.unimelb.itime.test.david_dev;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.unimelb.itime.vendor.helper.DensityUtil;
import org.unimelb.itime.vendor.helper.MyCalendar;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by yuhaoliu on 31/08/16.
 */
public class AgendaViewBody extends LinearLayout{
    private final String TAG = "AgendaViewBody";

    private MyCalendar myCalendar;
    private AgendaBodyHeader rowHeader;
    private LinearLayout rowBody;

    private List<ITimeEventInterface> events = new ArrayList<>();

    private TextView noEvent;

    private int titleSize = 12;

    private Context context;
    private OnLoadEvents onLoadEvents;

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

    public void setOnLoadEvents(OnLoadEvents onLoadEvents){
        this.onLoadEvents = onLoadEvents;
    }

    public void loadEvents(){
        if (this.onLoadEvents != null){
            this.events.clear();
            List<ITimeEventInterface> events = this.onLoadEvents.loadTodayEvents(myCalendar.getBeginOfDayMilliseconds());
            if (events != null){
                this.events.addAll(events);
                Log.i(TAG, "loadEvents: " + events.size());
            }
            displayEvents(this.events);
        }
    }

    public void updateHeaderView(){
        this.rowHeader.updateHeaderView();
    }

    private void initLayouts(){
        //header
        rowHeader = new AgendaBodyHeader(context);
        this.addView(rowHeader);

        //add dividerLine
//        this.addView(getDivider());

        //body
        rowBody = new LinearLayout(context);
        rowBody.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams rowBodyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        this.addView(rowBody, rowBodyParams);
    }

    private void displayEvents(List<ITimeEventInterface> events){
        this.rowBody.removeAllViews();

        if (events.size() != 0){
            for (int i = 0; i < events.size(); i++) {
                AgendaViewInnerBody rowBody = new AgendaViewInnerBody(context, events.get(i), this.currentDayType);
                LinearLayout.LayoutParams rowBodyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                this.rowBody.addView(rowBody, rowBodyParams);
                if (i != events.size() -1){
                    this.rowBody.addView(getDivider());
                }

//                bodyRows.add(rowBody);
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

    public interface OnLoadEvents{
        List<ITimeEventInterface> loadTodayEvents(long beginOfDayMilliseconds);
    }

}
