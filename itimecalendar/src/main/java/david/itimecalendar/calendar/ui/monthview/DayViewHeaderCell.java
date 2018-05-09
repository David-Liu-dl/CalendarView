package david.itimecalendar.calendar.ui.monthview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import david.itimecalendar.calendar.util.DensityUtil;


/**
 * Created by David Liu on 18/02/2017.
 * ${COMPANY}
 * lyhmelbourne@gmail.com
 */

public class DayViewHeaderCell extends RelativeLayout {
    private TextView dateView;
    private TextView titleView;
    private ImageView dotView;

    public DayViewHeaderCell(Context context) {
        super(context);
    }

    public DayViewHeaderCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextView getDateView() {
        return dateView;
    }

    public TextView getTitleView() {
        return titleView;
    }

    public ImageView getDotView() {
        return dotView;
    }

    public void setDateView(TextView dateView, TextView titleView, ImageView dotView) {
        this.removeAllViews();

        this.dateView = dateView;
        LayoutParams dateParams = new LayoutParams(DensityUtil.dip2px(getContext(),50), DensityUtil.dip2px(getContext(),50));
        dateParams.addRule(CENTER_IN_PARENT);
        this.dateView.setId(View.generateViewId());
        this.dateView.setLayoutParams(dateParams);

        this.titleView = titleView;
        LayoutParams titleParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.addRule(CENTER_HORIZONTAL);
        this.titleView.setLayoutParams(titleParams);

        this.dotView = dotView;
        LayoutParams dotParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dotParams.addRule(CENTER_HORIZONTAL);
        dotParams.addRule(ALIGN_PARENT_BOTTOM);
        dotParams.bottomMargin = DensityUtil.dip2px(getContext(),3);
        this.dotView.setLayoutParams(dotParams);

        this.addView(dateView);
        this.addView(titleView);
        this.addView(dotView);
    }
}
