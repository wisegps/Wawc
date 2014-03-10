package com.wise.wawc;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import com.wise.pubclas.Constant;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class FeedBackActivity extends Activity {
    private static final int feedBack = 1;
	EditText et_content,et_qq;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed_back);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		Button bt_send = (Button)findViewById(R.id.bt_send);
		bt_send.setOnClickListener(onClickListener);
		ImageView iv_back = (ImageView)findViewById(R.id.iv_back);
		iv_back.setOnClickListener(onClickListener);
		et_content = (EditText)findViewById(R.id.et_content);
		et_qq = (EditText)findViewById(R.id.et_qq);		
	}
	OnClickListener onClickListener = new OnClickListener() {        
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.bt_send:
                String content = et_content.getText().toString().trim();
                if(content.equals("")){
                    Toast.makeText(FeedBackActivity.this, "反馈的内容不能为空", Toast.LENGTH_SHORT).show();
                    break;
                }
                String url = Constant.BaseUrl + "feedback?auth_code=" + Variable.auth_code;
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("content", content));
                params.add(new BasicNameValuePair("contact", et_qq.getText().toString().trim()));
                params.add(new BasicNameValuePair("cust_id", Variable.cust_id == null ? "0":Variable.cust_id));
                new Thread(new NetThread.putDataThread(handler, url, params, feedBack)).start();
                break;
            case R.id.iv_back:
                finish();
                break;
            }
        }
    };
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case feedBack:
                Toast.makeText(FeedBackActivity.this, "意见反馈成功", Toast.LENGTH_SHORT).show();
                break;
            }
        }        
    };
}
