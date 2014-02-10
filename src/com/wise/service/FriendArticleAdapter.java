package com.wise.service;

import java.util.List;

import com.wise.data.Article;
import com.wise.extend.FaceConversionUtil;
import com.wise.wawc.FriendHomeActivity;
import com.wise.wawc.R;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FriendArticleAdapter extends BaseAdapter implements OnClickListener{
	private Context context;
	private LayoutInflater layoutInflater;
	private TextView publishTime;
	private EditText inputView;   //点击评论显示输入框
	private ImageView favoriteStart = null;
	private TextView favoriteUser = null;
	
	private TextView articleContent = null;
	private boolean isClick = false;
	private ImageView LvComment;
	private List<Article> articleList = null;
	
	private StringBuffer sb = null;
	
//	friend_article_praises_layout
	public FriendArticleAdapter(Context context,EditText editText,List<Article> articleList){
		this.context = context;
		this.inputView = editText;
		this.articleList = articleList;
		layoutInflater = LayoutInflater.from(context);
	}
	public int getCount() {
		return articleList.size();
	}
	public Object getItem(int position) {
		return articleList.get(position);
	}
	public long getItemId(int position) {
		return position;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = layoutInflater.inflate(R.layout.friend_article_adapter, null);
		publishTime = (TextView) convertView.findViewById(R.id.publish_time);
		articleContent = (TextView) convertView.findViewById(R.id.friend_article_content);
		favoriteStart = (ImageView) convertView.findViewById(R.id.friend_article_praises_star);
		favoriteUser = (TextView)convertView.findViewById(R.id.friend_article_praises_user);
		String str = articleList.get(position).getCreate_time();
		String createTime = str.substring(0, str.indexOf(".")).replace("T"," ");
		articleContent.setText(articleList.get(position).getContent());
		publishTime.setText(MyAdapter.getTime(createTime));
		//   TODO   显示图片    文章评论
		//动态添加用户的评论
				LinearLayout commentLayout = (LinearLayout) convertView.findViewById(R.id.article_comment_layout);
				for(int i = 0 ; i < articleList.get(position).getCommentList().size() ; i ++){
					LinearLayout oneComment = new LinearLayout(context);
					oneComment.setOrientation(LinearLayout.HORIZONTAL);
					TextView commentName = new TextView(context);
				    TextView commentContent = new TextView(context);
					String[] commentStr = articleList.get(position).getCommentList().get(i);
					commentName.setText(commentStr[0] + ":");
					oneComment.addView(commentName, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(context, commentStr[1]);
					commentContent.setText(spannableString);
					oneComment.addView(commentContent, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					commentLayout.addView(oneComment, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				}
				//点赞者
				if(articleList.get(position).getPraisesList() != null){
					if(articleList.get(position).getPraisesList().size() != 0){
						sb = new StringBuffer();
						for(int f = 0 ; f < articleList.get(position).getPraisesList().size(); f ++){
							sb.append(articleList.get(position).getPraisesList().get(f) + "、");
						}
						favoriteStart.setVisibility(View.VISIBLE);
						favoriteUser.setText(sb.toString());
					}else{
						favoriteStart.setVisibility(View.GONE);
					}
				}else{
					favoriteStart.setVisibility(View.GONE);
				}
		
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
	public void refreshDates(List<Article> articleList){ 
		this.articleList = articleList;
		this.notifyDataSetChanged();
	}
}
