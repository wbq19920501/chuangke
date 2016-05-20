package cn.com.easytaxi.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.BookConfig;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.NewNetworkRequest;
import cn.com.easytaxi.NewNetworkRequest.DiaoDuPrice;
import cn.com.easytaxi.NewNetworkRequest.TimeLines;
import cn.com.easytaxi.NewNetworkRequest.TipPrices;
import cn.com.easytaxi.airport.AirportBookPublishFragement;
import cn.com.easytaxi.airport.view.ScrollingTextView;
import cn.com.easytaxi.book.BookBean;
import cn.com.easytaxi.book.BookHistoryFragementActivity;
import cn.com.easytaxi.book.BookUtil;
import cn.com.easytaxi.book.NewBookDetail;
import cn.com.easytaxi.client.channel.TcpClient;
import cn.com.easytaxi.client.common.MsgConst;
import cn.com.easytaxi.common.ActionLogUtil;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.common.Const;
import cn.com.easytaxi.common.MapUtil;
import cn.com.easytaxi.common.SocketUtil;
import cn.com.easytaxi.common.ToolUtil;
import cn.com.easytaxi.common.User;
import cn.com.easytaxi.common.Window;
import cn.com.easytaxi.onetaxi.SearchAddressActivity;
import cn.com.easytaxi.onetaxi.TitleBar;
import cn.com.easytaxi.platform.CitySelectActivity;
import cn.com.easytaxi.platform.RegisterActivity;
import cn.com.easytaxi.platform.common.Util;
import cn.com.easytaxi.platform.service.SystemService;
import cn.com.easytaxi.util.DateTimePickDialog;
import cn.com.easytaxi.util.TimeTool;
import cn.com.easytaxi.workpool.bean.GeoPointLable;

import com.baidu.mapapi.model.inner.GeoPoint;
import com.easytaxi.etpassengersx.R;
import com.easytaxi.etpassengersx.wxapi.WXPayEntryActivity;
import com.google.gson.JsonObject;

/**
 * 预约订单发布页面
 * 
 * @ClassName: BookPublishFragement
 * @Description: TODO
 * @author Brook Xu
 * @date 2015年4月18日 下午4:30:20
 * @version 1.0
 */
public class BookPublishFragement extends BookBaseFragement implements View.OnClickListener, View.OnTouchListener {
	private DateTimePickDialog mDateTimePicker;
	private AlertDialog detailDialog;
	private ProgressDialog mProgressDialog;
	/**
	 * 获取以前发过的历史订单
	 */
	private List<BookBean> historyBooks;

	protected static final int START_CITY_REQ_CODE = 400;
	protected static final int END_CITY_REQ_CODE = 401;

	private static SimpleDateFormat f_use_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat f_show_time = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
	private String cityId;
	private String cityName;
	private String mobile;
	private long timestamp;
	private static final int D_TIME = 10;
	// InputMethodManager imm;
	/**
	 * 是否强制在线支付： 0代表不需要在线支付；1表示必须在线支付
	 */
	private int onlinePayment = 0;
	private String tempstr = "高峰期适当加价，有助于更快坐到车";
	// 位置广播监听
	private LocationBroadcastReceiver locationReceiver;
	private RegisterReceiver registerReceiver;

	// 乘客位置信息（通过监听平台广播获得的,第一次直接取平台的）
	int p_lat = 0;
	int p_lng = 0;

	// 乘客起始地点位置
	int u_lat = 0;
	int u_lng = 0;
	// 乘客结束地点位置
	int u_lat_end = 0;
	int u_lng_end = 0;

	/** price list pager */
	private LinearLayout chatAddMoneyPager;
	private TitleBar titleBar;

	private Date submitTime;
	private View sendBook;
	private Button btnStartLoc;
	private Button btnEndLoc;
	private EditText etPhone;
	private EditText etUseTime;
	private TextView etStartAddr;
	private TextView etEndAddr;
	private TextView etBanyungong;
	private TextView default_book_time;
	private TextView tvDistanPrice;
	private ToggleButton payMode;

	String checkedType = "";
	private RadioGroup rgCarType;
	private RadioButton rbCarOne;
	private RadioButton rbCarTwo;

	private int carType = 1;
	/** 搬运工人数 */
	private int needPerson = 0;

	private String endCityName;

	private String endCityId;

	private String startCityName;

	private String startCityId;

	private Button end_city;

	private Button start_city;

	private View cover_loading;

