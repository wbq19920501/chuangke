package cn.com.easytaxi.platform.common.common;

/**
 * 消息队列状态回调
 * 注意(在这里不要处理耗时的事件，否者可能会阻塞网络，耗时事件启动另一个线程去处理)
 */
public interface SendCallback {

	/**
	 * 发送成功
	 * 
	 * @param timestamp
	 *            发送时间
	 * @param ttl
	 *            重发次数
	 */
	public void success(Long timestamp, int ttl);

	/**
	 * 发送失败
	 * 
	 * @param timestamp
	 *            发送时间
	 * @param ttl
	 *            重发次数
	 */
	public void fail(Long timestamp, int ttl, Exception e);

	/**
	 * 每次失败
	 */
	public void failevery(Long timestamp, int ttl, Exception e);

}
