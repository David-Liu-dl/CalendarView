package david.itimecalendar.calendar.ui.unitviews;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daasuu.bl.ArrowDirection;
import com.daasuu.bl.BubbleLayout;

import david.itimecalendar.R;
import david.itimecalendar.calendar.util.DensityUtil;

/**
 * Created by David Liu on 7/06/2017.
 * ${COMPANY}
 * lyhmelbourne@gmail.com
 */

public class TimeslotToolBar extends BubbleLayout {
    private TextView editBtn;
    private ImageView slash;
    private TextView deleteBtn;

    public TimeslotToolBar(Context context) {
        super(context);
        initViews();
    }

    private void initViews(){
        this.setArrowDirection(ArrowDirection.BOTTOM);
        this.setCornersRadius(DensityUtil.dip2px(getContext(),10));
        this.setBubbleColor(getResources().getColor(R.color.timeslot_bubble_bg));
        this.setArrowHeight(20);
        this.setArrowWidth(20);
        this.setStrokeWidth(0);
        this.setVisibility(GONE);

        LinearLayout bubbleMenuContainer = new LinearLayout(getContext());
        FrameLayout.LayoutParams bubbleParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(bubbleMenuContainer,bubbleParams);

        editBtn = new TextView(getContext());
        editBtn.setText("Edit");
        editBtn.setTextColor(Color.WHITE);
        editBtn.setTextSize(12);
        editBtn.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams editBtnParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        editBtnParams.weight = 10;
        editBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onButtonClickListener != null){
                    onButtonClickListener.onEditClick();
                }
            }
        });
        bubbleMenuContainer.addView(editBtn,editBtnParams);

        slash = new ImageView(getContext());
        slash.setImageDrawable(getResources().getDrawable(R.drawable.icon_slash));
        slash.setPadding(0,20,0,20);
        LinearLayout.LayoutParams slashParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bubbleMenuContainer.addView(slash,slashParams);

        deleteBtn = new TextView(getContext());
        deleteBtn.setText("Delete");
        deleteBtn.setTextSize(12);
        deleteBtn.setTextColor(Color.WHITE);
        deleteBtn.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams dltBtnParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        dltBtnParams.weight = 10;
        deleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
              if (onButtonClickListener != null){
                  onButtonClickListener.onDeleteClick();
              }
            }
        });
        bubbleMenuContainer.addView(deleteBtn,dltBtnParams);
    }

    public interface OnButtonClickListener {
        void onEditClick();
        void onDeleteClick();
    }

    OnButtonClickListener onButtonClickListener;

    public OnButtonClickListener getOnButtonClickListener() {
        return onButtonClickListener;
    }

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
    }

    public void setBubbleEditable(boolean editable){
        if (editable){
            slash.setVisibility(VISIBLE);
            editBtn.setVisibility(VISIBLE);
        }else {
            slash.setVisibility(GONE);
            editBtn.setVisibility(GONE);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        this.setArrowPosition(width/2 - this.getArrowWidth()/2);
    }
}
