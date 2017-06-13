package david.itimecalendar.calendar.ui.monthview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import david.itimecalendar.calendar.ui.unitviews.DraggableEventView;
import david.itimecalendar.calendar.ui.unitviews.DraggableTimeSlotView;
import david.itimecalendar.calendar.ui.unitviews.RecommendedSlotView;
import david.itimecalendar.calendar.wrapper.WrapperEvent;
import david.itimecalendar.calendar.wrapper.WrapperTimeSlot;

/**
 * Created by yuhaoliu on 21/09/16.
 */
public class DayInnerBodyLayout extends ViewGroup {
    private static final String TAG = "DayInnerBodyEventLayout";
    ArrayList<WrapperEvent> events = new ArrayList<>();
    ArrayList<DraggableEventView> dgEvents = new ArrayList<>();
    ArrayList<WrapperTimeSlot> slots = new ArrayList<>();

    public ArrayList<WrapperEvent> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<WrapperEvent> events) {
        this.events = events;
    }

    public ArrayList<DraggableEventView> getDgEvents() {
        return dgEvents;
    }

    public void setDgEvents(ArrayList<DraggableEventView> dgEvents) {
        this.dgEvents = dgEvents;
    }

    public ArrayList<WrapperTimeSlot> getSlots() {
        return slots;
    }

    public void setSlots(ArrayList<WrapperTimeSlot> slots) {
        this.slots = slots;
    }

    public DayInnerBodyLayout(Context context) {
        super(context);
    }

    public DayInnerBodyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        public int left = 0;
        public int top = 0;

        public int relativeMarginLeft = 0;
        public int relativeMarginRight = 0;

        public LayoutParams(Context arg0, AttributeSet arg1) {
            super(arg0, arg1);
        }

        public LayoutParams(int arg0, int arg1) {
            super(arg0, arg1);
        }

        public LayoutParams(ViewGroup.LayoutParams arg0) {
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
            if (getChildAt(i) instanceof DraggableTimeSlotView) {
                getChildAt(i).getLayoutParams().width = width;
                if (getChildAt(i).getVisibility() != View.GONE) {
                    //Make or work out measurements for children here (MeasureSpec.make...)
                    DraggableTimeSlotView timeSlotView = (DraggableTimeSlotView) getChildAt(i);
                    DayInnerBodyLayout.LayoutParams params =  (DayInnerBodyLayout.LayoutParams) timeSlotView.getLayoutParams();
                    DraggableTimeSlotView.PosParam pos = timeSlotView.getPosParam();
                    if (pos == null) {
                        // for creating a new item
                        // the pos parameter is null, because we just mock it
                        params.width = width;
                        continue;
                    }
                    int timeslotConsumedWidth = width/pos.widthFactor;
                    int leftMargin = timeslotConsumedWidth * pos.startX;
                    params.width = timeslotConsumedWidth;
                    params.left = leftMargin + 1 * pos.startX;
                    params.top = pos.topMargin;
                    // measure child with correct spec
                    int childWidthSpec = MeasureSpec.makeMeasureSpec(timeslotConsumedWidth,MeasureSpec.EXACTLY);
                    int childHeightSpec = heightMeasureSpec;
                    measureChild(getChildAt(i), childWidthSpec, childHeightSpec);
                }
            }

            if (getChildAt(i) instanceof RecommendedSlotView){
                getChildAt(i).getLayoutParams().width = width;
                if (getChildAt(i).getVisibility() != View.GONE) {
                    //Make or work out measurements for children here (MeasureSpec.make...)
                    measureChild (getChildAt(i), widthMeasureSpec, heightMeasureSpec);
                }
            }

            if (!(getChildAt(i) instanceof DraggableEventView)) {
                continue;
            }
            DraggableEventView eventView = (DraggableEventView) getChildAt(i);
            DayInnerBodyLayout.LayoutParams params =  (DayInnerBodyLayout.LayoutParams) eventView.getLayoutParams();
            DraggableEventView.PosParam pos = eventView.getPosParam();
            if (pos == null) {
                // for creating a new item
                // the pos parameter is null, because we just mock it
                params.width = width;
                continue;
            }
            int eventConsumedWidth = width/pos.widthFactor;
            int leftMargin = eventConsumedWidth * pos.startX;
            params.width = eventConsumedWidth;
            params.left = leftMargin + 1 * pos.startX;
            params.top = pos.topMargin;
            // measure child with correct spec
            int childWidthSpec = MeasureSpec.makeMeasureSpec(eventConsumedWidth,MeasureSpec.EXACTLY);
            int childHeightSpec = heightMeasureSpec;
            measureChild(getChildAt(i), childWidthSpec, childHeightSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int cCount = getChildCount();
        int paddingLeft  = this.getPaddingLeft();
        int paddingTop  = this.getPaddingTop();

        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            if (child instanceof DraggableEventView || child instanceof DraggableTimeSlotView || child instanceof RecommendedSlotView){
                DayInnerBodyLayout.LayoutParams params = (DayInnerBodyLayout.LayoutParams) child.getLayoutParams();
                child.layout(
                        paddingLeft + params.left + params.relativeMarginLeft,
                        params.top + paddingTop,
                        paddingLeft + params.left + child.getLayoutParams().width - (params.relativeMarginLeft + params.relativeMarginRight),
                        params.top + child.getLayoutParams().height + paddingTop);
            }

//            if (){
//                DayInnerBodyLayout.LayoutParams params = (DayInnerBodyLayout.LayoutParams) child.getLayoutParams();
//                child.layout(paddingLeft + params.left, params.top, paddingLeft + params.left + child.getLayoutParams().width, params.top + child.getLayoutParams().height);
//            }
        }
    }

    public void clearViews(){
        this.removeAllViews();
        events.clear();
        dgEvents.clear();
        slots.clear();
    }
}