	protected int priceKey = 0;
	protected String priceValue = "0";
	// protected List<DiaoDuPrice> priceList = new
	// ArrayList<NewNetworkRequest.DiaoDuPrice>(12);
	private int[] priceList;
	private Callback<Object> priceCallback = new Callback<Object>() {

		@Override
		public void handle(Object param) {
			try {
				if (param != null) {
					TipPrices tipPrices = (NewNetworkRequest.TipPrices) param;
					if (tipPrices.error == 0) {// 有价格列表
						priceList = tipPrices.priceList;
						tempstr = tipPrices.msg;
						onlinePayment = tipPrices.onlinePayment;
						// 大于0时，表示必须在线支付
						if (onlinePayment > 0) {
							payMode.setChecked(true);
							payMode.setEnabled(false);
						} else {
							payMode.setChecked(false);
							payMode.setEnabled(true);
						}
					} else {// 没获取到价格列表
						priceList = new int[] { 0 };
						Toast.makeText(bookParent, tipPrices.errormsg, Toast.LENGTH_LONG).show();
					}
					priceKey = priceList[0];
					priceValue = priceList[0] + "";
					initPriveViewcell(priceList);
					initScrollingTextView(tempstr);
				}
			} catch (Exception e) {
				Toast.makeText(bookParent, "没有获取到切换城市的价格列表...", Toast.LENGTH_LONG).show();
			}
		}
	};
	/**
	 * 滚动提示文字：显示加价上面的提示文字
	 */
	private ScrollingTextView scrollingTextView;
	/**
	 * 默认的订车最小时间：单位分钟
	 */
	private static final int DEFAULT_MIN_USE_TIME = 30;
	/**
	 * 默认的订车最大时间：单位分钟
	 */
	private int defaultMaxUseCarTime = 3 * 24 * 60;

