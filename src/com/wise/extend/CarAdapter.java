package com.wise.extend;

import java.util.List;
import com.wise.data.CarData;
import com.wise.wawc.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private static final int VALUE_CAR = 0;
    private static final int VALUE_ADD = 1;
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
	    System.out.println("position = " + position);
	    int type = getItemViewType(position);
		ViewHolder holder = null;
		ViewAdd viewAdd;
		if (convertView == null) {
		    switch (type) {
                case VALUE_CAR:
        			convertView = mInflater.inflate(R.layout.item_cars, null);
        			holder = new ViewHolder();
        			holder.tv_item_carnumber = (TextView) convertView.findViewById(R.id.tv_item_carnumber);
        			holder.iv_item_cars = (ImageView)convertView.findViewById(R.id.iv_item_cars);
        			holder.ll_item_cars = (LinearLayout)convertView.findViewById(R.id.ll_item_cars);
        			convertView.setTag(holder);
        			break;
                case VALUE_ADD:
                    convertView = mInflater.inflate(R.layout.item_add, null);
                    viewAdd = new ViewAdd();
                    viewAdd.rl_add = (RelativeLayout)convertView.findViewById(R.id.rl_add);
                    convertView.setTag(viewAdd);
                    break;
    		    }
		} else {
		    switch (type) {
                case VALUE_CAR:
                    holder = (ViewHolder) convertView.getTag();                    
                    break;
                case VALUE_ADD:
                    viewAdd = (ViewAdd) convertView.getTag();
                    break;
		    }
		}
		switch (type) {
            case VALUE_CAR:
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
            break;
        }
		return convertView;
	}
	
	@Override
    public int getItemViewType(int position) {
	    return carDatas.get(position).getType();
    }

    private class ViewHolder {
		TextView tv_item_carnumber;
		ImageView iv_item_cars;
		LinearLayout ll_item_cars;
	}
	private class ViewAdd{
	    RelativeLayout rl_add;
	}
}
