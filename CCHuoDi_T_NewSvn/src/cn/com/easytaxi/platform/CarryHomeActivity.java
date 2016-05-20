package cn.com.easytaxi.platform;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.BookConfig;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.NewNetworkRequest;
import cn.com.easytaxi.book.NewBookDetail;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.common.Const;
import cn.com.easytaxi.common.SocketUtil;
import cn.com.easytaxi.common.Window;
import cn.com.easytaxi.common.Window.WheelCallData;
import cn.com.easytaxi.onetaxi.SearchAddressActivity;
import cn.com.easytaxi.onetaxi.TitleBar;
import cn.com.easytaxi.util.DateTimePickDialog;

import com.easytaxi.etpassengersx.R;

public class CarryHomeActivity extends YdLocaionBaseActivity implements OnClickListener, OnCheckedChangeListener {

	private static final int REQEUST_CODE_STARTADDR = 1;
	private static final int REQEUST_CODE_ENDADDR = 2;

	private TitleBar bar;
	private String[] mCarBz = new String[] { "注：所选车型长3米，宽1.65米，高1.7米，载重1吨！", "注：所选车型长4.2米，宽1.8米，载重2吨！", "注：所选车型长3.3米，宽1.61米，高1.57米，载重1.7吨！", "注：所选车型长4.2米，宽1.8米，高1.85米，载重2吨！" };

	/*
	 * Exceptions found during parsing
	 * 
	 * References a layout (@layout/title_layout)
	 */

	// Content View Elements

	private DateTimePickDialog mDateTimePicker;

	private RelativeLayout mBj_layout_time;
	private TextView mBj_tv_time;
	private TextView mBj_tv_week;
	private TextView mBj_tv_date;
	private TextView mBj_tv_city;
	private TextView mBj_tv_startaddr;
	private EditText mBj_tv_endaddr;
	private RadioGroup mBj_radiogroup;
	private TextView mBj_tv_bz;
	private LinearLayout mBj_layout_detailinfo;
	private TextView mBj_tv_person;
	private TextView mBj_tv_car;
	private TextView mBj_tv_floor;
	private LinearLayout mBj_layout_submit;

	// End Of Content View Elements

	private long timestamp;

	// 乘客位置信息（通过监听平台广播获得的,第一次直接取平台的）
	int p_lat = 0;
	int p_lng = 0;

	// 乘客起始地点位置
	int u_lat = 0;
	int u_lng = 0;
	// 乘客结束地点位置
	int u_lat_end = 0;
	int u_lng_end = 0;

	/**
	 * 车辆类型
	 */
	private int carType = 3;

	/**
	 * 搬运工人数
	 */
	private int personNum = 0;

	/**
	 * 板车数量
	 */
	private int needCar = 0;

	/**
	 * 楼层数量
	 */
	private int stairs = 0;

	/**
	 * 用车时间
	 */
	private String useTime;

	private String cityName;
	private TextView fujia;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		if (VERSION.SDK_INT >= 14) {
			setTheme(android.R.style.Theme_Holo_Light_NoActionBar);
		}
		super.onCreate(arg0);
		setContentView(R.layout.act_carryhome);
		mDateTimePicker = new DateTimePickDialog(this, "");
		bar = new TitleBar(this);
		bar.setTitleName("搬家");
		bindViews();

		p_lat = ETApp.getInstance().getCacheInt("_P_LAT");
		p_lng = ETApp.getInstance().getCacheInt("_P_LNG");
		u_lat = p_lat;
		u_lng = p_lng;

		cityName = getCityName();

