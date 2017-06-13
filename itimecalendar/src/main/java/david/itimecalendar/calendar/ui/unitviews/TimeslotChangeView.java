package david.itimecalendar.calendar.ui.unitviews;

import android.content.Context;
import android.support.annotation.NonNull;
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

import david.itimecalendar.R;
import david.itimecalendar.calendar.listeners.ITimeTimeSlotInterface;
import david.itimecalendar.calendar.util.DensityUtil;

/**
 * Created by yuhaoliu on 1/06/2017.
 */

public class TimeslotChangeView extends LinearLayout {
    private ITimeTimeSlotInterface timeslot;

    private TextView label;

    private RelativeLayout infoContainer;
    private TextView startInfoTv;
    private TextView startTimeTv;

    private ImageView arrowImageV;

    private TextView endInfoTv;
    private TextView endTimeTv;

    private FrameLayout timePickerBlk;
    private TimePicker timePicker;

    private LinearLayout optBlock;
    private TextView cancelBtn;
    private TextView saveBtn;

    public TimeslotChangeView(@NonNull Context context, ITimeTimeSlotInterface timeslotInterface) {
        super(context);
        this.timeslot = timeslotInterface;
        this.initParams();
        this.initViews();
    }

    int labelTextSize;
    int labelContainerHeight;

    int timeInfoTextSize;
    int timeTextSize;
    int timeBlockMarginLR;
    int timeBlockMarginTB;
    int timeContainerHeight;

    int btnTextSize;
    int btnContainerHeight;

    private void initParams(){
        Context context = getContext();
        labelTextSize = 17;
        labelContainerHeight = DensityUtil.dip2px(context,50);

        timeInfoTextSize = 12;
        timeTextSize = 20;
        timeContainerHeight = DensityUtil.dip2px(context,80);
        timeBlockMarginLR = DensityUtil.dip2px(context,35);
        timeBlockMarginTB = DensityUtil.dip2px(context,20);

        btnTextSize = 15;
        btnContainerHeight = DensityUtil.dip2px(context,50);
    }

    private void initViews(){
        this.setOrientation(VERTICAL);
        Context context = getContext();

        label = new TextView(context);
        FrameLayout.LayoutParams labelParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, labelContainerHeight);
        label.setLayoutParams(labelParams);
        label.setGravity(Gravity.CENTER);
        label.setText("Edit Timeslot");
        label.setTextSize(labelTextSize);
        this.addView(label);

        infoContainer = new RelativeLayout(context);
        infoContainer.setPadding(timeBlockMarginLR,timeBlockMarginTB,timeBlockMarginLR,timeBlockMarginTB);
        FrameLayout.LayoutParams infoContainerParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, timeContainerHeight);
        infoContainer.setLayoutParams(infoContainerParams);
        this.addView(infoContainer);

        startInfoTv = new TextView(context);
        RelativeLayout.LayoutParams startInfoTvParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        startInfoTvParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        startInfoTvParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        startInfoTv.setLayoutParams(startInfoTvParams);
        startInfoTv.setText("Start At");
        startInfoTv.setTextSize(timeInfoTextSize);
        this.infoContainer.addView(startInfoTv);

        startTimeTv = new TextView(context);
        RelativeLayout.LayoutParams startTimeTvParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        startTimeTvParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        startTimeTvParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        startTimeTv.setLayoutParams(startTimeTvParams);
        startTimeTv.setText("12:00");
        startTimeTv.setTextSize(timeTextSize);
        this.infoContainer.addView(startTimeTv);

        endInfoTv = new TextView(context);
        RelativeLayout.LayoutParams endInfoTvParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        endInfoTvParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        endInfoTvParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        endInfoTv.setLayoutParams(endInfoTvParams);
        endInfoTv.setText("End At");
        endInfoTv.setTextSize(timeInfoTextSize);
        this.infoContainer.addView(endInfoTv);

        endTimeTv = new TextView(context);
        RelativeLayout.LayoutParams endTimeTvParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        endTimeTvParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        endTimeTvParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        endTimeTv.setLayoutParams(endTimeTvParams);
        endTimeTv.setText("14:00");
        endTimeTv.setTextSize(timeTextSize);
        this.infoContainer.addView(endTimeTv);

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

        optBlock = new LinearLayout(context);
        optBlock.setOrientation(HORIZONTAL);
        FrameLayout.LayoutParams optBlockParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, btnContainerHeight);
        optBlock.setLayoutParams(optBlockParams);
        this.addView(optBlock);

        cancelBtn = new TextView(context);
        LinearLayout.LayoutParams cancelBtnParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,1f);
        cancelBtn.setLayoutParams(cancelBtnParams);
        cancelBtn.setText("Cancel");
        cancelBtn.setTextSize(btnTextSize);
        cancelBtn.setGravity(Gravity.CENTER);
        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPopupWindowListener != null){
                    onPopupWindowListener.onCancel();
                }
            }
        });
        optBlock.addView(cancelBtn);

        saveBtn = new TextView(context);
        LinearLayout.LayoutParams saveBtnParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,1f);
        saveBtn.setLayoutParams(saveBtnParams);
        saveBtn.setText("Save");
        saveBtn.setTextSize(btnTextSize);
        saveBtn.setGravity(Gravity.CENTER);
        saveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timeslot != null && onPopupWindowListener != null){
                    int hour = timePicker.getCurrentHour();
                    int minute = timePicker.getCurrentMinute();
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(timeslot.getStartTime());
                    cal.set(Calendar.HOUR_OF_DAY, hour);
                    cal.set(Calendar.MINUTE, minute);
                    onPopupWindowListener.onSave(cal.getTimeInMillis());
                }
            }
        });
        optBlock.addView(saveBtn);

        updateTimeInfo();
    }

    OnPopupWindowListener onPopupWindowListener;

    public void setOnPopupWindowListener(OnPopupWindowListener onPopupWindowListener) {
        this.onPopupWindowListener = onPopupWindowListener;
    }

    public interface OnPopupWindowListener{
        void onCancel();
        void onSave(long startTime);
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
}
