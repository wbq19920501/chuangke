package cn.com.easytaxi.drivingAgent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import cn.com.easytaxi.NewNetworkRequest.TimeLines;
import cn.com.easytaxi.NewNetworkRequest.TipPrices;
import cn.com.easytaxi.airport.AirportBookPublishFragement;
import cn.com.easytaxi.book.BookUtil;
import cn.com.easytaxi.book.NewBookDetail;
import cn.com.easytaxi.common.ActionLogUtil;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.common.Const;
import cn.com.easytaxi.common.MapUtil;
import cn.com.easytaxi.common.SocketUtil;
import cn.com.easytaxi.common.ToolUtil;
import cn.com.easytaxi.common.Window;
import cn.com.easytaxi.onetaxi.SearchAddressActivity;
import cn.com.easytaxi.onetaxi.TitleBar;
import cn.com.easytaxi.platform.CitySelectActivity;
import cn.com.easytaxi.platform.RegisterActivity;
import cn.com.easytaxi.platform.common.Util;
import cn.com.easytaxi.platform.service.SystemService;
import cn.com.easytaxi.ui.BookBaseFragement;
import cn.com.easytaxi.util.TimeTool;
import cn.com.easytaxi.workpool.bean.GeoPointLable;

import com.baidu.mapapi.model.inner.GeoPoint;
import com.easytaxi.etpassengersx.R;
import com.easytaxi.etpassengersx.wxapi.WXPayEntryActivity;

public class DrivingOrderPublishFragement extends BookBaseFragement implements View.OnClickListener {

	private final int START_CITY_REQ_CODE = 400;
	private final int END_CITY_REQ_CODE = 401;

	private SimpleDateFormat f_use = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String cityId;
	private String cityName;
	private String mobile;
	/**
	 * �Ƿ�ǿ������֧���� 0������Ҫ����֧����1�����������֧��
	 */
	private int onlinePayment = 0;
	private String tempstr = "�߷����ʵ��Ӽۣ������ڸ���������";
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
	private TitleBar titleBar;

	private String submitTime;
	private View sendBook;
	private Button btnStartLoc;
	private Button btnEndLoc;
	private EditText etPhone;
	private EditText etUseTime;
	private TextView etStartAddr;
	private TextView etEndAddr;
	private TextView default_book_time;
	private TextView tvDistanPrice;

	private String endCityName;
	private String endCityId;
	private String startCityName;
	private String startCityId;
	private Button end_city;
	private Button start_city;

	private View cover_loading;

