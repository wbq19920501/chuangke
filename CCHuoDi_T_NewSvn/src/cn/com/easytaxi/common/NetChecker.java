package cn.com.easytaxi.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetChecker {

	private Context context;

	private static NetChecker instance;
	public static NetChecker getInstance(Context context) {
		if(instance == null){
			instance = new NetChecker(context);
		}
		
		return instance;
	}
	
	public NetChecker(Context context) {
		this.context = context;
	}

	public boolean checkNetwork() {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo == null) {
			return false;
		} else {
			if (activeNetInfo.isAvailable()) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * 检测有网络连接，但是不通的情况
	 * 
	 * @return
	 * 
	 */
	public boolean isAvailableNetwork() {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo == null) {
			return false;
		} else {
			if (activeNetInfo.isConnected()) {
				if ((activeNetInfo.isAvailable())) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}
	
	
	
	/**
	 * 检测wifi是否可用
	 * 
	 * @return
	 * 
	 */
	public boolean isWifiAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (activeNetInfo == null) {
			return false;
		} else {
			if (activeNetInfo.isConnected()) {
				if (activeNetInfo.isAvailable()) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}
}
