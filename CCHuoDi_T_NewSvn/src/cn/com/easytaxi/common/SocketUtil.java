package cn.com.easytaxi.common;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.client.channel.TcpClient;
import cn.com.easytaxi.client.common.ConfigUtil;
import cn.com.easytaxi.client.common.MsgConst;

public class SocketUtil {

	public static JSONObject send(short msgId, JSONObject json) throws Throwable {
		return (JSONObject) sendMsg(msgId, json)[1];
	}

	public static JSONObject send(String ip, Integer port, short msgId, JSONObject json) throws Throwable {
		return (JSONObject) sendMsg(ip, port, msgId, json)[1];
	}

	public static Object[] sendMsg(short msgId, JSONObject json) throws Throwable {
		return sendMsg(Config.SERVER_IP, Config.SERVER_PORT, msgId, json);
	}

	private static Object[] sendMsg(String ip, Integer port, short msgId, JSONObject json) throws Throwable {
		List<Object> list = new ArrayList<Object>();
		boolean success = false;
		for (int i = 0; i < 3; i++) {
			Socket socket = null;
			OutputStream out = null;
			InputStream in = null;
			try {
				socket = new Socket(ip, port);
				socket.setSoTimeout(10000);
				out = socket.getOutputStream();
				in = socket.getInputStream();
				{
					byte[] data = new byte[0];
					if (json != null) {
						data = json.toString().getBytes("utf-8");
					}
					ByteArrayBuffer buf = new ByteArrayBuffer(HeadBean.size() + data.length);
					buf.append(new HeadBean((short) data.length, msgId).getBytes(), 0, HeadBean.size());
					buf.append(data, 0, data.length);
					out.write(buf.toByteArray());
					out.flush();
				}
				boolean end = false;
				while (!end) {
					byte[] buf = new byte[HeadBean.size()];
					int count = 0;
					while (count < buf.length) {
						int r = in.read(buf, count, buf.length - count);
						if (r < 0)
							break;
						else
							count += r;
					}
					HeadBean head = HeadBean.parseBytes(buf);
					parseHead(head);
					list.add(head);

					if (head.getLength() > 0) {
						buf = new byte[head.getLength()];
						count = 0;
						while (count < buf.length) {
							count += in.read(buf, count, buf.length - count);
							AppLog.LogD("<<<已返回");
						}
						String jsonStr = new String(buf);
						// Log.v("MY", jsonStr);
						list.add(new JSONObject(jsonStr));
					} else {
						list.add(null);
					}
					end = head.isEnd();
				}
				success = true;
				break;
			} catch (Throwable e) {
				e.printStackTrace();
			} finally {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
				if (socket != null)
					socket.close();
			}
		}
		if (!success) {
			throw new Exception("通讯失败，请检查网络");
		}

		return list.toArray();
	}

	private static void parseHead(HeadBean head) {
		short s = head.getMsg();
		if ((short) (s & 0xC000) == (short) 0xC000) {
			head.setError((short) 1);
		} else {
			head.setError((short) 0);
		}
		if ((short) (s & 0x2000) == 0x2000) {
			head.setEnd(true);
		} else {
			head.setEnd(false);
		}

		head.setMsg((short) (s & 0x1fff));
	}

	public static void asynSend(final Short msgId, final JSONObject json, final Callback<JSONObject> callback) {
		asynSend(Config.SERVER_IP, Config.SERVER_PORT, msgId, json, callback);
	}

