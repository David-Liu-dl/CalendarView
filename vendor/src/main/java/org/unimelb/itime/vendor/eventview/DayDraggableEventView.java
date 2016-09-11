package org.unimelb.itime.vendor.eventview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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
import org.unimelb.itime.vendor.listener.ITimeContactInterface;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;

/**
 * Created by yuhaoliu on 3/08/16.
 */
public class DayDraggableEventView extends RelativeLayout {
    private final String TAG = "MyAPP";

    private TextView title;
    private ImageView icon;
    private ImageView leftBar;

    private boolean isAllDayEvent = false;

    private ITimeEventInterface event;

    public DayDraggableEventView(Context context, @Nullable ITimeEventInterface event, boolean isAllDayEvent) {
        super(context);
        this.event = event;
        this.isAllDayEvent = isAllDayEvent;
        this.setBackground(getResources().getDrawable(R.drawable.itime_draggable_event_bg));
        ((GradientDrawable)this.getBackground()).setColor(Color.BLUE);

        initBackground();
        initDataInViews();
    }

    private void initDarkLeftBorder(){
        leftBar = new ImageView(this.getContext());
        RelativeLayout.LayoutParams leftBar_params = new RelativeLayout.LayoutParams(DensityUtil.dip2px(getContext(), 3), ViewGroup.LayoutParams.MATCH_PARENT);
        leftBar_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        leftBar.setLayoutParams(leftBar_params);
        leftBar.setId(View.generateViewId());
        this.addView(leftBar);
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
        int color = Color.RED;

        switch (this.event.getEventType()){
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

        this.setBackground(getResources().getDrawable(R.drawable.itime_draggable_event_bg));
        ((GradientDrawable)this.getBackground()).setColor(color);
        this.getBackground().setAlpha(128);

        updateLeftBar(getResources().getDrawable(R.drawable.itime_draggable_event_bg), color);
    }

    private void setStatus(){
        long duration = this.event.getEndTime() - this.event.getStartTime();
        boolean useSmallIcon = ((duration <= (15 * 60 * 1000)) || isAllDayEvent);
        this.resetIcon(getStatusIcon(this.event.getStatus(), useSmallIcon));
    }

    private int getStatusIcon(int status, boolean useSmallIcon){
        switch (status){
            case 0:
                if (useSmallIcon){
                    return R.drawable.itime_question_mark_small;
                }else {
                    return R.drawable.itime_question_mark;
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
        int margin = DensityUtil.dip2px(getContext(),isAllDayEvent ? 1 : 3);
        icon = new ImageView(getContext());
        icon.setImageResource(R.drawable.itime_question_mark_small);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(DensityUtil.dip2px(getContext(), 20), DensityUtil.dip2px(getContext(), 20));
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.rightMargin = margin;
        params.topMargin = margin;
        icon.setId(View.generateViewId());
        icon.setLayoutParams(params);
        this.addView(icon);
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
//        title.setText(summary);
        title.setMaxLines(1);
        title.setEllipsize(TextUtils.TruncateAt.END);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTextColor(Color.WHITE);
        title.setIncludeFontPadding(false);
        title.setPadding(padding,padding,padding,padding);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.RIGHT_OF, leftBar.getId());
        params.addRule(RelativeLayout.LEFT_OF, icon.getId());
        this.addView(title,params);
    }

    private void updateLeftBar(Drawable db, int color){
        leftBar.setImageDrawable(db);
        ((GradientDrawable)leftBar.getDrawable()).setColor(color);
    }

}
