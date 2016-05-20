package cn.com.easytaxi.common;

import cn.com.easytaxi.common.Config;

public class Const {

	private static final String WEB_ACTION = "index.php/Platform";
	private static final String WEB_FILE = "download";
	private static final String WEB_ICON = "icon";

	public static final String REQUEST_URL = Config.WEB_SERVER + "/" + Config.WEB_APP + "/" + WEB_ACTION + "/";
	public static final String REQUEST_ICON = Config.WEB_SERVER + "/" + Config.WEB_APP + "/" + WEB_ICON + "/";
	public static final String REQUEST_DOWNLOAD = Config.WEB_SERVER + "/" + Config.WEB_APP + "/" + WEB_FILE + "/";

	// 乘客通讯MSG_ID的掩码
	public static short MSG_TYPE_USER_SERVER = 0x400;
	public static short MSG_USER_UDP_LOCATION = (short) (MSG_TYPE_USER_SERVER | 0x05);// 乘客位置报告
	public static short MSG_USER_TCP_LOCATION = (short) (MSG_TYPE_USER_SERVER | 0x3E0);// 乘客位置报告

	public static int APP_ID_ONETAXI = 0x3; // 即时打车

	
	
	
	
// 新版及时订单（1）   旧版及时订单（2）        IOS1.5预约订单（3）      旧版预约订单（4）           Android新版预约订单 （5）           代价（6）          接送机（7）
	/**
	 *  新版即时订单（1）
	 */
	public static final int JISHI_BOOK_TYPE = 1;
	/**
	 *   Android新版预约订单 （5）
	 */
	public static final int BOOK_BOOK_TYPE = 5;
	
	/**
	 *   Android新版代价（6）
	 */
	public static final int DRIVINT_ORDER_BOOK_TYPE = 6;
	
	/**
	 *  接送机（7）
	 */
	public static final int AIRPORT_BOOK_TYPE = 7;
	
	
	/**
	 * 即时打车的价格列表
	 */
	public static final String JISHI_PRICELIST ="jishi_pricelist";
	
	/**
	 * 预约打车的价格列表
	 */
	public static final String BOOK_PRICELIST ="book_pricelist";
	
	/**
	 * 接机送机的价格列表
	 */
	public static final String AIRPORT_PRICELIST ="airport_pricelist";
	
	/**
	 * 代驾的价格列表
	 */
	public static final String DRIVING_ORDER_PRICELIST ="driving_order_pricelist";
	
	/**
	 * 日志开关：true的时候则记录和上传日子   
	 */
	public static final boolean isActionLogCouldUpload = true;
	
	/**
	 * 天气预报的服务的数据库
	 */
	public static final String WEATHER_DB_NAME = "citys.db";
	
	/**
	 * 公司内部百度Web方式获取Poi的key
	 */
//    public static final String baiduWebPoiKey = "F1f3c50228554ec93f1b734dc3761a5d";
    public static final String[] baiduWebPoiKeys = {"F1f3c50228554ec93f1b734dc3761a5d","31ced7bffb840720912f4f18f93abbed","87af5f187bae9f9e233bc0c82eddabea","06ce24471af76d95c2e88797b2f39b97","664513c92e748a7d4819dadc9f9ad365","E5f7ffa65b229dff95f374352ac2bd40","031909457dca378177cef1710135c867","1fd4d9bd1b6f8b52a262f003983432b9","75fa4d1c19789e8d41f891d0502b439e","76d38783c535b236bdbfebb9351dc666","257cb82ee78456768acd658a0e3dda1d","E491da0140d78b315968d8555b969ed3","F2f07874ea42c6ed86d84830513ae94d","2fb99e87ac15528795e7549a193110ac" };

    /**
     * 订单状态改变广播
     */
    public static final String BOOK_STATE_CHANGED_LIST = "cn.com.easytaxi.book.book_state_changed_list";
}
