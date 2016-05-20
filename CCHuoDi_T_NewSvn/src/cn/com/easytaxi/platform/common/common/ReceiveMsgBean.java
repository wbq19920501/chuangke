package cn.com.easytaxi.platform.common.common;


public class ReceiveMsgBean {

	private Long id; // 改消息所属的通道
	
	private Integer msgId;

	private byte[] body; // 消息体

	private Long receiveTime; // 消息接收的时间
	
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
