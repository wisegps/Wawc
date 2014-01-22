package com.wise.service;
import java.util.List;

import com.wise.extend.MyImageView;
import com.wise.wawc.ImageActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter{
	Context mContext; // 上下文
	List<Bitmap> imageList = null;

	// 构造函数
	public ImageAdapter(Context context, List<Bitmap> imageList) {
		this.mContext = context;
		this.imageList = imageList;
	}
	public int getCount() {
		return imageList.size();
	}
	public Object getItem(int position) {
		return imageList.get(position);
	}
	public long getItemId(int position) {
		return position;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		//每次移动获取图片并重新加载，当图片很多时可以构造函数就把bitmap引入并放入list当中，
		//然后在getview方法当中取来直接用
//		Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), mImageResourceIds[position]);
		
		MyImageView imageView = new MyImageView(mContext, imageList.get(position).getWidth(), imageList.get(position).getHeight());
		imageView.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		imageView.setImageBitmap(imageList.get(position));
		return imageView;
	}
	
	public void refreshDatas(List<Bitmap> imageList){
		this.imageList = imageList;
		Log.e("刷新迭代器","刷新迭代器" + this.imageList.size());
		this.notifyDataSetChanged();
	}
}
