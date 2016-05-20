package cn.com.easytaxi.platform;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.NewNetworkRequest;
import cn.com.easytaxi.common.ActionLogUtil;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.common.Const;
import cn.com.easytaxi.common.SntpClient;
import cn.com.easytaxi.message.BadgeView;
import cn.com.easytaxi.mine.MyMainActivity;
import cn.com.easytaxi.platform.service.OneBookService;
import cn.com.easytaxi.platform.service.SystemService;
import cn.com.easytaxi.platform.view.MarqueeText;
import cn.com.easytaxi.receiver.LocationBroadcastReceiver;
import cn.com.easytaxi.ui.BookFragementActivity;
import cn.com.easytaxi.ui.MoreActivity;
import cn.com.easytaxi.ui.adapter.LoadCallback;
import cn.com.easytaxi.ui.bean.WeatherCityBean;
import cn.com.easytaxi.ui.view.CommonDialog;
import cn.com.easytaxi.util.AsyncUtil;
import cn.com.easytaxi.util.InfoConfig;
import cn.com.easytaxi.util.InfoTool;

import com.easytaxi.etpassengersx.R;

public class MainActivityNew extends YdLocaionBaseActivity {

	public static final String tag = "main page_MainActivityNew";

	public static final int CLOSE_DLG = 250;
	/**
	 * 搬家
	 */
	private View airport_icon_can;

	/**
	 * 租车
	 */
	private View drivint_agent;
	/**
	 * 一键召车
	 */
	private View onekey_icon_can;

	/**
	 * 预约订车
	 */
	private View book_icon_can;

	// private ViewSwitcher viewSwitcher_online;

	private MarqueeText weatherTextView;

	private MainActivityNew self = this;

	private Callback<JSONObject> onlineCallback = null;

	private Button cityTextView;

	public static String currentCityName = "";
	public static String cityId = "";

	protected String currentAddress;

	private TextView mTvMore;
	private BadgeView badgeView;

	private boolean isRefreshedWeather = false;
	private Handler mHandler;

	public Context context;
	public LocationBroadcastReceiver locationReceiver;
	Callback<String> newVersion = new Callback<String>() {

		@Override
		public void handle(String param) {
			// TODO Auto-generated method stub
			if (param.equals("current")) {
				runOnUiThread(new Runnable() {
					public void run() {
						// Toast.makeText(MainActivityNew.this, "当前版本已经最新",
						// 300).show();
					}
				});
			}
		}
	};

	/**
	 * 获取天气的回调
	 */
	private Callback<String> weatherCallBack = new Callback<String>() {
		@Override
		public void handle(String param) {
			try {
				AppLog.LogE("weatherCallBack--->" + param);
				JSONObject json = new JSONObject(param).getJSONObject("weatherinfo");

				String cityName = json.getString("city");
				String temp = json.getString("temp1");
				String weather = json.getString("weather1");
				String wind = json.getString("wind1");

				StringBuilder strBuilder = new StringBuilder();
				strBuilder.append(cityName + "  ").append(temp + "  ").append(weather + "  ").append(wind + "  ");
				weatherTextView.setText(strBuilder);
				weatherTextView.setScrollType(MarqueeText.SCROLL_TYPE_AUTO);
				weatherTextView.startScroll();
				setRefreshedWeather(true);

			} catch (Exception e) {
				e.printStackTrace();
				weatherTextView.setText(String.format("创客货的，说到就到"));
				setRefreshedWeather(false);

				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						requestWeather();
					}
				}, 10 * 1000);
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.p_main_new);
		registLocationReciever();
		mHandler = new Handler();
		initViews();
		initListeners();
		initUserData();

		context = getBaseContext();
		if (!checkDataBase(context)) {
			copyDataBase(context);
		}
		getNetTime();

