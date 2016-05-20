package cn.com.easytaxi.platform.service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Intent;
import cn.com.easytaxi.platform.common.common.Const;
import cn.com.easytaxi.platform.common.common.SendMsgBean;
import cn.com.easytaxi.platform.common.common.SocketUtil;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class LocationService implements BDLocationListener {

	private static final String tag = "LocationService";

	private MainService mainService;

	// 前一次系统时间
	private long preSysTime = 0;

	LocationClient locationClient;

	SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public LocationService(MainService mainService) {
		this.mainService = mainService;
		locationClient = new LocationClient(mainService);
		locationClient.registerLocationListener(this); // 注册监听函数
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setScanSpan(5000);
		option.disableCache(false);
		locationClient.setLocOption(option);
	}

	public void start() {
		locationClient.start();
		locationClient.requestLocation();
	}

	public void onReceiveLocation(BDLocation location) {
		try {

		 
			int lat = (int) (location.getLatitude() * 1E6);
			int lng = (int) (location.getLongitude() * 1E6);
			//long sysTime = Calendar.getInstance().getTimeInMillis();
			long sysTime = System.currentTimeMillis();

			// 上传经纬度信息
			if (sysTime - preSysTime > 10000) { // 上传经纬度为0主要为了保持UDP通讯
				preSysTime = sysTime;
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				bout.write(SocketUtil.toHH(lng));
				bout.write(SocketUtil.toHH(lat));
				
				MainService.udpChannelService.sendMessage(new SendMsgBean(Const.UDP_LOCATION_UP, bout.toByteArray()));
			}

			// 广播经纬度
			if (lat <= 0 || lng <= 0)
				return;

			Intent intent = new Intent(SystemService.BROADCAST_LOCATION);

//			intent.putExtra("time", sysTime);
//			intent.putExtra("type", location.getLocType());
			intent.putExtra("latitude", lat);
			intent.putExtra("longitude", lng);
			intent.putExtra("radius", location.getRadius());
			intent.putExtra("derect", location.getDerect());
//			intent.putExtra("speed", location.getSpeed());
	 
			
			
//			intent.putExtra("satellite", location.getSatelliteNumber());
			
	/*		AppLog.LogD(tag, "time : " + sysTime + " , type : " + location.getLocType());
			AppLog.LogD(tag, "lat : " + lat + " , lng : " + lng + " , radius : " +  location.getRadius());
			AppLog.LogD(tag, "radius : " +  location.getRadius() + " , speed : "+ location.getSpeed() + ", satellite : " + location.getSatelliteNumber());
		*/	
			mainService.sendBroadcast(intent);

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void onReceivePoi(BDLocation location) {

	}

	public void stop() {
		if (locationClient != null)
			locationClient.stop();
	}

}
