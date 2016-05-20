package cn.com.easytaxi.platform.service;


import java.io.Serializable;

import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.platform.common.common.Const;


public class SystemService implements Serializable {
	
	/**
	 * λ�ù㲥<br/>
	 */
	public final static String BROADCAST_LOCATION = "cn.com.easytaxi.magi.system.location";
	
	/**
	 * ������������Ϣ�㲥
	 */
	public final static String BROADCAST_MESSAGE = "cn.com.easytaxi.magi.system.message";
	

	/**
	 * ����״̬�ı�ʱ�򣬽��㲥����Ϣ<br/>
	 * state=Const.OFF:����ģʽ Const.ON:����ģʽ
	 */
	public final static String BROADCAST_SOUND_STATE = "cn.com.easytaxi.magi.system.sound.state";

	/**
	 * ��������ģʽ
	 * 
	 * @param soundMode
	 *            ����ģʽ:Const.OFF:����ģʽ Const.ON:����ģʽ
	 */
	public final static void setSoundMode(int soundMode) {

	}

	/**
	 * �������ģʽ
	 * 
	 * @param soundMode
	 *            ����ģʽ:Const.OFF:����ģʽ Const.ON:����ģʽ
	 */
	public final static int getSoundMode() {
		return Const.ON;
	}
	
	/**
	 * ��Ļ״̬�ı�ʱ�򣬽��㲥����Ϣ<br/>
	 * state=Const.OFF:androidϵͳ��ʱ���� Const.ON:����ģʽ
	 */
	public final static String BROADCAST_SCREEN_STATE = "cn.com.easytaxi.magi.system.screen.state";
	
	
	/**
	 * ������Ļģʽ
	 * 
	 * @param soundMode
	 *            Const.OFF:androidϵͳ��ʱ���� Const.ON:����ģʽ
	 */
	public final static void setScreenMode(int soundMode) {

	}

	/**
	 * �����Ļģʽ
	 * 
	 * @param soundMode
	 *            Const.OFF:androidϵͳ��ʱ���� Const.ON:����ģʽ
	 */
	public final static int getScreenMode() {
		return Const.ON;
	}
	
	
	
	/**
	 * ʵʱ��ͨ�Ƿ���<br/>
	 * state=Const.OFF:androidϵͳ��ʱ���� Const.ON:����ģʽ
	 */
	public final static String BROADCAST_TRAFFIC_STATE = "cn.com.easytaxi.magi.system.traffic.state";
	
	/**
	 * ����ʵʱ��ͨģʽ
	 * 
	 * @param soundMode
	 *            Const.OFF:�ر� Const.ON:����
	 */
	public final static void setTrafficMode(int soundMode) {

	}

	/**
	 * ���ʵʱ��ͨģʽ
	 * 
	 * @param soundMode
	 *            Const.OFF:�ر� Const.ON:����
	 */
	public final static int getTrafficMode() {
		return Const.ON;
	}
	
	
	/**
	 * �ı�ת����
	 * @param text �ı�
	 */
	public static void playTTS(String text){
		AppLog.LogD(text);
	}
	
	
	
}
