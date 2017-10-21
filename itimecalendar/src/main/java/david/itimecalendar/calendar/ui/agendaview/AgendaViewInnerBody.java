package david.itimecalendar.calendar.ui.agendaview;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Transformation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import david.itimecalendar.R;
import david.itimecalendar.calendar.listeners.ITimeEventInterface;
import david.itimecalendar.calendar.listeners.ITimeInviteeInterface;
import david.itimecalendar.calendar.util.BaseUtil;
import david.itimecalendar.calendar.util.DensityUtil;
import david.itimecalendar.calendar.util.LoadImgHelper;
import david.itimecalendar.calendar.util.RoundedImageView;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by yuhaoliu on 31/08/16.
 */
public class AgendaViewInnerBody extends RelativeLayout {
    private final String TAG = "MyAPP";

    public final static int TP_PRIVATE = 0;
    public final static int TP_GROUP_NEED_ACTION = 1;
    public final static int TP_GROUP_CONFIRM = 2;

    public final static int icon_wh = 18;
    public final static int NUMBER_PHOTO = 5;

    private static class AgendaColor{
        public static int START_TIME = R.color.title_text_enable;
        public static int END_TIME = R.color.title_text_disable;
        public static int NOW_START_TIME = R.color.brand_main;
        public static int NOW_END_TIME = R.color.now_end_time;
        public static int TITLE = R.color.title_text_enable;
        public static int NUMBER = R.color.title_text_disable;
        public static int LOCATION = R.color.title_text_disable;;
    }
    /*************************** Start of Color Setting **********************************/
//    private int color_enable = R.color.title_text_enable;
//    private int color_disable = R.color.title_text_disable;
//    private int color_title = R.color.title_text_color;
//    private int color_subtitle = R.color.sub_title_text_color;
//    private int color_now = R.color.brand_main;
//    private int color_normal = R.color.sub_title_text_color;
    /*************************** End of Color Setting **********************************/

    /*************************** Start of Resources Setting ****************************/
    private int rs_icon_private = R.drawable.icon_calendar_solo_event;
    private int rs_icon_group_confirmed = R.drawable.icon_calendar_group_confirmed;
    private int rs_icon_group_needAction = R.drawable.icon_calendar_group_unconfirmed;
    private int rs_icon_location = R.drawable.icon_calendar_grey_location;
    /*************************** End of Resources Setting ****************************/

    private RelativeLayout self = this;

    private LinearLayout leftInfo;
    private LinearLayout rightInfo;
    private LinearLayout inviteeLayout;
    private ImageView eventTypeView;
    private ImageView eventStatusView;

//    private TextView leftTimeTv;
    private TextView leftTime1;
    private TextView leftTime2;
    private TextView eventNameTv;
    private TextView locationTv;
//    private TextView timeLeftTv;

    private float textRegularSize = 15;
    private float textSmallSize = 12;

    private int pic_height_width;
    private int paddingUpDown;

    final DisplayMetrics dm = getResources().getDisplayMetrics();
    private float screenWidth = dm.widthPixels;

    private Context context;

    private ITimeEventInterface event;

    private String timeStr1;
    private String timeStr2;
    private String duration;
    private String eventName;
    private String location;
    private String timeLeft;
    private String iconName;

    private String status;
//    private String status;
    private int currentDayType;

    private List<String> urls = new ArrayList<>();

    private DateFormat date = new SimpleDateFormat("HH:mm a");

    private boolean isEnded = false;
    private boolean isHappening = false;
    public AgendaViewInnerBody(Context context, ITimeEventInterface event, int currentDayType) {
        super(context);
        this.context = context;
        this.currentDayType = currentDayType;
        this.event = event;
        this.pic_height_width = DensityUtil.dip2px(context, 30);
        this.paddingUpDown = DensityUtil.dip2px(context, 2);
        setWillNotDraw(false);
        initAttrs();
        initEventShowAttrs(event);
        initAllViews();
    }

