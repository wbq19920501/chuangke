package cn.com.easytaxi.airport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.BookConfig;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.NewNetworkRequest;
import cn.com.easytaxi.NewNetworkRequest.TimeLines;
import cn.com.easytaxi.NewNetworkRequest.TipPrices;
import cn.com.easytaxi.airport.view.ScrollingTextView;
import cn.com.easytaxi.book.BookUtil;
import cn.com.easytaxi.book.NewBookDetail;
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
import cn.com.easytaxi.workpool.bean.GeoPointLable;

import com.baidu.mapapi.model.inner.GeoPoint;
import com.easytaxi.etpassengersx.R;
import com.easytaxi.etpassengersx.wxapi.WXPayEntryActivity;

public class AirportBookPublishFragement extends AirportBookBaseFragement implements View.OnClickListener {
	protected static final int START_CITY_REQ_CODE = 400;
	protected static final int END_CITY_REQ_CODE = 401;
	protected static final int AIRPORT_REQ_CODE = 69;

	private static SimpleDateFormat f_use = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String cityId;
	private String cityName;
	private String mobile;
	/**
	 * 是否强制在线支付： 0代表不需要在线支付；1表示需要强制在线支付
	 */
    public int onlinePayment = 0;
    private String tempstr = "高峰期适当加价，有助于更快坐到车";
    
	public static int removeDays;
	// InputMethodManager imm;

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
	private ImageButton switchButton;
	private EditText etPhone;
	private EditText etUseTime;
	private TextView etStartAddr;
	private TextView etEndAddr;
	private TextView tvDistanPrice;
	private ImageView startIcon;
	private ImageView endIcon;
    
	private String endCityName;

	private String endCityId;

	private String startCityName;

	private String startCityId;

	private Button end_city;
	private ToggleButton payMode;
	private Button start_city;
	/**
	 * 滚动提示文字：显示加价上面的提示文字
	 */
	private ScrollingTextView scrollingTextView;
	/**
	 * 航班号
	 */
	private EditText airplan_number_edit;
    private String airplan_number;
    
	private View cover_loading;
	
	/**
	 * 存储表示机场的textView的id
	 */
	private int airportTextViewId = R.id.et_airport_book_endaddr;//默认下车地点view为机场

	protected int priceKey = 0;
	protected String priceValue = "0";
//	protected List<DiaoDuPrice> priceList = new ArrayList<NewNetworkRequest.DiaoDuPrice>(12);
	private int[] priceList;
	private Callback<Object> priceCallback = new Callback<Object>() {

		@Override
		public void handle(Object param) {
			try{
				if (param != null) {
					TipPrices tipPrices = (NewNetworkRequest.TipPrices) param;
					if(tipPrices.error == 0){//有价格列表
						priceList =  tipPrices.priceList;
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
					}else{//没获取到价格列表
						priceList = new int[]{0};
						Toast.makeText(bookParent,tipPrices.errormsg, Toast.LENGTH_LONG).show();
					}
					priceKey =  priceList[0];
					priceValue =  priceList[0]+"";
					initPriveViewcell(priceList);
					initScrollingTextView(tempstr);
				}
			}catch(Exception e){
				Toast.makeText(bookParent, "没有获取到切换城市的价格列表...", Toast.LENGTH_LONG).show();
			}
		}
	};
	
