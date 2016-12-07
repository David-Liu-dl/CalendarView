package org.unimelb.itime.vendor.eventview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.helper.DensityUtil;
import org.unimelb.itime.vendor.helper.MyCalendar;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;

import java.util.Calendar;

/**
 * Created by yuhaoliu on 3/08/16.
 */
public class DayDraggableEventView extends ViewGroup {
    private final String TAG = "MyAPP";

    public static int TYPE_NORMAL = 0;
    public static int TYPE_TEMP = 1;
    //0: default_normal, -1: temp view;
    private int type = 0;

    private int indexInView = 0;

    private TextView title;
    private ImageView icon;
    private ImageView leftBar;

    private boolean isAllDayEvent = false;

    private MyCalendar newCalendar = new MyCalendar(Calendar.getInstance());

    private ITimeEventInterface event;

    private int color;
    private String status;
    private String icon_name;

    private PosParam posParam;

    private int width;
    private int height;

    public MyCalendar getNewCalendar() {
        return newCalendar;
    }

    public void setNewCalendar(MyCalendar newCalendar) {
        this.newCalendar = newCalendar;
    }

    public DayDraggableEventView(Context context, @Nullable ITimeEventInterface event, boolean isAllDayEvent) {
        super(context);
        this.event = event;
        initAttrs();

        this.isAllDayEvent = isAllDayEvent;
        this.setBackgroundDrawable(getResources().getDrawable(R.drawable.itime_draggable_event_bg));
        ((GradientDrawable)this.getBackground()).setColor(Color.BLUE);

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
            Paint p = new Paint();
            p.setAntiAlias(true);
            p.setColor(Color.WHITE);
            p.setStrokeWidth(DensityUtil.dip2px(getContext(),1));

            float slope = 0.8f;
            int nowAtPxX = (int)(-width*0.5);
            int nowAtPxY = 0;

            int xDiffer = DensityUtil.dip2px(getContext(),30);
            int xGap = DensityUtil.dip2px(getContext(),10);

            while (nowAtPxX <= width){
//                canvas.drawLine(nowAtPxX, nowAtPxY, nowAtPxX + xDiffer, height, p);
                canvas.drawLine(nowAtPxX, nowAtPxY, nowAtPxX + height, height, p);
                nowAtPxX += xGap;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        width = r - l;
        height = b - t;

        leftBar.layout(l, 0, l + leftBar.getLayoutParams().width, b-t);
        int icon_margin = DensityUtil.dip2px(getContext(),1);
        icon.layout(r - icon.getLayoutParams().width -icon_margin,icon_margin,r,icon_margin + icon.getLayoutParams().height);
        title.layout(l+leftBar.getLayoutParams().width,0, l + r - icon.getLayoutParams().width,b);
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

    private void initDarkLeftBorder(){
        leftBar = new ImageView(this.getContext());
        LayoutParams param = new LayoutParams(DensityUtil.dip2px(getContext(), 3),0);
        this.addView(leftBar,param);
    }

    private void initDataInViews(){
        if (this.event != null){
            this.setSummary();
            this.setType();
            this.setStatus();
        }
    }

    private void setSummary(){
        title.setText(event.getTitle());
    }

    private void setType(){
//        int color = Color.RED;
//
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

        this.setBackground(getResources().getDrawable(R.drawable.itime_draggable_event_bg));
        ((GradientDrawable)this.getBackground()).setColor(color);
        this.getBackground().setAlpha(128);

        updateLeftBar(getResources().getDrawable(R.drawable.itime_draggable_event_bg), color);
    }

    private void setStatus(){
        long duration = this.event.getEndTime() - this.event.getStartTime();
//        boolean useSmallIcon = ((duration <= (15 * 60 * 1000)) || isAllDayEvent);
//        this.resetIcon(getStatusIcon(this.event.getDisplayStatus(), useSmallIcon));
    }

    private int getStatusIcon(int status, boolean useSmallIcon){
        switch (status){
            case 0:
                if (useSmallIcon){
                    return R.drawable.itime_question_mark_small;
                }else {
                    return R.drawable.itime_question_mark_small;
                }
            default:
                return -1;
        }
    }

    /****************************************************************************************/
    private void initBackground(){
        initIcon();
        initDarkLeftBorder();
        initEventTitle();
    }

    private void initIcon(){
        icon = new ImageView(getContext());
        icon.setImageResource(R.drawable.itime_question_mark_small);
        LayoutParams params = new LayoutParams(DensityUtil.dip2px(getContext(), 15),DensityUtil.dip2px(getContext(), 15));
        this.addView(icon,params);
    }

    private void resetIcon(int iconDrawable){
        if (iconDrawable != -1){
            icon.setImageResource(iconDrawable);
        }else{
            icon.setImageResource(0);
        }
    }

    private void initEventTitle(){
        int padding = DensityUtil.dip2px(getContext(), isAllDayEvent ? 1 : 3);
        title = new TextView(getContext());
        title.setMaxLines(1);
        title.setEllipsize(TextUtils.TruncateAt.END);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTextColor(Color.WHITE);
        title.setIncludeFontPadding(false);
        title.setPadding(padding,padding,padding,padding);
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
