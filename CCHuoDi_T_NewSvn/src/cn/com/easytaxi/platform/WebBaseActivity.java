package cn.com.easytaxi.platform;

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class WebBaseActivity extends YdBaseActivity {
	protected void setWebViewClient(WebView webView , final Button progressInfo){
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				if (progressInfo != null) {
					progressInfo.setVisibility(View.VISIBLE);
					progressInfo.setText("Мгдижа...");
				}
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if (progressInfo != null) {
					progressInfo.setVisibility(View.GONE);
				}
			}
		});
	}

	@Override
	protected void initViews() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initListeners() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initUserData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void regReceiver() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void unRegReceiver() {
		// TODO Auto-generated method stub
		
	}
}
