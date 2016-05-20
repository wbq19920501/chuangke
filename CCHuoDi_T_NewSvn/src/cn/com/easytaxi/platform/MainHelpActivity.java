package cn.com.easytaxi.platform;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import cn.com.easytaxi.ETApp;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.client.common.ConfigUtil;
import cn.com.easytaxi.common.NetChecker;
import cn.com.easytaxi.onetaxi.TitleBar;

public class MainHelpActivity extends WebBaseActivity {
	MainHelpActivity self;
	TitleBar bar;
	private WebView webView;
	private String verisonName;
	private Button btn;
	
	protected void initUserData() {
		verisonName = ETApp.getInstance().getMobileInfo().getVerisonName();
		webView = (WebView) findViewById(R.id.help);
		WebSettings webSettings = webView.getSettings();
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(false);
		webView.setWebChromeClient(new WebChromeClient());
		setWebViewClient(webView, btn);
		loadUrl();
	}
	
	
	private void loadUrl() {

		if (NetChecker.getInstance(this.getApplicationContext()).isAvailableNetwork()) {

			btn.setVisibility(View.INVISIBLE);
			String cityId = getCityId();
			if(TextUtils.isEmpty(cityId)){
				cityId = "1";
			}
			webView.loadUrl(ConfigUtil.WEB_URL+"About!showPassengerHelp?version="+verisonName+"&cityId="+cityId);
			 
			webView.setVisibility(View.VISIBLE);

		} else {

			webView.setVisibility(View.INVISIBLE);
			btn.setVisibility(View.VISIBLE);
			btn.setText(R.string.network_notgood);
			// webView.loadUrl(url);
		}
		// TODO Auto-generated method stub

	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.p_help);
		self = this;
		btn = (Button) findViewById(R.id.textView_refresh_again);
		bar = new TitleBar(self);
		bar.setTitleName("°ïÖú");
		initUserData();
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				loadUrl();
			}
		});
		

	}

 

	@Override
	protected void onDestroy() {
		bar.close();
		super.onDestroy();
	}
}
