package com.developer.paul.recycleviewgroup;

/**
 * Created by Paul on 22/5/17.
 */


import android.animation.ValueAnimator;
import android.content.Context;
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
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Paul on 9/5/17.
 */


public class RecycleViewGroup extends ViewGroup {
    private String TAG = "RecycledViewGroup";
    private int mTouchSlop;
    private List<AwesomeViewGroup> awesomeViewGroups;
    int[] colors =new int[]{Color.RED, Color.BLUE, Color.GRAY, Color.YELLOW, Color.GREEN, Color.WHITE, Color.MAGENTA, Color.DKGRAY, Color.CYAN};
    private int width,height, childWidth, childHeight;
    private int preX, preY, originX, newX, newY;


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

    private OnSetting onSetting;


    // for fling
    private VelocityTracker mVelocityTracker;
    private int mMaxVelocity;
    private int mVelocityX, mVelocityY;
    private Scroller mScroller;
    private float mScrollTime;
    private float mAccelerator;


    // for fling thread
//    private boolean canHorizontalFling = false;
    private boolean canVerticalFling = false;
    private int mSlots = 0;

    //for record offset
    private int offsetX = 0;
    private int offsetY = 0;
    //index of first view shown in screen, (near left side of screen)
    private int horizontalIndex = 0;

    //for calendar height calculation
    private int CELL_HEIGHT = 500;
    private int NUM_LAYOUTS = 7;

    // for disable cell scroll and allow percent of scroll
    private boolean disableCellScroll = false;
    private ScrollInterface scrollInterface;
    private boolean shouldAbortGesture = false;

    private boolean disableScroll;



    public RecycleViewGroup(Context context, int CELL_HEIGHT, int NUM_LAYOUTS) {
        super(context);
        this.CELL_HEIGHT = CELL_HEIGHT;
        this.NUM_LAYOUTS = NUM_LAYOUTS;
        init();
    }

