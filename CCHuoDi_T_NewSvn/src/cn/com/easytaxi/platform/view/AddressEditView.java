package cn.com.easytaxi.platform.view;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.common.Const;
import cn.com.easytaxi.common.SessionAdapter;
import cn.com.easytaxi.util.ToastUtil;
import cn.com.easytaxi.workpool.bean.GeoPointLable;

import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionResult.SuggestionInfo;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.easytaxi.etpassengersx.R;

public class AddressEditView extends LinearLayout implements View.OnFocusChangeListener, OnGetSuggestionResultListener {
	private AutoCompleteTextView textView;
	private ProgressBar pb;
	private AddressAdapter adapter;
	private SessionAdapter session;
	private MyTextWatcher textWatcher;
	private GeoPointLable selectedGeoPoint;
	private OnEditOverListner onEditOverListner;
	private boolean loadOnInflated = false;
	private Context context;

	/**
	 * pois搜索
	 */
	// private PoiSearch poiSearch;

	private MyThreadGetPoiAddressList th;
	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case 2:
					break;
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	public boolean isLoadOnInflated() {
		return loadOnInflated;
	}

	public MyTextWatcher getTextWatcher() {
		return textWatcher;
	}

	public void setLoadOnInflated(boolean loadOnInflated) {
		this.loadOnInflated = loadOnInflated;
	}

	public AddressEditView(Context context) {
		super(context);
		this.context = context;
		LayoutInflater.from(getContext()).inflate(R.layout.workpool_addr_editview, this, true);
		init();
	}

