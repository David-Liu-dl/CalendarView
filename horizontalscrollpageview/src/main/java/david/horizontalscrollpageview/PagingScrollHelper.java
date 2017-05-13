package david.horizontalscrollpageview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;

/**
 * 实现RecycleView分页滚动的工具类
 * Created by zhuguohui on 2016/11/10.
 */

public class PagingScrollHelper {

    private static final String TAG = "itime";
    RecyclerView mRecyclerView = null;
    private MyOnScrollListener mOnScrollListener = new MyOnScrollListener();

    private MyOnFlingListener mOnFlingListener = new MyOnFlingListener();
    private int offsetY = 0;
    //手指滑动时时位置
    private int offsetX = 0;
    private VelocityTracker mVelocityTracker;//生命变量
    private enum MOVE_TYPE{
        STAY, PAGE, PART
    }

    int startY = 0;
    int startX = 0;
    int nowPst = 0;
    float globalVelocityX = 0;

    private int columns = 3;


    enum ORIENTATION {
        HORIZONTAL, VERTICAL, NULL
    }

    ORIENTATION mOrientation = ORIENTATION.HORIZONTAL;

    public PagingScrollHelper(int columns) {
        this.columns = columns;
    }

    public void setUpRecycleView(RecyclerView recycleView) {
        if (recycleView == null) {
            throw new IllegalArgumentException("recycleView must be not null");
        }
        mRecyclerView = recycleView;
        //处理滑动
        recycleView.setOnFlingListener(mOnFlingListener);
//        设置滚动监听，记录滚动的状态，和总的偏移量
        recycleView.addOnScrollListener(mOnScrollListener);
        //记录滚动开始的位置
        recycleView.setOnTouchListener(mOnTouchListener);
        //获取滚动的方向
        updateLayoutManger();
    }

