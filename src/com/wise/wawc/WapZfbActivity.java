package com.wise.wawc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
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
		myWebView.addJavascriptInterface(new JSInvokeClass(),"android");
		myWebView.setWebViewClient(new myWebViewClient());

	}
	
	class JSInvokeClass {
        @JavascriptInterface
        public void goActivity(){
            Toast.makeText(WapZfbActivity.this, "goActivity", Toast.LENGTH_SHORT).show();
            System.out.println("goActivity");
        }
        public void clickOnAndroid(){
            System.out.println("clickOnAndroid");
            Toast.makeText(WapZfbActivity.this, "clickOnAndroid", Toast.LENGTH_SHORT).show();
        } 
        public void showToast(String toast) {
            Toast.makeText(WapZfbActivity.this, toast, Toast.LENGTH_SHORT).show();
            System.out.println("goActivity");
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

	public String getFromAssets(String fileName) {
		try {
			InputStreamReader inputReader = new InputStreamReader(
					getResources().getAssets().open(fileName));

			BufferedReader bufReader = new BufferedReader(inputReader);

			String line = "";
			String Result = "";

			while ((line = bufReader.readLine()) != null)
				Result += line;
			if (bufReader != null)
				bufReader.close();
			if (inputReader != null)
				inputReader.close();
			return Result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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