package cn.com.easytaxi.platform.common.common;

/**
 * ��Ϣ����״̬�ص�
 * ע��(�����ﲻҪ�����ʱ���¼������߿��ܻ��������磬��ʱ�¼�������һ���߳�ȥ����)
 */
public interface SendCallback {

	/**
	 * ���ͳɹ�
	 * 
	 * @param timestamp
	 *            ����ʱ��
	 * @param ttl
	 *            �ط�����
	 */
	public void success(Long timestamp, int ttl);

	/**
	 * ����ʧ��
	 * 
	 * @param timestamp
	 *            ����ʱ��
	 * @param ttl
	 *            �ط�����
	 */
	public void fail(Long timestamp, int ttl, Exception e);

	/**
	 * ÿ��ʧ��
	 */
	public void failevery(Long timestamp, int ttl, Exception e);

}
