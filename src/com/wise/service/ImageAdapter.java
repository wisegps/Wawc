package com.wise.service;
import com.wise.extend.MyImageView;
import com.wise.wawc.ImageActivity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter{
	Context mContext; // 上下文
	int mImageResourceIds[];

	// 构造函数
	public ImageAdapter(Context context, int mImageResourceIds[]) {
		this.mContext = context;
		this.mImageResourceIds = mImageResourceIds;
	}
	public int getCount() {
		return mImageResourceIds.length;
	}
	
	public Object getItem(int position) {
		return mImageResourceIds[position];
	}
	public long getItemId(int position) {
		return position;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
//		MyImageView imageView = new MyImageView(mContext);
//		imageView.setImageDrawable(mContext.getResources().getDrawable(mImageResourceIds[position]));
		ImageView imageView = new ImageView(mContext);
		imageView.setImageResource(mImageResourceIds[position]);
		imageView.setLayoutParams(new Gallery.LayoutParams(ImageActivity.screenWidth, ImageActivity.screenHeight));
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		return imageView;
	}
}
