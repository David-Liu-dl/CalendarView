package david.itimecalendar.calendar.calendar.mudules.weekview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.sundeepk.compactcalendarview.DensityUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;

import david.itimecalendar.R;
import david.itimecalendar.calendar.calendar.mudules.monthview.DayViewBody;
import david.itimecalendar.calendar.calendar.mudules.monthview.EventController;
import david.itimecalendar.calendar.listeners.ITimeEventPackageInterface;
import david.itimecalendar.calendar.util.BaseUtil;
import david.itimerecycler.RecycledViewGroup;

import static com.github.sundeepk.compactcalendarview.CompactCalendarView.FILL_LARGE_INDICATOR;
import static com.github.sundeepk.compactcalendarview.CompactCalendarView.SMALL_INDICATOR;

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

    private WeekViewHeader header;
    private FrameLayout dayViewBodyContainer;
    private DayViewBody dayViewBody;

    private void initView(){
        this.context = getContext();
        this.setOrientation(VERTICAL);
        this.setUpHeader();
        this.setUpDivider();
        this.setUpBody();
    }

    private void setUpHeader(){
        header = new WeekViewHeader(getContext(), viewAttrs);
        header.setStartDate(new Date());
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(context,50));
        header.setLayoutParams(params);
        this.addView(header);
    }

    private void setUpDivider(){
        ImageView divider = BaseUtil.getDivider(context, R.drawable.itime_header_divider_line);
        this.addView(divider);
    }

    private void setUpBody(){
        dayViewBodyContainer = new FrameLayout(getContext());
        this.addView(dayViewBodyContainer, new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        dayViewBody = new DayViewBody(context, viewAttrs);
        dayViewBody.setOnScrollListener(new RecycledViewGroup.OnScroll() {
            @Override
            public void onPageSelected(View v) {
            }

            @Override
            public void onHorizontalScroll(int dx, int preOffsetX) {

            }

            @Override
            public void onVerticalScroll(int dy, int preOffsetY) {

            }
        });

        FrameLayout.LayoutParams bodyParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.dayViewBodyContainer.addView(dayViewBody, bodyParams);

        ((LayoutParams)header.getLayoutParams()).leftMargin = dayViewBody.getLeftBarWidth();
    }

    public void scrollToDate(Date date){
        dayViewBody.scrollToDate(date);
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

<<<<<<< HEAD

    public void setDisableCellScroll(boolean isDisabled){
        dayViewBody.setDisableCellScroll(isDisabled);
    }

=======
>>>>>>> 3eb2314bb3969edc720fcfef8552410eb8ad395f
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
