package david.itimecalendar.calendar.ui.unitviews;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.Calendar;

import david.itimecalendar.R;
import david.itimecalendar.calendar.listeners.ITimeEventInterface;
import david.itimecalendar.calendar.util.DensityUtil;
import david.itimecalendar.calendar.util.MyCalendar;
import david.itimecalendar.calendar.util.VendorAnimation;

/**
 * Created by yuhaoliu on 3/08/16.
 */
public class DraggableEventView extends RelativeLayout {
    private final String TAG = "MyAPP";

    public static int TYPE_NORMAL = 0;
    public static int TYPE_TEMP = 1;
    //0: default_normal, -1: temp view;
    private int viewType = 0;
    // 0,un-confirm, 1, confirm
    private int eventStatus = 0;
    private String eventType = "";
    private int bgColor;
    private int barColor;

    private boolean isAllDayEvent = false;
    private long duration = 0;

    private TextView title;
    private TextView location;
    private ImageView icon;
    private ImageView leftBar;
    private View background;

    private MyCalendar calendar = new MyCalendar(Calendar.getInstance());
    private ITimeEventInterface event;
    private PosParam posParam;

    public DraggableEventView(Context context, @Nullable ITimeEventInterface event, boolean isAllDayEvent) {
        super(context);
        this.setEvent(event);
        this.isAllDayEvent = isAllDayEvent;

        initBackground();
        initDataInViews();
    }

    public MyCalendar getCalendar() {
        return calendar;
    }

    public void setCalendar(MyCalendar calendar) {
        this.calendar = calendar;
    }

    private void setText(){
        title.setText(event.getSummary());
        location.setText(event.getLocationName());
    }

    public void setToBg(){
        leftBar.setVisibility(INVISIBLE);
        bgColor = getResources().getColor(R.color.event_as_bg_bg);
        ((GradientDrawable)background.getBackground()).setColor(bgColor);
        title.setTextColor(getResources().getColor(R.color.event_as_bg_title));
    }

    private void initDisplayData(){
        this.eventStatus = event.isConfirmed() ? 1 : 0;
        this.eventType = event.getEventType() == null ? ITimeEventInterface.EVENT_TYPE_SOLO : event.getEventType();
    }

    private void updateViewStatus(){
        //if bgColor is not determined
        switch (this.eventStatus){
            case 0:
                bgColor = getContext().getResources().getColor(R.color.unconfirmed_event_bg);
                barColor = getContext().getResources().getColor(R.color.unconfirmed_event_bar);
                break;
            case 1:
                bgColor = getContext().getResources().getColor(R.color.confirmed_event_bg);
                barColor = getContext().getResources().getColor(R.color.confirmed_event_bar);
                break;
        }

        GradientDrawable bg = (GradientDrawable) background.getBackground();
        bg.mutate();
        bg.setColor(bgColor);

        leftBar.setBackgroundColor(barColor);

        // refresh icon

        switch (this.eventType){
            case ITimeEventInterface.EVENT_TYPE_SOLO:
                icon.setVisibility(View.GONE);
                break;
            case ITimeEventInterface.EVENT_TYPE_GROUP:
                icon.setVisibility(View.VISIBLE);
                Drawable iconSrc = getContext().getResources().getDrawable(
                        eventStatus == 0 ?
                                R.drawable.icon_calendar_group_unconfirmed
                                : R.drawable.icon_calendar_group_confirmed );
                icon.setImageDrawable(iconSrc);
                break;
        }
    }

    private void initDataInViews(){
        if (this.event != null){
            this.initDisplayData();
            this.setText();
            this.updateViewStatus();
        }
    }

    private void initBackground(){
        background = new View(getContext());
        ViewGroup.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        background.setLayoutParams(params);
        background.setBackground(getResources().getDrawable(R.drawable.itime_draggable_event_bg));
        this.addView(background);

        initDarkLeftBorder();
        initIcon();
        initEventTitle();
        initEventLocation();
    }

    private void initDarkLeftBorder(){
        leftBar = new ImageView(this.getContext());
        leftBar.setId(View.generateViewId());
        LayoutParams param = new LayoutParams(DensityUtil.dip2px(getContext(), 2), ViewGroup.LayoutParams.MATCH_PARENT);
        param.rightMargin = DensityUtil.dip2px(getContext(),3);
        leftBar.setLayoutParams(param);
        this.addView(leftBar);
    }

    private void initIcon(){
        icon = new ImageView(getContext());
        icon.setVisibility(GONE);
        icon.setId(generateViewId());
        LayoutParams params = new LayoutParams(DensityUtil.dip2px(getContext(), 12),DensityUtil.dip2px(getContext(), 12));
        params.topMargin = DensityUtil.dip2px(getContext(),6);
        params.addRule(RIGHT_OF,leftBar.getId());
        icon.setLayoutParams(params);
        this.addView(icon);
    }

    private void initEventTitle(){
        title = new TextView(getContext());
        title.setId(generateViewId());
        title.setTextSize(14);
        title.setTextColor(getResources().getColor(event.isHighlighted() ? R.color.white : R.color.black));
        title.setEllipsize(TextUtils.TruncateAt.END);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setIncludeFontPadding(true);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin =  DensityUtil.dip2px(getContext(),3);
        params.addRule(RIGHT_OF, icon.getId());
        title.setLayoutParams(params);
        this.addView(title);
    }

    private void initEventLocation(){
        location = new TextView(getContext());
        location.setTextSize(12);
        location.setTextColor(getResources().getColor(event.isHighlighted() ? R.color.white : R.color.black));
        location.setEllipsize(TextUtils.TruncateAt.END);
        location.setGravity(Gravity.CENTER_VERTICAL);
        location.setIncludeFontPadding(true);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin =  DensityUtil.dip2px(getContext(),3);
        params.addRule(RIGHT_OF, icon.getId());
        params.addRule(BELOW, title.getId());
        location.setLayoutParams(params);
        this.addView(location);
    }

    public ITimeEventInterface getEvent() {
        return event;
    }

    public void setEvent(ITimeEventInterface event) {
        this.event = event;
        this.duration = event.getEndTime() - event.getStartTime();
    }

    public void showAlphaAnim(){
        this.title.setTextColor(getResources().getColor(R.color.white));
        VendorAnimation.getInstance().getAlphaAnim(125,255,this).start();
    }

    /**
     * the display position of draggable item,
     * for overlapping algorithm
     *
     */
    public static class PosParam{
        public int startY;
        public int startX;
        public int widthFactor;
        public int topMargin;

        public PosParam(int startY, int startX, int widthFactor, int topMargin) {
            this.startY = startY;
            this.startX = startX;
            this.widthFactor = widthFactor;
            this.topMargin = topMargin;
        }
    }

    public PosParam getPosParam() {
        return posParam;
    }

    public void setPosParam(PosParam posParam) {
        this.posParam = posParam;
    }

    public long getStartTimeM(){
        return this.getCalendar().getCalendar().getTimeInMillis();
    }

    public long getEndTimeM(){
        return this.getStartTimeM() + duration;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
