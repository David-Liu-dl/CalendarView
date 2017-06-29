package david.itimecalendar.calendar.ui.monthview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import david.itimecalendar.R;
import david.itimecalendar.calendar.util.DensityUtil;
import david.itimecalendar.calendar.util.MyCalendar;

/**
 * Created by yuhaoliu on 5/08/16.
 */

public class DayViewHeader extends LinearLayout {
    public String TAG = "DayViewHeader";
    public int rowPst = -1;

    private int paddingBg;
    private int paddingText;

    private int textSize = 15;

    private int currentSelectedPst = 0;
    //means default is none;
    private int todayPst = -1;

    private int monthTitleSize = 11;

    /**
     * Color category
     */
    /*************************** Start of Color Setting **********************************/
    private int color_headerTodayTextColor = Color.BLACK;
    private int color_headerNormalTextColorOdd = Color.BLACK;
    private int color_headerNormalTextColorEven = Color.BLACK;
    private int color_headerSelectedTextColor = Color.WHITE;
    private int color_todaySelectedCircleBgColor = Color.RED;
    private int color_otherSelectedCircleBgColor = getResources().getColor(R.color.group_et);
    /*************************** End of Color Setting **********************************/

    /*************************** Start of Resources Setting ****************************/
    private int rs_header_bg = R.drawable.itime_day_rectangle;
    private int rs_event_dot = R.drawable.itime_event_dot;
    /*************************** End of Resources Setting ****************************/

    private Context context;
    private ViewGroup parent = this;
    private LinearLayout dateLayout;
    public ArrayList<DayViewHeaderCell> textViews = new ArrayList<>();
    
    private OnCalendarHeaderDayClickListener onCalendarHeaderDayClickListener;
    private OnCheckIfHasEvent onCheckIfHasEvent;
    
    private Paint monthTitlePaint = new Paint();

    MyCalendar currentCalendar;

    public DayViewHeader(Context context) {
        super(context);
    }

