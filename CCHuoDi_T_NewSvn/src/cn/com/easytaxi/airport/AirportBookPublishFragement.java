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
	 * �Ƿ�ǿ������֧���� 0������Ҫ����֧����1��ʾ��Ҫǿ������֧��
	 */
    public int onlinePayment = 0;
    private String tempstr = "�߷����ʵ��Ӽۣ������ڸ���������";
    
	public static int removeDays;
	// InputMethodManager imm;

	// λ�ù㲥����
	private LocationBroadcastReceiver locationReceiver;
	private RegisterReceiver registerReceiver;

	// �˿�λ����Ϣ��ͨ������ƽ̨�㲥��õ�,��һ��ֱ��ȡƽ̨�ģ�
	int p_lat = 0;
	int p_lng = 0;

	// �˿���ʼ�ص�λ��
	int u_lat = 0;
	int u_lng = 0;

	// �˿ͽ����ص�λ��
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
	 * ������ʾ���֣���ʾ�Ӽ��������ʾ����
	 */
	private ScrollingTextView scrollingTextView;
	/**
	 * �����
	 */
	private EditText airplan_number_edit;
    private String airplan_number;
    
	private View cover_loading;
	
	/**
	 * �洢��ʾ������textView��id
	 */
	private int airportTextViewId = R.id.et_airport_book_endaddr;//Ĭ���³��ص�viewΪ����

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
					if(tipPrices.error == 0){//�м۸��б�
						priceList =  tipPrices.priceList;
						tempstr = tipPrices.msg;
						onlinePayment = tipPrices.onlinePayment;
						
						// ����0ʱ����ʾ��������֧��
						if (onlinePayment > 0) {
							payMode.setChecked(true);
							payMode.setEnabled(false);
						} else {
							payMode.setChecked(false);
							payMode.setEnabled(true);
						}
					}else{//û��ȡ���۸��б�
						priceList = new int[]{0};
						Toast.makeText(bookParent,tipPrices.errormsg, Toast.LENGTH_LONG).show();
					}
					priceKey =  priceList[0];
					priceValue =  priceList[0]+"";
					initPriveViewcell(priceList);
					initScrollingTextView(tempstr);
				}
			}catch(Exception e){
				Toast.makeText(bookParent, "û�л�ȡ���л����еļ۸��б�...", Toast.LENGTH_LONG).show();
			}
		}
	};
	
	/**
	 * Ĭ�ϵĶ�����Сʱ�䣺��λ����
	 */
	private int defaultMinUseCarTime = 30;
	/**
	 * Ĭ�ϵĶ������ʱ�䣺��λ����
	 */
	private int defaultMaxUseCarTime = 3*24*60;
	
	/**
	 * ����ʱ��������
	 */
	private NewNetworkRequest.TimeLine timeLine;
	/**
	 * ʱ�������߻�ȡ�ص�
	 */
	private Callback<Object> timeLinesCallBack = new Callback<Object>() {

		@Override
		public void handle(Object param) {
			if (param != null) {
				TimeLines timeLines = (NewNetworkRequest.TimeLines) param;
				ArrayList<NewNetworkRequest.TimeLine> timeLineList =  timeLines.datas;
				if(timeLineList.size()>0){
					timeLine = timeLineList.get(0);
					//��ȡ��ʱ�������ߣ�����Ĭ�ϵ��ó�ʱ��
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
		if(cityName == null){//Ĭ��Ϊ������
			cityName = "����";
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
		regReceiver();//���û�ȥע��ʱ��Ľ�����
		NewNetworkRequest.getAddressByLocation(p_lat, p_lng, firstAddressCallback);
		
		initPrice(Const.AIRPORT_PRICELIST);
		
		new Thread(){
			public void run() {
				
				NewNetworkRequest.getDiaoDuPriceList(Integer.parseInt(cityId), Const.AIRPORT_BOOK_TYPE, BookConfig.ClientType.CLIENT_TYPE_PASSENGER, priceCallback);
				
			};
		}.start();
		
		//��ȡʱ���������� 
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
	 * ��ʼ��������ʾ�Ӽ۵�ScrollingTextView����
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
			if(airportTextViewId == R.id.et_airport_book_startaddr){//��ʼλ��Ϊ�ɻ�
				u_lng_end = data.getIntExtra("lng",0);
				u_lat_end = data.getIntExtra("lat",0);
				etEndAddr.setText(data.getStringExtra("address"));
			}else if(airportTextViewId == R.id.et_airport_book_endaddr){//����λ��Ϊ�ɻ�
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
			//ˢ�¼۸��б�Ϊ��ǰ��ѡ�������еļ۸�
			NewNetworkRequest.getDiaoDuPriceList(Integer.parseInt(startCityId), Const.AIRPORT_BOOK_TYPE, BookConfig.ClientType.CLIENT_TYPE_PASSENGER, priceCallback);

		}
		
		// �˿���ʼ�ص�λ��
//		int u_lat = 0;
//		int u_lng = 0;
//
//		// �˿ͽ����ص�λ��
//		int u_lat_end = 0;
//		int u_lng_end = 0;
		//��ȡ��������
		if(requestCode == AIRPORT_REQ_CODE && resultCode == Activity.RESULT_OK){
			if(airportTextViewId == R.id.et_airport_book_startaddr){//��ʼλ��Ϊ�ɻ�
//				data.getStringExtra("id");
//				data.getStringExtra("name");
				u_lng = data.getIntExtra("longitude",0);
				u_lat = data.getIntExtra("latitude",0);
				etStartAddr.setText(data.getStringExtra("name"));
			}else if(airportTextViewId == R.id.et_airport_book_endaddr){//����λ��Ϊ�ɻ�
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
        
		//��ʼ��Ĭ��Ϊ30���Ӻ��ó�
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
		
		//�����ӻ��ͻ�ģʽ
		switchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//������ֵ
//				Intent intent = new Intent(bookParent,PayMent.class);
//				startActivity(intent);
		
				if(airportTextViewId == R.id.et_airport_book_endaddr){
					//����ͼ����ʾ
					startIcon.setImageResource(R.drawable.airport_start);
					endIcon.setImageResource(R.drawable.pic155_end);
					//�޸���ʾ��
					etStartAddr.setHint("�ӻ���ַ");
					etEndAddr.setHint("�³���ַ");
					//����Ĭ�ϵ��ĸ�viewΪ����ѡ��
					airportTextViewId = R.id.et_airport_book_startaddr;
				}else if(airportTextViewId == R.id.et_airport_book_startaddr){
					//����ͼ����ʾ
					startIcon.setImageResource(R.drawable.pic155);
					endIcon.setImageResource(R.drawable.airport);
					//�޸���ʾ��
					etStartAddr.setHint("�ϳ���ַ");
					etEndAddr.setHint("�ͻ���ַ");
					//����Ĭ�ϵ��ĸ�viewΪ����ѡ��
					airportTextViewId = R.id.et_airport_book_endaddr;
				}
				//����������ʾ
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
				//��������id
				str = startCityId;
				startCityId = endCityId;
				endCityId = str;
				
				//������ʼ����
				int lat = u_lat;
			    int lng = u_lng;
			    u_lat = u_lat_end;
			    u_lng = u_lng_end;
			    u_lat_end = lat;
			    u_lng_end = lng;
				//������ʼ��ַ
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
	 * �µĽ����۸��б�ķ���
	 * @param priceList �۸��б�
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
		case R.id.et_airport_book_endaddr://�³��ص�
			etPhone.setCursorVisible(false);
			if(R.id.et_airport_book_endaddr == airportTextViewId){//��������б�
				Intent intent = new Intent(bookParent,AirportSelectActivity.class);
				intent.putExtra("cityName", endCityName);
				intent.putExtra("choosedAirportName",etEndAddr.getText().toString());
				startActivityForResult(intent,AIRPORT_REQ_CODE);
			}else if(R.id.et_airport_book_startaddr == airportTextViewId){//����ص��ѯ
				switchAddress(1, endCityName);
			}
			
			break;
		case R.id.et_airport_book_startaddr://�ϳ��ص�
			etPhone.setCursorVisible(false);
			if(R.id.et_airport_book_startaddr == airportTextViewId){//��������б�
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
		case R.id.book_send://�ύ
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
					Toast.makeText(bookParent, "�绰���벻��ȷ", Toast.LENGTH_SHORT).show();
					etPhone.requestFocus();
					return;
				}
				
				Calendar now = Calendar.getInstance();
				Calendar use = Calendar.getInstance();

				if (submitTime == null) {// �����ó�
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
						Toast.makeText(bookParent, "�ó�ʱ�������ڵ�ǰʱ��", Toast.LENGTH_SHORT).show();
						return;
					}
//					if (use.getTimeInMillis() >= limit.getTimeInMillis()) {
//						selectDateByWheel();
//						Toast.makeText(bookParent, "�ó�ʱ�����Ϊ����,��������", Toast.LENGTH_SHORT).show();
//						return;
//					}
				} catch (Exception e) {
					return;
				}
				if (strStartAddr.equals("")) {
					Toast.makeText(bookParent, "�������ϳ��ص�", Toast.LENGTH_SHORT).show();
					etStartAddr.requestFocus();
					return;
				}
				if (strEndAddr.equals("")) {
					Toast.makeText(bookParent, "�������³��ص�", Toast.LENGTH_SHORT).show();
					etEndAddr.requestFocus();
					return;
				}

				// if (startAddr.getTag().equals(endAddr.getTag())) {
				// Toast.makeText(bookParent, "��㲻�ܺ��յ�һ��",
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
			Toast.makeText(bookParent, "���ϸ�����Ϊ0Ԫ", Toast.LENGTH_SHORT).show();
			return;
		}
		sendBook.setEnabled(false);
		try {
			int versionCode = ETApp.getInstance().getMobileInfo().getVerisonCode();
			final JSONObject json = new JSONObject();
			json.put("action", "scheduleAction");
			json.put("method", "submitBook");
			json.put("bookType", Const.AIRPORT_BOOK_TYPE);

			//�����
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
			
			if (u_lng == 0 && u_lat == 0) {//Ĭ��ʹ���û���ǰ��ַ
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
			
			
			// ����֧��
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
							// �ɹ�
							case 0x0000:
								Toast.makeText(bookParent, "�ύ�ɹ�", Toast.LENGTH_SHORT).show();
								Intent intent = new Intent("cn.com.easytaxi.book.refresh_list");
								intent.putExtra("fromSubmited", true);
								bookParent.sendBroadcast(intent);
								// bookParent.setCurrentPage(1);
								etEndAddr.setText("");

								// ��ת������ҳ��
								Intent intent1 = new Intent(bookParent, NewBookDetail.class);
								intent1.putExtra("bookId", param.getLong("bookId"));
								bookParent.startActivity(intent1);
								break;
							// ����,��ת����ֵҳ��
							case 0x0003:
								Toast.makeText(bookParent, param.getString("errormsg"), Toast.LENGTH_SHORT).show();
								Intent intent2 = new Intent(bookParent, WXPayEntryActivity.class);
								intent2.putExtra("payMoney", selectPrice);
								bookParent.startActivity(intent2);
								break;
							// ��Ҫ����֧����ֱ����ʾerrormsg
							case 0x0004:
							default:
								Toast.makeText(bookParent, param.getString("errormsg"), Toast.LENGTH_SHORT).show();
								break;
							}
						} else {
							AppLog.LogD("error--->�ύ�������ؽ��Ϊ��");
							Toast.makeText(bookParent, "�������粻������", Toast.LENGTH_SHORT).show();
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
					Toast.makeText(bookParent, "�������粻������", Toast.LENGTH_SHORT).show();
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
			
			//д����־
			ActionLogUtil.writeActionLog(bookParent,R.array.airport_phone_numb,phone);//�ֻ���
			ActionLogUtil.writeActionLog(bookParent,R.array.airport_use_time, time);//�ó�ʱ��
			ActionLogUtil.writeActionLog(bookParent,R.array.airport_start_city, startCityName);//��ʼ����
			ActionLogUtil.writeActionLog(bookParent,R.array.airport_start_addr, strStartAddr);//�ϳ��ص�
			ActionLogUtil.writeActionLog(bookParent,R.array.airport_end_city, endCityName);//��������
			ActionLogUtil.writeActionLog(bookParent,R.array.airport_end_addr,strEndAddr);//�³��ص�
			ActionLogUtil.writeActionLog(bookParent,R.array.airport_add_price,priceValue);//�Ӽ�
			ActionLogUtil.writeActionLog(bookParent,R.array.airport_submit,"");//�ύ��ť
			
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
		// ����day
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
				Toast.makeText(bookParent, "δѡ��ص�", Toast.LENGTH_SHORT).show();
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

	// ��ʼ���㲥����
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
			times[0] = new String[] { "����" };
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
	 * �µ�ʱ�����
	 * @param hourOffSet
	 * @param type
	 *            0������1Сʱ����2������
	 * @param selectHourIndex
	 *            Ĭ��-1Ϊ��ǰСʱ ����typeΪ2ʱ���ã�������Сʱ��ȡ������
	 * @param selectDayIndex
	 *            Ĭ��0Ϊ���죬1Ϊ���죻 ����typeΪ1ʱ���ã�������Day��ȡСʱ��
	 * @return
	 * @return String[]
	 */
	public synchronized static String[] getTimeValues(int type, int selectHourIndex,int selectHour, int selectDayIndex,NewNetworkRequest.TimeLine timeLine,int defaultMinUseCarTime,int defaultMaxUseCarTime) {
		String[] values = null; 
		int minTime;
		int maxTime;
		//�Ƿ�ӷ�������ȡ��ʱ��������
		if(null != timeLine){
			minTime = Integer.parseInt(timeLine.lower);
			maxTime = Integer.parseInt(timeLine.upper);
		}else{
			minTime = defaultMinUseCarTime;
			maxTime = defaultMaxUseCarTime;
		}
		//��������
//		minTime = 80;
//		maxTime = 4*24*60;
//		maxTime = 3*60;
		Date nowDate = new Date(System.currentTimeMillis());
		//��С������ó�ʱ��
		Date minUseDate = new Date(System.currentTimeMillis() + minTime * 60 * 1000);
		Date maxUseDate = new Date(System.currentTimeMillis() + maxTime * 60 * 1000);
		 
		int daysCount = 1;
		int mins = maxTime - ((24-nowDate.getHours())*60-nowDate.getMinutes());//��ȥ����ʣ�����������ʣ�ķ�����
		if(mins>0){
			int day = mins/(24*60);  
			if(mins > day*24*60){
				daysCount++;
			}
			daysCount = daysCount+day;
		}
		
		//��Сʱ���ʱ��������
		removeDays = 0;
		int minitesMin = minTime - ((24-nowDate.getHours())*60-nowDate.getMinutes());//��ȥ����ʣ�����������ʣ�ķ�����
		if(minitesMin > 0){//��Ҫ�Ƴ�������
			removeDays++;
			removeDays  = removeDays + minitesMin / (24*60);
		}
		
		daysCount = daysCount - removeDays;
		
		switch (type) {
			case 0://��ȡ����
				values = new String[daysCount];
				for(int i = 0; i < daysCount; i++){
					//����
					if(i+removeDays == 0){
						values[i] = "����";
						continue;
					}
					//����
					if(i+removeDays == 1){
						values[i] = "����";
						continue;
					}
					//����
					if(i+removeDays == 2){
						values[i] = "����";
						continue;
					}
					//��ʾ����
					if(i+removeDays > 2){
						values[i] = String.valueOf(i+removeDays+"���");
						continue;
					}
				}
				break;
			case 1://��ȡСʱ��
				int startHour=0;
				int endHour=23;
				
				//��ǰѡ�е��ǽ��죺���ÿ�ʼСʱ
				if(0 == selectDayIndex+removeDays){
					//��ǰСʱ
					startHour = nowDate.getHours();
					//��СԤ��ʱ��ӵ�ǰʱ�����һСʱ
					if(minTime + nowDate.getMinutes() >=60){
						if(startHour < 23){
							startHour++;
						}
					}
				}
				//��ǰѡ�е������һ�죺���ý���Сʱ
				if(selectDayIndex+removeDays == daysCount+removeDays-1){
					endHour = maxUseDate.getHours();
				}
				
				values = new String[endHour -startHour+1];
				for(int i = startHour,j=0;i<=endHour;i++,j++){
					values[j] = getHourString(i);
				}
				break;
			case 2://��ȡ������
				int startMinites=0;
				int endMinites=55;
				//ѡ�е��ǽ���ĵ�һ��Сʱ
				if(0 == selectDayIndex+removeDays && 0 == selectHourIndex){
					startMinites = (minUseDate.getMinutes()/5)*5+5;
					if(startMinites >= 60){
						startMinites = 55;
					}
				}
				//ѡ�е������һ������һ��Сʱ
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
	 * �ɵı���ʱ�����
	 * @param hourOffSet
	 * @param type
	 *            0������1Сʱ����2������
	 * @param selectHour
	 *            Ĭ��-1Ϊ��ǰСʱ ����typeΪ2ʱ���ã�������Сʱ��ȡ������
	 * @param selectDay
	 *            Ĭ��0Ϊ���죬1Ϊ���죻 ����typeΪ1ʱ���ã�������Day��ȡСʱ��
	 * @return
	 * @return String[]
	 */
	public synchronized static String[] getTimeValues(int type, int selectHour, int selectDay) {
		String values[] = null;

		Calendar now = Calendar.getInstance();
		int hour = now.get(Calendar.HOUR_OF_DAY);
		int min = now.get(Calendar.MINUTE);

		switch (type) {
		case 0://��
			values = new String[] { "����","����","����" };
			break;
		case 1://Сʱ
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
		case 2://����
			//����ĸ�Сʱ
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
	 * �����ó�ʱ��
	 * @param minites Ϊ���ٷ��Ӻ��ó�
	 */
	public void setUseCarTime(int minites){
		Date date = new Date(System.currentTimeMillis() + minites * 60 * 1000);
		etUseTime.setText(ToolUtil.showTime(date));
		submitTime = f_use.format(date);
	}
	
	
}