    public RecycleViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
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
//            awesomeViewGroup.setBackgroundColor(colors[i]);
            addView(awesomeViewGroup);
            awesomeViewGroup.setInRecycledViewIndex(i);
            awesomeViewGroups.add(awesomeViewGroup);
        }

        mMaxVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
        mScroller = new Scroller(getContext());
    }

    public void moveXBy(int x){
        if (x != 0) {
            curScrollDir = x > 0 ? SCROLL_RIGHT : SCROLL_LEFT;
        }
        sendMoveMessage(curScrollDir, x);
    }

    private void moveChildX(int x){
        int awesomeViewSize = awesomeViewGroups.size();
        for(int i = 0 ; i < awesomeViewSize; i ++ ){
            AwesomeViewGroup awesomeViewGroup = awesomeViewGroups.get(i);
            AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroup.getLayoutParams();
            lp.left = lp.left + x;
            lp.right = lp.right + x;
            awesomeViewGroup.reLayoutByLp();
        }

        boolean changed = postCheckAfterMoveX();
        // if page has changed, notify handler
        if (changed){
            sendPageChangeMessage();
        }

        if (onScroll!=null) {
            onScroll.onHorizontalScroll(x, newX - originX);
        }

        // send percent when move
        if (disableCellScroll){
            float moveDis = newX - originX;
            float percent = Math.abs(moveDis / width);
            sendMovePercentMessage(percent);
        }

        preX = newX; // has to be there, because handler might delay...
    }


    private void moveChildY(int y){
        int realY = 0;
        for (int i = 0 ; i < awesomeViewGroups.size() ; i ++ ){
            AwesomeViewGroup awesomeViewGroup = awesomeViewGroups.get(i);
            AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroup.getLayoutParams();
            int top = lp.top + y;
            int bottom = lp.bottom + y;

            realY = preCheckBeforeMoveY(top, bottom, lp); // if scroll too much...
            awesomeViewGroup.reLayoutByLp();
        }
        Log.i(TAG, "moveChildY: after : " + y );

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
//                printAwesomeViewGroupRightLeft();
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
        printAwesomeViewgroupTopBottom();
        if (curScrollDir == SCROLL_UP){
            Log.i(TAG, "preCheckBeforeMoveY:  " + "up");
            if (bottom < lp.parentHeight){
                // reach bottom, stop scrolling
                realY = lp.parentHeight - lp.bottom;
                lp.bottom = lp.parentHeight;
                lp.top = lp.bottom - lp.height;
                return realY;
            }
        }else if (curScrollDir == SCROLL_DOWN){
            Log.i(TAG, "preCheckBeforeMoveY: " + "down");
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
            adapter.notifyDataSetChanged(awesomeViewGroup);
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
        if (x != 0) {
            curScrollDir = x < 0 ? SCROLL_LEFT : SCROLL_RIGHT;
        }
    }

    private void recordVerticalScrollDir(int y){
        if (y != 0) {
            curScrollDir = y > 0 ? SCROLL_DOWN : SCROLL_UP;
        }
    }

    private void resetScrollWay(){
        curScrollWay = 0;
        hasDecideScrollWay = false;
    }

    private void flingEndCheck(){
        scrollToClosetPosition();
    }

    private void scrollToClosetPosition() {
        int needScrollDistance = childNeedsScrollToNearestPosition(awesomeViewGroups);
        if (needScrollDistance != 0.0) {
            smoothMoveChildX(needScrollDistance, null);
        }
    }

    // for x fling
    private void smoothMoveChildX(int x, long duration){
        if (x != 0) {
            curScrollDir = x < 0 ? SCROLL_LEFT : SCROLL_RIGHT; // animation scroll direction
        }
        applyAnimation(x, duration, null);
    }



    /**
     * move each children with animations (fake move, not real move), then real move each children
     * @param x
     */
    private void
    smoothMoveChildX(int x, Animation.AnimationListener animationListener){
        if (x != 0) {
            curScrollDir = x < 0 ? SCROLL_LEFT : SCROLL_RIGHT; // animation scroll direction
        }
        applyAnimation(x, 500,animationListener);
    }

    private void applyAnimation(final int x, long duration, Animation.AnimationListener animationListener){
        ValueAnimator animator = ValueAnimator.ofInt(0, x);
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            int preAniX = 0;
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                shouldAbortGesture = true;
                int nowValue = (int) animation.getAnimatedValue();
                int offset = nowValue - preAniX;
                newX = preX + offset;
                moveChildX(offset);
                preAniX=nowValue;
                preX = newX;
                if (x == (int)animation.getAnimatedValue()){
                    // when end of animation
                    shouldAbortGesture = false;
                    flingEndCheck();
                }
            }

        });
        animator.start();
    }

    private int childNeedsScrollToNearestPosition(List<AwesomeViewGroup> awesomeViewGroups){
        for (AwesomeViewGroup awesomeViewGroup : awesomeViewGroups){
            if (awesomeViewGroup.isVisibleInParent() && awesomeViewGroup.isOutOfParent()){
                AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroup.getLayoutParams();
                return Math.abs(lp.right) > Math.abs(lp.left) ? -lp.left : -lp.right;
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
            if (msg.what == AwesomeHandler.HORIZONTAL_MOVE) {
                AwesomeMessageObject awesomeMessageObject = (AwesomeMessageObject) msg.obj;
                int moveDis = awesomeMessageObject.distance;
                moveChildX(moveDis);
            }

            if (msg.what == AwesomeHandler.CHANGE_PAGE){
                // TODO: 22/5/17 inform david the page has changed
            }

            if (msg.what == AwesomeHandler.FLING_HORIZONTAL){
                int shouldMoveDis = (int) msg.obj;
                newX += shouldMoveDis;
                moveChildX(shouldMoveDis);
            }

            if (msg.what == AwesomeHandler.FLING_VERTICAL){
                int shouldMoveDis = (int) msg.obj;
                moveChildY(shouldMoveDis);
            }

            if (msg.what == AwesomeHandler.MOVE_WITH_PERCENT){
                if (scrollInterface!=null){
                    float percent = (float) msg.obj;
                    scrollInterface.getMovePercent(percent, curScrollDir);
                }
            }

            if (msg.what == AwesomeHandler.VERTICAL_MOVE){
                int shouldMoveDis = (int) msg.obj;
                moveChildY(shouldMoveDis);
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
//    private AwesomeThread flingThread = new AwesomeThread();

    private AwesomeVerticalThread verticalFlingThread = new AwesomeVerticalThread();


    private void checkFling(int velocityX, int velocityY, int scrollDir, int alreadyMoveDis){
        int maxDis = width;
        int[] scrollPos = ScrollHelper.calculateScrollDistance(mScroller, velocityX, velocityY, maxDis, alreadyMoveDis, childWidth);

//        Log.i(TAG, "checkFling: " + " maxDis = " + maxDis  +
//                " alreadyMoveDis : " + alreadyMoveDis+ " dis : " + scrollPos[0] +
//                " unitLength : " + childWidth);
        switch (scrollDir){
            case SCROLL_LEFT:
            case SCROLL_RIGHT:

                if (disableCellScroll){
                    int distance = 0;
                    // page scroll threshold
                    if (Math.abs(alreadyMoveDis)> 0.3 * width){
                        int posOrNeg = Math.abs(alreadyMoveDis)/alreadyMoveDis;
                        distance = (width - Math.abs(alreadyMoveDis)) * posOrNeg;
                    }else{
                        // if scroll back, change scroll direction.
                        distance = -alreadyMoveDis;
                    }
                    if (distance!=0) {
                        curScrollDir = distance > 0 ? SCROLL_RIGHT : SCROLL_LEFT;
                    }
                    smoothMoveChildX(distance, (long)mScrollTime * 1000);
                    return;
                }

                if (shouldFling(velocityX)){
                    mScrollTime = ScrollHelper.calculateScrollTime(velocityX);
                    int distance = scrollPos[0];
                    smoothMoveChildX(distance, (long)mScrollTime * 1000);
                }else{
                    scrollToClosetPosition();
                    // not fling, only do post check
                    }
                break;
            case SCROLL_UP:
            case SCROLL_DOWN:
                if (shouldFling(velocityY)){
                    int distance = scrollPos[1];
                    AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroups.get(0).getLayoutParams();
//                    Log.i(TAG, "checkFling: before " + distance + " top : " + lp.top);
                    int top = lp.top + distance; // need to check pos or neg
                    int bottom = lp.bottom + distance;
                    int actualY = getShouldVerticalMoveY(top, bottom, lp);
                    mScrollTime = ScrollHelper.calculateVerticalScrollTime(velocityY);
                    mAccelerator = ScrollHelper.calculateAccelerator(actualY, mScrollTime);
                    mSlots = (int) (Math.abs(mScrollTime) * 16);
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
        message.what = AwesomeHandler.HORIZONTAL_MOVE;
        message.obj = new AwesomeMessageObject(direction, distance);
        mHandler.sendMessage(message);
    }

    private void sendMovePercentMessage(float percent){
        Message msg = new Message();
        msg.what = AwesomeHandler.MOVE_WITH_PERCENT;
        msg.obj = percent;
        mHandler.sendMessage(msg);
    }

    private void sendVerticalMoveMessage(int y){
        Message msg = new Message();
        msg.what = AwesomeHandler.VERTICAL_MOVE;
        msg.obj = y;
        mHandler.sendMessage(msg);
    }

    // 200 is a threshold , if more than 200, then fling, otherwise not
    private boolean shouldFling(int velocity){
        return Math.abs(velocity) > 200;
    }

    public void setDisableScroll(boolean b){
        this.disableScroll = b;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        printFirstBoundary();
        if (disableScroll){
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // action down need to pass to child
                // when only page scroll and fling
//                if (disableCellScroll && canHorizontalFling){

                if (shouldAbortGesture){
                    return true;
                }

                if (mVelocityTracker==null){
                    mVelocityTracker = VelocityTracker.obtain();
                }else{
                    mVelocityTracker.clear();
                }
                mVelocityX = 0;
                mVelocityY = 0;
                originX = (int) ev.getX();
                preX = (int) ev.getX();
                preY = (int) ev.getY();
//                if (canHorizontalFling){
//                    // view group is horizontal flinging, then can only do horizontal scroll
//                    hasDecideScrollWay = true;
//                    curScrollWay = SCROLL_HORIZONTAL;
//                    canHorizontalFling = false;
//                    return true;
//                }
//                canHorizontalFling = false; // canFling -> false, stop flinging
                break;
            case MotionEvent.ACTION_MOVE:
                //action move here has two options.
                // option1: if the scroll distance is larger than scroll threshold, then this is a recycledView scroll,
                //          so that this action move need to be intercepted, this recycledViewGroup will consume the moving
                // option2: if the scroll distance is less than scroll threshold, then this is not a recycledView scroll, this
                //          action must be passed to its children.
                // the action move code here is similar to code in onTouchEvent, because when intercepted move, next time,
                // move action only be called in onTouchEvent
                newY = (int) ev.getY();
                newX = (int) ev.getX();

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
                        int moveX = newX - preX;
                        recordHorizontalScrollDir(moveX);
                        sendMoveMessage(curScrollDir, moveX);

                    }else if (curScrollWay == SCROLL_VERTICAL){
                        int moveY = newY - preY;
                        recordVerticalScrollDir(moveY);
                        sendVerticalMoveMessage(moveY);
                        preY = newY;
                    }
                    mVelocityTracker.addMovement(ev);
                    mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                    mVelocityX = (int) mVelocityTracker.getXVelocity();
                    mVelocityY = (int) mVelocityTracker.getYVelocity();

                    return true; // here must be true, so the recycledViewGroup can consume the action from now on
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }

        boolean value = super.onInterceptTouchEvent(ev);
        return value;
    }

    private boolean isScrollOutParent = false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        if (disableScroll){
            return false;
        }

        // if still fling, then abort this touch
//        if (disableCellScroll && shouldAbortGesture){
        if (shouldAbortGesture){
            return true;
        }

        switch (action){
            case MotionEvent.ACTION_OUTSIDE:
                Log.i(TAG, "onTouchEvent: " + "outside");
                break;
            case MotionEvent.ACTION_MOVE:
                if (isScrollOutParent){
                    return true;
                }
                newY = (int) event.getY();
                newX = (int) event.getX();

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
                        if (newX > width){
                            // if right out of parent
                            isScrollOutParent = true;
//                            event.setAction(MotionEvent.ACTION_OUTSIDE);
                            newX = width;
                        }else if (newX < 0){
                            isScrollOutParent = true;
//                            event.setAction(MotionEvent.ACTION_OUTSIDE);
                            newX = 0;
                        }
                        int moveX = newX - preX;
                        recordHorizontalScrollDir(moveX);
                        sendMoveMessage(curScrollDir, moveX);
                    }else if (curScrollWay == SCROLL_VERTICAL){
                        int moveY = newY - preY;
                        recordVerticalScrollDir(moveY);
                        sendVerticalMoveMessage(moveY);
                        preY = newY;
                    }
                    mVelocityTracker.addMovement(event);
                    mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                    mVelocityX = (int) mVelocityTracker.getXVelocity();
                    mVelocityY = (int) mVelocityTracker.getYVelocity();
                }
                // if recycled view is scrolling, then the move action has to be consumed, need true here
                return super.onTouchEvent(event);

            case MotionEvent.ACTION_DOWN:
                // here must return true. So if no children use the 'down', recycledViewGroup can still use it.
                // Otherwise, you will find the recycledViewGroup cannot be scrolled.
                return true;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                if (hasDecideScrollWay) {
                    resetScrollWay();
                    int alreadyMoveDis = 0;
                    if (isScrollOutParent){
                        alreadyMoveDis = event.getX() < 0 ?  0 - originX : width - originX;
                        isScrollOutParent = false;
                        checkFling(mVelocityX, mVelocityY, curScrollDir, alreadyMoveDis);
                        return false;
                    }else {
                        alreadyMoveDis = (int) (event.getX() - originX);
                    }
                    checkFling(mVelocityX, mVelocityY, curScrollDir, alreadyMoveDis);
                }
                // this action up has to be true, because if you consumed the move actions in here,
                // the last action up should also be consumed here.
                return true;
        }

        boolean value = super.onTouchEvent(event);
//        Log.i(TAG, "onTouchEvent:onTouch" + value);
        return value;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);

        childWidth = width/NUM_LAYOUTS;

        //for childHeight;
        // if MatchParent && Exact -> = passed params;
        // if wrap content -> = interfaced value
        if (onSetting==null) {
            childHeight = calculateItemHeight(CELL_HEIGHT);
        }else{
            childHeight = onSetting.getItemHeight(heightMeasureSpec);
        }

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
                lp.left = (i-1) * childWidth;
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
        void getMovePercent(float percent, int direction);
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
    }

    private void printFirstBoundary(){
        AwesomeViewGroup awesomeViewGroup = awesomeViewGroups.get(1);
        AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroup.getLayoutParams();
        Log.i(TAG, "printFirstBoundary: " + " left : " + lp.left + " right : "+ lp.right);
    }

    private void printAwesomeViewgroupTopBottom(){
        for (AwesomeViewGroup awesomeViewGroup : awesomeViewGroups){
            AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroup.getLayoutParams();
//            Log.i(TAG, "printAwesomeV: " + "top : " + lp.top + " bottom : " + lp.bottom);
        }
    }

    private void printAwesomeViewGroupRightLeft(){
        for (AwesomeViewGroup awesomeViewGroup : awesomeViewGroups){
            AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroup.getLayoutParams();
            Log.i(TAG, "printAwesomeViewGroupRightLeft: ");
            Log.i(TAG, "printAwesomeViewGroupRightLeft" + "left : " + lp.left + " right : " + lp.right);
            Log.i(TAG, "printAwesomeViewGroupRightLeft: ");
        }
//        Log.i(TAG, "run: right" );
    }

    private class AwesomeVerticalThread extends Thread{
        @Override
        public void run() {
            int index = 0;
            while(canVerticalFling){
                shouldAbortGesture = true;
                Message msg = new Message();
                msg.what = AwesomeHandler.FLING_VERTICAL;
                float curTime = mScrollTime * (mSlots - index - 1) / (float) mSlots;
                float nextTime = index + 1 == mSlots ? curTime : mScrollTime * (mSlots - index) / (float) mSlots;
                int curDistance = ScrollHelper.getCurrentDistance(mAccelerator, curTime);
                int nextDistance = ScrollHelper.getCurrentDistance(mAccelerator, nextTime);
                int shouldMoveDis = nextDistance - curDistance;
                msg.obj = shouldMoveDis;
//                Log.i(TAG, "run: " + shouldMoveDis);
                mHandler.sendMessage(msg);
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                index++;
                if (index >= mSlots) {
                    canVerticalFling = false;
                    shouldAbortGesture = false;
//                    printAwesomeViewgroupTopBottom();
                }
            }
        }
    }


    private OnScroll onScroll;

    public void setOnScrollListener(OnScroll onScroll){
        this.onScroll = onScroll;
    }

    public interface OnSetting{
        int getItemHeight(int heightSpec);
    }

    public void setOnSetting(OnSetting onSetting){
        this.onSetting = onSetting;
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
        public final static int HORIZONTAL_MOVE = 1001;
        public final static int STOP = 1002;
        public final static int FLING_HORIZONTAL = 1003;
        public final static int CHANGE_PAGE = 1004;
        public final static int FLING_VERTICAL = 1005;
        public final static int MOVE_WITH_PERCENT = 1006;
        public final static int VERTICAL_MOVE = 1007;
    }

}
