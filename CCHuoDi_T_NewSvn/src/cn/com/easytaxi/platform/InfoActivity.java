package cn.com.easytaxi.platform;

import java.text.DecimalFormat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.client.common.ConfigUtil;
import cn.com.easytaxi.common.NetChecker;
import cn.com.easytaxi.common.SessionAdapter;
import cn.com.easytaxi.onetaxi.TitleBar;

public class InfoActivity extends WebBaseActivity {

	private InfoActivity self = this;
	private TitleBar bar;
	private String mobile;
	private String name;
	private String sex;

	 

	private String[] levels;
	private DecimalFormat df = new DecimalFormat("#0.000");

	private SessionAdapter dao;
	
	private WebView webView;
	private Button btn;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.p_info);
		btn = (Button) findViewById(R.id.textView_refresh_again);
		levels = getResources().getStringArray(R.array.levels);
		bar = new TitleBar(self);
		bar.setTitleName("Œ“µƒ’Àªß");
		
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				loadUrl();
			}
		});
		
		
		
		dao = new SessionAdapter(self);
		mobile = dao.get("_MOBILE");
		name = dao.get("_NAME");
		sex = dao.get("_SEX");
		 
		
		initUserData();
		
	 

	 
	}
	
	private void loadUrl() {

		if (NetChecker.getInstance(this.getApplicationContext()).isAvailableNetwork()) {

			btn.setVisibility(View.INVISIBLE);
			String cityId = getCityId();
			if(TextUtils.isEmpty(cityId)){
				cityId = "1";
			}
			webView.loadUrl(ConfigUtil.WEB_URL+"Me!showPassengerInfo1?id="+mobile+"&cityId="+cityId);
			 
			webView.setVisibility(View.VISIBLE);

		} else {

			webView.setVisibility(View.INVISIBLE);
			btn.setVisibility(View.VISIBLE);
			btn.setText(R.string.network_notgood);
			// webView.loadUrl(url);
		}
		// TODO Auto-generated method stub

	}

	protected void initUserData() {
 
		webView = (WebView) findViewById(R.id.my_info);
		WebSettings webSettings = webView.getSettings();
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(false);
		webView.setWebChromeClient(new WebChromeClient());
		setWebViewClient(webView, btn);
		loadUrl();
		
	}

	@Override
	protected void onDestroy() {
		bar.close();
		if (dao != null) {
			dao.close();
			dao = null;
		}
		super.onDestroy();
	}

}
