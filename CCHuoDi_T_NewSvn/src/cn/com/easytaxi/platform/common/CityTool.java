package cn.com.easytaxi.platform.common;

import cn.com.easytaxi.NewNetworkRequest;
import cn.com.easytaxi.common.Callback;

public class CityTool {
	private static CityTool instance;
	public static CityTool getInstance() {
		if (instance == null) {
			instance = new CityTool();
		}
		
		return instance;
	}

	private CityTool() {
 	 
	}
	
	public void sendGetCityMsg(int lat, int lng , Callback<String> callback){
		 
		NewNetworkRequest.getCityByLocation(lat, lng , callback);
		 
	}

 

}
