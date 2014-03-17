package com.wise.service;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.wise.extend.MyImageView;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.wawc.ImageActivity;
import com.wise.wawc.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter{
	Context mContext; // 上下文
	List<Bitmap> imageList = null;
	Bitmap bitmap = null;
	LayoutInflater layoutInflater = null;
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
		
//		if(convertView == null){
//			viewHolder = new ViewHolder();
//			convertView = layoutInflater.inflate(R.layout.image_adapter_item, null);
//			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_explare_item);
//			convertView.setTag(viewHolder);
//		}else{
//			viewHolder = (ViewHolder) convertView.getTag();
//		}
		MyImageView imageView = new MyImageView(mContext, imageList.get(position).getWidth(), imageList.get(position).getHeight());
		imageView.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		imageView.setImageBitmap(imageList.get(position));
		return imageView;
		
//		Map<String,String> imageMap = imageList.get(position);
//		String str = imageMap.get("big_pic");
//		Bitmap bitmap = imageIsExist(Constant.VehiclePath + str.substring(str.lastIndexOf("/")),str);
//		if(bitmap == null){
//			Bitmap tempBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.article);
//			viewHolder.imageView.setImageBitmap(tempBitmap);
//		}else{
//			viewHolder.imageView.setImageBitmap(bitmap);
//		}
//		return convertView;
	}
	public void refreshDatas(List<Bitmap> imageList){
		this.imageList = imageList;
		Log.e("刷新迭代器","刷新迭代器" + this.imageList.size());
		this.notifyDataSetChanged();
	}
}
