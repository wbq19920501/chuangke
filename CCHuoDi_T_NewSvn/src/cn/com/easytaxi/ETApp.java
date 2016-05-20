package cn.com.easytaxi;

import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import cn.com.easytaxi.airport.bean.AirportBean;
import cn.com.easytaxi.common.SessionAdapter;
import cn.com.easytaxi.common.User;
import cn.com.easytaxi.onetaxi.common.BookBean;
import cn.com.easytaxi.platform.common.Cities.City;
import cn.com.easytaxi.platform.service.EasyTaxiCmd;
import cn.com.easytaxi.platform.service.MainService;
import cn.com.easytaxi.ui.MoreWebviewActivity;
import cn.com.easytaxi.ui.bean.BookDataSource;
import cn.com.easytaxi.ui.bean.MsgBean;
import cn.com.easytaxi.ui.bean.MsgData;
import cn.com.easytaxi.ui.bean.PushMessage;

import com.baidu.mapapi.SDKInitializer;
import com.easytaxi.etpassengersx.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ETApp extends Application {

	private static ETApp instance = null;
	private MessageReceiver messageReceiver;

	/** old code */
	public static HashMap<String, SoftReference<Object>> cache = new HashMap<String, SoftReference<Object>>();

//	private BMapManager bMapManager;

	// sdcard上对应包名的根目录
	private static String mSdcardAppDir;

	private User currentUser;

	private LocalPreferences preferences;

	private MobileInfo mobileInfo;

	private BookBean cacheBookbean;

	private byte[] soundData;

	private String sendMode = "wordInput";// 即时召车最后的状态

	private ArrayList<City> cityCache;// 城市列表的缓存

	private ArrayList<AirportBean> airportListCache;// 机场列表的缓存

	public boolean isNew = false; // version

	public static BookDataSource bds = new BookDataSource();

	public byte[] getSoundData() {
		return soundData;
	}

	public ArrayList<AirportBean> getAirportListCache() {
		return airportListCache;
	}

	public void setAirportListCache(ArrayList<AirportBean> airportListCache) {
		this.airportListCache = airportListCache;
	}

	public String getSendMode() {
		return sendMode;
	}

	public ArrayList<City> getCityCache() {
		return cityCache;
	}

	public void setCityCache(ArrayList<City> cityCache) {
		this.cityCache = cityCache;
	}

	public void setSendMode(String sendMode) {
		this.sendMode = sendMode;
	}

	public void setSoundData(byte[] soundData) {
		this.soundData = soundData;
	}

	public static ETApp getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		SDKInitializer.initialize(getApplicationContext());
		CrashHandler.getInstance().init(this.getApplicationContext());
		initClobalData();
		instance = this;

		messageReceiver = new MessageReceiver();
		registerReceiver(messageReceiver, new IntentFilter("cn.com.easytaxi.pushmsg.nima.aciton"));
		//
		new Thread() {
			public void run() {
				for (int i = 10; i < 20; i++) {

					try {
						sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					Intent intent = new Intent("cn.com.easytaxi.pushmsg.nima.aciton");
					PushMessage p = new PushMessage();
					p.set_is_show(i / 2);
					p.set_msg_sub_type(i / 6);
					p.set_url("");
					p.setMsg("短信息  i+ " + i);
					p.set_CREATE_TIME(new Date(System.currentTimeMillis()).toString());
					Gson g = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
					String info = g.toJson(p, PushMessage.class);
					byte[] data = null;
					try {
						data = info.getBytes("utf-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					intent.putExtra("pushbody", data);
					// sendBroadcast(intent);
				}

			};
		}.start();

	}

	public BookBean getCacheBookbean() {
		return cacheBookbean;
	}

	public void setCacheBookbean(BookBean cacheBookbean) {
		this.cacheBookbean = cacheBookbean;
	}

	private void initClobalData() {
		mSdcardAppDir = Environment.getExternalStorageDirectory() + "/" + this.getPackageName();
		currentUser = new User();
		preferences = LocalPreferences.getInstance(this);
		mobileInfo = MobileInfo.getInstance(this);
		// login();
//		initBMapManager();
		startMainService();
	}

	private void startMainService() {
		Intent serviceIntent = new Intent(this, MainService.class);
		serviceIntent.setAction(EasyTaxiCmd.START_MAINSERVICE);
		startService(serviceIntent);
	}

	/**
	 * 
	 * @return mobile
	 */
	public synchronized String login() {
		SessionAdapter session = new SessionAdapter(this);

		// 判断是否用新程序注册过，
		String tmpMobile;
		String mobileNew = session.get(User._MOBILE_NEW);
		tmpMobile = mobileNew;
		// 新版本注册登陆用的账号 mobileNew
		if (TextUtils.isEmpty(mobileNew)) {
			String mobile = session.get(User._MOBILE);
			tmpMobile = mobile;
			if (TextUtils.isEmpty(mobile)) {
				// 用户需要重新注册

				currentUser.setLogin(false);
			} else {
				// 老版本登陆

				fillUserInfo(session, mobile);
				session.set(User._ISLOGIN, User._LOGIN_LOGIN);
				session.set(User._MOBILE_NEW, mobile);
			}
		} else {
			String isLogin = session.get(User._ISLOGIN);
			if (!TextUtils.isEmpty(isLogin)) {
				if (isLogin.equals(User._LOGIN_LOGIN)) {
					fillUserInfo(session, mobileNew);
				}

				if (isLogin.equals(User._LOGIN_CANCLED)) {
					// 用户需要重新注册
					currentUser.setLogin(false);
				}
			} else {
				currentUser.setLogin(false);
			}
		}

		try {
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return tmpMobile;
	}

	public synchronized void logOut() {
		SessionAdapter session = new SessionAdapter(this);
		session.set(User._ISLOGIN, User._LOGIN_CANCLED);// 注销
		currentUser.setLogin(false);
		try {
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void fillUserInfo(SessionAdapter session, String mobile) {
		currentUser.setLogin(true);
		currentUser.setPhoneNumber(User._MOBILE, mobile);
		String sex = session.get(User._SEX);
		currentUser.setSex(StringUtils.isEmpty(sex) ? -1 : Integer.valueOf(sex));
		String pIdString = session.get(User._PUID);
		currentUser.setPassengerId(StringUtils.isEmpty(pIdString) ? 0 : Long.valueOf(pIdString));
		String name = session.get(User._NICKNAME);
		currentUser.setUserNickName(StringUtils.isEmpty(name) ? "" : name);
	}

//	private void initBMapManager() {
//		if (bMapManager == null) {
//			bMapManager = new BMapManager(this);
//			if (!bMapManager.init(Config.BAIDU_KEY, null)) {
//				AppLog.LogD("初始化百度地图失败!");
//			}
//		}
//	}
//
//	public BMapManager getBMapManager() {
//		initBMapManager();
//		return bMapManager;
//	}

	public boolean isLogin() {
		boolean ret = currentUser == null ? false : currentUser.isLogin();

		AppLog.LogD("login is " + ret);
		return ret;
	}

	public void setLogin(boolean isLogin) {
		if (currentUser != null) {
			currentUser.setLogin(isLogin);
		}
	}

//	public BMapManager getbMapManager() {
//		return bMapManager;
//	}

	public MobileInfo getMobileInfo() {
		return mobileInfo;
	}

	public User getCurrentUser() {
		return currentUser;
	}

	@Override
	public void onTerminate() {
//		if (bMapManager != null) {
//			bMapManager.destroy();
//			bMapManager = null;
//		}
		if (messageReceiver != null) {
			unregisterReceiver(messageReceiver);
		}

		super.onTerminate();
	}

	public void saveCacheString(String key, String value) {
		preferences.saveCacheString(key, value);

	}

	public String getCacheString(String key) {
		return preferences.getCacheString(key);
	}

	public int getCacheInt(String key) {
		return preferences.getCacheInt(key);
	}

	public void saveCahceInt(String key, int value) {
		preferences.saveCahceInt(key, value);
	}

	public long getCacheLong(String key) {
		return preferences.getCacheLong(key);
	}

	public void saveCahceLong(String key, long value) {
		preferences.saveCahceLong(key, value);
	}

	public boolean getCacheBoolean(String key) {
		return preferences.getCacheBoolean(key);
	}

	public void saveCacheBoolean(String key, boolean value) {
		preferences.saveCahceBoolean(key, value);
	}

	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			byte[] body = arg1.getByteArrayExtra("pushbody");
			if (body != null && body.length > 0) {
				try {
					String info = new String(body, "utf-8");
					toDispPushMsg(info);

				} catch (Exception e) {
					// TODO: handle exception
				}

			}

		}
	}

	public int nId = 1024;

	public void toDispPushMsg(String info) {

		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

		PushMessage pMsg = gson.fromJson(info, PushMessage.class);
		MsgBean msgBean = new MsgBean();
		msgBean.setCacheId(pMsg.getId());
		msgBean.setBody(pMsg.getMsg());
		msgBean.setDate(new Date(pMsg.get_CREATE_TIME()));

		try {
			MsgData.saveMsg(msgBean);

		} catch (Exception e) {
			// TODO: handle exception
		}
		boolean isPalySound = false;
		if (pMsg.get_is_show() == 0) {
			isPalySound = false;

		} else {
			isPalySound = true;

		}

		int picId = R.drawable.nitify_logo003;
		// 1=公告 2=推荐 3=服务 4=新闻 5=恭喜 6=验证系统，可APP端对应不同的消息提示图标
		switch (pMsg.get_msg_sub_type()) {
		case 1:

			picId = R.drawable.umeng_share_face_06;

			break;
		case 2:
			picId = R.drawable.umeng_share_face_05;

			break;
		case 3:
			picId = R.drawable.umeng_share_face_04;
			break;
		case 4:
			picId = R.drawable.umeng_share_face_03;
			break;
		case 5:
			picId = R.drawable.umeng_share_face_02;
			break;
		case 6:
			picId = R.drawable.umeng_share_face_01;
			break;

		default:
			break;
		}
		showNotification(pMsg.getMsg(), isPalySound, picId, pMsg.get_url());
	}

	private void showNotification(String msgString, boolean isPalySound, int picId, String url) {

		NotificationManager notificationManager = (NotificationManager) this.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		long id = System.currentTimeMillis();
		Notification notification = new Notification(picId, msgString, id);
		CharSequence contentTitle = "我的消息";
		CharSequence contentText = msgString;

		if (isPalySound) {
			notification.sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.noti);
		}

		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		if (TextUtils.isEmpty(url)) {
			intent.setComponent(new ComponentName("cn.com.easytaxi", "cn.com.easytaxi.ui.Message"));
			intent.putExtra("form", "notify");
		} else {

			intent.setComponent(new ComponentName("cn.com.easytaxi", "cn.com.easytaxi.ui.MoreWebviewActivity"));
			intent.putExtra(MoreWebviewActivity.TITLE, "消息通知");
			intent.putExtra(MoreWebviewActivity.URI, url);
		}
		// intent.setComponent(new ComponentName("cn.com.easytaxi",
		// Message.class));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

		//
		// Intent notificationIntent = new Intent(this, Message.class);
		// notificationIntent.setAction(Intent.ACTION_MAIN);
		// notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
		notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);
		// notification.flags = Notification.FLAG_NO_CLEAR |
		// Notification.FLAG_ONGOING_EVENT ;
		notifyId++;
		notificationManager.notify(notifyId, notification);

		// clearNotification(notifyId, notificationManager);
	}

	int notifyId = 0;

	private void clearNotification(final int id, final NotificationManager nf) {

		new Thread() {

			public void run() {

				try {
					sleep(10000);
					if (nf != null) {
						nf.cancel(id);
					}

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			};

		}.start();

	}

	public static String getmSdcardAppDir() {
		return mSdcardAppDir;
	}

	public static void setmSdcardAppDir(String mSdcardAppDir) {
		ETApp.mSdcardAppDir = mSdcardAppDir;
	}
}
