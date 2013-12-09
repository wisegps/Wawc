package com.wise.extend;

import com.wise.wawc.R;
import com.wise.wawc.R.id;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Scroller;

/**
 * EffectSpace
 * @author lance
 *
 */
public class SlidingMenuView extends ViewGroup{
	
	private static String LOG_TAG = "SlidingMenuView";
    private static final int INVALID_SCREEN = -1;

    private static final int SNAP_VELOCITY = 1000;

    private int mDefaultScreen = 1;

    private int mCurrentScreen;
    private int mNextScreen = INVALID_SCREEN;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;


    private float mLastMotionX;
    private float mLastMotionY;

    private final static int TOUCH_STATE_REST = 0;
    private final static int TOUCH_STATE_SCROLLING = 1;

    public int mTouchState = TOUCH_STATE_REST;


    private boolean mAllowLongPress;
    private boolean mLocked;

    private int mTouchSlop;

    int totalWidth = 0;
    
    public SlidingMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
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

    int getCurrentScreen() {
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
		
		View v1 = findViewById(R.id.left_sliding_tab);

		v1.measure(v1.getLayoutParams().width+v1.getLeft()+v1.getRight(), heightMeasureSpec);
		
		View v2 = findViewById(R.id.sliding_body);
	    v2.measure(widthMeasureSpec, heightMeasureSpec);
	    
	    View v3 = findViewById(R.id.right_sliding_tab);
	    v3.measure(v3.getLayoutParams().width+v3.getLeft()+v3.getRight(), heightMeasureSpec);
	}

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
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
        return true;
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
        newX =Math.min(totalWidth - getWidth(), newX);
        final int delta = newX - getScrollX();
        mScroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta) * 2);
        invalidate();
    }
    
    public int getScreenForView(View v) {
        int result = -1;
        if (v != null) {
            ViewParent vp = v.getParent();
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                if (vp == getChildAt(i)) {
                    return i;
                }
            }
        }
        return result;
    }

    public void unlock() {
        mLocked = false;
    }

    public void lock() {
        mLocked = true;
    }
    
    void moveToDefaultScreen() {
        snapToScreen(mDefaultScreen);
        getChildAt(mDefaultScreen).requestFocus();
    }


	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		View child;
		for(int i=0;i<getChildCount();i++){
			child = getChildAt(i);
			child.setFocusable(true);
			child.setClickable(true);
		}
	}
}