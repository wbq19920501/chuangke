package cn.com.easytaxi.common;

import cn.com.easytaxi.common.Config;

public class Const {

	private static final String WEB_ACTION = "index.php/Platform";
	private static final String WEB_FILE = "download";
	private static final String WEB_ICON = "icon";

	public static final String REQUEST_URL = Config.WEB_SERVER + "/" + Config.WEB_APP + "/" + WEB_ACTION + "/";
	public static final String REQUEST_ICON = Config.WEB_SERVER + "/" + Config.WEB_APP + "/" + WEB_ICON + "/";
	public static final String REQUEST_DOWNLOAD = Config.WEB_SERVER + "/" + Config.WEB_APP + "/" + WEB_FILE + "/";

	// �˿�ͨѶMSG_ID������
	public static short MSG_TYPE_USER_SERVER = 0x400;
	public static short MSG_USER_UDP_LOCATION = (short) (MSG_TYPE_USER_SERVER | 0x05);// �˿�λ�ñ���
	public static short MSG_USER_TCP_LOCATION = (short) (MSG_TYPE_USER_SERVER | 0x3E0);// �˿�λ�ñ���

	public static int APP_ID_ONETAXI = 0x3; // ��ʱ��

	
	
	
	
// �°漰ʱ������1��   �ɰ漰ʱ������2��        IOS1.5ԤԼ������3��      �ɰ�ԤԼ������4��           Android�°�ԤԼ���� ��5��           ���ۣ�6��          ���ͻ���7��
	/**
	 *  �°漴ʱ������1��
	 */
	public static final int JISHI_BOOK_TYPE = 1;
	/**
	 *   Android�°�ԤԼ���� ��5��
	 */
	public static final int BOOK_BOOK_TYPE = 5;
	
	/**
	 *   Android�°���ۣ�6��
	 */
	public static final int DRIVINT_ORDER_BOOK_TYPE = 6;
	
	/**
	 *  ���ͻ���7��
	 */
	public static final int AIRPORT_BOOK_TYPE = 7;
	
	
	/**
	 * ��ʱ�򳵵ļ۸��б�
	 */
	public static final String JISHI_PRICELIST ="jishi_pricelist";
	
	/**
	 * ԤԼ�򳵵ļ۸��б�
	 */
	public static final String BOOK_PRICELIST ="book_pricelist";
	
	/**
	 * �ӻ��ͻ��ļ۸��б�
	 */
	public static final String AIRPORT_PRICELIST ="airport_pricelist";
	
	/**
	 * ���ݵļ۸��б�
	 */
	public static final String DRIVING_ORDER_PRICELIST ="driving_order_pricelist";
	
	/**
	 * ��־���أ�true��ʱ�����¼���ϴ�����   
	 */
	public static final boolean isActionLogCouldUpload = true;
	
	/**
	 * ����Ԥ���ķ�������ݿ�
	 */
	public static final String WEATHER_DB_NAME = "citys.db";
	
	/**
	 * ��˾�ڲ��ٶ�Web��ʽ��ȡPoi��key
	 */
//    public static final String baiduWebPoiKey = "F1f3c50228554ec93f1b734dc3761a5d";
    public static final String[] baiduWebPoiKeys = {"F1f3c50228554ec93f1b734dc3761a5d","31ced7bffb840720912f4f18f93abbed","87af5f187bae9f9e233bc0c82eddabea","06ce24471af76d95c2e88797b2f39b97","664513c92e748a7d4819dadc9f9ad365","E5f7ffa65b229dff95f374352ac2bd40","031909457dca378177cef1710135c867","1fd4d9bd1b6f8b52a262f003983432b9","75fa4d1c19789e8d41f891d0502b439e","76d38783c535b236bdbfebb9351dc666","257cb82ee78456768acd658a0e3dda1d","E491da0140d78b315968d8555b969ed3","F2f07874ea42c6ed86d84830513ae94d","2fb99e87ac15528795e7549a193110ac" };

    /**
     * ����״̬�ı�㲥
     */
    public static final String BOOK_STATE_CHANGED_LIST = "cn.com.easytaxi.book.book_state_changed_list";
}
