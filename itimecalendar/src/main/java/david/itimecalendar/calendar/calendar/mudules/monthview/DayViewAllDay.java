package david.itimecalendar.calendar.calendar.mudules.monthview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.developer.paul.itimerecycleviewgroup.ITimeAdapter;
import com.developer.paul.itimerecycleviewgroup.ITimeRecycleViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import david.itimecalendar.R;
import david.itimecalendar.calendar.listeners.ITimeEventInterface;
import david.itimecalendar.calendar.listeners.ITimeEventPackageInterface;
import david.itimecalendar.calendar.unitviews.DraggableEventView;
import david.itimecalendar.calendar.util.DensityUtil;
import david.itimecalendar.calendar.util.MyCalendar;
import david.itimecalendar.calendar.wrapper.WrapperEvent;

/**
 * Created by yuhaoliu on 5/06/2017.
 */

public class DayViewAllDay extends FrameLayout {

    private static final String TAG = "DayViewAllDay";
    private ITimeRecycleViewGroup recycleViewGroup;
    private AllDayAdapter adapter;
    private TextView label;
    private float leftBarWidth;
    private int NUM_CELL;

    public DayViewAllDay(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        loadAttributes(attrs, context);
        initViews();
    }

    private void loadAttributes(AttributeSet attrs, Context context) {
        if (attrs != null && context != null) {
            TypedArray typedArrayBody = context.getTheme().obtainStyledAttributes(attrs, R.styleable.viewBody, 0, 0);
            try {
                leftBarWidth = typedArrayBody.getDimension(R.styleable.viewBody_leftBarWidth, leftBarWidth);
                NUM_CELL = typedArrayBody.getInteger(R.styleable.viewBody_cellNum, NUM_CELL);
            } finally {
                typedArrayBody.recycle();
            }
        }
    }

    private void initViews(){
        this.setBackgroundColor(Color.LTGRAY);

        Context context = getContext();
        label = new TextView(context);
        label.setText("All day");
        label.setGravity(Gravity.CENTER_VERTICAL);
        FrameLayout.LayoutParams labelParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        label.setLayoutParams(labelParams);
        this.addView(label);

        recycleViewGroup = new ITimeRecycleViewGroup(context, NUM_CELL);
        recycleViewGroup.setOnSetting(new ITimeRecycleViewGroup.OnSetting() {
            @Override
            public int getItemHeight(int heightSpec) {
                return 100;
            }
        });
        adapter = new AllDayAdapter();
        recycleViewGroup.setAdapter(adapter);
        LayoutParams recycleVGParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
        recycleViewGroup.setLayoutParams(recycleVGParams);
        recycleVGParams.leftMargin = (int)leftBarWidth;
        this.addView(recycleViewGroup);
    }

    private class AllDayAdapter extends ITimeAdapter<AllDayCell>{
        private ITimeEventPackageInterface eventPackage;

        @Override
        public AllDayCell onCreateViewHolder() {
            int paddingLR = DensityUtil.dip2px(getContext(), 2);
            int paddingBT = DensityUtil.dip2px(getContext(), 2);
            AllDayCell allDayCell = new AllDayCell(getContext());
            allDayCell.setPadding(paddingLR,paddingBT,paddingLR,paddingBT);

            return allDayCell;
        }

        @Override
        public void onBindViewHolder(AllDayCell item, int index) {
            updateVisibility();

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, index);
            item.setCalendar(new MyCalendar(cal));
            item.setPackageInterface(eventPackage);

        }

        public void setPackageInterface(ITimeEventPackageInterface eventPackage) {
            this.eventPackage = eventPackage;
        }
    }

    private void updateVisibility(){
        boolean hasAllDayEvent = hasAllDayEvent();
        if (hasAllDayEvent && this.getVisibility() != VISIBLE){
            this.setVisibility(VISIBLE);
            //if not notify, first view will not be drawn
            //because of parent is GONE when child is created.
            adapter.notifyDataSetChanged();
            return;
        }

        if (!hasAllDayEvent && this.getVisibility() == VISIBLE){
            this.setVisibility(GONE);
        }
    }

    private boolean hasAllDayEvent(){
        List<AllDayCell> items = adapter.getAllCompeletedItems();
        
        if (items == null){
            return false;
        }

        for (AllDayCell item:items
             ) {
            if (item.allDayEvents.size() > 0){
                return true;
            }
        }
        return false;
    }

    private class AllDayCell extends LinearLayout{

        private static final String TAG = "AllDayCell";
        MyCalendar calendar = new MyCalendar(Calendar.getInstance());
        List<ITimeEventInterface> allDayEvents = new ArrayList<>();

        public AllDayCell(Context context) {
            super(context);
            this.setOrientation(HORIZONTAL);
        }

        private void addAllDayEvent(WrapperEvent wrapper) {
            DraggableEventView new_dgEvent = new DraggableEventView(getContext(),wrapper.getEvent(),true);
            LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100,1f);
            new_dgEvent.setLayoutParams(params);
            this.addView(new_dgEvent);
            this.allDayEvents.add(wrapper.getEvent());
        }

        private void setPackageInterface(ITimeEventPackageInterface eventPackage){
            //clear all views
            this.removeAllViews();
            this.allDayEvents.clear();

            if (eventPackage == null){
                return;
            }

            Map<Long, List<ITimeEventInterface>> regularDayEventMap = eventPackage.getRegularEventDayMap();
            Map<Long, List<ITimeEventInterface>> repeatedDayEventMap = eventPackage.getRepeatedEventDayMap();

            long startTime = calendar.getBeginOfDayMilliseconds();

            if (regularDayEventMap != null && regularDayEventMap.containsKey(startTime)){
                List<ITimeEventInterface> currentDayEvents = regularDayEventMap.get(startTime);
                for (ITimeEventInterface event : currentDayEvents) {
                    // is shown in calendar
                    if (event.isShownInCalendar() == View.VISIBLE){
                        WrapperEvent wrapperEvent = new WrapperEvent(event);
                        wrapperEvent.setFromDayBegin(startTime);
                        if (event.isAllDay()){
                            this.addAllDayEvent(wrapperEvent);
                        }
                    }
                }
            }

            if (repeatedDayEventMap != null && repeatedDayEventMap.containsKey(startTime)){
                List<ITimeEventInterface> currentDayEvents = repeatedDayEventMap.get(startTime);
                for (ITimeEventInterface event : currentDayEvents) {
                    if (event.isShownInCalendar() == View.VISIBLE){
                        WrapperEvent wrapperEvent = new WrapperEvent(event);
                        wrapperEvent.setFromDayBegin(startTime);
                        if (event.isAllDay()){
                            this.addAllDayEvent(wrapperEvent);
                        }
                    }
                }
            }
        }

        public void setCalendar(MyCalendar calendar) {
            this.calendar = calendar;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void setEventPackage(ITimeEventPackageInterface eventPackage) {
        this.adapter.setPackageInterface(eventPackage);
        this.adapter.notifyDataSetChanged();
    }

    public ITimeRecycleViewGroup getRecycleViewGroup(){
        return this.recycleViewGroup;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
