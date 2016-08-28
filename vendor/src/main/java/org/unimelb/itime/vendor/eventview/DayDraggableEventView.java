package org.unimelb.itime.vendor.eventview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.helper.DensityUtil;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;

/**
 * Created by yuhaoliu on 3/08/16.
 */
public class DayDraggableEventView extends RelativeLayout {
    private final String TAG = "MyAPP";
    private String summary = "pre";
    private String type;
    private String status;
    private int width = 0;
    private int height = 0;

    private TextView title;
    private ImageView icon;
    private ImageView leftBar;

    public DayDraggableEventView(Context context) {
        super(context);
        this.setBackground(getResources().getDrawable(R.drawable.itime_draggable_event_bg));
        ((GradientDrawable)this.getBackground()).setColor(Color.BLUE);
        init();
    }

    public DayDraggableEventView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DayDraggableEventView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initDarkLeftBorder(){
        leftBar = new ImageView(this.getContext());
        RelativeLayout.LayoutParams leftBar_params = new RelativeLayout.LayoutParams(DensityUtil.dip2px(getContext(), 3), ViewGroup.LayoutParams.MATCH_PARENT);
        leftBar_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        leftBar.setLayoutParams(leftBar_params);
        leftBar.setId(1000);
        this.addView(leftBar);
    }

    public void setSummary(String summary){
        this.summary = summary;
        title.setText(summary);
    }

    public void setTypeAndStatus(String type, String status){
        this.type = type;
        this.status = status;
        int color = Color.RED;
        switch (this.type){
            case "PRIVATE":
                color = getContext().getResources().getColor(R.color.private_et);
                break;
            case "GROUP":
                color = getContext().getResources().getColor(R.color.group_et);
                break;
            case "PUBLIC":
                color = getContext().getResources().getColor(R.color.public_et);
                break;

        }

        this.setBackground(getResources().getDrawable(R.drawable.itime_draggable_event_bg));
        ((GradientDrawable)this.getBackground()).setColor(color);
        this.getBackground().setAlpha(128);

//        leftBar = initDarkLeftBorder(getResources().getDrawable(R.drawable.itime_draggable_event_bg));
//        RelativeLayout.LayoutParams leftBar_params = new RelativeLayout.LayoutParams(DensityUtil.dip2px(getContext(), 3), ViewGroup.LayoutParams.MATCH_PARENT);
//        leftBar_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        leftBar.setLayoutParams(leftBar_params);
//        ((GradientDrawable)leftBar.getDrawable()).setColor(color);
//        this.addView(leftBar);
        updateLeftBar(getResources().getDrawable(R.drawable.itime_draggable_event_bg), color);

        //add right top icon
        this.resetIcon(getStatusIcon(status));
    }

    private int getStatusIcon(String status){
        switch (status){
            case "PENDING":
                return R.drawable.itime_question_mark;
            default:
                return -1;
        }
    }

    /****************************************************************************************/
    private void init(){
        initIcon();
        initDarkLeftBorder();
        initEventTitle();
    }

    private void initIcon(){
        icon = new ImageView(getContext());
        icon.setImageResource(R.drawable.itime_question_mark);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(DensityUtil.dip2px(getContext(), 20), DensityUtil.dip2px(getContext(), 20));
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        icon.setId(1001);
        icon.setLayoutParams(params);
        this.addView(icon);
    }

    private void resetIcon(int iconDrawable){
        icon.setImageResource(iconDrawable);
    }

    private void initEventTitle(){
        title = new TextView(getContext());
        title.setText(summary);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTextColor(Color.WHITE);
        title.setIncludeFontPadding(false);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.RIGHT_OF, leftBar.getId());
        params.addRule(RelativeLayout.LEFT_OF, icon.getId());
        this.addView(title,params);
    }

    private void updateLeftBar(Drawable db, int color){
        leftBar.setImageDrawable(db);
        ((GradientDrawable)leftBar.getDrawable()).setColor(color);
    }

    private float calculateTextBounds(TextView textView, float height){
        Paint paint = new Paint();
        Rect bounds = new Rect();

        paint.setTypeface(textView.getTypeface());
        float textSize = textView.getTextSize();
        paint.setTextSize(textSize);
        String text = textView.getText().toString();
        paint.getTextBounds(text, 0, text.length(), bounds);
        height = height - bounds.bottom + bounds.top;
        while (bounds.height() < height)
        {
            textSize++;
            paint.setTextSize(textSize);
            paint.getTextBounds(text, 0, text.length(), bounds);
        }

        return textSize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.width = MeasureSpec.getSize(widthMeasureSpec);
        this.height = (MeasureSpec.getSize(heightMeasureSpec));
        setMeasuredDimension(width,height);
    }

}
