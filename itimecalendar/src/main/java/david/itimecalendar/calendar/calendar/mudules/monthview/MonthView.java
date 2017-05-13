package david.itimecalendar.calendar.calendar.mudules.monthview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.github.sundeepk.compactcalendarview.DensityUtil;

import david.horizontalscrollpageview.RecyclerScrollView;
import david.itimecalendar.R;
import david.itimecalendar.calendar.listeners.ITimeEventPackageInterface;

/**
 * Created by yuhaoliu on 10/05/2017.
 */

public class MonthView extends LinearLayout{
    private Context context;

    private ITimeEventPackageInterface eventPackage;

    private RecyclerView headerRecyclerView;
    private DayViewHeaderRecyclerAdapter headerRecyclerAdapter;


    private ScrollView dayViewBodyContainer;
    private DayViewBody dayViewBody;


    private LinearLayoutManager headerLinearLayoutManager;
    private LinearLayout item;
    private int upperBoundsOffset;
    private int startPosition;

    private final DisplayMetrics dm = getResources().getDisplayMetrics();
    private int headerCollapsedHeight;
    private int headerExpandedHeight;

    public MonthView(Context context) {
        super(context);
        initView();
    }

    public MonthView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView(){
        this.context = getContext();
        this.setOrientation(VERTICAL);
        this.upperBoundsOffset = 100000;

        this.setUpHeader();
        this.setUpBody();
    }

    private void setUpHeader(){
        headerRecyclerView = new RecyclerView(context);
        headerRecyclerAdapter = new DayViewHeaderRecyclerAdapter(context, upperBoundsOffset);

        headerRecyclerAdapter.setOnCheckIfHasEvent(new DayViewHeader.OnCheckIfHasEvent() {
            @Override
            public boolean todayHasEvent(long startOfDay) {
//                boolean hasRegular = eventPackage.getRegularEventDayMap().containsKey(startOfDay) && (eventPackage.getRegularEventDayMap().get(startOfDay).size() != 0);
//                boolean hasRepeated = eventPackage.getRepeatedEventDayMap().containsKey(startOfDay) && (eventPackage.getRepeatedEventDayMap().get(startOfDay).size() != 0);
//                return hasRegular || hasRepeated;
                return false;
            }
        });
        headerRecyclerView.setHasFixedSize(true);
        headerRecyclerView.setAdapter(headerRecyclerAdapter);
        headerLinearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        headerRecyclerView.setLayoutManager(headerLinearLayoutManager);
        headerRecyclerView.addItemDecoration(new DayViewHeaderRecyclerDivider(context));
        headerCollapsedHeight = (dm.widthPixels / 7 - 20) * 2;
        headerExpandedHeight = (dm.widthPixels / 7 - 20) * 4;

        ViewGroup.LayoutParams headerParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, headerCollapsedHeight);
        headerRecyclerView.setLayoutParams(headerParams);
        headerRecyclerView.scrollToPosition(upperBoundsOffset/2);

        this.addView(headerRecyclerView);
    }

    private void setUpBody(){
        dayViewBodyContainer = new ScrollView(getContext());
        this.addView(dayViewBodyContainer, new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        dayViewBody = new DayViewBody(context);
        ScrollView.LayoutParams bodyParams = new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.dayViewBodyContainer.addView(dayViewBody, bodyParams);
    }


    public void setEventPackage(ITimeEventPackageInterface eventPackage){
        this.dayViewBody.setEventPackage(eventPackage);
    }
}
