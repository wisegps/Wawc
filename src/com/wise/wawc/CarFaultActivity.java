package com.wise.wawc;

import java.util.ArrayList;
import java.util.List;
import com.wise.list.XListView;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * 爱车故障
 * @author honesty
 */
public class CarFaultActivity extends Activity{
	
	XListView lv_activity_car_fault;
	List<FaultData> faultDatas = new ArrayList<FaultData>();
	FaultAdapter faultAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_car_fault);
		lv_activity_car_fault = (XListView)findViewById(R.id.lv_activity_car_fault);
		GetData();
		for(int i = 0 ; i < faultDatas.size() ; i++){
			System.out.println(faultDatas.get(i).toString());
		}
		faultAdapter = new FaultAdapter(CarFaultActivity.this,faultDatas);
		lv_activity_car_fault.setAdapter(faultAdapter);
	}
	private void GetData(){
		FaultData faultData = new FaultData();
		faultData.setData("今天");
		faultData.setDataFrist(true);
		faultData.setDataLast(false);
		faultData.setFaultCode("故障码：1234");
		faultData.setFaultInfo("故障描述：发送机故障");
		faultDatas.add(faultData);
		
		FaultData faultData1 = new FaultData();
		faultData1.setData("今天");
		faultData1.setDataFrist(false);
		faultData1.setDataLast(true);
		faultData1.setFaultCode("故障码：1234");
		faultData1.setFaultInfo("故障描述：发送机故障");
		faultDatas.add(faultData1);
		
		for(int i = 0 ; i < 8; i++){
			FaultData faultData2 = new FaultData();
			faultData2.setData("今天" + i);
			faultData2.setDataFrist(true);
			faultData2.setDataLast(true);
			faultData2.setFaultCode("故障码：1234");
			faultData2.setFaultInfo("故障描述：发送机故障");
			faultDatas.add(faultData2);
		}
	}
	
	private class FaultAdapter extends BaseAdapter{
		LayoutInflater mInflater;
		List<FaultData> faultDatas;
		private FaultAdapter(Context mContext,List<FaultData> faultDatas){
			mInflater = LayoutInflater.from(mContext);
			this.faultDatas = faultDatas;
		}
		@Override
		public int getCount() {
			return faultDatas.size();
		}
		@Override
		public Object getItem(int position) {
			return faultDatas.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_car_fault, null);
				holder = new ViewHolder();
				holder.tv_item_car_fault_data = (TextView) convertView.findViewById(R.id.tv_item_car_fault_data);
				holder.tv_item_car_fault_code = (TextView)convertView.findViewById(R.id.tv_item_car_fault_code);
				holder.tv_item_car_fault_info = (TextView)convertView.findViewById(R.id.tv_item_car_fault_info);
				holder.iv_item_car_fault_share = (ImageView)convertView.findViewById(R.id.iv_item_car_fault_share);
				holder.ll_item_car_fault = (LinearLayout)convertView.findViewById(R.id.ll_item_car_fault);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			FaultData faultData = faultDatas.get(position);
			holder.tv_item_car_fault_data.setText(faultData.getData());
			holder.tv_item_car_fault_code.setText(faultData.getFaultCode());
			holder.tv_item_car_fault_info.setText(faultData.getFaultInfo());
			if(faultData.isDataFrist){
				holder.tv_item_car_fault_data.setVisibility(View.VISIBLE);
			}else{
				holder.tv_item_car_fault_data.setVisibility(View.INVISIBLE);
			}
			if(faultData.isDataLast){
				holder.ll_item_car_fault.setVisibility(View.VISIBLE);
			}else{
				holder.ll_item_car_fault.setVisibility(View.GONE);
			}
			return convertView;
		}
		private class ViewHolder {
			TextView tv_item_car_fault_data,tv_item_car_fault_code,tv_item_car_fault_info;
			ImageView iv_item_car_fault_share;
			LinearLayout ll_item_car_fault;
		}
	}
	
	private class FaultData{
		String Data;
		String FaultCode;
		String FaultInfo;
		boolean isDataFrist;
		boolean isDataLast;
		public String getData() {
			return Data;
		}
		public void setData(String data) {
			Data = data;
		}
		public String getFaultCode() {
			return FaultCode;
		}
		public void setFaultCode(String faultCode) {
			FaultCode = faultCode;
		}
		public String getFaultInfo() {
			return FaultInfo;
		}
		public void setFaultInfo(String faultInfo) {
			FaultInfo = faultInfo;
		}
		public boolean isDataFrist() {
			return isDataFrist;
		}
		public void setDataFrist(boolean isDataFrist) {
			this.isDataFrist = isDataFrist;
		}
		public boolean isDataLast() {
			return isDataLast;
		}
		public void setDataLast(boolean isDataLast) {
			this.isDataLast = isDataLast;
		}
		@Override
		public String toString() {
			return "FaultData [Data=" + Data + ", FaultCode=" + FaultCode
					+ ", FaultInfo=" + FaultInfo + ", isDataFrist="
					+ isDataFrist + ", isDataLast=" + isDataLast + "]";
		}		
	}
}