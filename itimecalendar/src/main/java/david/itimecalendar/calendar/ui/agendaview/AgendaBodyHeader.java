package david.itimecalendar.calendar.ui.agendaview;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import david.itimecalendar.R;
import david.itimecalendar.calendar.util.DensityUtil;
import david.itimecalendar.calendar.util.MyCalendar;

/**
 * Created by yuhaoliu on 6/09/16.
 */
public class AgendaBodyHeader extends LinearLayout {

    private static final String TAG = "MyAPP2";

    /**
     * Color category
     */
    /*************************** Start of Color Setting **********************************/
    private int color_title = R.color.title_text_color;
    private int color_title_bg = R.color.title_bg_color;
    private int color_title_today = R.color.brand_main;
    private int color_title_bg_today = R.color.title_today_bg_color;
    /*************************** End of Color Setting **********************************/

    /*************************** Start of Resources Setting ****************************/
    private int rs_header_divider = R.drawable.itime_header_divider_line;
    /*************************** End of Resources Setting ****************************/


    private LinearLayout contentLayout;

    private TextView mentionTv;
    private TextView nthTv;
    private TextView monthTv;
    private TextView dayOfWeekTv;

    private String mention = "";
    private String nth;
    private String month;
    private String dayOfWeek;

    private int titleSize = 12;
    private int titleColor;
    private int titleBgColor;
    private int textPadding;

    private MyCalendar myCalendar = new MyCalendar(Calendar.getInstance());
    private int currentDayType = -2;
    private Context context;

    public AgendaBodyHeader(Context context) {
        super(context);
        this.context = context;
        RelativeLayout.LayoutParams rowHeaderParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(rowHeaderParams);
        this.setOrientation(LinearLayout.VERTICAL);

        this.contentLayout = new LinearLayout(context);
        contentLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams contentLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, DensityUtil.dip2px(context,25));
        contentLayout.setPadding(DensityUtil.dip2px(context, 10),0,DensityUtil.dip2px(context, 10),0);
        contentLayout.setLayoutParams(contentLayoutParams);
        contentLayout.setGravity(Gravity.CENTER_VERTICAL);
        this.initHeaderTvs();

        this.addView(contentLayout,contentLayoutParams);
    }

    public void setMyCalendar(MyCalendar myCalendar){
        this.myCalendar = myCalendar;
        this.updateHeaderView();
    }

    public void updateHeaderView(){
        initHeaderShowAttrs();

        this.setBackgroundColor(titleBgColor);

        mentionTv.setText(mention.toUpperCase());
        mentionTv.setTextColor(titleColor);
        mentionTv.setTextSize(titleSize);
        nthTv.setText(nth);
        nthTv.setTextColor(titleColor);
        nthTv.setTextSize(titleSize);

        monthTv.setText(month);
        monthTv.setTextColor(titleColor);
        monthTv.setTextSize(titleSize);

        dayOfWeekTv.setText(dayOfWeek);
        dayOfWeekTv.setTextColor(titleColor);
        dayOfWeekTv.setTextSize(titleSize);

        this.invalidate();
    }

    private void initHeaderTvs(){
        textPadding = DensityUtil.dip2px(context, 5);

        mentionTv = new TextView(context);
        mentionTv.setTextSize(14);
        mentionTv.setTypeface(null, Typeface.BOLD);
        mentionTv.setAllCaps(true);
        mentionTv.setPadding(0,0,textPadding,0);
        contentLayout.addView(mentionTv);

        dayOfWeekTv = new TextView(context);
        dayOfWeekTv.setAllCaps(true);
        dayOfWeekTv.setTextSize(14);
        dayOfWeekTv.setPadding(0,0,textPadding,0);
        contentLayout.addView(dayOfWeekTv);

        nthTv = new TextView(context);
        nthTv.setPadding(0,0,textPadding,0);
        nthTv.setTextSize(14);
        contentLayout.addView(nthTv);

        monthTv = new TextView(context);
        monthTv.setTextSize(14);
        monthTv.setAllCaps(true);
        monthTv.setPadding(0,0,textPadding,0);
        contentLayout.addView(monthTv);
    }

    private void initHeaderShowAttrs(){
        Calendar calendar = this.myCalendar.getCalendar();
        int day_of_month = calendar.get(Calendar.DAY_OF_MONTH);
        nth =  day_of_month + "";
        month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) + ",";

        mentionTv.setPadding(0,0,textPadding,0);
        titleColor = getResources().getColor(color_title);
        titleBgColor = getResources().getColor(color_title_bg);

        Calendar todayCal = Calendar.getInstance();

        long current_day_milliseconds = this.myCalendar.getBeginOfDayMilliseconds();
        long today_milliseconds = (new MyCalendar(todayCal)).getBeginOfDayMilliseconds();
        this.currentDayType = getDatesRelationType(today_milliseconds, current_day_milliseconds);

        switch (currentDayType){
            case 1:
                mention = "Tomorrow";
                break;
            case 0:
                mention = "Today";
                titleColor = getResources().getColor(color_title_today);
                titleBgColor = getResources().getColor(color_title_bg_today);
                break;
            case -1:
                mention = "Yesterday";
                break;
            default:
                mention = "";
                mentionTv.setPadding(0,0,0,0);
                break;
        }
    }

    private int getDatesRelationType(long todayM, long currentDayM){
        // -2 no relation, 1 tomorrow, 0 today, -1 yesterday
        int type = -2;
        int dayM = 24 * 60 * 60 * 1000;
        long diff = (currentDayM - todayM);
        if (diff >0 && diff <= dayM){
            type = 1;
        }else if(diff < 0 && diff >= -dayM){
            type = -1;
        }else if (diff == 0){
            type = 0;
        }

        return type;
    }
}
