package org.unimelb.itime.vendor.dayview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.eventview.DayDraggableEventView;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;
import org.unimelb.itime.vendor.timeslot.TimeSlotView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by yuhaoliu on 21/09/16.
 */
public class DayInnerBodyEventLayout extends ViewGroup {
    private static final String TAG = "MyAPP";
    ArrayList<ITimeEventInterface> events = new ArrayList<>();
    ArrayList<DayDraggableEventView> dgEvents = new ArrayList<>();

    public ArrayList<ITimeEventInterface> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<ITimeEventInterface> events) {
        this.events = events;
    }

    public ArrayList<DayDraggableEventView> getDgEvents() {
        return dgEvents;
    }

    public void setDgEvents(ArrayList<DayDraggableEventView> dgEvents) {
        this.dgEvents = dgEvents;
    }

    public DayInnerBodyEventLayout(Context context) {
        super(context);
    }

    public DayInnerBodyEventLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int cCount = getChildCount();
        int paddingLeft  = this.getPaddingLeft();
        int paddingRight  = this.getPaddingRight();
        width = width - (paddingLeft + paddingRight);

        for (int i = 0; i < cCount; i++) {
            if (getChildAt(i) instanceof TimeSlotView) {
                getChildAt(i).getLayoutParams().width = width;
            }

            if (!(getChildAt(i) instanceof DayDraggableEventView)) {
                continue;
            }
            DayDraggableEventView eventView = (DayDraggableEventView) getChildAt(i);
            DayDraggableEventView.LayoutParams params = (DayDraggableEventView.LayoutParams) eventView.getLayoutParams();
            DayDraggableEventView.PosParam pos = eventView.getPosParam();
            if (pos == null) {
                // for creating a new event
                // the pos parameter is null, because we just mock it
                params.width = width;
                continue;
            }
            int eventWidth = width / pos.widthFactor;
            int leftMargin = eventWidth * pos.startX;
            params.width = eventWidth;
            eventView.measure(0,0);
            eventView.setX(leftMargin + 1 * pos.startX);
            eventView.setY(pos.topMargin);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int cCount = getChildCount();
        int paddingLeft  = this.getPaddingLeft();
        int paddingRight  = this.getPaddingRight();

        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            if (child instanceof DayDraggableEventView){
                child.layout(paddingLeft, 0, paddingLeft + child.getLayoutParams().width, child.getLayoutParams().height);
            }
            
            if (child instanceof TimeSlotView){
                child.layout(paddingLeft, 0, paddingLeft + child.getLayoutParams().width, child.getLayoutParams().height);
            }
        }
    }

    public void resetView(){
        this.removeAllViews();
        events.clear();
        dgEvents.clear();
    }
}
