package david.itimecalendar.calendar.util;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;


import java.util.Calendar;
import java.util.Date;

import david.itimecalendar.calendar.listeners.ITimeEventInterface;
import david.itimecalendar.calendar.listeners.ITimeComparable;

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

        int offset = (int)((beginTo - beginFrom) / (1000*60*60*24));

        return offset;
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
}
