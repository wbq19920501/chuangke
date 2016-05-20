package cn.com.easytaxi.ui.bean;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PushMessage implements Serializable {

	/*
	 * _ID Integer 记录id 自增长字段,NOT NULL _MSG String 发送内容 _SEND_TYPE String
	 * 0x1:UDP消息通道 0x2:短信 _RE_COUNT Integer 重发次数
	 * 后台有一个线程不断扫描该字段，如果大于0，将再次发送该消息，并发送后该值减1 _RE_INTERVAL Integer 重发间隔
	 * _MSG_TYPE Integer 1=系统通知 2=自定义消息通知 _MSG_SUB_TYPE Integer 1=公告 2=推荐 3=服务
	 * 4=新闻 5=恭喜 6=验证系统，可APP端对应不同的消息提示图标 _IS_SHOW Integer 0:不显示出来，只嘟嘟一声 1:显示出来
	 * _IS_TTS Integer 0:不用TTS播报 1:TTS播报 _URL String 跳转到指定页面，注意是应用的WEBVIEW
	 * 如果该值为空，跳转到我的消息页面。 _STATE _CREATE_TIME _CREATE_USER _UPDATE_TIME Date
	 * 根据该值和间隔时间来判断下一次重发时间
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
