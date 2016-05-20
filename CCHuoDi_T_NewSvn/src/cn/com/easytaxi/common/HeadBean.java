package cn.com.easytaxi.common;

import org.apache.http.util.ByteArrayBuffer;


public class HeadBean {

	private short magic = 0x2012;

	private short version = 0x0000;

	private short error = 0;

	private short property = 0; // 特性

	private short msg; // 消息码

	private short length;// 数据长度
	
	private boolean end=true;//是否是最后一个消息

	public HeadBean() {

	}

	public HeadBean(short length, short msg) {
		this.length = length;
		this.msg = msg;
	}

	public byte[] getBytes() {
		ByteArrayBuffer buf = new ByteArrayBuffer(12);
		buf.append(SocketUtil.toHH(magic), 0, 2);
		buf.append(SocketUtil.toHH(version), 0, 2);
		buf.append(SocketUtil.toHH((short)0), 0, 2);
		buf.append(SocketUtil.toHH(property), 0, 2);
		buf.append(SocketUtil.toHH(msg), 0, 2);
		buf.append(SocketUtil.toHH(length), 0, 2);
		return buf.toByteArray();
	}

	public static HeadBean parseBytes(byte[] buf) {
		HeadBean headBean = new HeadBean();
		headBean.setMagic(SocketUtil.toShort(buf, 0));
		headBean.setVersion(SocketUtil.toShort(buf, 2));
		headBean.setError(SocketUtil.toShort(buf, 4));
		headBean.setProperty(SocketUtil.toShort(buf, 6));
		headBean.setMsg(SocketUtil.toShort(buf, 8));
		headBean.setLength(SocketUtil.toShort(buf, 10));

		return headBean;
	}

	public static byte size() {
		return 12;
	}

	public short getMagic() {
		return magic;
	}

	public void setMagic(short magic) {
		this.magic = magic;
	}

	public short getVersion() {
		return version;
	}

	public void setVersion(short version) {
		this.version = version;
	}

	

	public short getError() {
		return error;
	}

	public void setError(short error) {
		this.error = error;
	}

	public short getProperty() {
		return property;
	}

	public void setProperty(short property) {
		this.property = property;
	}

	public short getMsg() {
		return msg;
	}

	public void setMsg(short msg) {
		this.msg = msg;
	}

	public short getLength() {
		return length;
	}

	public void setLength(short length) {
		this.length = length;
	}

	public boolean isEnd() {
		return end;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}
	
	
}
