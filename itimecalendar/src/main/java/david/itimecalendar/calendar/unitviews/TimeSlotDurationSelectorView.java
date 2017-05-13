package david.itimecalendar.calendar.unitviews;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by yuhaoliu on 4/05/2017.
 */

public class TimeSlotDurationSelectorView extends LinearLayout{
    private TextView label;


    public TimeSlotDurationSelectorView(Context context) {
        super(context);
    }

    public TimeSlotDurationSelectorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeSlotDurationSelectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(){
        initLabel();
    }

    private void initLabel(){
        label = new TextView(getContext());
        label.setText("Duration");
        LayoutParams labelParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.addView(label,labelParams);
    }

    private void initOptions(){

    }
}
