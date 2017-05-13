package david.itimecalendar.calendar.unitviews;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.ITimeInnerCalendar;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import david.itimecalendar.R;
import david.itimecalendar.calendar.util.DensityUtil;

/**
 * Created by yuhaoliu on 2/05/2017.
 */

public class TimeSlotInnerCalendarView extends LinearLayout {
    private Context context;

    private LinearLayout btnBlock;
    private TextView monthTitle;
    private ImageView indicator;
    private RotateAnimation showIndicatorAnim;
    private RotateAnimation hideIndicatorAnim;

    private ITimeInnerCalendar calendarView;

    private OnTimeSlotInnerCalendar onTimeSlotInnerCalendar;
    private int headerHeight;

    public TimeSlotInnerCalendarView(Context context) {
        super(context);
        this.context = context;
        intViews();
    }

    public TimeSlotInnerCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        intViews();
    }

    public TimeSlotInnerCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        intViews();
    }

    public void setHeaderHeight(int headerHeight) {
        this.headerHeight = headerHeight;
        this.btnBlock.getLayoutParams().height = this.headerHeight;
    }

    public void setSlotNumMap(HashMap<String, Integer> slotNumMap) {
        this.calendarView.setSlotNumMap(slotNumMap);
    }

    private void intViews(){
        this.setOrientation(VERTICAL);
        initBtnBlock();
        initITimeInnerCalendar();
        initListeners();
        initAnimations();
    }

    private void initBtnBlock(){
        int leftBarWidth = DensityUtil.dip2px(context,70);
        int leftBarHeight = headerHeight;

        btnBlock = new LinearLayout(getContext());
        btnBlock.setBackgroundColor(getResources().getColor(R.color.white));
        btnBlock.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams leftBtnParams = new LayoutParams(leftBarWidth, leftBarHeight);

        int leftBarWidgetPadding = DensityUtil.dip2px(getContext(),10);
        int monthTitleWidth = DensityUtil.dip2px(getContext(),35);
        monthTitle = new TextView(getContext());
        LayoutParams monthTitleParams = new LayoutParams(monthTitleWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        monthTitle.setText("April" + ".");
        monthTitle.setGravity(Gravity.CENTER);
        monthTitleParams.leftMargin = leftBarWidgetPadding;
        monthTitleParams.rightMargin = leftBarWidgetPadding;
        btnBlock.addView(monthTitle,monthTitleParams);

        indicator = new ImageView(getContext());
        int triangleSize = DensityUtil.dip2px(getContext(),10);

        indicator.setImageDrawable(getResources().getDrawable(R.drawable.triangle));
        LayoutParams indicatorParams = new LayoutParams(triangleSize, triangleSize);
        indicatorParams.gravity = Gravity.CENTER;
        btnBlock.addView(indicator,indicatorParams);
        this.addView(btnBlock, leftBtnParams);
        addDivider();
    }

    private void initITimeInnerCalendar(){
        this.calendarView = (ITimeInnerCalendar) LayoutInflater.from(context).inflate(R.layout.itime_timeslot_inner_calendar, null);
        LayoutParams calParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.calendarView.setOnBgClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideCalendar();
            }
        });
        this.calendarView.setVisibility(GONE);

        this.addView(calendarView,calParams);
    }

    private void initAnimations(){
        showIndicatorAnim = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        showIndicatorAnim.setDuration(200);
        showIndicatorAnim.setFillAfter(true);
        showIndicatorAnim.setInterpolator(new LinearInterpolator());

        hideIndicatorAnim = new RotateAnimation(180, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        hideIndicatorAnim.setFillAfter(true);
        hideIndicatorAnim.setDuration(200);
        hideIndicatorAnim.setInterpolator(new LinearInterpolator());
    }

    private void initListeners(){
        btnBlock.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calendarView.getVisibility() != VISIBLE){
                    showCalendar();
                }else {
                   hideCalendar();
                }

                if (onTimeSlotInnerCalendar != null){
                    onTimeSlotInnerCalendar.onCalendarBtnClick(v,!(calendarView.getVisibility()==INVISIBLE));
                }
            }
        });

        this.calendarView.setBodyListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                if (onTimeSlotInnerCalendar != null){
                    onTimeSlotInnerCalendar.onDayClick(dateClicked);
                }
                hideCalendarFadeOut();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                if (onTimeSlotInnerCalendar != null){
                    onTimeSlotInnerCalendar.onMonthScroll(firstDayOfNewMonth);
                }
            }
        });
    }

    private void showCalendar(){
        calendarView.setVisibility(VISIBLE);
        YoYo.with(Techniques.FadeInDown)
                .duration(200)
                .playOn(calendarView);
        indicator.startAnimation(showIndicatorAnim);
    }

    private void hideCalendar(){
        YoYo.with(Techniques.FadeOutUp)
                .duration(200)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        calendarView.setVisibility(GONE);
                    }
                })
                .playOn(calendarView);
        indicator.startAnimation(hideIndicatorAnim);
    }

    private void hideCalendarFadeOut(){
        YoYo.with(Techniques.FadeOut)
                .duration(100)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        calendarView.setVisibility(GONE);
                    }
                })
                .playOn(calendarView);
        indicator.startAnimation(hideIndicatorAnim);
    }

    private void addDivider(){
        ImageView dividerImgV;
        //divider
        dividerImgV = new ImageView(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dividerImgV.setLayoutParams(params);
        dividerImgV.setImageDrawable(getResources().getDrawable(R.drawable.itime_header_divider_line));
        this.addView(dividerImgV);
    }

    public void setMonthTitle(Calendar calendar){
        String monthName = calendar.getDisplayName(Calendar.MONTH,Calendar.SHORT, Locale.getDefault());
        monthTitle.setText(monthName + ".");
    }

    public OnTimeSlotInnerCalendar getOnTimeSlotInnerCalendar() {
        return onTimeSlotInnerCalendar;
    }

    public void setOnTimeSlotInnerCalendar(OnTimeSlotInnerCalendar onTimeSlotInnerCalendar) {
        this.onTimeSlotInnerCalendar = onTimeSlotInnerCalendar;
    }

    public void setCurrentDate(Date currentDate){
        this.calendarView.setCurrentDate(currentDate);
    }

    public void refreshSlotNum(){
        calendarView.setWillNotDraw(false);
        calendarView.refreshSlotNum();
    }

    public interface OnTimeSlotInnerCalendar{
        void onCalendarBtnClick(View v, boolean result);
        void onDayClick(Date dateClicked);
        void onMonthScroll(Date firstDayOfNewMonth);
    }
}
