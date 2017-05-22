package david.itimecalendar.calendar.calendar.mudules.monthview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import david.itimecalendar.calendar.unitviews.DraggableEventView;
import david.itimecalendar.calendar.unitviews.DraggableTimeSlotView;
import david.itimecalendar.calendar.unitviews.RecommendedSlotView;
import david.itimecalendar.calendar.wrapper.WrapperEvent;

/**
 * Created by yuhaoliu on 21/09/16.
 */
public class DayInnerBodyEventLayout extends ViewGroup {
    private static final String TAG = "DayInnerBodyEventLayout";
    ArrayList<WrapperEvent> events = new ArrayList<>();
    ArrayList<DraggableEventView> dgEvents = new ArrayList<>();

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
                    measureChild (getChildAt(i), widthMeasureSpec, heightMeasureSpec);
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
            DraggableEventView.LayoutParams params = (DraggableEventView.LayoutParams) eventView.getLayoutParams();
            DraggableEventView.PosParam pos = eventView.getPosParam();
            if (pos == null) {
                // for creating a new event
                // the pos parameter is null, because we just mock it
                params.width = width;
                continue;
            }
            int eventWidth = width / pos.widthFactor;
            int leftMargin = eventWidth * pos.startX;
            params.width = eventWidth;
            params.left = leftMargin + 1 * pos.startX;
            params.top = pos.topMargin;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int cCount = getChildCount();
        int paddingLeft  = this.getPaddingLeft();

        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            if (child instanceof DraggableEventView){
                DraggableEventView.LayoutParams params = (DraggableEventView.LayoutParams) child.getLayoutParams();
                child.layout(paddingLeft + params.left, params.top, paddingLeft + params.left + child.getLayoutParams().width, params.top + child.getLayoutParams().height);
            }

            if (child instanceof DraggableTimeSlotView || child instanceof RecommendedSlotView){
                DayInnerBodyEventLayout.LayoutParams params = (DayInnerBodyEventLayout.LayoutParams) child.getLayoutParams();
                child.layout(paddingLeft + params.left, params.top, paddingLeft + params.left + child.getLayoutParams().width, params.top + child.getLayoutParams().height);
            }
        }
    }

    public void resetView(){
        this.removeAllViews();
        events.clear();
        dgEvents.clear();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

//        switch (event.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                Log.i(TAG, "onTouchEvent: ACTION_DOWN");
//                break;
//            case MotionEvent.ACTION_UP:
//                Log.i(TAG, "onTouchEvent: ACTION_UP");
//                break;
//            case MotionEvent.ACTION_MOVE:
//                Log.i(TAG, "onTouchEvent: ACTION_MOVE");
//                break;
//        }
//        Log.i(TAG, "onTouchEvent: R: " + );
//        return super.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


}
