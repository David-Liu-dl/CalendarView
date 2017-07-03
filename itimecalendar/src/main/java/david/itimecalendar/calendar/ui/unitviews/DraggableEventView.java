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
    // 30% alpha
    public static int OPACITY_INT = 77;

    private int width;
    private int height;
    //0: default_normal, -1: temp view;
    private int type = 0;
    private int indexInView = 0;
    private int color;

    private boolean isAllDayEvent = false;
    private long duration = 0;

    private String status;
    private String iconName;

    private TextView title;
    private TextView location;
    private ImageView icon;
    private ImageView leftBar;

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
        location.setText("Nothing To Show");
    }

    private void setType(){
        //if color is not determined
        if (color == 0){
            switch (0){
                case 0:
                    color = getContext().getResources().getColor(R.color.private_et);
                    break;
                case 1:
                    color = getContext().getResources().getColor(R.color.group_et);
                    break;
                case 2:
                    color = getContext().getResources().getColor(R.color.public_et);
                    break;
            }
        }

    }

    private void initDataInViews(){
        if (this.event != null){
            this.setText();
            this.setType();
        }
    }

    private void initBackground(){
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
        icon.setId(generateViewId());
        icon.setImageResource(R.drawable.itime_question_mark_small);
        LayoutParams params = new LayoutParams(DensityUtil.dip2px(getContext(), 12),DensityUtil.dip2px(getContext(), 12));
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



    private void updateLeftBar(Drawable db, int color){
        leftBar.setImageDrawable(db);
        ((GradientDrawable)leftBar.getDrawable()).setColor(color);
    }

    public ITimeEventInterface getEvent() {
        return event;
    }

    public void setEvent(ITimeEventInterface event) {
        this.event = event;
        this.duration = event.getEndTime() - event.getStartTime();
    }

    public int getIndexInView() {
        return indexInView;
    }

    public void setIndexInView(int indexInView) {
        this.indexInView = indexInView;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
//
//
//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        int baseL = getPaddingLeft();
//        int baseT = getPaddingTop();
//        int icon_margin = DensityUtil.dip2px(getContext(),1);
//
//        width = (r-l) - baseL;
//        height = b - t;
//
//        bg.layout(baseL,baseT,width,height + baseT);
//        leftBar.layout(baseL, baseT, leftBar.getLayoutParams().width, height);
//        icon.layout(width - baseL - icon.getLayoutParams().width -icon_margin,icon_margin + baseT,width,icon_margin + icon.getLayoutParams().height + baseT);
//        title.layout(baseL+leftBar.getLayoutParams().width,baseT, width,height - baseT);
//    }

}
