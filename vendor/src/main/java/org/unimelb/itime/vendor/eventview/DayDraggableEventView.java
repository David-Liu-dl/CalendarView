package org.unimelb.itime.vendor.eventview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.helper.DensityUtil;
import org.unimelb.itime.vendor.helper.MyCalendar;
import org.unimelb.itime.vendor.helper.VendorAnimation;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;

import java.util.Calendar;

/**
 * Created by yuhaoliu on 3/08/16.
 */
public class DayDraggableEventView extends ViewGroup {
    private final String TAG = "MyAPP";

    public static int TYPE_NORMAL = 0;
    public static int TYPE_TEMP = 1;

    private int width;
    private int height;
    //0: default_normal, -1: temp view;
    private int type = 0;
    private int indexInView = 0;
    private int color;
    private boolean isAllDayEvent = false;

    private String status;
    private String icon_name;

    private Paint p = new Paint();
    private ImageView bg;
    private TextView title;
    private ImageView icon;
    private ImageView leftBar;

    private MyCalendar newCalendar = new MyCalendar(Calendar.getInstance());
    private ITimeEventInterface event;
    private PosParam posParam;

    public DayDraggableEventView(Context context, @Nullable ITimeEventInterface event, boolean isAllDayEvent) {
        super(context);
        this.event = event;
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
                this.icon_name = attrs[2];
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(this.event != null
                && this.status != null
                && !this.status.equals("")
                && this.status.equals("slash")){

            p.setAntiAlias(true);
            p.setColor(Color.WHITE);
            p.setStrokeWidth(DensityUtil.dip2px(getContext(),1));

            int nowAtPxX = 0 - height;
            int nowAtPxY = getPaddingTop();

            int xGap = DensityUtil.dip2px(getContext(),10);

            while (nowAtPxX <= width){
                canvas.drawLine(nowAtPxX, nowAtPxY, nowAtPxX + height, height, p);
                nowAtPxX += xGap;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int baseL = getPaddingLeft();
        int baseT = getPaddingTop();
        width = r - l - baseL;
        height = b - t;

        bg.layout(baseL,baseT,r,b);
        leftBar.layout(baseL, baseT, leftBar.getLayoutParams().width, height);
        int icon_margin = DensityUtil.dip2px(getContext(),1);
        icon.layout(width - baseL - icon.getLayoutParams().width -icon_margin,icon_margin + baseT,width,icon_margin + icon.getLayoutParams().height + baseT);
        title.layout(baseL+leftBar.getLayoutParams().width,t + baseT, width,b);
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        public int left = 0;
        public int top = 0;

        public LayoutParams(Context arg0, AttributeSet arg1) {
            super(arg0, arg1);
        }

        public LayoutParams(int arg0, int arg1) {
            super(arg0, arg1);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams arg0) {
            super(arg0);
        }

    }

    public MyCalendar getNewCalendar() {
        return newCalendar;
    }

    public void setNewCalendar(MyCalendar newCalendar) {
        this.newCalendar = newCalendar;
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
        ((GradientDrawable)bg.getBackground()).setColor(color);
        bg.getBackground().setAlpha(128);
        //set leftBar color base on type
        updateLeftBar(getResources().getDrawable(R.drawable.itime_draggable_event_bg), color);
    }

    public void setBackground(Drawable drawable){
        bg.setBackground(drawable);
        ((GradientDrawable)bg.getBackground()).setColor(color);
        bg.getBackground().setAlpha(128);
    }

    @Override
    public Drawable getBackground() {
        return this.bg.getBackground();
    }

    private void initDarkLeftBorder(){
        leftBar = new ImageView(this.getContext());
        LayoutParams param = new LayoutParams(DensityUtil.dip2px(getContext(), 3),0);
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
        bg = new ImageView(getContext());
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

        this.addView(icon,params);
    }

    private void initEventTitle(){
        int padding = DensityUtil.dip2px(getContext(), isAllDayEvent ? 1 : 3);
        title = new TextView(getContext());
        title.setTextSize(11);
        title.setTextColor(getResources().getColor(R.color.black));
        title.setEllipsize(TextUtils.TruncateAt.END);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setIncludeFontPadding(true);
        title.setPadding(padding,0,padding,0);
        this.addView(title);
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
     * the display position of draggable event,
     * for overlapping algorithm
     *
     */
    public static class PosParam{
        public int startY;
        public int startX;
        public int widthFactor;
        public int topMargin;

        public PosParam(){

        }

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
        return this.getNewCalendar().getCalendar().getTimeInMillis();
    }

    public long getEndTimeM(){
        return this.getStartTimeM() + (event.getEndTime() - event.getStartTime());
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
