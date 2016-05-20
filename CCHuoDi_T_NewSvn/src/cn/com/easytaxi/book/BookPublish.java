package cn.com.easytaxi.book;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.BookConfig;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.NewNetworkRequest;
import cn.com.easytaxi.NewNetworkRequest.DiaoDuPrice;
import cn.com.easytaxi.NewNetworkRequest.DioaDuPrices;
import cn.com.easytaxi.common.Callback;
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
import cn.com.easytaxi.workpool.BaseActivity;
import cn.com.easytaxi.workpool.bean.GeoPointLable;

import com.baidu.mapapi.model.inner.GeoPoint;
import com.easytaxi.etpassengersx.R;
import com.google.gson.Gson;

public class BookPublish extends BaseActivity implements View.OnClickListener {
	
	private Context context;
	protected static final int START_CITY_REQ_CODE = 400;

	protected static final int END_CITY_REQ_CODE = 401;

	public static final int D_TIME = 10;

	private static SimpleDateFormat f_use = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	 
	private String cityId;
	private String cityName;
	private String mobile;

	InputMethodManager imm;

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

	private String submitTime;
	private View sendBook;
	private Button btnStartLoc;
	private Button btnEndLoc;
	private EditText etPhone;
	private EditText etUseTime;
	private TextView etStartAddr;
	private TextView etEndAddr;

	private TextView tvDistanPrice;
	private long timestamp;

	protected int priceKey = 3;
	protected String priceValue = "3";
	protected List<DiaoDuPrice> priceList = new ArrayList<NewNetworkRequest.DiaoDuPrice>(12);
	private Callback<Object> priceCallback = new Callback<Object>() {

		@Override
		public void handle(Object param) {
			if (param != null) {
				priceList = (List<DiaoDuPrice>) param;
				initPriveViewcell(priceList);
			}
		}
	};

	private Button end_city;

	private Button start_city;

