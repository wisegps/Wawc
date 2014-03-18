package com.wise.extend;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class MyScrollLayout extends ViewGroup{
    private static final String TAG = "ScrollLayout";      
    private VelocityTracker mVelocityTracker;  			// 用于判断甩动手势    
    private static final int SNAP_VELOCITY = 600;       //滑动速度
    private Scroller  mScroller;						// 滑动控制器	
    private int mCurScreen;    			//当前所在屏幕			    
	private int mDefaultScreen = 0;    						 
    private float mLastMotionX;       
    
    private OnViewChangeListener mOnViewChangeListener;	 
	public MyScrollLayout(Context context) {
		super(context);
		init(context);
	}	
	public MyScrollLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	public MyScrollLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context){
		Log.d(TAG, "init");
		mCurScreen = mDefaultScreen;    	     	        
	    mScroller = new Scroller(context); 
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		Log.d(TAG, "onLayout="+changed);	
		 if (changed) {    
	            int childLeft = 0;    
	            final int childCount = getChildCount();    	                
	            for (int i=0; i<childCount; i++) {    
	                final View childView = getChildAt(i);     
	                    final int childWidth = childView.getMeasuredWidth();    
	                    childView.layout(childLeft, 0,     
	                            childLeft+childWidth, childView.getMeasuredHeight());    
	                    childLeft += childWidth;    
	            }    
	        }    
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);		     	    		
		final int count = getChildCount();       
        for (int i = 0; i < count; i++) {       
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);   
        }                
        scrollTo(0, 0);	
        Log.d(TAG, "-------------count="+count);    
	}
	/**
	 * 滑动速度过慢的话调用这个方法判断是否滑动了半个屏幕
	 * 并计算出当前显示那个屏幕
	 */
	 public void snapToDestination() {    
	        final int screenWidth = getWidth();    
	        final int destScreen = (getScrollX()+ screenWidth/2)/screenWidth; //算出对应的屏幕
	        //Log.d(TAG, "screenWidth="+screenWidth+",getScrollX()="+getScrollX()+",destScreen="+destScreen);
	        snapToScreen(destScreen);    
	 }  
	 /**
	  * 跳转到对应的屏幕
	  * @param whichScreen
	  */
	 public void snapToScreen(int whichScreen) {    	
	        // get the valid layout page  
	        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount()-1));  //防止输入不再范围内的数字
	        if (getScrollX() != (whichScreen*getWidth())) {    	                
	            final int delta = whichScreen*getWidth()-getScrollX(); //还有多少没有显示   
	      	    mScroller.startScroll(getScrollX(), 0,delta, 0, Math.abs(delta)*2); //Math.abs(delta)*2滚动delta距离
	            
	            mCurScreen = whichScreen;   //当前显示的屏幕 
	            invalidate();       // Redraw the layout    	            
	            if (mOnViewChangeListener != null){
	            	mOnViewChangeListener.OnViewChange(mCurScreen);
	            	//mOnViewChangeListener.doit(mCurScreen);
	            }
	        }    
	    }    

	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if (mScroller.computeScrollOffset()) {    
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());  
            postInvalidate();    
        }   
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub           	            
	        final int action = event.getAction();    
	        final float x = event.getX();    
	        final float y = event.getY();    
	            
	        switch (action) {    
	        case MotionEvent.ACTION_DOWN: 
	        	if (mVelocityTracker == null) {    
			            mVelocityTracker = VelocityTracker.obtain();    
			            mVelocityTracker.addMovement(event); 
			    }        	 
	            if (!mScroller.isFinished()){    
	                mScroller.abortAnimation();    
	            }                
	            mLastMotionX = x;	
	            //Log.d(TAG, "mLastMotionX="+mLastMotionX);
	            break;    
	                
	        case MotionEvent.ACTION_MOVE:  
	           int deltaX = (int)(mLastMotionX - x);//计算横向移动距离
	           //Log.d(TAG, "deltaX="+deltaX+",mLastMotionX="+mLastMotionX+",x="+x);
        	   if (IsCanMove(deltaX)){
        		 if (mVelocityTracker != null){
  		            	mVelocityTracker.addMovement(event); 
  		         }   
  	             mLastMotionX = x;   
  	             //Log.d(TAG, "deltaX="+deltaX);
  	             scrollBy(deltaX, 0);	//画面跟随指尖
        	   }
	           break;    	                
	        case MotionEvent.ACTION_UP:       	        	
	        	int velocityX = 0;
	            if (mVelocityTracker != null){
	            	mVelocityTracker.addMovement(event); 
	            	mVelocityTracker.computeCurrentVelocity(1000);  
	            	velocityX = (int) mVelocityTracker.getXVelocity();//计算x方向的速度
	            	//Log.d(TAG, "velocityX="+velocityX + ",mCurScreen=" + mCurScreen);
	            }	               	                
	            if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {       
	                // Fling enough to move left       
	                //Log.e(TAG, "snap left");    
	                snapToScreen(mCurScreen - 1);       
	            } else if (velocityX < -SNAP_VELOCITY&& mCurScreen < getChildCount() - 1) {       
	                // Fling enough to move right       
	                //Log.e(TAG, "snap right");    
	                snapToScreen(mCurScreen + 1);       
	            } else {
	            	//Log.e(TAG, "到底翻不翻");    
	                snapToDestination();       
	            }      
	            	            
	            if (mVelocityTracker != null) {       
	                mVelocityTracker.recycle();       
	                mVelocityTracker = null;       
	            }       
	            break;      
	        }    	            
	        return true;    
	}
	/**
	 * 排除第一页像左划，最后一页像右划
	 * @param deltaX
	 * @return
	 */
	private boolean IsCanMove(int deltaX){
		//Log.d(TAG, "getScrollX() = "+getScrollX() + ",deltaX = "+ deltaX);
		if (getScrollX() <= 0 && deltaX < 0 ){
			return false;
		}	
		if  (getScrollX() >=  (getChildCount() - 1) * getWidth() && deltaX > 0){
			return false;
		}		
		return true;
	}
	
	public void SetOnViewChangeListener(OnViewChangeListener listener){
		mOnViewChangeListener = listener;
	}
	public void showWhatSp(){
		System.out.println("showWhatSp()");
	}
}