package david.itimecalendar.calendar.calendar.mudules.monthview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import david.itimecalendar.calendar.listeners.ITimeEventPackageInterface;

/**
 * Created by yuhaoliu on 27/05/2017.
 */

public class AllDayEventLayout extends LinearLayout {
    private int NUM_CELL = 1;
    private float leftBarWidth = 100;

    private ITimeEventPackageInterface packageInterface;

    public AllDayEventLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }



}
