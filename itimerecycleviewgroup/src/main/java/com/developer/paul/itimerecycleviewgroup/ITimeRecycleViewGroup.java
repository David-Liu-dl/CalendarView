package com.developer.paul.itimerecycleviewgroup;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul on 30/5/17.
 */

public class ITimeRecycleViewGroup extends ViewGroup implements RecycleInterface {
    private static final String TAG = "crazyMove";
    private List<AwesomeViewGroup> awesomeViewGroupList = new ArrayList<>();
    private int NUM_SHOW = 7;
    int[] colors = new int[]{Color.RED, Color.BLUE, Color.GRAY, Color.YELLOW, Color.GREEN, Color.WHITE, Color.MAGENTA, Color.DKGRAY, Color.CYAN};

    private int viewHeight, viewWidth, childWidth, childHeight;

    private final int SCROLL_LEFT = -1, SCROLL_RIGHT = 1, SCROLL_UP = -2, SCROLL_DOWN = 2;
    private int scrollDir = 0; //{ SCROLL_LEFT, SCROLL_RIGHT, SCROLL_UP, SCROLL_DOWN }

    private final int SCROLL_HORIZONTAL = 10001, SCROLL_VERTICAL = 10002;
    private int scrollModel = 0;

    private int mTouchSlop;
    private boolean scrollOverTouchSlop = false;

    //for fling
    private VelocityTracker mVelocityTracker;
    private int mMaxVelocity;
    private float mVelocityX, mVelocityY;
    private Scroller mScroller;
    private boolean canHorizontalFling;
    private int status;

    public final static int START = 1000;
    public final static int HORIZONTAL_MOVE = 1001;
    public final static int STOP = 1002;
    public final static int HORIZONTAL_FLING = 1003;
    public final static int CHANGE_PAGE = 1004;
    public final static int VERTICAL_FLING = 1005;
    public final static int MOVE_WITH_PERCENT = 1006;
    public final static int VERTICAL_MOVE = 1007;

    // for coordination
    private float originX, originY;
    private float newX, newY, preX, preY;

    private float totalMoveY = 0.0f;
    private int totalMoveX = 0;

    private float lastMoveY = 0.0f;

    private boolean isAllowScroll = true;

    public ITimeRecycleViewGroup(Context context, int NUM_SHOW) {
        super(context);
        this.NUM_SHOW = NUM_SHOW;
        init();
    }

    public ITimeRecycleViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ITimeRecycleViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        for (int i = 0; i < NUM_SHOW + 2; i++) {
            AwesomeViewGroup awesomeViewGroup = new AwesomeViewGroup(getContext());
//            awesomeViewGroup.setBackgroundColor(colors[i]);
            awesomeViewGroup.setInRecycledViewIndex(i - 1);
            awesomeViewGroup.setLayoutParams(new AwesomeViewGroup.AwesomeLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            awesomeViewGroupList.add(awesomeViewGroup);
            addView(awesomeViewGroup);
        }

        ViewConfiguration vc = ViewConfiguration.get(getContext());
        mTouchSlop = vc.getScaledTouchSlop();
        mMaxVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
        mScroller = new Scroller(getContext());

    }