    private void initAllViews() {
        //left info
        leftInfo = new LinearLayout(context);
        leftInfo.setOrientation(LinearLayout.VERTICAL);
        LayoutParams leftInfoParams = new LayoutParams(DensityUtil.dip2px(context,60), ViewGroup.LayoutParams.WRAP_CONTENT);
        leftInfoParams.addRule(ALIGN_PARENT_LEFT);
        leftInfo.setGravity(Gravity.CENTER_VERTICAL);
        leftInfo.setId(generateViewId());
        this.addView(leftInfo, leftInfoParams);

        leftTime1 = new TextView(context);
        leftTime1.setText(duration.equals(getContext().getString(R.string.label_allday)) ? getContext().getString(R.string.label_allday) : timeStr1);
        leftTime1.setGravity(Gravity.RIGHT);
        leftTime1.setTextSize(textSmallSize);
        leftTime1.setId(View.generateViewId());
        leftInfo.addView(leftTime1);

        leftTime2 = new TextView(context);
        leftTime2.setText(duration.equals(getContext().getString(R.string.label_allday)) ? "" : timeStr2);
        leftTime2.setGravity(Gravity.RIGHT);
        leftTime2.setTextSize(textSmallSize);
        LayoutParams params2 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params2.addRule(BELOW,leftTime1.getId());
        params2.topMargin = DensityUtil.dip2px(context,8);
        leftTime2.setLayoutParams(params2);
        leftInfo.addView(leftTime2);

        if (isHappening){
            leftTime1.setTextColor(getResources().getColor(AgendaColor.NOW_START_TIME));
            leftTime2.setTextColor(getResources().getColor(AgendaColor.NOW_END_TIME));
        }else {
            leftTime1.setTextColor(getResources().getColor(AgendaColor.START_TIME));
            leftTime2.setTextColor(getResources().getColor(AgendaColor.END_TIME));
        }

        //type bar && right info, relation bet 2
        eventTypeView = new ImageView(context);
        eventTypeView.setId(generateViewId());
        LayoutParams eventTypeViewParams = new LayoutParams(DensityUtil.dip2px(context,icon_wh), DensityUtil.dip2px(context,icon_wh));
        eventTypeView.setVisibility(event.getEventType().equals(ITimeEventInterface.EVENT_TYPE_GROUP) ? VISIBLE : INVISIBLE);
        eventTypeView.setImageDrawable(event.isConfirmed() ? context.getResources().getDrawable(rs_icon_group_confirmed) : context.getResources().getDrawable(rs_icon_group_needAction));
        eventTypeViewParams.leftMargin = DensityUtil.dip2px(context,11);
        eventTypeViewParams.topMargin = DensityUtil.dip2px(context,4);

        rightInfo = new LinearLayout(context);
        rightInfo.setOrientation(LinearLayout.VERTICAL);
        LayoutParams rightInfoParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rightInfoParams.addRule(RIGHT_OF, eventTypeView.getId());
        rightInfoParams.leftMargin = DensityUtil.dip2px(context,11);

        rightInfo.setId(generateViewId());
        this.addView(rightInfo, rightInfoParams);

        eventTypeViewParams.addRule(RIGHT_OF, leftInfo.getId());
        eventTypeViewParams.addRule(ALIGN_TOP, rightInfo.getId());
        self.addView(eventTypeView, eventTypeViewParams);

        eventNameTv = new TextView(context);
        eventNameTv.setText(eventName);
        eventNameTv.setPadding(0, paddingUpDown, 0, 0);
        eventNameTv.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        eventNameTv.setTextSize(textRegularSize);
//        eventNameTv.setSingleLine(true);
//        eventNameTv.setEllipsize(TextUtils.TruncateAt.END);
        eventNameTv.setTextColor(getResources().getColor(AgendaColor.TITLE));
        rightInfo.addView(eventNameTv);

        inviteeLayout = new LinearLayout(context);
        LinearLayout.LayoutParams inviteeLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        inviteeLayout.setOrientation(LinearLayout.HORIZONTAL);
        inviteeLayout.setGravity(Gravity.CENTER_VERTICAL);
        this.initInviteeLayout(this.urls, inviteeLayout);

        rightInfo.addView(inviteeLayout, inviteeLayoutParams);

        locationTv = new TextView(context);
        locationTv.setText(location);
        locationTv.setPadding(0, paddingUpDown, 0, 0);
        locationTv.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        locationTv.setTextSize(textSmallSize);
        locationTv.setTextColor(getResources().getColor(AgendaColor.LOCATION));
        Drawable orgDb = getResources().getDrawable(rs_icon_location);
        Drawable scaleDb = BaseUtil.scaleDrawable(orgDb,DensityUtil.dip2px(context,15),DensityUtil.dip2px(context,15));
        locationTv.setCompoundDrawables(scaleDb, null, null, null);
        locationTv.setSingleLine(true);
        locationTv.setEllipsize(TextUtils.TruncateAt.END);
        locationTv.setVisibility(View.VISIBLE);
        locationTv.setVisibility(event.getLocationName().equals("")? View.GONE: View.VISIBLE);
        rightInfo.addView(locationTv);
    }

