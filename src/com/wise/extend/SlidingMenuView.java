package com.wise.extend;

import com.wise.wawc.R;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.MeasureSpec;
import android.widget.Scroller;

/**
 * 滑动菜单
 * @author honesty
 */
public class SlidingMenuView extends ViewGroup{
    private static final int INVALID_SCREEN = -1;
    private static final int SNAP_VELOCITY = 600;
    private int mDefaultScreen = 1;
    /**
     * 当前显示屏幕
     */
    private int mCurrentScreen;
    private int mNextScreen = INVALID_SCREEN;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;


    private float mLastMotionX;
    private float mLastMotionY;

    private final static int TOUCH_STATE_REST = 0;
    private final static int TOUCH_STATE_SCROLLING = 1;
    /**
     * 当前Touch状态，停止or移动
     */
    public int mTouchState = TOUCH_STATE_REST;


    private boolean mAllowLongPress;

    private int mTouchSlop;
    int rightWidth = 0;
    int totalWidth = 0;
    OnViewTouchMoveListener onViewTouchMoveListener;
    int width;
    public SlidingMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        // 获取屏幕宽度
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        width = Even((int) (wm.getDefaultDisplay().getWidth() * 0.8));// 屏幕宽度
    }
    public SlidingMenuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWorkspace();
        postDelayed(new Runnable(){
			@Override
			public void run() {
				scrollTo(findViewById(R.id.left_sliding_tab).getWidth(), 0);
			}
        }, 50);
    }
    private void initWorkspace() {
        mScroller = new Scroller(getContext());
        mCurrentScreen = mDefaultScreen;   
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }
    boolean isDefaultScreenShowing() {
        return mCurrentScreen == mDefaultScreen;
    }
    public int getCurrentScreen() {
        return mCurrentScreen;
    }    
    public void setCurrentScreen(int currentScreen) {
        mCurrentScreen = Math.max(0, Math.min(currentScreen, getChildCount() - 1));        
        invalidate();
    }
    void showDefaultScreen() {
        setCurrentScreen(mDefaultScreen);
    }

    @Override
    public void computeScroll() {
    	if (mScroller.computeScrollOffset()) {
        	scrollTo(mScroller.getCurrX(), mScroller.getCurrY()); 
        } else if (mNextScreen != INVALID_SCREEN) {
            mCurrentScreen = Math.max(0, Math.min(mNextScreen, getChildCount() - 1));
            mNextScreen = INVALID_SCREEN;
            clearChildrenCache();
        }
    }
    @Override
	public void scrollTo(int x, int y) {
    	super.scrollTo(x, y);
		postInvalidate();
	}

	@Override
    protected void dispatchDraw(Canvas canvas) {
		final int scrollX = getScrollX();
		super.dispatchDraw(canvas);
		canvas.translate(scrollX, 0);
    }	
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureViews(widthMeasureSpec, heightMeasureSpec);        
    }	
	public void measureViews(int widthMeasureSpec, int heightMeasureSpec){
        int width_v1 = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
	    View v1 = findViewById(R.id.left_sliding_tab);
		v1.measure(width_v1, heightMeasureSpec);
		View v2 = findViewById(R.id.sliding_body);
	    v2.measure(widthMeasureSpec, heightMeasureSpec);
	    View v3 = findViewById(R.id.ll_right);
        v3.measure(widthMeasureSpec, heightMeasureSpec);
	}
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        rightWidth = getChildAt(0).getMeasuredWidth();
    	int childLeft = 0;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                final int childWidth = child.getMeasuredWidth();
                child.layout(childLeft, 0, childLeft + childWidth, child.getMeasuredHeight());
                childLeft += childWidth;
            }
        }
        totalWidth = childLeft;
        if(onViewTouchMoveListener != null){
            onViewTouchMoveListener.OnViewLoad(totalWidth, getChildAt(0).getMeasuredWidth());
        }
    }

	@Override
    public boolean dispatchUnhandledMove(View focused, int direction) {
		if (direction == View.FOCUS_LEFT) {
            if (getCurrentScreen() > 0) {
                snapToScreen(getCurrentScreen() - 1);
                return true;
            }
        } else if (direction == View.FOCUS_RIGHT) {
            if (getCurrentScreen() < getChildCount() - 1) {
                snapToScreen(getCurrentScreen() + 1);
                return true;
            }
        }
        return super.dispatchUnhandledMove(focused, direction);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    	
        final int action = ev.getAction();
        
        if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {
            return true;
        }

        final float x = ev.getX();
        final float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_MOVE:
             
                final int xDiff = (int) Math.abs(x - mLastMotionX);
                final int yDiff = (int) Math.abs(y - mLastMotionY);

                final int touchSlop = mTouchSlop;
                boolean xMoved = xDiff > touchSlop;
                boolean yMoved = yDiff > touchSlop;
                
                if (xMoved || yMoved) {
                    
                    if (xMoved) {
                        // Scroll if the user moved far enough along the X axis
                        mTouchState = TOUCH_STATE_SCROLLING;
                        enableChildrenCache();
                    }
                    // Either way, cancel any pending longpress
                    if (mAllowLongPress) {
                        mAllowLongPress = false;
                        // Try canceling the long press. It could also have been scheduled
                        // by a distant descendant, so use the mAllowLongPress flag to block
                        // everything
                        final View currentScreen = getChildAt(mCurrentScreen);
                        currentScreen.cancelLongPress();
                    }
                }
                break;

            case MotionEvent.ACTION_DOWN:
                // Remember location of down touch
                mLastMotionX = x;
                mLastMotionY = y;
                mAllowLongPress = true;
  
                mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
               
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // Release the drag
                clearChildrenCache();
                mTouchState = TOUCH_STATE_REST;
                mAllowLongPress = false;
                break;
        }

        /*
         * The only time we want to intercept motion events is if we are in the
         * drag mode.
         */
        return false;
        //return mTouchState != TOUCH_STATE_REST;
    }

    void enableChildrenCache() {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View layout = (View) getChildAt(i);
            layout.setDrawingCacheEnabled(true);
        }
    }

    void clearChildrenCache() {
    	 final int count = getChildCount();
         for (int i = 0; i < count; i++) {
             final View layout = (View) getChildAt(i);
             layout.setDrawingCacheEnabled(false);
         }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        final int action = ev.getAction();
        final float x = ev.getX();
	        switch (action) {
	        case MotionEvent.ACTION_DOWN:
	            /*
	             * If being flinged and user touches, stop the fling. isFinished
	             * will be false if being flinged.
	             */
	            if (!mScroller.isFinished()) {
	                mScroller.abortAnimation();
	            }
	
	            // Remember where the motion event started
	            mLastMotionX = x;
	            break;
	        case MotionEvent.ACTION_MOVE:
				if (mTouchState == TOUCH_STATE_SCROLLING) {// TODO 考虑滑动过了的情况
					int deltaX = (int) (mLastMotionX - x);
					mLastMotionX = x;
					deltaX = Even(deltaX);
					if (deltaX < 0) {// 向右滑
						if (getScrollX() > 0) {// 滚动的距离
						    if(getScrollX() > rightWidth){
						        
						    }else{
						        int scrollX = Math.max(-getScrollX(), deltaX);
	                            scrollBy(scrollX , 0);
	                            if(onViewTouchMoveListener != null){
	                                onViewTouchMoveListener.OnViewMove(getScrollX());
	                            }
						    }						    
						}
					} else if (deltaX > 0) {
					    if(getScrollX() >= rightWidth){
	                        
	                    }else{
	                        final int availableToScroll = getChildAt(
	                                getChildCount() - 1).getRight()
	                                - getScrollX() - getWidth();
	                        if (availableToScroll > 0) {
	                            int scrollX = Math.min(availableToScroll, deltaX);
	                            scrollBy(scrollX , 0);
	                            if(onViewTouchMoveListener != null){
	                                onViewTouchMoveListener.OnViewMove(getScrollX());
	                            }
	                        }
	                    }						
					}
				}
	            break;
	        case MotionEvent.ACTION_UP:
	            if (mTouchState == TOUCH_STATE_SCROLLING) {
	                final VelocityTracker velocityTracker = mVelocityTracker;
	                velocityTracker.computeCurrentVelocity(1000);
	                int velocityX = (int) velocityTracker.getXVelocity();
	
	                if (velocityX > SNAP_VELOCITY && mCurrentScreen > 0) {//向左滑                
	                    if(mCurrentScreen == 2){
                            
                        }else{                            
                            snapToScreen(mCurrentScreen - 1);
                        }
	                } else if (velocityX < -SNAP_VELOCITY && mCurrentScreen < getChildCount() - 1) {
	                    if((mCurrentScreen + 1) == 2){
                            
                        }else{
                            snapToScreen(mCurrentScreen + 1);
                        }
	                } else {
	                    snapToDestination();
	                }
	                if (mVelocityTracker != null) {
	                    mVelocityTracker.recycle();
	                    mVelocityTracker = null;
	                }
	            }
	            mTouchState = TOUCH_STATE_REST;
	            break;
	        case MotionEvent.ACTION_CANCEL:
	            mTouchState = TOUCH_STATE_REST;
	        }
        return true;
    }
    
    /**
     * 偶数化
     * @param deltaX
     * @return
     */
    private int Even(int deltaX){
        if(deltaX%2 == 0){
            
        }else{
            deltaX += 1;
        }
        return deltaX;
    }

    protected void snapToDestination() {

    	int whichScreen = 0;
    	int count = getChildCount();
    	int start = 0;
    	int end = 0;
    	int viewWidth = 0;
    	int tend = 0;
    	int tstart = 0;
    	final int scrollX = getScrollX();
    	for(int i =0;i<count;i++){
    		viewWidth = getChildAt(i).getWidth();
    		tend = end + viewWidth/2;
    		if(i != 0 ){
    			viewWidth = getChildAt(i-1).getWidth();
    		}
    		tstart-=viewWidth;
    		if(scrollX > tstart && scrollX < tend){
    			break;
    		}
    		start+=viewWidth;
    		end+=viewWidth;
    		whichScreen++;
    	}
        snapToScreen(whichScreen);
    }

    public void snapToScreen(int whichScreen) {
    	
        enableChildrenCache();

        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        boolean changingScreens = whichScreen != mCurrentScreen;
        
        mNextScreen = whichScreen;

        View focusedChild = getFocusedChild();
        if (focusedChild != null && changingScreens && focusedChild == getChildAt(mCurrentScreen)) {
            focusedChild.clearFocus();
        }
        
        int newX = 0;
        
        for(int i=0;i<whichScreen;i++){
        	newX+=getChildAt(i).getWidth();
        }
        newX = Math.min(totalWidth - getWidth(), newX);
        final int delta = newX - getScrollX();
        int duration = Math.abs(delta)*2;
        //TODO 松开手后自动滑动
        mScroller.startScroll(getScrollX(), 0, delta, 0, duration);
        onViewTouchMoveListener.OnViewChange(getScrollX(), delta,whichScreen, duration);
        invalidate();
    }
    void moveToDefaultScreen() {
        snapToScreen(mDefaultScreen);
        getChildAt(mDefaultScreen).requestFocus();
    }


	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		View child;
		for(int i=0;i<getChildCount();i++){
			child = getChildAt(i);
			child.setFocusable(true);
			child.setClickable(true);
		}
	}
	public void setOnViewTouchMoveListener(OnViewTouchMoveListener onViewTouchMoveListener){
        this.onViewTouchMoveListener = onViewTouchMoveListener;
    }
}