    private void moveXPostCheck(List<AwesomeViewGroup> awesomeViewGroups, int scrollDir) {
        int viewGroupSize = awesomeViewGroups.size();
        boolean pageChanged = false;
        if (Math.abs(offset) >= childWidth) {
            pageChanged = true;
        }

        if (pageChanged) {
            if (scrollDir == SCROLL_LEFT) {
                reDrawViewGroupToLast(awesomeViewGroups.get(0), awesomeViewGroups.get(viewGroupSize - 1));
                moveViewGroupToLast(awesomeViewGroups.get(0), awesomeViewGroups);
                if (adapter != null) {
                    adapter.notifyDataSetChanged(awesomeViewGroups.get(viewGroupSize - 1));
                }
            } else if (scrollDir == SCROLL_RIGHT) {
                reDrawViewGroupToFirst(awesomeViewGroups.get(awesomeViewGroups.size() - 1), awesomeViewGroups.get(0));
                moveViewGroupToFirst(awesomeViewGroups.get(awesomeViewGroups.size() - 1), awesomeViewGroups);
                if (adapter != null) {
                    adapter.notifyDataSetChanged(awesomeViewGroups.get(0));
                }
            }

            if (onScroll != null) {
                AwesomeViewGroup a = getFirstShownAwesomeViewGroup(awesomeViewGroupList);
                AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) a.getLayoutParams();
                onScroll.onPageSelected(getFirstShowItem());
            }
            if (offset != 0) {
                int sign = offset / Math.abs(offset);
                offset = Math.abs(offset) % childWidth * sign;
            }


        }
    }

    private void touchUpPostCheck() {
        int maxDis = viewWidth;
        int alreadyMoveDis = (int) (newX - originX);
        int[] scrollPos = ScrollHelper.calculateScrollDistance(mScroller, (int) mVelocityX,
                (int) mVelocityY, maxDis, alreadyMoveDis, childWidth);
        switch (scrollDir) {
            case SCROLL_LEFT:
            case SCROLL_RIGHT:
                setStatus(HORIZONTAL_FLING); // should fling or scroll to closet are both horizontal fling
                if (ScrollHelper.shouldFling(mVelocityX)) {
                    int distance = scrollPos[0];
                    if (scrollDir != 0) {
                        // sometimes, velocity direction is wrong, need to use scrollDir fix that
                        distance = Math.abs(distance) * (Math.abs(scrollDir) / scrollDir);
                    }
                    //float compensite, for inaccurate calculate
                    LogUtil.log("touchup", "already : " + alreadyMoveDis + " direction : " + scrollDir + " velo : " + mVelocityX);
                    distance += compensateDistance(distance, awesomeViewGroupList);
                    float scrollTime = ScrollHelper.calculateScrollTime(mVelocityX);
                    LogUtil.logFirstAwesome(awesomeViewGroupList);
                    scrollByXSmoothly(distance, (long) (scrollTime * 1000));
                } else {
                    scrollToClosestPosition(awesomeViewGroupList);
                    int distance = scrollPos[1];

                }
                break;

            case SCROLL_DOWN:
            case SCROLL_UP:
                if (ScrollHelper.shouldFling(mVelocityY)) {
                    int distance = scrollPos[1];
                    if (scrollDir != 0) {
                        // sometimes, velocity direction is wrong, need to use scrollDir fix that
                        distance = Math.abs(distance) * (Math.abs(scrollDir) / scrollDir);
                    }
                    distance = getInBoundY(distance);
//                    if (isFalseMove(distance, scrollDir)){
//                        if (!isBecauseReverseVelocity(lastMoveY)){
//                            Log.i("up", "touchUpPostCheck: distance" + distance + " , " + scrollDir + " lastMoveY : " + lastMoveY + " mTouchSlot : " + mTouchSlop);
//                            return;
//                        }
//                        distance = -distance;
//                    }

//                    Log.i("up", "touchUpPostCheck: " + "fling : " + distance + mVelocityTracker);
                    setStatus(VERTICAL_FLING);
                    scrollByYSmoothly(distance, 500);
                } else {
//                    Log.i("up", "touchUpPostCheck: " + "should not fling : " + mVelocityY);
                }
                break;
        }
    }

    // ensure y and dir are same
    private boolean isFalseMove(int y, int dir) {
        return y * dir < 0;
    }

    // if the fling dis is incorrect, fix this by direction
    private boolean isBecauseReverseVelocity(float lastMoveY) {
        return Math.abs(lastMoveY) < mTouchSlop;
    }

    private AwesomeViewGroup getFirstShownAwesomeViewGroup(List<AwesomeViewGroup> awesomeViewgroups) {
        return awesomeViewgroups.get(1);
    }

    private void scrollToClosestPosition(List<AwesomeViewGroup> awesomeViewGroups) {
        int dis = childNeedsScrollToNearestPosition(awesomeViewGroups);
        LogUtil.log("scrollToCloset : ", "distance : " + dis);
        scrollByXSmoothly(dis);
    }

    private int childNeedsScrollToNearestPosition(List<AwesomeViewGroup> awesomeViewGroups) {
        for (AwesomeViewGroup awesomeViewGroup : awesomeViewGroups) {
            if (awesomeViewGroup.isVisibleInParent() && awesomeViewGroup.isOutOfParent()) {
                AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroup.getLayoutParams();
                return Math.abs(lp.right) > Math.abs(lp.left) ? -lp.left : -lp.right;
            }
        }
        return 0;
    }

    /**
     * distance might be inaccurate, so before fling, distance need add compensate.
     *
     * @param distance
     * @param awesomeViewGroups
     * @return
     */
    private int compensateDistance(int distance, List<AwesomeViewGroup> awesomeViewGroups) {
        int min = 10000;
        int minValue = 0;
        for (AwesomeViewGroup awesomeViewGroup : awesomeViewGroups) {
            AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroup.getLayoutParams();
            if (Math.abs(lp.left + distance) < min) {
                min = Math.abs(lp.left + distance);
                minValue = lp.left + distance;
            }
        }
        return -minValue;
    }

    private void moveViewGroupToLast(AwesomeViewGroup awesomeViewGroup, List<AwesomeViewGroup> awesomeViewGroups) {
        awesomeViewGroups.remove(awesomeViewGroup);
        awesomeViewGroups.add(awesomeViewGroup);
        awesomeViewGroup.setInRecycledViewIndex(awesomeViewGroup.getInRecycledViewIndex() + NUM_SHOW + 2);
    }

    private void moveViewGroupToFirst(AwesomeViewGroup awesomeViewGroup, List<AwesomeViewGroup> awesomeViewGroups) {
        awesomeViewGroups.remove(awesomeViewGroup);
        awesomeViewGroups.add(0, awesomeViewGroup);
        awesomeViewGroup.setInRecycledViewIndex(awesomeViewGroup.getInRecycledViewIndex() - (NUM_SHOW + 2));
    }

    private void reDrawViewGroupToLast(AwesomeViewGroup toBeDrawViewGroup, AwesomeViewGroup lastAwesomeViewGroup) {
        AwesomeViewGroup.AwesomeLayoutParams lastLp = (AwesomeViewGroup.AwesomeLayoutParams) lastAwesomeViewGroup.getLayoutParams();
        int lastLpRight = lastLp.right;
        AwesomeViewGroup.AwesomeLayoutParams toBeDrawLp = (AwesomeViewGroup.AwesomeLayoutParams) toBeDrawViewGroup.getLayoutParams();
        toBeDrawLp.left = lastLpRight;
        toBeDrawLp.right = toBeDrawLp.left + toBeDrawLp.width;
    }

    private void reDrawViewGroupToFirst(AwesomeViewGroup toBeDrawViewGroup, AwesomeViewGroup firstAwesomeViewGroup) {
        AwesomeViewGroup.AwesomeLayoutParams firstLp = (AwesomeViewGroup.AwesomeLayoutParams) firstAwesomeViewGroup.getLayoutParams();
        int firstLpLeft = firstLp.left;
        AwesomeViewGroup.AwesomeLayoutParams toBeDrawLp = (AwesomeViewGroup.AwesomeLayoutParams) toBeDrawViewGroup.getLayoutParams();
        toBeDrawLp.right = firstLpLeft;
        toBeDrawLp.left = toBeDrawLp.right - toBeDrawLp.width;
    }

    private int offset = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);

        childWidth = viewWidth / NUM_SHOW;
        if (onSetting == null) {
            childHeight = 2 * viewHeight;
        } else {
            childHeight = onSetting.getItemHeight(heightMeasureSpec);
        }

        int childWidthSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
        int childHeightSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);

        measureChildren(childWidthSpec, childHeightSpec);

        for (int i = 0; i < awesomeViewGroupList.size(); i++) {

            AwesomeViewGroup awesomeViewGroup = awesomeViewGroupList.get(i);
            AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroup.getLayoutParams();

            lp.parentHeight = viewHeight;
            lp.width = childWidth;
            lp.height = childHeight;

            lp.left = (i - 1) * childWidth + offset;
            lp.right = lp.left + childWidth;
            lp.bottom = lp.top + lp.height;

            if (i == 0) {
                Log.i(TAG, "onMeasure: left : " + lp.left + " offset : " + offset);
            }
        }

