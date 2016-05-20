package cn.com.easytaxi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cn.com.easytaxi.AppLog;

public class LocationBroadcastReceiver extends BroadcastReceiver {

	private static int currentLat;
	private static int currentLng;
	private static float radius;
	private static float derect;
	
 
	@Override
	public void onReceive(Context context, Intent intent) {
		currentLat = intent.getIntExtra("latitude", 0);
		currentLng = intent.getIntExtra("longitude", 0);
		radius = intent.getFloatExtra("radius", 0.0f);
	 
		derect = intent.getFloatExtra("derect", 0.0f);
//		derect = 0.0f;

//		AppLog.LogD( " currentLng = "+ currentLng + " , " +  " currentlat = "+ currentLat);
	}
	
	public static int getCurrentLat(){
		return currentLat;
	}
	
	public static int getCurrentLng(){
		return currentLng;
	}
	public static float getRadius(){
		return radius;
	}
	public static float getDerect(){
		return derect;
	}

	
	
}