	/**
	 * 默认的订车最小时间：单位分钟
	 */
	private int defaultMinUseCarTime = 30;
	/**
	 * 默认的订车最大时间：单位分钟
	 */
	private int defaultMaxUseCarTime = 3*24*60;
	
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
				ArrayList<NewNetworkRequest.TimeLine> timeLineList =  timeLines.datas;
				if(timeLineList.size()>0){
					timeLine = timeLineList.get(0);
					//获取到时间上下线，重置默认的用车时间
					setUseCarTime(Integer.parseInt(timeLine.lower));
				}
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mobile = getPassengerId();
		cityName = bookParent.getCityName();
		cityId = bookParent.getCityId();
		if(cityName == null){//默认为北京市
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
		View view = inflater.inflate(R.layout.airport_book_publish, container, false);
		init(view);
		regReceiver();//当用户去注册时候的接收器
		NewNetworkRequest.getAddressByLocation(p_lat, p_lng, firstAddressCallback);
		
		initPrice(Const.AIRPORT_PRICELIST);
		
		new Thread(){
			public void run() {
				
				NewNetworkRequest.getDiaoDuPriceList(Integer.parseInt(cityId), Const.AIRPORT_BOOK_TYPE, BookConfig.ClientType.CLIENT_TYPE_PASSENGER, priceCallback);
				
			};
		}.start();
		
		//获取时间上线限制 
		String version = ""+ETApp.getInstance().getMobileInfo().getVerisonCode();
		NewNetworkRequest.getTimeDeadLine((Integer.parseInt(cityId)), Const.AIRPORT_BOOK_TYPE, BookConfig.ClientType.CLIENT_TYPE_PASSENGER,version, timeLinesCallBack);
		
		return view;
	}
	
	private void initPrice(String priceListCacheName) {
		try{
			TipPrices tips = new TipPrices();
			JSONObject js = new JSONObject(ETApp.getInstance().getCacheString(priceListCacheName));
			tips.error = js.getInt("error");
			tips.errormsg = js.getString("errormsg");
			tips.onlinePayment = js.getJSONObject("datas").getInt("onlinePayment");
			JSONArray jsArray =  js.getJSONObject("datas").getJSONArray("priceList");
			int length = jsArray.length();
			tips.priceList = new int[length];
			for(int i=0;i<length;i++){
				tips.priceList[i] = jsArray.getInt(i);
			}
			
			if(null != tips){
				priceList = tips.priceList;
			}
			if(null != priceList && priceList.length>0){
				priceKey =  priceList[0];
				priceValue =  priceList[0]+"";
				initPriveViewcell(priceList);
				tempstr = js.getJSONObject("datas").getString("msg");
				initScrollingTextView(tempstr);
				onlinePayment = tips.onlinePayment;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 初始化滚动提示加价的ScrollingTextView内容
	 * @param price
	 */
	private void initScrollingTextView(String str) {
		scrollingTextView.setText(str);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
			if(airportTextViewId == R.id.et_airport_book_startaddr){//起始位置为飞机
				u_lng_end = data.getIntExtra("lng",0);
				u_lat_end = data.getIntExtra("lat",0);
				etEndAddr.setText(data.getStringExtra("address"));
			}else if(airportTextViewId == R.id.et_airport_book_endaddr){//结束位置为飞机
				u_lng = data.getIntExtra("lng",0);
				u_lat = data.getIntExtra("lat",0);
				etStartAddr.setText(data.getStringExtra("address"));
			}
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
			//刷新价格列表为当前所选出发城市的价格
			NewNetworkRequest.getDiaoDuPriceList(Integer.parseInt(startCityId), Const.AIRPORT_BOOK_TYPE, BookConfig.ClientType.CLIENT_TYPE_PASSENGER, priceCallback);

		}
		
		// 乘客起始地点位置
//		int u_lat = 0;
//		int u_lng = 0;
//
//		// 乘客结束地点位置
//		int u_lat_end = 0;
//		int u_lng_end = 0;
		//获取机场数据
		if(requestCode == AIRPORT_REQ_CODE && resultCode == Activity.RESULT_OK){
			if(airportTextViewId == R.id.et_airport_book_startaddr){//起始位置为飞机
//				data.getStringExtra("id");
//				data.getStringExtra("name");
				u_lng = data.getIntExtra("longitude",0);
				u_lat = data.getIntExtra("latitude",0);
				etStartAddr.setText(data.getStringExtra("name"));
			}else if(airportTextViewId == R.id.et_airport_book_endaddr){//结束位置为飞机
				u_lng_end = data.getIntExtra("longitude",0);
				u_lat_end = data.getIntExtra("latitude",0);
				etEndAddr.setText(data.getStringExtra("name"));
			}
		}

		setDistance();

	}

	protected boolean isChooseStartCity;

	private void init(View view) {
		scrollingTextView = (ScrollingTextView)view.findViewById(R.id.scrollingTextView);
		payMode = (ToggleButton) view.findViewById(R.id.pay_mode);
		payMode.setChecked(false);
		airplan_number_edit = (EditText)view.findViewById(R.id.airplan_number_edit);
		startIcon = (ImageView) view.findViewById(R.id.start_icon);
		endIcon = (ImageView) view.findViewById(R.id.end_icon);
        switchButton = (ImageButton) view.findViewById(R.id.switch_icon);
//        ActionLogUtil.setViewTag(switchButton, bookParent, R.array.switch_icon);
        
		end_city = (Button) view.findViewById(R.id.end_airport);
		start_city = (Button) view.findViewById(R.id.start_airport);
		cover_loading = view.findViewById(R.id.airport_cover_loading);
        
		end_city.setText(cityName);
		start_city.setText(cityName);
		startCityName = cityName;
		startCityId = cityId;
		endCityName = cityName;
		endCityId = cityId;

		chatAddMoneyPager = (LinearLayout) view.findViewById(R.id.chat_addmoney_pager);
		sendBook = view.findViewById(R.id.book_send);
		tvDistanPrice = (TextView) view.findViewById(R.id.book_airport_publish_distance);
		btnStartLoc = (Button) view.findViewById(R.id.btn_airport_book_startloc);
		btnEndLoc = (Button) view.findViewById(R.id.btn_airport_book_endloc);
		etPhone = (EditText) view.findViewById(R.id.et_airport_book_phone);

		etUseTime = (EditText) view.findViewById(R.id.et_airport_book_usetime);
		etStartAddr = (TextView) view.findViewById(R.id.et_airport_book_startaddr);
		
		etEndAddr = (TextView) view.findViewById(R.id.et_airport_book_endaddr);
		etPhone.setText(mobile);
        
		//初始化默认为30分钟后用车
		setUseCarTime(defaultMinUseCarTime);

		etPhone.setOnClickListener(this);
		etStartAddr.setOnClickListener(this);
		etEndAddr.setOnClickListener(this);
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

			}
		});
		
