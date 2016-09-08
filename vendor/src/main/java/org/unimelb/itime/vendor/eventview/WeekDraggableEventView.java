package org.unimelb.itime.vendor.eventview;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.telecom.Connection;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;

/**
 * Created by Paul on 26/08/2016.
 */
public class WeekDraggableEventView extends RelativeLayout implements View.OnLongClickListener{
    private ITimeEventInterface event;
    private TextView title;
    private ImageView icon;
    private ImageView leftBar;
    private final int PRIVATE =0;
    private final int GROUP =1;
    private final int PUBLIC = 2;

    private final int PENDING = 5;
    private final int CONFIRM = 6;
//    private Event.Type type;
//    private Event.Status status;

    private int width;
    private int height;

    public WeekDraggableEventView(Context context, ITimeEventInterface event) {
        super(context);
        this.event = event;
        init();
    }

    public void init(){
        initBackground();
        initWidgets();
    }

    public void initBackground(){
        this.setBackground(getResources().getDrawable(R.drawable.itime_draggable_event_bg));
        ((GradientDrawable)this.getBackground()).setColor(Color.BLUE);
    }

    public void initWidgets(){
        initIcon();
        initTitle();
        initDarkLeftBorder();
    }

    private void initDarkLeftBorder(){
        leftBar = new ImageView(this.getContext());
        this.addView(leftBar);
    }


    private void initTitle(){
        title = new TextView(getContext());
        title.setText(event.getTitle());
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTextColor(Color.WHITE);
        title.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        this.addView(title);
    }

    public void update(){
        int type = event.getEventType();
        int status = event.getStatus();
        int color = Color.RED;
        switch (type){
            case PRIVATE:
                color = getContext().getResources().getColor(R.color.private_et);
                break;
            case GROUP:
                color = getContext().getResources().getColor(R.color.group_et);
                break;
            case PUBLIC:
                color = getContext().getResources().getColor(R.color.public_et);
                break;
            default:
                color = getContext().getResources().getColor(R.color.private_et);
                break;
        }
        this.setBackground(getResources().getDrawable(R.drawable.itime_draggable_event_bg));
        ((GradientDrawable)this.getBackground()).setColor(color);
        this.getBackground().setAlpha(128);
        updateLeftBar(getResources().getDrawable(R.drawable.itime_draggable_event_bg), color);
        if (getStatusIcon(status)!=0) {
            this.resetIcon(getStatusIcon(status));
        }
    }


    private int getStatusIcon(int status){
        switch (status){
            case PENDING:
                return R.drawable.itime_question_mark;
//            case CONFIRM:
//                return R.drawable.itime_icon_tick;
            default:
                return 0;
        }
    }

    private void resetIcon(int iconDrawable){
        icon.setImageResource(iconDrawable);
    }


    private void updateLeftBar(Drawable db, int color){
        leftBar.setImageDrawable(db);
        ((GradientDrawable)leftBar.getDrawable()).setColor(color);
    }


    private void initIcon(){
        if (event.getStatus()!=0) {
            icon = new ImageView(getContext());
            icon.setImageResource(R.drawable.itime_question_mark);
            this.addView(icon);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        super.onLayout(changed, l, t, r, b);
        this.width = r-l;
        int leftBarWidth = this.width/10;
        this.height = b-t;
        RelativeLayout.LayoutParams leftBarParams = (RelativeLayout.LayoutParams)leftBar.getLayoutParams();
        leftBar.layout(0,0,leftBarWidth,height);

        if (event.getStatus()!=0) {
            int iconWidth = this.width / 3;
            int iconLeft = this.width / 3 * 2 - this.width / 10;
            int iconTop = this.width / 10;
            RelativeLayout.LayoutParams iconParams = (RelativeLayout.LayoutParams) icon.getLayoutParams();
            iconParams.topMargin = iconTop;
            iconParams.leftMargin = iconLeft;
            icon.layout(iconLeft, iconTop, iconLeft + iconWidth, iconTop + iconWidth);
        }

        RelativeLayout.LayoutParams titleParams = (RelativeLayout.LayoutParams)title.getLayoutParams();
        titleParams.topMargin=this.width/10;
        titleParams.leftMargin = width/3;
        title.layout(this.width/18 + this.width/10, this.width/3+this.width/10, (int)(this.width * 0.9),this.height);
        Log.i("title height", String.valueOf(title.getHeight()));
        Log.i("title width",String.valueOf(title.getWidth()));
        update();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.width = (int) (MeasureSpec.getSize(widthMeasureSpec));
        this.height = (MeasureSpec.getSize(heightMeasureSpec));
        setMeasuredDimension(width,height);
    }

    public ITimeEventInterface getEvent(){
        return this.event;
    }

    public void setEvent(ITimeEventInterface event){
        this.event = event;
    }


    @Override
    public boolean onLongClick(View view) {
        ClipData data = ClipData.newPlainText("", "");
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                view);
        view.startDrag(data, shadowBuilder, view, 0);
        view.setVisibility(View.VISIBLE);
        view.getBackground().setAlpha(255);
        return false;
    }
}
