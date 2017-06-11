package david.itime_calendar;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import david.itimecalendar.calendar.mudules.monthview.DayViewBodyCell;
import david.itimecalendar.calendar.mudules.monthview.EventController;
import david.itimecalendar.calendar.unitviews.DraggableEventView;
import david.itimecalendar.calendar.util.MyCalendar;

/**
 * Created by yuhaoliu on 11/06/2017.
 */

public class TestView extends FrameLayout {
    private DayViewBodyCell cell;

    public TestView(@NonNull Context context) {
        super(context);
        init();
    }

    public TestView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TestView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        cell = new DayViewBodyCell(getContext());
        cell.setOnBodyListener(new EventController.OnEventListener() {
            @Override
            public boolean isDraggable(DraggableEventView eventView) {
                return false;
            }

            @Override
            public void onEventCreate(DraggableEventView eventView) {

            }

            @Override
            public void onEventClick(DraggableEventView eventView) {

            }

            @Override
            public void onEventDragStart(DraggableEventView eventView) {

            }

            @Override
            public void onEventDragging(DraggableEventView eventView, MyCalendar curAreaCal, int x, int y) {

            }

            @Override
            public void onEventDragDrop(DraggableEventView eventView) {

            }
        });


        ViewGroup.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(cell,params);
    }


}