		//交换接机送机模式
		switchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//银联充值
//				Intent intent = new Intent(bookParent,PayMent.class);
//				startActivity(intent);
		
				if(airportTextViewId == R.id.et_airport_book_endaddr){
					//交换图标显示
					startIcon.setImageResource(R.drawable.airport_start);
					endIcon.setImageResource(R.drawable.pic155_end);
					//修改提示语
					etStartAddr.setHint("接机地址");
					etEndAddr.setHint("下车地址");
					//交换默认的哪个view为机场选项
					airportTextViewId = R.id.et_airport_book_startaddr;
				}else if(airportTextViewId == R.id.et_airport_book_startaddr){
					//交换图标显示
					startIcon.setImageResource(R.drawable.pic155);
					endIcon.setImageResource(R.drawable.airport);
					//修改提示语
					etStartAddr.setHint("上车地址");
					etEndAddr.setHint("送机地址");
					//交换默认的哪个view为机场选项
					airportTextViewId = R.id.et_airport_book_endaddr;
				}
				//交换城市显示
				String str = startCityName;
				if(endCityName.length()>2){
					start_city.setText(endCityName.substring(0, 2));
				}else{
					start_city.setText(endCityName);
				}
				startCityName = endCityName;
				if(str.length()>2){
					end_city.setText(str.substring(0, 2));
				}else{
					end_city.setText(str);
				}
				
				endCityName= str;
				//交换城市id
				str = startCityId;
				startCityId = endCityId;
				endCityId = str;
				