	public AddressEditView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater.from(getContext()).inflate(R.layout.workpool_addr_editview, this, true);
		init();
		TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.AddressAutoCompleteEditView);
		CharSequence hint = t.getString(R.styleable.AddressAutoCompleteEditView_hint);
		loadOnInflated = t.getBoolean(R.styleable.AddressAutoCompleteEditView_load_oninflated, false);
		t.recycle();
		if (hint != null) {
			textView.setHint(hint);
		}
	}

	private void init() {
		mSearch = SuggestionSearch.newInstance();
		mSearch.setOnGetSuggestionResultListener(this);

		
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(this);
		
		textView = (AutoCompleteTextView) findViewById(R.id.workpool_addredit);
		session = new SessionAdapter(getContext());
		pb = (ProgressBar) findViewById(R.id.workpool_addredit_progress);
		adapter = new AddressAdapter(getContext(), R.layout.addr_adpter_item, android.R.id.text1, session);
		textView.setAdapter(adapter);
		textView.setThreshold(2);
		textView.setOnItemClickListener(new MyItemClickLisener());

		textView.clearFocus();
		textWatcher = new MyTextWatcher();
		textView.addTextChangedListener(textWatcher);
		textView.setOnFocusChangeListener(this);
	}

	public void setHint(String info) {
		if (textView != null) {
			textView.setHint(info);
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}

	private boolean newestValue = false;

	private class MyTextWatcher implements TextWatcher {

		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			AppLog.LogD("beforeTextChanged::" + s);
		}

		public void onTextChanged(CharSequence s, int start, int before, int count) {
			AppLog.LogD("onTextChanged::" + s);
		}

		public void afterTextChanged(Editable s) {
			AppLog.LogD("afterTextChanged::" + s);

			if (session == null) {
				return;
			}

			if (loadOnInflated) {// 刚进入会加载当前位置，不弹出下拉框
				loadOnInflated = false;
				return;
			}
			// 在autocomplete下拉列表中选择一项，也会触发afterTextChanged，造成死循环，判断一下
			boolean enable = true;
			ArrayList<GeoPointLable> datas = ((AddressAdapter) textView.getAdapter()).getDatas();
			for (int i = 0; i < datas.size(); i++) {
				if (s.toString().equals(datas.get(i).getName())) {
					selectedGeoPoint = new GeoPointLable(datas.get(i).getLat(), datas.get(i).getLog(), "","");
					enable = false;
				}
			}
			if (enable && s.length() >= 2) {
				String city = getCityName();
				if (TextUtils.isEmpty(city)) {
					city = "四川";
				}

				if (TextUtils.isEmpty(city)) {
					return;
				}
				newestValue = false;
				poiSearchInCity(city, s.toString());
				pb.setVisibility(View.VISIBLE);

			}
		}
	}

	private class MyItemClickLisener implements OnItemClickListener {

		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			newestValue = true;
			// 关闭输入法
			InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getWindowToken(), 0);
			// 获取经纬度
			selectedGeoPoint = (GeoPointLable) view.getTag();

			if (onEditOverListner != null) {
				AddressEditView.this.setTag(selectedGeoPoint);
				onEditOverListner.OnEditOver(AddressEditView.this);
			}
			String city = session.get("_CITY_NAME");
			String mobile = session.get("_MOBILE");
			session.savePoi(city, mobile, 1, selectedGeoPoint);

		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (session != null) {
			session.close();
			session = null;
		}
		textWatcher = null;

	}

	public String getCityName() {

		return this.cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	private String cityName;

	int sessionId = 0;

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {

		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
	}

	public void poiSearchInHistory() {
		
	}

	private SuggestionSearch mSearch;
	private SuggestionSearch mSuggestionSearch = null;

	public void poiSearchInCity(final String city, final String key) {
//		mSearch.requestSuggestion(new SuggestionSearchOption().city(city).keyword(key));
		mSuggestionSearch.requestSuggestion((new SuggestionSearchOption()).keyword(city).city(city));
	}

	public GeoPoint getSelectedGeoPoint() {
		return new GeoPoint(selectedGeoPoint.getLat(), selectedGeoPoint.getLog());
	}

	public void setSelectedGeoPoint(GeoPointLable selectedGeoPoint) {
		this.selectedGeoPoint = selectedGeoPoint;
	}

	public Editable getText() {
		return textView.getText();
	}

	public void setText(CharSequence text) {
		newestValue = true;
		textView.removeTextChangedListener(textWatcher);
		textView.setText(text);
		textView.setSelection(textView.getText().length());
		textView.addTextChangedListener(textWatcher);
	}

	public OnEditOverListner getOnEditOverListner() {
		return onEditOverListner;
	}

	public void setOnEditOverListner(OnEditOverListner onEditOverListner) {
		this.onEditOverListner = onEditOverListner;
	}

	public interface OnEditOverListner {
		public void OnEditOver(AddressEditView view);
	}

	public void setError(CharSequence msg) {
		textView.setError(msg);
	}

	public AutoCompleteTextView getEditText() {
		return textView;
	}

	public void setEditText(AutoCompleteTextView editText) {
		this.textView = editText;
	}

	public void showProgress() {
		pb.setVisibility(View.VISIBLE);
	}

	public void hiddenProgress() {
		pb.setVisibility(View.GONE);
	}

	public boolean isNewestValue() {
		return newestValue;
	}

	public void setNewestValue(boolean newestValue) {
		this.newestValue = newestValue;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// 关闭输入法
		try {
			InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			if (hasFocus) {// 如果有焦点就显示软件盘
				imm.showSoftInputFromInputMethod(v.getWindowToken(), 0);
			} else {// 否则就隐藏
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 点击输入框，跳转
	 * 
	 * @param activity
	 * @param cityName
	 * @param requestCode
	 */
	public void setTextViewOnclickListener(final Activity activity, final String cityName, final int requestCode, final String className) {
		textView.clearFocus();
		textView.setFocusable(false);
		textView.setFocusableInTouchMode(false);

		textView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 关闭输入法
				try {
					InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(getWindowToken(), 0);
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					Intent intent = null;
					intent = new Intent(activity, Class.forName(className));
					intent.putExtra("cityName", cityName);
					activity.startActivityForResult(intent, requestCode);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		});

	}


	/**
	 * 反解析地址的poi
	 * 
	 * @param geoPointLable
	 * @param url
	 *            反解析的百度url
	 * @param address
	 *            地址名字
	 * @param city
	 *            地址城市
	 * @param output
	 *            输出格式 json 或 xml
	 * @param ak
	 *            开发者申请的百度秘钥
	 */
//	public void executeGetPoi(GeoPointLable geoPointLable, String url, String address, String city, String output, String ak) {
//		List<NameValuePair> params = new ArrayList<NameValuePair>();
//		params.add(new BasicNameValuePair("address", address));
//		params.add(new BasicNameValuePair("city", city));
//		params.add(new BasicNameValuePair("output", output));
//		params.add(new BasicNameValuePair("ak", ak));
//
//		url = url + "?" + URLEncodedUtils.format(params, HTTP.UTF_8);
//		String result = HttpGetMethod(url);
//		// 解析返回结果
//		try {
//			JSONObject js = new JSONObject(result);
//			JSONObject location = js.getJSONObject("result").getJSONObject("location");
//			geoPointLable.setLat((int) (Float.parseFloat(location.getString("lat")) * 1000000));
//			geoPointLable.setLog((int) (Float.parseFloat(location.getString("lng")) * 1000000));
//		} catch (JSONException e) {
//			e.printStackTrace();
//			// 当异常的时候
//			geoPointLable.setLat(0);
//			geoPointLable.setLog(0);
//		}
//	}

	/**
	 * Get方式
	 * 
	 * @param url
	 *            请求的Url
	 * @return
	 */
//	public static String HttpGetMethod(String url) {
//		String strResult = "";
//		HttpClient client = new DefaultHttpClient();
//		HttpGet get = new HttpGet(url);
//		try {
//			HttpResponse res = client.execute(get);
//			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//				HttpEntity entity = res.getEntity();
//				strResult = EntityUtils.toString(entity);
//				return strResult;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			strResult = "";
//		} finally {
//			// 关闭连接 ,释放资源
//			client.getConnectionManager().shutdown();
//		}
//		return strResult;
//	}

	/**
	 * 
	 * @param callback
	 * @param address
	 * @param cityName
	 * @param baiduKey
	 *            开发者秘钥：我们是"F1f3c50228554ec93f1b734dc3761a5d"
	 */
	public void getFromBaidu(final String address, final String cityName, final String baiduKey) {
		try {
			if (th != null) {
				th.setFlag(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		th = new MyThreadGetPoiAddressList(address, baiduKey);
		th.start();
	}

	class MyThreadGetPoiAddressList extends Thread {
		boolean flag = false;
		String address = "";
		String baiduKey = "";

		public MyThreadGetPoiAddressList(String address, String baiduKey) {
			this.address = address;
			this.baiduKey = baiduKey;
		}

		@Override
		public void run() {

		}

		public void setFlag(boolean flag) {
			this.flag = flag;
		}
	}


	/**
	 * 随机获取百度key
	 * 
	 * @return
	 */
	public String getBaiduWebPoiKey() {
		int length = Const.baiduWebPoiKeys.length;
		int index = (int) (Math.random() * length);
		return Const.baiduWebPoiKeys[index];
	}

	public void onDestory() {
//		if (mSearch != null)
		mSearch.destroy();
		mSuggestionSearch.destroy();
	}

	@Override
	public void onGetSuggestionResult(SuggestionResult res) {
		if (res == null || res.getAllSuggestions() == null) {
			return;
		}
		List<SuggestionInfo> addrrs = res.getAllSuggestions();
		ArrayList<GeoPointLable> datas = adapter.getDatas();
		datas.clear();
		for (SuggestionInfo poiInfo : addrrs) {
			if (poiInfo == null || poiInfo.key == null || poiInfo.pt == null)
				continue;
			long lat = (long) (poiInfo.pt.latitude * 1E6);
			long lng = (long) (poiInfo.pt.longitude * 1E6);
			try {
				if (getCityName() != null && getCityName().equals(poiInfo.city))
					datas.add(new GeoPointLable((int)lat, (int)lng, poiInfo.key, poiInfo.city));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		adapter.setDatas(datas);
		adapter.notifyDataSetChanged();
		hiddenProgress();
	}
}
