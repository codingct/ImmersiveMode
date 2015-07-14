package com.immersive.helper;

import android.app.Activity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.code.immersivemode.R;

public class CommunityHelper {
	private View mLayout = null;
	private Activity mActivity = null;
	private WebView mWeb = null;
	public CommunityHelper(View layout, Activity activity) {
		this.mLayout = layout;
		this.mActivity = activity;
	}
	public void init() {
		mWeb = (WebView) mLayout.findViewById(R.id.webview);
		mWeb.loadUrl("http://sunriser.gotoip55.com/Monitor/counter/index.html");
		//启用支持javascript
		WebSettings settings = mWeb.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(false);
		settings.setUseWideViewPort(true);
		
		mWeb.setWebViewClient(new WebViewClient(){
	           @Override
	        public boolean shouldOverrideUrlLoading(WebView view, String url) {
	            // TODO Auto-generated method stub
	               //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
	             view.loadUrl(url);
	            return true;
	        }
	       });
	}
}
