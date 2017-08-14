package david.itimecalendar.calendar.ui.unitviews;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

import david.itimecalendar.R;
import david.itimecalendar.calendar.util.BaseUtil;
import david.itimecalendar.calendar.util.DensityUtil;
import david.itimecalendar.calendar.util.MyCalendar;
import david.itimecalendar.calendar.wrapper.WrapperTimeSlot;

/**
 * Created by yuhaoliu on 1/05/2017.
 */

public class RcdAllDayTimeslotView extends RelativeLayout {
    private TextView label;
    private ImageView icon;
    private WrapperTimeSlot wrapper;

    public RcdAllDayTimeslotView(@NonNull Context context, WrapperTimeSlot wrapper) {
        super(context);
        this.wrapper = wrapper;
        init();
    }

    public RcdAllDayTimeslotView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RcdAllDayTimeslotView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        this.setBackground(getResources().getDrawable(R.drawable.icon_timeslot_allday));

        label = new TextView(getContext());
        label.setText( "All Day Timeslot");
        label.setTextColor(getResources().getColor(R.color.allday_timeslot_text));
        label.setGravity(Gravity.CENTER);
        label.setTextSize(12);
        label.setId(generateViewId());
        LayoutParams labelPrams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        labelPrams.topMargin = DensityUtil.dip2px(getContext(),10);
        labelPrams.addRule(ALIGN_PARENT_TOP);
        labelPrams.addRule(CENTER_HORIZONTAL);
        this.addView(label,labelPrams);

        FrameLayout frameLayout = new FrameLayout(getContext());
        LayoutParams frameLayoutPrams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        frameLayoutPrams.addRule(BELOW,label.getId());
        this.addView(frameLayout,frameLayoutPrams);

        icon = new ImageView(getContext());
        icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_calendar_alldayplus));

        int size = DensityUtil.dip2px(getContext(),25);
        FrameLayout.LayoutParams iconPrams = new FrameLayout.LayoutParams(size, size);
        iconPrams.gravity = Gravity.CENTER;
        frameLayout.addView(icon, iconPrams);
    }

    public WrapperTimeSlot getWrapper() {
        return wrapper;
    }

    public void setWrapper(WrapperTimeSlot wrapper) {
        this.wrapper = wrapper;
    }

    private MyCalendar myCalendar;

    public long getAllDayBeginTime(){
        return myCalendar.getBeginOfDayMilliseconds();
    }

    public void setMyCalendar(MyCalendar myCalendar) {
        this.myCalendar = myCalendar;
    }
}
