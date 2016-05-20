package cn.com.easytaxi.platform.common.common;

public class SocketUtil {
	

	public static byte[] toHH(int number) {
		int temp = number;
		byte[] b = new byte[4];
		for (int i = b.length - 1; i > -1; i--) {
			b[i] = new Integer(temp & 0xff).byteValue();
			temp = temp >> 8;
		}
		return b;
	}

	public static byte[] toHH(long number) {
		long temp = number;
		byte[] b = new byte[8];
		for (int i = b.length - 1; i > -1; i--) {
			b[i] = new Long(temp & 0xff).byteValue();
			temp = temp >> 8;
		}
		return b;
	}

	public static byte[] toHH(short n) {
		byte[] b = new byte[2];
		b[1] = (byte) (n & 0xff);
		b[0] = (byte) (n >> 8 & 0xff);
		return b;
	}

	public static short toShort(byte[] b, int offest) {
		return (short) ((b[offest] & 0xff) << 8 | (b[offest + 1] & 0xff));
	}

	public static int toInt(byte[] b, int offest) {
		return ((b[offest] & 0xff) << 24) | ((b[offest + 1] & 0xff) << 16) | ((b[offest + 2] & 0xff) << 8) | (b[offest + 3] & 0xff);
	}

	public static Long toLong(byte[] b, int offest) {
		return ((b[offest] & 0xffL) << 56) | ((b[offest + 1] & 0xffL) << 48) | ((b[offest + 2] & 0xffL) << 40) | ((b[offest + 3] & 0xffL) << 32) | ((b[offest + 4] & 0xffL) << 24) | ((b[offest + 5] & 0xffL) << 16) | ((b[offest + 6] & 0xffL) << 8) | (b[offest + 7] & 0xffL);
	}

}
