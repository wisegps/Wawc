package com.wise.service;
import com.wise.wawc.ArticleDetailActivity;
import com.wise.wawc.FriendHomeActivity;
import com.wise.wawc.FriendInformationActivity;
import com.wise.wawc.ImageActivity;
import com.wise.wawc.R;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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
	private TextView detailArticle;  //点击查看详细信息
	private TableLayout tableLayout;  //用户发表的图片
	private TableRow tableRow;
	private ImageView imageView;
	private int imageNumber = 0;
	
	int padding = 40;
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
		//动态添加用户发表的图片
		
		TableLayout table = (TableLayout) convertView.findViewById(R.id.user_image);
		TableRow row = new TableRow(context);
		for(int i = 0 ; i < 9 ; i ++){
			ImageView t = new ImageView(context);
			t.setClickable(true);
			t.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					context.startActivity(new Intent(context,ImageActivity.class));
				}
			});
			t.setBackgroundResource(R.drawable.image);
			t.setId(i);
			if((i%3 + 1) == 3){
				row.addView(t);
				table.addView(row,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				row = new TableRow(context);
			}else{
				row.addView(t);
			}
		}
		
		saySomething = (ImageView) convertView.findViewById(R.id.list_say_somthing);
		userHead = (ImageView) convertView.findViewById(R.id.head_article);
		detailArticle = (TextView) convertView.findViewById(R.id.detail_article);
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
			Log.e("进入文章详情","进入文章详情");
			context.startActivity(new Intent(context,ArticleDetailActivity.class));
			break;
		case 1:
//			context.startActivity(new Intent(context,ImageActivity.class));
			break;
		}
	}
}
