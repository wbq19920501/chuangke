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
import cn.com.easytaxi.common.SessionAdapter;
import cn.com.easytaxi.onetaxi.TitleBar;

public class MainMsgManageActivity extends WebBaseActivity {
	MainMsgManageActivity self;
	TitleBar bar;
	private WebView webView;
	private String verisonName;
	private SessionAdapter session;
	String mobile;// 本机电话号码
	
	protected void initUserData() {
		session = new SessionAdapter(MainMsgManageActivity.this);
		mobile = session.get("_MOBILE");
		
		verisonName = ETApp.getInstance().getMobileInfo().getVerisonName();
		webView = (WebView) findViewById(R.id.help);
		WebSettings webSettings = webView.getSettings();
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(false);
		webView.setWebChromeClient(new WebChromeClient());
		loadUrl();
	}
	private Button btn;
	
	private void loadUrl() {

		if (NetChecker.getInstance(this.getApplicationContext()).isAvailableNetwork()) {

			btn.setVisibility(View.INVISIBLE);
			String cityId = getCityId();
			if(TextUtils.isEmpty(cityId)){
				cityId = "1";
			}
			 
			webView.loadUrl(ConfigUtil.WEB_URL+"Message!showPassengerInfo?id="+mobile+"&cityId="+cityId);
			 
			webView.setVisibility(View.VISIBLE);

		} else {

			webView.setVisibility(View.INVISIBLE);
			btn.setVisibility(View.VISIBLE);
			// webView.loadUrl(url);
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.p_help);
		self = this;
		btn = (Button) findViewById(R.id.textView_refresh_again);
		bar = new TitleBar(self);
		bar.setTitleName("消息管理");
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
		if(session != null){
			session.close();
			session = null;
		}
		bar.close();
		super.onDestroy();
	}
}
