package com.wise.wawc;
import com.wise.service.FriendArticleAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
/**
 * 好友主页
 * @author 王庆文
 */
public class FriendHomeActivity extends Activity {
	private ListView friendArticleList;
	private FriendArticleAdapter friendArticleAdapter;
	private EditText saySomething;
	private ImageView friendHead;    //点击好友头像显示资料
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_home);
		friendArticleList = (ListView) findViewById(R.id.friend_home_article_list);
		saySomething = (EditText) findViewById(R.id.friend_home_say_something);
		friendArticleAdapter = new FriendArticleAdapter(FriendHomeActivity.this,saySomething);
		friendArticleList.setAdapter(friendArticleAdapter);
		friendHead = (ImageView) findViewById(R.id.friend_home_user_head);
		friendHead.setOnClickListener(new OnClickListener());
	}
	
	class OnClickListener implements android.view.View.OnClickListener{
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.friend_home_user_head:
				startActivity(new Intent(FriendHomeActivity.this,
						FriendInformationActivity.class));
				break;
			default:
				return;
			}
		}
	}
}
