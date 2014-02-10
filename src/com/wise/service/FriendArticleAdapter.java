package com.wise.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.wise.data.Article;
import com.wise.extend.FaceConversionUtil;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.sql.DBExcute;
import com.wise.wawc.FriendHomeActivity;
import com.wise.wawc.ImageActivity;
import com.wise.wawc.R;
import com.wise.wawc.VehicleFriendActivity;

import android.app.ProgressDialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class FriendArticleAdapter extends BaseAdapter{
	private Context context;
	private LayoutInflater layoutInflater;
	private TextView publishTime;
	private LinearLayout linearLayout;   //点击评论显示输入框
	private ImageView favoriteStart = null;
	private TextView favoriteUser = null;
	private Bitmap bitmap = null;
	private ImageView addFavorite = null;
	
	private TextView articleContent = null;
	public static boolean isClick = false;
	private ImageView LvComment;
	private List<Article> articleList = null;
	
	private StringBuffer sb = null;
	private OnClickListeners listener = null;
	private ProgressDialog myDialog = null;
	private MyHandler myHandler = null;
	private DBExcute dbExcute= null;
	private FriendHomeActivity friendHomeActivity = null;
	private static final int articleFavorite = 2;
	private int blogId = 0;
	
	public FriendArticleAdapter(Context context,LinearLayout linearLayout,List<Article> articleList){
		this.context = context;
		this.linearLayout = linearLayout;
		this.articleList = articleList;
		layoutInflater = LayoutInflater.from(context);
		listener = new OnClickListeners(0);
		myHandler = new MyHandler();
		dbExcute = new DBExcute();
		friendHomeActivity = new FriendHomeActivity();
		for(int i = 0 ; i < this.articleList.size() ; i ++){
			Log.e("iiiii",this.articleList.get(i).getBlog_id()+"");
		}
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		convertView = layoutInflater.inflate(R.layout.friend_article_adapter, null);
		publishTime = (TextView) convertView.findViewById(R.id.friend_article_publish_time);
		articleContent = (TextView) convertView.findViewById(R.id.friend_article_content);
		favoriteStart = (ImageView) convertView.findViewById(R.id.friend_article_praises_star);
		favoriteUser = (TextView)convertView.findViewById(R.id.friend_article_praises_user);
		addFavorite = (ImageView) convertView.findViewById(R.id.friend_article_praises);
		addFavorite.setOnClickListener(new OnClickListeners(position));
		String str = articleList.get(position).getCreate_time();
		String createTime = str.substring(0, str.indexOf(".")).replace("T"," ");
		articleContent.setText(articleList.get(position).getContent());
		publishTime.setText(MyAdapter.getTime(createTime));
		//动态显示用户发表的图片
		List<Bitmap> smallImageList = new ArrayList<Bitmap>();
		for (int i = 0; i < articleList.get(position).getImageList().size(); i++) {
			Map<String, String> imageMap = articleList.get(position)
					.getImageList().get(i);
			// 判断小图是否存在sd卡 /点击小图的时候再判断是否存在sd卡
			String smallImage = imageMap.get("big_pic").substring(imageMap.get("big_pic").lastIndexOf("/"));
			Bitmap smallBitmap = imageIsExist(Constant.VehiclePath + smallImage,imageMap.get("small_pic"));
			smallImageList.add(smallBitmap);
		}
		TableLayout table = (TableLayout) convertView.findViewById(R.id.friend_home_image);
		TableRow row = new TableRow(context);
		for (int i = 0; i < smallImageList.size(); i++) {
			ImageView t = new ImageView(context);
			t.setClickable(true);
			t.setId(i);
			t.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(context, ImageActivity.class);
					intent.putExtra("position", position);
					context.startActivity(intent);
				}
			});
			t.setImageBitmap(smallImageList.get(i));
			row.addView(t);
			if ((i % 3 + 1) == 3) {
				table.addView(row, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				row = new TableRow(context);
			} else if (i == (smallImageList.size() - 1)) {
				table.addView(row, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			}
		}
		//动态添加用户的评论
				LinearLayout commentLayout = (LinearLayout) convertView.findViewById(R.id.friend_article_comment_layout);
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
		LvComment.setOnClickListener(new OnClickListeners(position));
		return convertView;
	}
	
	
	class OnClickListeners implements OnClickListener{
		int position = 0;
		OnClickListeners(int position){
			this.position = position;
		}
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.friend_article_comment:
				FriendHomeActivity.blogId = articleList.get(this.position).getBlog_id();
				//编辑框不可见，设置为可见
				Log.e("onClick",String.valueOf(isClick));
				if(!isClick){
					isClick = true;
					linearLayout.setVisibility(View.VISIBLE);
				//编辑框可见，设置为不可见	
				}else if(isClick){
					isClick = false;
					linearLayout.setVisibility(View.GONE);
				}
				break;
			case R.id.friend_article_praises:  //  点赞
				FriendArticleAdapter.this.blogId = articleList.get(this.position).getBlog_id();
				myDialog = ProgressDialog.show(context, "提示","数据提交中...");
				myDialog.setCancelable(true);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("name",Variable.cust_name));
				params.add(new BasicNameValuePair("cust_id",Variable.cust_id));
				new Thread(new NetThread.putDataThread(myHandler, Constant.BaseUrl + "blog/" + blogId +"/praise?auth_code=" + Variable.auth_code, params, articleFavorite)).start();
				Log.e("点赞url:",Constant.BaseUrl + "blog/" + blogId +"/praise?auth_code=" + Variable.auth_code);
				break;
			}
		}
	}
	
	class MyHandler extends Handler{
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case articleFavorite:
				String result = msg.obj.toString();
				try {
					JSONObject jsonObject = new JSONObject(result);
					if(Integer.valueOf(jsonObject.getString("status_code")) == 0){
						
						//更新数据库
						dbExcute.updateArticlePraises(context, Constant.TB_VehicleFriend, blogId, Variable.cust_name, Integer.valueOf(Variable.cust_id));
						
						VehicleFriendActivity vehicleFriendActivity = new VehicleFriendActivity();
						Log.e("---->",Constant.start + "");
						//更新列表
						List<Article> oldArticlList = vehicleFriendActivity.getArticleDataList();
						oldArticlList.clear();
						vehicleFriendActivity.setArticleDataList(oldArticlList);
						List<Article> newArticlList = FriendArticleAdapter.this.dbExcute.getArticlePageDatas(context, "select * from " + Constant.TB_VehicleFriend + " order by Blog_id desc limit ?,?", new String[]{String.valueOf(0),String.valueOf(Constant.start1 + Constant.pageSize)}, vehicleFriendActivity.getArticleDataList());
						Variable.articleList = newArticlList;
						vehicleFriendActivity.setArticleDataList(newArticlList);
						FriendArticleAdapter.this.refreshDates(newArticlList);
						myDialog.dismiss();
						
						Toast.makeText(context, "点赞成功", 0).show();
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}
	
	private Bitmap imageIsExist(String path,final String loadUrl) {
		File file = new File(path);
		if(file.exists()){
			bitmap = BitmapFactory.decodeFile(path);
		}else{
			new Thread(new Runnable() {
				public void run() {
					bitmap = GetSystem.getBitmapFromURL(loadUrl);
					if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
						File imagePath = new File(Constant.VehiclePath);
						if(!imagePath.exists()){
							imagePath.mkdir();
						}
						createImage(Constant.VehiclePath + loadUrl.substring(loadUrl.lastIndexOf("/")),bitmap);
					}
				}
			}).start();
		}
		return bitmap;
	}
	
	public void createImage(String fileName,Bitmap bitmap){
		FileOutputStream b = null;
		try {  
            b = new FileOutputStream(fileName);  
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                b.flush();  
                b.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }
	}
	public void refreshDates(List<Article> articleList){ 
		this.articleList = articleList;
		this.notifyDataSetChanged();
	}
}
