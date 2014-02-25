package com.wise.wawc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WapZfbActivity extends Activity {
	/** Called when the activity is first created. */
	private WebView myWebView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wap);
		String redirect = getIntent().getStringExtra("redirect");
		myWebView = (WebView) findViewById(R.id.myWebView);
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.loadUrl(redirect);
		//myWebView.loadUrl("http://wiwc.api.wisegps.cn/pay/callback?out_trade_no=3014011100000008008406&request_token=requestToken&result=success&trade_no=2014022425693457&sign=3148bcb0476f64b003d217cef467344c&sign_type=MD5");
		myWebView.addJavascriptInterface(new JSInvokeClass(),"android");
		myWebView.setWebViewClient(new myWebViewClient());
		myWebView.setOnTouchListener(new OnTouchListener() {            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                myWebView.requestFocus();
                return false;
            }
        });
	}
	
	class JSInvokeClass {
        @JavascriptInterface
        public void goActivity(){
            Toast.makeText(WapZfbActivity.this, "goActivity", Toast.LENGTH_SHORT).show();
            System.out.println("goActivity");
        }
        public void clickOnAndroid(){
            System.out.println("clickOnAndroid");
        }
    }

	// 此按键监听的是返回键，能够返回到上一个网页（通过网页的hostlistery）
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
			myWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	class myWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			view.loadUrl(url);
			return true;
		}

	}
}