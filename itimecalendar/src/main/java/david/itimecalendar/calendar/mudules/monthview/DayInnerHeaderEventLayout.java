package david.itimecalendar.calendar.mudules.monthview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.ArrayList;

import david.itimecalendar.calendar.listeners.ITimeEventInterface;
import david.itimecalendar.calendar.unitviews.DraggableEventView;

/**
 * Created by yuhaoliu on 22/09/16.
 */
public class DayInnerHeaderEventLayout extends LinearLayout {
    ArrayList<ITimeEventInterface> events = new ArrayList<>();
    ArrayList<DraggableEventView> dgEvents = new ArrayList<>();

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

    public ArrayList<DraggableEventView> getDgEvents() {
        return dgEvents;
    }

    public void setDgEvents(ArrayList<DraggableEventView> dgEvents) {
        this.dgEvents = dgEvents;
    }

    public void resetView(){
        this.removeAllViews();
        events.clear();
        dgEvents.clear();
    }
}
