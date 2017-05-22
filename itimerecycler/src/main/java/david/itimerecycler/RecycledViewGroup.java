package david.itimerecycler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by Paul on 9/5/17.
 */

public class RecycledViewGroup extends ViewGroup{
    private String TAG = "RecycledViewGroup";
    private int mTouchSlop;
    private List<AwesomeViewGroup> awesomeViewGroups;
    int[] colors =new int[]{Color.RED, Color.BLUE, Color.GRAY, Color.YELLOW, Color.GREEN};
    private int width,height, childWidth, childHeight;
    private float preX, preY;


    public final static int NON_SCROLL = 0;
    public final static int SCROLL_LEFT = 1;
    public final static int SCROLL_RIGHT = -1;

    public final static int SCROLL_UP = 2;
    public final static int SCROLL_DOWN = -2;

    public final static int SCROLL_VERTICAL = 1001;
    public final static int SCROLL_HORIZONTAL = 1002;

    private int curScrollDir = 0; // {SCROLL_LEFT, SCROLL_RIGHT, SCROLL_UP, SCROLL_DOWN}

    private boolean hasDecideScrollWay = false; // if scroll too small, cannot decide whether is vertical or horizontal
    private int curScrollWay = 0; // {SCROLL_VERTICAL, SCROLL_HORIZONTAL} only one

    public CalendarInterface calendarInterface;

    public int scrollThreshold;

    // for fling
    private VelocityTracker mVelocityTracker;
    private int mMaxVelocity;
    private float mVelocityX, mVelocityY;
    private Scroller mScroller;
    private float mAccelerator, mScrollTime;


    // for fling thread
    private boolean canFling = false;
    private int mSlots = 0;

    //for record offset
    private float offsetX = 0;
    private float offsetY = 0;
    //index of first view shown in screen, (near left side of screen)
    private int horizontalIndex = 0;

    //for calendar height calculation
    private float CELL_HEIGHT = 0;
    private int NUM_LAYOUTS = 3;


    public RecycledViewGroup(Context context, float CELL_HEIGHT, int NUM_LAYOUTS) {
        super(context);
        this.CELL_HEIGHT = CELL_HEIGHT;
        this.NUM_LAYOUTS = NUM_LAYOUTS;
        init();
    }

