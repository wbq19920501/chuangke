package cn.com.easytaxi.service;

import java.util.ArrayList;
import java.util.List;

import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.NewNetworkRequest;
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
 

public class MyService extends Service {
	private AsyncQueryHandler asyncQuery;
	private boolean isRunning =false;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	protected void initUserData() {
		asyncQuery = new MyAsyncQueryHandler(getContentResolver());
		Uri uri = Uri.parse("content://com.android.contacts/data/phones");
		String[] projection = { "_id", "display_name", "data1", "sort_key" };
		asyncQuery.startQuery(0, null, uri, projection, null, null, "sort_key COLLATE LOCALIZED asc");
	}

	private class MyAsyncQueryHandler extends AsyncQueryHandler {
		List<ContactEntity> list = new ArrayList<ContactEntity>();

		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {
				isRunning  = true;
				list.clear();
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					ContactEntity cv = new ContactEntity();
					cursor.moveToPosition(i);
					String name = cursor.getString(1);
					String number = cursor.getString(2);
					String sortKey = cursor.getString(3);
					// System.out.println(sortKey);
					if (number.startsWith("+86")) {
						cv.name = name;
						cv.phone = number.substring(3); // È¥µô+86
						cv.sortKey = sortKey;

					} else {
						cv.name = name;
						cv.phone = number;
						cv.sortKey = sortKey;
					}
					cv.phone.replaceAll(" ", "");
					cv.isSelected = false;
					list.add(cv);
				}
				if (list.size() > 0) {
					 NewNetworkRequest.uploadPhones(handler, ETApp.getInstance().getCurrentUser().getPhoneNumber(User._MOBILE), cityId, list);
				}
			}

			if (cursor != null) {
				cursor.close();
			}
			//
		}

	}
	
	
	Handler handler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			
			if(msg.arg1 == 200){
				isRunning = false;
				ETApp.getInstance().saveCahceLong("upPhoneTime",System.currentTimeMillis());
				
				stopSelf();
			}
			
		};
	};
	String cityId;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
 
		if(intent != null && !isRunning){
		  
			
			cityId = intent.getStringExtra("cityId");
			long lastTime = ETApp.getInstance().getCacheLong("upPhoneTime");
			long now = System.currentTimeMillis();
			if(lastTime == 0 && !isRunning){
				initUserData();			 
			}else if((now -lastTime )  > 23*60*60*1000 && !isRunning){
				initUserData();
			}
		}else{
			 
		}
		
		if(intent == null){
			stopSelf();
		}
		
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onDestroy() {
		AppLog.LogD(this.getClass().getSimpleName() + " onDestroy");
		super.onDestroy();
	}
}
