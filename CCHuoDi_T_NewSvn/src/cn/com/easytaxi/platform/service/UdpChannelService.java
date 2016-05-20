package cn.com.easytaxi.platform.service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;

import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.platform.common.common.Config;
import cn.com.easytaxi.platform.common.common.HeadBean;
import cn.com.easytaxi.platform.common.common.ReceiveMsgBean;
import cn.com.easytaxi.platform.common.common.SendCallback;
import cn.com.easytaxi.platform.common.common.SendMsgBean;
import cn.com.easytaxi.platform.common.common.UdpMessageHandleListener;

/**
 * ����UDPͨ��
 * 
 * @author magi
 * 
 */
public class UdpChannelService {

	private HashMap<Integer, UdpMessageHandleListener> msgMap;

	// ���ܶ���
	private LinkedList<ReceiveMsgBean> receiveQueue;

	// ���Ͷ���
	private LinkedList<SendMsgBean> sendQueue;

	public DatagramSocket datasocket;

	private boolean isLoop = true;

	private InetAddress ip;

	private volatile Long id;

	public UdpChannelService() {
		receiveQueue = new LinkedList<ReceiveMsgBean>();
		sendQueue = new LinkedList<SendMsgBean>();
		msgMap = new HashMap<Integer, UdpMessageHandleListener>();
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void start(final Long id) throws Exception {
		this.id = id;

		if (datasocket != null && datasocket.isConnected()) {
			datasocket.disconnect();
		}

		datasocket = new DatagramSocket();
		isLoop = true;

		// ����UDP����ͨ��
		new Thread() {
			// ����4K�ֽڵĻ�����

			@Override
			public void run() {

				while (isLoop) {
				 
					
					try {
						  byte[] buf = new byte[4096];
						  DatagramPacket dataPacket = new DatagramPacket(buf, buf.length);

						datasocket.receive(dataPacket);

						Long curTime = Calendar.getInstance().getTimeInMillis();

						HeadBean headBean = HeadBean.parseHead(buf);
						if (headBean.getMagic() == HeadBean.MAGIC) {

							if (!headBean.getId().equals(UdpChannelService.this.id))
								continue;

							if (headBean.getLength() > 4096)
								continue;
							 
							byte[] objbuf = new byte[headBean.getLength()];
							for (int i = 0; i < headBean.getLength(); i++) {
								objbuf[i] = buf[i + HeadBean.size()];
							}

						 
							synchronized (receiveQueue) {

								ReceiveMsgBean msgBean = new ReceiveMsgBean();
								msgBean.setId(headBean.getId());
								msgBean.setMsgId(headBean.getMsgId());
							 
								msgBean.setBody(objbuf);
								AppLog.LogD("====---9999999--udp--receive msg ----  " + Integer.toHexString(headBean.getMsgId()) + " , "   );
								msgBean.setReceiveTime(curTime);
								receiveQueue.add(msgBean);
							}
						}
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
		}.start();

		// ����UDP����ͨ��
		new Thread() {
			@Override
			public void run() {
				while (isLoop) {
					try {
						long curtime = System.currentTimeMillis();
						
						SendMsgBean sendMsgBean = null;
						synchronized (sendQueue) {
							if (sendQueue.isEmpty())
								continue;
							else
								sendMsgBean = sendQueue.poll();
						}
						//AppLog.LogD("====---9999999--udp----------------s msg ----  1 :"  + isLoop  );
						if (sendMsgBean == null)
							continue;
						//AppLog.LogD("====---9999999--udp----------------s msg ----  2 :"  + isLoop  );
						sendMsgBean.setId(id);
						//AppLog.LogD("====---9999999--udp----------------s msg ----  3 :"  + isLoop  );
						SendCallback callback = sendMsgBean.getSendCallback();

						if (sendMsgBean.isDead())
							continue;

						if (Calendar.getInstance().getTimeInMillis() > sendMsgBean.getTimeout())
							continue;

						// ��װ��������
						byte[] obj = sendMsgBean.getBody();
						//AppLog.LogD("====---9999999--udp----------------s msg ----  4 :"  + isLoop  );
						HeadBean headBean = new HeadBean();
						headBean.setId(id);
						headBean.setMsgId(sendMsgBean.getMsgId());
						headBean.setLength(obj.length);
						byte[] head = headBean.toArrayBytes();

						byte[] data = new byte[HeadBean.size() + obj.length];
						for (int i = 0; i < head.length; i++) {
							data[i] = head[i];
						}
						for (int i = 0; i < obj.length; i++) {
							data[i + head.length] = obj[i];
						}

						if (ip == null) {
							ip = InetAddress.getByName(Config.SERVER_IP);
						}

						

						DatagramPacket pack = new DatagramPacket(data, data.length, ip, Config.SERVER_UDP_PORT);
						sendMsgBean.addTtl();
						try {
						
							datasocket.send(pack);

							if (callback != null) {// ���ͳɹ�
								callback.success(curtime, sendMsgBean.getTtl());
							}
						} catch (Exception e) {
							e.printStackTrace();
						
							if (callback != null) {
								callback.failevery(curtime, sendMsgBean.getTtl(), e);
							}
							if (sendMsgBean.getTtl() < sendMsgBean.getSendCount()) {
								
//								AppLog.LogD("====-----999999999  send udp----  " + Integer.toHexString(sendMsgBean.getMsgId()) + " , "   );
								
								
								AppLog.LogD("====-------" + sendMsgBean.getMsgId() + " , " + new String(sendMsgBean.getBody(), "utf-8"));
								sendQueue.add(sendMsgBean); // �ŵ���Ϣ����β�����ȴ��´η���
							} else {
								if (callback != null)
									callback.fail(curtime, sendMsgBean.getTtl(), new Exception("��Ϣ����ʧ��"));
							}
						}
					} catch (Throwable e) {
						e.printStackTrace();
					} finally {
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}.start();

		// ������Ϣ�����߳�
		new Thread() {
			@Override
			public void run() {
				while (isLoop) {
					try {
						ReceiveMsgBean receiveMsgBean = null;
						synchronized (receiveQueue) {
							receiveMsgBean = receiveQueue.poll();
						}
						if (receiveMsgBean == null)
							continue;

						final UdpMessageHandleListener handleListener = msgMap.get(receiveMsgBean.getMsgId());

						if (handleListener == null)
							continue;

						if (handleListener.isThread()) { // ��������һ���߳�������
							new Thread() {
								private ReceiveMsgBean receiveMsgBean;

								private Thread set(ReceiveMsgBean msg) {
									this.receiveMsgBean = msg;
									return this;
								}

								public void run() {
								 // TODO Auto-generated catch block
//									AppLog.LogD("====-----udp--receive msg ----  " + Integer.toHexString(headBean.getMsgId()) + " , "   );
										
									handleListener.handle(receiveMsgBean);
								}
							}.set(receiveMsgBean).start();
						} else { // ����Ϣ�߳��д���
							handleListener.handle(receiveMsgBean);
						}

					} catch (Throwable e) {
						e.printStackTrace();
					} finally {
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}.start();
	}

	/**
	 * ֹͣ����
	 */
	public void stop() {
		isLoop = false;
	}

	/**
	 * ������Ϣ
	 */
	public void sendMessage(SendMsgBean sendMsgBean) {
		
		
		if (this.id <= 10L)
			return;
		synchronized (sendQueue) {
			AppLog.LogD("udp sendMsgBean" + Integer.toHexString(sendMsgBean.getMsgId()));
			sendQueue.add(sendMsgBean);
		}
	}

	/**
	 * ע����Ϣ������
	 */
	public void regMsgHandleListener(Integer msgId, UdpMessageHandleListener handleListener) {
		synchronized (msgMap) {
			msgMap.put(msgId, handleListener);
		}
	}

	/**
	 * ע����Ϣ������
	 */
	public void unregMsgHandleListener(Integer msgId) {
		synchronized (msgMap) {
			msgMap.remove(msgId);
		}
	}

}