    public RecycledViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadAttributes(attrs, context);
        init();
    }

    private void loadAttributes(AttributeSet attrs, Context context){
        if (attrs != null && context != null){
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RecycledViewGroup,0,0);
            try {
                CELL_HEIGHT = typedArray.getDimension(R.styleable.RecycledViewGroup_cell_height, CELL_HEIGHT);
                NUM_LAYOUTS = typedArray.getInteger(R.styleable.RecycledViewGroup_column, NUM_LAYOUTS);
            }finally {
                typedArray.recycle();
            }
        }
    }

    private ITimeAdapter adapter;

    public void setAdapter(ITimeAdapter adapter){
        this.adapter = adapter;

        for (int i = 0; i < awesomeViewGroups.size(); i++) {
            AwesomeViewGroup parent = awesomeViewGroups.get(i);
            View item = adapter.onCreateViewHolder();
            parent.setItem(item);
            adapter.addViewOffset(item, horizontalIndex + i - 1);
        }
    }

    public float getFirstVisibleLeftOffset(){
        for (AwesomeViewGroup awesomeViewGroup: awesomeViewGroups){
            if (awesomeViewGroup.isVisibleInParent()){
                AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroup.getLayoutParams();
                return lp.left;
            }
        }

        return 0.0f;
    }

    private void init(){
        ViewConfiguration vc = ViewConfiguration.get(getContext());
        mTouchSlop = vc.getScaledTouchSlop();
        awesomeViewGroups = new ArrayList<>();
        int numTotalLayouts = NUM_LAYOUTS + 2;
        for (int i = 0 ; i < numTotalLayouts ; i ++){
            AwesomeViewGroup awesomeViewGroup = new AwesomeViewGroup(getContext());
            awesomeViewGroup.setLayoutParams(new AwesomeViewGroup.AwesomeLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//            awesomeViewGroup.setBackgroundColor(colors[i]);
            awesomeViewGroup.setId(i);
            addView(awesomeViewGroup);
            awesomeViewGroups.add(awesomeViewGroup);
        }

        mMaxVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
        mScroller = new Scroller(getContext());
    }

    private void moveChildX(float x){
        if (x == 0){
            // means page selected;
            if (onScroll != null){
                View firstItem = getFirstShowItem();
                onScroll.onPageSelected(firstItem);
            }
            return;
        }
        //normal scroll (x within one child width)
        if (Math.abs(x) <= childWidth){
            if (onScroll != null){
                onScroll.onHorizontalScroll(x, offsetX);
            }
            offsetX += x;

            int childCount = getChildCount();
            for(int i = 0 ; i < childCount; i ++ ){
                AwesomeViewGroup awesomeViewGroup = awesomeViewGroups.get(i);
                AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroup.getLayoutParams();

                lp.left = lp.left - x;
                lp.right = lp.right - x;
                awesomeViewGroup.reLayoutByLp();
            }

            boolean changed = postCheckAfterMoveX();

            //after move child position, trigger Notify Page Selected by moveChildX(0)
            if (changed){
                moveChildX(0);
            }

            return;
        }

        //deals multi children skipping (x > one page width)
        float pageSize = childWidth * (NUM_LAYOUTS+2);
        if (Math.abs(x) > pageSize){
            int pageOffset = (int) Math.floor(Math.abs(x)/pageSize);
            int symbol = x > 0 ? 1 : -1;
            pageOffset *= symbol;

            int skipCellOffset = pageOffset * (NUM_LAYOUTS+2);
            float skipX = skipCellOffset * childWidth;
            float remainsX = (Math.abs(x)%pageSize) * symbol;

            horizontalIndex += skipCellOffset;
            offsetX += skipX;
            if (adapter != null){
                adapter.updateBaseOffsetForMap(horizontalIndex);
            }

            moveChildX(remainsX);
            return;
        }

        //deals multi children skipping (one child width < x < one page width)
        if (Math.abs(x) > childWidth){
            int childRange = (int) Math.floor(Math.abs(x)/childWidth);
            int symbol = x > 0 ? 1 : -1;
            //move remains child
            for (int i = 0; i < childRange; i++) {
                moveChildX(childWidth*symbol);
            }
            float remains = (Math.abs(x)%childWidth) * symbol;
            moveChildX(remains);
            return;
        }
    }

    private void moveChildY(float y){
        int childCount = getChildCount();
        float realY = 0;
        for (int i = 0 ; i < childCount ; i ++ ){
            AwesomeViewGroup awesomeViewGroup = awesomeViewGroups.get(i);
            AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroup.getLayoutParams();

            float top = lp.top - y;
            float bottom = lp.bottom - y;

            realY = preCheckBeforeMoveY(top, bottom, lp); // if scroll too much...
            awesomeViewGroup.reLayoutByLp();
        }

        if (this.onScroll != null && realY != 0){
            this.onScroll.onVerticalScroll(realY, offsetY);
        }

        offsetY += realY;
    }


    private boolean postCheckAfterMoveX(){
        int viewGroupSize = awesomeViewGroups.size();
        boolean pageChanged = false;
        if (curScrollDir == SCROLL_LEFT){
            // scroll left only check the first one
            AwesomeViewGroup leftViewGroup = awesomeViewGroups.get(1);
            if (leftViewGroup.isLeftOutOfParent()){
                pageChanged = true;

                moveFirstViewToLast(awesomeViewGroups);
                reDrawViewGroupToLast(awesomeViewGroups.get(viewGroupSize - 2), awesomeViewGroups.get(viewGroupSize -1));
            }
        }else if(curScrollDir == SCROLL_RIGHT){
            // scroll right only check the last one
            AwesomeViewGroup rightViewGroup = awesomeViewGroups.get(viewGroupSize-2);
            if (rightViewGroup.isRightOutOfParent()){
                pageChanged = true;

                moveLastViewToFirst(awesomeViewGroups);
                reDrawViewGroupToFirst(awesomeViewGroups.get(1), awesomeViewGroups.get(0));
                updateNewFirstCalendar(awesomeViewGroups.get(0));
            }
        }

        return pageChanged;
    }

    private float preCheckBeforeMoveY(float top, float bottom, AwesomeViewGroup.AwesomeLayoutParams lp){
        float realY = 0;
        if (curScrollDir == SCROLL_DOWN){
            if (bottom < lp.parentHeight){
                // reach bottom, stop scrolling
                realY = lp.parentHeight - lp.bottom;
                lp.bottom = lp.parentHeight;
                lp.top = lp.bottom - lp.height;
                return realY;
            }
        }else if (curScrollDir == SCROLL_UP){
            if (top > 0 ){
                // reach top, stop scrolling
                realY = 0 - lp.top;
                lp.top = 0;
                lp.bottom = lp.top + lp.height;
                return realY;
            }
        }

        realY = bottom - lp.bottom;
        lp.bottom = bottom;
        lp.top = top;

        return realY;
    }

    private void moveFirstViewToLast(List<AwesomeViewGroup> viewGroupList){
        AwesomeViewGroup first = viewGroupList.get(0);
        viewGroupList.remove(0);
        viewGroupList.add(first);

        horizontalIndex += 1;
        if (adapter != null){
            adapter.removeViewOffset(horizontalIndex - 2);
            adapter.addViewOffset(first.getItem(), horizontalIndex + NUM_LAYOUTS);
        }
    }

    private void moveLastViewToFirst(List<AwesomeViewGroup> viewGroupList){
        AwesomeViewGroup last = viewGroupList.get(viewGroupList.size() -1);
        viewGroupList.remove(last);
        viewGroupList.add(0, last);

        horizontalIndex -= 1;
        if (adapter != null){
            adapter.removeViewOffset(horizontalIndex + NUM_LAYOUTS + 1);
            adapter.addViewOffset(last.getItem(), horizontalIndex - 1);
        }
    }

    // relativeIndex means offset count from first shown item
    private void updateChildParams(AwesomeViewGroup outParentChild, int relativeIndex){
        AwesomeViewGroup.AwesomeLayoutParams params = (AwesomeViewGroup.AwesomeLayoutParams) outParentChild.getLayoutParams();
        float left = relativeIndex * childWidth;
        params.left = left;
        params.right = left + childWidth;
    }

    private void reDrawViewGroupToLast(AwesomeViewGroup preAwesomeViewGroup, AwesomeViewGroup awesomeViewGroup){
        AwesomeViewGroup.AwesomeLayoutParams preLp = (AwesomeViewGroup.AwesomeLayoutParams) preAwesomeViewGroup.getLayoutParams();
        float preLpRight = preLp.right;

        AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroup.getLayoutParams();
        lp.left = preLpRight;
        lp.right = lp.left + lp.width;

        awesomeViewGroup.reLayoutByLp();
    }

    private void reDrawViewGroupToFirst(AwesomeViewGroup postAwesomeViewGroup, AwesomeViewGroup awesomeViewGroup){
        AwesomeViewGroup.AwesomeLayoutParams postLp = (AwesomeViewGroup.AwesomeLayoutParams) postAwesomeViewGroup.getLayoutParams();
        float postLeft = postLp.left;

        AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroup.getLayoutParams();
        lp.right = postLeft;
        lp.left = postLeft - lp.width;

        awesomeViewGroup.reLayoutByLp();
    }

    private void updateNewFirstCalendar(AwesomeViewGroup awesomeViewGroup){
        updateCalendarForAwesomeViewGroup(awesomeViewGroup, -5);
    }

    private void updateNewLastCalendar(AwesomeViewGroup awesomeViewGroup){
        updateCalendarForAwesomeViewGroup(awesomeViewGroup, 5);
    }

    private void updateCalendarForAwesomeViewGroup(AwesomeViewGroup awesomeViewGroup, int delta){
        Calendar c = awesomeViewGroup.getCalendar();
        if (c==null){
            return;
        }

        c.add(Calendar.DATE, delta);
    }


    private void recordHorizontalScrollDir(float x){
         curScrollDir = x > 0 ? SCROLL_LEFT: SCROLL_RIGHT;
    }

    private void recordVerticalScrollDir(float y){
        curScrollDir = y > 0 ? SCROLL_DOWN : SCROLL_UP;
    }

    private void resetScrollWay(){
        curScrollWay = 0;
        hasDecideScrollWay = false;
    }

    private void noFlingEndCheck(){
        float needScrollDistance = childNeedsScrollToNearestPosition(awesomeViewGroups);
        if (needScrollDistance != 0.0){
            smoothMoveChildX(needScrollDistance, null);
        }
    }


    /**
     * move each children with animations (fake move, not real move), then real move each children
     * @param x
     */
    private void
    smoothMoveChildX(float x, Animation.AnimationListener animationListener){
        curScrollDir = x < 0 ? SCROLL_RIGHT : SCROLL_LEFT; // animation scroll direction

        for (AwesomeViewGroup awesomeViewGroup: awesomeViewGroups){
            applyAnimation(x, awesomeViewGroup, animationListener);
        }

        moveChildX(x);
    }

    private void directMoveChildX(float x){
        curScrollDir = x < 0 ? SCROLL_RIGHT : SCROLL_LEFT; // animation scroll direction
        moveChildX(x);
    }

    private void smoothMoveChildY(float y){

    }

    private void applyAnimation(float x, AwesomeViewGroup awesomeViewGroup, Animation.AnimationListener animationListener){
        Animation ani = new TranslateAnimation(
                x,  0, 0.0f, 0.0f);
        ani.setDuration(500);
        ani.setInterpolator(new DecelerateInterpolator());
        ani.setFillAfter(false);
        ani.setAnimationListener(animationListener);
        awesomeViewGroup.startAnimation(ani);
    }

    private float childNeedsScrollToNearestPosition(List<AwesomeViewGroup> awesomeViewGroups){
        for (AwesomeViewGroup awesomeViewGroup : awesomeViewGroups){
            if (awesomeViewGroup.isVisibleInParent() && awesomeViewGroup.isOutOfParent()){
                AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroup.getLayoutParams();
                return Math.abs(lp.right) > Math.abs(lp.left) ? lp.left : lp.right;
            }
        }
        return 0;
    }

    /**
     * handler receive message from awesome thread, and continuously drawing new position
     */
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what ==0 ) {
                float moveDis = (float) msg.obj;
                moveChildX(moveDis);
            }
        }
    };

    /**
     * fling thread is for when doing fling, it continuously sending new moving
     * distance to handler.
     */
    private AwesomeThread flingThread = new AwesomeThread();


    private void checkFling(float velocityX, float velocityY, int scrollDir){
        int[] scrollPos = ScrollHelper.calculateScrollDistance(mScroller, (int)velocityX, (int)velocityY);
        switch (scrollDir){
            case SCROLL_LEFT:
            case SCROLL_RIGHT:
                if (shouldFling(velocityX)){

                    mScrollTime = ScrollHelper.calculateScrollTime(velocityX);
                    float distance = scrollPos[0];
                    float offset = scrollDir == SCROLL_LEFT ? getFirstVisibleLeftOffset() : -getFirstVisibleLeftOffset();
                    distance = ScrollHelper.findRightPosition(distance, offset,childWidth);

                    mAccelerator = ScrollHelper.calculateAccelerator(distance, mScrollTime);
                    mSlots = (int) (Math.abs(mScrollTime) * 16);

                    canFling = true;
                    if (flingThread.getState()!= Thread.State.NEW){
                        flingThread = new AwesomeThread();
                    }
                    flingThread.start();

                }else{
                    // not fling, only do post check
                    noFlingEndCheck();
                }
                break;
            case SCROLL_UP:
            case SCROLL_DOWN:
                if (shouldFling(velocityY)){
                    smoothMoveChildY(scrollPos[1]);
                }
                break;
        }
    }


    // 200 is a threshold , if more than 200, then fling, otherwise not
    private boolean shouldFling(float velocity){
        return Math.abs(velocity) > 200;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // action down need to pass to child
                Log.i(TAG, "onInterceptTouchEvent: " + "down");
                if (mVelocityTracker==null){
                    mVelocityTracker = VelocityTracker.obtain();
                }else{
                    mVelocityTracker.clear();
                }
                mVelocityX = 0;
                mVelocityY = 0;
                preX = ev.getX();
                preY = ev.getY();
                if (canFling){
                    // view group is flinging, then can only do horizontal scroll
                    hasDecideScrollWay = true;
                    curScrollWay = SCROLL_HORIZONTAL;
                }
                canFling = false; // canFling -> false, stop flinging
                break;
            case MotionEvent.ACTION_MOVE:
                //action move here has two options.
                // option1: if the scroll distance is larger than scroll threshold, then this is a recycledView scroll,
                //          so that this action move need to be intercepted, this recycledViewGroup will consume the moving
                // option2: if the scroll distance is less than scroll threshold, then this is not a recycledView scroll, this
                //          action must be passed to its children.
                // the action move code here is similar to code in onTouchEvent, because when intercepted move, next time,
                // move action only be called in onTouchEvent
                Log.i(TAG, "onTouchEvent: " + "move");
                float newY = ev.getY();
                float newX = ev.getX();

                if (!hasDecideScrollWay) {
                    if (mTouchSlop < Math.abs(newY - preY)) {
                        // vertical scroll
                        hasDecideScrollWay = true;
                        curScrollWay = SCROLL_VERTICAL;
                    } else if (mTouchSlop < Math.abs(newX - preX)) {
                        // horizontal scroll
                        hasDecideScrollWay = true;
                        curScrollWay = SCROLL_HORIZONTAL;
                    }
                }

                if (hasDecideScrollWay){

                    if (curScrollWay == SCROLL_HORIZONTAL) {
                        float moveX = preX - newX;
                        recordHorizontalScrollDir(moveX);
                        moveChildX(moveX);

                        preX = newX;
                    }else if (curScrollWay == SCROLL_VERTICAL){
                        float moveY = preY - newY;
                        recordVerticalScrollDir(moveY);
                        moveChildY(moveY);
                        preY = newY;
                    }
                    mVelocityTracker.addMovement(ev);
                    mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                    mVelocityX = mVelocityTracker.getXVelocity();
                    mVelocityY = mVelocityTracker.getYVelocity();

                    Log.i(TAG, "onInterceptTouchEvent: " + "move");
                    return true; // here must be true, so the recycledViewGroup can consume the action from now on
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "onInterceptTouchEvent: " + "up");
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.i(TAG, "onInterceptTouchEvent: " + "cancel");
                break;
        }

        boolean value = super.onInterceptTouchEvent(ev);
        Log.i(TAG, "onInterceptTouchEvent: " + value);
        return value;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch (action){
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "onTouchEvent: " + "move");
                float newY = event.getY();
                float newX = event.getX();

                if (!hasDecideScrollWay) {
                    if (mTouchSlop < Math.abs(newY - preY)) {
                        // vertical scroll
                        hasDecideScrollWay = true;
                        curScrollWay = SCROLL_VERTICAL;
                    } else if (mTouchSlop < Math.abs(newX - preX)) {
                        // horizontal scroll
                        hasDecideScrollWay = true;
                        curScrollWay = SCROLL_HORIZONTAL;
                    }
                }

                if (hasDecideScrollWay){

                    if (curScrollWay == SCROLL_HORIZONTAL) {
                        float moveX = preX - newX;
                        recordHorizontalScrollDir(moveX);
                        moveChildX(moveX);
                        preX = newX;
                    }else if (curScrollWay == SCROLL_VERTICAL){
                        float moveY = preY - newY;
                        recordVerticalScrollDir(moveY);
                        moveChildY(moveY);
                        preY = newY;
                    }
                    mVelocityTracker.addMovement(event);
                    mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                    mVelocityX = mVelocityTracker.getXVelocity();
                    mVelocityY = mVelocityTracker.getYVelocity();
                }

                Log.i(TAG, "onTouchEvent: " + "true");
                // if recycled view is scrolling, then the move action has to be consumed, need true here
                return true;

            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "onTouchEvent: " + "down");
                Log.i(TAG, "onTouchEvent: " + "true");
                // here must return true. So if no children use the 'down', recycledViewGroup can still use it.
                // Otherwise, you will find the recycledViewGroup cannot be scrolled.
                return false;
            case MotionEvent.ACTION_CANCEL:
                Log.i(TAG, "onTouchEvent: " + "cancel");
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "onTouchEvent: " + "up");
                resetScrollWay();
                checkFling(mVelocityX, mVelocityY, curScrollDir);
                Log.i(TAG, "onTouchEvent: " + "true");
                // this action up has to be true, because if you consumed the move actions in here,
                // the last action up should also be consumed here.
                return true;
        }

        boolean value = super.onTouchEvent(event);
        Log.i(TAG, "onTouchEvent:onTouch" + value);
        return value;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);

        childWidth = width/NUM_LAYOUTS;
        childHeight = (int) calculateItemHeight(CELL_HEIGHT);
        scrollThreshold = childHeight/2;

        int childCount = getChildCount();
        int childWidthSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
        int childHeightSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);

        for (int i = 0 ; i < childCount ; i++){
            AwesomeViewGroup awesomeViewGroup = awesomeViewGroups.get(i);
//            measureChild(awesomeViewGroup, widthMeasureSpec, heightMeasureSpec);
            measureChild(awesomeViewGroup, childWidthSpec, childHeightSpec);
            AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroup.getLayoutParams();

            lp.parentHeight = height;
            lp.width = childWidth;
            lp.height = childHeight;

            if (curScrollDir == SCROLL_LEFT){
                lp.left = getFirstVisibleLeftOffset() + i * childWidth;
            }else if (curScrollDir == SCROLL_RIGHT){
                lp.left = getFirstVisibleLeftOffset() + (i-1) * childWidth;
            }else if (curScrollDir == NON_SCROLL){
                lp.left = getFirstVisibleLeftOffset() + (i-1) * childWidth;
            }

            lp.right = lp.left + childWidth;
            lp.bottom = lp.top + lp.height;
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // start to layout children
        int childCount = getChildCount();
        for (int i = 0 ; i < childCount; i ++){
            AwesomeViewGroup child = awesomeViewGroups.get(i);
            child.reLayoutByLp();
        }
    }

    private float calculateItemHeight(float baseParams){
        return baseParams * 25;
    }


    public interface CalendarInterface{
        Calendar getFirstDay();
    }

    private class AwesomeThread extends Thread{

        @Override
        public void run() {
            int index = 0;
            while(canFling) {
                Message msg = new Message();
                msg.what = 0;
                float curTime = mScrollTime * (mSlots - index - 1) / mSlots;
                float nextTime = index+1 == mSlots? curTime : mScrollTime * (mSlots - index) / mSlots;
                float curDistance = ScrollHelper.getCurrentDistance(mAccelerator, curTime);
                float nextDistance = ScrollHelper.getCurrentDistance(mAccelerator, nextTime);
                float shouldMoveDis = curDistance - nextDistance;

                msg.obj = shouldMoveDis;
                mHandler.sendMessage(msg);
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                index ++;
                if (index >= mSlots){
                    canFling = false;
                }

            }
        }
    }

    private OnScroll onScroll;

    public void setOnScrollListener(OnScroll onScroll){
        this.onScroll = onScroll;
    }

    public interface OnScroll{
        void onPageSelected(View v);
        void onHorizontalScroll(float dx, float preOffsetX);
        void onVerticalScroll(float dy, float preOffsetY);
    }

    public void smoothMoveWithOffset(int moveOffset, @Nullable Animation.AnimationListener animationListener){
        if (moveOffset == 0){
            return;
        }
        float distance = moveOffset * childWidth;
        smoothMoveChildX(distance, animationListener);
    }

    public void moveWithOffset(int moveOffset){
        if (moveOffset == 0){
            return;
        }
        float distance = moveOffset * childWidth;
        directMoveChildX(distance);
    }

    public View getFirstShowItem(){
        return awesomeViewGroups.get(1).getItem();
    }

}
