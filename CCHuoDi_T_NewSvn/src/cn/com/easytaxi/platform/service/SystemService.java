package cn.com.easytaxi.platform.service;


import java.io.Serializable;

import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.platform.common.common.Const;


public class SystemService implements Serializable {
	
	/**
	 * 位置广播<br/>
	 */
	public final static String BROADCAST_LOCATION = "cn.com.easytaxi.magi.system.location";
	
	/**
	 * 服务器推送消息广播
	 */
	public final static String BROADCAST_MESSAGE = "cn.com.easytaxi.magi.system.message";
	

	/**
	 * 声音状态改变时候，将广播改消息<br/>
	 * state=Const.OFF:静音模式 Const.ON:放音模式
	 */
	public final static String BROADCAST_SOUND_STATE = "cn.com.easytaxi.magi.system.sound.state";

	/**
	 * 设置声音模式
	 * 
	 * @param soundMode
	 *            声音模式:Const.OFF:静音模式 Const.ON:放音模式
	 */
	public final static void setSoundMode(int soundMode) {

	}

	/**
	 * 获得声音模式
	 * 
	 * @param soundMode
	 *            声音模式:Const.OFF:静音模式 Const.ON:放音模式
	 */
	public final static int getSoundMode() {
		return Const.ON;
	}
	
	/**
	 * 屏幕状态改变时候，将广播改消息<br/>
	 * state=Const.OFF:android系统超时黑屏 Const.ON:常量模式
	 */
	public final static String BROADCAST_SCREEN_STATE = "cn.com.easytaxi.magi.system.screen.state";
	
	
	/**
	 * 设置屏幕模式
	 * 
	 * @param soundMode
	 *            Const.OFF:android系统超时黑屏 Const.ON:常亮模式
	 */
	public final static void setScreenMode(int soundMode) {

	}

	/**
	 * 获得屏幕模式
	 * 
	 * @param soundMode
	 *            Const.OFF:android系统超时黑屏 Const.ON:常亮模式
	 */
	public final static int getScreenMode() {
		return Const.ON;
	}
	
	
	
	/**
	 * 实时交通是否开启<br/>
	 * state=Const.OFF:android系统超时黑屏 Const.ON:常量模式
	 */
	public final static String BROADCAST_TRAFFIC_STATE = "cn.com.easytaxi.magi.system.traffic.state";
	
	/**
	 * 设置实时交通模式
	 * 
	 * @param soundMode
	 *            Const.OFF:关闭 Const.ON:开启
	 */
	public final static void setTrafficMode(int soundMode) {

	}

	/**
	 * 获得实时交通模式
	 * 
	 * @param soundMode
	 *            Const.OFF:关闭 Const.ON:开启
	 */
	public final static int getTrafficMode() {
		return Const.ON;
	}
	
	
	/**
	 * 文本转语音
	 * @param text 文本
	 */
	public static void playTTS(String text){
		AppLog.LogD(text);
	}
	
	
	
}