//		checkUpdate();
	}

	/**
	 * 检查版本更新
	 * 
	 * @return void
	 */
	private void checkUpdate() {
		try {
			PackageManager pm = MainActivityNew.this.getPackageManager();
			final PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);

			TextView versionView = (TextView) findViewById(R.id.setting_version);
			versionView.setText("当前版本：" + pi.versionName);

			findViewById(R.id.setting_update).setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					String cityId = getCityId();
					if (TextUtils.isEmpty(cityId)) {
						cityId = "1";
					}
					String passengerId = getPassengerId();
					NewNetworkRequest.checkUpdate(MainActivityNew.this, cityId, pi.versionCode, false, passengerId, newVersion);
				}
			});

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	Callback<String> cityCallback = new Callback<String>() {

		@Override
		public void handle(String param) {
			if (param != null) {
				// 移除到市字
				try {
					int index = param.indexOf("市");
					if (index > 0) {
						currentCityName = param.substring(0, index);
					} else {
						currentCityName = param;
					}
				} catch (Exception e) {
					currentCityName = param;
				}
				setCityView(currentCityName);
				saveCacheCity(currentCityName);
			}
		}
	};

	private void registLocationReciever() {
		locationReceiver = new LocationBroadcastReceiver();
		registerReceiver(locationReceiver, new IntentFilter(SystemService.BROADCAST_LOCATION));

	}

	@Override
	protected void initUserData() {

		currentCityName = findCacheCity();
		if (!StringUtils.isEmpty(currentCityName)) {
			setCityView(currentCityName);
		} else {
			setCityView(getString(R.string.city_default_cityname));
		}
		cityId = getCityId();
		if (TextUtils.isEmpty(cityId)) {
			cityId = "1";
		}
		String passengerId = getPassengerId();
		NewNetworkRequest.checkUpdate(self, cityId, ETApp.getInstance().getMobileInfo().getVerisonCode(), false, passengerId, newVersion);

		// requestOnLine();

		requestCity(new CityAddressCallback());
		requestCurrentLoacionAddress(new FirstAddressCallback());
		requestWeather();

	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {

		switch (id) {
		case CLOSE_DLG:

			return createCloseDlg();

			// break;

		default:
			break;
		}

		return super.onCreateDialog(id, args);
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	protected void initListeners() {

		weatherTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// requestOnLine();
				requestWeather();
			}
		});

		// 分享
		findViewById(R.id.main_recommend).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				Intent intent = new Intent(self, RemdActivity.class);
				startActivity(intent);

			}
		});

		// 我的账户
		findViewById(R.id.main_info1).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (!isLogin()) {
					showRegistDialog(getBaseContext(), "提示", "请先注册以后再使用此功能", "立刻注册", "稍后再说", "cn.com.easytaxi.platform.RegisterActivity");
					// 写入日志
					ActionLogUtil.writeActionLog(MainActivityNew.this, R.array.main_my_account, "");
				} else {
					Intent intent = new Intent(self, MyMainActivity.class);
					startActivity(intent);

				}
			}
		});

		findViewById(R.id.main_setting1).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

			}
		});

		// 更多
		findViewById(R.id.main_about).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (badgeView.isShown()) {
					badgeView.hide();
				}
				startActivity(new Intent(self, MoreActivity.class));

			}
		});

		// 城市定位
		cityTextView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				requestCity(cityCallback);
			}
		});

		// 即时打车
		onekey_icon_can.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivityNew.this, cn.com.easytaxi.onetaxi.MainActivityNew.class);
				intent.putExtra("startAddress", currentAddress);
				startActivity(intent);
			}
		});

		// 预约打车
		book_icon_can.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivityNew.this, BookFragementActivity.class);
				startActivity(intent);
			}
		});

		// 租车
		drivint_agent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(MainActivityNew.this, "该功能即将上线", Toast.LENGTH_LONG).show();
			}
		});

		// 搬家
		airport_icon_can.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(MainActivityNew.this, CarryHomeActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void initViews() {

		airport_icon_can = findViewById(R.id.airport_icon_can);
		drivint_agent = findViewById(R.id.tell_icon_can);
		onekey_icon_can = findViewById(R.id.onekey_icon_can);
		book_icon_can = findViewById(R.id.book_icon_can);

		weatherTextView = (MarqueeText) findViewById(R.id.weatherTextView);

		cityTextView = (Button) findViewById(R.id.main_city);
		// viewSwitcher_online = (ViewSwitcher)
		// findViewById(R.id.viewSwitcher_online);
		mTvMore = (TextView) findViewById(R.id.main_about);

		badgeView = new BadgeView(this, mTvMore);
		badgeView.setText("New");
		badgeView.setTextColor(Color.RED);
		badgeView.setBadgeBackgroundColor(Color.YELLOW);
		badgeView.setTextSize(14);
		if (ETApp.getInstance().getCacheBoolean("new_message")) {
			badgeView.show();
		}
	}

	/**
	 * 获取在线车辆数量
	 */
	public void requestOnLine() {

		String mobile = getUserPhoneNum();
		long id = 0;
		if (StringUtils.isEmpty(mobile)) {
		} else {
			id = Long.valueOf(mobile);
		}
		onlineCallback = new OnlineCallback();
		NewNetworkRequest.getOnLine(System.currentTimeMillis(), InfoConfig.REFRESH_COMMERCIAL_ONLINE_DTIME, id, onlineCallback);
	}

	/**
	 * 获取天气
	 */
	public void requestWeather() {

		// if (isRefreshedWeather()) {
		// return;
		// }
		//
		// // 获取天气
		// try {
		// new AsyncTask<String, Integer, Integer>() {
		//
		// @Override
		// protected Integer doInBackground(String... params) {
		// try {
		// String values = new
		// String(HttpUtil.getBytes("http://61.4.185.48:81/g/"), "utf-8");
		// // var ip="125.71.77.186";var
		// // id=101270101;if(typeof(id_callback)!="undefined"){id_callback();}
		// String[] ids = values.split(";");
		// int index = ids[1].indexOf("=");
		// String id = ids[1].substring(index + 1);
		// return Integer.parseInt(id);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// return -1;
		// }
		//
		// @Override
		// protected void onPostExecute(Integer result) {
		// super.onPostExecute(result);
		// NewNetworkRequest.getWeather(result, weatherCallBack);
		// }
		// }.execute("");
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	private void setCityView(final String city) {
		currentCityName = city;
		cityTextView.post(new Runnable() {

			@Override
			public void run() {

				cityTextView.setText(city);
			}
		});
	}

	public class CityAddressCallback extends Callback<String> {
		@Override
		public void handle(String param) {
			if (param != null) {
				// 移除到市字
				try {
					int index = param.indexOf("市");
					if (index > 0) {
						currentCityName = param.substring(0, index);
					} else {
						currentCityName = param;
					}
				} catch (Exception e) {
					currentCityName = param;
				}
				saveCacheCity(currentCityName);
				cityTextView.post(new Runnable() {
					@Override
					public void run() {
						cityTextView.setText(currentCityName);
					}
				});
				// 刷新cityId为新获取数据
				cityId = session.get("_CITY_ID");
				requestWeather();// 刷新为最新获取到地址的城市天气
			}
		}
	}

	public class FirstAddressCallback extends Callback<String> {
		@Override
		public void handle(String param) {
			if (param != null) {
				currentAddress = String.valueOf(param);
				if (currentAddress.contains("1")) {// errorcode:1 地址解析出错
					currentAddress = "";
				}
			}
		}
	}

	public class OnlineCallback extends Callback<JSONObject> {

		@Override
		public void handle(JSONObject param) {
			// TODO Auto-generated method stub
			if (param == null) {
				weatherTextView.setText(String.format("创客货的，说到就到"));
				return;
			}

			String count;
			try {
				int realCount = param.getInt("onlineTaxiCount");
				count = InfoTool.getShowNumber(realCount) + " 辆";
				SpannableStringBuilder builder = new SpannableStringBuilder();
				SpannableString sb = new SpannableString(count);
				sb.setSpan(new ForegroundColorSpan(Color.RED), 0, count.length() - 2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

				builder.append("在线出租车").append(sb);

				weatherTextView.setText(builder);
				// viewSwitcher_online.showNext();

				session.set("_ONLINE", String.valueOf(realCount));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				weatherTextView.setText(String.format("创客货的，说到就到"));
				// tvOnlineCar.setVisibility(View.INVISIBLE);
			}
		}

		@Override
		public void error(Throwable e) {
			// TODO Auto-generated method stub
			super.error(e);
			weatherTextView.setText(String.format("创客货的，说到就到"));
		}

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
	public void onBackPressed() {
		showDialog(CLOSE_DLG);

		AppLog.LogD("----------------------------------------");
		// super.onBackPressed();
	}

	/**
	 * 天气服务:根据城市名获取城市Id：
	 * 
	 * @param context
	 * @param cityName
	 *            城市名字，这个不能够包含'市'及以后的字符
	 * @return
	 */
	public WeatherCityBean getCityIdByCityName(Context context, String cityName) {
		// 城市名字，这个不能够包含'市'及以后的字符
		String useCityName = cityName;
		int index = useCityName.indexOf("市");
		if (index > 0) {
			useCityName = useCityName.substring(0, index);
		}

		SQLiteDatabase checkDB = null;

		Cursor cursor = null;
		try {
			String databaseFilename = context.getDatabasePath(Const.WEATHER_DB_NAME).getPath();
			checkDB = SQLiteDatabase.openDatabase(databaseFilename, null, SQLiteDatabase.OPEN_READONLY);

			cursor = checkDB.rawQuery("select * from citys where name = ?", new String[] { useCityName });

			while (cursor.moveToNext()) {

				WeatherCityBean city = new WeatherCityBean();

				city.setName(cursor.getString(cursor.getColumnIndex("name")));
				city.setNumber(cursor.getString(cursor.getColumnIndex("city_num")));
				return city;

			}

		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {

			if (cursor != null) {
				cursor.close();
			}
			if (checkDB != null) {
				checkDB.close();
			}
		}
		return null;
	}

	/**
	 * 把数据库文件放到数据库目录下
	 * 
	 * @param context
	 */
	public void copyDataBase(Context context) {
		String databaseFilenames = context.getDatabasePath(Const.WEATHER_DB_NAME).getPath();

		FileOutputStream os = null;
		try {
			os = new FileOutputStream(databaseFilenames);// 得到数据库文件的写入流
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		InputStream is = null;
		try {
			is = context.getResources().getAssets().open(Const.WEATHER_DB_NAME);// 得到数据库文件的数据流
			byte[] buffer = new byte[8192];
			int count = 0;

			while ((count = is.read(buffer)) > 0) {
				os.write(buffer, 0, count);
				os.flush();
			}
		} catch (IOException e) {

		}
		try {
			is.close();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检查数据库是否存在
	 * 
	 * @param context
	 * @return
	 */
	public boolean checkDataBase(Context context) {
		SQLiteDatabase checkDB = null;
		try {
			String databaseFilename = context.getDatabasePath(Const.WEATHER_DB_NAME).getPath();
			checkDB = SQLiteDatabase.openDatabase(databaseFilename, null, SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
		}
		if (checkDB != null) {
			checkDB.close();
		}
		return checkDB != null ? true : false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		weatherTextView.stopScroll();
	}

	/**
	 * 显示注册dialog
	 */
	public void showRegistDialog(final Context context, String titile, String content, String btn1, String btn2, final String gotoClassName) {
		Callback<Object> okBtnCallback = new Callback<Object>() {
			@Override
			public void handle(Object param) {
				try {
					Intent intent;
					intent = new Intent(context, Class.forName(gotoClassName));
					intent.putExtra("type", 2);// 注册完后，直接跳到个人信息页面
					startActivity(intent);

					CommonDialog dialog = (CommonDialog) param;
					dialog.dismiss();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		};

		Callback<Object> cancelBtnCallback = new Callback<Object>() {
			@Override
			public void handle(Object param) {
				CommonDialog dialog = (CommonDialog) param;
				dialog.dismiss();
			}
		};

		Dialog dialog = new CommonDialog(MainActivityNew.this, titile, content, btn1, btn2, R.layout.dlg_close, okBtnCallback, cancelBtnCallback);
		dialog.show();
	}

	public Dialog createCloseDlg() {
		Callback<Object> okBtnCallback = new Callback<Object>() {
			@Override
			public void handle(Object param) {
				Intent service = new Intent(MainActivityNew.this, OneBookService.class);
				stopService(service);

				int currentVersion = android.os.Build.VERSION.SDK_INT;
				if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
					Intent startMain = new Intent(Intent.ACTION_MAIN);
					startMain.addCategory(Intent.CATEGORY_HOME);
					startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(startMain);
					System.exit(0);
				} else {// android2.1
					ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
					am.restartPackage(getPackageName());
				}
			}
		};

		Callback<Object> cancelBtnCallback = new Callback<Object>() {
			@Override
			public void handle(Object param) {
				CommonDialog dialog = (CommonDialog) param;
				dialog.dismiss();
				// dismissDialog(CLOSE_DLG);
			}
		};

		Dialog dialog = new CommonDialog(MainActivityNew.this, "提示", "您确认要退出吗？", "确定", "取消", R.layout.dlg_close, okBtnCallback, cancelBtnCallback);
		// dialog.show();
		return dialog;
	}

	/**
	 * 获取网络时间并判断本地和网络时间是否相差太大
	 */
	public void getNetTime() {
		AsyncUtil.goAsync(new Callable<Long>() {
			/**
			 * 获取网络时间
			 */
			@Override
			public Long call() throws Exception {
				SntpClient client = new SntpClient();
				if (client.requestTime("pool.ntp.org", 30000)) {
					return client.getNtpTime();
				} else {
					return 0L;
				}
			}
		}, new LoadCallback<Long>() {
			@Override
			public void onStart() {
			}

			/**
			 * 判断本地和网络时间是否相差太大
			 */
			@Override
			public void handle(Long serverTime) {
				long localTime = Calendar.getInstance().getTimeInMillis();
				try {
					if (serverTime != 0 && Math.abs(serverTime - localTime) >= 10 * 60 * 1000) {// 相差10分钟
						Dialog dialog = createModifySysTimeDlg();
						dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
						dialog.show();
					}
				} catch (Exception e) {
				}
			}
		});
	}

	/**
	 * 提示修改手机系统时间
	 * 
	 * @return
	 */
	public Dialog createModifySysTimeDlg() {
		Callback<Object> okBtnCallback = new Callback<Object>() {
			@Override
			public void handle(Object param) {
				CommonDialog dialog = (CommonDialog) param;
				dialog.dismiss();
				try {
					Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				} catch (Exception e) {
					Toast.makeText(context, "请进入'设置'中重新设置时间", Toast.LENGTH_SHORT).show();
				}
			}
		};

		Callback<Object> cancelBtnCallback = new Callback<Object>() {
			@Override
			public void handle(Object param) {
				CommonDialog dialog = (CommonDialog) param;
				dialog.dismiss();
			}
		};

		Dialog dialog = new CommonDialog(MainActivityNew.this, "信息提示", "检测到您手机时间与服务器时间相差较大，可能影响使用，建议调整手机时间", "设置", "取消", R.layout.dlg_close, okBtnCallback, cancelBtnCallback);
		return dialog;
	}

	public boolean isRefreshedWeather() {
		return isRefreshedWeather;
	}

	public void setRefreshedWeather(boolean isRefreshedWeather) {
		this.isRefreshedWeather = isRefreshedWeather;
	}
}