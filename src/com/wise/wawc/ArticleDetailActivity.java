package com.wise.wawc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.wise.data.Article;
import com.wise.extend.FaceConversionUtil;
import com.wise.pubclas.BlurImage;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.sql.DBExcute;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
/*
 * 文章详情
 */
public class ArticleDetailActivity extends Activity{
	private static final int initDatas = 3;
	ImageView goBack;
	ImageView userHead;
	ImageView articleDetaileFavorite;
	Article article;
	MyHandler myHandler;
	Bitmap bitmap;
	TextView userName;
	TextView articleContent;
	TextView publishTime;
	TableRow favoriteLayout;
	View articleDetailesLine;
	StringBuffer sb;
	ImageView favoriteStart;
	TextView praisesUser;
	TableLayout tableLayout;
	Map<String,String> favoriteMap;
	ProgressDialog myDialog;
	DBExcute dbExcute;
	ImageView saySomethig;
	LinearLayout commentView;
	boolean isShow = false;
	Button sendMessage;
	TextView articleDetailesCommentContent;
	String commentContent;
	LinearLayout commentLayout;
	TableLayout tableLayout1;
	TableLayout tableLayout3;
	private static final int addFavorite = 4;
	private static final int commentArticle = 5;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.article_detaile);
		goBack = (ImageView) findViewById(R.id.article_detaile_menu);
		userHead = (ImageView)findViewById(R.id.article_detail_user_head);
		userName = (TextView)findViewById(R.id.article_detail_user_name);
		articleContent = (TextView) findViewById(R.id.tv_article_detaile_content);
		publishTime = (TextView) findViewById(R.id.detail_article_publish_time);
		favoriteLayout = (TableRow) findViewById(R.id.article_details_favorite_layout);
		articleDetailesLine = findViewById(R.id.article_details_line);
		commentLayout = (LinearLayout) findViewById(R.id.article_details_comment_layout);
		favoriteStart = (ImageView) findViewById(R.id.article_details_praises_star);
		praisesUser = (TextView) findViewById(R.id.article_details_praises_user);
		tableLayout = (TableLayout) findViewById(R.id.article_detailes_user_image);
		articleDetaileFavorite = (ImageView) findViewById(R.id.article_detaile_favorite);
		saySomethig = (ImageView) findViewById(R.id.article_detailes_say_somthing);
		commentView = (LinearLayout) findViewById(R.id.article_detailes_comment);
		sendMessage  = (Button) findViewById(R.id.btn_send);
		articleDetailesCommentContent = (TextView) findViewById(R.id.et_sendmessage);
		tableLayout1 = (TableLayout) findViewById(R.id.article_details_user_image_tr);
		tableLayout3 = (TableLayout) findViewById(R.id.article_details_comments_tl);
		sendMessage.setOnClickListener(new ClickListener());
		dbExcute = new DBExcute();
		
		goBack.setOnClickListener(new ClickListener());
		articleDetaileFavorite.setOnClickListener(new ClickListener());
		saySomethig.setOnClickListener(new ClickListener());
		article =  (Article) getIntent().getSerializableExtra("article");
		myHandler = new MyHandler();
		Message msg = new Message();
		msg.what = initDatas;
		myHandler.sendMessage(msg);
		GetSystem.getScreenInfor(ArticleDetailActivity.this);
	}
	
	
	class MyHandler extends Handler{
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case initDatas:
				userName.setText(article.getName());
				articleContent.setText(article.getContent());
				String str = article.getCreate_time();
				String createTime = str.substring(0, str.indexOf(".")).replace("T"," ");
				publishTime.setText(getTime(createTime));
				
				//判断本地是否存在头像
				Bitmap userIcons = imageIsExist(Constant.userIconPath + article.getCust_id() + ".jpg",article.getUserLogo(),4,article.getCust_id());
				if(userIcons == null){
					userHead.setBackgroundResource(R.drawable.body_icon_help);
				}else{
					Bitmap user = BitmapFactory.decodeFile(Constant.userIconPath + article.getCust_id()+".jpg");
					userHead.setImageBitmap(BlurImage.getRoundedCornerBitmap(user));
				}
				
				//判断本地是否存在图片（动态添加图片到布局  先本地查找是否存在小图  点击小图再判断大图）
				List<Bitmap> bitMapList = new ArrayList<Bitmap>();
				List<Map<String,String>> smallImage = article.getImageList();
				for(int i = 0; i < smallImage.size() ; i ++){
					Map<String,String> imageMap = smallImage.get(i);
					String iamgeName = imageMap.get(Constant.smallImage).substring(imageMap.get(Constant.smallImage).lastIndexOf("/") + 1);
					bitMapList.add(imageIsExist(Constant.VehiclePath + iamgeName,imageMap.get(Constant.smallImage),3,0));
					Log.e("imageUrl:",imageMap.get(Constant.smallImage));
				}
				//   赞     如果没有赞 隐藏赞布局  同时将分割线隐藏
				boolean sdfsafdsaf = article.getPraisesList() == null ? true : false;
				Log.e("getPraisesList() = null",sdfsafdsaf+"");
				if (article.getPraisesList() != null) {
					if (article.getPraisesList().size() != 0) {
						sb = new StringBuffer();
						Iterator iter = article.getPraisesList().entrySet().iterator();
						while (iter.hasNext()) {
							Map.Entry entry = (Map.Entry) iter.next();
							String val = (String) entry.getValue();
							sb.append(val+",");
							if(Variable.cust_name.equals(val)){
								articleDetaileFavorite.setBackgroundResource(R.drawable.body_icon_heart_press);
							}
						}
						favoriteStart.setVisibility(View.VISIBLE);
						favoriteLayout.setVisibility(View.VISIBLE);
						praisesUser.setText(sb.toString());
					} else {
						favoriteLayout.setVisibility(View.GONE);
						articleDetailesLine.setVisibility(View.GONE);
					}
				}else{
					favoriteLayout.setVisibility(View.GONE);
					articleDetailesLine.setVisibility(View.GONE);
				}
				//   评论     如果没有评论   将 评论布局隐藏  同时将分割线隐藏
				if(article.getCommentList() != null){
					if(article.getCommentList().size() != 0){
						for(int i = 0 ; i < article.getCommentList().size() ; i ++){
							LinearLayout oneComment = new LinearLayout(ArticleDetailActivity.this);
							oneComment.setPadding(0,Variable.margins, 0, 0);
							oneComment.setOrientation(LinearLayout.HORIZONTAL);
							TextView commentName = new TextView(ArticleDetailActivity.this);  //评论者昵称
							commentName.setTextColor(Color.parseColor("#3b5197"));
						    TextView commentContent = new TextView(ArticleDetailActivity.this);   //评论内容
						    commentContent.setTextColor(Color.parseColor("#313131"));
							String[] commentStr = article.getCommentList().get(i);
							commentName.setText(commentStr[0] + ":");
							
							oneComment.addView(commentName, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
							SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(ArticleDetailActivity.this, commentStr[1]);
							commentContent.setText(spannableString);
							oneComment.addView(commentContent, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
							commentLayout.addView(oneComment, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
						}
					}else{
						commentLayout.setVisibility(View.GONE);
						articleDetailesLine.setVisibility(View.GONE);
					}
				}else{
					commentLayout.setVisibility(View.GONE);
					articleDetailesLine.setVisibility(View.GONE);
				}
				
				//动态添加用户发表的图片
				
				tableLayout.removeAllViews();
				TableRow row = new TableRow(ArticleDetailActivity.this);
				TableLayout.LayoutParams params = new TableLayout.LayoutParams(Variable.smallImageReqWidth,Variable.smallImageReqWidth);
				
				for(int i = 0; i < bitMapList.size() ; i ++){
					ImageView t = new ImageView(ArticleDetailActivity.this);
					t.setPadding(0, 0, Variable.margins, 0);
					t.setClickable(true);
					t.setId(i);
					t.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							//查看大图
							Intent intent = new Intent(ArticleDetailActivity.this,ImageActivity.class);
							intent.putExtra("article", article);
							ArticleDetailActivity.this.startActivity(intent);
						}
					});
					t.setImageBitmap(bitMapList.get(i));
					row.addView(t);
					if((i%3 + 1) == 3){
						tableLayout.addView(row,params);
						row = new TableRow(ArticleDetailActivity.this);
						row.setPadding(0, Variable.margins, 0, 0);
					}else if(i == (bitMapList.size() - 1)){
						tableLayout.addView(row,params);
					}
				}
				
				if(article.getCommentList() != null && article.getPraisesList() == null){
					tableLayout1.setVisibility(View.GONE);
					tableLayout3.setVisibility(View.GONE);
				}
				break;
			case addFavorite:
				Log.e("点赞结果：",msg.obj.toString());
				myDialog.dismiss();
				try {
					JSONObject jsonObject = new JSONObject(msg.obj.toString());
					if(Integer.valueOf(jsonObject.getString("status_code")) == 0){
						//更新数据库
						dbExcute.updateArticlePraises(ArticleDetailActivity.this, Constant.TB_VehicleFriend, article.getBlog_id(), Variable.cust_name, Integer.valueOf(Variable.cust_id));
						//更新显示信息
						if(article.getPraisesList() == null){
							Map<String,String> praisesMap = new HashMap<String, String>();
							praisesMap.put(Variable.cust_id, Variable.cust_name);
							article.setPraisesList(praisesMap);
							Log.e("没有评论者","没有评论者");
						}else{
							Map<String,String> praisesMap = article.getPraisesList();
							praisesMap.put(Variable.cust_id, Variable.cust_name);
							Log.e("有评论者","有     评论者");
							article.setPraisesList(praisesMap);
						}
						Message msgs = new Message();
						msgs.what = initDatas;
						myHandler.sendMessage(msgs);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
				
				//评论结果
			case commentArticle:
				String commentResult = msg.obj.toString();
				try {
					JSONObject jsonObject = new JSONObject(commentResult);
					Log.e("评论结果：",commentResult);
					if(Integer.valueOf(jsonObject.getString("status_code")) == 0){
						articleDetailesCommentContent.setText("");
						//隐藏键盘
						getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
						commentView.setVisibility(View.GONE);
						//更新数据库
						dbExcute.updateArticleComments(ArticleDetailActivity.this, Constant.TB_VehicleFriend, article.getBlog_id(), commentContent, Variable.cust_name, Integer.valueOf(Variable.cust_id));
						//更新数据显示
						if(article.getCommentList() == null){
							List<String[]> tempList = new ArrayList<String[]>();
							String[] commentMessage = new String[2];
							commentMessage[0] = Variable.cust_name;
							commentMessage[1] = commentContent;
							tempList.add(commentMessage);
							article.setCommentList(tempList);
						}else{
							List<String[]> tempList = article.getCommentList();
							String[] strs = new String[2];
							strs[0] = Variable.cust_name;
							strs[1] = commentContent;
							tempList.add(strs);
							article.setCommentList(tempList);
						}
						Message msgs = new Message();
						msgs.what = initDatas;
						myHandler.sendMessage(msgs);
						
						myDialog.dismiss();
						Toast.makeText(getApplicationContext(), "评论成功", 0).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}
	class ClickListener implements OnClickListener{
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.article_detaile_menu:
				ArticleDetailActivity.this.finish();
				break;
			case R.id.article_detaile_favorite:
				boolean hasFavorite = true;
				//判断当前登录用户是否已经赞过
				favoriteMap = article.getPraisesList();
				if(favoriteMap != null){
					Iterator iter = favoriteMap.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						String val = (String) entry.getValue();
						//已经赞过  不许再赞
						if(Variable.cust_name.equals(val)){
							hasFavorite = false;
						}
					}
				}
				if(hasFavorite){
					int blogId = article.getBlog_id();
					myDialog = ProgressDialog.show(ArticleDetailActivity.this, "提示","数据提交中...");
					myDialog.setCancelable(true);
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("name",Variable.cust_name));
					params.add(new BasicNameValuePair("cust_id",Variable.cust_id));
					new Thread(new NetThread.putDataThread(myHandler, Constant.BaseUrl + "blog/" + blogId +"/praise?auth_code=" + Variable.auth_code, params, addFavorite)).start();
				}else{
					hasFavorite = true;
					Toast.makeText(ArticleDetailActivity.this,"已经赞过了", 0).show();
				}
				
				break;
			case R.id.article_detailes_say_somthing:
				if(!isShow){
					commentView.setVisibility(View.VISIBLE);
					isShow = true;
				}else if(isShow){
					commentView.setVisibility(View.GONE);
					isShow = false;
				}
				break;
				
				//发表评论
			case R.id.btn_send:

				commentContent = articleDetailesCommentContent.getText().toString().trim();
				//发布到服务器/刷新文章内容显示/评论成功后清空编辑框/隐藏编辑框
				
				if("".equals(commentContent)){
					Toast.makeText(getApplicationContext(), "评论类容不能为空", 0).show();
					return;
				}else{
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("cust_id", Variable.cust_id));
					params.add(new BasicNameValuePair("name", Variable.cust_name));
					params.add(new BasicNameValuePair("content", commentContent));
					myDialog = ProgressDialog.show(ArticleDetailActivity.this, getString(R.string.dialog_title), getString(R.string.dialog_message));
					myDialog.setCancelable(true);
					new Thread(new NetThread.putDataThread(myHandler, Constant.BaseUrl + "blog/" + article.getBlog_id() + "/comment?auth_code=" + Variable.auth_code, params, commentArticle)).start();
				}
				break;
			}
		}
	}
	//判断图片是否存在SD卡   TODO
		private Bitmap imageIsExist(String path,final String loadUrl,final int action,final int custId) {
			File file = new File(path);
			if(file.exists()){
				bitmap = BitmapFactory.decodeFile(path);
				return bitmap;
			}
			else{
				new Thread(new Runnable() {
					public void run() {
						bitmap = GetSystem.getBitmapFromURL(loadUrl);
						if(bitmap != null){
						    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
		                        File imagePath = new File(Constant.VehiclePath);
		                        if(!imagePath.exists()){
		                            imagePath.mkdir();
		                        }
		                        if(action == 3){
		                        	createImage(Constant.VehiclePath + loadUrl.substring(loadUrl.lastIndexOf("/")),bitmap);
		                        }
		                        if(action == 4){
		                        	createImage(Constant.userIconPath + custId + ".jpg",bitmap);
		                        }
		                    }
						}else{
						}
					}
				}).start();
				return null;
			}
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
			Message msg = new Message();
			msg.what = initDatas;
			myHandler.sendMessage(msg);
			Log.e("创建图片","创建图片");
		}
		
		

		public static String getTime(String time){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String currentTime = sdf.format(new Date());
			String time1 =  transform(time);
		     if(Integer.parseInt(time1.substring(0,4)) < Integer.parseInt(currentTime.substring(0,4))){
		    	 return time1.substring(0, 16);
		     }else{
		    	 if((Integer.parseInt(currentTime.substring(8,10)) - Integer.parseInt(time1.substring(8,10))) == 1){
		    		 return "昨天" + time1.substring(11, 16);
		    	 }else if((Integer.parseInt(currentTime.substring(8,10)) - Integer.parseInt(time1.substring(8,10))) == 2){
		    		 return "前天" + time1.substring(11, 16);
		    	 }else if((Integer.parseInt(currentTime.substring(8,10)) == Integer.parseInt(time1.substring(8,10)))){
		    		 return time1.substring(11, 16);
		    	 }
		    	 return time1.substring(5, 16);
		     }
	}

	// 转换时区
	public static String transform(String from) {
		String to = "";
		SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 本地时区
		Calendar nowCal = Calendar.getInstance();
		TimeZone localZone = nowCal.getTimeZone();
		// 设定SDF的时区为本地
		simple.setTimeZone(localZone);

		SimpleDateFormat simple1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 设置 DateFormat的时间区域为GMT
		simple1.setTimeZone(TimeZone.getTimeZone("GMT"));

		// 把字符串转化为Date对象，然后格式化输出这个Date
		Date fromDate = new Date();
		try {
			// 时间string解析成GMT时间
			fromDate = simple1.parse(from);
			// GMT时间转成当前时区的时间
			to = simple.format(fromDate);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		return to;
	}
}
