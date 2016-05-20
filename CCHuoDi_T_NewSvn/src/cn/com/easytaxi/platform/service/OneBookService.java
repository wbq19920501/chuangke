package cn.com.easytaxi.platform.service;

import cn.com.easytaxi.AppLog;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.platform.IndexActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;

public class OneBookService extends Service {

	private OneBookCore core;;
	private static PowerManager sPm;
	private static PowerManager.WakeLock sWl;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sPm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		sWl = sPm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "OneBookWakeLock");

		initCore();

	}

	public static void maybeAquireWakelock() {
		if (!sWl.isHeld()) {
			sWl.acquire();
		}
	}

	private void initCore() {
		if (core == null) {
			core = OneBookCore.getInstance(this);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null) {
			startService(new Intent(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_START));
			return START_NOT_STICKY;
		}

		if (intent.getAction().equals(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_START)) {
			maybeAquireWakelock();
			initCore();
			sWl.release();
			// AppLog.LogD("start one book  serveic ===");
		} else {
			maybeAquireWakelock();
			handleIntent(intent);
			sWl.release();
		}
		return START_NOT_STICKY;

	}

	private void handleIntent(Intent intent) {
		String action = intent.getAction();
		if (action.equals(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_CMD)) {
			dispatchCmd(intent);
		}
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

	private void dispatchCmd(Intent intent) {
		int subCmd = intent.getIntExtra(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_SUB_CMD_REQ, 0);
		switch (subCmd) {
		case EasyTaxiCmd.ONE_TAXI_BOOK_SUB_CMD_SUBMIT_REQ:
			core.submitOneBook(this.getApplicationContext(), intent);
			break;

		default:
			break;
		}
	}
	
	@Override
	public void onDestroy() {
		AppLog.LogD("OneBookService destroy");
		if (sWl.isHeld()) {
			sWl.release();
		}
		
		super.onDestroy();
	}

}
