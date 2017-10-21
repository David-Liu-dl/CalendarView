package david.itimecalendar.calendar.ui.unitviews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import david.itimecalendar.R;
import david.itimecalendar.calendar.listeners.ITimeTimeSlotInterface;
import david.itimecalendar.calendar.util.DensityUtil;

/**
 * Created by yuhaoliu on 1/06/2017.
 */

public class TimeslotEditView extends LinearLayout {
    private ITimeTimeSlotInterface timeslot;

    private RelativeLayout infoContainer;
    private TextView startInfoTv;
    private TextView startTimeTv;

    private ImageView arrowImageV;

    private TextView endInfoTv;
    private TextView endTimeTv;

    private FrameLayout timePickerBlk;
    private TimePicker timePicker;

    public TimeslotEditView(@NonNull Context context, ITimeTimeSlotInterface timeslotInterface) {
        super(context);
        this.timeslot = timeslotInterface;
        this.initParams();
        this.initViews();
    }

    int timeInfoTextSize;
    int timeTextSize;

    int btnTextSize;
    int btnContainerHeight;

    private void initParams(){
        Context context = getContext();

        timeInfoTextSize = 14;
        timeTextSize = 20;

        btnTextSize = 16;
        btnContainerHeight = DensityUtil.dip2px(context,50);
    }

    private void initViews(){
        this.setOrientation(VERTICAL);
        Context context = getContext();

        infoContainer = new RelativeLayout(context);
        FrameLayout.LayoutParams infoContainerParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        infoContainer.setLayoutParams(infoContainerParams);
        this.addView(infoContainer);

        arrowImageV = new ImageView(getContext());
        arrowImageV.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_time_arrow));
        int arrowImageVSize = DensityUtil.dip2px(context,20);
        RelativeLayout.LayoutParams arrowImageVParams = new RelativeLayout.LayoutParams(arrowImageVSize,arrowImageVSize);
        arrowImageVParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        arrowImageV.setLayoutParams(arrowImageVParams);
        infoContainer.addView(arrowImageV);

        LinearLayout timeInfoContainer = new LinearLayout(getContext());
        timeInfoContainer.setOrientation(LinearLayout.HORIZONTAL);
        timeInfoContainer.setWeightSum(2.0f);
        RelativeLayout.LayoutParams timeInfoContainerParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        infoContainer.addView(timeInfoContainer,timeInfoContainerParams);

        LinearLayout leftTimeInfoFrame = new LinearLayout(context);
        leftTimeInfoFrame.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams leftTimeInfoFrameParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1f);
        timeInfoContainer.addView(leftTimeInfoFrame,leftTimeInfoFrameParams);

        startInfoTv = new TextView(context);
        LinearLayout.LayoutParams startInfoTvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        startInfoTv.setLayoutParams(startInfoTvParams);
        startInfoTv.setGravity(Gravity.CENTER);
        startInfoTv.setText(getDateText(timeslot.getStartTime()));
        startInfoTv.setTextSize(timeInfoTextSize);
        leftTimeInfoFrame.addView(startInfoTv);

        startTimeTv = new TextView(context);
        LinearLayout.LayoutParams startTimeTvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        startTimeTv.setLayoutParams(startTimeTvParams);
        startTimeTv.setGravity(Gravity.CENTER);
        startTimeTv.setText("12:00");
        startTimeTv.setTextColor(ContextCompat.getColor(context,R.color.brand_main));
        startTimeTv.setTextSize(timeTextSize);
        leftTimeInfoFrame.addView(startTimeTv);

        LinearLayout rightTimeInfoFrame = new LinearLayout(context);
        rightTimeInfoFrame.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams rightTimeInfoFrameParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1f);
        timeInfoContainer.addView(rightTimeInfoFrame,rightTimeInfoFrameParams);

        endInfoTv = new TextView(context);
        LinearLayout.LayoutParams endInfoTvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        endInfoTv.setLayoutParams(endInfoTvParams);
        endInfoTv.setGravity(Gravity.CENTER);
        endInfoTv.setTextColor(ContextCompat.getColor(context,R.color.timeslot_edit_menu_grey));
        endInfoTv.setText(getDateText(timeslot.getEndTime()));
        endInfoTv.setTextSize(timeInfoTextSize);
        rightTimeInfoFrame.addView(endInfoTv);

        endTimeTv = new TextView(context);
        LinearLayout.LayoutParams endTimeTvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        endTimeTv.setLayoutParams(endTimeTvParams);
        endTimeTv.setGravity(Gravity.CENTER);
        endTimeTv.setText("14:00");
        endTimeTv.setTextColor(ContextCompat.getColor(context,R.color.timeslot_edit_menu_grey));
        endTimeTv.setTextSize(timeTextSize);
        rightTimeInfoFrame.addView(endTimeTv);

        timePickerBlk = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.widget_timepicker_spinner, this, false);
        FrameLayout.LayoutParams timePickerParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        timePickerBlk.setLayoutParams(timePickerParams);
        this.addView(timePickerBlk);

        timePicker = (TimePicker) timePickerBlk.findViewById(R.id.timepicker);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeslot.getStartTime());
        timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                updateTimeInfo();
            }
        });

        updateTimeInfo();
    }

    public long getCurrentSelectedStartTime(){
        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeslot.getStartTime());
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);

        return cal.getTimeInMillis();
    }

    private void updateTimeInfo(){
        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeslot.getStartTime());
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);

        String startTime = new SimpleDateFormat("HH:mm").format(cal.getTime());
        cal.setTimeInMillis(cal.getTimeInMillis() + (timeslot.getEndTime() - timeslot.getStartTime()));
        String endTime = new SimpleDateFormat("HH:mm").format(cal.getTime());

        startTimeTv.setText(startTime);
        endTimeTv.setText(endTime);
    }

    private String getDateText(long time){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        String monthTh =  cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, getResources().getConfiguration().locale);
        String dayOfMonth = cal.get(Calendar.DAY_OF_MONTH) + "";
        String dayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, getResources().getConfiguration().locale);

        return dayOfWeek + ", " + dayOfMonth + " " + monthTh;
    }
}
