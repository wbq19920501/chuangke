package cn.com.easytaxi.service;

import java.util.ArrayList;
import java.util.List;

import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.NewNetworkRequest;
import cn.com.easytaxi.common.ActionLogUtil;
import cn.com.easytaxi.common.Const;
import cn.com.easytaxi.common.User;
import cn.com.easytaxi.ui.ContactList.ContactEntity;
 
import android.app.Service;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
 

public class UploadActionLogFileService extends Service {
	public static String cityId = "";
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		// 上传日志
		if (Const.isActionLogCouldUpload) {
			if (cityId != null && !cityId.equals("")) {
				ActionLogUtil.uploadActionLog(cityId,
						UploadActionLogFileService.this);
			}
		}
	}
	
	 
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
