package david.itimecalendar.calendar.ui.weekview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import david.itimecalendar.R;

/**
 * Created by yuhaoliu on 23/05/2017.
 */

public class WeekViewHeaderDeprecated extends FrameLayout {

    public int NUM_CELL = 7;
    private List<CellPair> cellPairs = new ArrayList<>();
    private UpdateAnimator updateAnimator = new UpdateAnimator();

    private int text_calendar_weekdate = R.color.text_calendar_weekdate;
    private int dayOfMonthTextSize = 16;
    private int dayOfWeekTextSize = 11;

    public WeekViewHeaderDeprecated(Context context) {
        super(context);
        init();
    }

    public WeekViewHeaderDeprecated(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.loadAttributes(attrs,context);
        init();
    }

    public WeekViewHeaderDeprecated(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.loadAttributes(attrs,context);
        init();
    }

    private void loadAttributes(AttributeSet attrs, Context context) {
        if (attrs != null && context != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.viewBody, 0, 0);
            try {
                NUM_CELL = typedArray.getInteger(R.styleable.viewBody_cellNum, NUM_CELL);
            } finally {
                typedArray.recycle();
            }
        }
    }

    private void init(){
        initCells();
    }

    private void initCells(){
        for (int i = 0; i < NUM_CELL; i++) {
            CellPair pair = new CellPair();
            cellPairs.add(pair);

            for (int j = 2; j < 4; j++) {
                Cell cell = getCell();
                cell.sub = j%2 != 0;

                if (cell.sub){
                    pair.sub = setUpCell(cell);
                    cell.setScaleX(0);
                    cell.setScaleY(0);
//                    cell.setAlpha(1);
//                    cell.setBackgroundColor(Color.RED);
                }else {
                    pair.showing = setUpCell(cell);
                    cell.setScaleX(1);
                    cell.setScaleY(1);
//                    cell.setAlpha(1);
//                    cell.setBackgroundColor(Color.GRAY);
                }

                this.addView(cell);
            }
        }
    }

    private Cell getCell(){
        Cell cell = new Cell(getContext());
        cell.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams cellParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cell.setLayoutParams(cellParams);

        return cell;
    }

    private Cell setUpCell(Cell parent){
        TextView dayOfWeekTv = new TextView(getContext());
        dayOfWeekTv.setTextSize(dayOfWeekTextSize);
        dayOfWeekTv.setTextColor(getResources().getColor(text_calendar_weekdate));
        dayOfWeekTv.setText("WED");
        dayOfWeekTv.measure(0, 0);
        dayOfWeekTv.setGravity(Gravity.CENTER);
        final int dayOfWeekTvMeasuredHeight = dayOfWeekTv.getMeasuredHeight(); //get height
        final int dayOfWeekTvMeasuredWidth =  dayOfWeekTv.getMeasuredWidth();
        LinearLayout.LayoutParams dayOfWeekTvParams
                = new LinearLayout.LayoutParams(dayOfWeekTvMeasuredWidth, dayOfWeekTvMeasuredHeight);
        dayOfWeekTvParams.gravity = Gravity.CENTER;
        dayOfWeekTv.setLayoutParams(dayOfWeekTvParams);
        parent.addView(dayOfWeekTv);
        parent.dayOfWeekTv = dayOfWeekTv;

        TextView dayOfMonthTv = new TextView(getContext());
        dayOfMonthTv.setTextSize(dayOfMonthTextSize);
        dayOfMonthTv.setTextColor(getResources().getColor(text_calendar_weekdate));
        dayOfMonthTv.setText("20");
        dayOfMonthTv.measure(0, 0);
        dayOfMonthTv.setGravity(Gravity.CENTER);
        final int dayOfMonthTvMeasuredHeight = dayOfMonthTv.getMeasuredHeight(); //get height
        final int dayOfMonthTvMeasuredWidth =  dayOfMonthTv.getMeasuredWidth();
        LinearLayout.LayoutParams dayOfMonthTvParams
                = new LinearLayout.LayoutParams(dayOfMonthTvMeasuredWidth, dayOfMonthTvMeasuredHeight);
        dayOfMonthTvParams.gravity = Gravity.CENTER;

        dayOfMonthTv.setLayoutParams(dayOfMonthTvParams);
        parent.addView(dayOfMonthTv);
        parent.dayOfMonthTv = dayOfMonthTv;

        return parent;
    }

    float dayWidth;
    float dayHeight;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i("animtest", "onMeasure: ");
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        dayWidth = (float) width/NUM_CELL;
        dayHeight = (float) height;

        int curCell = 0;

        measureChildren(0,0);

        for (CellPair cellPair: cellPairs
             ) {
            View shownCell = cellPair.showing;
            shownCell.setTranslationX(0);
            int cellWidth = shownCell.getMeasuredWidth();
            int cellHeight = shownCell.getMeasuredHeight();
            updateAnimator.moveDis = (int) (cellWidth * 0.25);

            //params for shown cell
            float startXShown = curCell * dayWidth;
            int toX = (int) (startXShown + (dayWidth/2 - cellWidth/2));
            int toY = (int) (0 + (dayHeight/2 - cellHeight/2));
            ((LayoutParams)shownCell.getLayoutParams()).leftMargin = toX;
            ((LayoutParams)shownCell.getLayoutParams()).topMargin = toY;
            //params for sub cell
            View subCell = cellPair.sub;
            subCell.setTranslationX(0);
            ((LayoutParams)subCell.getLayoutParams()).leftMargin = toX;
            ((LayoutParams)subCell.getLayoutParams()).topMargin = toY;

            curCell++;
        }
    }

    private class CellPair{
        Cell showing;
        Cell sub;

        public void swap(){
            showing.setTranslationX(0);
            sub.setTranslationX(0);
            Cell oldShown = showing;
            showing = sub;
            sub = oldShown;
        }
    }

    private class Cell extends LinearLayout{
        TextView dayOfWeekTv;
        TextView dayOfMonthTv;
        boolean sub;

        public Cell(@NonNull Context context) {
            super(context);
        }
    }

    private Date startDate = new Date();

    public void setStartDate(Date date){
        this.startDate = date;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //update view info
        for (int i = 0; i < cellPairs.size(); i++) {
            CellPair pair = cellPairs.get(i);
            cal.add(Calendar.DATE,1);
            String dayOfWeekStr = cal.getDisplayName(
                    Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).toUpperCase();
            pair.showing.dayOfWeekTv.setText(dayOfWeekStr);
            pair.showing.dayOfMonthTv.setText("" + cal.get(Calendar.DAY_OF_MONTH));
        }
    }

    public Date getStartDate() {
        return startDate;
    }

    public void updateDate(Date startDate, float progress){
        updateAnimator.updateDate(startDate, progress);
    }


    private class UpdateAnimator {
        private int moveDis = 0;
        private boolean updating = false;
        private boolean swapped = false;
        private int direction = 0;
        private float minScale = 0.5f;
        private float alphaThreshold = 0.1f;

        private void updateDate(Date startDate, @FloatRange(from=0.0, to=1.0) float progress){
            //start updating
            if (progress == 0){
                updating = true;
                swapped = false;
                //set direction
                if (WeekViewHeaderDeprecated.this.startDate.getTime() < startDate.getTime()){
                    //move to right
                    direction = -1;
                }else {
                    //move to left
                    direction = 1;
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(startDate);
                //update view info
                for (int i = 0; i < cellPairs.size(); i++) {
                    CellPair pair = cellPairs.get(i);
                    cal.add(Calendar.DATE,1);
                    String dayOfWeekStr = cal.getDisplayName(
                            Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).toUpperCase();
                    pair.sub.dayOfWeekTv.setText(dayOfWeekStr);
                    pair.sub.dayOfMonthTv.setText("" + cal.get(Calendar.DAY_OF_MONTH));
                }
            }
            //end updating
            if (progress == 1){
                updating = false;
                direction = 0;
            }

            if (updating){
                for (CellPair pair: cellPairs
                        ) {
//                    float scaleFactor = progress * 0.5f;
                    pair.showing.setTranslationX(direction * progress * moveDis);
                    pair.showing.setScaleX(1 - progress);
                    pair.showing.setScaleY(1 - progress);
//                    if (needUpdate(pair.showing.getAlpha(), 1 - progress, alphaThreshold)){
//                        pair.showing.setAlpha(1 - progress);
//                    }


                    pair.sub.setTranslationX(direction * (progress - 1) * moveDis);
                    pair.sub.setScaleX(progress);
                    pair.sub.setScaleY(progress);
//                    if (needUpdate(pair.sub.getAlpha(), progress, alphaThreshold)){
//                        pair.showing.setAlpha(progress);
//                    }
                }
                invalidate();
            }else if (!swapped){
                swapped = true;
                WeekViewHeaderDeprecated.this.startDate = startDate;
                for (CellPair pair: cellPairs
                        ) {
                    pair.swap();
                }
                requestLayout();
            }else {
                Log.i("Warning: ", "Redundant updating, drop ");
            }
        }
    }

    private boolean needUpdate(float org, float compare, float threshold){
        return  (Math.abs(compare - org) > threshold);
    }
}
