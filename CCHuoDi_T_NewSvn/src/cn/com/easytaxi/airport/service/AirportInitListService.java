package cn.com.easytaxi.airport.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.json.JSONObject;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.airport.bean.AirportBean;
import cn.com.easytaxi.airport.store.AirportSessionAdapter;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.common.Callback;

public class AirportInitListService extends Service implements Runnable{
    public  static final String AIRPORT_LIST_VERSION = "airport_list_version";
	private AirportSessionAdapter airportSession;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		airportSession = new AirportSessionAdapter(this);
		Thread thread = new Thread(this);
		thread.start();
	}

	/**
	 * 从文件中获取机场的列表数据：用于接机送机时候的列表
	 * @param callBack
	 */
	public void getAirportListFromJsonFile(final Callback<Object> callBack) {
		new AsyncTask<JSONObject, Object, String>() {

			@Override
			protected String doInBackground(JSONObject... params) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.airport_list)));
				String line = null;
				try {
					line = reader.readLine();
				} catch (IOException e) {
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
	
	/**
	 * 从用逗号分隔的文件中解析出数据
	 */
	public void getAirportListFromFile(Context context){
		String line_str = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.airport_list)));
		ArrayList<AirportBean> airportList = new ArrayList<AirportBean>();

		try {
			while ((line_str = reader.readLine()) != null) {
				if (line_str.equals("")) {
					continue;
				}
				AirportBean airport = new AirportBean();
				String[] infos = line_str.split(",");
				airport.id = Integer.parseInt(infos[0]);
				airport.name = infos[1];
				airport.longitude = Integer.parseInt(infos[2]);
				airport.latitude = Integer.parseInt(infos[3]);
				airportList.add(airport);
			}
			//第一项为版本信息
			AirportBean airport = airportList.get(0);
			int airportVersion = airport.getLatitude();
			airportList.remove(0);
			//存到数据库
			airportSession.saveAirportList(airportList);
			//保存airport的数据版本
			ETApp.getInstance().saveCahceInt(AIRPORT_LIST_VERSION, airportVersion);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			close();
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (airportSession != null) {
			airportSession.close();
		}
	}

	private void updateAirport(boolean hasNew) {
		if (!hasNew) {
			close();
			return;
		}
	}

	private void close() {
		this.stopSelf();
	}

	@Override
	public void run() {
		int airportListVersion = ETApp.getInstance().getCacheInt(AIRPORT_LIST_VERSION);

		if (airportListVersion <= 0) {//初始化数据
			try {
				getAirportListFromFile(getBaseContext());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {//暂时不更新数据
			updateAirport(false);
		}
	}
}
