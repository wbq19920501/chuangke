package cn.com.easytaxi.platform;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.telephony.gsm.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.common.NetChecker;
import cn.com.easytaxi.common.SessionAdapter;
import cn.com.easytaxi.common.User;
import cn.com.easytaxi.dialog.MyLoadingDialog;

public abstract class YdBaseActivity extends Activity {
	protected SessionAdapter session;
	protected MyLoadingDialog loading;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (VERSION.SDK_INT >= 14) {
			setTheme(android.R.style.Theme_Holo_Light_NoActionBar);
		}
		
		session = new SessionAdapter(this);
		setUncaughtExceotion();
	}

	protected abstract void initViews();
	protected abstract void initListeners();
	protected abstract void initUserData();
	protected abstract void regReceiver();
	protected abstract void unRegReceiver();
	
	
	protected  String getCityId(){
		return  session.get("_CITY_ID");
	}
	
	protected String getCityName(){
		return   session.get("_CITY_NAME");
	}
	
	protected String getPassengerId(){
		
		long id = ETApp.getInstance().getCurrentUser().getPassengerId();
		
		String pid = String.valueOf(id);
		
		if(TextUtils.isEmpty(pid)){
			pid = session.get(User._MOBILE_NEW);
		}
		if(TextUtils.isEmpty(pid)){
			pid = session.get(User._MOBILE);
		}
		 
		if(TextUtils.isEmpty(pid)){
			pid = ETApp.getInstance().getCurrentUser().getPhoneNumber(User._MOBILE);
		}
 
		return pid;
	}
	
	protected boolean isNetAvailable(){
		NetChecker nc = NetChecker.getInstance(this.getApplicationContext());
		if(nc.checkNetwork()){
			return true;
		}else{
			return false;
		}
	}
	
	private void setUncaughtExceotion() {
		if (AppLog.DEBUG) {
			final UncaughtExceptionHandler subclass = Thread.currentThread().getUncaughtExceptionHandler();
			Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
				@Override
				public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
					Log.getStackTraceString(paramThrowable);
					AppLog.LogD(paramThrowable.getMessage());
					AppLog.LogE("uncaughtException", paramThrowable.getMessage());
					subclass.uncaughtException(paramThread, paramThrowable);		
				}
			});
		}
	}
	
	public void showLoadingDialog(String text) {
		if (loading == null)
			loading = new MyLoadingDialog(this);
		loading.showWithText(text);
	}

	public void cancelLoadingDialog() {
		if (loading != null && loading.isShowing())
			loading.dismiss();
	}
	
	
	@Override
	protected void onDestroy() {
		if(session != null){
			session.close();
			session = null;
		}
		super.onDestroy();
	}
	
	protected void sendSms(String mobile , String msg) {
		SmsManager smsManager = SmsManager.getDefault();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		smsManager.sendTextMessage(mobile, null, msg, null, null);
	}
	
	protected void startRegActivity(){
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
	}
	
}
