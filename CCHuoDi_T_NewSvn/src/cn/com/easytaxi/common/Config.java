package cn.com.easytaxi.common;

import java.util.HashMap;

import android.os.Environment;

public class Config {

	public static final String SERVER_IP = "www.easytaxi.com.cn";
	// public static final String SERVER_IP = "10.143.132.61";
	// public static final String CARLIST_SERVER_IP = SERVER_IP;
	// public static final Integer CALL_CAR_SERVER_PORT = 8485;
	public static final Integer SERVER_PORT = 8484;
	// public static final Integer SERVER_UDP_PORT = 8787;
	// public static final String WEB_SERVER = "http://180.168.2.58";
	public static final String WEB_SERVER = "http://www.easytaxi.com.cn";
	public static final String WEB_APP = "taxi";

	public static final String SHARE_CONTENT = "我正在使用易达打车，打车真方便，从开始呼叫到上车短短几分钟就搞定了，真是爽啊！你也快来用吧，官方下载地址：http://www.easytaxi.com.cn";
	public static final String SHARE_CONTENT_WEIBOO = "我正在使用@创客货的-智能手机应用，在手机屏幕上轻轻一点就可以叫到出租车，从此专车出行，更少等待、更高效率、更好体验，真的很好用，推荐你也用用！官方下载地址：http://www.easytaxi.com.cn";

	public static final String storePath = Environment.getExternalStorageDirectory() + "/cn.com.easytaxi/";
	public static final String memoryPath = "/data/data/cn.com.easytaxi/files/";

	public static final String BAIDU_KEY = "F84D6BA4280A527CDE60DECEA3649ED5ECEF02C3";

	public static String EXTEND_ID = "0";

	public static final String FIXED_SERVER = "www.easytaxi.com.cn";
//	public static final String FIXED_SERVER = "42.121.124.72";
	public static final Integer FIXED_SERVER_PORT = 8484;

	 
 
	public static final String APP_ID = "wx8a6d3c643eb2ce24";
	//public static final String APP_ID = "wxee4c7eae40a50c59";
	public static final String NEW_TCP_ACTION = "proxyAction";
	public static final String NEW_TCP_ACTION_QUERY = "query";
	 

 
}
