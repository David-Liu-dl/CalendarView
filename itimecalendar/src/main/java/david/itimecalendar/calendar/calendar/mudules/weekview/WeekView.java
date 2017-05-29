package david.itimecalendar.calendar.calendar.mudules.weekview;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.developer.paul.recycleviewgroup.RecycleViewGroup;
import com.github.sundeepk.compactcalendarview.DensityUtil;

import java.util.Calendar;
import java.util.Date;

import david.itimecalendar.R;
import david.itimecalendar.calendar.calendar.mudules.monthview.DayViewBody;
import david.itimecalendar.calendar.calendar.mudules.monthview.EventController;
import david.itimecalendar.calendar.listeners.ITimeEventPackageInterface;
import david.itimecalendar.calendar.util.BaseUtil;
import david.itimecalendar.calendar.util.MyCalendar;

/**
 * Created by yuhaoliu on 10/05/2017.
 */

public class WeekView extends LinearLayout{
    public WeekView(@NonNull Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public WeekView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.viewAttrs = attrs;
        initView();
    }

    public WeekView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.viewAttrs = attrs;
        initView();
    }
    private AttributeSet viewAttrs;

    private Context context;

    private ITimeEventPackageInterface eventPackage;

    private RecycleViewGroup headerRG;

    private FrameLayout dayViewBodyContainer;
    private DayViewBody dayViewBody;

    private void initView(){
        this.context = getContext();
        this.setOrientation(VERTICAL);
        this.setUpHeader();
        this.setUpDivider();
        this.setUpBody();
    }

    private WeekViewHeaderAdapter headerAdapter;
    private void setUpHeader(){

        headerRG = new RecycleViewGroup(context, viewAttrs);
        headerRG.setOnSetting(new RecycleViewGroup.OnSetting() {
            @Override
            public int getItemHeight(int i) {
                int childMaxHeight = MeasureSpec.getSize(i);
                return childMaxHeight;
            }
        });
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(context,50));
        headerRG.setLayoutParams(params);
        headerAdapter = new WeekViewHeaderAdapter(context);
        headerRG.setAdapter(headerAdapter);
        headerRG.setDisableScroll(true);
        this.addView(headerRG);
    }

    private void setUpDivider(){
        ImageView divider = BaseUtil.getDivider(context, R.drawable.itime_header_divider_line);
        this.addView(divider);
    }

    private void setUpBody(){
        dayViewBodyContainer = new FrameLayout(getContext());
        this.addView(dayViewBodyContainer, new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        dayViewBody = new DayViewBody(context, viewAttrs);
        dayViewBody.setOnScrollListener(new RecycleViewGroup.OnScroll() {
            @Override
            public void onPageSelected(View v) {
            }

            @Override
            public void onHorizontalScroll(int dx, int preOffsetX) {
                headerRG.moveXBy(dx);
            }

            @Override
            public void onVerticalScroll(int dy, int preOffsetY) {

            }
        });

        FrameLayout.LayoutParams bodyParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.dayViewBodyContainer.addView(dayViewBody, bodyParams);

        ((LayoutParams) headerRG.getLayoutParams()).leftMargin = dayViewBody.getLeftBarWidth();
    }

    public void setScrollInterface(RecycleViewGroup.ScrollInterface scrollInterface){
        dayViewBody.setScrollInterface(scrollInterface);
    }

    public void scrollToDate(Date date){
        this.headerScrollToDate(date);
        dayViewBody.scrollToDate(date);
    }

    private void headerScrollToDate(Date date){
        MyCalendar currentFstShowDay = ((WeekViewHeaderCell) headerRG.getFirstShowItem()).getCalendar();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int offset = BaseUtil.getDatesDifference(currentFstShowDay.getCalendar().getTimeInMillis(), cal.getTimeInMillis());
        headerRG.moveWithOffset(offset);
    }

    public void setEventPackage(ITimeEventPackageInterface eventPackage){
        this.eventPackage = eventPackage;
        this.dayViewBody.setEventPackage(eventPackage);
    }

    public void setOnBodyListener(EventController.OnEventListener onEventListener) {
        this.dayViewBody.setOnBodyListener(onEventListener);
    }

    public void smoothMoveWithOffset(int moveOffset){
        dayViewBody.smoothMoveWithOffset(moveOffset);
    }

    public void refresh(){
        dayViewBody.refresh();
    }


    public void setDisableCellScroll(boolean isDisabled){
        headerRG.setDisableCellScroll(isDisabled);
        dayViewBody.setDisableCellScroll(isDisabled);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
