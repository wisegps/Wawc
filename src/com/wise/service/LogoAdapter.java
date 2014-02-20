package com.wise.service;

import java.util.ArrayList;
import java.util.List;

import com.wise.data.CarData;
import com.wise.pubclas.Constant;
import com.wise.pubclas.Variable;
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
		
		for(int i = 0 ; i < this.carDataList.size() ; i ++){
			String carNumber =  this.carDataList.get(i).getObj_name();
			String carNumber1 = carNumber == null?"":carNumber;
			Log.e("车牌号----->",carNumber1);
			Log.e("品牌：",this.carDataList.get(i).car_brand);
		}
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
			Bitmap bitmap = BitmapFactory.decodeFile(Constant.VehicleLogoPath + carDataList.get(position).getCar_brand() + ".png");
			hodler.imageView.setImageBitmap(bitmap);
			String str = this.carDataList.get(position).getObj_name();
			String str2 = str==null?"null":str;
			hodler.vehicleNum.setText(str2);
		if(position == carDataList.size()){
			hodler.imageView.setBackgroundResource(R.drawable.new_vehicle);
		}
		
		 if(carDataList.get(position).isCheck()){
//			 hodler.linearLayout.setBackgroundResource(R.color.gray);
			 hodler.linearLayout.setBackgroundResource(R.drawable.bg_car_logo);
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
