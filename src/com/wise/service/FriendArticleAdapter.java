package com.wise.service;

import com.wise.wawc.FriendHomeActivity;
import com.wise.wawc.R;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendArticleAdapter extends BaseAdapter implements OnClickListener{
	private Context context;
	private LayoutInflater layoutInflater;
	private TextView publishTime;
	private EditText inputView;   //点击评论显示输入框
	private boolean isClick = false;
	private ImageView LvComment;
	public FriendArticleAdapter(Context context,EditText editText){
		this.context = context;
		this.inputView = editText;
		layoutInflater = LayoutInflater.from(context);
	}
	public int getCount() {
		return 10;
	}
	public Object getItem(int position) {
		return null;
	}
	public long getItemId(int position) {
		return 10;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = layoutInflater.inflate(R.layout.friend_article_adapter, null);
		publishTime = (TextView) convertView.findViewById(R.id.publish_time);
		publishTime.setText("11-"+(position+1));
		LvComment = (ImageView) convertView.findViewById(R.id.friend_article_comment);
		LvComment.setOnClickListener(this);
		return convertView;
	}
	
	
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.friend_article_comment:
			//编辑框不可见，设置为可见
			Log.e("onClick",String.valueOf(isClick));
			if(!isClick){
				isClick = true;
				inputView.setVisibility(View.VISIBLE);
			//编辑框可见，设置为不可见	
			}else if(isClick){
				isClick = false;
				inputView.setVisibility(View.GONE);
			}
			break;
		}
	}
}
