package cn.com.easytaxi.ui;

import java.lang.Thread.UncaughtExceptionHandler;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.common.SessionAdapter;
import cn.com.easytaxi.common.User;

public class BaseFragementActivity extends FragmentActivity {

	protected SessionAdapter session;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
//		catchCrash();
		session = new SessionAdapter(this);
	}

	@Override
	protected void onDestroy() {
		if (session != null) {
			session.close();
			session = null;
		}
		super.onDestroy();
	}

	public String getPassengerId() {
		String id = ETApp.getInstance().getCurrentUser().getPhoneNumber(User._MOBILE);
		return id;
	}

	public int getCacheLat() {
		return ETApp.getInstance().getCacheInt("_P_LAT");
	}

	public int getCacheLng() {
		return ETApp.getInstance().getCacheInt("_P_LNG");
	}
	
	public String getCityId(){
		return   session.get("_CITY_ID");
	}
	public String getCityName(){
		return   session.get("_CITY_NAME");
	}


	  
	
	
	protected void catchCrash() {
		if (AppLog.DEBUG) {
			final UncaughtExceptionHandler subclass = Thread.currentThread().getUncaughtExceptionHandler();
			Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
				@Override
				public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
					Log.getStackTraceString(paramThrowable);
					Log.e("xyw", "uncaughtException", paramThrowable);
					subclass.uncaughtException(paramThread, paramThrowable);
				}
			});
		}
	}
}