	/**
	 * 订车时间上下线
	 */
	private NewNetworkRequest.TimeLine timeLine;
	/**
	 * 时间上下线获取回调
	 */
	private Callback<Object> timeLinesCallBack = new Callback<Object>() {

		@Override
		public void handle(Object param) {
			if (param != null) {
				TimeLines timeLines = (NewNetworkRequest.TimeLines) param;
				ArrayList<NewNetworkRequest.TimeLine> timeLineList = timeLines.datas;
				if (timeLineList.size() > 0) {
					timeLine = timeLineList.get(0);
					// 获取到时间上下线，重置默认的用车时间
					setUseCarTime(Integer.parseInt(timeLine.lower));
					// 显示时间
					if (Integer.parseInt(timeLine.lower) > 0) {
						default_book_time.setText(getTimeStr(Integer.parseInt(timeLine.lower)));
					}

				}
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mProgressDialog = new ProgressDialog(this.getActivity());
		mProgressDialog.setMessage("请稍后...");
		mobile = getPassengerId();
		cityName = bookParent.getCityName();
		cityId = bookParent.getCityId();
		if (cityName == null) {// 默认为北京市
			cityName = "北京";
			cityId = "1";
		}
		p_lat = ETApp.getInstance().getCacheInt("_P_LAT");
		p_lng = ETApp.getInstance().getCacheInt("_P_LNG");
		u_lat = p_lat;
		u_lng = p_lng;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.book_publish, container, false);
		init(view);
		regReceiver();

		NewNetworkRequest.getAddressByLocation(p_lat, p_lng, firstAddressCallback);

		// initPrice(Const.BOOK_PRICELIST);

		// new Thread() {
		// public void run() {
		//
		// // NewNetworkRequest.getDiaoDuPriceList(2, priceCallback);
		// NewNetworkRequest.getDiaoDuPriceList(Integer.parseInt(cityId),
		// Const.BOOK_BOOK_TYPE, Const.CLIENTTYPE, priceCallback);
		//
		// };
		// }.start();

		// 获取时间上线限制
		String version = "" + ETApp.getInstance().getMobileInfo().getVerisonCode();
		NewNetworkRequest.getTimeDeadLine((Integer.parseInt(cityId)), Const.BOOK_BOOK_TYPE, BookConfig.ClientType.CLIENT_TYPE_PASSENGER, version, timeLinesCallBack);
		new LoadBooks().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		return view;
	}

	private void initPrice(String priceListCacheName) {
		try {
			TipPrices tips = new TipPrices();
			JSONObject js = new JSONObject(ETApp.getInstance().getCacheString(priceListCacheName));
			tips.error = js.getInt("error");
			tips.errormsg = js.getString("errormsg");
			tips.onlinePayment = js.getJSONObject("datas").getInt("onlinePayment");
			JSONArray jsArray = js.getJSONObject("datas").getJSONArray("priceList");
			int length = jsArray.length();
			tips.priceList = new int[length];
			for (int i = 0; i < length; i++) {
				tips.priceList[i] = jsArray.getInt(i);
			}

			if (null != tips) {
				priceList = tips.priceList;
			}
			if (null != priceList && priceList.length > 0) {
				priceKey = priceList[0];
				priceValue = priceList[0] + "";
				initPriveViewcell(priceList);
				tempstr = js.getJSONObject("datas").getString("msg");
				initScrollingTextView(tempstr);
				onlinePayment = tips.onlinePayment;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 初始化滚动提示加价的ScrollingTextView内容
	 * 
	 * @param price
	 */
	private void initScrollingTextView(String msg) {
		scrollingTextView.setText(msg);
	}

	@Override
	public void onStart() {

		super.onStart();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
			u_lat = data.getIntExtra("lat", 0);
			u_lng = data.getIntExtra("lng", 0);
			etStartAddr.setText(data.getStringExtra("address"));
		}
		if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
			u_lat_end = data.getIntExtra("lat", 0);
			u_lng_end = data.getIntExtra("lng", 0);
			etEndAddr.setText(data.getStringExtra("address"));
		}
		if (requestCode == END_CITY_REQ_CODE && resultCode == Activity.RESULT_OK) {
			endCityName = data.getStringExtra("cityName");//
			endCityId = data.getStringExtra("cityId");
			AppLog.LogD("endCityName " + endCityName + " ,endCityId  " + endCityId);

			if (endCityName.length() > 2) {

				end_city.setText(TextUtils.substring(endCityName, 0, 2));
			} else {
				end_city.setText(endCityName);

			}
		}
		if (requestCode == START_CITY_REQ_CODE && resultCode == Activity.RESULT_OK) {
			startCityName = data.getStringExtra("cityName");
			startCityId = data.getStringExtra("cityId");// ;", 0);
			endCityId = startCityId;
			endCityName = startCityName;
			etStartAddr.setText("");

			AppLog.LogD("endCityName " + startCityName + " ,endCityId  " + startCityId + " , cityName" + cityName);

			if (startCityName.length() > 2) {
				start_city.setText(TextUtils.substring(startCityName, 0, 2));
				end_city.setText(TextUtils.substring(startCityName, 0, 2));
			} else {
				start_city.setText(startCityName);
				end_city.setText(startCityName);
			}
			// // 刷新价格列表
			// NewNetworkRequest.getDiaoDuPriceList(Integer.parseInt(startCityId),
			// Const.BOOK_BOOK_TYPE, Const.CLIENTTYPE, priceCallback);

		}

		setDistance();

	}

	protected boolean isChooseStartCity;

	private void init(View view) {
		rgCarType = (RadioGroup) view.findViewById(R.id.cartype_radiogroup);
		rbCarOne = (RadioButton) view.findViewById(R.id.cartype_rb_one);
		rbCarTwo = (RadioButton) view.findViewById(R.id.cartype_rb_two);

		rbCarOne.setOnTouchListener(this);
		rbCarTwo.setOnTouchListener(this);

		rgCarType.clearCheck();
		rgCarType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				switch (checkedId) {
				case R.id.cartype_rb_one:
					checkedType = "面包车";
					carType = 1;
					break;
				case R.id.cartype_rb_two:
					checkedType = "厢式车";
					carType = 2;
					break;
				default:
					// -1
					return;
				}

				String strEndAddr = etEndAddr.getText().toString().trim();
				if (TextUtils.isEmpty(strEndAddr)) {
					Toast.makeText(bookParent, "请先输入下车地点", Toast.LENGTH_SHORT).show();
					etEndAddr.requestFocus();
					checkedType = "";
					rgCarType.clearCheck();
					return;
				} else {
					if (strEndAddr.contains("，")) {
						int index = strEndAddr.lastIndexOf("，");
						strEndAddr = strEndAddr.substring(0, index);
						etEndAddr.setText(strEndAddr + "，" + checkedType);
					} else {
						etEndAddr.setText(strEndAddr + "，" + checkedType);
					}
				}
			}
		});
		payMode = (ToggleButton) view.findViewById(R.id.pay_mode);
		payMode.setChecked(false);
		default_book_time = (TextView) view.findViewById(R.id.default_book_time);
		scrollingTextView = (ScrollingTextView) view.findViewById(R.id.book_scrollingTextView);

		end_city = (Button) view.findViewById(R.id.end_city);
		start_city = (Button) view.findViewById(R.id.start_city);
		cover_loading = view.findViewById(R.id.cover_loading);

		end_city.setText(cityName);
		start_city.setText(cityName);
		startCityName = cityName;
		startCityId = cityId;
		endCityName = cityName;
		endCityId = cityId;

		chatAddMoneyPager = (LinearLayout) view.findViewById(R.id.chat_addmoney_pager);
		sendBook = view.findViewById(R.id.book_send);
		tvDistanPrice = (TextView) view.findViewById(R.id.book_publish_distance);
		btnStartLoc = (Button) view.findViewById(R.id.btn_book_startloc);
		btnEndLoc = (Button) view.findViewById(R.id.btn_book_endloc);
		etPhone = (EditText) view.findViewById(R.id.et_book_phone);

		etUseTime = (EditText) view.findViewById(R.id.et_book_usetime);
		etStartAddr = (TextView) view.findViewById(R.id.et_book_startaddr);
		etEndAddr = (TextView) view.findViewById(R.id.et_book_endaddr);
		etBanyungong = (TextView) view.findViewById(R.id.et_book_banyungong);
		etPhone.setText(mobile);