	protected int priceKey = 0;
	protected String priceValue = "0";
	private int[] priceList;
	private Callback<Object> priceCallback = new Callback<Object>() {

		@Override
		public void handle(Object param) {
			try{
				if (param != null) {
					TipPrices tipPrices = (NewNetworkRequest.TipPrices) param;
					if (tipPrices.error == 0) {// �м۸��б�
						priceList = tipPrices.priceList;
						tempstr = tipPrices.msg;
						onlinePayment = tipPrices.onlinePayment;
					} else {// û��ȡ���۸��б�
						priceList = new int[] { 0 };
						Toast.makeText(bookParent, tipPrices.errormsg, Toast.LENGTH_LONG).show();
					}
					priceKey = priceList[0];
					priceValue = priceList[0] + "";
					initDrivingAgentRule(tempstr);
				}
			}catch(Exception e){
//				Toast.makeText(bookParent, "û�л�ȡ���л����еļ۸��б�...", Toast.LENGTH_LONG).show();
			}
		}
	};
	/**
	 * ���ݹ�����ʾ����
	 */
	private EditText drivingAgentRule;
	/**
	 * Ĭ�ϵĶ�����Сʱ�䣺��λ����
	 */
	private int defaultMinUseCarTime = 30;
	/**
	 * Ĭ�ϵĶ������ʱ�䣺��λ����
	 */
	private int defaultMaxUseCarTime = 3 * 24 * 60;
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
				ArrayList<NewNetworkRequest.TimeLine> timeLineList = timeLines.datas;
				if (timeLineList.size() > 0) {
					timeLine = timeLineList.get(0);
					// ��ȡ��ʱ�������ߣ�����Ĭ�ϵ��ó�ʱ��
					setUseCarTime(Integer.parseInt(timeLine.lower));
					// ��ʾʱ��
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
		mobile = getPassengerId();
		cityName = bookParent.getCityName();
		cityId = bookParent.getCityId();
		if (cityName == null) {// Ĭ��Ϊ������
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
		View view = inflater.inflate(R.layout.drivring_order_publish, container, false);
		init(view);
		regReceiver();

		NewNetworkRequest.getAddressByLocation(p_lat, p_lng, firstAddressCallback);

		initPrice(Const.DRIVING_ORDER_PRICELIST);

		new Thread() {
			public void run() {
				NewNetworkRequest.getDiaoDuPriceList(Integer.parseInt(cityId), Const.DRIVINT_ORDER_BOOK_TYPE, BookConfig.ClientType.CLIENT_TYPE_PASSENGER, priceCallback);
			};
		}.start();

		// ��ȡʱ����������
		String version = "" + ETApp.getInstance().getMobileInfo().getVerisonCode();
		NewNetworkRequest.getTimeDeadLine((Integer.parseInt(cityId)), Const.DRIVINT_ORDER_BOOK_TYPE, BookConfig.ClientType.CLIENT_TYPE_PASSENGER, version, timeLinesCallBack);

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
				tempstr = js.getJSONObject("datas").getString("msg");
				initDrivingAgentRule(tempstr);
				onlinePayment = tips.onlinePayment;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * ��ʼ��������ʾ�Ӽ۵�ScrollingTextView����
	 * 
	 * @param price
	 */
	private void initDrivingAgentRule(String msg) {
		msg = msg.replaceAll("\\\\n", "\\\n");
		drivingAgentRule.setText(msg);
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
			// ˢ�¼۸��б�
			NewNetworkRequest.getDiaoDuPriceList(Integer.parseInt(startCityId), Const.DRIVINT_ORDER_BOOK_TYPE, BookConfig.ClientType.CLIENT_TYPE_PASSENGER, priceCallback);
		}

		setDistance();

	}

	protected boolean isChooseStartCity;

	private void init(View view) {
		default_book_time = (TextView) view.findViewById(R.id.default_book_time);
		drivingAgentRule = (EditText) view.findViewById(R.id.book_scrollingTextView);

		end_city = (Button) view.findViewById(R.id.end_city);
		start_city = (Button) view.findViewById(R.id.start_city);
		cover_loading = view.findViewById(R.id.cover_loading);

		end_city.setText(cityName);
		start_city.setText(cityName);
		startCityName = cityName;
		startCityId = cityId;
		endCityName = cityName;
		endCityId = cityId;

		sendBook = view.findViewById(R.id.book_send);
		tvDistanPrice = (TextView) view.findViewById(R.id.book_publish_distance);
		btnStartLoc = (Button) view.findViewById(R.id.btn_book_startloc);
		btnEndLoc = (Button) view.findViewById(R.id.btn_book_endloc);
		etPhone = (EditText) view.findViewById(R.id.et_book_phone);

		etUseTime = (EditText) view.findViewById(R.id.et_book_usetime);
		etStartAddr = (TextView) view.findViewById(R.id.et_book_startaddr);
		etEndAddr = (TextView) view.findViewById(R.id.et_book_endaddr);
		etPhone.setText(mobile);

		// ��ʼ��Ĭ��Ϊ30���Ӻ��ó�
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
		
		//��ʼ��������ʾ����
		((TextView) view.findViewById(R.id.text2)).setText("��Ĵ���");
		((TextView) view.findViewById(R.id.text3)).setText("ԤԼ��ش��ݼǵ��л�����Ŷ");
	}

	private void switchAddress(int type, String cityName) {
		Intent intent = new Intent(bookParent, SearchAddressActivity.class);
		intent.putExtra("cityName", cityName);
		AppLog.LogD(cityName);
		startActivityForResult(intent, type);
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
		if (!StringUtils.isEmpty(getPassengerId())) {

			final String phone = etPhone.getText().toString().trim();
			// final String time = etUseTime.getText().toString().trim();
			final String strStartAddr = etStartAddr.getText().toString().trim();
			final String strEndAddr = etEndAddr.getText().toString().trim();

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
				// Calendar limit = Calendar.getInstance();
				// limit.set(Calendar.YEAR, now.get(Calendar.YEAR));
				// limit.set(Calendar.MONTH, now.get(Calendar.MONTH));
				// limit.set(Calendar.DAY_OF_MONTH,
				// now.get(Calendar.DAY_OF_MONTH) + 2);
				// limit.set(Calendar.HOUR_OF_DAY, 0);
				// limit.set(Calendar.MINUTE, 0);
				// limit.set(Calendar.SECOND, 0);
				AppLog.LogD("date:>>>+now:" + f_use.format(now.getTime()));
				AppLog.LogD("date:>>>+use:" + submitTime);
				// AppLog.LogD(">>>+limit:" + f_use.format(limit.getTime()));
				try {
					if (use.getTimeInMillis() <= now.getTimeInMillis()) {
						selectDateByWheel();
						Toast.makeText(bookParent, "�ó�ʱ�������ڵ�ǰʱ��", Toast.LENGTH_SHORT).show();
						return;
					}
					// if (use.getTimeInMillis() >= limit.getTimeInMillis()) {
					// selectDateByWheel();
					// Toast.makeText(bookParent, "�ó�ʱ�����Ϊ���������",
					// Toast.LENGTH_SHORT).show();
					// return;
					// }
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

		sendBook.setEnabled(false);
		try {
			int versionCode = ETApp.getInstance().getMobileInfo().getVerisonCode();
			final JSONObject json = new JSONObject();
			json.put("action", "scheduleAction");
			json.put("method", "submitBook");
			json.put("bookType", Const.DRIVINT_ORDER_BOOK_TYPE);
			json.put("cityId", startCityId);
			json.put("cityName", startCityName);

			json.put("passengerPhone", phone);
			json.put("passengerId", phone);
			// BUG --FIXED------------
//			json.put("passengerId", ETApp.getInstance().getCurrentUser().getPhoneNumber(User._MOBILE));

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
//			json.put("payment", priceValue);
//			json.put("price", priceValue);
			json.put("payment", 0);//���ݲ��Ӽ�
			json.put("price", 0);

			// ����֧��
			json.put("onlinePayment", false);
			

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

			// д����־
			ActionLogUtil.writeActionLog(bookParent, R.array.driving_agent_phone, phone);// �ֻ���
			ActionLogUtil.writeActionLog(bookParent, R.array.driving_agent_use_time, time);// ����ʱ��
			ActionLogUtil.writeActionLog(bookParent, R.array.driving_agent_start_city, startCityName);// ��ʼ����
			ActionLogUtil.writeActionLog(bookParent, R.array.driving_agent_start_address, strStartAddr);// �ϳ��ص�
			ActionLogUtil.writeActionLog(bookParent, R.array.driving_agent_end_city, endCityName);// ��������
			ActionLogUtil.writeActionLog(bookParent, R.array.driving_agent_end_address, strEndAddr);// �³��ص�
			ActionLogUtil.writeActionLog(bookParent, R.array.driving_agent_submit_btn, "");// �ύ��ť

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
				Date date = new Date(ToolUtil.getLongTimeNew(dayChoosedIndex, hour, minites, AirportBookPublishFragement.removeDays));
				etUseTime.setText(ToolUtil.showTime(dayName, hour, minites));
				String strDate = f_use.format(date);
				submitTime = strDate;
			}
		};
		Window.selectDate(bookParent, callBack, timeLine, defaultMinUseCarTime, defaultMaxUseCarTime);
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
	 * �����ó�ʱ��
	 * 
	 * @param minites
	 *            Ϊ���ٷ��Ӻ��ó�
	 */
	public void setUseCarTime(int minites) {
		Date date = new Date(System.currentTimeMillis() + minites * 60 * 1000);
		etUseTime.setText(ToolUtil.showTime(date));
		submitTime = f_use.format(date);
	}

	/**
	 * ת��������Ϊ��,ʱ,��
	 * 
	 * @param minites
	 */
	public String getTimeStr(int minites) {
		HashMap<String, String> map = TimeTool.formatDuring(minites * 60 * 1000);
		StringBuffer str = new StringBuffer();
		if (Integer.parseInt(map.get("days")) > 0) {
			str.append(map.get("days") + "��");
		}

		if (Integer.parseInt(map.get("hours")) > 0) {
			str.append(map.get("hours") + "Сʱ");
		}

		if (Integer.parseInt(map.get("minutes")) > 0) {
			str.append(map.get("minutes") + "����");
		}
		return str.toString();
	}

}
