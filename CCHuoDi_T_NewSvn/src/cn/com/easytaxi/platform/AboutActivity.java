package cn.com.easytaxi.platform;

 
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.ETApp;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.client.common.ConfigUtil;
import cn.com.easytaxi.common.NetChecker;
import cn.com.easytaxi.common.SessionAdapter;
import cn.com.easytaxi.onetaxi.TitleBar;

public class AboutActivity extends WebBaseActivity {
	
	private static final String ABOUT_HTML = "file:///android_asset/about_us.html";
//	private static final String ABOUT_HTML = "file:///android_asset/Untitled-4.html";
	
	private TitleBar bar;
	private WebView webView;
	private SessionAdapter session;
	String mobile;// 本机电话号码
	private String versionName;
	private Button btn;
	
	
	protected void initUserData() {
		session = new SessionAdapter(AboutActivity.this);
		mobile = session.get("_MOBILE");
		versionName = ETApp.getInstance().getMobileInfo().getVerisonName();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.p_about);
		btn = (Button) findViewById(R.id.textView_refresh_again);
		bar = new TitleBar(AboutActivity.this);
		bar.setTitleName("关于我们");
		initUserData();
		
		PackageManager pm = this.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = pm.getPackageInfo(this.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				loadUrl();
			}
		});
		
		
		webView = (WebView) findViewById(R.id.about_myinfo);
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
			
			webView.loadUrl(ABOUT_HTML);
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (session != null) {
			session.close();
			session = null;
		}
		bar.close();
		super.onDestroy();
		
	}
}
