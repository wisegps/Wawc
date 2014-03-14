package com.wise.wawc;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
/**
 * 汽油标号选择
 * @author Mr.Wang
 */
public class PetrolGradeActivity extends Activity {
	ListView petrolGrade = null;
	List<String> petrolGradeList = new ArrayList<String>();
	MyAdapter myAdapter = null;
	LayoutInflater inflater = null;
	int actionCode = 0;
	ImageView cancle = null;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.petrol_grade);
		actionCode = getIntent().getIntExtra("code", 0);
		petrolGrade = (ListView) findViewById(R.id.petrol_grade_lv);
		cancle = (ImageView) findViewById(R.id.choice_petrol_cancle);
		petrolGradeList.add("90#");
		petrolGradeList.add("93#(京92#)");
		petrolGradeList.add("97#(京95#)");
		petrolGradeList.add("0#");
		myAdapter = new MyAdapter();
		petrolGrade.setAdapter(myAdapter);
		petrolGrade.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				String chickItem = petrolGradeList.get(arg2);
				Intent intent = new Intent();
				intent.putExtra("result", chickItem);
				PetrolGradeActivity.this.setResult(actionCode, intent);
				PetrolGradeActivity.this.finish();
			}
		});
		
		cancle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				PetrolGradeActivity.this.finish();
			}
		});
	}
	
	
	class MyAdapter extends BaseAdapter{
		ViewHolder viewHolder = null;
		public int getCount() {
			return petrolGradeList.size();
		}
		public Object getItem(int position) {
			return petrolGradeList.get(position);
		}
		public long getItemId(int position) {
			return position;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			inflater = LayoutInflater.from(PetrolGradeActivity.this);
			if(convertView == null){
				convertView = inflater.inflate(R.layout.item_petrol, null);
				viewHolder = new ViewHolder();
				viewHolder.petRolName = (TextView) convertView.findViewById(R.id.petrol_name);
				viewHolder.petRolName.setText(petrolGradeList.get(position));
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			return convertView;
		}
		class ViewHolder{
			TextView petRolName = null;
		}
	}
}