		// 初始化默认为30分钟后用车
		setUseCarTime(DEFAULT_MIN_USE_TIME);
		mDateTimePicker = new DateTimePickDialog(this.getActivity(),  f_show_time.format(submitTime));

		etPhone.setOnClickListener(this);
		etStartAddr.setOnClickListener(this);
		etEndAddr.setOnClickListener(this);
		etBanyungong.setOnClickListener(this);
		btnStartLoc.setOnClickListener(this);
		btnEndLoc.setOnClickListener(this);

		// etStartAddr.setText(this.getIntent().getStringExtra("startAddress"));
		sendBook.setOnClickListener(this);
		etUseTime.setOnClickListener(this);
		etStartAddr.setOnClickListener(this);
		etEndAddr.setOnClickListener(this);

		end_city.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(bookParent, CitySelectActivity.class);

				startActivityForResult(intent, END_CITY_REQ_CODE);
			}
		});

		start_city.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isChooseStartCity = true;
				Intent intent = new Intent(bookParent, CitySelectActivity.class);
				startActivityForResult(intent, START_CITY_REQ_CODE);
			}
		});

		cover_loading.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void switchAddress(int type, String cityName) {
		Intent intent = new Intent(bookParent, SearchAddressActivity.class);
		intent.putExtra("cityName", cityName);
		AppLog.LogD(cityName);
		startActivityForResult(intent, type);
	}

	/**
	 * 新的解析价格列表的方法
	 * 
	 * @param priceList
	 *            价格列表
	 */
	private void initPriveViewcell(int[] priceList) {
		LayoutInflater inflater = LayoutInflater.from(bookParent);
		chatAddMoneyPager.removeAllViews();
		for (Integer p : priceList) {
			View view = inflater.inflate(R.layout.p_price_item, null);
			RadioButton cb = (RadioButton) view.findViewById(R.id.price_item);
			cb.setTag(p);
			cb.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					RadioButton cbb = (RadioButton) arg0;
					if (cbb != null) {
						int ppp = (Integer) cbb.getTag();
						restet(chatAddMoneyPager);
						cbb.setText(ppp + "");
						priceKey = ppp;
						priceValue = ppp + "";
						cbb.setChecked(true);
					}
				}
			});
			if (priceKey == p) {
				priceValue = p + "";
				cb.setChecked(true);
			}

			cb.setText(p + "");
			chatAddMoneyPager.addView(view);
		}
	}

	private void initPriveViewcell(List<DiaoDuPrice> priceList) {
		// int len = priceList.size();
		LayoutInflater inflater = LayoutInflater.from(bookParent);
		chatAddMoneyPager.removeAllViews();
		for (DiaoDuPrice p : priceList) {
			final DiaoDuPrice pp = p;
			View view = inflater.inflate(R.layout.p_price_item, null);
			RadioButton cb = (RadioButton) view.findViewById(R.id.price_item);
			cb.setTag(pp);
			cb.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					RadioButton cbb = (RadioButton) arg0;
					final DiaoDuPrice ppp = (DiaoDuPrice) cbb.getTag();
					if (cbb != null) {
						restet(chatAddMoneyPager);
						if (ppp.text.equals("扣表")) {
							cbb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
							cbb.setText(R.string.one_taix_koubiao);
						} else {
							cbb.setText(ppp.text);
						}
						// cbb.setText(ppp.text);
						priceKey = ppp.val;
						priceValue = ppp.text;

						cbb.setChecked(true);
					}
				}
			});
			if (priceKey == p.val) {
				priceValue = p.text;
				cb.setChecked(true);
			}

			if (p.text.equals("扣表")) {
				cb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				cb.setText(R.string.one_taix_koubiao);
			} else {

				cb.setText(p.text);
			}

			chatAddMoneyPager.addView(view);
		}

	}

	protected void restet(LinearLayout parent) {
		int count = parent.getChildCount();
		for (int i = 0; i < count; i++) {
			View v = parent.getChildAt(i);
			RadioButton cb = (RadioButton) v.findViewById(R.id.price_item);
			cb.setChecked(false);
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (titleBar != null) {

			titleBar.close();
		}

		if (addrSetReceiver != null) {
			bookParent.unregisterReceiver(addrSetReceiver);
			addrSetReceiver = null;
		}

		unRegReceiver();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.et_book_phone:
			etPhone.setCursorVisible(true);
			etPhone.setSelection(etPhone.getText().toString().length());
			break;
		case R.id.et_book_banyungong:
			int height = this.getActivity().getWindowManager().getDefaultDisplay().getHeight();
			int[] location = new int[2];
			v.getLocationOnScreen(location);
			int defaultPerson = 0;
			try {
				defaultPerson = Integer.parseInt(etBanyungong.getText().toString().substring(0, 1));
			} catch (Exception e) {
				e.printStackTrace();
			}
			Window.selectBanyungong(this.getActivity(), height - location[1], defaultPerson, new Callback<Integer>() {

				@Override
				public void handle(Integer param) {
					// TODO Auto-generated method stub
					etBanyungong.setText(param + "人");

				}
			});
			break;
		case R.id.et_book_endaddr:
			etPhone.setCursorVisible(false);
			switchAddress(2, endCityName);
			AppLog.LogD("xyw", "et_book_endaddr" + endCityName);
			break;
		case R.id.et_book_startaddr:
			etPhone.setCursorVisible(false);
			switchAddress(1, startCityName);
			AppLog.LogD("xyw", "et_book_startaddr");
			break;

		case R.id.btn_book_startloc:
			etPhone.setCursorVisible(false);
			switchAddress(1, startCityName);
			break;
		case R.id.btn_book_endloc:
			etPhone.setCursorVisible(false);
			switchAddress(2, endCityName);
			break;

		case R.id.et_book_usetime:
			etPhone.setCursorVisible(false);
			// selectDateByWheel();
			mDateTimePicker.dateTimePicKDialog(new Callback<Calendar>() {

				@Override
				public void handle(Calendar param) {
					// TODO Auto-generated method stub
					submitTime = param.getTime();
					etUseTime.setText(f_show_time.format(submitTime));
				}
			});
			break;
		case R.id.book_send:
			String strEndAddr = etEndAddr.getText().toString();
			if (!TextUtils.isEmpty(strEndAddr) && !strEndAddr.contains("，")) {
				if (!TextUtils.isEmpty(checkedType))
					etEndAddr.setText(strEndAddr + "，" + checkedType);
			}
			check();
			break;

		default:
			break;
		}
	}

	private void check() {
		if (!StringUtils.isEmpty(getPassengerId())) {

			final String phone = etPhone.getText().toString().trim();
			// final String time = etUseTime.getText().toString().trim();
			final String strStartAddr = etStartAddr.getText().toString().trim();
			final String strEndAddr = etEndAddr.getText().toString().trim();

			try {

				if (!phone.matches(Util.REGEX_MOBILE)) {
					Toast.makeText(bookParent, "电话号码不正确", Toast.LENGTH_SHORT).show();
					etPhone.requestFocus();
					return;
				}

				Calendar now = Calendar.getInstance();
				Calendar use = Calendar.getInstance();

				if (submitTime == null) {// 现在用车
					setUseCarTime(DEFAULT_MIN_USE_TIME);
					use.setTime(submitTime);
				} else {
					use.setTime(submitTime);
				}
				try {
					if (use.getTimeInMillis() <= now.getTimeInMillis()) {
						selectDateByWheel();
						Toast.makeText(bookParent, "用车时间必须大于当前时间", Toast.LENGTH_SHORT).show();
						return;
					}
					// if (use.getTimeInMillis() >= limit.getTimeInMillis()) {
					// selectDateByWheel();
					// Toast.makeText(bookParent, "用车时间必须为今天或明天",
					// Toast.LENGTH_SHORT).show();
					// return;
					// }
				} catch (Exception e) {
					return;
				}
				if (strStartAddr.equals("")) {
					Toast.makeText(bookParent, "请输入上车地点", Toast.LENGTH_SHORT).show();
					etStartAddr.requestFocus();
					return;
				}
				if (strEndAddr.equals("")) {
					Toast.makeText(bookParent, "请输入下车地点", Toast.LENGTH_SHORT).show();
					etEndAddr.requestFocus();
					return;
				}

				if (rgCarType.getCheckedRadioButtonId() == -1) {
					Toast.makeText(this.getActivity(), "请选择车型", Toast.LENGTH_LONG).show();
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				needPerson = Integer.parseInt(etBanyungong.getText().toString().substring(0, 1));
			} catch (Exception e) {
				needPerson = 0;
			}

			send(carType, needPerson, phone, f_use_time.format(submitTime), strStartAddr, strEndAddr);
		} else {

			Intent intent = new Intent(bookParent, RegisterActivity.class);
			startActivity(intent);

		}
	}

	private void send(int carType, int personNum, String phone, String time, String strStartAddr, String strEndAddr) {
		if (payMode.isChecked() && Integer.parseInt(priceValue) == 0) {
			Toast.makeText(bookParent, "线上付不能为0元", Toast.LENGTH_SHORT).show();
			return;
		}
		sendBook.setEnabled(false);
		try {
			int versionCode = ETApp.getInstance().getMobileInfo().getVerisonCode();
			final JSONObject json = new JSONObject();
			json.put("action", "scheduleAction");
			json.put("method", "submitBook");
			json.put("timestamp", timestamp);
			json.put("bookType", Const.BOOK_BOOK_TYPE);
			json.put("cityId", startCityId);
			json.put("cityName", startCityName);

			json.put("carType", carType);
			json.put("personNum", personNum);

			json.put("passengerPhone", phone);
			json.put("passengerId", phone);
			// BUG --FIXED------------
			json.put("passengerId", ETApp.getInstance().getCurrentUser().getPhoneNumber(User._MOBILE));

			json.put("clientType", BookConfig.ClientType.CLIENT_TYPE_PASSENGER);
			json.put("clientVersion", "" + versionCode);

			json.put("startAddress", strStartAddr);
			if (u_lng == 0 || u_lat == 0) {
				json.put("startLongitude", p_lng);
				json.put("startLatitude", p_lat);
			} else {
				json.put("startLongitude", u_lng);
				json.put("startLatitude", u_lat);
			}

			json.put("endAddress", strEndAddr);
			json.put("endLongitude", u_lng_end);
			json.put("endLatitude", u_lat_end);

			json.put("useTime", time);
			json.put("payment", priceValue);
			json.put("price", priceValue);

			// 在线支付
			if (payMode.isChecked()) {
				json.put("onlinePayment", true);
			} else {
				json.put("onlinePayment", false);
			}

			final int selectPrice = Integer.parseInt(priceValue);
			AppLog.LogD("xyw", "submit moblie:" + mobile);
			AppLog.LogD("xyw", "submit json:" + json.toString());
			cover_loading.setVisibility(View.VISIBLE);
			SocketUtil.getJSONObject(Long.valueOf(mobile), json, new Callback<JSONObject>() {

				@Override
				public void handle(JSONObject param) {

					try {
						if (param != null) {
							AppLog.LogD("xyw", param.toString());
							int error = param.getInt("error");
							switch (error) {
							// 成功
							case 0x0000:
								Toast.makeText(bookParent, "提交成功", Toast.LENGTH_SHORT).show();
								Intent intent = new Intent("cn.com.easytaxi.book.refresh_list");
								intent.putExtra("fromSubmited", true);
								bookParent.sendBroadcast(intent);
								// bookParent.setCurrentPage(1);
								etEndAddr.setText("");

								// 跳转到详情页面
								Intent intent1 = new Intent(bookParent, NewBookDetail.class);
								intent1.putExtra("bookId", param.getLong("bookId"));
								bookParent.startActivity(intent1);
								break;
							// 余额不足,跳转到充值页面
							case 0x0003:
								Toast.makeText(bookParent, param.getString("errormsg"), Toast.LENGTH_SHORT).show();
								Intent intent2 = new Intent(bookParent, WXPayEntryActivity.class);
								intent2.putExtra("payMoney", selectPrice);
								bookParent.startActivity(intent2);
								break;
							// 需要在线支付，直接显示errormsg
							case 0x0004:
							default:
								Toast.makeText(bookParent, param.getString("errormsg"), Toast.LENGTH_SHORT).show();
								break;
							}
						} else {
							AppLog.LogD("error--->提交订单返回结果为空");
							Toast.makeText(bookParent, "您的网络不给力！", Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						//
						e.printStackTrace();
					}

					cover_loading.setVisibility(View.GONE);
				}

				@Override
				public void error(Throwable e) {
					super.error(e);
					cover_loading.setVisibility(View.GONE);
					Toast.makeText(bookParent, "您的网络不给力！", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void complete() {
					super.complete();
					try {
						// dismissDialog(0);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					sendBook.setEnabled(true);
					cover_loading.setVisibility(View.GONE);
				}
			});

			// 写入日志
			ActionLogUtil.writeActionLog(bookParent, R.array.book_bookpublishFragement_phoneNum, phone);// 手机号
			ActionLogUtil.writeActionLog(bookParent, R.array.book_bookpublishFragement_bookCar_time, time);// 用车时间
			ActionLogUtil.writeActionLog(bookParent, R.array.book_bookpublishFragement_start_city, startCityName);// 开始城市
			ActionLogUtil.writeActionLog(bookParent, R.array.book_bookpublishFragement_start_addr, strStartAddr);// 上车地点
			ActionLogUtil.writeActionLog(bookParent, R.array.book_bookpublishFragement_start_city, endCityName);// 结束城市
			ActionLogUtil.writeActionLog(bookParent, R.array.book_bookpublishFragement_end_addr, strEndAddr);// 下车地点
			ActionLogUtil.writeActionLog(bookParent, R.array.book_bookpublishFragement_add_price, priceValue);// 加价
			ActionLogUtil.writeActionLog(bookParent, R.array.book_bookpublishFragement_submit, "");// 提交按钮

		} catch (Exception e) {
			cover_loading.setVisibility(View.GONE);
			e.printStackTrace();

		}
	}

	public void selectDateByWheel() {

		/*
		 * Window.selectDate(bookParent, new Callback<String[]>() {
		 * 
		 * @Override public void handle(String[] param) {
		 * 
		 * Date date = new Date(ToolUtil.getLongTime(param)); String strDate =
		 * f_use.format(date); // etUseTime.setText(strDate);
		 * etUseTime.setText(ToolUtil.showTime(date)); submitTime = strDate; }
		 * });
		 */
		Callback<String[]> callBack = new Callback<String[]>() {

			@Override
			public void handle(String[] param) {
				int dayChoosedIndex = Integer.parseInt(param[0]);
				String dayName = param[1];
				int hour = Integer.parseInt(param[2]);
				int minites = Integer.parseInt(param[3]);
				submitTime = new Date(ToolUtil.getLongTimeNew(dayChoosedIndex, hour, minites, AirportBookPublishFragement.removeDays));
				etUseTime.setText(ToolUtil.showTime(dayName, hour, minites));
			}
		};
		Window.selectDate(bookParent, callBack, timeLine, DEFAULT_MIN_USE_TIME, defaultMaxUseCarTime);
	}

	AddressSetBroadCast addrSetReceiver;

	private class AddressSetBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent data) {
			if (addrSetReceiver != null) {
				bookParent.unregisterReceiver(addrSetReceiver);
				addrSetReceiver = null;
			}
			int who = data.getIntExtra("who", 0);
			GeoPointLable point = (GeoPointLable) data.getSerializableExtra("result");
			if (point == null) {
				Toast.makeText(bookParent, "未选择地点", Toast.LENGTH_SHORT).show();
				return;
			}
			if (who == btnStartLoc.getId()) {
				etStartAddr.setText(point.getName());
				etStartAddr.setTag(new GeoPoint((int) point.getLat(), (int) point.getLog()));
			} else {
				etEndAddr.setText(point.getName());
				etEndAddr.setTag(new GeoPoint((int) point.getLat(), (int) point.getLog()));
			}
			setDistance();
		}
	}

	private class LocationBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			p_lat = intent.getIntExtra("latitude", 0);
			p_lng = intent.getIntExtra("longitude", 0);

		}
	}

	private class RegisterReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			mobile = getPassengerId();
			// mobile = session.get("_MOBILE");
			if (etPhone != null) {
				etPhone.setText(mobile);
			}
			NewNetworkRequest.getAddressByLocation(p_lat, p_lng, firstAddressCallback);
		}
	}

	// 初始化广播监听
	private void regReceiver() {
		locationReceiver = new LocationBroadcastReceiver();
		bookParent.registerReceiver(locationReceiver, new IntentFilter(SystemService.BROADCAST_LOCATION));
		registerReceiver = new RegisterReceiver();
		bookParent.registerReceiver(registerReceiver, new IntentFilter(RegisterActivity.ACTION_REGISTER));
	}

	private void unRegReceiver() {
		if (locationReceiver != null) {
			bookParent.unregisterReceiver(locationReceiver);
			locationReceiver = null;
		}

		if (registerReceiver != null) {
			bookParent.unregisterReceiver(registerReceiver);
			registerReceiver = null;
		}

	}

	Callback<String> firstAddressCallback = new Callback<String>() {
		@Override
		public void handle(String param) {
			if (param != null && !isChooseStartCity) {

				etStartAddr.setText(String.valueOf(param));
				etEndAddr.requestFocus();
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		timestamp = System.currentTimeMillis();
	}

	private void setDistance() {
		if (u_lat != 0 && u_lng != 0 && u_lat_end != 0 && u_lng_end != 0) {
			try {
				MapUtil.getRoutePlanAsync(Long.valueOf(mobile), u_lng, u_lat, u_lng_end, u_lat_end, new Callback<JSONObject>() {

					@Override
					public void handle(JSONObject param) {
						if (param != null) {
							try {
								String distance = param.getString("dis");
								String price = param.getString("price");

								AppLog.LogD("xyw", "distance--->" + distance);
								AppLog.LogD("xyw", "price--->" + price);

								String txt = bookParent.getString(R.string.book_distance, BookUtil.getDecimalNumber(Integer.parseInt(distance)), price);
								tvDistanPrice.setText(txt);
							} catch (Exception e) {
								e.printStackTrace();
								tvDistanPrice.setText(bookParent.getString(R.string.addr_tip));
							}
						}
					}

					@Override
					public void error(Throwable e) {
						// TODO Auto-generated method stub
						super.error(e);
						tvDistanPrice.setText(bookParent.getString(R.string.addr_tip));
					}
				});
			} catch (Exception e) {
				tvDistanPrice.setText(getString(R.string.addr_tip));
			}
		}
	};

	/**
	 * 设置用车时间
	 * 
	 * @param minites
	 *            为多少分钟后用车
	 */
	public void setUseCarTime(int minites) {
		submitTime = new Date(System.currentTimeMillis() + minites * 60 * 1000);
		etUseTime.setText( f_show_time.format(submitTime));
	}

	/**
	 * 转换分钟数为天,时,分
	 * 
	 * @param minites
	 */
	public String getTimeStr(int minites) {
		HashMap<String, String> map = TimeTool.formatDuring(minites * 60 * 1000);
		StringBuffer str = new StringBuffer();
		if (Integer.parseInt(map.get("days")) > 0) {
			str.append(map.get("days") + "天");
		}

		if (Integer.parseInt(map.get("hours")) > 0) {
			str.append(map.get("hours") + "小时");
		}

		if (Integer.parseInt(map.get("minutes")) > 0) {
			str.append(map.get("minutes") + "分钟");
		}
		return str.toString();
	}

	private class CutDownTime extends AsyncTask<AlertDialog.Builder, Integer, Boolean> {

		private Builder alert;

		@Override
		protected Boolean doInBackground(Builder... params) {
			// TODO Auto-generated method stub

			alert = params[0];
			publishProgress(5);
			for (int i = 4; i >= 0; i--) {
				try {
					Thread.currentThread().sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				publishProgress(i);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				detailDialog.cancel();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			// alert.setNegativeButton("取消" + values[0], dilClick);
			detailDialog.setButton2("取消" + values[0], dilClick);
		}

	}

	private class LoadBooks extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showMyProgressDialog();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			JsonObject json = new JsonObject();
			json.addProperty("action", "scheduleAction");
			json.addProperty("method", "getActiveBookListByPassenger");
			json.addProperty("passengerId", mobile);
			json.addProperty("size", 10);
			json.addProperty("startId", 0);
			try {
				byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
				if (response != null && response.length > 0) {
					JSONObject jsonObject = new JSONObject(new String(response, "UTF-8"));
					AppLog.LogD("xyw", "book list-->" + jsonObject.toString());
					if (jsonObject.getInt("error") == 0) {
						historyBooks = new ArrayList<BookBean>();
						JSONArray jsonArray = jsonObject.getJSONArray("bookList");
						int length = jsonArray.length();
						JSONObject jsonObjectBookBean;
						for (int i = 0; i < length; i++) {
							jsonObjectBookBean = (JSONObject) jsonArray.get(i);
							BookBean bookBean = new BookBean();
							bookBean.setBookType(getJsonInt(jsonObjectBookBean, "bookType"));
							bookBean.setId(getJsonLong(jsonObjectBookBean, "id"));
							historyBooks.add(bookBean);
						}
					} else {
						// errorcode != 0
						historyBooks = null;
					}
				} else {
					// no datas
					historyBooks = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
				historyBooks = null;
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (historyBooks != null && historyBooks.size() != 0) {
				for (BookBean book : historyBooks) {
					if (book.getBookType() == 4 || book.getBookType() == 3 || book.getBookType() == 5) {
						AlertDialog.Builder alert = showConfirm(book.getId());
						// new CutDownTime().execute(alert);
						break;
					}
				}
			} else {
				// do nothing, 发布新订单
			}

			dismissMyProgressDialog();
		}

	}

	private AlertDialog.Builder showConfirm(final long bookId) {
		// TODO Auto-generated method stub
		try {
			AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());
			alert.setCancelable(false);
			alert.setMessage("您当前还有未完成的订单！");
			alert.setTitle("提示");
			alert.setPositiveButton("查看", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// Intent intent = new
					// Intent(BookPublishFragement.this.getActivity(),
					// NewBookDetail.class);
					Intent intent = new Intent(BookPublishFragement.this.getActivity(), BookHistoryFragementActivity.class);
					intent.putExtra("bookId", bookId);
					BookPublishFragement.this.getActivity().startActivity(intent);
					dialog.dismiss();
				}
			});
			alert.setNegativeButton("取消", dilClick);
			detailDialog = alert.create();
			detailDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			detailDialog.show();

			return alert;
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	private DialogInterface.OnClickListener dilClick = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	};

	public static String getJsonString(JSONObject jsonObject, String name) {
		try {
			return jsonObject.getString(name);
		} catch (Exception e) {
			return "";
		}
	}

	public static int getJsonInt(JSONObject jsonObject, String name) {
		try {
			return Integer.parseInt(jsonObject.getString(name));
		} catch (Exception e) {
			return -1;
		}
	}

	public static long getJsonLong(JSONObject jsonObject, String name) {
		try {
			return Long.parseLong(jsonObject.getString(name));
		} catch (Exception e) {
			return -1;
		}
	}

	public void showMyProgressDialog(String msg) {
		if (!TextUtils.isEmpty(msg)) {
			mProgressDialog.setMessage(msg);
		}
		mProgressDialog.show();
	}

	public void showMyProgressDialog() {
		showMyProgressDialog(null);
	}

	public void dismissMyProgressDialog() {
		mProgressDialog.dismiss();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub

		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (v instanceof RadioButton) {
				if (((RadioButton) v).isChecked()) {
					int height = this.getActivity().getWindowManager().getDefaultDisplay().getHeight();
					int[] location = new int[2];
					v.getLocationOnScreen(location);
					if (v == rbCarOne) {
						Window.showCarDetails(this.getActivity(), 1, height - location[1]);
					} else if (v == rbCarTwo) {
						Window.showCarDetails(this.getActivity(), 2, height - location[1]);
					}
				}
			}
		}
		return false;
	}
}
