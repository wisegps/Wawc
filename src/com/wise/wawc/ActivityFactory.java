package com.wise.wawc;

import com.wise.extend.SlidingMenuView;
import android.view.ViewGroup;
/**
 * 传递类
 * @author honesty
 */
public class ActivityFactory {
	public static MainActivity A;
	/**
	 * 用于解决滑动冲突
	 */
	public static ViewGroup v;
	/**
	 * 用于判断当前屏幕所处状态
	 */
	public static SlidingMenuView S;
}
