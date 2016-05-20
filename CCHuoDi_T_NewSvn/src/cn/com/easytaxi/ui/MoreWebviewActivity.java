package cn.com.easytaxi.ui;

import android.content.Intent;
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
import cn.com.easytaxi.common.NetChecker;
import cn.com.easytaxi.onetaxi.TitleBar;
import cn.com.easytaxi.platform.WebBaseActivity;

public class MoreWebviewActivity extends WebBaseActivity {

	public static final int TYPE_NEW_MESSAGE = 1;
	private TitleBar bar;
	private WebView webView;
	private Button btn;
	private String title;
	private String uri;
	private int uriType;

	public static final String TITLE = "title";
	public static final String URI = "uri";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.p_more_webview_activity);

		initIntent();

		initViews();
		initUserData();
		initListeners();

	}

	private void initIntent() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		uri = intent.getStringExtra(URI);
		//Ĭ��Ϊmessage uri
		uriType = intent.getIntExtra("type", 1);
		
		// ���е���ҳ���ӣ�����¿������²���û��
		// id:�ն�ID
		// version:�ն˰汾
		// type:�ն�����1.ANDROID,2.ANDROID�Ƶ꣬3.IOS
		// cityId:����ID
		if (uriType == TYPE_NEW_MESSAGE) {
			String versionName = ETApp.getInstance().getMobileInfo().getVerisonName();
			String param = "?type=1&id=" + getPassengerId() + "&version=" + versionName + "&cityId=" + getCityId();
			uri = uri + param;
		}
		title = intent.getStringExtra(TITLE);
	}

	@Override
	protected void initViews() {

		btn = (Button) findViewById(R.id.textView_refresh_again);
		bar = new TitleBar(this);
		if (TextUtils.isEmpty(title)) {
			bar.setTitleName("����");
		} else {
			bar.setTitleName(title);
		}

	}

	@Override
	protected void onDestroy() {

		if (bar != null) {
			bar.close();
		}
		// TODO Auto-generated method stubi
		super.onDestroy();
	}

	@Override
	protected void initUserData() {

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
			if (TextUtils.isEmpty(cityId)) {
				cityId = "1";
			}

			webView.loadUrl(uri);

			webView.setVisibility(View.VISIBLE);

		} else {

			webView.setVisibility(View.INVISIBLE);
			btn.setVisibility(View.VISIBLE);
			btn.setText(R.string.network_notgood);
			// webView.loadUrl(url);
		}

	}

	@Override
	protected void initListeners() {

		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				loadUrl();
			}
		});
	}
}
