package cn.com.easytaxi.workpool;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.common.SessionAdapter;
import cn.com.easytaxi.common.User;

public class BaseActivity extends Activity {
	public SessionAdapter session;
	public final String Tag = getClass().getSimpleName();
	public BaseActivity self;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		session = new SessionAdapter(this);
		self = this;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (session != null) {
			session.close();
			session = null;
		}
	}

	
	protected boolean isLogin(){
		if (ETApp.getInstance().isLogin()) {
			return false;
		}else{
			return true;
		}
	}
	public void d(String msg) {
		AppLog.LogD(Tag, msg);
	};
	
	protected String getPassengerId() {
		String id = ETApp.getInstance().getCurrentUser().getPhoneNumber("_MOBILE");
		if (StringUtils.isEmpty(id)) {
			return session.get("_MOBILE");
		} else {
			return id;
		}
	}
	
	protected  String getCityId(){
		return  session.get("_CITY_ID");
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
