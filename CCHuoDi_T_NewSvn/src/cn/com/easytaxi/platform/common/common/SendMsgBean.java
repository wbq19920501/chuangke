package cn.com.easytaxi.platform.common.common;

/**
 * 放入队列中的消息
 * 
 * @author magi
 * 
 */
public class SendMsgBean {

	private Long id; // 该消息所属的通道

	private Integer msgId; // 消息ID

	private byte[] body; // 消息体

	private Long timeout = Long.MAX_VALUE; // 消息超时时间，当为Long.MAX_VALUE的时候，无超时

	private int ttl = 0; // 发送次数

	private int sendCount = 1; // 当消息发送失败，进行重发的次数，为Integer.MAX_VALUE的时候无限重发，直到超时

	private SendCallback sendCallback; // 对消息监控

	private boolean isDead; // 如果为true,表示消息已死，队列在适当的时候会抛弃该消息

	public SendMsgBean( Integer msgId, byte[] body) {
		this.msgId = msgId;
		this.body = body;
	}

	public SendMsgBean(Long id, Integer msgId) {
		this(msgId, new byte[0]);
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	public Long getTimeout() {
		return timeout;
	}

	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}

	public int getSendCount() {
		return sendCount;
	}

	public void setSendCount(int sendCount) {
		this.sendCount = sendCount;
	}

	public SendCallback getSendCallback() {
		return sendCallback;
	}

	public void setSendCallback(SendCallback sendCallback) {
		this.sendCallback = sendCallback;
	}

	public boolean isDead() {
		return isDead;
	}

	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	public void addTtl() {
		this.ttl++;
	}

	public Integer getMsgId() {
		return msgId;
	}

	public void setMsgId(Integer msgId) {
		this.msgId = msgId;
	}

}
