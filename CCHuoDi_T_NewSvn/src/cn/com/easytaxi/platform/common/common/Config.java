package cn.com.easytaxi.platform.common.common;

import cn.com.easytaxi.client.common.ConfigUtil;

public class Config {

	public  static String SERVER_IP = ConfigUtil.getString("IP");

//	public static int SERVER_UDP_PORT = ConfigUtil.UDP_PORT_03;//13512;
	public  static int SERVER_UDP_PORT = ConfigUtil.getInt("TCP_PORT_30");//13512;

	public static int SERVER_TCP_PORT = ConfigUtil.getInt("TCP_PORT_30");
/*	public static int SERVER_UDP_PORT = 13589;
	
	public static int SERVER_TCP_PORT = 13589;
*/}
