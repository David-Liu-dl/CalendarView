package org.unimelb.itime.vendor.dayview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by Paul on 1/08/2016.
 */
public class ScrollContainerView extends ScrollView {
    private  final String TAG = "MyAPP";
    private Context context;
    public ScrollContainerView(Context context) {
        super(context);
        this.context = context;
        this.setVerticalScrollBarEnabled(false);

    }

    public ScrollContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.setVerticalScrollBarEnabled(false);
    }

    public ScrollContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.setVerticalScrollBarEnabled(false);

    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        return super.onDragEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }

    @Override
    public void fling(int velocityY) {
        super.fling(velocityY);
    }
}
