package com.wise.service;

import java.util.ArrayList;
import java.util.List;

import com.wise.data.CarData;
import com.wise.pubclas.Constant;
import com.wise.wawc.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LogoAdapter extends BaseAdapter {
	private Context context;
	private List<CarData> carDataList;
	private LayoutInflater layoutInflater;
	
	private List<View> viewList = new ArrayList<View>();
	public LogoAdapter(Context context,List<CarData> carDataList){
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.carDataList = carDataList;
	}
	public int getCount() {
		return carDataList.size();
	}
	public Object getItem(int position) {
		return carDataList.get(position);
	}
	public long getItemId(int position) {
		return 0;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = layoutInflater.inflate(R.layout.logo_adapter, null);
		ViewHodler hodler = new ViewHodler();
		hodler.linearLayout = (LinearLayout) convertView.findViewById(R.id.image_layout);
		hodler.imageView = (ImageView) convertView.findViewById(R.id.image_view);
		hodler.vehicleNum = (TextView) convertView.findViewById(R.id.vehicle_logo_num);
		
		if(carDataList.get(position).getCar_brand() != null){
			Log.e("路径：",Constant.VehicleLogoPath + carDataList.get(position).getCar_brand() + ".jpg");
			Bitmap bitmap = BitmapFactory.decodeFile(Constant.VehicleLogoPath + carDataList.get(position).getCar_brand() + ".jpg");
			hodler.imageView.setImageBitmap(bitmap);
			hodler.vehicleNum.setText(carDataList.get(position).getObj_name());
		}else{
			hodler.imageView.setBackgroundResource(R.drawable.image);
		}
		hodler.imageView.setBackgroundResource(R.drawable.image);
		if(position == carDataList.size()){
			hodler.imageView = (ImageView) convertView.findViewById(R.id.image_view);
			hodler.imageView.setBackgroundResource(R.drawable.new_vehicle);
		}
		
		 if(carDataList.get(position).isCheck()){
			 hodler.linearLayout.setBackgroundResource(R.color.bkg1);
         }else{
        	 hodler.linearLayout.setBackgroundDrawable(null);
         }
		return convertView;
	}
	private class ViewHodler{
		LinearLayout linearLayout;
		ImageView imageView;
		TextView vehicleNum;
	}
	
	public void updataDatas(List<CarData> carDataList){
		this.carDataList = carDataList;
		this.notifyDataSetChanged();
	}
}
