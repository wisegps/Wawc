package com.wise.service;
import com.wise.wawc.FriendHomeActivity;
import com.wise.wawc.FriendInformationActivity;
import com.wise.wawc.R;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 车友圈文章列表
 * @author 王庆文
 */
public class MyAdapter extends BaseAdapter implements OnClickListener{
	private LayoutInflater inflater;
	private ImageView saySomething;
	private ImageView userHead;
	private View view;
	private Context context;
	public boolean isClick = false;
	private ImageView detailArticle;  //点击查看详细信息
	public MyAdapter(Context context,View v){
		inflater=LayoutInflater.from(context);
		this.view = v;
		this.context = context;
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
		convertView = inflater.inflate(R.layout.article_adapter, null);
		saySomething = (ImageView) convertView.findViewById(R.id.list_say_somthing);
		userHead = (ImageView) convertView.findViewById(R.id.head_article);
		detailArticle = (ImageView) convertView.findViewById(R.id.detail_article);
		saySomething.setOnClickListener(this);
		userHead.setOnClickListener(this);
		detailArticle.setOnClickListener(this);
		return convertView;
	}
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.list_say_somthing:
			//编辑框不可见，设置为可见
			Log.e("onClick",String.valueOf(isClick));
			if(!isClick){
				isClick = true;
				view.setVisibility(View.VISIBLE);
			//编辑框可见，设置为不可见	
			}else if(isClick){
				isClick = false;
				view.setVisibility(View.GONE);
			}
			break;
		case R.id.head_article:   //点击用户头像 进入好友主页
			context.startActivity(new Intent(context,FriendHomeActivity.class));
			break;
		case R.id.detail_article:   //点击进入文章的详细介绍
			
			break;
		}
	}
}
