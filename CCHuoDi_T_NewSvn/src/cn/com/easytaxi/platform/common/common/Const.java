package cn.com.easytaxi.platform.common.common;

public final class Const {

	/**
	 * 关
	 */
	public final static int OFF = 0;

	/**
	 * 开
	 */
	public final static int ON = 1;

	/**
	 * 不是
	 */
	public final static int NO = 0;

	/**
	 * 保密
	 */
	public final static int SECRECY = -1;

	/**
	 * 是
	 */
	public final static int YES = 1;

	/**
	 * 女
	 */
	public final static int WOMEN = 0;

	/**
	 * 未知
	 */
	public final static int UNKNOW = -1;

	/**
	 * 男
	 */
	public final static int MAN = 1;

	/**
	 * 位置上传消息ID
	 */
	public final static int UDP_LOCATION_UP = 0x000001;

	/**
	 * 服务器UDP消息ID
	 */
	public final static int UDP_SERVER_MESSAGE = 0x000002;

	/**
	 * 推送业务订单
	 */
	public final static int UDP_BUSINESS_BOOK = 0x00000003;

	/**
	 * TCP action
	 */
	public final static int TCP_ACTION = 0xF00001;

	
	
	/**
	 * 告诉乘客有出租车回应
	 */
	public final static int UDP_BOOK_TAXI_REPLY = 0xFF0002;
	
	
	/**
	 * 告诉乘客有出租车回应
	 */
	public final static int UDP_BOOK_TAXI_SCHEDULE = 0xFF0003;
	
	/**
	 * 订单状态变化通知
	 */
	public final static int UDP_BOOK_STATE_CHANGED = 0xFF0007;
	/**
	 * 回应服务器udp
	 */
	public final static int UDP_NOTIFY_ECHO = 0xFF0008;
}
