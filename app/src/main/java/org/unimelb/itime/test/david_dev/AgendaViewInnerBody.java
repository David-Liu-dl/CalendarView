package org.unimelb.itime.test.david_dev;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.unimelb.itime.vendor.helper.DensityUtil;
import org.unimelb.itime.vendor.helper.LoadImgHelper;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by yuhaoliu on 31/08/16.
 */
public class AgendaViewInnerBody extends RelativeLayout{
    private final String TAG = "AgendaViewInnerBody";

    private RelativeLayout self = this;

    private LinearLayout leftInfo;
    private LinearLayout rightInfo;
    private LinearLayout pic_list;
    private ImageView eventTypeView;
    private ImageView eventStatusView;

    private TextView leftTimeTv;
    private TextView durationTv;
    private TextView eventNameTv;
    private TextView locationTv;
    private TextView timeLeftTv;

    private float textRegularSize = 13;
    private float textSmallSize = 11;

    private int titleColor = getResources().getColor(org.unimelb.itime.vendor.R.color.title_text_color);
    private int subColor = getResources().getColor(org.unimelb.itime.vendor.R.color.sub_title_text_color);;

    private int pic_height_width;
    private int paddingUpDown;

    final DisplayMetrics dm = getResources().getDisplayMetrics();
    private float screenWidth = dm.widthPixels;

    private Context context;

    private ITimeEventInterface event;

    private String startTime;
    private String duration;
    private String eventName;
    private String location;
    private String timeLeft;

    private int type;
    private int status;
    private int currentDayType;

    private int nowColor;
    private int normalColor;

    private List<String> urls = new ArrayList<>();

    DateFormat date = new SimpleDateFormat("HH:mm a");

    public AgendaViewInnerBody(Context context, ITimeEventInterface event, int currentDayType){
        super(context);
        this.context = context;
        this.currentDayType = currentDayType;
        this.event = event;
        this.pic_height_width = DensityUtil.dip2px(context, 50);
        this.paddingUpDown = DensityUtil.dip2px(context, 2);
        this.nowColor = context.getResources().getColor(org.unimelb.itime.vendor.R.color.time_red);
        this.normalColor = this.subColor;

        initEventShowAttrs(event);
        initAllViews();
    }

    public AgendaViewInnerBody(Context context, AttributeSet attrs, ITimeEventInterface event, int currentDayType) {
        super(context, attrs);
        this.context = context;
        this.currentDayType = currentDayType;
        this.event = event;
        this.pic_height_width = DensityUtil.dip2px(context, 50);
        this.paddingUpDown = DensityUtil.dip2px(context, 2);
        this.nowColor = context.getResources().getColor(org.unimelb.itime.vendor.R.color.time_red);
        this.normalColor = this.subColor;

        initEventShowAttrs(event);
        initAllViews();
    }

