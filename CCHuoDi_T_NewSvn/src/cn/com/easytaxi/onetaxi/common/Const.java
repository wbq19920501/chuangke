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
	public static short MSG_USER_UP = (short) (MSG_TYPE_USER_SERVER | 0x07);// �˿����ϳ�
	public static short MSG_TAXI_BREACH = (short) (MSG_TYPE_USER_SERVER | 0x0a);// ˾��ΥԼ
	public static short MSG_GET_ROUND_CARS = (short) (MSG_TYPE_USER_SERVER | 0x3C0);// ��ȡ�ܱ߳��⳵
	public static short MSG_LIST_CALL_CARS = (short) (MSG_TYPE_USER_SERVER | 0x0b);// �б��
	public static short MSG_LIST_CALL_CARS_RESULT = (short) (MSG_TYPE_USER_SERVER | 0x0c);// ��ȡ�б�򳵽��
	
	public static short MSG_GET_SERVER = (short) (MSG_TYPE_USER_SERVER | 0x3E1);// ��ȡ�����

	public static short NOTIFY_MSG_UNKOWN = 0;
	public static short NOTIFY_MSG_CALL_SCHEDULE = 1; // ���ȳ��⳵
	public static short NOTIFY_MSG_CALL_ANSWER = 2; // ���⳵Ӧ��
	public static short NOTIFY_MSG_CALL_REPLY = 3; // ȷ�ϳ��⳵
	public static short NOTIFY_MSG_LOCATION = 4; // λ����Ϣ
	public static short NOTIFY_MSG_GETON = 5; // �˿��ϳ�
	public static short NOTIFY_MSG_CALL_CANCEL = 6; // �˿�ȡ��������
	public static short NOTIFY_MSG_GETON_REPLY = 7; // �˿�ȷ���ϳ�
	public static short NOTIFY_MSG_CALL_TAXI_CANCEL = 8; // ���⳵ȡ��������
	public static short NOTIFY_MSG_CALL_ADVERTISE = 9; // �˿ʹ�ҵ�񲥱�
	public static short NOTIFY_MSG_CALL_LIST_CAR = 10; // �б��

}
