package com.wise.service;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;

/**
 * 位图缓存类
 * @author Administrator
 */
public class ViewCache {
	/**
	 * 最大缓存字节
	 */
	private int mMaxCacheSizeInbytes;
	/**
	 * 当前的缓存字节
	 */
	private int mCurrentSizeInbytes;
	private Map<String,View> mCache;
	public static ViewCache viewCache;
	private ViewCache(){
		mCache = Collections.synchronizedMap(new LinkedHashMap<String,View>(10, 1.5f,true));//按使用次数排序
		mMaxCacheSizeInbytes = 100;
	}
	/**
	 * 得到一个缓存对象的实例 . 可缓存50个控件
	 */
	public static ViewCache getInstance(){
		if(viewCache == null){
			viewCache = new ViewCache();
		}
		return viewCache;
	}
	public void setmMaxCacheSizeInbytes(int mMaxCacheSizeInbytes) {
		this.mMaxCacheSizeInbytes = mMaxCacheSizeInbytes;
		checkCache();
	}
	public int getmMaxCacheSizeInbytes() {
		return mMaxCacheSizeInbytes;
	}
	/**
	 * 得到控件
	 */
	public void putView(String name,View view){
		if (mCache.containsKey(name)) {  //存在这张图的key
			mCurrentSizeInbytes -= 1;
		}
		Log.e("图片数量：",mCache.size()+"");
		mCache.put(name, view);
		mCurrentSizeInbytes += 1;  //每添加一张图片将内存大小计算出来
		//TODO 测试后删除
		System.out.println("mCurrentSizeInbytes checking : "+mCurrentSizeInbytes +" \t datetime : " + System.currentTimeMillis());
		checkCache();
		//TODO 测试后删除
		System.out.println("mCurrentSizeInbytes checked : "+mCurrentSizeInbytes +" \t datetime : " + System.currentTimeMillis());
	}
	/**
	 * 取得位图
	 * @param bitmapId 位图id
	 * @return 如果没有则返回null
	 */
	public View getView(String name) {
		if (mCache.containsKey(name)) {
			return mCache.get(name);
		}
		return null;
	}
	/**
     * 严格控制堆内存，
     * 如果内存超过将移除最近最少使用的图片缓存 .
     * 直到当前缓存大小,小于最大缓存 . 
     */
	private void checkCache() {
		if (mCurrentSizeInbytes > mMaxCacheSizeInbytes) {
			Log.e("清除缓存","清除缓存");
			// 先遍历最近最少使用的元素
			Iterator<Entry<String, View>> iter = mCache.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, View> entry = iter.next();
				mCurrentSizeInbytes -= 1;
				iter.remove();
				if (mCurrentSizeInbytes <= mMaxCacheSizeInbytes){
					break;
				}
			}
		}
	}
	/**
	 * 释放全部缓存
	 */
	public void releaseCache(){
		mCache.clear();
	}
}

