package com.developer.paul.recycleviewgroup;

/**
 * Created by Paul on 22/5/17.
 */


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
import java.util.regex.Matcher;

/**
 * Created by Paul on 9/5/17.
 */


public class RecycleViewGroup extends ViewGroup {
    private String TAG = "RecycledViewGroup";
    private int mTouchSlop;
    private List<AwesomeViewGroup> awesomeViewGroups;
    int[] colors =new int[]{Color.RED, Color.BLUE, Color.GRAY, Color.YELLOW, Color.GREEN};
    private int width,height, childWidth, childHeight;
    private int preX, preY, startX, originX;


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
    private int mVelocityX, mVelocityY;
    private Scroller mScroller;
    private int mScrollTime;
    private float mAccelerator;


    // for fling thread
    private boolean canHorizontalFling = false;
    private boolean canVerticalFling = false;
    private int mSlots = 0;

    //for record offset
    private int offsetX = 0;
    private int offsetY = 0;
    //index of first view shown in screen, (near left side of screen)
    private int horizontalIndex = 0;

    //for calendar height calculation
    private int CELL_HEIGHT = 500;
    private int NUM_LAYOUTS = 3;

    // for disable cell scroll and allow percent of scroll
    private boolean disableCellScroll = false;
    private ScrollInterface scrollInterface;


    public RecycleViewGroup(Context context, int CELL_HEIGHT, int NUM_LAYOUTS) {
        super(context);
        this.CELL_HEIGHT = CELL_HEIGHT;
        this.NUM_LAYOUTS = NUM_LAYOUTS;
        init();
    }

