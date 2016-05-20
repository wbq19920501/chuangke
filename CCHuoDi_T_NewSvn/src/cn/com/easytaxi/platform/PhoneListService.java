package cn.com.easytaxi.platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import cn.com.easytaxi.AppLog;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.common.SessionAdapter;
import cn.com.easytaxi.phone.common.DaoAdapter;

public class PhoneListService extends Service {

	private SessionAdapter session;
	private DaoAdapter dao;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		session = new SessionAdapter(this);
		dao = new DaoAdapter(this);
		String version = session.get("_PHONE_LIST_VERSION");

		if (StringUtils.isEmpty(version)) {
			try {
				getCity(callback);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			updateCity(false);
		}

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
//		AppLog.LogD("xyw", "service onDestroy");

		if (session != null) {
			session.close();
		}

		if (dao != null) {
			dao.close();
		}
	}

	private void updateCity(boolean hasNew) {
		if (!hasNew) {
			close();
			return;
		}

	}

	Callback<Object> callback = new Callback<Object>() {
		@Override
		public void handle(Object param) {
			if (param != null) {
				try {
					
					String json = (String) param;
					JSONObject jsons = new JSONObject(json);
					dao.savePhoneList(jsons.getJSONArray("datas"));
					session.set("_PHONE_LIST_VERSION",  "version");
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void complete() {
			close();
		}
	};

	private void getCity(final Callback<Object> callBack) throws Exception {
 

		new AsyncTask<JSONObject, Object, String>() {

			@Override
			protected String doInBackground(JSONObject... params) {
				// TODO Auto-generated method stub
				 

				BufferedReader reader = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.tell)));

				String line = null;
				try {
					line = reader.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return line;
			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				try {
					callBack.handle((Object)result);
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					callBack.error(e);
				}

				callBack.complete();
			}

		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private void close() {
		this.stopSelf();
	}
}
