package cn.com.easytaxi.platform.service;

public class EasyTaxiCmd {
	public static String START_MAINSERVICE = "cn.com.easytaxi.start_mainservice.action";
	
	/*��ʱ�򳵷���**/
	public static String ONE_TAXI_BOOK_MAIN_START = "cn.com.easytaxi.onetaxi.start.action";
	public static String ONE_TAXI_BOOK_MAIN_CMD = "cn.com.easytaxi.onetaxi.maincmd.action";
	public static String ONE_TAXI_BOOK_MAIN_CMD_RESP = "cn.com.easytaxi.onetaxi.maincmd.RESP.action";
	public static String ONE_TAXI_BOOK_MAIN_SUB_CMD_REQ ="one_req";
	public static String ONE_TAXI_BOOK_MAIN_SUB_CMD_RESP ="one_resp";
	/*��ʱ��������**/
	
	//ֻ�з���ʱ�򳵶����ͽ��ܴ򳵵�����
	//����ʧ�ܵķ���
	public static int ONE_TAXI_BOOK_SUB_CMD_SUBMIT_FAILED = 100;
	// 
	public static final int ONE_TAXI_BOOK_SUB_CMD_SUBMIT_REQ = 101;
	public static final int ONE_TAXI_BOOK_SUB_CMD_SUBMIT_RESP = 201;	
	public static final int ONE_TAXI_BOOK_SUB_CMD_SUBMIT_RESP_FAILED = 202;	
	public static final int ONE_TAXI_BOOK_SUB_CMD_SUBMIT_RESP_OK = 203;	
	public static final int ONE_TAXI_BOOK_SUB_CMD_SUBMIT_RESP_WAITTING = 204;	
	public static final int ONE_TAXI_BOOK_SUB_CMD_SUBMIT_RESP_TIMEOUT = 205;
	public static final int ONE_TAXI_BOOK_SUB_CMD_SUBMIT_RESP_JIEDAN_OK = 206;
	
	public static final int ONE_TAXI_BOOK_SUB_CMD_UDP_RESP = 300;	
	public static final int ONE_TAXI_BOOK_SUB_CMD_UDP_RESP_DRIVER_JIEDAN = 301;	//002
	public static final int ONE_TAXI_BOOK_SUB_CMD_UDP_RESP_DRIVER_CANCEL = 302;	//006
	public static final int ONE_TAXI_BOOK_SUB_CMD_UDP_RESP_DIAODU_INFO = 303;	//003
	
	//�ȴ����������ܵĹ���
	public static final int ONE_TAXI_BOOK_SUB_CMD_WAITTING = 102;

	
}
