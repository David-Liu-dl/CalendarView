package david.itimecalendar.calendar.calendar.mudules.weekview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import david.itimecalendar.R;
import david.itimecalendar.calendar.util.MyCalendar;

import static android.widget.LinearLayout.VERTICAL;

/**
 * Created by yuhaoliu on 29/05/2017.
 */

public class WeekViewHeaderCell extends FrameLayout {
    private int text_calendar_weekdate = R.color.text_calendar_weekdate;
    private int dayOfMonthTextSize = 16;
    private int dayOfWeekTextSize = 11;

    TextView dayOfWeekTv;
    TextView dayOfMonthTv;
    private LinearLayout container;

    MyCalendar calendar;

    public WeekViewHeaderCell(Context context) {
        super(context);
        init();
    }

    public WeekViewHeaderCell(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WeekViewHeaderCell(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MyCalendar getCalendar() {
        return calendar;
    }

    public void setCalendar(MyCalendar calendar) {
        this.calendar = calendar;
        updateDate(calendar.getCalendar());
    }

    private void init(){
        container = new LinearLayout(getContext());
        container.setOrientation(VERTICAL);

        dayOfWeekTv = new TextView(getContext());
        dayOfWeekTv.setTextSize(dayOfWeekTextSize);
        dayOfWeekTv.setTextColor(getResources().getColor(text_calendar_weekdate));
        dayOfWeekTv.setText("WED");
        dayOfWeekTv.measure(0, 0);
        dayOfWeekTv.setGravity(Gravity.CENTER);
        final int dayOfWeekTvMeasuredHeight = dayOfWeekTv.getMeasuredHeight(); //get height
        final int dayOfWeekTvMeasuredWidth =  dayOfWeekTv.getMeasuredWidth();
        LinearLayout.LayoutParams dayOfWeekTvParams
                = new LinearLayout.LayoutParams(dayOfWeekTvMeasuredWidth, dayOfWeekTvMeasuredHeight);
        dayOfWeekTvParams.gravity = Gravity.CENTER;
        dayOfWeekTv.setLayoutParams(dayOfWeekTvParams);
        container.addView(dayOfWeekTv);

        dayOfMonthTv = new TextView(getContext());
        dayOfMonthTv.setTextSize(dayOfMonthTextSize);
        dayOfMonthTv.setTextColor(getResources().getColor(text_calendar_weekdate));
        dayOfMonthTv.setText("20");
        dayOfMonthTv.measure(0, 0);
        dayOfMonthTv.setGravity(Gravity.CENTER);
        final int dayOfMonthTvMeasuredHeight = dayOfMonthTv.getMeasuredHeight();
        final int dayOfMonthTvMeasuredWidth =  dayOfMonthTv.getMeasuredWidth();
        LinearLayout.LayoutParams dayOfMonthTvParams
                = new LinearLayout.LayoutParams(dayOfMonthTvMeasuredWidth, dayOfMonthTvMeasuredHeight);
        dayOfMonthTvParams.gravity = Gravity.CENTER;
        dayOfMonthTv.setLayoutParams(dayOfMonthTvParams);
        container.addView(dayOfMonthTv);

        int containerHeight = dayOfMonthTvMeasuredHeight + dayOfWeekTvMeasuredHeight;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,containerHeight);
        params.gravity = Gravity.CENTER;
        container.setLayoutParams(params);
        this.addView(container);
    }

    private void updateDate(Calendar cal){
        String dayOfWeekStr = cal.getDisplayName(
                Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).toUpperCase();
        dayOfWeekTv.setText(dayOfWeekStr);
        dayOfMonthTv.setText("" + cal.get(Calendar.DAY_OF_MONTH));
    }
}
