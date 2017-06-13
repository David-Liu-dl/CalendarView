package david.itime_calendar;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by yuhaoliu on 11/06/2017.
 */

public class TestView extends FrameLayout {
    private static final String TAG = "TestView";
    //    private DayViewBodyCell cell;
    TextView textView;

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
        textView = new TextView(getContext());
        textView.setText("12312321321321321");
        textView.setBackgroundColor(Color.RED);
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 300);
        textView.setLayoutParams(params);
        addView(textView);
        textView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                        view){
                    @Override
                    public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
//                        super.onProvideShadowMetrics(outShadowSize, outShadowTouchPoint);
                        final View view = getView();
                        if (view != null) {
                            outShadowSize.set(300, 300);
                            outShadowTouchPoint.set(0, 0);
                        } else {
//                            Log.e(View.VIEW_LOG_TAG, "Asked for drag thumb metrics but no view");
                        }
                    }
                };

                view.startDrag(data, shadowBuilder, view, 0);

                return true;
            }
        });


        this.setOnDragListener(new OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if (event.getAction() == DragEvent.ACTION_DRAG_LOCATION){
                    float y = event.getY();
                    Log.i(TAG, "onDrag: " + y);
                }
                return true;
            }
        });
    }

//    private void init(){
//        cell = new DayViewBodyCell(getContext());
//        cell.setOnBodyListener(new EventController.OnEventListener() {
//            @Override
//            public boolean isDraggable(DraggableEventView eventView) {
//                return false;
//            }
//
//            @Override
//            public void onEventCreate(DraggableEventView eventView) {
//
//            }
//
//            @Override
//            public void onEventClick(DraggableEventView eventView) {
//
//            }
//
//            @Override
//            public void onEventDragStart(DraggableEventView eventView) {
//
//            }
//
//            @Override
//            public void onEventDragging(DraggableEventView eventView, MyCalendar curAreaCal, int x, int y) {
//
//            }
//
//            @Override
//            public void onEventDragDrop(DraggableEventView eventView) {
//
//            }
//        });
//
//
//        ViewGroup.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        this.addView(cell,params);
//    }


}
