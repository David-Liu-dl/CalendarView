package org.unimelb.itime.vendor.dayview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import org.unimelb.itime.vendor.eventview.DayDraggableEventView;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;

import java.util.ArrayList;

/**
 * Created by yuhaoliu on 21/09/16.
 */
public class DayEventLayout extends RelativeLayout {
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

    public DayEventLayout(Context context) {
        super(context);
    }

    public DayEventLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void resetView(){
        this.removeAllViews();
        events.clear();
        dgEvents.clear();
    }
}
