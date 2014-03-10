package com.wise.extend;

import com.wise.wawc.ActivityFactory;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
/**
 * 自定义FrameLayout,用于滑出菜单后点击onLoad页面关闭菜单
 * @author honesty
 */
public class MyFrameLayout extends FrameLayout{

    public MyFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //如果当前屏幕处在第一屏，则不需要拦截事件
        //如果屏幕处在非第一瓶，则拦截load页面里的点击事件
        if(ActivityFactory.S.getCurrentScreen() == 1){
            return false;
        }
        ActivityFactory.A.LeftMenu();
        return true;
    }
}