	public static void asynSend(final String ip, final Integer port, final Short msgId, final JSONObject json, final Callback<JSONObject> callback) {
		final Handler handler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				try {
					Object result = msg.obj;
					if (callback != null) {
						if (result instanceof Throwable) {
							callback.error((Throwable) result);
						} else {
							if (result == null) {
								callback.handle(null);
							} else {
								JSONObject json = (JSONObject) result;
								if (json.isNull("errorcode")) {
									callback.handle(json);
								} else {
									callback.error(new Throwable("服务器返回错误信息(" + json.getString("errorcode") + ")"), json.getInt("errorcode"));
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (callback != null)
						callback.complete();
				}

			}
		};

		new Thread() {
			public void run() {
				Object result;
				try {
					result = send(ip, port, msgId, json);
					AppLog.LogD(">>>>>>" + result);
					handler.obtainMessage(0, result).sendToTarget();
				} catch (Throwable e) {
					handler.obtainMessage(0, e).sendToTarget();
				}

			};
		}.start();

	}

	/**
	 * @param server
	 *            3-ScheduleServer即时打车
	 * @param msgId
	 * @param json
	 * @param callback
	 */

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

	public static byte[] toHH(double n) {
		byte[] b = new byte[8];
		long l = Double.doubleToLongBits(n);
		for (int i = 0; i < 8; i++) {
			b[i] = new Long(l).byteValue();
			l = l >> 8;
		}
		return b;
	}

	public static short toShort(byte[] b, int offest) {
		return (short) ((b[offest] & 0xff) << 8 | (b[offest + 1] & 0xff));
	}

	public static int toInt(byte[] b, int offest) {
		return ((b[offest] & 0xff) << 24) | ((b[offest + 1] & 0xff) << 16) | ((b[offest + 2] & 0xff) << 8) | (b[offest + 3] & 0xff);
	}

	public static Long toLong(byte[] b, int offest) {
		return ((b[offest + 7] & 0xffL) << 56) | ((b[offest + 6] & 0xffL) << 48) | ((b[offest + 5] & 0xffL) << 40) | ((b[offest + 4] & 0xffL) << 32) | ((b[offest + 3] & 0xffL) << 24) | ((b[offest + 2] & 0xffL) << 16) | ((b[offest + 1] & 0xffL) << 8) | (b[offest + 0] & 0xffL);
	}

	public static double toDouble(byte[] b, int offest) {
		return Double.longBitsToDouble(toLong(b, offest));
	}

	public static void sendNewTcp(final Long id, final Integer msgId, final byte[] buf, final Callback<byte[]> callback) throws Exception {
		final Handler handler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				try {
					Object result = msg.obj;
					if (callback != null) {
						if (result instanceof Throwable) {
							callback.error((Throwable) result);
						} else {
							if (result == null) {
								callback.handle(null);
							} else {
								byte[] buf = (byte[]) result;
								callback.handle(buf);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (callback != null)
						callback.complete();
				}

			}
		};

		new Thread() {
			public void run() {
				byte[] result;
				try {
					InetAddress ip = InetAddress.getByName(ConfigUtil.getString("IP"));

					Integer port = ConfigUtil.getInt("TCP_PORT_30");
					AppLog.LogD("xyw", "=====--- ip : " + ip + " , port : " + port);
					result = TcpClient.send(ip, port, id, msgId, buf);

					// AppLog.LogD("xyw" + new String( result, "utf-8"));
					handler.obtainMessage(0, result).sendToTarget();
				} catch (Throwable e) {
					e.printStackTrace();
					handler.obtainMessage(0, e).sendToTarget();
				}

			};
		}.start();
	}

	public static void getJSONObject(Long id, JSONObject json, final Callback<JSONObject> callback) throws Exception {
		sendNewTcp(id, MsgConst.MSG_TCP_ACTION, json.toString().getBytes(), new Callback<byte[]>() {
			@Override
			public void handle(byte[] result) {
				try {
					if (result == null) {
						callback.handle(null);
					} else {
						JSONObject json = new JSONObject(new String(result, "UTF-8"));
						callback.handle(json);
					}
				} catch (Throwable e) {
					e.printStackTrace();
					callback.error(e);
				}
			}

			public void error(Throwable e) {
				callback.error(e);
			}

			@Override
			public void complete() {
				callback.complete();
			}
		});
	}

	public static void getJSONArray(Long id, JSONObject json, final Callback<byte[]> callback) throws Exception {
		sendNewTcp(id, MsgConst.MSG_TCP_ACTION, json.toString().getBytes(), new Callback<byte[]>() {

			@Override
			public void handle(byte[] result) {
				try {
					if (result == null) {
						callback.handle(null);
					} else {
						callback.handle(result);
					}
				} catch (Throwable e) {
					e.printStackTrace();
					callback.error(e);
				}
			}

			@Override
			public void complete() {
				callback.complete();
			}
		});
	}

	public static JSONObject getJSONObject(Long id, JSONObject json) throws Exception {
		byte[] rst = TcpClient.send(id, MsgConst.MSG_TCP_ACTION, json.toString().getBytes());
		return new JSONObject(new String(rst, "UTF-8"));
	}

	public static JSONArray getJSONArray(Long id, JSONObject json) throws Exception {
		byte[] rst = TcpClient.send(id, MsgConst.MSG_TCP_ACTION, json.toString().getBytes());
		return new JSONArray(new String(rst, "UTF-8"));
	}
}