	protected boolean isChooseStartCity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_publish_rebook);
		context = getBaseContext();
		catchCrash();
		imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

		timestamp = System.currentTimeMillis();
		mobile = getPassengerId();

		p_lat = ETApp.getInstance().getCacheInt("_P_LAT");
		p_lng = ETApp.getInstance().getCacheInt("_P_LNG");

		u_lat = p_lat;
		u_lng = p_lng;

		regReceiver();
		initTitleBar();
		initViews();
		initListenrs();
		reBook();

		chatAddMoneyPager = (LinearLayout) findViewById(R.id.chat_addmoney_pager);

		try {
			initPrice();
		} catch (Exception e) {
			// TODO: handle exception
			// 首次使用，价格为空
		}

		NewNetworkRequest.getDiaoDuPriceList(2, priceCallback);
	}



	private void initPrice() {
		Gson gson = new Gson();
		DioaDuPrices dioaDuPrices = gson.fromJson(ETApp.getInstance().getCacheString("book_pricelist"), DioaDuPrices.class);
		priceList = dioaDuPrices.prices;
		priceKey = dioaDuPrices.index;
		initPriveViewcell(priceList);
	}
	
	

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			ProgressDialog pd = new ProgressDialog(this);
			pd.setMessage("请稍后...");
			return pd;
		default:
			break;
		}
		return super.onCreateDialog(id);
	}

	private void initTitleBar() {
		cityId = session.get("_CITY_ID");
		cityName = session.get("_CITY_NAME");

		titleBar = new TitleBar(BookPublish.this);
		titleBar.setTitleName("预约打车");
	}

	private void initViews() {
		end_city = (Button)findViewById(R.id.end_city);
		start_city = (Button)findViewById(R.id.start_city);
		
		end_city.setText(cityName);
		start_city.setText(cityName);
		startCityName = cityName;
		startCityId = cityId;
		endCityName = cityName;
		endCityId = cityId;
		
		sendBook = findViewById(R.id.book_send);
		tvDistanPrice = (TextView) findViewById(R.id.book_publish_distance);
		btnStartLoc = (Button) findViewById(R.id.btn_book_startloc);
		btnEndLoc = (Button) findViewById(R.id.btn_book_endloc);
		etPhone = (EditText) findViewById(R.id.et_book_phone);
		etUseTime = (EditText) findViewById(R.id.et_book_usetime);
		etStartAddr = (TextView) findViewById(R.id.et_book_startaddr);
		etEndAddr = (TextView) findViewById(R.id.et_book_endaddr);
		etPhone.setText(mobile);
		
		Date date = new Date(System.currentTimeMillis()+D_TIME*60*1000);
		etUseTime.setText(ToolUtil.showTime(date));
		submitTime = f_use.format(date);

	}
	
	private void initListenrs() {
		etPhone.setOnClickListener(this);
		etStartAddr.setOnClickListener(this);
		etEndAddr.setOnClickListener(this);
		btnStartLoc.setOnClickListener(this);
		btnEndLoc.setOnClickListener(this);

		etStartAddr.setText(this.getIntent().getStringExtra("startAddress"));
		sendBook.setOnClickListener(this);
		etUseTime.setOnClickListener(this);
		etStartAddr.setOnClickListener(this);
		etEndAddr.setOnClickListener(this);
		
		end_city.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BookPublish.this, CitySelectActivity.class);
				 
				startActivityForResult(intent, END_CITY_REQ_CODE);
			}
		});
		
		start_city.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isChooseStartCity = true;
				Intent intent = new Intent(BookPublish.this, CitySelectActivity.class);
				startActivityForResult(intent, START_CITY_REQ_CODE);
			}
		});
		
	}
	

	private void switchAddress(int type,String cityName) {
		Intent intent = new Intent(BookPublish.this, SearchAddressActivity.class);
		intent.putExtra("cityName", cityName);
		AppLog.LogD(cityName);
		startActivityForResult(intent, type);
	}

	private void reBook() {
		if ("cn.com.easytaxi.book.resubmit".equals(this.getIntent().getAction())) {
			Intent i = this.getIntent();
			etStartAddr.setText(i.getStringExtra("startAddr"));
			etEndAddr.setText(i.getStringExtra("endAddr"));
			etPhone.setText(i.getStringExtra("mobile"));
			u_lat = i.getIntExtra("startLat", 0);
			u_lng = i.getIntExtra("startLng", 0);
			u_lat_end = i.getIntExtra("endLat", 0);
			u_lng_end = i.getIntExtra("endLng", 0);
		} else {
			AppLog.LogD("xyw", "new publish");
			NewNetworkRequest.getAddressByLocation(p_lat, p_lng, firstAddressCallback);
		}
	}

	private void initPriveViewcell(List<DiaoDuPrice> priceList) {
		// int len = priceList.size();
		LayoutInflater inflater = getLayoutInflater();
		chatAddMoneyPager.removeAllViews();
		for (DiaoDuPrice p : priceList) {
			final DiaoDuPrice pp = p;
			if(p == null){
				continue;
			}
			
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
	protected void onDestroy() {
		// TODO Auto-generated method stub

		titleBar.close();

		if (addrSetReceiver != null) {
			this.unregisterReceiver(addrSetReceiver);
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
		case R.id.et_book_endaddr:
			etPhone.setCursorVisible(false);
			switchAddress(2,endCityName);
			AppLog.LogD("xyw", "et_book_endaddr"+ endCityName);
			break;
		case R.id.et_book_startaddr:
			etPhone.setCursorVisible(false);
			switchAddress(1,startCityName);
			AppLog.LogD("xyw", "et_book_startaddr");
			break;

		case R.id.btn_book_startloc:
			etPhone.setCursorVisible(false);
			switchAddress(1,startCityName);
			break;
		case R.id.btn_book_endloc:
			etPhone.setCursorVisible(false);
			switchAddress(2,endCityName);
			break;

		case R.id.et_book_usetime:
			etPhone.setCursorVisible(false);
			selectDateByWheel();
			break;
		case R.id.book_send:
			check();
			break;

		default:
			break;
		}
	}

	private void check() {
		if (isLogin()) {

			final String phone = etPhone.getText().toString().trim();
			// final String time = etUseTime.getText().toString().trim();
			final String strStartAddr = etStartAddr.getText().toString().trim();
			final String strEndAddr = etEndAddr.getText().toString().trim();

			try {

				if (!phone.matches(Util.REGEX_MOBILE)) {
					Toast.makeText(self, "电话号码不正确", Toast.LENGTH_SHORT).show();
					etPhone.requestFocus();
					return;
				}

				Calendar now = Calendar.getInstance();
				Calendar use = Calendar.getInstance();

				if (submitTime == null) {// 现在用车
					submitTime = f_use.format(System.currentTimeMillis() + 30 * 60 * 1000);
					use.setTime(f_use.parse(submitTime));
				} else {
					use.setTime(f_use.parse(submitTime));
				}
				Calendar limit = Calendar.getInstance();
				limit.set(Calendar.YEAR, now.get(Calendar.YEAR));
				limit.set(Calendar.MONTH, now.get(Calendar.MONTH));
				limit.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH) + 2);
				limit.set(Calendar.HOUR_OF_DAY, 0);
				limit.set(Calendar.MINUTE, 0);
				limit.set(Calendar.SECOND, 0);
				d("date:>>>+now:" + f_use.format(now.getTime()));
				d("date:>>>+use:" + submitTime);
				d(">>>+limit:" + f_use.format(limit.getTime()));
				try {
					if (use.getTimeInMillis() <= now.getTimeInMillis()) {
						selectDateByWheel();
						Toast.makeText(self, "用车时间必须大于当前时间", Toast.LENGTH_SHORT).show();
						return;
					}
					if (use.getTimeInMillis() >= limit.getTimeInMillis()) {
						selectDateByWheel();
						Toast.makeText(self, "用车时间必须为今天或明天", Toast.LENGTH_SHORT).show();
						return;
					}
				} catch (Exception e) {
					return;
				}
				if (strStartAddr.equals("")) {
					Toast.makeText(self, "请输入上车地点", Toast.LENGTH_SHORT).show();
					etStartAddr.requestFocus();
					return;
				}
				if (strEndAddr.equals("")) {
					Toast.makeText(self, "请输入下车地点", Toast.LENGTH_SHORT).show();
					etEndAddr.requestFocus();
					return;
				}

				// if (startAddr.getTag().equals(endAddr.getTag())) {
				// Toast.makeText(self, "起点不能和终点一样",
				// Toast.LENGTH_SHORT).show();
				// endAddr.requestFocus();
				// return;
				// }

			} catch (Exception e) {
				e.printStackTrace();
			}
			send(phone, submitTime, strStartAddr, strEndAddr);
		} else {

			Intent intent = new Intent(BookPublish.this, RegisterActivity.class);
			startActivity(intent);

		}
	}

	private void send(String phone, String time, String strStartAddr, String strEndAddr) {

		showDialog(0);
		sendBook.setEnabled(false);
		try {
			int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
			final JSONObject json = new JSONObject();
			json.put("action", "scheduleAction");
			json.put("method", "submitBook");
			json.put("bookType", 5);

//			json.put("cityId", cityId);
//			json.put("cityName", cityName);
			
			json.put("timestamp", timestamp);
			json.put("cityId", startCityId);
			json.put("cityName", startCityName);
			json.put("clientType", BookConfig.ClientType.CLIENT_TYPE_PASSENGER);
			
			json.put("passengerPhone", phone);
			// json.put("passengerName", 5);
//			json.put("passengerId", phone);
			//BUG --FIXED------------
			json.put("passengerId", ETApp.getInstance().getCurrentUser().getPhoneNumber(User._MOBILE));
			
			json.put("source", 1);
			json.put("sourceName", "android." + versionCode);

			json.put("startAddress", strStartAddr);
			if (u_lng == 0 || u_lat == 0) {
				json.put("startLongitude", u_lng);
				json.put("startLatitude", u_lat);
			} else {
				json.put("startLongitude", p_lng);
				json.put("startLatitude", p_lat);
			}

			json.put("endAddress", strEndAddr);
			json.put("endLongitude", u_lng_end);
			json.put("endLatitude", u_lat_end);

			json.put("useTime", time);
			json.put("price", priceValue);

			AppLog.LogD("xyw", "submit moblie:" + mobile);
			AppLog.LogD("xyw", "submit json:" + json.toString());

			SocketUtil.getJSONObject(Long.valueOf(mobile), json, new Callback<JSONObject>() {

				@Override
				public void handle(JSONObject param) {

					try {
						if (param != null) {
							if (param.getInt("error") == 0) {

								AppLog.LogD("xyw", param.toString());
								Toast.makeText(self, "提交成功", Toast.LENGTH_SHORT).show();
								Intent intent = new Intent("cn.com.easytaxi.book.refresh_list");
								intent.putExtra("fromSubmited", true);
								sendBroadcast(intent);
								finish();
								// startActivity(new Intent(BookPublish.this,
								// BookListActivity.class));
							} else {
								Toast.makeText(self, "您的网络不给力！", Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(self, "您的网络不给力！", Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void error(Throwable e) {
					super.error(e);
					Toast.makeText(self, "您的网络不给力！", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void complete() {
					super.complete();
					try {
						dismissDialog(0);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					sendBook.setEnabled(true);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			try {
				dismissDialog(0);
			} catch (Exception e1) {
				e1.printStackTrace();
				Toast.makeText(self, "您的网络不给力！", Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void selectDateByWheel() {

		Window.selectDate(context, new Callback<String[]>() {

			@Override
			public void handle(String[] param) {

				Date date = new Date(ToolUtil.getLongTime(param));
				String strDate = f_use.format(date);
				// etUseTime.setText(strDate);
				etUseTime.setText(ToolUtil.showTime(date));
				submitTime = strDate;
			}
		});
	
		
		/*
		Window.selectDate(self, new Callback<Integer[]>() {

			@Override
			public void handle(Integer[] param) {
				Resources res = self.getResources();
				final String[] hours = res.getStringArray(R.array.clockadd_hour_array);
				final String[] mins = res.getStringArray(R.array.clockadd_4mins_array);

				Date date = new Date();
				// 设置day
				if (param[0] == 1) {
					date.setDate(date.getDate() + 1);
				} else {
				}

				date.setHours(Integer.parseInt(hours[param[1]]));
				date.setMinutes(Integer.parseInt(mins[param[2]]));
				date.setSeconds(0);
				String strDate = f_use.format(date);
				// etUseTime.setText(strDate);
				etUseTime.setText(ToolUtil.showTime(date));
				submitTime = strDate;
			}
		});
	*/}

	AddressSetBroadCast addrSetReceiver;

	private String endCityName;

	private String endCityId;

	private String startCityName;

	private String startCityId;

	private class AddressSetBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent data) {
			if (addrSetReceiver != null) {
				unregisterReceiver(addrSetReceiver);
				addrSetReceiver = null;
			}
			int who = data.getIntExtra("who", 0);
			GeoPointLable point = (GeoPointLable) data.getSerializableExtra("result");
			if (point == null) {
				Toast.makeText(BookPublish.this, "未选择地点", Toast.LENGTH_SHORT).show();
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1 && resultCode == RESULT_OK) {
			u_lat = data.getIntExtra("lat", 0);
			u_lng = data.getIntExtra("lng", 0);
			etStartAddr.setText(data.getStringExtra("address"));
		}
		if (requestCode == 2 && resultCode == RESULT_OK) {
			u_lat_end = data.getIntExtra("lat", 0);
			u_lng_end = data.getIntExtra("lng", 0);
			etEndAddr.setText(data.getStringExtra("address"));
		}
		if (requestCode == END_CITY_REQ_CODE && resultCode == RESULT_OK) {
			endCityName = data.getStringExtra("cityName");//
			endCityId = data.getStringExtra("cityId");
			AppLog.LogD("endCityName " + endCityName + " ,endCityId  " + endCityId);
			
			if(endCityName.length()>4){
				
				end_city.setText(TextUtils.substring(endCityName, 0, 4));
			}else{
				end_city.setText(endCityName);
				
			}
		}
		if (requestCode == START_CITY_REQ_CODE && resultCode == RESULT_OK) {
			startCityName = data.getStringExtra("cityName");
			startCityId = data.getStringExtra("cityId");//;", 0);
			endCityId = startCityId;
			endCityName = startCityName;
			etStartAddr.setText("");
 
			AppLog.LogD("endCityName " + startCityName + " ,endCityId  " + startCityId +" , cityName" + cityName);
			
			if(startCityName.length()>4){
				start_city.setText(TextUtils.substring(startCityName, 0, 4));
				end_city.setText(TextUtils.substring(startCityName, 0, 4));
			}else{
				start_city.setText(startCityName);
				end_city.setText(startCityName);
			}
			
			
		}
  
		setDistance();
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

			mobile = session.get("_MOBILE");
			if (etPhone != null) {
				etPhone.setText(mobile);
			}

			NewNetworkRequest.getAddressByLocation(p_lat, p_lng, firstAddressCallback);
		}
	}

	// 初始化广播监听
	private void regReceiver() {
		locationReceiver = new LocationBroadcastReceiver();
		registerReceiver(locationReceiver, new IntentFilter(SystemService.BROADCAST_LOCATION));

		registerReceiver = new RegisterReceiver();
		registerReceiver(registerReceiver, new IntentFilter(RegisterActivity.ACTION_REGISTER));

 
	}

	private void unRegReceiver() {
		if (locationReceiver != null) {
			unregisterReceiver(locationReceiver);
			locationReceiver = null;
		}

		if (registerReceiver != null) {
			unregisterReceiver(registerReceiver);
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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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

								String txt = BookPublish.this.getString(R.string.book_distance,
										BookUtil.getDecimalNumber(Integer.parseInt(distance)), price);
								tvDistanPrice.setText(txt);
							} catch (Exception e) {
								e.printStackTrace();
								tvDistanPrice.setText(BookPublish.this.getString(R.string.addr_tip));
							}
						}
					}

					@Override
					public void error(Throwable e) {
						// TODO Auto-generated method stub
						super.error(e);
						tvDistanPrice.setText(BookPublish.this.getString(R.string.addr_tip));
					}
				});
			} catch (Exception e) {
				tvDistanPrice.setText(getString(R.string.addr_tip));
			}
		}
	};
}
