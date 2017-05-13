package david.itimecalendar.calendar.util;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;


import java.util.Calendar;

import david.itimecalendar.calendar.listeners.ITimeEventInterface;

/**
 * Created by yuhaoliu on 14/03/2017.
 */

public class BaseUtil {

    public static long getAllDayLong(long withInDayTime){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(withInDayTime);
        MyCalendar myCal = new MyCalendar(cal);
        return myCal.getEndOfDayMilliseconds() - myCal.getBeginOfDayMilliseconds();
    }

    public static boolean isAllDayEvent(ITimeEventInterface event) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(event.getStartTime());
        int hour = cal.get(Calendar.HOUR);
        int minutes = cal.get(Calendar.MINUTE);
        long duration = event.getEndTime() - event.getStartTime();
        boolean isAllDay = hour == 0
                && minutes == 0
                && duration >= (getAllDayLong(event.getStartTime()) * 0.9);

        return isAllDay;
    }

    public static Drawable scaleDrawable(Drawable drawable, int width, int height){
        int wi = drawable.getIntrinsicWidth();
        int hi = drawable.getIntrinsicHeight();
        int dimDiff = Math.abs(wi - width) - Math.abs(hi - height);
        float scale = (dimDiff > 0) ? width / (float)wi : height /
                (float)hi;
        Rect bounds = new Rect(0, 0, (int)(scale * wi), (int)(scale * hi));
        drawable.setBounds(bounds);

        return drawable;
    }
}
