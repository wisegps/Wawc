package com.wise.extend;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.HorizontalScrollView;
import android.widget.Scroller;

public class PicHorizontalScrollView extends HorizontalScrollView{
    static final String TAG = "PicHorizontalScrollView";
    private Scroller mScroller;
    int FristScreenWidth;
    public PicHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(getContext());
    }
    /**
     * 只传后2个参数即可
     * @param ScrollX
     * @param delta
     * @param whichScreen 
     * @param duration
     */
    public void snapToPic(int ScrollX,int delta,int whichScreen ,int duration){
        int mydelta = 0;//移动的目标
        if(whichScreen == 0){
            mydelta = 0;
        }else{
            mydelta = FristScreenWidth;
        }
        int Distance = mydelta - getScrollX(); //从当前位置到目标位置的距离
        mScroller.startScroll(getScrollX(), 0, Distance, 0, duration);
        invalidate();
    }
    public void SetFristScreenWidth(int width){
        FristScreenWidth = width;
    }
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
        }
    }
    
}