package cn.com.easytaxi.onetaxi.common;

import cn.com.easytaxi.common.Config;

public class Const {

	private static final String WEB_ACTION = "index.php/Passenger";

	public static final String REQUEST_URL = Config.WEB_SERVER + "/" + Config.WEB_APP + "/" + WEB_ACTION + "/";

	public static short MSG_TYPE_USER_SERVER = 0x400;
	public static short MSG_USER_CALL_TAXI = (short) (MSG_TYPE_USER_SERVER | 0x01);
	public static short MSG_USER_GET_TAXI = (short) (MSG_TYPE_USER_SERVER | 0x02);
	public static short MSG_USER_REPLY_TAXI = (short) (MSG_TYPE_USER_SERVER | 0x03);
	public static short MSG_USER_GET_TAXI_LOCATION = (short) (MSG_TYPE_USER_SERVER | 0x04);
	public static short MSG_USER_LOCATION = (short) (MSG_TYPE_USER_SERVER | 0x05);
	public static short MSG_USER_CANCEL_CALL = (short) (MSG_TYPE_USER_SERVER | 0x06);
	public static short MSG_USER_UP = (short) (MSG_TYPE_USER_SERVER | 0x07);// 乘客已上车
	public static short MSG_TAXI_BREACH = (short) (MSG_TYPE_USER_SERVER | 0x0a);// 司机违约
	public static short MSG_GET_ROUND_CARS = (short) (MSG_TYPE_USER_SERVER | 0x3C0);// 获取周边出租车
	public static short MSG_LIST_CALL_CARS = (short) (MSG_TYPE_USER_SERVER | 0x0b);// 列表打车
	public static short MSG_LIST_CALL_CARS_RESULT = (short) (MSG_TYPE_USER_SERVER | 0x0c);// 获取列表打车结果
	
	public static short MSG_GET_SERVER = (short) (MSG_TYPE_USER_SERVER | 0x3E1);// 获取服务端

	public static short NOTIFY_MSG_UNKOWN = 0;
	public static short NOTIFY_MSG_CALL_SCHEDULE = 1; // 调度出租车
	public static short NOTIFY_MSG_CALL_ANSWER = 2; // 出租车应答
	public static short NOTIFY_MSG_CALL_REPLY = 3; // 确认出租车
	public static short NOTIFY_MSG_LOCATION = 4; // 位置信息
	public static short NOTIFY_MSG_GETON = 5; // 乘客上车
	public static short NOTIFY_MSG_CALL_CANCEL = 6; // 乘客取消打车请求
	public static short NOTIFY_MSG_GETON_REPLY = 7; // 乘客确认上车
	public static short NOTIFY_MSG_CALL_TAXI_CANCEL = 8; // 出租车取消打车请求
	public static short NOTIFY_MSG_CALL_ADVERTISE = 9; // 乘客打车业务播报
	public static short NOTIFY_MSG_CALL_LIST_CAR = 10; // 列表打车

}