    private void initAllViews(){
        //left info
        leftInfo = new LinearLayout(context);
        leftInfo.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout.LayoutParams leftInfoParams = new RelativeLayout.LayoutParams((int) (screenWidth * 0.2), ViewGroup.LayoutParams.WRAP_CONTENT);
        leftInfoParams.addRule(ALIGN_PARENT_LEFT);
        leftInfo.setGravity(Gravity.CENTER_VERTICAL);
        leftInfo.setId(generateViewId());
        this.addView(leftInfo, leftInfoParams);

        leftTimeTv = new TextView(context);
        if (duration.equals("All Day")){

        }else {
            leftTimeTv.setPadding(0,paddingUpDown,0,0);
            leftTimeTv.setText(duration.equals("All Day")?"":startTime);
            leftTimeTv.setTextSize(textSmallSize);
            leftTimeTv.setTextColor(titleColor);
            leftTimeTv.setGravity(Gravity.CENTER);
            leftInfo.addView(leftTimeTv);
        }

        durationTv = new TextView(context);
        durationTv.setText(duration);
        durationTv.setPadding(0,paddingUpDown,0,0);
        durationTv.setTextSize(textSmallSize);
        durationTv.setTextColor(subColor);
        durationTv.setGravity(Gravity.CENTER);
        leftInfo.addView(durationTv);

        //type bar
        eventTypeView = new ImageView(context);
        eventTypeView.setId(generateViewId());
        this.post(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: " + event.getTitle());
                RelativeLayout.LayoutParams eventTypeViewParams = new RelativeLayout.LayoutParams(DensityUtil.dip2px(context, 3), self.getHeight());
                updateLeftBar(getResources().getDrawable(org.unimelb.itime.vendor.R.drawable.itime_draggable_event_bg), getEventColor(type));
                eventTypeViewParams.addRule(RIGHT_OF, leftInfo.getId());
                self.addView(eventTypeView, eventTypeViewParams);

            }
        });


        //right info
        rightInfo = new LinearLayout(context);
        rightInfo.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout.LayoutParams rightInfoParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rightInfoParams.addRule(RIGHT_OF, eventTypeView.getId());
        rightInfo.setPadding(DensityUtil.dip2px(context, 3),0,0,0);
        rightInfo.setId(generateViewId());
        this.addView(rightInfo, rightInfoParams);

        eventNameTv = new TextView(context);
        eventNameTv.setText(eventName);
        eventNameTv.setPadding(0,paddingUpDown,0,0);
        eventNameTv.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
        eventNameTv.setTextSize(textRegularSize);
        eventNameTv.setTextColor(titleColor);
        rightInfo.addView(eventNameTv);

        pic_list = new LinearLayout(context);
        LinearLayout.LayoutParams pic_listParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pic_list.setOrientation(LinearLayout.HORIZONTAL);

        for (String url: this.urls
             ) {
            pic_list.addView(addImage(url));
        }
        rightInfo.addView(pic_list, pic_listParams);

        locationTv = new TextView(context);
        locationTv.setText(location);
        locationTv.setPadding(0,paddingUpDown,0,0);
        locationTv.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
        locationTv.setTextSize(textSmallSize);
        locationTv.setTextColor(subColor);
        rightInfo.addView(locationTv);

        //right bottom time remains
        timeLeftTv = new TextView(context);
        setTimeLeftTv(timeLeftTv);
        timeLeftTv.setGravity(Gravity.CENTER);
        timeLeftTv.setTextSize(textRegularSize);
        timeLeftTv.setPadding(0,0,DensityUtil.dip2px(context,10),DensityUtil.dip2px(context,10));
        RelativeLayout.LayoutParams timeLeftTvParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        timeLeftTvParams.addRule(ALIGN_BOTTOM, rightInfo.getId());
        timeLeftTvParams.addRule(ALIGN_PARENT_RIGHT);
        this.addView(timeLeftTv, timeLeftTvParams);

        //status icon
        eventStatusView = new ImageView(context);
        eventStatusView.setId(generateViewId());
        eventStatusView.setPadding(0,DensityUtil.dip2px(context,10),DensityUtil.dip2px(context,10),0);
        int iconId = getStatusIcon(this.event.getStatus(),true);
        if (iconId != -1){
            eventStatusView.setImageDrawable(context.getResources().getDrawable(iconId));
        }
        RelativeLayout.LayoutParams eventStatusViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        eventStatusViewParams.addRule(ALIGN_TOP, rightInfo.getId());
        eventStatusViewParams.addRule(ALIGN_PARENT_RIGHT);
        this.addView(eventStatusView, eventStatusViewParams);
    }

    private void setTimeLeftTv(TextView timeLeftTv){
        if (this.currentDayType == 0){
            String str = getEventMentionStr(this.event);
            timeLeftTv.setText(str);
            timeLeftTv.setTextColor(str.equals("Now")?nowColor:normalColor);
        }
    }

    private String getEventMentionStr(ITimeEventInterface event){
        Calendar cal = Calendar.getInstance();
        long nowM = cal.getTimeInMillis();
        long eventStartM = event.getStartTime();
        long eventEndM = event.getEndTime();

        if((nowM + 60 * 1000 ) >=  eventEndM){
            return "Ended";
        }else if ((nowM + 60 * 1000 ) >=  eventStartM){
            return "Now";
        }else {
            long timeLeftM = eventStartM - nowM;
            int hoursLeft = (int)timeLeftM/(3600*1000);
            int minutesLeft =(int) ((timeLeftM/(60*1000))%60);
            if (hoursLeft >= 3){
                return "In " + hoursLeft + "hrs";
            }else{
                return
                        hoursLeft == 0 ? "In " + minutesLeft + "min":
                                minutesLeft == 0 ? "In " + hoursLeft + "hrs ":
                                "In " + hoursLeft + "hrs " + minutesLeft + "min";
            }
        }
    }

    private void updateEventInfo(ITimeEventInterface event){

    }

    private int getStatusIcon(int status, boolean useSmallIcon){
        switch (status){
            case 0:
                if (useSmallIcon){
                    return org.unimelb.itime.vendor.R.drawable.itime_question_mark_small;
                }else {
                    return org.unimelb.itime.vendor.R.drawable.itime_question_mark;
                }
            default:
                return -1;
        }
    }

    private ImageView addImage(String url){
        ImageView img = new ImageView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(pic_height_width,pic_height_width);
        img.setPadding(5,5,5,5);
        img.setLayoutParams(params);
        LoadImgHelper.getInstance().bindUrlWithImageView(context, url, img);

        return img;
    }

    private void initEventShowAttrs(ITimeEventInterface event){
        type = event.getEventType();
        status = event.getStatus();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(event.getStartTime());
        Date dateTime = calendar.getTime();
        startTime = date.format(dateTime);
        long duration_m = event.getEndTime() - event.getStartTime();
        int hours = (int)duration_m/(3600*1000);
        int minutes =(int) ((duration_m/(60*1000))%60);
        duration =
                hours >= 24 ? "All Day":
                hours == 0 ? minutes + "min":
                minutes == 0 ?  hours + "hrs ":
                hours + "hrs " + minutes + "min";
        eventName = event.getTitle();
        location = event.getLocation();

        String allUrls = event.getInvitees_urls();
        if (allUrls.contains("|")){
            String[] urls = allUrls.split("\\|");
            for (int i = 0; i < urls.length; i++) {
                this.urls.add(urls[i]);
            }
        }
    }

    private void updateLeftBar(Drawable db, int color){
        eventTypeView.setImageDrawable(db);
        ((GradientDrawable)eventTypeView.getDrawable()).setColor(color);
    }

    private int getEventColor(int type){
        int color;

        switch (type){
            case 0:
                color = getContext().getResources().getColor(org.unimelb.itime.vendor.R.color.private_et);
                break;
            case 1:
                color = getContext().getResources().getColor(org.unimelb.itime.vendor.R.color.group_et);
                break;
            case 2:
                color = getContext().getResources().getColor(org.unimelb.itime.vendor.R.color.public_et);
                break;
            default:
                color = getContext().getResources().getColor(org.unimelb.itime.vendor.R.color.public_et);
        }

        return color;
    }

//        RelativeLayout.LayoutParams eventTypeViewParams = new RelativeLayout.LayoutParams(DensityUtil.dip2px(context, 3), self.getHeight());
//        updateLeftBar(getResources().getDrawable(org.unimelb.itime.vendor.R.drawable.itime_draggable_event_bg), getEventColor(type));
//        eventTypeViewParams.addRule(RIGHT_OF, leftInfo.getId());
//        self.addView(eventTypeView, eventTypeViewParams);
}
