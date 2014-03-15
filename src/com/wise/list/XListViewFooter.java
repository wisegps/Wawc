/**
 * @file XFooterView.java
 * @create Mar 31, 2012 9:33:43 PM
 * @author Maxwin
 * @description XListView's footer
 */
package com.wise.list;

import com.wise.wawc.MainActivity;
import com.wise.wawc.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

public class XListViewFooter extends LinearLayout {
    /**
     * footer一般状态，显示“查看更多”
     */
    public final static int STATE_NORMAL = 0;
    /**
     * footer准备状态，显示“松开加载更多”
     */
    public final static int STATE_READY = 1;
    /**
     * footer加载状态，显示进度条
     */
    public final static int STATE_LOADING = 2;

    private Context mContext;

    private View mContentView;
    private View xlistview_footer_iv;
    private TextView mHintView;
    
    public XListViewFooter(Context context) {
        super(context);
        initView(context);
    }
    
    public XListViewFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    /**
     * 控件的3个状态
     * @param state
     */
    public void setState(int state) {
        mHintView.setVisibility(View.INVISIBLE);
        xlistview_footer_iv.setVisibility(View.INVISIBLE);
        xlistview_footer_iv.clearAnimation();
        mHintView.setVisibility(View.INVISIBLE);
        if (state == STATE_READY) {
            mHintView.setVisibility(View.VISIBLE);
            mHintView.setText(R.string.xlistview_footer_hint_ready);
        } else if (state == STATE_LOADING) {
            xlistview_footer_iv.setVisibility(View.VISIBLE);
            Animation operatingAnim = AnimationUtils.loadAnimation(mContext, R.anim.tip_fast);  
            LinearInterpolator lin = new LinearInterpolator();  
            operatingAnim.setInterpolator(lin); 
            if (operatingAnim != null) {  
                xlistview_footer_iv.startAnimation(operatingAnim);  
            }
        } else {
            mHintView.setVisibility(View.VISIBLE);
            mHintView.setText(R.string.xlistview_footer_hint_normal);
        }
    }
    
    public void setBottomMargin(int height) {
        if (height < 0) return ;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mContentView.getLayoutParams();
        lp.bottomMargin = height;
        mContentView.setLayoutParams(lp);
    }
    
    public int getBottomMargin() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mContentView.getLayoutParams();
        return lp.bottomMargin;
    }
    
    
    /**
     * 一般状态
     */
    public void normal() {
        mHintView.setVisibility(View.VISIBLE);
        xlistview_footer_iv.setVisibility(View.GONE);
        xlistview_footer_iv.clearAnimation();
    }
    
    
    /**
     * 加载状态
     */
    public void loading() {
        mHintView.setVisibility(View.GONE);
        xlistview_footer_iv.setVisibility(View.VISIBLE);
        Animation operatingAnim = AnimationUtils.loadAnimation(mContext, R.anim.tip_fast);  
        LinearInterpolator lin = new LinearInterpolator();  
        operatingAnim.setInterpolator(lin); 
        if (operatingAnim != null) {  
            xlistview_footer_iv.startAnimation(operatingAnim);  
        }
    }
    
    /**
     * 隐藏底部
     */
    public void hide() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mContentView.getLayoutParams();
        lp.height = 0;
        mContentView.setLayoutParams(lp);
    }
    
    /**
     * 显示底部
     */
    public void show() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mContentView.getLayoutParams();
        lp.height = LayoutParams.WRAP_CONTENT;
        mContentView.setLayoutParams(lp);
    }
    /**
     * 初始化控件
     * @param context
     */
    private void initView(Context context) {
        mContext = context;
        LinearLayout moreView = (LinearLayout)LayoutInflater.from(mContext).inflate(R.layout.xlistview_footer, null);
        addView(moreView);
        moreView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        
        mContentView = moreView.findViewById(R.id.xlistview_footer_content);
        xlistview_footer_iv = moreView.findViewById(R.id.xlistview_footer_iv);
        mHintView = (TextView)moreView.findViewById(R.id.xlistview_footer_hint_textview);
    }
    
    
}
