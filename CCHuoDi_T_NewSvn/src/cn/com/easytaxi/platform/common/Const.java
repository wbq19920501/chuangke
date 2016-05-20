package cn.com.easytaxi.platform.common;

import cn.com.easytaxi.common.Config;

public class Const {

	private static final String WEB_ACTION = "index.php/Platform";
	private static final String WEB_FILE = "download";
	private static final String WEB_ICON = "icon";

	public static final String REQUEST_URL = Config.WEB_SERVER + "/" + Config.WEB_APP + "/" + WEB_ACTION + "/";
	public static final String REQUEST_ICON = Config.WEB_SERVER + "/" + Config.WEB_APP + "/" + WEB_ICON + "/";
	public static final String REQUEST_DOWNLOAD = Config.WEB_SERVER + "/" + Config.WEB_APP + "/" + WEB_FILE + "/";
}
