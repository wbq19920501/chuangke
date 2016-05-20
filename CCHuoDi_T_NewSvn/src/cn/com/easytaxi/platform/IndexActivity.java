package cn.com.easytaxi.platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.NewNetworkRequest;
import cn.com.easytaxi.airport.service.AirportInitListService;
import cn.com.easytaxi.common.NetChecker;
import cn.com.easytaxi.common.ToolUtil;
import cn.com.easytaxi.platform.common.Cities.City;
import cn.com.easytaxi.service.AlarmClockBookService;
import cn.com.easytaxi.service.UploadActionLogFileService;
import cn.com.easytaxi.util.InfoConfig;

import com.easytaxi.etpassengersx.R;

public class IndexActivity extends YdBaseActivity {

	private IndexActivity self = this;
	private TextView infoView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.p_index);

		Bundle bundle = this.getIntent().getBundleExtra("appBundle");
		if (bundle != null) {
			AppLog.LogD("start app by--->" + bundle.getString("phoneNumber"));
			AppLog.LogD("start app type--->" + bundle.getInt("appType"));
		} else {
			AppLog.LogD("start app by--->self");
		}

		initSystemData();
		initViews();
		initListeners();
		initUserData();

		// 上传日志
		uploadActionLogFile();

//		String key = AndroidUtil.getPublicKey(IndexActivity.this.getApplicationContext());
//		System.out.println("key---------->" + key);
	}

	/**
	 * 上传日志
	 */
	public void uploadActionLogFile() {
		boolean isAvailableNetwork = NetChecker.getInstance(self).isWifiAvailable();
		if (isAvailableNetwork) {
			Intent intent = new Intent(IndexActivity.this, UploadActionLogFileService.class);
			UploadActionLogFileService.cityId = getCityId();
			startService(intent);
		}
	}

	@Override
	protected void initUserData() {

		// if (dao.get("_CITY_LIST_VERSION").equals("4")) {
		saveCityFile();

	}

	private void saveCityFile() {
		FileSaveTask saveCityTask = new FileSaveTask(IndexActivity.this);
		saveCityTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"saveCity");
	}

	@Override
	protected void initListeners() {
		// nothing
	}

	@Override
	protected void initViews() {
		infoView = (TextView) findViewById(R.id.login_info);
		infoView.setText(R.string.fisrt_loading);
	}

	private void initSystemData() {
		// 关闭欢迎页语音提示
		// initSound();
		// playSound(R.raw.p_main_welcom, true);

		ToolUtil.createShortCut(this, session); // 创建快捷方式
		this.startService(new Intent(this, PhoneListService.class));
		// 初始化机场数据列表
		this.startService(new Intent(this, AirportInitListService.class));
		new Thread() {
			@Override
			public void run() {
				NewNetworkRequest.getDiaoDuPriceList(1);
				// super.run();
			}
		}.start();

		// 开启订单列表闹钟服务
		startService(new Intent(self, AlarmClockBookService.class));
	}

	private void startPlatform() {

		Intent intent = new Intent(self, MainActivityNew.class);
		startActivity(intent);
		finish();
	}

	private void playSound(final int resId, boolean isSound) {
		if (isSound) {
			ToolUtil.playSound(self, resId);
		}
	}

	private void initSound() {
		setVolumeControlStream(AudioManager.STREAM_MUSIC); // 设置音量控制为媒体
	}

	@Override
	protected void onDestroy() {
		if (session != null) {
			session.close();
			session = null;
		}
		super.onDestroy();
	}

	/**
	 * only to load city
	 * 
	 * @author Administrator
	 * 
	 */
	class FileSaveTask extends AsyncTask<String, Integer, Integer> {

		static final int COMPLETE = 100;
		String line_str;
		String result;
		Context context;
		long time;

		FileSaveTask(Context context) {
			this.context = context;
		}

		@Override
		protected Integer doInBackground(String... params) {
			time = System.currentTimeMillis();
			String cityVersion = session.get("_CITY_LIST_VERSION");
			if (StringUtils.isEmpty(cityVersion) || !cityVersion.equals("4")) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.city)));
				List<City> cityList = new ArrayList<City>(12);

				try {
					while ((line_str = reader.readLine()) != null) {
						if (line_str.equals("")) {
							continue;
						}
						City city = new City();
						String[] infos = line_str.split(",");
						city.id = Integer.valueOf(infos[0]);
						city.provice = infos[1];
						city.name = infos[2];
						city.lat = Integer.valueOf(infos[3]);
						city.lng = Integer.valueOf(infos[4]);
						city.cityNameSimple = infos[5];
						city.type = Integer.valueOf(infos[6]);
						cityList.add(city);
					}
					session.saveCityList(cityList);
					session.set("_CITY_LIST_VERSION", "4");

				} catch (IOException e) {
					session.set("_CITY_LIST_VERSION", "0");
					e.printStackTrace();
				}

			} else {
				// publishProgress(100);
			}

			long dTime = System.currentTimeMillis() - time;
			// AppLog.LogD( dTime+ " ---- ");
			if (InfoConfig.FLASH_TIME_LOADINGDELAY > dTime) {
				try {
					Thread.sleep(InfoConfig.FLASH_TIME_LOADINGDELAY - dTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			publishProgress(100);

			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			int progress = values[0];
			if (progress == 100) {

				startPlatform();
			}
		}
	}

	@Override
	protected void regReceiver() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void unRegReceiver() {
		// TODO Auto-generated method stub

	}
}