    public void updateLayoutManger() {
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager != null) {
            if (layoutManager.canScrollVertically()) {
                mOrientation = ORIENTATION.VERTICAL;
            } else if (layoutManager.canScrollHorizontally()) {
                mOrientation = ORIENTATION.HORIZONTAL;
            } else {
                mOrientation = ORIENTATION.NULL;
            }
            if (mAnimator != null) {
                mAnimator.cancel();
            }
            startX = 0;
            startY = 0;
            offsetX = 0;
            offsetY = 0;

        }

    }

    ValueAnimator mAnimator = null;

    public class MyOnFlingListener extends RecyclerView.OnFlingListener {
        MOVE_TYPE moveType = MOVE_TYPE.STAY;

        public void setMovePart(MOVE_TYPE moveType){
            this.moveType = moveType;
        }

        @Override
        public boolean onFling(int velocityX, int velocityY) {
            if (mOrientation == ORIENTATION.NULL) {
                return false;
            }
            //获取开始滚动时所在页面的index
            int p = nowPst;
            //记录滚动开始和结束的位置
            int endPoint = 0;
            int startPoint = 0;

            //如果是垂直方向
            if (mOrientation == ORIENTATION.VERTICAL) {
                startPoint = offsetY;

                if (velocityY < 0) {
                    p--;
                } else if (velocityY > 0) {
                    p++;
                }
                //更具不同的速度判断需要滚动的方向
                //注意，此处有一个技巧，就是当速度为0的时候就滚动会开始的页面，即实现页面复位
                endPoint = p * mRecyclerView.getHeight();

            } else {
                int pstOffset = 0;

                switch (moveType){
                    case STAY:
                        pstOffset = 0;
                        break;
                    case PAGE:
                        pstOffset = 3;
                        break;
                    case PART:
                        pstOffset = 1;
                        break;
                }

                startPoint = offsetX;

                if (velocityX < 0) {
                    p -=  pstOffset;
                } else if (velocityX > 0) {
                    p +=  pstOffset;
                }

                endPoint = (int) (p * (mRecyclerView.getWidth()/(float)columns));
            }

            if (endPoint < 0) {
                endPoint = 0;
            }
            nowPst = p;
            startX = endPoint;

            final int distance = endPoint - startPoint;
            final float globalVelocity = getLocalVelocity(globalVelocityX, 2f, 5);
            final float vX = distance * globalVelocity >= 0 ? globalVelocity : -globalVelocity;
            final float acc = getAccelerateValue(distance, vX);
            int duration = (int) getMoveDuration(acc, vX);
            final int startPst = startPoint;

            if (mAnimator != null){
                mAnimator.cancel();
            }

            //使用动画处理滚动
            mAnimator = new ValueAnimator().ofInt(0, duration);
            mAnimator.setDuration(duration);
            mAnimator.setInterpolator(new LinearInterpolator());
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int nowPoint = (int) animation.getAnimatedValue();

                    if (mOrientation == ORIENTATION.VERTICAL) {
                        int dy = nowPoint - offsetY;
                        //这里通过RecyclerView的scrollBy方法实现滚动。
                        mRecyclerView.scrollBy(0, dy);
                    } else {
                        int toDistance = (int) getNowDistanceFromAccelerator(startPst, vX, nowPoint);
                        int dx = toDistance - offsetX;
                        mRecyclerView.scrollBy(dx,0);

                    }
                }
            });
            mAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    //回调监听
                    if (null != mOnPageChangeListener) {
                        mOnPageChangeListener.onPageChange(getPageIndex());
                    }
                }
            });
            mAnimator.start();

            return true;
        }
    }

    public class MyOnScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            //滚动结束记录滚动的偏移量
            offsetY += dy;
            offsetX += dx;
        }
    }

    private MyOnTouchListener mOnTouchListener = new MyOnTouchListener();


    public class MyOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //get velocityX
            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain();//获得VelocityTracker类实例
            }
            mVelocityTracker.addMovement(event);//将事件加入到VelocityTracker类实例中
            //判断当ev事件是MotionEvent.ACTION_UP时：计算速率
            final VelocityTracker velocityTracker = mVelocityTracker;
            // 1000 provides pixels per second
            velocityTracker.computeCurrentVelocity(1, (float)0.01); //设置maxVelocity值为0.1时，速率大于0.01时，显示的速率都是0.01,速率小于0.01时，显示正常
            velocityTracker.computeCurrentVelocity(1); //设置units的值为1000，意思为一秒时间内运动了多少个像素

            //手指按下的时候记录开始滚动的坐标
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                startY = offsetY;
                startX = offsetX;
            }else if (event.getAction() == MotionEvent.ACTION_UP){
//                Log.i("test","velocityTraker"+velocityTracker.getXVelocity());
                globalVelocityX = velocityTracker.getXVelocity();
                handleFling();
                return true;
            }

            return false;
        }
    }

    private void handleFling(){
        if (mOrientation != ORIENTATION.NULL) {
            MOVE_TYPE moveType;

            int vX = 0, vY = 0;
            int columnW = mRecyclerView.getWidth()/ columns;
            if (mOrientation == ORIENTATION.VERTICAL) {
                int absY = Math.abs(offsetY - startY);
                //如果滑动的距离超过屏幕的一半表示需要滑动到下一页
                moveType = absY > mRecyclerView.getHeight() / 2 ? MOVE_TYPE.PAGE : MOVE_TYPE.STAY;
                vY = 0;

                if (moveType == MOVE_TYPE.PAGE) {
                    vY = offsetY - startY < 0 ? -1000 : 1000;
                }

            } else {
                int absX = Math.abs(offsetX - startX);

                if (absX < columnW/2){
                    moveType = MOVE_TYPE.STAY;

                }else if (absX >= columnW * 0.3f && absX < mRecyclerView.getWidth()/2){
                    moveType = MOVE_TYPE.PART;
                }else {
                    moveType = MOVE_TYPE.PAGE;
                }

                if (moveType == MOVE_TYPE.PART){
                    vX = offsetX - startX < 0 ? -columnW : columnW;
                }else if(moveType == MOVE_TYPE.PAGE){
                    vX = offsetX - startX < 0 ? -mRecyclerView.getWidth() : mRecyclerView.getWidth();
                }

            }
            mOnFlingListener.setMovePart(moveType);
            mOnFlingListener.onFling(vX, vY);
        }
    }

    public int getPageIndex() {
        int position = 0;
        if (mOrientation == ORIENTATION.VERTICAL) {
            position = offsetY / mRecyclerView.getHeight();
        } else {
            position = nowPst;
        }
        return position;
    }

    private int getStartPageIndex() {
        int position = 0;
        if (mOrientation == ORIENTATION.VERTICAL) {
            position = startY / mRecyclerView.getHeight();
        } else {
            position = startX / (mRecyclerView.getWidth() / columns);
        }
        return position;
    }

    onPageChangeListener mOnPageChangeListener;

    public void setOnPageChangeListener(onPageChangeListener listener) {
        mOnPageChangeListener = listener;
    }

    public interface onPageChangeListener {
        void onPageChange(int index);
    }

    public void scrollToPosition(final int position){
        if (position >= 0){
            if (mRecyclerView.getWidth() == 0){
                mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        scrollToPosition(position);
                        // Once data has been obtained, this listener is no longer needed, so remove it...
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        else {
                            mRecyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                    }
                });
                return;
            }

            double cell_width = (float)mRecyclerView.getWidth()/(float)columns;
            int offset = position - nowPst;
            int disOffset = (int) (offset*cell_width);
            nowPst = position;
            startX += disOffset;
            mRecyclerView.scrollBy(disOffset, 0);

        }
    }


    private float getAccelerateValue(float distance, float velocity){
        return  - (float)Math.pow(velocity,2.0)/(2*distance);
    }

    private float getMoveDuration(float acc, float velocity){
        return -velocity/acc;
    }

    private float getNowDistanceFromAccelerator(float startPoint, float velocity, float nowTime){
        float result = startPoint + 0.5f * velocity * nowTime;
        return result;
    }

    private float getLocalVelocity(float globalVelocity, float min, float max){
        int symbol = globalVelocity >= 0 ? 1 : -1;
        float absV = Math.abs(globalVelocity);
        float absMin = Math.abs(min);
        float absMax = Math.abs(max);
        if (absV > absMax){
            return absMax * symbol;
        }else if (absV < absMin){
            return absMin * symbol;
        }else {
            return globalVelocity;
        }
    }
}
