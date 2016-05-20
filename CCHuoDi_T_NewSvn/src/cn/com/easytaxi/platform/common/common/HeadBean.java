package cn.com.easytaxi.platform.common.common;

public class HeadBean {

	public static final int MAGIC = 0x21000000;

	// 标明是消息开始
	private Integer magic = MAGIC;

	// 消息ID
	private Integer msgId;

	// 消息的长度
	private Integer length;

	// 通道标识
	private Long id;

	// 辅助数据
	private Integer data=0;

	
	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public byte[] toArrayBytes() {
		byte[] buf = new byte[size()];

		byte[] magicbuf = SocketUtil.toHH(magic | msgId);
		for (int i = 0; i < 4; i++)
			buf[i] = magicbuf[i];

		byte[] lenbuf = SocketUtil.toHH(length ^ 19810803);
		for (int i = 0; i < 4; i++)
			buf[i + 4] = lenbuf[i];

		byte[] idbuf = SocketUtil.toHH(id ^ 19830920);
		for (int i = 0; i < 8; i++)
			buf[i + 8] = idbuf[i];
		
		byte[] databuf = SocketUtil.toHH(data ^ 20100114);
		for (int i = 0; i < 4; i++)
			buf[i + 16] = databuf[i];

		return buf;
	}

	public static HeadBean parseHead(byte[] buf) {
		HeadBean headBean = new HeadBean();
		Integer mid = SocketUtil.toInt(buf, 0);
		headBean.setMagic(mid & 0xFF000000);
		headBean.setMsgId(mid & 0xFFFFFF);
		headBean.setLength(SocketUtil.toInt(buf, 4) ^ 19810803);
		headBean.setId(SocketUtil.toLong(buf, 8) ^ 19830920);
		headBean.setData(SocketUtil.toInt(buf, 16)^20100114);
		return headBean;
	}

	public static int size() {
		return 20;
	}

	public Integer getMagic() {
		return magic;
	}

	public void setMagic(Integer magic) {
		this.magic = magic;
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

	public Integer getData() {
		return data;
	}

	public void setData(Integer data) {
		this.data = data;
	}
	
	

}