//        moveXPostCheck(awesomeViewGroupList, scrollDir);
        setMeasuredDimension(viewWidth, viewHeight);

    }

    private boolean isTouchOutOfParent(MotionEvent ev) {
        if (ev.getX() > getWidth() || ev.getX() < 0) {
            LogUtil.logError("out");
            return true;
        }
        return false;
    }

    private void setStatus(int status) {
        this.status = status;
        LogUtil.log("setStatus", status + "");
    }

    private int getInBoundY(int y) {
        AwesomeViewGroup sampleAwesomeViewGroup = awesomeViewGroupList.get(0);
        AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) sampleAwesomeViewGroup.getLayoutParams();
        int realY = y;
        if (scrollDir == SCROLL_UP) {
            if (lp.bottom + y < lp.parentHeight) {
                // reach bottom, stop up
                realY = lp.parentHeight - lp.bottom;
            }
        } else if (scrollDir == SCROLL_DOWN) {
            if (lp.top + y > 0) {
                realY = 0 - lp.top;
            }
        }
        return realY;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(!isAllowScroll){
            return false;
        }
        int action = MotionEventCompat.getActionMasked(ev);
        if (isTouchOutOfParent(ev)) {
            return true;
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                LogUtil.log("paul", "onInterceptTouchEvent Down");
                if (status == HORIZONTAL_FLING) {
                    scrollOverTouchSlop = true;
                    scrollModel = SCROLL_HORIZONTAL;
                    return true;
                }

                if (status == VERTICAL_FLING) {
                    scrollOverTouchSlop = true;
                    scrollModel = SCROLL_VERTICAL;
                }
                setStatus(START);
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    mVelocityTracker.clear();
                }
                mVelocityX = 0;
                mVelocityY = 0;

                originX = ev.getX();
                originY = ev.getY();
                preX = ev.getX();
                preY = ev.getY();
                mVelocityTracker.addMovement(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                newX = ev.getX();
                newY = ev.getY();
                if (!scrollOverTouchSlop) {
                    if (mTouchSlop <= Math.abs(newX - preX)) {
                        setStatus(HORIZONTAL_MOVE);
                        scrollOverTouchSlop = true;
                        scrollModel = SCROLL_HORIZONTAL;
                    } else if (mTouchSlop <= Math.abs(newY - preY)) {
                        setStatus(VERTICAL_MOVE);
                        scrollOverTouchSlop = true;
                        scrollModel = SCROLL_VERTICAL;
                    }
                }

                if (scrollOverTouchSlop) {
                    if (scrollModel == SCROLL_HORIZONTAL) {
                        setStatus(HORIZONTAL_MOVE);
                        int moveX = (int) (newX - preX);

                        if (moveX > 0) {
                            scrollDir = SCROLL_RIGHT;
                        } else if (moveX < 0) {
                            scrollDir = SCROLL_LEFT;
                        }
                        scrollByX(moveX);
                        preX = newX;
                    } else if (scrollModel == SCROLL_VERTICAL) {
                        setStatus(VERTICAL_MOVE);
                        int moveY = (int) newY - (int) preY;
                        lastMoveY = moveY;

                        if (moveY > 0) {
                            scrollDir = SCROLL_DOWN;
                        } else if (moveY < 0) {
                            scrollDir = SCROLL_UP;
                        }
                        scrollByY(moveY);
                        preY = newY;
                    }
                    mVelocityTracker.addMovement(ev);
                    mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                    mVelocityX = mVelocityTracker.getXVelocity();
                    mVelocityY = mVelocityTracker.getYVelocity();
                    LogUtil.log("paul","OnInterceptEvent Move  true");
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
                // when fling, and touch on this, child might consume this event
                // so also need to action up on the Intercepted event.
                newX = getEventXFilterOutside(ev);
                newY = ev.getY();

                if (scrollOverTouchSlop) {
                    touchUpPostCheck();
                }
                scrollOverTouchSlop = false;
                preX = newX;
                preY = newY;
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isAllowScroll){
            return false;
        }

        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (status == HORIZONTAL_FLING) {
                    scrollOverTouchSlop = true;
                    scrollModel = SCROLL_HORIZONTAL;
                }

                if (status == VERTICAL_FLING) {
                    scrollOverTouchSlop = true;
                    scrollModel = SCROLL_VERTICAL;
                }
                setStatus(START);
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    mVelocityTracker.clear();
                }
                mVelocityX = 0;
                mVelocityY = 0;

                originX = event.getX();
                originY = event.getY();
                preX = event.getX();
                preY = event.getY();
                super.onTouchEvent(event);
                mVelocityTracker.addMovement(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                if (isTouchOutOfParent(event)) {
                    return true;
                }
                newX = event.getX();
                newY = event.getY();
                if (!scrollOverTouchSlop) {
                    if (mTouchSlop <= Math.abs(newX - preX)) {
                        setStatus(HORIZONTAL_MOVE);
                        scrollOverTouchSlop = true;
                        scrollModel = SCROLL_HORIZONTAL;
                    } else if (mTouchSlop <= Math.abs(newY - preY)) {
                        setStatus(VERTICAL_MOVE);
                        scrollOverTouchSlop = true;
                        scrollModel = SCROLL_VERTICAL;
                    }
                }

                if (scrollOverTouchSlop) {
                    if (scrollModel == SCROLL_HORIZONTAL) {
                        setStatus(HORIZONTAL_MOVE);
                        int moveX = (int) (newX - preX);
                        if (moveX > 0) {
                            scrollDir = SCROLL_RIGHT;
                        } else if (moveX < 0) {

                            scrollDir = SCROLL_LEFT;
                        }

                        scrollByX(moveX);
                        preX = newX;
                    } else if (scrollModel == SCROLL_VERTICAL) {
                        setStatus(VERTICAL_MOVE);
                        float moveY = newY - preY;
                        lastMoveY = moveY;

                        if (moveY > 0) {
                            scrollDir = SCROLL_DOWN;
                        } else if (moveY < 0) {
                            scrollDir = SCROLL_UP;
                        }
                        scrollByY((int) moveY);
                        preY = newY;
                    }
                    mVelocityTracker.addMovement(event);
                    mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                    mVelocityX = mVelocityTracker.getXVelocity();
                    mVelocityY = mVelocityTracker.getYVelocity();

                }
                LogUtil.log("paul", "onTouchEvent Move true");
//                return super.onTouchEvent(event);
                return true; /// paul add
            case MotionEvent.ACTION_UP:
                newX = getEventXFilterOutside(event);
                newY = event.getY();

                if (scrollOverTouchSlop) {
//                    LogUtil.log("velocityX", " : " + mVelocityX + " dir : " + scrollDir );
                    touchUpPostCheck();
                }

                scrollOverTouchSlop = false;
                preX = newX;
                preY = newY;
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_OUTSIDE:
                break;

        }

        return super.onTouchEvent(event);
    }

    private float getEventXFilterOutside(MotionEvent event) {
        if (event.getX() < 0) {
            return 0;
        }
        if (event.getX() > viewWidth) {
            return viewWidth;
        }
        return event.getX();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int childCount = awesomeViewGroupList.size();
        for (int i = 0; i < childCount; i++) {
            AwesomeViewGroup child = awesomeViewGroupList.get(i);
            AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) child.getLayoutParams();
            child.layout(lp.left, lp.top, lp.right, lp.bottom);
        }
    }


    /**
     * after test, if has a maxmove condition, the head wont match body if scroll very fast
     *
     * @param x
     */
    public void followScrollByX(int x) {
//        if (this.getVisibility() != GONE && Math.abs(x) >= 5 * childWidth){
//            // this is page jump, then refresh page
//            // this should never be called when human scroll
//            int pageScroll = -x/childWidth;
//            updateIndexes(pageScroll);
//            LogUtil.log("follow ", x + " , " + childWidth);
//
//            if (adapter!=null){
//                adapter.notifyDataSetChanged();
//            }
//            return;
//        }
        scrollByX(x);
    }

    @Override
    public void scrollByX(int x) {

        if (x > 0) {
            scrollDir = SCROLL_RIGHT;
        } else if (x < 0) {
            scrollDir = SCROLL_LEFT;
        }

//        for (AwesomeViewGroup awesomeViewGroup: awesomeViewGroupList){
//            AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroup.getLayoutParams();
//            lp.left += x;
//            lp.right += x;
//            awesomeViewGroup.requestLayout();
//        }

        if (onScroll != null) {
            onScroll.onHorizontalScroll(x, totalMoveX);
        }

        totalMoveX += x;
        offset += x;

        for (int i = 0; i < awesomeViewGroupList.size(); i++) {

            AwesomeViewGroup awesomeViewGroup = awesomeViewGroupList.get(i);
            AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroup.getLayoutParams();
            lp.left = (i - 1) * childWidth + offset;
            lp.right = lp.left + childWidth;
            lp.bottom = lp.top + lp.height;
            awesomeViewGroup.requestLayout();
        }

        moveXPostCheck(awesomeViewGroupList, scrollDir);
//        requestLayout();
    }

    @Override
    public void scrollByY(int y) {
        // precheck the validation of y
        if (y > 0) {
            scrollDir = SCROLL_DOWN;
        } else if (y < 0) {
            scrollDir = SCROLL_UP;
        }
        LogUtil.log("aaa before: ", y + "");
        y = getInBoundY(y);
        if (y == 0) {
            return;
        }

        for (AwesomeViewGroup awesomeViewGroup : awesomeViewGroupList) {
            AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) awesomeViewGroup.getLayoutParams();
            lp.top += y;
            lp.bottom += y;
            awesomeViewGroup.layout(lp.left, lp.top, lp.right, lp.bottom);
//            requestLayout();
        }


        if (onScroll != null) {
            Log.i("onScroll", "scrollByY1: " + y + " totalMove: " + totalMoveY + " dir : " + scrollDir);
//            LogUtil.logAwesomes(awesomeViewGroupList);
            onScroll.onVerticalScroll(y, (int) totalMoveY);
        }

        totalMoveY += y;
    }

    @Override
    public void scrollByXSmoothly(int x) {
        scrollByXSmoothly(x, 200);
    }

    public void scrollByXSmoothly(int x, long duration, @Nullable Animator.AnimatorListener animatorListener) {

        setStatus(HORIZONTAL_FLING);
        ValueAnimator animator = ValueAnimator.ofInt(0, x);
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        if (animatorListener != null) {
            animator.addListener(animatorListener);
        }
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            int preAniX = 0;
            int totalScroll = 0;
            boolean abortRest = false;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (status != HORIZONTAL_FLING) {
                    // should abort animation...
                    abortRest = true;
                    return;
                }
                if (abortRest) {
                    animation.removeAllListeners();
                    return;
                }
                int nowValue = (int) animation.getAnimatedValue();
                int offset = (nowValue - preAniX);
                newX = preX + offset;
                if (offset < 0) {
                    Log.i("", "onAnimationUpdate: ");
                }
                scrollByX(offset);
                preAniX = nowValue;
                preX = newX;
                totalScroll += offset;
                LogUtil.logError("totalScroll : " + totalScroll);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setStatus(STOP);
                // when smoothly scroll end, page must changed, so need to call on page selected interface
                if (onScroll != null) {
                    AwesomeViewGroup a = getFirstShownAwesomeViewGroup(awesomeViewGroupList);
                    AwesomeViewGroup.AwesomeLayoutParams lp = (AwesomeViewGroup.AwesomeLayoutParams) a.getLayoutParams();
                    onScroll.onPageSelected(getFirstShowItem());
                }

                LogUtil.logAwesomes(awesomeViewGroupList);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    public float getAwesomeScrollY() {
        return totalMoveY;
    }


    public int getAwesomeScrollX() {
        return totalMoveX;
    }

    @Override
    public void scrollByXSmoothly(int x, long duration) {
        scrollByXSmoothly(x, duration, null);
    }

    @Override
    public void scrollByYSmoothly(int y, long duration) {
        setStatus(VERTICAL_FLING);
        ValueAnimator animator = ValueAnimator.ofInt(0, y);
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            int preAniY = 0;
            int totalScroll = 0;
            boolean abortRest = false;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (status != VERTICAL_FLING) {
                    abortRest = true;
                }
                if (abortRest) {
                    animation.removeAllListeners();
                    return;
                }
                int nowValue = (int) animation.getAnimatedValue();
                int offset = (nowValue - preAniY);
                newY = preY + offset;
                if (offset != 0) {
                    Log.i("ani", "onAnimationUpdate: " + offset);
                }
                scrollByY(offset);
                preAniY = nowValue;
                preY = newY;

                totalScroll += offset;
                LogUtil.log("vertical fling ", totalScroll + "");
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setStatus(STOP);
//                LogUtil.logAwesomes(awesomeViewGroupList);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    private void updateIndexes(int indexOffset) {
        for (AwesomeViewGroup awesomeViewGroup : awesomeViewGroupList) {
            awesomeViewGroup.setInRecycledViewIndex(awesomeViewGroup.getInRecycledViewIndex() + indexOffset);
        }
    }

    @Override
    public void scrollByYSmoothly(int y) {
        scrollByYSmoothly(y, 200);
    }

    private OnScroll onScroll;

    public void setOnScrollListener(OnScroll onScroll) {
        this.onScroll = onScroll;
    }

    public interface OnSetting {
        int getItemHeight(int heightSpec);
    }

    private OnSetting onSetting;

    public void setOnSetting(OnSetting onSetting) {
        this.onSetting = onSetting;
    }

    public interface OnScroll<V extends View> {
        void onPageSelected(V view);

        void onHorizontalScroll(int dx, int preOffsetX);

        void onVerticalScroll(int dy, int preOffsetY);
    }

    private ITimeAdapter adapter;

    public void setAdapter(ITimeAdapter adapter) {
        this.adapter = adapter;
        adapter.setAwesomeViewGroups(awesomeViewGroupList);
        adapter.onCreateViewHolders();
    }

    public void smoothMoveWithOffsetX(int moveOffset, @Nullable Animator.AnimatorListener animatorListener) {
        if (moveOffset == 0) {
            return;
        }
        int distance = (-1) * moveOffset * childWidth;
        scrollByXSmoothly(distance, 200, animatorListener);
    }

    /**
     * @param moveOffset Number of cells should to be moved. Positive value means r
     */
    public void moveWithOffsetX(int moveOffset) {
        if (moveOffset == 0) {
            return;
        }
        int distance = (-1) * moveOffset * childWidth;

        if (Math.abs(distance) >= childWidth) {
            // this is page jump, then refresh page
            int pageScroll = moveOffset;
            updateIndexes(pageScroll);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            return;
        }

        scrollByX(distance);
    }

    public View getFirstShowItem() {
        return getFirstShownAwesomeViewGroup(awesomeViewGroupList).getItem();
    }

    public boolean isAllowScroll() {
        return isAllowScroll;
    }

    public void setAllowScroll(boolean allowScroll) {
        isAllowScroll = allowScroll;
    }
}