		timestamp = System.currentTimeMillis();
		mBj_tv_city.setText(getCityName());
		fujia.setText("附加费:" + fujiaFei + "元");
		NewNetworkRequest.getAddressByLocation(p_lat, p_lng, firstAddressCallback);

	}

	private Callback<String> firstAddressCallback = new Callback<String>() {
		@Override
		public void handle(String param) {
			if (param != null) {
				mBj_tv_startaddr.setText(String.valueOf(param));
			}
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bj_layout_time:
			mDateTimePicker.dateTimePicKDialog(new Callback<Calendar>() {

				@Override
				public void handle(Calendar param) {
					long time = param.getTimeInMillis();
					if (time < (System.currentTimeMillis() + 1000 * 60 * 30)) {
						Toast.makeText(CarryHomeActivity.this, "搬家时间必须提前30分钟", Toast.LENGTH_SHORT).show();
						return;
					}
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					useTime = sdf.format(param.getTime());
					// TODO Auto-generated method stub
					// mBj_tv_time.setTag(param);
					mBj_tv_time.setText(DateTimePickDialog.getTimeByCalendar(param));
					mBj_tv_week.setText(DateTimePickDialog.getWeekByCalendar(param));
					mBj_tv_date.setText(DateTimePickDialog.getDateByCalendar(param));
				}
			});
			break;
		case R.id.bj_layout_submit:
			if (!ETApp.getInstance().isLogin()) {
				Intent intent = new Intent(CarryHomeActivity.this, RegisterActivity.class);
				startActivity(intent);
				return;
			}

			// Calendar date = (Calendar) mBj_tv_time.getTag();
			// if (date == null) {
			// date = Calendar.getInstance();
			// }
			// if (date.getTimeInMillis() < (System.currentTimeMillis() + 1000 *
			// 60 * 40)) {
			// date.setTimeInMillis(System.currentTimeMillis() + 1000 * 60 *
			// 40);
			// }
			// SimpleDateFormat sdf = new
			// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// useTime = sdf.format(date.getTime());
			String startAddr = mBj_tv_startaddr.getText().toString();
			String endAddr = mBj_tv_endaddr.getText().toString();

			if (TextUtils.isEmpty(startAddr)) {
				Toast.makeText(CarryHomeActivity.this, "起始地点不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			if (TextUtils.isEmpty(endAddr)) {
				Toast.makeText(CarryHomeActivity.this, "下车地点不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			send(useTime, startAddr, endAddr, carType, personNum, needCar, stairs);
			break;
		case R.id.bj_tv_startaddr:
			mBj_tv_endaddr.setCursorVisible(false);
			switchAddress(REQEUST_CODE_STARTADDR, cityName);
			break;
		case R.id.bj_tv_endaddr:
			mBj_tv_endaddr.setCursorVisible(false);
			switchAddress(REQEUST_CODE_ENDADDR, cityName);
			break;
		case R.id.bj_layout_detailinfo:
			Window.selectCarInfos(this, new Callback<WheelCallData[]>() {

				@Override
				public void handle(WheelCallData[] param) {

					mBj_tv_person.setText(param[0].index == 0 ? "下楼方式 无" : param[0].name);
					mBj_tv_car.setText(param[1].index == 0 ? "特殊物品 无" : param[1].name);
					mBj_tv_floor.setText(param[2].index == 0 ? "上楼方式 无" : param[2].name);

					personNum = param[0].index;
					needCar = param[1].index;
					stairs = param[2].index;
					fujiaFei = cacluMoney(param);
					fujia.setText("附加费:" + fujiaFei + "元");
					mBj_tv_bz.setText("总费用:" + (fujiaFei + 300) + "元");
				}
			});
			break;
		default:
			break;
		}
	}

	private int fujiaFei = 0;
	private int[] teshus = new int[] { 0, 300, 400, 500, 100, 100, 0 };

	/**
	 * 计算价格
	 * 
	 * @return
	 */
	private int cacluMoney(WheelCallData[] params) {
		int xialou_num = 0;
		int shanglou_num = 0;
		if (params[0].index == 1)
			xialou_num = 50;
		else if (params[0].index == 0) {
			xialou_num = 0;
		} else {
			xialou_num = (params[0].index - 1) * 10;
		}
		if (params[2].index == 1)
			shanglou_num = 50;
		else if (params[2].index == 0) {
			shanglou_num = 0;
		} else {
			shanglou_num = (params[2].index - 1) * 10;
		}

		return (xialou_num + shanglou_num + teshus[params[1].index]);
	}

	private void bindViews() {
		mBj_layout_time = (RelativeLayout) findViewById(R.id.bj_layout_time);
		mBj_tv_time = (TextView) findViewById(R.id.bj_tv_time);
		mBj_tv_week = (TextView) findViewById(R.id.bj_tv_week);
		mBj_tv_date = (TextView) findViewById(R.id.bj_tv_date);
		mBj_tv_city = (TextView) findViewById(R.id.bj_tv_city);
		mBj_tv_startaddr = (TextView) findViewById(R.id.bj_tv_startaddr);
		mBj_tv_endaddr = (EditText) findViewById(R.id.bj_tv_endaddr);
		mBj_radiogroup = (RadioGroup) findViewById(R.id.bj_radiogroup);
		mBj_tv_bz = (TextView) findViewById(R.id.bj_tv_bz);
		mBj_layout_detailinfo = (LinearLayout) findViewById(R.id.bj_layout_detailinfo);
		mBj_tv_person = (TextView) findViewById(R.id.bj_tv_person);
		mBj_tv_car = (TextView) findViewById(R.id.bj_tv_car);
		mBj_tv_floor = (TextView) findViewById(R.id.bj_tv_floor);
		mBj_layout_submit = (LinearLayout) findViewById(R.id.bj_layout_submit);

		fujia = (TextView) findViewById(R.id.fujia_money);

		mBj_layout_time.setOnClickListener(this);
		mBj_layout_submit.setOnClickListener(this);
		mBj_layout_detailinfo.setOnClickListener(this);
		mBj_radiogroup.setOnCheckedChangeListener(this);
		mBj_tv_endaddr.setOnClickListener(this);
		mBj_tv_startaddr.setOnClickListener(this);

		// 初始化时间
		Calendar calendar = Calendar.getInstance();
		long time = calendar.getTimeInMillis() + 1000 * 60 * 31;
		calendar.setTimeInMillis(time);
		mBj_tv_time.setText(DateTimePickDialog.getTimeByCalendar(calendar));
		mBj_tv_week.setText(DateTimePickDialog.getWeekByCalendar(calendar));
		mBj_tv_date.setText(DateTimePickDialog.getDateByCalendar(calendar));

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		useTime = sdf.format(calendar.getTime());
	}

	private void send(String time, String strStartAddr, String strEndAddr, int carType, int personNum, int needCar, int stairs) {
		mBj_layout_submit.setEnabled(false);
		try {
			int versionCode = ETApp.getInstance().getMobileInfo().getVerisonCode();
			final JSONObject json = new JSONObject();
			json.put("action", "scheduleAction");
			// json.put("method", "submitCarry");
			json.put("method", "submitBook");
			// json.put("carType", carType);
			json.put("carType", carType);
			json.put("order_type", 1);
			json.put("carr_type", carType);

			/**
			 * 
			 personNum = param[0].index; needCar = param[1].index; stairs =
			 * param[2].index;
			 */
			json.put("stairsup", stairs + 1);
			json.put("specialGoods", needCar + 1);
			json.put("stairsdown", personNum + 1);
			json.put("fee", fujiaFei + 300);
			// json.put("personNum", personNum);
			// json.put("needCar", needCar);
			// json.put("stairs", stairs);
			json.put("timestamp", timestamp);
			json.put("bookType", Const.BOOK_BOOK_TYPE);
			json.put("cityId", getCityId());
			json.put("cityName", getCityName());

			json.put("passengerPhone", getPassengerId());
			json.put("passengerId", getPassengerId());
			// BUG --FIXED------------
			try {
				json.put("passengerName", ETApp.getInstance().getCurrentUser().getUserNickName());
			} catch (Exception e) {
				e.printStackTrace();
			}

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
			json.put("payment", 0);
			json.put("price", 0);

			AppLog.LogD("xyw", "submit json:" + json.toString());
			SocketUtil.getJSONObject(Long.valueOf(getPassengerId()), json, new Callback<JSONObject>() {

				@Override
				public void handle(JSONObject param) {

					try {
						if (param != null) {
							AppLog.LogD("xyw", param.toString());
							int error = param.getInt("error");
							switch (error) {
							// 成功
							case 0x0000:
								Toast.makeText(CarryHomeActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
								// 跳转到详情页面
								Intent intent1 = new Intent(CarryHomeActivity.this, NewBookDetail.class);
								intent1.putExtra("bookId", param.getLong("bookId"));
								CarryHomeActivity.this.startActivity(intent1);
								CarryHomeActivity.this.finish();
								break;
							}
						} else {
							AppLog.LogD("error--->提交订单返回结果为空");
							Toast.makeText(CarryHomeActivity.this, "您的网络不给力！", Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						//
						e.printStackTrace();
					}

				}

				@Override
				public void error(Throwable e) {
					super.error(e);
					Toast.makeText(CarryHomeActivity.this, "您的网络不给力！", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void complete() {
					super.complete();
					try {
						// dismissDialog(0);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					mBj_layout_submit.setEnabled(true);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	@Override
	protected void initViews() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initListeners() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initUserData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void regReceiver() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void unRegReceiver() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		String txt = "";
		switch (checkedId) {
		case R.id.zuche_cartyp1:// 搬家板车
			carType = 3;
			txt = mCarBz[0];
			break;
		case R.id.zuche_cartyp2:// 搬家箱车
			carType = 4;
			txt = mCarBz[1];
			break;
		case R.id.zuche_cartyp3:
			carType = 3;
			txt = mCarBz[2];
			break;
		case R.id.zuche_cartyp4:
			carType = 4;
			txt = mCarBz[3];
			break;
		default:
			break;
		}

		// mBj_tv_bz.setText(txt);
	}

	private void switchAddress(int requestCode, String cityName) {
		Intent intent = new Intent(this, SearchAddressActivity.class);
		intent.putExtra("cityName", cityName);
		startActivityForResult(intent, requestCode);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQEUST_CODE_STARTADDR && resultCode == Activity.RESULT_OK) {
			u_lat = data.getIntExtra("lat", 0);
			u_lng = data.getIntExtra("lng", 0);
			mBj_tv_startaddr.setText(data.getStringExtra("address"));
		}

		if (requestCode == REQEUST_CODE_ENDADDR && resultCode == Activity.RESULT_OK) {
			u_lat_end = data.getIntExtra("lat", 0);
			u_lng_end = data.getIntExtra("lng", 0);
			mBj_tv_endaddr.setText(data.getStringExtra("address"));
		}
	}
}
