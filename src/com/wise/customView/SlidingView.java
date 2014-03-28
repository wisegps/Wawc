package com.wise.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;
/**
 * 滑动删除控件
 * @author honesty
 */
public class SlidingView extends ViewGroup{
    /**
     * 滑动控件
     */
    Scroller mScroller;
    /**
     * 滑动速度对象
     */
    VelocityTracker velocityTracker;
    private int mTouchSlop;
    /**
     * 右边控件宽度
     */
    private int rightWidth = 200;
    /**
     * 滑动速度
     */
    private static final int SNAP_VELOCITY = 300;

    public SlidingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for(int i = 0 ; i < count; i++){
            if(i == 0){
                getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);//设置每个view的大小
            }else{
                getChildAt(i).measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            }
        }
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);  
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = 0;
        int width = 0;
        for(int i = 0 ; i < getChildCount() ; i++){
            View childView = getChildAt(i);
            width = childView.getMeasuredWidth();
            childView.layout(left, 0, left+width, childView.getMeasuredHeight());
            left += width;
        }
        rightWidth = width;
    }
    private float mLastMotionX;
    /**
     * 返回true，触摸事件被消费，不会传给子控件
     * 子控件有点击事件会触发move，up事件，否者只会触发down事件
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mLastMotionX = x;
            break;

        case MotionEvent.ACTION_MOVE:
            int xDiff = (int) Math.abs(x - mLastMotionX);//水平方向移动绝对值
            boolean xMove = xDiff > mTouchSlop; //判断水平方向是否移动
            if(xMove){
                return true;
            }
            break;
        }
        return false;
    }

    float lastX = 0;
    float lastY = 0;
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //System.out.println("onTouchEvent = " + event.getAction());
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            addVelocityTracker(event);
            lastX = x;
            lastY = y;
            break;

        case MotionEvent.ACTION_MOVE:
            int xDiff = (int) Math.abs(x - lastX);//水平方向移动绝对值
            int yDiff = (int) Math.abs(y - lastY);//垂直方向移动绝对值
            if(yDiff > xDiff){//list滑动
                //System.out.println("list滑动");
            }else{//水平滑动
                //System.out.println("水平滑动");
                getParent().requestDisallowInterceptTouchEvent(true);
                addVelocityTracker(event);
                int deltax = (int) (lastX - x);
                if(deltax < 0){//向右
                    if(getScrollX() > 0){
                        int ScrollX = - getScrollX();
                        scrollBy(deltax > ScrollX ? deltax : ScrollX, 0);
                    }
                }else{//向左
                    if(getScrollX() < rightWidth){
                        int ScrollX = rightWidth - getScrollX();
                        scrollBy(deltax > ScrollX ? ScrollX : deltax, 0);
                    }
                }
                lastX = x;
            }
            break;
        case MotionEvent.ACTION_UP:
            eventCancel();
            break;
        case MotionEvent.ACTION_CANCEL:
            eventCancel();
            break;
        }
        return true;
    }
    private void eventCancel(){
        getParent().requestDisallowInterceptTouchEvent(false);
        int velocityX = getScrollVelocity();
        if(velocityX > SNAP_VELOCITY){//向右划
            scrollRight();
        }else if(velocityX < - SNAP_VELOCITY){//向左划
            scrollLeft();
        }else{
            scrollByDistanceX();
        }
        recycleVelocityTracker();
    }
    /**
     * 向右划
     */
    private void scrollRight(){
        mScroller.startScroll(getScrollX(), 0, -getScrollX(), 0, 250);
        postInvalidate();
    }
    /**
     * 向左划
     */
    private void scrollLeft(){
        mScroller.startScroll(getScrollX(), 0, rightWidth - getScrollX() , 0, 250);
        postInvalidate();
    }
    /**
     * 根据滑动距离判断滚动位置
     */
    private void scrollByDistanceX(){
        if(getScrollX() >= rightWidth/2){
            scrollLeft();
        }else{
            scrollRight();
        }
    }
    /**
     * 添加速度跟踪
     * @param ev
     */
    private void addVelocityTracker(MotionEvent ev){
        if(velocityTracker == null){
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(ev);
    }
    /**
     * 移除速度跟踪
     */
    private void recycleVelocityTracker(){
        if(velocityTracker != null){
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }
    /**
     * 获取滑动速度
     * @return
     */
    private int getScrollVelocity(){
        if(velocityTracker == null){
            return 0;
        }else{
            velocityTracker.computeCurrentVelocity(1000);
            int velocity = (int) velocityTracker.getXVelocity();
            return velocity;
        }
    }
    
    @Override
    public void computeScroll() {//控制松手后的滑动
        super.computeScroll();
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }
    /**
     * 恢复原状
     */
    public void ScorllRest(){
        scrollRight();
    }
    public void ScorllRestFast(){
        scrollTo(0, 0);
    }
}