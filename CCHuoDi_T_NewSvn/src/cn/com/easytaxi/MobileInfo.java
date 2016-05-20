package cn.com.easytaxi;

import cn.com.easytaxi.common.SDInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.LocationManager;
import android.os.Environment;
import android.telephony.TelephonyManager;

public class MobileInfo {

	private static MobileInfo instance = null;
	private Context context;

	private MobileInfo(Context context) {
		this.context = context;
	}

	public static MobileInfo getInstance(Context context) {
		if (instance == null) {
			instance = new MobileInfo(context);
		}
		return instance;
	}

	public int getVerisonCode() {

		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		int version = 0;
		if (packInfo != null) {
			version = packInfo.versionCode;
		}
		return version;
	}
	public String getVerisonName() {
		
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		String version = "";
		if (packInfo != null) {
			version = packInfo.versionName;
		}
		return version;
	}

	public String getImei() {
		String imei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		return imei;
	}

	public String getSimCardTellNum() {
		String num = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
		return num;
	}

	public String getSDCardPath() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {

			if (SDInfo.checkSdcard()) {
				return Environment.getExternalStorageDirectory().toString()+"/cn.com.easytaxi/";
			} else {
				return ETApp.getInstance().getFilesDir().toString();
			}

		} else {
			return ETApp.getInstance().getFilesDir().toString();
		}

	}

	public boolean isGpgOpened() {
		LocationManager alm = (LocationManager) ETApp.getInstance().getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			return true;
		} else {
			return false;
		}
	}

}
