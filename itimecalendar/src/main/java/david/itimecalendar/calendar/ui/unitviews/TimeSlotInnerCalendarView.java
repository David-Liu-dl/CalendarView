package david.itimecalendar.calendar.ui.unitviews;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.easing.linear.Linear;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.ITimeTimeslotCalendar;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import david.itimecalendar.R;
import david.itimecalendar.calendar.util.DensityUtil;

/**
 * Created by yuhaoliu on 2/05/2017.
 */

public class TimeSlotInnerCalendarView extends LinearLayout {
    private Context context;

    private LinearLayout btnBlock;
    private LinearLayout titleBlock;
    private RelativeLayout calTitleBar;
    private TextView titleTv;

    private TextView monthTitle;
    private ImageView indicator;
    private RotateAnimation showIndicatorAnim;
    private RotateAnimation hideIndicatorAnim;

    private ITimeTimeslotCalendar calendarView;
    private OnTimeSlotInnerCalendar onTimeSlotInnerCalendar;

    private int headerHeight;
    public Calendar calendar = Calendar.getInstance();

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
        this.titleBlock.getLayoutParams().height = this.headerHeight;
    }

    public void setSlotNumMap(ITimeTimeslotCalendar.InnerCalendarTimeslotPackage innerSlotPackage) {
        this.calendarView.setSlotNumMap(innerSlotPackage);
    }

    public void setCurrentDate(Date date){
        calendar.setTime(date);
        calendarView.setCurrentDate(calendar.getTime());
        String monthName = calendar.getDisplayName(Calendar.MONTH,Calendar.SHORT, Locale.getDefault());
        monthTitle.setText(monthName);
    }

    private void intViews(){
        this.setOrientation(VERTICAL);
        initBtnBlock();
        initITimeInnerCalendar();
        initListeners();
        initAnimations();
    }

    private void initBtnBlock(){
        titleBlock = new LinearLayout(getContext());
        titleBlock.setOrientation(HORIZONTAL);
        titleBlock.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, headerHeight));
        this.addView(titleBlock);

        int leftBarWidth = DensityUtil.dip2px(context,70);
        btnBlock = new LinearLayout(getContext());
        btnBlock.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams leftBtnParams = new LayoutParams(leftBarWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        titleBlock.addView(btnBlock, leftBtnParams);

        int leftBarWidgetPadding = DensityUtil.dip2px(getContext(),10);
        int monthTitleWidth = DensityUtil.dip2px(getContext(),35);
        monthTitle = new TextView(getContext());
        monthTitle.setAllCaps(true);
        monthTitle.setTextColor(ContextCompat.getColor(context, R.color.brand_main));
//        monthTitle.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        LayoutParams monthTitleParams = new LayoutParams(monthTitleWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        monthTitle.setGravity(Gravity.CENTER);
        monthTitleParams.leftMargin = leftBarWidgetPadding;
        monthTitleParams.rightMargin = leftBarWidgetPadding;
        btnBlock.addView(monthTitle,monthTitleParams);

        indicator = new ImageView(getContext());
        int triangleSize = DensityUtil.dip2px(getContext(),10);

        indicator.setImageDrawable(getResources().getDrawable(R.drawable.icon_calendar_triangle_blue));
        LayoutParams indicatorParams = new LayoutParams(triangleSize, triangleSize);
        indicatorParams.gravity = Gravity.CENTER;
        btnBlock.addView(indicator,indicatorParams);

        initCalendarTitleBar();
    }

    private void initCalendarTitleBar(){
        this.calTitleBar = new RelativeLayout(getContext()){
            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return false;
            }

            @Override
            public boolean onTouchEvent(MotionEvent event) {
                return true;
            }
        };

        this.calTitleBar.setBackgroundColor(Color.WHITE);
        this.calTitleBar.setId(View.generateViewId());
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        calTitleBar.setLayoutParams(containerParams);
        titleBlock.addView(calTitleBar);

        int indicatorSize = DensityUtil.dip2px(context,15);

        ImageView rightIcon = new ImageView(getContext());
        rightIcon.setId(generateViewId());
        rightIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon_calendar_arrowright));
        RelativeLayout.LayoutParams rightIconParams = new RelativeLayout.LayoutParams(indicatorSize, indicatorSize);
        rightIconParams.rightMargin = DensityUtil.dip2px(context, 20);
        rightIconParams.addRule(RelativeLayout.CENTER_VERTICAL);
        rightIconParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        rightIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarView.getCalendarView().showNextMonth();
                updateTitle(calendarView.getCalendarView().getFirstDayOfCurrentMonth());
            }
        });

        ImageView leftIcon = new ImageView(getContext());
        leftIcon.setId(generateViewId());
        leftIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon_calendar_arrowleft));
        RelativeLayout.LayoutParams leftIconParams = new RelativeLayout.LayoutParams(indicatorSize, indicatorSize);
        leftIconParams.rightMargin = DensityUtil.dip2px(context,50);
        leftIconParams.addRule(RelativeLayout.CENTER_VERTICAL);
        leftIconParams.addRule(RelativeLayout.LEFT_OF,rightIcon.getId());
        calTitleBar.addView(leftIcon,leftIconParams);
        leftIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarView.getCalendarView().showPreviousMonth();
                updateTitle(calendarView.getCalendarView().getFirstDayOfCurrentMonth());
            }
        });

        titleTv = new TextView(getContext());
        titleTv.setId(View.generateViewId());
        titleTv.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams titleTvParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleTvParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        titleTvParams.addRule(RelativeLayout.CENTER_VERTICAL);
        titleTvParams.addRule(RelativeLayout.LEFT_OF, leftIcon.getId());
        calTitleBar.addView(titleTv,titleTvParams);
        updateTitle(new Date());

        calTitleBar.addView(rightIcon,rightIconParams);
        calTitleBar.setVisibility(GONE);
    }

    private void initITimeInnerCalendar(){
        this.calendarView = (ITimeTimeslotCalendar) LayoutInflater.from(context).inflate(R.layout.itime_timeslot_inner_calendar, null);
        LayoutParams calParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.calendarView.getCalendarView().setUseThreeLetterAbbreviation(true);
        this.calendarView.getCalendarView().setFirstDayOfWeek(Calendar.SUNDAY);
        this.calendarView.setOnBgClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBlock.callOnClick();
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
                    setCurrentDate(calendar.getTime());
                    showCalendar();
                }else {
                    //set title to actual date of calendar
                    updateTitle(calendar.getTime());
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
                setCurrentDate(dateClicked);
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

    private void updateTitle(Date date){
        Calendar cal = Calendar.getInstance();
        String currentYearName = String.valueOf(cal.get(Calendar.YEAR));
        cal.setTime(date);
        String targetYearName = String.valueOf(cal.get(Calendar.YEAR));
        String dateStr = currentYearName.equals(targetYearName) ? "" : targetYearName;

        String monthName = cal.getDisplayName(Calendar.MONTH,Calendar.SHORT,Locale.getDefault());
        monthTitle.setText(monthName);
        titleTv.setText(dateStr);
    }

    private void showCalendar(){
        calTitleBar.setVisibility(VISIBLE);
        calendarView.setVisibility(VISIBLE);
        YoYo.with(Techniques.FadeInDown)
                .duration(200)
                .playOn(calendarView);
        YoYo.with(Techniques.FadeInDown)
                .duration(200)
                .playOn(calTitleBar);
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
        YoYo.with(Techniques.FadeOutUp)
                .duration(200)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        calTitleBar.setVisibility(GONE);
                    }
                })
                .playOn(calTitleBar);
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
        YoYo.with(Techniques.FadeOut)
                .duration(200)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        calTitleBar.setVisibility(GONE);
                    }
                })
                .playOn(calTitleBar);
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

    public OnTimeSlotInnerCalendar getOnTimeSlotInnerCalendar() {
        return onTimeSlotInnerCalendar;
    }

    public void setOnTimeSlotInnerCalendar(OnTimeSlotInnerCalendar onTimeSlotInnerCalendar) {
        this.onTimeSlotInnerCalendar = onTimeSlotInnerCalendar;
    }

    public void refreshSlotNum(){
        calendarView.setWillNotDraw(false);
        calendarView.refreshSlotNum();
    }

    public interface OnTimeSlotInnerCalendar extends CompactCalendarView.CompactCalendarViewListener{
        void onCalendarBtnClick(View v, boolean result);
    }
}
