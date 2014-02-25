package com.wise.extend;

import java.util.List;
import com.wise.data.CarData;
import com.wise.pubclas.BlurImage;
import com.wise.pubclas.Constant;
import com.wise.wawc.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 车辆信息，车牌，车标
 * @author honesty
 */
public class CarAdapter extends BaseAdapter{
	private static final String TAG = "CarAdapter";
	Context context;
	List<CarData> carDatas;
	LayoutInflater mInflater;
	public CarAdapter(Context context,List<CarData> carDatas){
		this.context = context;
		this.carDatas = carDatas;
		mInflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return carDatas.size();
	}
	@Override
	public Object getItem(int arg0) {
		return carDatas.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_cars, null);
			holder = new ViewHolder();
			holder.tv_item_carnumber = (TextView) convertView.findViewById(R.id.tv_item_carnumber);
			holder.iv_item_cars = (ImageView)convertView.findViewById(R.id.iv_item_cars);
			holder.ll_item_cars = (LinearLayout)convertView.findViewById(R.id.ll_item_cars);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		CarData carData = carDatas.get(position);
		holder.tv_item_carnumber.setText(carData.getObj_name());
		if(carData.getLogoPath() == null || carData.getLogoPath().equals("")){
		    holder.iv_item_cars.setImageResource(R.drawable.ic_launcher);
		}else{
		    Bitmap bitmap = BitmapFactory.decodeFile(carData.getLogoPath());
		    //Bitmap bitmap = BlurImage.decodeSampledBitmapFromPath(carData.getLogoPath(), 80, 80);            
            if(bitmap != null){
                holder.iv_item_cars.setImageBitmap(bitmap);
            }else{
                holder.iv_item_cars.setImageResource(R.drawable.ic_launcher);
            }
		}
		if(carData.isCheck()){
			holder.ll_item_cars.setBackgroundResource(R.drawable.bg_car_logo);
		}else{
			holder.ll_item_cars.setBackgroundDrawable(null);
		}
		return convertView;
	}
	private class ViewHolder {
		TextView tv_item_carnumber;
		ImageView iv_item_cars;
		LinearLayout ll_item_cars;
	}
}
