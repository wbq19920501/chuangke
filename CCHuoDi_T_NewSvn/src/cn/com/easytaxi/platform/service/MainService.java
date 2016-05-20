package cn.com.easytaxi.platform.service;

import org.apache.commons.lang3.StringUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.ETApp;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.platform.IndexActivity;
import cn.com.easytaxi.platform.common.common.Const;
import cn.com.easytaxi.platform.common.common.ReceiveMsgBean;
import cn.com.easytaxi.platform.common.common.UdpMessageHandleListener;

public class MainService extends Service {

	public static UdpChannelService udpChannelService;
	public static LocationService locationService;
	//

	private ETApp app;
	private OneBookCore oneBookCore;

	private UdpMessageHandleListener udpMessageHandleListener_002 = new UdpMessageHandleListener() {

		@Override
		public void handle(ReceiveMsgBean msg) {

			if (oneBookCore == null) {
				oneBookCore = OneBookCore.getInstance(MainService.this);
			}
			oneBookCore.dispatchUdp(msg, 0xFF0002);
		}
	};
	private UdpMessageHandleListener udpMessageHandleListener_003 = new UdpMessageHandleListener() {

		@Override
		public void handle(ReceiveMsgBean msg) {

			if (oneBookCore == null) {
				oneBookCore = OneBookCore.getInstance(MainService.this);
			}
			oneBookCore.dispatchUdp(msg, 0xFF0003);
		}
	};

	private UdpMessageHandleListener udpMessageHandleListener_006 = new UdpMessageHandleListener() {

		@Override
		public void handle(ReceiveMsgBean msg) {
			if (oneBookCore == null) {
				oneBookCore = OneBookCore.getInstance(MainService.this);
			}
			oneBookCore.dispatchUdp(msg, 0xFF0006);
		}
	};

	private UdpMessageHandleListener udpMessageHandleListener_001 = new UdpMessageHandleListener() {
		@Override
		public void handle(ReceiveMsgBean msg) {
			if (oneBookCore == null) {
				oneBookCore = OneBookCore.getInstance(MainService.this);
			}
			oneBookCore.dispatchUdp(msg, 0xFF0001);
		}
	};

	// 0xFF0010 新消息
	private UdpMessageHandleListener newMessageHandleListener = new UdpMessageHandleListener() {
		@Override
		public void handle(ReceiveMsgBean msg) {
			AppLog.LogD("====---1111--udp--new message ----  " + Integer.toHexString(msg.getMsgId()));
			if (oneBookCore == null) {
				oneBookCore = OneBookCore.getInstance(MainService.this);
			}
			oneBookCore.dispatchUdp(msg, 0xFF0010);
		}
	};

	// 订单状态变化通知
	private UdpMessageHandleListener bookStateChangedLis = new UdpMessageHandleListener() {
		@Override
		public void handle(ReceiveMsgBean msg) {
			if (oneBookCore == null) {
				oneBookCore = OneBookCore.getInstance(MainService.this);
			}
			oneBookCore.dispatchUdp(msg, Const.UDP_BOOK_STATE_CHANGED);
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		app = ETApp.getInstance();
		AppLog.LogD("MainService onCreate === ");
		try {
			// 启动UDP服务
			udpChannelService = new UdpChannelService();
			// 启动位置定位服务
			locationService = new LocationService(this);

		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	boolean isStarted = false;

	private void regListener() {
		udpChannelService.regMsgHandleListener(Const.UDP_BOOK_TAXI_SCHEDULE, udpMessageHandleListener_003);
		udpChannelService.regMsgHandleListener(Const.UDP_BOOK_TAXI_REPLY, udpMessageHandleListener_002);
		udpChannelService.regMsgHandleListener(0xFF0006, udpMessageHandleListener_006);
		udpChannelService.regMsgHandleListener(0xFF0001, udpMessageHandleListener_001);
		// 推送新的消息
		udpChannelService.regMsgHandleListener(0xFF0010, newMessageHandleListener);
		udpChannelService.regMsgHandleListener(Const.UDP_BOOK_STATE_CHANGED, bookStateChangedLis);
	}

	private void unRegListener() {
		udpChannelService.unregMsgHandleListener(cn.com.easytaxi.platform.common.common.Const.UDP_BOOK_TAXI_REPLY);
		udpChannelService.unregMsgHandleListener(cn.com.easytaxi.platform.common.common.Const.UDP_BOOK_TAXI_SCHEDULE);
		udpChannelService.unregMsgHandleListener(Const.UDP_BOOK_STATE_CHANGED);
		udpChannelService.unregMsgHandleListener(0xFF0006);
		udpChannelService.unregMsgHandleListener(0xFF0001);
		udpChannelService.unregMsgHandleListener(0xFF0010);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		String mobile = null;
		long id = 0;
		mobile = app.login();
		if (StringUtils.isEmpty(mobile)) {
			id = 0;
		} else {
			id = Long.valueOf(mobile);
		}
		AppLog.LogD(app.getCurrentUser().toString());

		try {

			if (isStarted) {
				udpChannelService.stop();
				udpChannelService.setId(id);
				udpChannelService.start(id);

				return START_STICKY;
			} else {
				if (udpChannelService == null) {

					udpChannelService = new UdpChannelService();
					locationService = new LocationService(this);
				}

				// switchNotification();
				udpChannelService.start(id);
				locationService.start();

				regListener();
				launchBusinessService();

				isStarted = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return START_STICKY;
	}

	private void launchBusinessService() {
		Intent intent = new Intent(this, OneBookService.class);
		intent.setAction(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_START);
		startService(intent);

	}

	public void showNotification(String title, String msgBody) {
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.nitify_logo003, "创客货的", System.currentTimeMillis());
		notification.defaults = Notification.DEFAULT_SOUND;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		Intent openIntent = new Intent(this, IndexActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, openIntent, 0);
		notification.setLatestEventInfo(this, title, msgBody, contentIntent);
		mNotificationManager.notify(0, notification);
	}

	@Override
	public void onDestroy() {

		unRegListener();
		udpChannelService.stop();
		locationService.stop();

		udpChannelService = null;
		locationService = null;
		isStarted = false;
		AppLog.LogD("mainservice destroy");
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
