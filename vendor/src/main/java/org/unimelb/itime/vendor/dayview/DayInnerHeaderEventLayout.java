package org.unimelb.itime.vendor.dayview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import org.unimelb.itime.vendor.eventview.DayDraggableEventView;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;

import java.util.ArrayList;

/**
 * Created by yuhaoliu on 22/09/16.
 */
public class DayInnerHeaderEventLayout extends LinearLayout {
    ArrayList<ITimeEventInterface> events = new ArrayList<>();
    ArrayList<DayDraggableEventView> dgEvents = new ArrayList<>();

    public DayInnerHeaderEventLayout(Context context) {
        super(context);
    }

    public DayInnerHeaderEventLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

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

    public void resetView(){
        this.removeAllViews();
        events.clear();
        dgEvents.clear();
    }
}