    private void initAttrs(){
        isEnded = event.getEndTime() < System.currentTimeMillis();
        isHappening = System.currentTimeMillis() > event.getStartTime() && !isEnded;
    }

    private void initInviteeLayout(List<String> urls, LinearLayout container){
        if (urls.size() <= 1){
            return;
        }
        for (int i = 0; i < urls.size(); i++) {
            if (urls.size() <= NUMBER_PHOTO || (urls.size() > NUMBER_PHOTO && i < (NUMBER_PHOTO - 1))){
                container.addView(addImage(urls.get(i)));
            }else{
                container.addView(addNumberPhoto(urls.size() - (NUMBER_PHOTO - 1)));
                break;
            }
        }
    }

    private ImageView addImage(String url) {
        ImageView img = new ImageView(context);
        int margin = DensityUtil.dip2px(getContext(),4);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(pic_height_width, pic_height_width);
        params.leftMargin = margin;
        params.rightMargin = margin;

        img.setLayoutParams(params);
        int size = DensityUtil.dip2px(getContext(),30);
        Transformation transformation = new CropCircleTransformation();
        LoadImgHelper.getInstance().bindUrlWithImageView(context,transformation, url, img, size);

        return img;
    }

    private ImageView addNumberPhoto(int number){
        RoundedImageView img = new RoundedImageView(context);
        img.setBorderColor(getResources().getColor(AgendaColor.NUMBER));
        img.setBgColor(getResources().getColor(R.color.image_number_white));
        img.setNumber(number);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(pic_height_width, pic_height_width);
        int margin = DensityUtil.dip2px(getContext(),4);
        params.leftMargin = margin;
        params.rightMargin = margin;
        img.setLayoutParams(params);
        return img;
    }

    private void initEventShowAttrs(ITimeEventInterface event) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(event.getStartTime());
        Date startDateTime = calendar.getTime();
        calendar.setTimeInMillis(event.getEndTime());
        Date endDateTime = calendar.getTime();
        String startTimeStr = date.format(startDateTime).toLowerCase();
        String endTimeStr = date.format(endDateTime).toLowerCase();
        timeStr1 = startTimeStr;
        timeStr2 = endTimeStr;
        long duration_m = event.getEndTime() - event.getStartTime();
        int hours = (int) duration_m / (3600 * 1000);
        int minutes = (int) ((duration_m / (60 * 1000)) % 60);
        duration =
                event.isAllDay() ? getResources().getString(R.string.label_allday) :
                        hours == 0 ? minutes + getResources().getString(R.string.label_hours) :
                                minutes == 0 ? hours + getResources().getString(R.string.label_min) + " " :
                                        hours + getResources().getString(R.string.label_hours) + " " + minutes + getResources().getString(R.string.label_min);
        eventName = event.getSummary();
        location = event.getLocationName();

        List<? extends ITimeInviteeInterface> inviteeList = event.getDisplayInvitee();

        if (inviteeList == null){
            return;
        }

        List<String> allUrls = new ArrayList<>();

        for (ITimeInviteeInterface invitee : inviteeList
                ) {
            allUrls.add(invitee.getPhoto());
        }

        this.urls.addAll(allUrls);
    }
}
