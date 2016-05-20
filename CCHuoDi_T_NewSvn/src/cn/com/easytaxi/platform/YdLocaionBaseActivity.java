package cn.com.easytaxi.platform;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import android.media.AudioManager;
import android.os.Bundle;
import android.text.TextUtils;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.NewNetworkRequest;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.common.User;
import cn.com.easytaxi.platform.common.CityTool;
import cn.com.easytaxi.receiver.LocationBroadcastReceiver;

public abstract class YdLocaionBaseActivity extends YdBaseActivity {

	protected CityTool cityTool;

	// protected int currentlat;
	//
	// protected int currentLng;
	//
	// protected float currentRadius;
	//
	// protected float currentDerect;

	protected int cacheStartLat;

	protected int cacheStartLng;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

//		cacheLat = ETApp.getInstance().getCacheInt("_P_LAT");
//		cacheLng = ETApp.getInstance().getCacheInt("_P_LNG");
		cityTool = CityTool.getInstance();
	}

	/**
	 * ��ȡ��ǰλ�� ���ڵĳ���
	 * 
	 * @param callback
	 */
	protected void requestCity(final Callback<String> callback) {

		if (getCurrentlat() > 0 && getCurrentLng()>0) {
			cityTool.sendGetCityMsg(getCurrentlat(), getCurrentLng(), callback);
		}else{
			new Thread(){
				public void run() {
					while(true){
						if(getCurrentlat() >0 &&  getCurrentLng()>0){
							cityTool.sendGetCityMsg(getCurrentlat(), getCurrentLng(), callback);
							return;
						}
					}
				};
				
			}.start();
			
		}

	}

	/**
	 * ���õ�ǰ����Ϊý������
	 */
	protected void initSound() {
		setVolumeControlStream(AudioManager.STREAM_MUSIC); // ������������Ϊý��
	}

	
	/**
	 * ��ȥ��ǰλ�õĵ�ַ
	 * 
	 * @param callback
	 */
	protected void requestCurrentLoacionAddress(final Callback<String> callback) {
		
		if ((cacheStartLat = getCurrentlat()) > 0 && (cacheStartLng=getCurrentLng())>0) {
			
			NewNetworkRequest.getAddressByLocation(cacheStartLat, cacheStartLng, callback);
		}else{

			new Thread(){
				public void run() {
					while(true){
						if((cacheStartLat = getCurrentlat() )>0 && ( cacheStartLng = getCurrentLng())>0){
							NewNetworkRequest.getAddressByLocation(cacheStartLat, cacheStartLng, callback);
							return;
						}
					}
				};
				
			}.start();
			
		
		}
		
	 
	}

	public int getCacheStartLat() {
		return cacheStartLat;
	}

	public int getCacheStartLng() {
		return cacheStartLng;
	}

	/**
	 * ��ѯ��Χ�ĳ��⳵
	 * @param callback
	 */
	protected void requestNearbyTaxi(Callback<Object> callback) {
		NewNetworkRequest.getNearbyTaxis(getUserPhoneNum(), getCurrentlat(), getCurrentLng(), callback, getCityId());
	}
	
	/**
	 * ��ѯ�ӵ��ĳ��⳵��λ��
	 * @param taxiId
	 * @param taxiLocationCallback
	 */
	protected void requestTaxiLocation(long taxiId, Callback<JSONObject> taxiLocationCallback) {
		 
		NewNetworkRequest.getTaxiLocation(taxiId, taxiLocationCallback);
	}
	

	/**
	 * ��ȥ��ǰ�û��ĵ绰
	 * 
	 * @return ����ֵ�п���ΪNULL
	 */
	protected String getUserPhoneNum() {
		String mobile = ETApp.getInstance().getCurrentUser().getPhoneNumber(User._MOBILE);
		AppLog.LogD("mobile");
		
		return mobile;
	}
	
	protected boolean isLogin(){
		if (ETApp.getInstance().isLogin()) {
			return true;
		}else{
			return false;
		}
	}

	/**
	 * ��ȡ���ݿ�����һ�λ�ȡ���ĵ� cityname
	 * 
	 * @return
	 */
	protected String findCacheCity() {
		return session.get("_CITY_NAME");
	}

	/**
	 * ���������ִ洢�����ݿ�
	 * 
	 * @param cityName
	 */
	protected void saveCacheCity(String cityName) {

		session.set("_CITY_NAME", cityName);
		String simpleStr = cityName.substring(0, 2);
		String cityId = session.getCityIdBySimple(simpleStr);

		if(TextUtils.isEmpty(cityId)){
			cityId = session.getCityIdByName(simpleStr);
		}else{
			
		}
		AppLog.LogD("save city id " + cityId);
		
		session.set("_CITY_ID", cityId);
	}

	@Override
	protected void onDestroy() {
		saveCacheLoaction();
		super.onDestroy();
	}

	private void saveCacheLoaction() {
		ETApp.getInstance().saveCahceInt("_P_LAT", getCurrentlat());
		ETApp.getInstance().saveCahceInt("_P_LNG", getCurrentLng());
	}

	public int getCurrentlat() {
		return LocationBroadcastReceiver.getCurrentLat();
	}

	public int getCurrentLng() {
		return LocationBroadcastReceiver.getCurrentLng();
	}

	public int getCurrentRadius() {
		return LocationBroadcastReceiver.getCurrentLng();
	}

	public int getCurrentDerect() {
		return LocationBroadcastReceiver.getCurrentLng();
	}
	
	protected void doBack() {
		this.finish();
	}

}
