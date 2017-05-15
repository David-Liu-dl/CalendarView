package david.itimecalendar.calendar.calendar.mudules.monthview;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import david.horizontalscrollpageview.HorizontalRecyclerView;
import david.horizontalscrollpageview.HorizontalScrollAdapter;
import david.itimecalendar.R;
import david.itimecalendar.calendar.listeners.ITimeEventPackageInterface;
import david.itimecalendar.calendar.util.DensityUtil;

/**
 * Created by yuhaoliu on 11/05/2017.
 */

public class DayViewBody extends FrameLayout {
    private FrameLayout leftTimeBarLayout;

    private HorizontalRecyclerView bodyRecyclerView;
    private BodyAdapter bodyPagerAdapter;
    private Context context;

    private int leftBarWidth = 100;
    private int hourHeight = 30;
    private int timeTextSize = 20;
    private int spaceTop = 30;

    private int color_time_text = R.color.text_enable;

    public DayViewBody(@NonNull Context context) {
        super(context);
        init();
    }

    public DayViewBody(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DayViewBody(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        this.context = getContext();
        this.hourHeight = DensityUtil.dip2px(context, hourHeight);
        setUpBody();
    }

    private void setUpBody(){
        setUpLeftTimeBar();
        setUpCalendarBody();
    }

    private void setUpCalendarBody(){
        this.bodyRecyclerView = new HorizontalRecyclerView(getContext(),3);
        bodyPagerAdapter = new BodyAdapter(R.layout.itime_day_view_body);
//        bodyPagerAdapter = new BodyAdapter(R.layout.item_view);
        bodyRecyclerView.setAdapter(bodyPagerAdapter);
//        bodyRecyclerView.scrollToPosition(HorizontalScrollAdapter.START_POSITION);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = leftBarWidth;
        this.addView(bodyRecyclerView, params);
    }

    private void setUpLeftTimeBar(){
        this.leftTimeBarLayout = new FrameLayout(getContext());
        this.initTimeText(getHours());
        this.addView(leftTimeBarLayout);
    }

    private void initTimeText(String[] HOURS) {
        int height = DensityUtil.dip2px(context,20);
        for (int time = 0; time < HOURS.length; time++) {
            int timeTextY = hourHeight * time + spaceTop;

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(leftBarWidth, height);
            TextView timeView = new TextView(context);
            timeView.setTextColor(context.getResources().getColor(color_time_text));
            timeView.setText(HOURS[time]);
            timeView.setTextSize(11);
            timeView.setGravity(Gravity.CENTER);
            params.setMargins(0, timeTextY - height/2, 0, 0);
            timeView.setLayoutParams(params);

            timeTextSize = (int) timeView.getTextSize() + timeView.getPaddingTop();
            leftTimeBarLayout.addView(timeView);
        }
    }

    private String[] getHours() {
        String[] HOURS = new String[]{
                "00", "01", "02", "03", "04", "05", "06", "07",
                "08", "09", "10", "11", "12", "13", "14", "15",
                "16", "17", "18", "19", "20", "21", "22", "23",
                "24"
        };

        return HOURS;
    }
    /************************************************************************************/
    public void setEventPackage(ITimeEventPackageInterface eventPackage){
        this.bodyPagerAdapter.setEventPackage(eventPackage);
    }
}