    public RecycleViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadAttributes(attrs, context);
        init();
    }

    private void loadAttributes(AttributeSet attrs, Context context){
//        if (attrs != null && context != null){
//            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RecycledViewGroup,0,0);
//            try {
//                CELL_HEIGHT = (int) typedArray.getDimension(R.styleable.RecycledViewGroup_cell_height, (float) CELL_HEIGHT);
//                NUM_LAYOUTS = typedArray.getInteger(R.styleable.RecycledViewGroup_column, NUM_LAYOUTS);
//            }finally {
//                typedArray.recycle();
//            }
//        }
    }

    public void setDisableCellScroll(boolean isDisable){
        this.disableCellScroll = isDisable;
    }

    private ITimeAdapter adapter;

    public void setAdapter(ITimeAdapter adapter){
        this.adapter = adapter;
        adapter.setAwesomeViewGroups(awesomeViewGroups);
        adapter.onCreateViewHolders();
    }

    public int getFirstVisibleLeftOffset(){
        for (AwesomeViewGroup awesomeViewGroup: awesomeViewGroups){
            if (awesomeViewGroup.isVisibleInParent()){
                AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroup.getLayoutParams();
                return lp.left;
            }
        }
        return 0;
    }

    private void init(){
        ViewConfiguration vc = ViewConfiguration.get(getContext());
        mTouchSlop = vc.getScaledTouchSlop();
        awesomeViewGroups = new ArrayList<>();
        int numTotalLayouts = NUM_LAYOUTS + 2;
        for (int i = 0 ; i < numTotalLayouts ; i ++){
            AwesomeViewGroup awesomeViewGroup = new AwesomeViewGroup(getContext());
            awesomeViewGroup.setLayoutParams(new AwesomeViewGroup.AwesomeLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            awesomeViewGroup.setId(i);
            awesomeViewGroup.setBackgroundColor(colors[i]);
            addView(awesomeViewGroup);
            awesomeViewGroup.setInRecycledViewIndex(i);
            awesomeViewGroups.add(awesomeViewGroup);
        }

        mMaxVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
        mScroller = new Scroller(getContext());

    }

    private void moveChildX(int x){
        int awesomeViewSize = awesomeViewGroups.size();
        for(int i = 0 ; i < awesomeViewSize; i ++ ){
            AwesomeViewGroup awesomeViewGroup = awesomeViewGroups.get(i);
            AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroup.getLayoutParams();

            Log.i(TAG, "moveChildX: before : " + lp.left + " " + x);
            lp.left = lp.left - x;
            lp.right = lp.right - x;
            awesomeViewGroup.reLayoutByLp();
            Log.i(TAG, "moveChildX: " + lp.left + " ");
        }
        boolean changed = postCheckAfterMoveX();
        // if page has changed, notify handler
        if (changed){
            sendPageChangeMessage();
        }
    }


    private void moveChildY(int y){
        int childCount = getChildCount();
        int realY = 0;
        Log.i(TAG, "moveChildY: " + y);
        for (int i = 0 ; i < childCount ; i ++ ){
            AwesomeViewGroup awesomeViewGroup = awesomeViewGroups.get(i);
            AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroup.getLayoutParams();

            int top = lp.top - y;
            int bottom = lp.bottom - y;

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
//                Log.i(TAG, "postCheckAfterMoveX: " + "left 1 out of parent, move start");
                pageChanged = true;
                moveFirstViewToLast(awesomeViewGroups);
                reDrawViewGroupToLast(awesomeViewGroups.get(viewGroupSize - 2), awesomeViewGroups.get(viewGroupSize -1));
//                Log.i(TAG, "postCheckAfterMoveX: " + "left 1 out of parent, finish move");
                printAwesomeViewGroupRightLeft();
            }
        }else if(curScrollDir == SCROLL_RIGHT){
            // scroll right only check the last one
            AwesomeViewGroup rightViewGroup = awesomeViewGroups.get(viewGroupSize-2);
            if (rightViewGroup.isRightOutOfParent()){
                pageChanged = true;
                moveLastViewToFirst(awesomeViewGroups);
                reDrawViewGroupToFirst(awesomeViewGroups.get(1), awesomeViewGroups.get(0));
            }
        }
        return pageChanged;
    }

    private int getShouldVerticalMoveY(int top , int bottom, AwesomeViewGroup.AwesomeLayoutParams lp){
        int realY = 0;
        if (curScrollDir == SCROLL_DOWN){
            if (bottom < lp.parentHeight){
                // reach bottom, stop scrolling
                realY = lp.parentHeight - lp.bottom;
                return realY;
            }
        }else if (curScrollDir == SCROLL_UP){
            if (top > 0 ){
                // reach top, stop scrolling
                realY = 0 - lp.top;
                return realY;
            }
        }
        realY = bottom - lp.bottom;
        return realY;
    }

    private int preCheckBeforeMoveY(int top, int bottom, AwesomeViewGroup.AwesomeLayoutParams lp){
        int realY = 0;
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

//        horizontalIndex += 1;
        updateViewGroupIndexes(first, NUM_LAYOUTS + 2);

    }




    public void updateViewGroupIndexes(AwesomeViewGroup awesomeViewGroup, int offset){
        int preIndex = awesomeViewGroup.getInRecycledViewIndex();
        awesomeViewGroup.setInRecycledViewIndex(preIndex + offset);

        // todo: check if this can be changed
        if (adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

    private void moveLastViewToFirst(List<AwesomeViewGroup> viewGroupList){
        AwesomeViewGroup last = viewGroupList.get(viewGroupList.size() -1);
        viewGroupList.remove(last);
        viewGroupList.add(0, last);

        updateViewGroupIndexes(last, -(NUM_LAYOUTS+2));
//        horizontalIndex -= 1;
//        if (adapter != null){
//            adapter.removeViewOffset(horizontalIndex + NUM_LAYOUTS + 1);
//            adapter.addViewOffset(last.getItem(), horizontalIndex - 1);
//        }
    }

    // relativeIndex means offset count from first shown item
    private void updateChildParams(AwesomeViewGroup outParentChild, int relativeIndex){
        AwesomeViewGroup.AwesomeLayoutParams params = (AwesomeViewGroup.AwesomeLayoutParams) outParentChild.getLayoutParams();
        int left = relativeIndex * childWidth;
        params.left = left;
        params.right = left + childWidth;
    }

    private void reDrawViewGroupToLast(AwesomeViewGroup preAwesomeViewGroup, AwesomeViewGroup awesomeViewGroup){
        AwesomeViewGroup.AwesomeLayoutParams preLp = (AwesomeViewGroup.AwesomeLayoutParams) preAwesomeViewGroup.getLayoutParams();
        int preLpRight = preLp.right;

        AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroup.getLayoutParams();
        lp.left = preLpRight;
        lp.right = lp.left + lp.width;

        awesomeViewGroup.reLayoutByLp();
    }

    private void reDrawViewGroupToFirst(AwesomeViewGroup postAwesomeViewGroup, AwesomeViewGroup awesomeViewGroup){
        AwesomeViewGroup.AwesomeLayoutParams postLp = (AwesomeViewGroup.AwesomeLayoutParams) postAwesomeViewGroup.getLayoutParams();
        int postLeft = postLp.left;

        AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroup.getLayoutParams();
        lp.right = postLeft;
        lp.left = postLeft - lp.width;

        awesomeViewGroup.reLayoutByLp();
    }

//    private void updateNewFirstCalendar(AwesomeViewGroup awesomeViewGroup){
//        updateCalendarForAwesomeViewGroup(awesomeViewGroup, -5);
//    }
//
//    private void updateNewLastCalendar(AwesomeViewGroup awesomeViewGroup){
//        updateCalendarForAwesomeViewGroup(awesomeViewGroup, 5);
//    }
//
//    private void updateCalendarForAwesomeViewGroup(AwesomeViewGroup awesomeViewGroup, int delta){
//        Calendar c = awesomeViewGroup.getCalendar();
//        if (c==null){
//            return;
//        }
//
//        c.add(Calendar.DATE, delta);
//    }


    private void recordHorizontalScrollDir(int x){
        curScrollDir = x > 0 ? SCROLL_LEFT: SCROLL_RIGHT;
    }

    private void recordVerticalScrollDir(int y){
        curScrollDir = y > 0 ? SCROLL_DOWN : SCROLL_UP;
    }

    private void resetScrollWay(){
        curScrollWay = 0;
        hasDecideScrollWay = false;
    }

    private void noFlingEndCheck(){
        int needScrollDistance = childNeedsScrollToNearestPosition(awesomeViewGroups);
        if (needScrollDistance != 0.0){
            smoothMoveChildX(needScrollDistance, null);
        }
    }


    /**
     * move each children with animations (fake move, not real move), then real move each children
     * @param x
     */
    private void
    smoothMoveChildX(int x, Animation.AnimationListener animationListener){
        curScrollDir = x < 0 ? SCROLL_RIGHT : SCROLL_LEFT; // animation scroll direction

        for (AwesomeViewGroup awesomeViewGroup: awesomeViewGroups){
            applyAnimation(x, awesomeViewGroup, animationListener);
        }

        moveChildX(x);
    }

    private void applyAnimation(int x, AwesomeViewGroup awesomeViewGroup, Animation.AnimationListener animationListener){
        Animation ani = new TranslateAnimation(
                x,  0, 0.0f, 0.0f);
        ani.setDuration(500);
        ani.setInterpolator(new DecelerateInterpolator());
        ani.setFillAfter(false);
        ani.setAnimationListener(animationListener);
        awesomeViewGroup.startAnimation(ani);
    }

    private int childNeedsScrollToNearestPosition(List<AwesomeViewGroup> awesomeViewGroups){
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
    private AwesomeHandler mHandler = new AwesomeHandler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == AwesomeHandler.MOVE ) {
                AwesomeMessageObject awesomeMessageObject = (AwesomeMessageObject) msg.obj;
                int moveDis = awesomeMessageObject.distance;
                Log.i(TAG, "handleMessage: " + moveDis);
                moveChildX(moveDis);
            }

            if (msg.what == AwesomeHandler.CHANGE_PAGE){
                // TODO: 22/5/17 inform david the page has changed
                Log.i(TAG, "handleMessage: " + "page changed");
            }

            if (msg.what == AwesomeHandler.FLING_HORIZONTAL){
                int shouldMoveDis = (int) msg.obj;
                Log.i(TAG, "handleMessage: " + "fling " + shouldMoveDis);
                moveChildX(shouldMoveDis);
            }

            if (msg.what == AwesomeHandler.FLING_VERTICAL){
                int shouldMoveDis = (int) msg.obj;
                moveChildY(shouldMoveDis);
            }

            if (msg.what == AwesomeHandler.MOVE_WITH_PERCENT){
                if (scrollInterface!=null){
                    float percent = (float) msg.obj;
                    scrollInterface.getMovePercent(percent);
                }
            }
        }
    };

    private void sendPageChangeMessage(){
        Message message = new Message();
        message.what = AwesomeHandler.CHANGE_PAGE;
        mHandler.sendMessage(message);
    }

    /**
     * fling thread is for when doing fling, it continuously sending new moving
     * distance to handler.
     */
    private AwesomeThread flingThread = new AwesomeThread();

    private AwesomeVerticalThread verticalFlingThread = new AwesomeVerticalThread();


    private void checkFling(int velocityX, int velocityY, int scrollDir, int alreadyMoveDis){
        int maxDis = width;
        int[] scrollPos = ScrollHelper.calculateScrollDistance(mScroller, velocityX, velocityY, maxDis, alreadyMoveDis);

        switch (scrollDir){
            case SCROLL_LEFT:
            case SCROLL_RIGHT:

                if (disableCellScroll){
                    int distance = 0;
                    // page scroll threshold
                    if (Math.abs(alreadyMoveDis)> 0.5 * width){
                        int posOrNeg = Math.abs(alreadyMoveDis)/alreadyMoveDis;
                        distance = (width - Math.abs(alreadyMoveDis)) * posOrNeg;
                    }else{
                        // if scroll back, change scroll direction.
                        distance = -alreadyMoveDis;
                    }
                    curScrollDir = distance > 0 ? SCROLL_RIGHT : SCROLL_LEFT;
                    Log.i(TAG, "checkFling: " + curScrollDir);
                    mScrollTime = ScrollHelper.calculateScrollTime(velocityX);
                    mAccelerator = ScrollHelper.calculateAccelerator(distance, mScrollTime);
                    mSlots = (Math.abs(mScrollTime) * 16);
                    canHorizontalFling = true;
                    if (flingThread.getState()!= Thread.State.NEW){
                        flingThread = new AwesomeThread();
                    }
                    flingThread.setShouldFlingDis(distance);
                    flingThread.start();
                    return;
                }

                if (shouldFling(velocityX)){
                    mScrollTime = ScrollHelper.calculateScrollTime(velocityX);
                    int distance = scrollPos[0];
                    int offset = alreadyMoveDis;
                    Log.i(TAG, "checkFling: before distance: " + distance + " offset : " + offset + " unitLength : " + childWidth);
                    distance = ScrollHelper.findRightPosition(distance, offset, childWidth);
                    Log.i(TAG, "checkFling: distance: " + distance + " offset : " + offset +  " unitLength : " + childWidth);
                    mAccelerator = ScrollHelper.calculateAccelerator(distance, mScrollTime);
                    mSlots = (Math.abs(mScrollTime) * 16);
//                    Log.i(TAG, "checkFling: distance: " + distance +
//                            " mScrollTime: " + mScrollTime + " offset: " + offset +
//                            " mSlots: " + mSlots);

                    canHorizontalFling = true;
                    if (flingThread.getState()!= Thread.State.NEW){
                        flingThread = new AwesomeThread();
                    }
                    flingThread.setShouldFlingDis(distance);
                    flingThread.start();

                }else{
                    noFlingEndCheck();
                    // not fling, only do post check
                    }
                break;
            case SCROLL_UP:
            case SCROLL_DOWN:
                if (shouldFling(velocityY)){
                    int distance = scrollPos[1];
                    AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroups.get(0).getLayoutParams();
                    Log.i(TAG, "checkFling: before " + distance + " top : " + lp.top);
                    int top = lp.top + distance; // need to check pos or neg
                    int bottom = lp.bottom + distance;
                    int actualY = getShouldVerticalMoveY(top, bottom, lp);
                    mScrollTime = ScrollHelper.calculateVerticalScrollTime(velocityY);
                    mAccelerator = ScrollHelper.calculateAccelerator(actualY, mScrollTime);
                    mSlots = (Math.abs(mScrollTime) * 16);
                    canVerticalFling = true;
                    if (verticalFlingThread.getState() != Thread.State.NEW){
                        verticalFlingThread = new AwesomeVerticalThread();
                    }
                    verticalFlingThread.start();
                }
                break;
        }
    }

    private void sendMoveMessage(int direction, int distance){
        Message message = new Message();
        message.what = AwesomeHandler.MOVE;
        message.obj = new AwesomeMessageObject(direction, distance);
        mHandler.sendMessage(message);
    }

    private void sendMovePercentMessage(float percent){
        Message msg = new Message();
        msg.what = AwesomeHandler.MOVE_WITH_PERCENT;
        msg.obj = percent;
        mHandler.sendMessage(msg);
    }

    // 200 is a threshold , if more than 200, then fling, otherwise not
    private boolean shouldFling(int velocity){
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
                originX = (int) ev.getX();
                startX = (int) ev.getX();
                preX = (int) ev.getX();
                preY = (int) ev.getY();
                if (canHorizontalFling){
                    // view group is horizontal flinging, then can only do horizontal scroll
                    hasDecideScrollWay = true;
                    curScrollWay = SCROLL_HORIZONTAL;
                    canHorizontalFling = false;
                    return true;
                }
                canHorizontalFling = false; // canFling -> false, stop flinging
                break;
            case MotionEvent.ACTION_MOVE:
                //action move here has two options.
                // option1: if the scroll distance is larger than scroll threshold, then this is a recycledView scroll,
                //          so that this action move need to be intercepted, this recycledViewGroup will consume the moving
                // option2: if the scroll distance is less than scroll threshold, then this is not a recycledView scroll, this
                //          action must be passed to its children.
                // the action move code here is similar to code in onTouchEvent, because when intercepted move, next time,
                // move action only be called in onTouchEvent
                int newY = (int) ev.getY();
                int newX = (int) ev.getX();

                if (!hasDecideScrollWay) {
                    if (mTouchSlop < Math.abs(newY - preY)) {
                        // vertical scroll
                        Log.i(TAG, "onInterceptTouchEvent: " + "vertical scroll");
                        hasDecideScrollWay = true;
                        curScrollWay = SCROLL_VERTICAL;
                    } else if (mTouchSlop < Math.abs(newX - preX)) {
                        // horizontal scroll
                        Log.i(TAG, "onInterceptTouchEvent: " + "horizontal scroll");
                        hasDecideScrollWay = true;
                        curScrollWay = SCROLL_HORIZONTAL;
                    }
                }

                if (hasDecideScrollWay){
                    if (curScrollWay == SCROLL_HORIZONTAL) {
                        int moveX = preX - newX;
                        int moveFromStartX = startX - newX;
                        if (Math.abs(moveFromStartX) > childWidth){
                            startX += curScrollDir == SCROLL_LEFT ? -childWidth: childWidth;
                        }
                        recordHorizontalScrollDir(moveFromStartX);
                        sendMoveMessage(curScrollDir, moveX);
                        preX = newX;
                    }else if (curScrollWay == SCROLL_VERTICAL){
                        int moveY = preY - newY;
                        recordVerticalScrollDir(moveY);
                        moveChildY(moveY);
                        preY = newY;
                    }
                    mVelocityTracker.addMovement(ev);
                    mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                    mVelocityX = (int) mVelocityTracker.getXVelocity();
                    mVelocityY = (int) mVelocityTracker.getYVelocity();

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
                int newY = (int) event.getY();
                int newX = (int) event.getX();

                if (!hasDecideScrollWay) {
                    if (mTouchSlop < Math.abs(newY - preY)) {
                        // vertical scroll
                        Log.i(TAG, "onTouchEvent: " + "vertical scroll");
                        hasDecideScrollWay = true;
                        curScrollWay = SCROLL_VERTICAL;
                    } else if (mTouchSlop < Math.abs(newX - preX)) {
                        // horizontal scroll
                        Log.i(TAG, "onTouchEvent: " + "horizontal scroll");
                        hasDecideScrollWay = true;
                        curScrollWay = SCROLL_HORIZONTAL;
                    }
                }

                if (hasDecideScrollWay){

                    if (curScrollWay == SCROLL_HORIZONTAL) {
                        int moveX = preX - newX;
                        int moveFromStartX = startX - newX;
                        if (Math.abs(moveFromStartX) > childWidth){
                            startX += curScrollDir == SCROLL_LEFT ? -childWidth: childWidth;
                        }
                        recordHorizontalScrollDir(moveFromStartX);
                        sendMoveMessage(curScrollDir, moveX);
                        preX = newX;

                        // if cell scroll is disable
                        if (disableCellScroll){
                            float moveDis = originX - event.getX();
                            float percent = Math.abs(moveDis / width);
                            sendMovePercentMessage(percent);
                            Log.i(TAG, "onTouchEvent: " + percent);

                        }

                    }else if (curScrollWay == SCROLL_VERTICAL){
                        int moveY = preY - newY;
                        recordVerticalScrollDir(moveY);
                        moveChildY(moveY);
                        preY = newY;
                    }
                    mVelocityTracker.addMovement(event);
                    mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                    mVelocityX = (int) mVelocityTracker.getXVelocity();
                    mVelocityY = (int) mVelocityTracker.getYVelocity();
                }

                Log.i(TAG, "onTouchEvent: " + "true");
                // if recycled view is scrolling, then the move action has to be consumed, need true here
                return true;

            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "onTouchEvent: " + "down");
                Log.i(TAG, "onTouchEvent: " + "true");
                // here must return true. So if no children use the 'down', recycledViewGroup can still use it.
                // Otherwise, you will find the recycledViewGroup cannot be scrolled.
                return true;
            case MotionEvent.ACTION_CANCEL:
                Log.i(TAG, "onTouchEvent: " + "cancel");
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "onTouchEvent: " + "up");
                if (hasDecideScrollWay) {
                    resetScrollWay();
                    int alreadyMoveDis =(int) (event.getX() - originX);
                    checkFling(mVelocityX, mVelocityY, curScrollDir, alreadyMoveDis);
                }
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
        childHeight = calculateItemHeight(CELL_HEIGHT);
        scrollThreshold = childHeight/2;

        int childCount = awesomeViewGroups.size();
        int childWidthSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
        int childHeightSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);

        for (int i = 0 ; i < childCount ; i++){
            AwesomeViewGroup awesomeViewGroup = awesomeViewGroups.get(i);
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
//                lp.left = getFirstVisibleLeftOffset() + (i-1) * childWidth;
                lp.left = (i-1) * childWidth;
                Log.i(TAG, "onMeasure: " + "this is : " + i + " left is : " + lp.left);
            }

            lp.right = lp.left + childWidth;
            lp.bottom = lp.top + lp.height;
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // start to layout children
        int childCount = awesomeViewGroups.size();
        for (int i = 0 ; i < childCount; i ++){
            AwesomeViewGroup child = awesomeViewGroups.get(i);
            child.reLayoutByLp();
        }
    }

    private int calculateItemHeight(int baseParams){
        return baseParams * 25;
    }


    public interface CalendarInterface{
        Calendar getFirstDay();
    }

    public interface ScrollInterface{
        void getMovePercent(float percent);
    }

    public void setScrollInterface(ScrollInterface scrollInterface){
        this.scrollInterface = scrollInterface;
    }

    /**
     * After fling finish, distance might has might variation, need this to check.
     */
    private int postCheckAfterFlingFinish(){
        int needScrollDistance = childNeedsScrollToNearestPosition(awesomeViewGroups);

        AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroups.get(1).getLayoutParams();
        AwesomeViewGroup.AwesomeLayoutParams lp2 = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroups.get(2).getLayoutParams();
        Log.i(TAG, "postCheckAfterFlingFinish: index1 left : " + lp.left + " index2 left: " + lp2.left + " scoll dis : " + needScrollDistance);

        return needScrollDistance;
//        return lp.left - 0;
    }

    private void printAwesomeViewgroupTopBottom(){
        for (AwesomeViewGroup awesomeViewGroup : awesomeViewGroups){
            AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroup.getLayoutParams();
            Log.i(TAG, "run: " + "top : " + lp.top + " bottom : " + lp.bottom);
        }
    }

    private void printAwesomeViewGroupRightLeft(){
        for (AwesomeViewGroup awesomeViewGroup : awesomeViewGroups){
            AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroup.getLayoutParams();
            Log.i(TAG, "run: right" + "left : " + lp.left + " right : " + lp.right);
        }
        Log.i(TAG, "run: right" );
    }

    private class AwesomeVerticalThread extends Thread{
        @Override
        public void run() {
            int index = 0;
            while(canVerticalFling){
                Message msg = new Message();
                msg.what = AwesomeHandler.FLING_VERTICAL;
                float curTime = mScrollTime * (mSlots - index - 1) / (float) mSlots;
                float nextTime = index + 1 == mSlots ? curTime : mScrollTime * (mSlots - index) / (float) mSlots;
                int curDistance = ScrollHelper.getCurrentDistance(mAccelerator, curTime);
                int nextDistance = ScrollHelper.getCurrentDistance(mAccelerator, nextTime);
                int shouldMoveDis = curDistance - nextDistance;
                msg.obj = shouldMoveDis;
                Log.i(TAG, "run: " + shouldMoveDis);
                mHandler.sendMessage(msg);
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                index++;
                if (index >= mSlots) {
                    canVerticalFling = false;
                    printAwesomeViewgroupTopBottom();
                }
            }
        }
    }


    private class AwesomeThread extends Thread{
        private int shouldFlingDis = 0;

        public void setShouldFlingDis(int shouldFlingDis) {
            this.shouldFlingDis = shouldFlingDis;
        }

        @Override
        public void run() {
            int index = 0;
            int totalFling = 0;
            // this is horizontal fling
            while (canHorizontalFling) {
                Message msg = new Message();
                msg.what = AwesomeHandler.FLING_HORIZONTAL;
                float curTime = mScrollTime * (mSlots - index - 1) / (float) mSlots;
                float nextTime = index + 1 == mSlots ? curTime : mScrollTime * (mSlots - index) / (float) mSlots;
                int curDistance = ScrollHelper.getCurrentDistance(mAccelerator, curTime);
                int nextDistance = ScrollHelper.getCurrentDistance(mAccelerator, nextTime);
                int shouldMoveDis = curDistance - nextDistance;
                msg.obj = shouldMoveDis;
                totalFling += shouldMoveDis;
                if (curTime == nextTime) {
                    msg.obj = postCheckAfterFlingFinish();
                    Log.i(TAG, "run: " + "totalFling: " + totalFling + " " + "shouldMoveDis" + shouldFlingDis + " last fling: " + msg.obj);
                }
                mHandler.sendMessage(msg);
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                index++;
                if (index >= mSlots) {
                    canHorizontalFling = false;
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
        void onHorizontalScroll(int dx, int preOffsetX);
        void onVerticalScroll(int dy, int preOffsetY);
    }

    public void smoothMoveWithOffset(int moveOffset, @Nullable Animation.AnimationListener animationListener){
        if (moveOffset == 0){
            return;
        }
        int distance = moveOffset * childWidth;
        smoothMoveChildX(distance, animationListener);
    }

    public void moveWithOffset(int moveOffset){
        if (moveOffset == 0){
            return;
        }
//        int distance = moveOffset * childWidth;
//        directMoveChildX(distance);
        for (AwesomeViewGroup awesomeViewGroup: awesomeViewGroups) {
            updateViewGroupIndexes(awesomeViewGroup, moveOffset);
        }
    }

    public View getFirstShowItem(){
        return awesomeViewGroups.get(1).getItem();
    }

    private class AwesomeMessageObject{
        public int direction;
        public int distance;

        public AwesomeMessageObject(int direction, int distance) {
            this.direction = direction;
            this.distance = distance;
        }
    }

    private static class AwesomeHandler extends Handler{
        public final static int START = 1000;
        public final static int MOVE = 1001;
        public final static int STOP = 1002;
        public final static int FLING_HORIZONTAL = 1003;
        public final static int CHANGE_PAGE = 1004;
        public final static int FLING_VERTICAL = 1005;
        public final static int MOVE_WITH_PERCENT = 1006;
    }

}
