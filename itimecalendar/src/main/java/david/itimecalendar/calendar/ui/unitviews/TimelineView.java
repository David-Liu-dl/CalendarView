package david.itimecalendar.calendar.ui.unitviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import david.itimecalendar.R;
import david.itimecalendar.calendar.util.DensityUtil;

/**
 * Created by David Liu on 17/7/17.
 * ${COMPANY}
 * lyhmelbourne@gmail.com
 */

public class TimelineView extends View {
    final Paint paint = new Paint();
    //dp
    private float lineHeight = 1;

    public TimelineView(Context context) {
        super(context);
        init();
    }

    public TimelineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimelineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setLineHeight(float lineHeight) {
        this.lineHeight = lineHeight;
        paint.setStrokeWidth(lineHeight);

    }

    private void init(){
        lineHeight = DensityUtil.dip2px(getContext(),lineHeight);
        paint.setColor(getContext().getResources().getColor(R.color.brand_main));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(lineHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float width = (float) getWidth();
        float height = (float) getHeight();
        float radius = height/2;
        //draw line
        canvas.drawLine(0,(height - lineHeight)/2, width, (height - lineHeight)/2, paint);
        canvas.drawCircle(0,radius,radius, paint);
    }
}
