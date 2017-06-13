package david.itimecalendar.calendar.ui.unitviews;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.List;

import david.itimecalendar.R;
import david.itimecalendar.calendar.util.DensityUtil;

import static android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM;
import static android.widget.RelativeLayout.ALIGN_PARENT_LEFT;
import static android.widget.RelativeLayout.ALIGN_PARENT_RIGHT;
import static android.widget.RelativeLayout.CENTER_VERTICAL;

/**
 * Created by yuhaoliu on 31/05/2017.
 */

public class TimeslotDurationWidget<T> extends FrameLayout{
    View bgView;
    LinearLayout container;

    RelativeLayout optBlock;
    TextView label;
    TextView nowValue;
    TextView doneBtn;
    View highlighter;

    //sp
    float textSizeSp = 15;
    float labelTextSizeSp = 13;
    int textNormalColor = Color.BLACK;
    int textHighlightColor = Color.BLUE;
    int fakeBgColor = R.color.calendar_timeslot_duration_bg;

    private WheelPicker wheelPicker;
    private int CurrentSelectedPosition;

    //dp
    private int btnBlockWidth = 50;

    public TimeslotDurationWidget(@NonNull Context context) {
        super(context);
        initViews();
        initViewState();
        initListeners();
    }

    public TimeslotDurationWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews();
        initViewState();
        initListeners();
    }

    public TimeslotDurationWidget(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
        initViewState();
        initListeners();
    }

    public void setOptHeight(int height){
        this.optBlock.getLayoutParams().height = height;
        this.requestLayout();
    }

    private void initViews(){
        btnBlockWidth = DensityUtil.dip2px(getContext(), btnBlockWidth);

        bgView = new View(getContext());
        bgView.setBackgroundColor(getResources().getColor(R.color.calendar_alpha_bg));
        FrameLayout.LayoutParams bgViewParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(bgView,bgViewParams);

        container = new LinearLayout(getContext());
        container.setOrientation(LinearLayout.VERTICAL);
        FrameLayout.LayoutParams containerParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        containerParams.gravity = Gravity.BOTTOM;
        this.addView(container,containerParams);

        Context context = getContext();
        optBlock = new RelativeLayout(context);

        optBlock.setBackgroundColor(getResources().getColor(fakeBgColor));
        LinearLayout.LayoutParams optParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(context,50));
        container.addView(optBlock,optParams);


        label = new TextView(context);
        label.setText("Duration");
