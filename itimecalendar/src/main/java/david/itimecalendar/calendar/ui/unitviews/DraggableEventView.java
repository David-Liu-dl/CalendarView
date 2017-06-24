package david.itimecalendar.calendar.ui.unitviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
public class DraggableEventView extends FrameLayout {
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
    private float slashOpacity = 0.30f;

    private boolean isAllDayEvent = false;
    private long duration = 0;

    private String status;
    private String iconName;

    private Paint p = new Paint();
    private ImageView bg;
    private TextView title;
    private ImageView icon;
    private ImageView leftBar;

    private MyCalendar calendar = new MyCalendar(Calendar.getInstance());
    private ITimeEventInterface event;
    private PosParam posParam;

    public DraggableEventView(Context context, @Nullable ITimeEventInterface event, boolean isAllDayEvent) {
        super(context);
        this.setEvent(event);
        initAttrs();

        this.isAllDayEvent = isAllDayEvent;
        initBackground();
        initDataInViews();
    }



    private void initAttrs(){
        String dpStatus = event.getDisplayStatus();
        if (dpStatus != null && !dpStatus.equals("")){
            String[] attrs = dpStatus.split("\\|");
            if (attrs.length < 3){
                Log.i(TAG, "initAttrs: attrs is not sufficient.");
            }else{
                this.color = Color.parseColor(attrs[0]);
                this.status = attrs[1];
                this.iconName = attrs[2];
            }
        }
    }

    @Override
    public String toString() {
        String str = " height : "+ height  + " width : " + width; // paul try
        return str;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int baseL = getPaddingLeft();
        int baseT = getPaddingTop();
        int icon_margin = DensityUtil.dip2px(getContext(),1);

        width = (r-l) - baseL;
        height = b - t;
        Log.i("DayViewAllDay", "DG onLayout: " + height);

        bg.layout(baseL,baseT,width,height + baseT);
        leftBar.layout(baseL, baseT, leftBar.getLayoutParams().width, height);
        icon.layout(width - baseL - icon.getLayoutParams().width -icon_margin,icon_margin + baseT,width,icon_margin + icon.getLayoutParams().height + baseT);
        title.layout(baseL+leftBar.getLayoutParams().width,baseT, width,height - baseT);
    }

    public MyCalendar getCalendar() {
        return calendar;
    }

    public void setCalendar(MyCalendar calendar) {
        this.calendar = calendar;
    }

    private void setSummary(){
        title.setText(event.getTitle());
    }

    private void setType(){
        //if color is not determined
        if (color == 0){
            switch (this.event.getDisplayEventType()){
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

        //set background color base on type
        bg.setBackground(getResources().getDrawable(R.drawable.itime_draggable_event_bg));

        if (!event.isHighlighted()){
            ((GradientDrawable)bg.getBackground()).setColor(color);
            bg.getBackground().setAlpha(OPACITY_INT);
        }else {
            color = getResources().getColor(R.color.private_et);
        }

        //if item is grouped && not confirmed
        if(event != null
                && this.event.getDisplayEventType() == 1
                && status != null
                && !status.equals("")
                && status.equals("slash")){
            ((GradientDrawable)bg.getBackground()).setColor(Color.WHITE);
        }else {
            ((GradientDrawable)bg.getBackground()).setColor(color);
        }

        //set leftBar color base on type
        updateLeftBar(getResources().getDrawable(R.drawable.itime_draggable_event_bg), color);
    }

    @Override
    public void setBackground(Drawable drawable){
        bg.setBackground(drawable);
        int actualColor = color;
        if(event != null
                && this.event.getDisplayEventType() == 1
                && status != null
                && !status.equals("")
                && status.equals("slash")){
            actualColor = Color.WHITE;
        }
        ((GradientDrawable)bg.getBackground()).setColor(actualColor);
        if (!event.isHighlighted()){
            bg.getBackground().setAlpha(OPACITY_INT);
        }
    }

    @Override
    public Drawable getBackground() {
        return this.bg.getBackground();
    }

    private void initDarkLeftBorder(){
        leftBar = new ImageView(this.getContext());
        LayoutParams param = new LayoutParams(DensityUtil.dip2px(getContext(), 2.5f),0);
        this.addView(leftBar,param);
    }

    private void initDataInViews(){
        if (this.event != null){
            this.setSummary();
            this.setType();
        }
    }

    private void initBackground(){
        initBackgroundView();
        initDarkLeftBorder();
        initEventTitle();
        initIcon();
    }

    private void initBackgroundView(){
        bg = new android.support.v7.widget.AppCompatImageView(getContext()){
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);

                if(event != null
                        && status != null
                        && !status.equals("")
                        && status.equals("slash")){
                    drawSlash(canvas);
                }

                if (iconName != null && !iconName.equals("icon_question")){
                    icon.setVisibility(View.INVISIBLE);
                }
            }
        };

        bg.setBackgroundDrawable(getResources().getDrawable(R.drawable.itime_draggable_event_bg));
        ((GradientDrawable)bg.getBackground()).setColor(Color.BLUE);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        this.addView(bg,params);
    }

    private void initIcon(){
        icon = new ImageView(getContext());
        icon.setImageResource(R.drawable.itime_question_mark_small);
        LayoutParams params = new LayoutParams(DensityUtil.dip2px(getContext(), 15),DensityUtil.dip2px(getContext(), 15));
        icon.setPadding(0,0,0,0);
        icon.setLayoutParams(params);
        this.addView(icon);
    }

    private void initEventTitle(){
        int padding = DensityUtil.dip2px(getContext(), isAllDayEvent ? 1 : 3);
        title = new TextView(getContext());
        title.setTextSize(11);
        title.setTextColor(getResources().getColor(event.isHighlighted() ? R.color.white : R.color.black));
        title.setEllipsize(TextUtils.TruncateAt.END);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setIncludeFontPadding(true);
        title.setPadding(padding,0,padding,0);
        this.addView(title);
    }

    private void drawSlash(Canvas canvas){
        int slashColor = getContext().getResources().getColor(R.color.group_et);
        p.setAntiAlias(true);
        p.setColor(slashColor);
        p.setAlpha((int)(255 * slashOpacity));
        p.setStrokeWidth(DensityUtil.dip2px(getContext(),1));

        int nowAtPxX = 0 - height;
        int nowAtPxY = getPaddingTop();

        int xGap = DensityUtil.dip2px(getContext(),10);

        while (nowAtPxX <= width){
            canvas.drawLine(nowAtPxX, nowAtPxY, nowAtPxX + height, height, p);
            nowAtPxX += xGap;
        }
    }

    private void updateLeftBar(Drawable db, int color){
        leftBar.setImageDrawable(db);
        ((GradientDrawable)leftBar.getDrawable()).setColor(color);
    }

    public ITimeEventInterface getEvent() {
        return event;
    }

    public void setToBg(){
        leftBar.setVisibility(INVISIBLE);
        color = getResources().getColor(R.color.event_as_bg_bg);
        ((GradientDrawable)bg.getBackground()).setColor(color);
        bg.getBackground().setAlpha(255);
        title.setTextColor(getResources().getColor(R.color.event_as_bg_title));
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

    private Path path = new Path();
    private RectF rect = new RectF();

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // compute the path
        path.reset();
        rect.set(0, 0, w, h);
        path.addRoundRect(rect, 15, 15, Path.Direction.CW);
        path.close();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int save = canvas.save();
        canvas.clipPath(path);
        super.dispatchDraw(canvas);
        canvas.restoreToCount(save);
    }
}