				//交换起始坐标
				int lat = u_lat;
			    int lng = u_lng;
			    u_lat = u_lat_end;
			    u_lng = u_lng_end;
			    u_lat_end = lat;
			    u_lng_end = lng;
				//交换起始地址
			    String addr = etStartAddr.getText().toString();
			    etStartAddr.setText(etEndAddr.getText().toString());
			    etEndAddr.setText(addr);
				
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
	 * @param priceList 价格列表
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
						cbb.setText(ppp+"");
						priceKey = ppp;
						priceValue = ppp+"";
						cbb.setChecked(true);
					}
				}
			});
			if (priceKey == p) {
				priceValue = p+"";
				cb.setChecked(true);
			}

			cb.setText(p+"");
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
		case R.id.et_airport_book_phone:
			etPhone.setCursorVisible(true);
			etPhone.setSelection(etPhone.getText().toString().length());
			break;
		case R.id.et_airport_book_endaddr://下车地点
			etPhone.setCursorVisible(false);
			if(R.id.et_airport_book_endaddr == airportTextViewId){//进入机场列表
				Intent intent = new Intent(bookParent,AirportSelectActivity.class);
				intent.putExtra("cityName", endCityName);
				intent.putExtra("choosedAirportName",etEndAddr.getText().toString());
				startActivityForResult(intent,AIRPORT_REQ_CODE);
			}else if(R.id.et_airport_book_startaddr == airportTextViewId){//进入地点查询
				switchAddress(1, endCityName);
			}
			
			break;
		case R.id.et_airport_book_startaddr://上车地点
			etPhone.setCursorVisible(false);
			if(R.id.et_airport_book_startaddr == airportTextViewId){//进入机场列表
				Intent intent = new Intent(bookParent,AirportSelectActivity.class);
				intent.putExtra("cityName", startCityName);
				intent.putExtra("choosedAirportName", etStartAddr.getText().toString());
				startActivityForResult(intent,AIRPORT_REQ_CODE);
			}else{
				switchAddress(1, startCityName);
			}
			
			break;

		case R.id.btn_airport_book_startloc:
			etPhone.setCursorVisible(false);
			switchAddress(1, startCityName);
			break;
		case R.id.btn_airport_book_endloc:
			etPhone.setCursorVisible(false);
			switchAddress(2, endCityName);
			break;

		case R.id.et_airport_book_usetime:
			etPhone.setCursorVisible(false);
			selectDateByWheel();
			break;
		case R.id.book_send://提交
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
            airplan_number = airplan_number_edit.getText().toString().trim();
			try {

				if (!phone.matches(Util.REGEX_MOBILE)) {
					Toast.makeText(bookParent, "电话号码不正确", Toast.LENGTH_SHORT).show();
					etPhone.requestFocus();
					return;
				}
				
				Calendar now = Calendar.getInstance();
				Calendar use = Calendar.getInstance();

				if (submitTime == null) {// 现在用车
					setUseCarTime(defaultMinUseCarTime);
					use.setTime(f_use.parse(submitTime));
				} else {
					use.setTime(f_use.parse(submitTime));
				}
//				Calendar limit = Calendar.getInstance();
//				limit.set(Calendar.YEAR, now.get(Calendar.YEAR));
//				limit.set(Calendar.MONTH, now.get(Calendar.MONTH));
//				limit.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH) + 3);
//				limit.set(Calendar.HOUR_OF_DAY, 0);
//				limit.set(Calendar.MINUTE, 0);
//				limit.set(Calendar.SECOND, 0);
				AppLog.LogD("date:>>>+now:" + f_use.format(now.getTime()));
				AppLog.LogD("date:>>>+use:" + submitTime);
//				AppLog.LogD(">>>+limit:" + f_use.format(limit.getTime()));
				try {
					if (use.getTimeInMillis() <= now.getTimeInMillis()) {
						selectDateByWheel();
						Toast.makeText(bookParent, "用车时间必须大于当前时间", Toast.LENGTH_SHORT).show();
						return;
					}
//					if (use.getTimeInMillis() >= limit.getTimeInMillis()) {
//						selectDateByWheel();
//						Toast.makeText(bookParent, "用车时间必须为今天,明天或后天", Toast.LENGTH_SHORT).show();
//						return;
//					}
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

				// if (startAddr.getTag().equals(endAddr.getTag())) {
				// Toast.makeText(bookParent, "起点不能和终点一样",
				// Toast.LENGTH_SHORT).show();
				// endAddr.requestFocus();
				// return;
				// }

			} catch (Exception e) {
				e.printStackTrace();
			}
			send(phone, submitTime, strStartAddr, strEndAddr);
		} else {

			Intent intent = new Intent(bookParent, RegisterActivity.class);
			startActivity(intent);

		}
	}

	private void send(String phone, String time, String strStartAddr, String strEndAddr) {
		if(payMode.isChecked() && Integer.parseInt(priceValue) == 0){
			Toast.makeText(bookParent, "线上付不能为0元", Toast.LENGTH_SHORT).show();
			return;
		}
		sendBook.setEnabled(false);
		try {
			int versionCode = ETApp.getInstance().getMobileInfo().getVerisonCode();
			final JSONObject json = new JSONObject();
			json.put("action", "scheduleAction");
			json.put("method", "submitBook");
			json.put("bookType", Const.AIRPORT_BOOK_TYPE);

			//航班号
			json.put("flightNo", airplan_number);
			// json.put("cityId", cityId);
			// json.put("cityName", cityName);

			json.put("cityId", startCityId);
			json.put("cityName", startCityName);
			json.put("clientType", BookConfig.ClientType.CLIENT_TYPE_PASSENGER);
			json.put("clientVersion",ETApp.getInstance().getMobileInfo().getVerisonCode());
			
			json.put("passengerPhone", phone);
			// json.put("passengerName", 5);
			// json.put("passengerId", phone);
			// BUG --FIXED------------
			json.put("passengerId", ETApp.getInstance().getCurrentUser().getPhoneNumber(User._MOBILE));

			json.put("source", 1);
			json.put("sourceName", "android." + versionCode);

			json.put("startAddress", strStartAddr);
			
			if (u_lng == 0 && u_lat == 0) {//默认使用用户当前地址
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

//			priceValue = InfoTool.spliteWiht4hour(time);

			json.put("price", priceValue);
			json.put("payment", priceValue);
			
			
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
			
			//写入日志
			ActionLogUtil.writeActionLog(bookParent,R.array.airport_phone_numb,phone);//手机号
			ActionLogUtil.writeActionLog(bookParent,R.array.airport_use_time, time);//用车时间
			ActionLogUtil.writeActionLog(bookParent,R.array.airport_start_city, startCityName);//开始城市
			ActionLogUtil.writeActionLog(bookParent,R.array.airport_start_addr, strStartAddr);//上车地点
			ActionLogUtil.writeActionLog(bookParent,R.array.airport_end_city, endCityName);//结束城市
			ActionLogUtil.writeActionLog(bookParent,R.array.airport_end_addr,strEndAddr);//下车地点
			ActionLogUtil.writeActionLog(bookParent,R.array.airport_add_price,priceValue);//加价
			ActionLogUtil.writeActionLog(bookParent,R.array.airport_submit,"");//提交按钮
			
		} catch (Exception e) {
			cover_loading.setVisibility(View.GONE);
			e.printStackTrace();

		}
	}

	public void selectDateByWheel() {
		/*Callback<String[]> callBack = new Callback<String[]>() {

			@Override
			public void handle(String[] param) {

				Date date = new Date(ToolUtil.getLongTime(param));
				String strDate = f_use.format(date);
				// etUseTime.setText(strDate);
				etUseTime.setText(ToolUtil.showTime(date));
				submitTime = strDate;
			}
		};
		Window.selectDate(bookParent, new Callback<String[]>() {*/
		
		Callback<String[]> callBack = new Callback<String[]>() {

			@Override
			public void handle(String[] param) {
                int dayChoosedIndex = Integer.parseInt(param[0]);
                String dayName = param[1];
                int hour =  Integer.parseInt(param[2]);
                int minites = Integer.parseInt(param[3]);
				Date date = new Date(ToolUtil.getLongTimeNew(dayChoosedIndex,hour,minites,AirportBookPublishFragement.removeDays));
				etUseTime.setText(ToolUtil.showTime(dayName, hour, minites));
				String strDate = f_use.format(date);
				submitTime = strDate;
			}
		};
		Window.selectDate(bookParent, callBack, timeLine, defaultMinUseCarTime, defaultMaxUseCarTime);
	}

	public static long getLongTime(Context context, Integer[] param) {
		long time = 0;
		Resources res = context.getResources();
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
		time = date.getTime();
		return time;
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

								String txt = bookParent.getString(R.string.book_distance,
										BookUtil.getDecimalNumber(Integer.parseInt(distance)), price);
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

	
	public static String[][] getTimeWheelValues() {
		String[][] times = new String[3][];
		Calendar now = Calendar.getInstance();

		int hour = now.get(Calendar.HOUR_OF_DAY);
		int min = now.get(Calendar.MINUTE);

		if (hour < 23) {
			times[0] = new String[] { "今天" };
			if (min < 5) {
				times[1] = new String[] { String.valueOf(hour) };
				times[2] = new String[] { "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55" };
			} else {
				times[1] = new String[] { String.valueOf(hour), String.valueOf(hour + 1) };
				times[2] = new String[] { "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55" };

			}
		} else {

		}
		return times;
	}

	public static String getHourString(int hour) {
		return getHourString(String.valueOf(hour));
	}

	public static String getHourString(String time) {
		String strTime = "";
		if (Integer.parseInt(time) < 10) {
			strTime = "0" + time;
		} else {
			strTime = time;
		}

		if (strTime.equals("24")) {
			return "00";
		} else {
			return strTime;
		}
	}
	
	/**
	 * 新的时间规则
	 * @param hourOffSet
	 * @param type
	 *            0天数，1小时数，2分钟数
	 * @param selectHourIndex
	 *            默认-1为当前小时 仅在type为2时有用，即根据小时获取分钟数
	 * @param selectDayIndex
	 *            默认0为今天，1为明天； 仅在type为1时有用，即根据Day获取小时数
	 * @return
	 * @return String[]
	 */
	public synchronized static String[] getTimeValues(int type, int selectHourIndex,int selectHour, int selectDayIndex,NewNetworkRequest.TimeLine timeLine,int defaultMinUseCarTime,int defaultMaxUseCarTime) {
		String[] values = null; 
		int minTime;
		int maxTime;
		//是否从服务器获取了时间上下线
		if(null != timeLine){
			minTime = Integer.parseInt(timeLine.lower);
			maxTime = Integer.parseInt(timeLine.upper);
		}else{
			minTime = defaultMinUseCarTime;
			maxTime = defaultMaxUseCarTime;
		}
		//测试数据
//		minTime = 80;
//		maxTime = 4*24*60;
//		maxTime = 3*60;
		Date nowDate = new Date(System.currentTimeMillis());
		//最小和最大用车时间
		Date minUseDate = new Date(System.currentTimeMillis() + minTime * 60 * 1000);
		Date maxUseDate = new Date(System.currentTimeMillis() + maxTime * 60 * 1000);
		 
		int daysCount = 1;
		int mins = maxTime - ((24-nowDate.getHours())*60-nowDate.getMinutes());//减去当天剩余分钟数，还剩的分钟数
		if(mins>0){
			int day = mins/(24*60);  
			if(mins > day*24*60){
				daysCount++;
			}
			daysCount = daysCount+day;
		}
		
		//最小时间的时间间隔天数
		removeDays = 0;
		int minitesMin = minTime - ((24-nowDate.getHours())*60-nowDate.getMinutes());//减去当天剩余分钟数，还剩的分钟数
		if(minitesMin > 0){//需要移除的天数
			removeDays++;
			removeDays  = removeDays + minitesMin / (24*60);
		}
		
		daysCount = daysCount - removeDays;
		
		switch (type) {
			case 0://获取天数
				values = new String[daysCount];
				for(int i = 0; i < daysCount; i++){
					//今天
					if(i+removeDays == 0){
						values[i] = "今天";
						continue;
					}
					//明天
					if(i+removeDays == 1){
						values[i] = "明天";
						continue;
					}
					//后天
					if(i+removeDays == 2){
						values[i] = "后天";
						continue;
					}
					//显示日期
					if(i+removeDays > 2){
						values[i] = String.valueOf(i+removeDays+"天后");
						continue;
					}
				}
				break;
			case 1://获取小时数
				int startHour=0;
				int endHour=23;
				
				//当前选中的是今天：设置开始小时
				if(0 == selectDayIndex+removeDays){
					//当前小时
					startHour = nowDate.getHours();
					//最小预定时间加当前时间大于一小时
					if(minTime + nowDate.getMinutes() >=60){
						if(startHour < 23){
							startHour++;
						}
					}
				}
				//当前选中的是最后一天：设置结束小时
				if(selectDayIndex+removeDays == daysCount+removeDays-1){
					endHour = maxUseDate.getHours();
				}
				
				values = new String[endHour -startHour+1];
				for(int i = startHour,j=0;i<=endHour;i++,j++){
					values[j] = getHourString(i);
				}
				break;
			case 2://获取分钟数
				int startMinites=0;
				int endMinites=55;
				//选中的是今天的第一个小时
				if(0 == selectDayIndex+removeDays && 0 == selectHourIndex){
					startMinites = (minUseDate.getMinutes()/5)*5+5;
					if(startMinites >= 60){
						startMinites = 55;
					}
				}
				//选中的是最后一天的最后一个小时
				int lastDayHour = maxUseDate.getHours();
				if(selectDayIndex+removeDays == daysCount+removeDays-1 && lastDayHour == selectHour){
					endMinites = (maxUseDate.getMinutes()/5)*5;
				}
				
				values = new String[(endMinites-startMinites)/5+1];
				for(int i = startMinites,j=0;i<=endMinites;i = i+5,j++){
					values[j] = getHourString(i);
				}
				break;
		     default :
				break;
		}
		
		return values;
	}
	
	/**
	 * 旧的本地时间规则
	 * @param hourOffSet
	 * @param type
	 *            0天数，1小时数，2分钟数
	 * @param selectHour
	 *            默认-1为当前小时 仅在type为2时有用，即根据小时获取分钟数
	 * @param selectDay
	 *            默认0为今天，1为明天； 仅在type为1时有用，即根据Day获取小时数
	 * @return
	 * @return String[]
	 */
	public synchronized static String[] getTimeValues(int type, int selectHour, int selectDay) {
		String values[] = null;

		Calendar now = Calendar.getInstance();
		int hour = now.get(Calendar.HOUR_OF_DAY);
		int min = now.get(Calendar.MINUTE);

		switch (type) {
		case 0://天
			values = new String[] { "今天","明天","后天" };
			break;
		case 1://小时
			if (selectDay == 0) {
				if (hour < 23) {
					int start = hour;
					int count = 24-hour;
					values = new String[count];
					for (int i = start,j=0; i < 24; i++,j++) {
						values[j] = getHourString(i); 
					}
				} else {
					if (min >= 55) {
						values = new String[] { "" };
					} else {
						values = new String[] { "23" };
					}
				}
			} else {
				values = new String[]{"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"};
			}
			break;
		case 2://分钟
			//当天的该小时
			if(selectDay == 0){
				if((selectDay == 0 && selectHour == hour) || selectHour == -1){
					int start = ((min / 5) + 1) * 5;
					int count = (55 - start) / 5 + 1;
					values = new String[count];
					for (int i = 0; i < count; i++) {
						values[i] = getTimeString(String.valueOf(i * 5 + start));
					}
				}
				else{
					values = new String[] { "00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55" };
				}
			}else{
				values = new String[] { "00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55" };
			}
			break;
		default:
			break;
		}

		return values;
	}
	
	private static String getTimeString(String time) {
		String strTime = "";
		if (Integer.parseInt(time) < 10) {
			strTime = "0" + time;
		} else {
			strTime = time;
		}

		if (strTime.equals("24")) {
			return "00";
		} else {
			return strTime;
		}
	}
	
	/**
	 * 设置用车时间
	 * @param minites 为多少分钟后用车
	 */
	public void setUseCarTime(int minites){
		Date date = new Date(System.currentTimeMillis() + minites * 60 * 1000);
		etUseTime.setText(ToolUtil.showTime(date));
		submitTime = f_use.format(date);
	}
	
	
}
