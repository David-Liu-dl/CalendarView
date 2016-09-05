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
    private LinearLayout rowHeader;
    private LinearLayout rowBody;

    private int totalHeight;
    private int totalWidth;

//    private List<AgendaInnerBody> bodyRows = new ArrayList<>();
    private List<ITimeEventInterface> events = new ArrayList<>();

    private TextView mentionTv;
    private TextView nthTv;
    private TextView monthTv;
    private TextView dayOfWeekTv;

    private String mention = "";
    private String nth;
    private String month;
    private String dayOfWeek;

    private TextView noEvent;

    private int titleSize = 12;
    private int titleColor;
    private int titleBgColor;
    private int textPadding;

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
        initHeaderShowAttrs();

        rowHeader.setBackgroundColor(titleBgColor);

        mentionTv.setText(mention);
        mentionTv.setTextColor(titleColor);
        mentionTv.setTextSize(titleSize);

        nthTv.setText(nth);
        nthTv.setTextColor(titleColor);
        nthTv.setTextSize(titleSize);

        monthTv.setText(month);
        monthTv.setTextColor(titleColor);
        monthTv.setTextSize(titleSize);

        dayOfWeekTv.setText(dayOfWeek);
        dayOfWeekTv.setTextColor(titleColor);
        dayOfWeekTv.setTextSize(titleSize);

        rowHeader.invalidate();
    }

    private void initLayouts(){
        //header
        rowHeader = new LinearLayout(context);
        rowHeader.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rowHeaderParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        rowHeader.setPadding(DensityUtil.dip2px(context, 10),DensityUtil.dip2px(context, 5),DensityUtil.dip2px(context, 10),DensityUtil.dip2px(context, 5));
        this.addView(rowHeader, rowHeaderParams);
        //header subviews
        initHeaderTvs();

        //add dividerLine
        this.addView(getDivider());

        //body
        rowBody = new LinearLayout(context);
        rowBody.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams rowBodyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        this.addView(rowBody, rowBodyParams);
    }

    private void initHeaderTvs(){
        textPadding = DensityUtil.dip2px(context, 10);

        mentionTv = new TextView(context);
        mentionTv.setTypeface(null, Typeface.BOLD);
        mentionTv.setPadding(0,0,textPadding,0);
        rowHeader.addView(mentionTv);

        nthTv = new TextView(context);
        nthTv.setPadding(0,0,textPadding,0);
        rowHeader.addView(nthTv);

        monthTv = new TextView(context);
        monthTv.setPadding(0,0,textPadding,0);
        rowHeader.addView(monthTv);

        dayOfWeekTv = new TextView(context);
        dayOfWeekTv.setPadding(0,0,textPadding,0);
        rowHeader.addView(dayOfWeekTv);
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

    private void initHeaderShowAttrs(){
        Calendar calendar = this.myCalendar.getCalendar();
        int day_of_month = calendar.get(Calendar.DAY_OF_MONTH);
        nth =  day_of_month + "th";
        month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

        mentionTv.setPadding(0,0,textPadding,0);
        titleColor = getResources().getColor(org.unimelb.itime.vendor.R.color.title_text_color);
        titleBgColor = getResources().getColor(org.unimelb.itime.vendor.R.color.title_bg_color);

        Calendar todayCal = Calendar.getInstance();

        long current_day_milliseconds = this.myCalendar.getBeginOfDayMilliseconds();
        long today_milliseconds = (new MyCalendar(todayCal)).getBeginOfDayMilliseconds();
        this.currentDayType = getDatesRelationType(today_milliseconds, current_day_milliseconds);

        switch (currentDayType){
            case 1:
                mention = "Tomorrow";
                break;
            case 0:
                mention = "Today";
                titleColor = getResources().getColor(org.unimelb.itime.vendor.R.color.time_red);
                titleBgColor = getResources().getColor(org.unimelb.itime.vendor.R.color.title_today_bg_color);
                break;
            case -1:
                mention = "Yesterday";
                break;
            default:
                mention = "";
                mentionTv.setPadding(0,0,0,0);
                break;
        }
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

    public interface OnLoadEvents{
        List<ITimeEventInterface> loadTodayEvents(long beginOfDayMilliseconds);
    }

}
