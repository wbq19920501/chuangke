package cn.com.easytaxi.platform.common.common;


public class ReceiveMsgBean {

	private Long id; // ����Ϣ������ͨ��
	
	private Integer msgId;

	private byte[] body; // ��Ϣ��

	private Long receiveTime; // ��Ϣ���յ�ʱ��
	
	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	public Long getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(Long receiveTime) {
		this.receiveTime = receiveTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getMsgId() {
		return msgId;
	}

	public void setMsgId(Integer msgId) {
		this.msgId = msgId;
	}
	
	

}
