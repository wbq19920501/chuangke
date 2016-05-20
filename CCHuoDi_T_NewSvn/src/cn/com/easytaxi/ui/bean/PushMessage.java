package cn.com.easytaxi.ui.bean;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PushMessage implements Serializable {

	/*
	 * _ID Integer ��¼id �������ֶ�,NOT NULL _MSG String �������� _SEND_TYPE String
	 * 0x1:UDP��Ϣͨ�� 0x2:���� _RE_COUNT Integer �ط�����
	 * ��̨��һ���̲߳���ɨ����ֶΣ��������0�����ٴη��͸���Ϣ�������ͺ��ֵ��1 _RE_INTERVAL Integer �ط����
	 * _MSG_TYPE Integer 1=ϵͳ֪ͨ 2=�Զ�����Ϣ֪ͨ _MSG_SUB_TYPE Integer 1=���� 2=�Ƽ� 3=����
	 * 4=���� 5=��ϲ 6=��֤ϵͳ����APP�˶�Ӧ��ͬ����Ϣ��ʾͼ�� _IS_SHOW Integer 0:����ʾ������ֻ��һ�� 1:��ʾ����
	 * _IS_TTS Integer 0:����TTS���� 1:TTS���� _URL String ��ת��ָ��ҳ�棬ע����Ӧ�õ�WEBVIEW
	 * �����ֵΪ�գ���ת���ҵ���Ϣҳ�档 _STATE _CREATE_TIME _CREATE_USER _UPDATE_TIME Date
	 * ���ݸ�ֵ�ͼ��ʱ�����ж���һ���ط�ʱ��
	 */

	@Expose
	@SerializedName("_ID")
	private long id;

	@Expose
	@SerializedName("_MSG")
	private String msg;

	@Expose
	@SerializedName("_MSG_SUB_TYPE")
	private int _msg_sub_type;

	@Expose
	@SerializedName("_IS_SHOW")
	private int _is_show;

	@Expose
	@SerializedName("_IS_TTS")
	private int _is_tts;

	@Expose
	@SerializedName("_URL")
	private String _url;

	@Expose
	@SerializedName("_CREATE_TIME")
	private String _CREATE_TIME;

	@Expose
	@SerializedName("_CREATE_USER")
	private String _CREATE_USER;

	public String get_CREATE_TIME() {
		return _CREATE_TIME;
	}

	public void set_CREATE_TIME(String _CREATE_TIME) {
		this._CREATE_TIME = _CREATE_TIME;
	}

	public String get_CREATE_USER() {
		return _CREATE_USER;
	}

	public void set_CREATE_USER(String _CREATE_USER) {
		this._CREATE_USER = _CREATE_USER;
	}

	public long getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int get_msg_sub_type() {
		return _msg_sub_type;
	}

	public void set_msg_sub_type(int _msg_sub_type) {
		this._msg_sub_type = _msg_sub_type;
	}

	public int get_is_show() {
		return _is_show;
	}

	public void set_is_show(int _is_show) {
		this._is_show = _is_show;
	}

	public int get_is_tts() {
		return _is_tts;
	}

	public void set_is_tts(int _is_tts) {
		this._is_tts = _is_tts;
	}

	public String get_url() {
		return _url;
	}

	public void set_url(String _url) {
		this._url = _url;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}
