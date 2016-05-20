package cn.com.easytaxi.platform.common.common;


public abstract class UdpMessageHandleListener {

	//是否单独开启线程来处理该消息(如果费时消息，该值为true)
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
