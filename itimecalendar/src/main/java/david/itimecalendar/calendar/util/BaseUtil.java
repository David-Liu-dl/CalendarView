package david.itimecalendar.calendar.util;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import david.itimecalendar.calendar.listeners.ITimeEventInterface;
import david.itimecalendar.calendar.listeners.ITimeComparable;
import david.itimecalendar.calendar.ui.CalendarConfig;

/**
 * Created by yuhaoliu on 14/03/2017.
 */

public class BaseUtil {
    public static String WEEK_DAY_MONTH_EN = "EEE, dd MMM";
    public static String WEEK_DAY_MONTH_ZH = "MM月dd日 EEE";

    public static String HOUR_MIN = "HH:mm";
    public static String HOUR = "HH";
    public static String HOUR_MIN_A = "hh:mm a";


    public static String getUnitTimePattern(CalendarConfig calendarConfig){
        switch (calendarConfig.time){
            case HH:
                return HOUR_MIN;
            case HH_A:
                return HOUR_MIN_A;
            default:
                return HOUR_MIN;
        }
    }

    public static String getWeekDayMonthPattern(Locale locale){
        if (locale == Locale.CHINESE){
            return WEEK_DAY_MONTH_ZH;
        }else {
            return WEEK_DAY_MONTH_EN;
        }
    }

    public static String getFormatTimeString(long time, String format, Locale locale){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        SimpleDateFormat fmt = new SimpleDateFormat(format, locale);
        return fmt.format(c.getTime());
    }

    public static long getAllDayLong(long withInDayTime){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(withInDayTime);
        MyCalendar myCal = new MyCalendar(cal);
        return myCal.getEndOfDayMilliseconds() - myCal.getBeginOfDayMilliseconds();
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

    public static void printEventTime(String preTag, ITimeEventInterface eventInterface){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(eventInterface.getStartTime());
        String sT = cal.getTime().toString();
        cal.setTimeInMillis(eventInterface.getEndTime());
        String eT = cal.getTime().toString();
    }

    public static int getDatesDifference(long from, long to){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(from);
        MyCalendar myCal = new MyCalendar(cal);
        long beginFrom = myCal.getBeginOfDayMilliseconds();

        cal.setTimeInMillis(to);
        MyCalendar myCal2 = new MyCalendar(cal);
        long beginTo = myCal2.getBeginOfDayMilliseconds();

        int dateDiff = (int)((beginTo - beginFrom) / (1000*60*60*24));

        return dateDiff;
    }

    public static ImageView getDivider(Context context, int resourceId) {
        ImageView dividerImgV;
        //divider
        dividerImgV = new ImageView(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dividerImgV.setLayoutParams(params);
        dividerImgV.setImageDrawable(context.getResources().getDrawable(resourceId));
        return dividerImgV;
    }

    public static boolean isToday(Calendar calendar){
        return DateUtils.isToday(calendar.getTimeInMillis());
    }

    public static Date getDate(long time){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return cal.getTime();
    }

    public static <O extends ITimeComparable> boolean isExpired(O o){
        long nowTime = Calendar.getInstance().getTimeInMillis();
        //check if timeslot expired
        return  o != null && nowTime >= o.getStartTime();
    }

    public static boolean isExpired(long time){
        long nowTime = Calendar.getInstance().getTimeInMillis();
        //check if time expired
        return nowTime >= time;
    }

    public static long getDayBeginMilliseconds(long startTime){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);

        return calendar.getTimeInMillis();
    }
}
