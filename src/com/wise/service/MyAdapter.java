package com.wise.service;
import java.util.List;

import com.wise.data.Article;
import com.wise.wawc.ArticleDetailActivity;
import com.wise.wawc.FriendHomeActivity;
import com.wise.wawc.FriendInformationActivity;
import com.wise.wawc.ImageActivity;
import com.wise.wawc.R;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.ContentValues;
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
	public static boolean isClick = false;
	private TextView articel_user_name;  //点击查看详细信息
	private TextView tv_article_content;
	private TextView publish_time;
	private TableLayout tableLayout;  //用户发表的图片
	private TableRow tableRow;
	private ImageView imageView;
	private int imageNumber = 0;
	
	private List<Article> articleList;
	
	int padding = 40;
	public MyAdapter(Context context,View v,List<Article> articleList){
		inflater=LayoutInflater.from(context);
		this.view = v;
		this.context = context;
		this.articleList = articleList;
	}
	public int getCount() {
		return 10;
//		return articleList.size();
	}
	public Object getItem(int position) {
		return null;
	}
	public long getItemId(int position) {
		return position;
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
		articel_user_name = (TextView) convertView.findViewById(R.id.article_user_name);
		tv_article_content = (TextView) convertView.findViewById(R.id.tv_article_content);
		publish_time = (TextView) convertView.findViewById(R.id.publish_time);
		
//		publish_time.setText(getTime(articleList.get(position).getPublish_time()));
//		articel_user_name.setText(articleList.get(position).getPublish_user());
//		tv_article_content.setText(articleList.get(position).getPublish_content());
		
		saySomething.setOnClickListener(this);
		userHead.setOnClickListener(this);
		articel_user_name.setOnClickListener(this);
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
		case R.id.article_user_name:   //点击进入文章的详细介绍
			Log.e("进入文章详情","进入文章详情");
			context.startActivity(new Intent(context,ArticleDetailActivity.class));
			break;
		case 1:
//			context.startActivity(new Intent(context,ImageActivity.class));
			break;
		}
	}
	
	public String getTime(String time){
	     String currentTime = "2014-06-21 18:28:14";
	     if(Integer.parseInt(time.substring(0,4)) < Integer.parseInt(currentTime.substring(0,4))){
	    	 return time.substring(0, 16);
	     }else{
	    	 if((Integer.parseInt(currentTime.substring(8,10)) - Integer.parseInt(time.substring(8,10))) == 1){
	    		 return "昨天" + time.substring(11, 16);
	    	 }else if((Integer.parseInt(currentTime.substring(8,10)) - Integer.parseInt(time.substring(8,10))) == 2){
	    		 return "前天" + time.substring(11, 16);
	    	 }
	    	 return time.substring(5, 16);
	     }
	}
	
	public void refreshDates(List<Article> articleList){ 
		this.articleList.clear();
		this.articleList = articleList;
		this.notifyDataSetChanged();
	}
}
