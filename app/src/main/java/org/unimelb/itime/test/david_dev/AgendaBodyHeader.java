package org.unimelb.itime.test.david_dev;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.unimelb.itime.vendor.helper.DensityUtil;
import org.unimelb.itime.vendor.helper.MyCalendar;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by yuhaoliu on 6/09/16.
 */
public class AgendaBodyHeader extends LinearLayout {

    private LinearLayout contentLayout;

    private TextView mentionTv;
    private TextView nthTv;
    private TextView monthTv;
    private TextView dayOfWeekTv;

    private String mention = "";
    private String nth;
    private String month;
    private String dayOfWeek;

    private int titleSize = 12;
    private int titleColor;
    private int titleBgColor;
    private int textPadding;

    private MyCalendar myCalendar = new MyCalendar(Calendar.getInstance());
    private int currentDayType = -2;
    private Context context;

    public AgendaBodyHeader(Context context) {
        super(context);
        this.context = context;
        RelativeLayout.LayoutParams rowHeaderParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(rowHeaderParams);
        this.setOrientation(LinearLayout.VERTICAL);

        this.contentLayout = new LinearLayout(context);
        contentLayout.setOrientation(LinearLayout.HORIZONTAL);
        RelativeLayout.LayoutParams contentLayoutParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(context,30));
        contentLayout.setPadding(DensityUtil.dip2px(context, 10),0,DensityUtil.dip2px(context, 10),0);
        contentLayout.setLayoutParams(contentLayoutParams);
        contentLayout.setGravity(Gravity.CENTER_VERTICAL);
        this.initHeaderTvs();

        this.addView(contentLayout,contentLayoutParams);
        this.addView(getDivider());
    }

    public void setMyCalendar(MyCalendar myCalendar){
        this.myCalendar = myCalendar;
        this.updateHeaderView();
    }

    public void updateHeaderView(){
        initHeaderShowAttrs();

        this.setBackgroundColor(titleBgColor);

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

        this.invalidate();
    }

//    public RelativeLayout.LayoutParams getRLayoutParams(){
//        return (RelativeLayout.LayoutParams) this.getLayoutParams();
//    }

    private void initHeaderTvs(){
        textPadding = DensityUtil.dip2px(context, 10);

        mentionTv = new TextView(context);
        mentionTv.setTypeface(null, Typeface.BOLD);
        mentionTv.setPadding(0,0,textPadding,0);
        contentLayout.addView(mentionTv);

        nthTv = new TextView(context);
        nthTv.setPadding(0,0,textPadding,0);
        contentLayout.addView(nthTv);

        monthTv = new TextView(context);
        monthTv.setPadding(0,0,textPadding,0);
        contentLayout.addView(monthTv);

        dayOfWeekTv = new TextView(context);
        dayOfWeekTv.setPadding(0,0,textPadding,0);
        contentLayout.addView(dayOfWeekTv);
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

    private ImageView getDivider(){
        ImageView dividerImgV;
        //divider
        dividerImgV = new ImageView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dividerImgV.setLayoutParams(params);
        dividerImgV.setImageDrawable(getResources().getDrawable(org.unimelb.itime.vendor.R.drawable.itime_header_divider_line));
        dividerImgV.setPadding(0,0,0,0);

        return  dividerImgV;
    }
}
