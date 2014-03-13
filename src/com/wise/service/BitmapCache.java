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

/**
 * 位图缓存类
 * @author Administrator
 *
 */
public class BitmapCache {
	/**
	 * 最大缓存字节
	 */
	private long mMaxCacheSizeInbytes;
	/**
	 * 当前的缓存字节
	 */
	private long mCurrentSizeInbytes;
	private Map<Object,BitmapDrawable> mCache;
	public static BitmapCache mBitmapCache;
	
	private BitmapCache(){
		mCache = Collections.synchronizedMap(new LinkedHashMap<Object, BitmapDrawable>(10, 1.5f,true));//按使用次数排序
//		mMaxCacheSizeInbytes = 4194304;//2<<(22-1)  1m = 2的20次方 1k = 2的10次方 1g = 2的30次方
		int i = 20971520;
		mMaxCacheSizeInbytes = i;
	}
	/**
	 * 得到一个缓存对象的实例 . 
	 * 默认缓存大小为4m .
	 */
	public static BitmapCache getInstance(){
		if(mBitmapCache == null){
			mBitmapCache = new BitmapCache();
		}
		return mBitmapCache;
	}
	public void setmMaxCacheSizeInbytes(long mMaxCacheSizeInbytes) {
		this.mMaxCacheSizeInbytes = mMaxCacheSizeInbytes;
		checkCache();
	}
	public long getmMaxCacheSizeInbytes() {
		return mMaxCacheSizeInbytes;
	}
	/**
	 * 添加位图 .
	 * @param bitmapId 位图id
	 * @param drawable 位图
	 */
	public void putBitmap(Object bitmapId,BitmapDrawable drawable){
		if (mCache.containsKey(bitmapId)) {  //存在这张图的key
			mCurrentSizeInbytes -= getSizeInbytes( mCache.get(bitmapId) );
		}
		mCache.put(bitmapId, drawable);
		mCurrentSizeInbytes += getSizeInbytes(drawable);  //每添加一张图片将内存大小计算出来
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
	public BitmapDrawable getBitmap(Object bitmapId) {
		if (mCache.containsKey(bitmapId)) {
			return mCache.get(bitmapId);
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
			// 先遍历最近最少使用的元素
			Iterator<Entry<Object, BitmapDrawable>> iter = mCache.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<Object, BitmapDrawable> entry = iter.next();
				mCurrentSizeInbytes -= getSizeInbytes((BitmapDrawable)entry.getValue());
				iter.remove();
				if (mCurrentSizeInbytes <= mMaxCacheSizeInbytes){
					break;
				}
			}
		}
	}
	/**
	 * 返回位图占用的字节数
	 * @param drawable
	 * @return 位图占用的字节数
	 */
	public long getSizeInbytes(BitmapDrawable drawable){
		return drawable.getBitmap().getRowBytes() * drawable.getBitmap().getHeight();
	}
	/**
	 * 释放全部缓存
	 */
	public void releaseCache(){
		mCache.clear();
	}
	
	
}

