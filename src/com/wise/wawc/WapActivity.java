package com.wise.wawc;

import android.app.Activity;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

public class WapActivity extends Activity{
	private WebView shareView = null;
	private ImageView shareCancle = null;
	private TextView title = null;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wap_note);
		Intent intent = getIntent();
		shareView = (WebView) findViewById(R.id.share_web);
		shareCancle = (ImageView) findViewById(R.id.share_gift_back);
		title = (TextView) findViewById(R.id.web_title);
		title.setText(intent.getStringExtra("Title"));
		shareCancle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				WapActivity.this.finish();
			}
		});
		//设置网页支持javaScript
		shareView.getSettings().setJavaScriptEnabled(true);
		shareView.requestFocus();   //设置可获取焦点
		shareView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);  //取消滚动条
		shareView.loadUrl(intent.getStringExtra("url"));
		shareView.setWebChromeClient(new WebChromeClient(){
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				 // 这里是设置activity的标题， 也可以根据自己的需求做一些其他的操作
			}
			
		});
		shareView.setWebViewClient(new WebViewClient() {
			@Override
			// 表示点击页面的链接不跳转到系统浏览器
			public boolean shouldOverrideUrlLoading(WebView view, String url) { // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
				view.loadUrl(url);
				return true;
			}
			//让WebView支持https请求
			public void onReceivedSslError(WebView view,SslErrorHandler handler, SslError error) {
				super.onReceivedSslError(view, handler, error);
				handler.proceed();
			}
		});
	}
	
	//覆盖back按键  在网页中返回上一个页面
//	public boolean onKeyDown(int keyCoder,KeyEvent event){
//        if(shareView.canGoBack() && keyCoder == KeyEvent.KEYCODE_BACK){
//        	shareView.goBack();   //goBack()表示返回webView的上一页面
//                 return true;
//           }
//        return false;
//   }
}
