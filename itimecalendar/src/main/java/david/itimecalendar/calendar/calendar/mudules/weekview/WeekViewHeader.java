package david.itimecalendar.calendar.calendar.mudules.weekview;

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

public class WeekViewHeader extends FrameLayout {

    public int NUM_CELL = 7;
    private List<CellPair> cellPairs = new ArrayList<>();

    public WeekViewHeader(Context context) {
        super(context);
        init();
    }

    public WeekViewHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.loadAttributes(attrs,context);
        init();
    }

    public WeekViewHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
                    cell.setAlpha(0);
//                    cell.setBackgroundColor(Color.RED);
                }else {
                    pair.showing = setUpCell(cell);
                    cell.setAlpha(1);
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
        LinearLayout.LayoutParams dayOfWeekTvParams
                = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dayOfWeekTvParams.gravity = Gravity.CENTER;
        dayOfWeekTv.setLayoutParams(dayOfWeekTvParams);
        parent.addView(dayOfWeekTv);
        parent.dayOfWeekTv = dayOfWeekTv;

        TextView dayOfMonthTv = new TextView(getContext());
        LinearLayout.LayoutParams dayOfMonthTvParams
                = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
            distance = cellWidth;

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

    public void setStartDate(Date date){
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

    private int distance = 0;
    private boolean updating = false;
    private boolean swapped = false;
    private Date startDate = new Date();
    private int direction = 0;

    public void updateDate(Date startDate, int progress){
        float progressF = progress/(float)100;

        //start updating
        if (progress <= 0){
            updating = true;
            swapped = false;
            //set direction
            if (this.startDate.getTime() < startDate.getTime()){
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
        if (progress >= 99){
            updating = false;
            direction = 0;
        }

        if (updating){
            for (CellPair pair: cellPairs
                    ) {
                float minScale = 0.5f;
                float scaleFactor = progressF * 0.5f;
                pair.showing.setTranslationX(direction * progressF * distance);
                pair.showing.setAlpha(1 - progressF);
                pair.showing.setScaleX(1 - progressF);
                pair.showing.setScaleY(1 - progressF);

                pair.sub.setTranslationX(direction * (progressF - 1) * distance);
                pair.sub.setAlpha(progressF);
                pair.sub.setScaleX(minScale + scaleFactor);
                pair.sub.setScaleY(minScale + scaleFactor);
            }
        }else if (!swapped){
            swapped = true;
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
