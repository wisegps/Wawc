package com.wise.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import com.wise.data.BrankModel;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.wawc.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BrankAdapter extends BaseAdapter{
	private Context context;
	private List<BrankModel> brankKList;
	ViewHolder viewHolder = null;
	private static final int showLogo = 1;
	public BrankAdapter(Context context,List<BrankModel> brankKList){
		this.context = context;
		this.brankKList = brankKList;
	}
	
	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param list
	 */
	public void updateListView(List<BrankModel> list){
		this.brankKList = list;
		notifyDataSetChanged();
	}
	final static class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
		ImageView logo;
	}
	public int getCount() {
		return brankKList.size();
	}
	public Object getItem(int position) {
		return brankKList.get(position);
	}
	public long getItemId(int position) {
		return position;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		final BrankModel mContent = brankKList.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.brank_adapter_list, null);
			viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.list_brank);
			viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.list_letter);
			viewHolder.logo = (ImageView) convertView.findViewById(R.id.brand_logo);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		//根据position获取分类的首字母的Char ascii值
				int section = getSectionForPosition(position);
				
				//如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
				if(position == getPositionForSection(section)){
					viewHolder.tvLetter.setVisibility(View.VISIBLE);
					viewHolder.tvLetter.setText(this.brankKList.get(position).getVehicleLetter());
				}else{
					viewHolder.tvLetter.setVisibility(View.GONE);
				}
				viewHolder.tvTitle.setText(this.brankKList.get(position).getVehicleBrank());
				if(new File(Constant.VehicleLogoPath+this.brankKList.get(position).getVehicleBrank() + ".png").exists()){
					Bitmap image = BitmapFactory.decodeFile(Constant.VehicleLogoPath+this.brankKList.get(position).getVehicleBrank() + ".png");
					viewHolder.logo.setImageBitmap(image);
				}else{
					viewHolder.logo.setImageResource(R.drawable.body_nothing_icon);
				}
		return convertView;
	}
	
	

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return brankKList.get(position).getVehicleLetter().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = brankKList.get(i).getVehicleLetter();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String  sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}
}
