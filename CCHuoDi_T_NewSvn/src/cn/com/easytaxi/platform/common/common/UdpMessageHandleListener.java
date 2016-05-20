package cn.com.easytaxi.platform.common.common;


public abstract class UdpMessageHandleListener {

	//�Ƿ񵥶������߳����������Ϣ(�����ʱ��Ϣ����ֵΪtrue)
	private boolean isThread;

	public UdpMessageHandleListener(boolean isThread) {
		this.isThread = isThread;
	}
	
	public UdpMessageHandleListener() {
		this(false);
	}

	public abstract void handle(ReceiveMsgBean msg);

	public boolean isThread() {
		return isThread;
	}
	
	

}
