package com.wise.extend;

public interface OnViewTouchMoveListener {
    public abstract void OnViewMove(int x);
    public abstract void OnViewChange(int ScrollX,int delta,int whichScreen,int duration);
    /**
     * 布局加载完毕后触发
     * @param width 总长度
     * @param delta 第一屏的长度
     */
    public abstract void OnViewLoad(int width,int delta);
}
