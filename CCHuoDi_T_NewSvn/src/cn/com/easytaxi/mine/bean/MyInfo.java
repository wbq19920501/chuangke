package cn.com.easytaxi.mine.bean;

import java.io.Serializable;

/**
 * '�ҵ�':��Ϣ����  ��Ϣ����  ����  ���  ΥԼ�� ����  
 * @author Administrator
 *
 */
public class MyInfo implements Serializable{
	/**
	 * ��Ϣ����
	 */
	private int _MSG_NUMBER;
	/**
	 * ����
	 */
	private int _SCORE;
	/**
	 * ���
	 */
	private int _RMB;
	/**
	 * ΥԼ��
	 */
	private int _WEIYUE_NUMBER;
	 /**	
     * ״̬
     */
	private int _STATE;
	/**
	 * ������
	 */
	private int _CALL_NUMBER;
	
	
	public int get_MSG_NUMBER() {
		return _MSG_NUMBER;
	}
	public void set_MSG_NUMBER(int _MSG_NUMBER) {
		this._MSG_NUMBER = _MSG_NUMBER;
	}
	public int get_SCORE() {
		return _SCORE;
	}
	public void set_SCORE(int _SCORE) {
		this._SCORE = _SCORE;
	}
	public int get_RMB() {
		return _RMB;
	}
	public void set_RMB(int _RMB) {
		this._RMB = _RMB;
	}
	public int get_WEIYUE_NUMBER() {
		return _WEIYUE_NUMBER;
	}
	public void set_WEIYUE_NUMBER(int _WEIYUE_NUMBER) {
		this._WEIYUE_NUMBER = _WEIYUE_NUMBER;
	}
	public int get_STATE() {
		return _STATE;
	}
	public void set_STATE(int _STATE) {
		this._STATE = _STATE;
	}
	public int get_CALL_NUMBER() {
		return _CALL_NUMBER;
	}
	public void set_CALL_NUMBER(int _CALL_NUMBER) {
		this._CALL_NUMBER = _CALL_NUMBER;
	}
}