//        label.setTextSize(DensityUtil.px2sp(context, labelTextSizeSp));
        label.setTextSize(labelTextSizeSp);
        RelativeLayout.LayoutParams labelParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        labelParams.leftMargin = DensityUtil.dip2px(context,15);
        labelParams.addRule(ALIGN_PARENT_LEFT);
        labelParams.addRule(CENTER_VERTICAL);
        optBlock.addView(label,labelParams);


        nowValue = new TextView(context);
        nowValue.setText("20");
        nowValue.setTextSize(textSizeSp);
        nowValue.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams nowValueParams = new RelativeLayout.LayoutParams(btnBlockWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        nowValueParams.rightMargin = DensityUtil.dip2px(context,15);
        nowValueParams.addRule(ALIGN_PARENT_RIGHT);
        nowValueParams.addRule(CENTER_VERTICAL);
        optBlock.addView(nowValue,nowValueParams);

        doneBtn = new TextView(context);

        doneBtn.setText("Done");
        doneBtn.setTextSize(textSizeSp);
        doneBtn.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(btnBlockWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        buttonParams.rightMargin = DensityUtil.dip2px(context,15);
        buttonParams.addRule(ALIGN_PARENT_RIGHT);
        buttonParams.addRule(CENTER_VERTICAL);
        optBlock.addView(doneBtn,buttonParams);

        highlighter = new View(context);
        highlighter.setBackgroundColor(getResources().getColor(R.color.calendar_timeslot_duration_highlighter));
        RelativeLayout.LayoutParams highlighterParams = new RelativeLayout.LayoutParams(btnBlockWidth, DensityUtil.dip2px(context,3));
        highlighterParams.rightMargin = DensityUtil.dip2px(context,15);
        highlighterParams.bottomMargin = DensityUtil.dip2px(context,7);
        highlighterParams.addRule(ALIGN_PARENT_RIGHT);
        highlighterParams.addRule(ALIGN_PARENT_BOTTOM);
        optBlock.addView(highlighter,highlighterParams);

        wheelPicker = new WheelPicker(context);
        wheelPicker.setBackgroundColor(getResources().getColor(fakeBgColor));
        wheelPicker.setSelectedItemTextColor(getResources().getColor(R.color.calendar_timeslot_duration_highlighter));
        wheelPicker.setItemTextSize(DensityUtil.sp2px(context,textSizeSp));
        wheelPicker.setAtmospheric(true);
        wheelPicker.setCurved(true);
        wheelPicker.setCyclic(true);
        wheelPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                wheelPicker.setSelectedItemPosition(position);
            }
        });
        LinearLayout.LayoutParams wheelPickerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        container.addView(wheelPicker,wheelPickerParams);
    }

    int wheelPickerHeight;

    private void initViewState(){
        bgView.setVisibility(GONE);
        doneBtn.setVisibility(GONE);
        nowValue.setVisibility(VISIBLE);
        wheelPicker.setVisibility(VISIBLE);

        wheelPicker.measure(0,0);
        wheelPickerHeight = wheelPicker.getMeasuredHeight();
        container.setTranslationY(wheelPickerHeight);
    }

    private long duration = 300;
    private int currentSelectedPosition = 0;
    private void initListeners(){
        bgView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    doneBtn.performClick();
                }
                return true;
            }
        });

        nowValue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                bgView.setVisibility(VISIBLE);
                wheelPicker.setSelectedItemPosition(currentSelectedPosition);
                performShowAnimation();
            }
        });

        doneBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemSelectedListener != null){
                    currentSelectedPosition = wheelPicker.getSelectedItemPosition();
                    updateValueText(currentSelectedPosition);
                }
                bgView.setVisibility(GONE);
                performHideAnimation();
            }
        });
    }

    public void setDate(List<T> data){
        wheelPicker.setData(data);

        currentSelectedPosition = 0;
        wheelPicker.setSelectedItemPosition(currentSelectedPosition);
        updateValueText(currentSelectedPosition);
    }

    public void setSelectedItemPosition(int index){
        wheelPicker.setSelectedItemPosition(index);
    }

    private void updateValueText(int indexInData){
        String value = String.format("%s", wheelPicker.getData().get(indexInData));
        nowValue.setText(value);
        onItemSelectedListener.onItemSelected(indexInData);
    }

    private void performShowAnimation(){

        YoYo.with(Techniques.SlideOutUp)
                .duration(duration)
                .onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                nowValue.setVisibility(GONE);
            }
        }).playOn(nowValue);

        YoYo.with(Techniques.SlideInUp)
                .duration(duration).onStart(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                doneBtn.setVisibility(VISIBLE);
            }
        }).playOn(doneBtn);

        container.animate().setDuration(duration).translationY(0).start();


        // prepare anim data
        float targetTextSizePx = label.getTextSize()*1.5f;
        float targetTextSizeSp = DensityUtil.px2sp(getContext(),targetTextSizePx);
        Rect bounds = new Rect();
        Paint textPaint = label.getPaint();
        textPaint.setTextSize(targetTextSizePx);
        textPaint.getTextBounds(label.getText().toString(),0,(label.getText().length()),bounds);
        int width = bounds.width();
        int toX =(getWidth() - width)/2 - label.getLeft();

        AnimationSet animSet = new AnimationSet(false);
        animSet.addAnimation(new TranslateAnimation(label.getTranslationX(), toX, 0, 0));
        animSet.setDuration(duration);
        animSet.setFillAfter(true);
        label.startAnimation(animSet);

        ValueAnimator textSizeAnimator = ValueAnimator.ofFloat(labelTextSizeSp,targetTextSizeSp);
        textSizeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float newValue = (float) animation.getAnimatedValue();
//                label.setTextSize(DensityUtil.px2sp(getContext(),newValue));
                label.setTextSize(newValue);
                label.invalidate();
            }
        });
        textSizeAnimator.start();
    }

    private void performHideAnimation(){

        YoYo.with(Techniques.SlideInDown)
                .duration(duration).onStart(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                nowValue.setVisibility(VISIBLE);
            }
        }).playOn(nowValue);

        YoYo.with(Techniques.SlideOutDown)
                .duration(duration).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                doneBtn.setVisibility(GONE);
            }
        }).playOn(doneBtn);

        container.animate().setDuration(duration).translationY(wheelPickerHeight).start();

        AnimationSet animSet = new AnimationSet(false);
        int fromX =(getWidth() - label.getWidth())/2 - label.getLeft();
        animSet.addAnimation(new TranslateAnimation(fromX, 0, 0, 0));
        animSet.setDuration(duration);
        animSet.setFillAfter(true);
        label.startAnimation(animSet);

        ValueAnimator textSizeAnimator = ValueAnimator.ofFloat(label.getTextSize(), DensityUtil.sp2px(getContext(),labelTextSizeSp));
        textSizeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float newValue = (float) animation.getAnimatedValue();
                label.setTextSize(DensityUtil.px2sp(getContext(),newValue));
                label.invalidate();
            }
        });
        textSizeAnimator.start();
    }


    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener){
        this.onItemSelectedListener = onItemSelectedListener;
    }

    private OnItemSelectedListener onItemSelectedListener;

    public interface OnItemSelectedListener<T>{
        void onItemSelected(int position);
    }

}