    public DayViewHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadAttributes(attrs,context);
        init();
    }

    public DayViewHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadAttributes(attrs,context);
        init();
    }

    private void init(){
        this.setOrientation(LinearLayout.HORIZONTAL);
        this.context = parent.getContext();

        this.dateLayout = new LinearLayout(parent.getContext());
        parent.addView(dateLayout);

        //init paint
        this.monthTitleSize = DensityUtil.dip2px(getContext(),12);
        monthTitlePaint.setTextSize(this.monthTitleSize);
        monthTitlePaint.setStyle(Paint.Style.FILL);
        monthTitlePaint.setTextAlign(Paint.Align.LEFT);
        paddingBg = DensityUtil.dip2px(context,6);
        paddingText = DensityUtil.dip2px(context,2);
    }
    private void loadAttributes(AttributeSet attrs, Context context) {
        if (attrs != null && context != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.dayStyle, 0, 0);
            try {
            } finally {
                color_headerTodayTextColor = typedArray.getColor(R.styleable.dayStyle_headerTodayTextColor, color_headerTodayTextColor);
                color_headerNormalTextColorOdd = typedArray.getColor(R.styleable.dayStyle_headerNormalTextColorOdd, color_headerNormalTextColorOdd);
                color_headerNormalTextColorEven = typedArray.getColor(R.styleable.dayStyle_headerNormalTextColorEven, color_headerNormalTextColorEven);
                color_headerSelectedTextColor = typedArray.getColor(R.styleable.dayStyle_headerSelectedTextColor, color_headerSelectedTextColor);
                color_todaySelectedCircleBgColor = typedArray.getColor(R.styleable.dayStyle_todaySelectedCircleBgColor, color_todaySelectedCircleBgColor);
                color_otherSelectedCircleBgColor = typedArray.getColor(R.styleable.dayStyle_otherSelectedCircleBgColor, color_otherSelectedCircleBgColor);

                typedArray.recycle();
            }
        }
    }

    private boolean checkEqualDay(Calendar c1, Calendar c2){
        return
                c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                &&
                        c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                &&
                        c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }

    public void setCalendar(MyCalendar calendar){
        this.currentCalendar = calendar;
    }

    public MyCalendar getCalendar(){
        return this.currentCalendar;
    }

    public void clearAllBg(){
        for (DayViewHeaderCell cell:textViews) {
            cell.getDateView().setBackgroundResource(0);
            cell.getTitleView().setText("");
            cell.getDotView().setImageDrawable(null);
            cell.setPadding(paddingText, paddingText, paddingText, paddingText);

            if (textViews.indexOf(cell) == todayPst)
                cell.getDateView().setTextColor(color_headerTodayTextColor);
            else{
                cell.getDateView().setTextColor(((Integer)cell.getTag())==0? color_headerNormalTextColorEven : color_headerNormalTextColorOdd);
            }
        }
    }

    private void setCircleColor(TextView tv, boolean isToday){
        int color;

        if (isToday){
            color = color_todaySelectedCircleBgColor;
        }else{
            color = color_otherSelectedCircleBgColor;
        }
        Drawable drawable = parent.getResources().getDrawable(rs_header_bg);
        tv.setBackgroundDrawable(drawable);
        tv.setTextColor(color_headerSelectedTextColor);
        ((GradientDrawable)tv.getBackground()).setColor(color);
    }

    private void setFstDayOfMonthText(TextView tv){
        if (tv.getLineCount() > 1){
            tv.setText(tv.getText().subSequence(0,1));
        }
    }

    private void resetParameters(){
        todayPst = -1;
    }
    /************************public methods*****************************/
    public void setOnCalendarHeaderDayClickListener(OnCalendarHeaderDayClickListener onCalendarHeaderDayClickListner){
        this.onCalendarHeaderDayClickListener = onCalendarHeaderDayClickListner;
    }

    public void setOnCheckIfHasEvent(OnCheckIfHasEvent onCheckIfHasEvent){
        this.onCheckIfHasEvent = onCheckIfHasEvent;
    }

    public int getCurrentSelectedIndex(){
        return this.currentSelectedPst;
    }

    public void updateDate(){
        clearAllBg();
        resetParameters();
        Calendar todayCalendar = Calendar.getInstance();
        Calendar calendar = this.currentCalendar.getCalendar();

        if (textViews.size() != 0){
            for (int day = 0; day < 7; day++) {
                DayViewHeaderCell cell = textViews.get(day);
                TextView dateView = cell.getDateView();
                String date = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                dateView.setGravity(Gravity.CENTER);

                //check if today has item
                Calendar checkCalendar = this.currentCalendar.getCalendar();
                checkCalendar.add(Calendar.DATE, day);
                checkCalendar.set(Calendar.HOUR_OF_DAY, 0);
                checkCalendar.set(Calendar.MINUTE,0);
                checkCalendar.set(Calendar.SECOND,0);
                checkCalendar.set(Calendar.MILLISECOND,0);
                long dayMilliSeconds = checkCalendar.getTimeInMillis();

                boolean thisDayHasEvent = false;
                if (onCheckIfHasEvent != null){
                    thisDayHasEvent = onCheckIfHasEvent.todayHasEvent(dayMilliSeconds);
                }else{
                    Log.i(TAG, "updateDate: onCheckIfHasEvent is null");
                }

                //flag of month even or not
                int month = calendar.get(calendar.MONTH);
                int oddEvent = month%2;
                //change month title paint color
                monthTitlePaint.setColor((oddEvent==0) ? color_headerNormalTextColorEven : color_headerNormalTextColorOdd);

                dateView.setTag(oddEvent);
                if (checkEqualDay(todayCalendar, calendar)) {
                    dateView.setTextColor(color_headerTodayTextColor);
                    todayPst = day;
                    currentSelectedPst = day;
                }else{
                    dateView.setTextColor((oddEvent==0) ? color_headerNormalTextColorEven : color_headerNormalTextColorOdd);
                }

                //add title if equals "1", means there is start of month, add the short-month title
                if (date.equals("1")){
                    String monthTitle = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
                    cell.getTitleView().setText(monthTitle);
                }
                //add dot
                if (thisDayHasEvent){
                    cell.getDotView().setImageDrawable(getResources().getDrawable(rs_event_dot));
                }

                dateView.setText(date);
                dateView.requestLayout();
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
            }

            parent.invalidate();
        }
    }

    public void initCurrentWeekHeaders(){
        for (int day = 0; day < 7; day++) {
            DayViewHeaderCell cell = new DayViewHeaderCell(context);
            TextView dateView = new TextView(parent.getContext());
            cell.setTag(0);
            TextView titleView = new TextView(parent.getContext());
            ImageView dotView = new ImageView(parent.getContext());
            cell.setDateView(dateView,titleView,dotView);
            textViews.add(cell);
        }
    }

    public void resizeCurrentWeekHeaders(){
        Calendar calendar = this.currentCalendar.getCalendar();
        DisplayMetrics dm = parent.getResources().getDisplayMetrics();
        int cellWidth = dm.widthPixels/7 - 20;
        for (int day = 0; day < textViews.size() ; day++) {
            //init date
            DayViewHeaderCell cell = textViews.get(day);
            TextView dateView = cell.getDateView(); //new TextView(context);
            dateView.setTextSize(textSize);
            cell.getTitleView().setTextSize((int)(textSize * 0.65));
            cell.setOnClickListener(new cellOnClickListener());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(cellWidth, cellWidth,1.0f);//viewWidth
            params.topMargin = 0;
            params.leftMargin = 10;
            params.rightMargin = 10;
            params.bottomMargin = 0;
            params.gravity = Gravity.CENTER_VERTICAL;
            cell.setLayoutParams(params);
            dateView.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));

            dateLayout.addView(cell);
            //update the date after using
            calendar.set(Calendar.DATE,calendar.get(Calendar.DATE)+1);
        }
        dateLayout.requestLayout();
        dateLayout.invalidate();
        parent.requestLayout();
        parent.invalidate();
    }

    public void performNthDayClick(int index){
        this.textViews.get(index).performClick();
    }

    public void performFstDayClick(){
        this.textViews.get(0).performClick();
    }

    public void performLastDayClick(){
        this.textViews.get(textViews.size()-1).performClick();
    }

    public void nextPerformClick(){
        if (currentSelectedPst + 1 < this.textViews.size()){
            this.textViews.get(currentSelectedPst + 1).performClick();
        }else {
            Log.i(TAG, "nextPerformClick: " + (currentSelectedPst + 1));
            Log.i(TAG, "nextPerformClick: out of bounds" );
        }
    }

    public void previousPerformClick(){
        if (currentSelectedPst - 1 >= 0){
            this.textViews.get(currentSelectedPst - 1).performClick();
        }else{
            Log.i(TAG, "previousPerformClick: out of bounds" );
            Log.i(TAG, "previousPerformClick: " + (currentSelectedPst - 1));
        }
    }
    /***************************************************************/
    private class cellOnClickListener implements OnClickListener{

        @Override
        public void onClick(View view) {
            if (onCalendarHeaderDayClickListener != null){
                //global changing
                onCalendarHeaderDayClickListener.setCurrentSelectPst(rowPst);
                onCalendarHeaderDayClickListener.onClick(view);

                //local changing
                DayViewHeaderCell cell = (DayViewHeaderCell) view;
                TextView tv = cell.getDateView();
                cell.setPadding(paddingBg, paddingBg, paddingBg, paddingBg);
                cell.getTitleView().setText("");
                cell.getDotView().setImageDrawable(null);

                boolean isToday = textViews.indexOf(cell) == todayPst;
                setFstDayOfMonthText(tv);
                setCircleColor(tv,isToday);

                //body changing
                currentSelectedPst = textViews.indexOf(cell);
                onCalendarHeaderDayClickListener.setCurrentSelectIndexInRow(currentSelectedPst);

                //synchronize body part
                Calendar cal = currentCalendar.getCalendar();
                cal.add(Calendar.DATE, currentSelectedPst);
                onCalendarHeaderDayClickListener.onDateSelected(cal.getTime());
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.initCurrentWeekHeaders();
    }

    /**
     * your customized listener
     */

    public interface OnCalendarHeaderDayClickListener{
        void onClick(View v);
        void setCurrentSelectPst(int rowPst);
        void setCurrentSelectIndexInRow(int indexInRow);
//        void synBodyPart(MyCalendar fstDayDate, int rowPst, int indexInRow);
        void onDateSelected(Date date);
    }

    public interface OnCheckIfHasEvent{
        boolean todayHasEvent(long startOfDay);
    }
}
