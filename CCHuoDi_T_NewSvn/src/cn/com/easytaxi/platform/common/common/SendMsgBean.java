package cn.com.easytaxi.platform.common.common;

/**
 * ��������е���Ϣ
 * 
 * @author magi
 * 
 */
public class SendMsgBean {

	private Long id; // ����Ϣ������ͨ��

	private Integer msgId; // ��ϢID

	private byte[] body; // ��Ϣ��

	private Long timeout = Long.MAX_VALUE; // ��Ϣ��ʱʱ�䣬��ΪLong.MAX_VALUE��ʱ���޳�ʱ

	private int ttl = 0; // ���ʹ���

	private int sendCount = 1; // ����Ϣ����ʧ�ܣ������ط��Ĵ�����ΪInteger.MAX_VALUE��ʱ�������ط���ֱ����ʱ

	private SendCallback sendCallback; // ����Ϣ���

	private boolean isDead; // ���Ϊtrue,��ʾ��Ϣ�������������ʵ���ʱ�����������Ϣ

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
