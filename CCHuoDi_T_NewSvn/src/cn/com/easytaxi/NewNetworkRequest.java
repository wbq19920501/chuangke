package cn.com.easytaxi;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import cn.com.easytaxi.client.channel.TcpClient;
import cn.com.easytaxi.client.common.MsgConst;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.common.Const;
import cn.com.easytaxi.common.NetChecker;
import cn.com.easytaxi.common.Window;
import cn.com.easytaxi.mine.bean.MyInfo;
import cn.com.easytaxi.onetaxi.common.BookBean;
import cn.com.easytaxi.platform.MainActivityNew;
import cn.com.easytaxi.platform.RegisterActivity;
import cn.com.easytaxi.ui.ContactList.ContactEntity;
import cn.com.easytaxi.ui.MoreActivity;
import cn.com.easytaxi.ui.SuggestionActivity;
import cn.com.easytaxi.ui.UserProfileActivity;
import cn.com.easytaxi.ui.bean.MsgBean;
import cn.com.easytaxi.ui.bean.YDUserComments;
import com.easytaxi.etpassengersx.wxapi.WXPayEntryActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class NewNetworkRequest {

	protected static boolean isMock = false;
	protected static final int SERVER_FAILED_REASON = -2;
	protected static final int FAILED = -1;
	protected static final int SUCESS = 0;
	protected static final int BOOK_REQUEST = 1;
	protected static final String tag = "NewNetworkRequest";
	protected static final int USER_PROFILE = UserProfileActivity.USER_PROFILE;
	protected static final int USER_COMMENTS = UserProfileActivity.USER_COMMENTS;

	// protected static final int MSG_HISTORY_SHORT_MSG_REMOTE =
	// MsgData.MSG_HISTORY_SHORT_MSG_REMOTE;

	private static ExecutorService pool;
	static {
		pool = Executors.newFixedThreadPool(8);
	}

	public static void sendOneTaxiVoice(String startAddress, String endAddress, String voicPath) {

	}

	public static void sendOneTaxiMsg(String startAddress, String endAddress) {

	}

	public static class DioaDuPrices {
		public int error;
		public int defaultKey;
		public int index;
		public List<DiaoDuPrice> prices;
	}

	public static class DiaoDuPrice {
		public String text;
		public int val;
	}

	public static void getDiaoDuPriceList(int type) {

		if (!NetChecker.getInstance(ETApp.getInstance().getApplicationContext()).isAvailableNetwork()) {
			return;
		}
		// ���ص��Ƕ�����ID
		AppLog.LogD(tag, "��ʼ���ݵ��ȷ���Ϣ ������ ");

		Gson gson = new Gson();
		JsonObject json = new JsonObject();
		json.addProperty("action", "utilAction");
		json.addProperty("method", "getPriceList");

		json.addProperty("type", type);

		try {
			byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
			if (response != null && response.length > 0) {
				String retString = new String(response, "utf-8");
				AppLog.LogD(tag, retString);
				DioaDuPrices dioaDuPrices = gson.fromJson(retString, DioaDuPrices.class);
				dioaDuPrices.defaultKey = dioaDuPrices.index;
				if (dioaDuPrices.error == 0) {
					if (type == 2) {
						ETApp.getInstance().saveCacheString("book_pricelist", retString);

					} else {
						ETApp.getInstance().saveCacheString("pricelist", retString);
					}

				} else {

				}

			} else {

			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	/**
	 * ��ȡ���ȷ��б�
	 * 
	 * @return
	 */
	public static void getDiaoDuPriceList(final int type, final Callback<Object> callback) {

		final Handler handlerPrice = new Handler(ETApp.getInstance().getMainLooper()) {
			public void handleMessage(Message msg) {

				AppLog.LogD(tag, "getDiaoDuPriceList ---- ");

				List<DiaoDuPrice> prices = null;
				switch (msg.arg1) {
				case SUCESS:
					prices = (List<DiaoDuPrice>) msg.obj;

					break;
				case FAILED:
					prices = new ArrayList<NewNetworkRequest.DiaoDuPrice>(12);

				case BOOK_REQUEST:
					prices = (List<DiaoDuPrice>) msg.obj;
				default:
					break;
				}
				if (callback != null) {

					callback.handle(prices);
				}
			}
		};

		new Thread(new Runnable() {
			public void run() {
				// ���ص��Ƕ�����ID
				AppLog.LogD(tag, "��ʼ���ݵ��ȷ���Ϣ ������ ");
				Message message = Message.obtain();
				Gson gson = new Gson();
				JsonObject json = new JsonObject();
				json.addProperty("action", "utilAction");
				json.addProperty("method", "getPriceList");

				json.addProperty("type", type);

				message.what = 1;

				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {
						String retString = new String(response, "utf-8");
						// AppLog.LogD(tag, retString);
						DioaDuPrices dioaDuPrices = gson.fromJson(retString, DioaDuPrices.class);
						dioaDuPrices.defaultKey = dioaDuPrices.index;
						if (dioaDuPrices.error == 0) {
							if (type == 2) {
								ETApp.getInstance().saveCacheString("book_pricelist", retString);
								message.arg1 = BOOK_REQUEST;
							} else {
								ETApp.getInstance().saveCacheString("pricelist", retString);
								message.arg1 = SUCESS;
							}
							message.obj = dioaDuPrices.prices;
							AppLog.LogD(tag, "���ݵ��ȷ���� --- " + retString);
							handlerPrice.sendMessage(message);
						} else {
							if (type == 2) {
								message.arg1 = BOOK_REQUEST;
								message.obj = null;
								AppLog.LogD(tag, "���ȡ�ݵ��ȷ� ʧ�� --1- ");
								handlerPrice.sendMessage(message);
							} else {
								String ret = ETApp.getInstance().getCacheString("pricelist");
								DioaDuPrices dioaDuPricesTmp = gson.fromJson(ret, DioaDuPrices.class);
								message.arg1 = SUCESS;
								message.obj = dioaDuPricesTmp.prices;
								AppLog.LogD(tag, "���ȡ�ݵ��ȷ� ʧ�� --1- ");
								handlerPrice.sendMessage(message);
							}
						}

					} else {
						if (type == 2) {
							message.arg1 = BOOK_REQUEST;
							message.obj = null;
							AppLog.LogD(tag, "���ȡ�ݵ��ȷ� ʧ�� --2- ");
							handlerPrice.sendMessage(message);
						} else {
							String ret = ETApp.getInstance().getCacheString("pricelist");
							DioaDuPrices dioaDuPricesTmp = gson.fromJson(ret, DioaDuPrices.class);
							message.arg1 = SUCESS;
							message.obj = dioaDuPricesTmp.prices;
							AppLog.LogD(tag, "���ȡ�ݵ��ȷ� ʧ�� --2- ");
							handlerPrice.sendMessage(message);
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
					if (type == 2) {
						message.arg1 = BOOK_REQUEST;
						message.obj = null;
						AppLog.LogD(tag, "���ȡ�ݵ��ȷ� ʧ�� --2- ");
						handlerPrice.sendMessage(message);
					} else {
						String ret = ETApp.getInstance().getCacheString("pricelist");
						DioaDuPrices dioaDuPricesTmp = gson.fromJson(ret, DioaDuPrices.class);
						message.arg1 = SUCESS;
						message.obj = dioaDuPricesTmp.prices;
						AppLog.LogD(tag, "���ȡ�ݵ��ȷ� ʧ�� --2- ");
						handlerPrice.sendMessage(message);
					}
				}
			}
		}).start();

	}

	public static int sendMms(String phone, String content) {
		int errorCode = 0;
		return errorCode;
	}

	/**
	 * ���߾�γ�� ��ȡ ��ַ
	 */
	public static void getAddressByLocation(final Integer lat, final Integer lng, final Callback<String> callback) {
		final Handler handler = new Handler(ETApp.getInstance().getMainLooper()) {
			public void handleMessage(Message msg) {
				String address = null;
				switch (msg.arg1) {
				case SUCESS:
					address = (String) msg.obj;

					break;
				case FAILED:

					address = null;
				default:
					break;
				}
				if (callback != null) {
					callback.handle(address);
				}
				// sendCallback.handle(address);
			}
		};

		Thread t = new Thread(new Runnable() {
			public void run() {
				// ���ص��Ƕ�����ID

				Message message = Message.obtain();

				JsonObject json = new JsonObject();
				json.addProperty("action", "geoCodingAction");
				json.addProperty("method", "getAddressByLocation");

				json.addProperty("lat", lat);
				json.addProperty("lng", lng);

				AppLog.LogD(tag, "��ʼ���������ȡ��ַ��Ϣ ������ " + json.toString());
				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {
						String address = new String(response, "UTF-8");
						message.arg1 = SUCESS;
						message.obj = address;
						AppLog.LogD(tag, "���ȡ��ַ��Ϣ ��� ��ַΪ�� " + message.obj);

						handler.sendMessage(message);
					} else {
						message.arg1 = FAILED;
						AppLog.LogD(tag, "���ȡ��ַ��Ϣ ʧ�� --1- ");
						handler.sendMessage(message);

					}
				} catch (Exception e) {
					e.printStackTrace();
					message.arg1 = FAILED;
					AppLog.LogD(tag, "���ȡ��ַ��Ϣ --2- ");
					handler.sendMessage(message);
				}
			}
		});
		// t.setPriority(6);
		t.start();
	}

	/**
	 * ���ߵ�ַ��ȡ ��γ��
	 */
	public static void getAddressLngLat(final String city, final String address, final Callback<Object> callback) {
		final Handler handler = new Handler(ETApp.getInstance().getMainLooper()) {
			public void handleMessage(Message msg) {
				JSONObject lat_lng = null;
				switch (msg.arg1) {
				case SUCESS:
					lat_lng = (JSONObject) msg.obj;
					break;
				case FAILED:

					lat_lng = null;
				default:
					break;
				}
				if (callback != null) {

					callback.handle(lat_lng);
				}
			}
		};

		new Thread(new Runnable() {
			public void run() {
				// ���ص��Ƕ�����ID
				AppLog.LogD(tag, "��ʼ���ݵ�ַ��ȡ��γ�� ������ " + city + " , " + address);
				Message message = Message.obtain();

				JsonObject json = new JsonObject();
				json.addProperty("action", "geoCodingAction");
				json.addProperty("method", "getAddressLngLat");

				json.addProperty("city", city);
				json.addProperty("address", address);
				message.what = 1;

				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {

						String res = new String(response, "UTF-8");
						AppLog.LogD(tag, " --- " + res);

						JSONObject retJson = new JSONObject(res);

						message.arg1 = SUCESS;
						message.obj = retJson;
						AppLog.LogD(tag, "��ַ��ȡ��γ�� ��� --- " + message.obj);

						handler.sendMessage(message);
					} else {
						message.arg1 = FAILED;
						AppLog.LogD(tag, "��ַ��ȡ��γ�� ʧ�� --1- ");
						handler.sendMessage(message);

					}
				} catch (Exception e) {
					e.printStackTrace();
					message.arg1 = FAILED;
					AppLog.LogD(tag, "��ַ��ȡ��γ�� --2- ");
					handler.sendMessage(message);
				}
			}
		}).start();
	}

	/**
	 * �˿�����Ӧ�ӵ�
	 */
	public static synchronized void getReplyBook(final long bookId, final Callback<BookBean> callback) {

		new Thread(new Runnable() {
			public void run() {
				// ���ص��Ƕ�����ID

				JsonObject json = new JsonObject();
				json.addProperty("action", "scheduleAction");
				json.addProperty("method", "getReplyBook");
				json.addProperty("bookId", bookId);
				AppLog.LogD(tag, " ��ʼ ��ȡ�ӵ���Ķ���  :  " + json.toString());
				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {
						String res = new String(response, "UTF-8");

						JSONObject jo = new JSONObject(res);
						int error = jo.getInt("error");
						if (error == 0) {
							AppLog.LogD(tag, "  ��ȡ�ӵ���Ķ��� :  " + res.toString());
							Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
							BookBean bookbean = gson.fromJson(res, BookBean.class);
							if (callback != null) {

								callback.handle(bookbean);
							}
						} else {
							if (callback != null)
								callback.handle(null);
						}
					} else {
						if (callback != null)
							callback.handle(null);

					}
				} catch (Exception e) {
					e.printStackTrace();
					if (callback != null)
						callback.handle(null);

				}
			}
		}).start();
	}

	// �ύ��ʱ����
	public static synchronized void submitBook(final BookBean bb, final Callback<Object> callback) {
		final Handler handler = new Handler(ETApp.getInstance().getMainLooper()) {
			public void handleMessage(Message msg) {
				JSONObject booJson = null;
				switch (msg.arg1) {
				case SUCESS:
					booJson = (JSONObject) msg.obj;
					// NewNetworkRequest.startWait(booJson.getLong("bookId"),
					// booJson.getLong("timeout"));
					break;

				case FAILED:
					booJson = null;
					break;

				case SERVER_FAILED_REASON:
					booJson = (JSONObject) msg.obj;
					break;

				default:
					break;
				}
				if (callback != null)
					callback.handle(booJson);
			}
		};

		new Thread(new Runnable() {
			public void run() {

				Message message = Message.obtain();
				Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

				JsonObject json = (JsonObject) gson.toJsonTree(bb);
				json.addProperty("action", "scheduleAction");
				json.addProperty("method", "submitBook");

				json.addProperty("clientType", BookConfig.ClientType.CLIENT_TYPE_PASSENGER);
				int versionCode = ETApp.getInstance().getMobileInfo().getVerisonCode();
				json.addProperty("clientVersion", "Ver." + versionCode);
				json.addProperty("payment", bb.getPrice());
				json.addProperty("onlinePayment", bb.isOnlinePayment());

				// ��ֱ�޷��ġ���������������������������������
				json.addProperty("carType", cn.com.easytaxi.onetaxi.MainActivityNew.mCarType);
				json.addProperty("personNum", cn.com.easytaxi.onetaxi.MainActivityNew.mNeedPerson);

				json.remove("replyerPhone");
				json.remove("replyerNumber");
				json.remove("replyerName");
				json.remove("replyerCompany");
				json.remove("replyerId");
				json.remove("replyerLongitude");
				json.remove("replyerLatitude");
				json.remove("replyerType");
				json.remove("forecastDistance");
				json.remove("replyerType");

				AppLog.LogD("xyw", "��ʱ��submit json��" + json);
				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null) {
						String rs = new String(response, "UTF-8");
						JSONObject retJson = new JSONObject(rs);
						int error = retJson.getInt("error");

						if (error == 0) {
							message.arg1 = SUCESS;
							message.obj = retJson;

						} else {
							message.arg1 = SERVER_FAILED_REASON;
							message.obj = retJson;
						}

						handler.sendMessage(message);
					} else {
						message.arg1 = FAILED;
						message.obj = null;
						handler.sendMessage(message);

					}
				} catch (Exception e) {
					e.printStackTrace();
					message.arg1 = FAILED;
					message.obj = null;

					handler.sendMessage(message);
				}
			}
		}).start();
	}

	static Timer timer;

	protected synchronized static void startWait(long bookId, long timeOut) {
		if (timer != null) {
			timer.cancel();
		}

		// timer = new Timer("waitTimer");

		// send borad cast

	}

	/**
	 * 
	 * @param t_lat
	 *            ���⳵
	 * @param t_lng
	 * @param p_lat
	 *            �˿�
	 * @param p_lng
	 * @param d_lat
	 *            Ŀ�ĵ�
	 * @param d_lng
	 * @param callback
	 * 
	 *            <p>
	 *            row.put("error", 0); row.put("distance", 446); //���⳵�˿;���
	 *            row.put("price", 33); //���⳵�˿;��� row.put("course", 11);
	 *            //���⳵�˿;��� row.put("time", 33); //���⳵�˿;��� row.put("wait", 5);
	 *            //���⳵�˿;���
	 *            </p>
	 */
	public static synchronized void getMark(final int t_lat, final int t_lng, final int p_lat, final int p_lng, final int d_lat, final int d_lng, final Callback<Object> callback) {
		final Handler handler = new Handler(ETApp.getInstance().getMainLooper()) {
			public void handleMessage(Message msg) {
				JSONObject markJson = null;
				switch (msg.arg1) {
				case SUCESS:
					markJson = (JSONObject) msg.obj;
					break;
				case FAILED:

					markJson = null;
				default:
					break;
				}
				if (callback != null)
					callback.handle(markJson);
			}
		};

		new Thread(new Runnable() {
			public void run() {
				// ���ص��Ƕ�����ID

				Message message = Message.obtain();

				JsonObject json = new JsonObject();
				json.addProperty("action", "geoCodingAction");
				json.addProperty("method", "getMark");

				json.addProperty("t_lat", t_lat);
				json.addProperty("t_lng", t_lng);

				json.addProperty("p_lat", p_lat);
				json.addProperty("p_lng", p_lng);

				json.addProperty("d_lat", d_lat);
				json.addProperty("d_lng", d_lng);
				AppLog.LogD(tag, "��ȡ mark ��ʼ --- " + json.toString());
				message.what = 1;
				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {

						String rs = new String(response, "UTF-8");

						// {"error":0,"bookId":1364286932656,"timeout":250000}

						JSONObject retJson = new JSONObject(rs);
						int error = retJson.getInt("error");

						if (error == 0) {
							message.arg1 = SUCESS;
							message.obj = retJson;
							AppLog.LogD(tag, "��ȡ mark ��� --- " + retJson.toString());
						} else {
							message.arg1 = FAILED;
						}

						handler.sendMessage(message);
					} else {
						message.arg1 = FAILED;
						AppLog.LogD(tag, "��ȡ mark�� ʧ�� --1- ");
						handler.sendMessage(message);

					}
				} catch (Exception e) {
					e.printStackTrace();
					message.arg1 = FAILED;
					AppLog.LogD(tag, "��ȡ mark --2- ");
					handler.sendMessage(message);
				}
			}
		}).start();
	}

	// 0 1-21 22---
	// ͷ ���� ����
	public static synchronized void sendSound(final byte[] datas, final Callback<Object> callback) {
		final Handler handler = new Handler(ETApp.getInstance().getMainLooper()) {
			public void handleMessage(Message msg) {
				JSONObject markJson = null;
				switch (msg.arg1) {
				case SUCESS:
					markJson = (JSONObject) msg.obj;
					break;
				case FAILED:
					markJson = null;
					break;
				case SERVER_FAILED_REASON:
					markJson = (JSONObject) msg.obj;
					break;
				default:
					break;
				}
				if (callback != null)
					callback.handle(markJson);
			}
		};

		new Thread(new Runnable() {
			public void run() {
				// ���ص��Ƕ�����ID
				AppLog.LogD(tag, "sendSound ������ ");
				Message message = Message.obtain();

				message.what = 1;
				try {
					byte[] response = TcpClient.send(1L, 0xF00008, datas);
					if (response != null && response.length > 0) {

						String rs = new String(response, "UTF-8");
						// {"error":0,"bookId":1364286932656,"timeout":250000}

						JSONObject retJson = new JSONObject(rs);

						AppLog.LogD(tag, "sendSound return  ������ " + rs);

						int error = retJson.getInt("error");

						if (error == 0) {
							message.arg1 = SUCESS;
							message.obj = retJson;

						} else {
							message.arg1 = SERVER_FAILED_REASON;
							message.obj = retJson;
						}

						handler.sendMessage(message);
					} else {
						message.arg1 = FAILED;
						message.obj = null;
						AppLog.LogD(tag, "������- ʧ�� --1- ");
						handler.sendMessage(message);

					}
				} catch (Exception e) {
					e.printStackTrace();
					message.arg1 = FAILED;
					message.obj = null;
					AppLog.LogD(tag, "������- --2- ");
					handler.sendMessage(message);
				}
			}
		}).start();
	}

	// public final static Integer TCP_SOUND_FILE = 0xF00008;

	/*
	 * NSMutableDictionary *dic = [[NSMutableDictionary alloc] init]; [dic
	 * setObject:@"suggestAction" forKey:@"action"]; [dic
	 * setObject:@"suggestByPassenger" forKey:@"method"]; [dic
	 * setObject:[NSNumber numberWithInteger:2] forKey:@"souce"]; [dic
	 * setObject:[NSNumber numberWithInteger:info.cancelType] forKey:@"type"];
	 * [dic setObject:info.bookID forKey:@"bizId"]; [dic setObject:info.taxiID
	 * forKey:@"taxiId"]; [dic setObject:[NSNumber numberWithInteger:info.value]
	 * forKey:@"value"];
	 */

	public final static void cancelBook(final long bookId, final long taxiId, final String passengerId, final Callback<Object> callback) {
		final Handler handler = new Handler(ETApp.getInstance().getMainLooper()) {
			public void handleMessage(Message msg) {
				JSONObject markJson = null;
				switch (msg.arg1) {
				case SUCESS:
					markJson = (JSONObject) msg.obj;
					break;
				case FAILED:

					markJson = null;
				default:
					break;
				}
				if (callback != null) {
					callback.handle(markJson);
				}
			}
		};

		new Thread(new Runnable() {
			public void run() {
				// ȡ������
				AppLog.LogD(tag, "cancelBook ������ ");
				Message message = Message.obtain();
				JsonObject json = new JsonObject();
				json.addProperty("action", "scheduleAction");
				json.addProperty("method", "cancelBookByPassenger");
				json.addProperty("reason", 1024);
				json.addProperty("bookId", bookId);
				json.addProperty("passengerId", passengerId);

				message.what = 1;
				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {

						String rs = new String(response, "UTF-8");
						// {"error":0,"bookId":1364286932656,"timeout":250000}

						JSONObject retJson = new JSONObject(rs);
						int error = retJson.getInt("error");

						if (error == 0) {
							message.arg1 = SUCESS;
							message.obj = retJson;
							AppLog.LogD(tag, "ȡ������--- " + retJson.toString());
						} else {
							message.arg1 = FAILED;
						}

						handler.sendMessage(message);
					} else {
						message.arg1 = FAILED;
						AppLog.LogD(tag, "ȡ�������� ʧ�� --1- ");
						handler.sendMessage(message);

					}
				} catch (Exception e) {
					e.printStackTrace();
					message.arg1 = FAILED;
					AppLog.LogD(tag, "ȡ������ --2- ");
					handler.sendMessage(message);
				}
			}
		}).start();
	}

	public final static void regNewUser(final String name, final String phone, final long id, final int sex, final Callback<Object> callback) {
		final Handler handler = new Handler(ETApp.getInstance().getMainLooper()) {
			public void handleMessage(Message msg) {
				JSONObject markJson = null;
				switch (msg.arg1) {
				case SUCESS:
					markJson = (JSONObject) msg.obj;
					break;
				case FAILED:

					markJson = null;
				default:
					break;
				}
				if (callback != null) {
					callback.handle(markJson);
					callback.complete();
					;
				}
			}
		};

		new Thread(new Runnable() {
			public void run() {
				// ȡ������
				AppLog.LogD(tag, "regNewUser ������ ");
				Message message = Message.obtain();

				JsonObject json = new JsonObject();
				json.addProperty("action", "userAction");
				json.addProperty("method", "regPassenger");

				json.addProperty("name", name);
				json.addProperty("phone", phone); //

				json.addProperty("id", id);//
				json.addProperty("sex", sex);// ����

				json.addProperty("cityId", MainActivityNew.cityId);
				json.addProperty("cityName", MainActivityNew.currentCityName);
				json.addProperty("clientType", BookConfig.ClientType.CLIENT_TYPE_PASSENGER);
				message.what = 1;
				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {

						String rs = new String(response, "UTF-8");
						// {"error":0,"bookId":1364286932656,"timeout":250000}
						AppLog.LogD(tag, " --- " + rs);
						JSONObject retJson = new JSONObject(rs);
						int error = retJson.getInt("error");

						if (error == 0) {
							message.arg1 = SUCESS;
							message.obj = retJson;
							AppLog.LogD(tag, "ע��ɹ�-- " + retJson.toString());
						} else {
							message.arg1 = FAILED;
						}

						handler.sendMessage(message);
					} else {
						message.arg1 = FAILED;
						AppLog.LogD(tag, "ע��ʧ�� --1- ");
						handler.sendMessage(message);

					}
				} catch (Exception e) {
					e.printStackTrace();
					message.arg1 = FAILED;
					AppLog.LogD(tag, "ע��ʧ�� --2- ");
					handler.sendMessage(message);
				}
			}
		}).start();
	}

	/**
	 * 
	 * @param name
	 * @param phone
	 * @param id
	 * @param sex
	 * @param callback
	 */
	public final static void check(final int version, final String cityId, String passengerId, final Callback<Object> callback) {

		JsonObject json = new JsonObject();
		json.addProperty("action", "versionAction");
		json.addProperty("method", "updateClient");
		json.addProperty("cityId", cityId);

		// 1.android�˿Ͷ� 2. android˾���ˣ�3.android Pad�Ƶ��
		json.addProperty("clientVersion", version); // �汾��
		json.addProperty("type", 1);

		json.addProperty("id", passengerId);// �绰��
		json.addProperty("cityId", MainActivityNew.cityId);
		json.addProperty("cityName", MainActivityNew.currentCityName);
		json.addProperty("clientType", BookConfig.ClientType.CLIENT_TYPE_PASSENGER);

		AppLog.LogD(tag, " --�������- " + json.toString());
		// response != null && response.length > 0
		try {
			byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
			if (response != null && response.length > 0) {

				String rs = new String(response, "UTF-8");
				AppLog.LogD(tag, " -������� ���-- " + rs);
				JSONObject retJson = new JSONObject(rs);
				int error = retJson.getInt("error");

				if (error == 0) {
					// message.obj = retJson;
					if (callback != null)
						callback.handle(retJson);

				} else {
					// erro != 0 ������
					if (callback != null)
						callback.handle(null);
				}
			} else {

				AppLog.LogD(tag, "������� ���ʧ�� --1- ");
				// handler.sendMessage(message);
				if (callback != null)
					callback.handle(null);

			}
		} catch (Exception e) {
			e.printStackTrace();

			AppLog.LogD(tag, "������� ���ʧ�� --2- ");
			if (callback != null)
				callback.handle(null);
		}

	}

	public final static void getNearbyTaxis(final String mobile, final int lat, final int lng, final Callback<Object> callback, final String cityId) {

		final Handler handler = new Handler(ETApp.getInstance().getMainLooper()) {
			public void handleMessage(Message msg) {
				JSONObject markJson = null;
				switch (msg.arg1) {
				case SUCESS:
					markJson = (JSONObject) msg.obj;
					break;
				case FAILED:

					markJson = null;
				default:
					break;
				}
				if (callback != null) {
					callback.handle(markJson);
				}
			}
		};

		Thread t = new Thread(new Runnable() {
			public void run() {

				Message message = Message.obtain();

				AppLog.LogD(tag, "getNearbyTaxis ������ ");
				JsonObject json = new JsonObject();
				json.addProperty("action", "locationAction");
				json.addProperty("method", "getNearbyTaxis1");

				// 1.android�˿Ͷ� 2. android˾���ˣ�3.android Pad�Ƶ��
				json.addProperty("latitude", lat); // �汾��
				json.addProperty("longitude", lng); // �汾��
				json.addProperty("cityId", cityId); // �汾��

				// 2013-0513�ӿڱ䶯Ϊ getNearbyTaxis1

				// AppLog.LogD(tag, " --��ȡ��Χ��---=====- " + json.toString());

				message.what = 1;
				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {

						String rs = new String(response, "UTF-8");

						// {"error":0,"bookId":1364286932656,"timeout":250000}
						// System.gc();
						JSONObject retJson = new JSONObject(rs);
						int error = retJson.getInt("error");

						if (error == 0) {
							message.arg1 = SUCESS;
							message.obj = retJson;
							AppLog.LogD(tag, "��ȡ��Χ���ɹ�-- " + retJson.toString());
						} else {
							message.arg1 = FAILED;
						}

						handler.sendMessageDelayed(message, 50);
					} else {
						message.arg1 = FAILED;
						AppLog.LogD(tag, "��ȡ��Χ��ʧ�� --1- ");
						handler.sendMessageDelayed(message, 50);

					}
				} catch (Exception e) {
					e.printStackTrace();
					message.arg1 = FAILED;
					AppLog.LogD(tag, "��ȡ��Χ�� --2- ");
					handler.sendMessageDelayed(message, 50);
				}
			}
		});
		t.start();

	}

	public final static void getTaxiLocation(final long taxiId, final Callback<JSONObject> callback) {

		final Handler handler = new Handler(ETApp.getInstance().getMainLooper()) {
			public void handleMessage(Message msg) {
				JSONObject markJson = null;
				switch (msg.arg1) {
				case SUCESS:
					markJson = (JSONObject) msg.obj;
					break;
				case FAILED:

					markJson = null;
				default:
					break;
				}
				if (callback != null) {
					callback.handle(markJson);
				}
			}
		};

		new Thread(new Runnable() {
			public void run() {

				Message message = Message.obtain();

				AppLog.LogD(tag, "getTaxiLocation ������ ");
				JsonObject json = new JsonObject();
				json.addProperty("action", "locationAction");
				json.addProperty("method", "getTaxiLocation");
				json.addProperty("taxiId", taxiId);

				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {

						String rs = new String(response, "UTF-8");
						// {"error":0,"bookId":1364286932656,"timeout":250000}
						AppLog.LogD(tag, " --��ȡ����λ��--- " + rs);
						JSONObject retJson = new JSONObject(rs);
						int error = retJson.getInt("error");

						if (error == 0) {
							message.arg1 = SUCESS;
							message.obj = retJson;

						} else {
							message.arg1 = FAILED;
						}

						handler.sendMessage(message);
					} else {
						message.arg1 = FAILED;
						AppLog.LogD(tag, "��ȡ����λ�� --1- ");
						handler.sendMessage(message);

					}
				} catch (Exception e) {
					e.printStackTrace();
					message.arg1 = FAILED;
					AppLog.LogD(tag, "��ȡ����λ�� --2- ");
					handler.sendMessage(message);
				}
			}
		}).start();

	}

	public final static void getCityByLocation(final int lat, final int lng, final Callback<String> callback) {

		new Thread(new Runnable() {
			public void run() {

				JsonObject json = new JsonObject();
				json.addProperty("action", "geoCodingAction");
				json.addProperty("method", "getCityByLocation");

				json.addProperty("lat", lat); //
				json.addProperty("lng", lng); //

				AppLog.LogD(json.toString());

				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {

						String retJson = new String(response, "UTF-8");
						AppLog.LogD(tag, "��ȡ���гɹ� - - " + retJson);
						// Intent intent = new
						// Intent(CityTool.ACTION_RESPONSE_GETCITY);
						// intent.putExtra("city", retJson);
						if (callback != null) {

							callback.handle(retJson);
						}
						// context.sendBroadcast(intent);

					} else {

						AppLog.LogD(tag, "��ȡ������Ϣʧ�� --1- ");

					}
				} catch (Exception e) {
					e.printStackTrace();

					AppLog.LogD(tag, "��ȡ����Ϣ --2- ");

				}
			}
		}).start();

	}

	/**
	 * 
	 * @param sendType
	 *            1 �Ƽ���˾�� ��0 �Ƽ����˿�
	 * @param handler
	 * @param cityId
	 * @param passengerId
	 * @param phone
	 * @param callback
	 */
	public final static void sendRmdInfoRequest(final int sendType, final Handler handler, final String cityId, final String passengerId, final List<ContactEntity> phones) {

		new Thread(new Runnable() {
			public void run() {
				JSONObject json = new JSONObject();
				// JsonObject json = new JsonObject();

				try {
					json.put("action", "recommendAction");

					// 0�˿ͶԽ�˾��
					if (sendType == 0) {
						json.put("method", "passengerToPassenger");
					} else {
						json.put("method", "passengerToDriver");
					}

					json.put("cityId", cityId); //
					json.put("passengerId", passengerId); //
					// json.addProperty("phones", ja); //
					JSONArray ja = new JSONArray();

					for (ContactEntity ce : phones) {
						JSONObject js = new JSONObject();
						if (ce != null) {

							js.put("name", ce.name);
							js.put("phone", ce.phone);
							ja.put(js);
						}

					}
					json.put("phones", ja);
					json.put("cityName", MainActivityNew.currentCityName);
					json.put("clientType", BookConfig.ClientType.CLIENT_TYPE_PASSENGER);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

				AppLog.LogD(json.toString());
				Message msg = Message.obtain();
				msg.arg1 = 100;
				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {

						String retJson = new String(response, "UTF-8");

						msg.obj = retJson;
						if (handler != null) {
							handler.sendMessage(msg);
						}
						// context.sendBroadcast(intent);
					} else {
						if (handler != null) {
							msg.obj = null;
							handler.sendMessage(msg);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (handler != null) {
						msg.obj = null;
						handler.sendMessage(msg);
					}
				}
			}
		}).start();

	}

	/**
	 * 
	 * @param type
	 *            1.�˿��Ƽ��˿ͣ�2.�˿��Ƽ�˾����//3��˾���Ƽ�˾����4��˾���Ƽ��˿�
	 * @param handler
	 * @param cityId
	 * @param passengerId
	 * @param phone
	 */
	public final static void sendRmdContentRequest(final int type, final Handler handler, final String cityId) {

		new Thread(new Runnable() {
			public void run() {

				JsonObject json = new JsonObject();
				json.addProperty("action", "recommendAction");
				json.addProperty("method", "getRecommendMessage");
				json.addProperty("type", type);
				json.addProperty("cityId", cityId); //

				AppLog.LogD(json.toString());
				Message msg = Message.obtain();
				msg.arg1 = 101;
				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {

						String retJson = new String(response, "UTF-8");
						AppLog.LogD(retJson);

						msg.obj = retJson;
						if (handler != null) {
							handler.sendMessage(msg);
						}
						// context.sendBroadcast(intent);
					} else {
						AppLog.LogD("����ֵΪnull");
						if (handler != null) {
							msg.obj = null;
							handler.sendMessage(msg);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (handler != null) {
						msg.obj = null;
						handler.sendMessage(msg);
					}
				}
			}
		}).start();

	}

	public static void getOnLine(final long lastTime, final long dTime, final long id, final Callback<JSONObject> callBack) {

		JSONObject json = new JSONObject();

		try {
			json.put("action", "taxiAction");
			json.put("method", "getOnlineTaxiCount");

		} catch (Exception e) {

		}

		new AsyncTask<JSONObject, Object, String>() {

			@Override
			protected String doInBackground(JSONObject... params) {

				byte[] buf = null;
				try {
					buf = TcpClient.send(id, MsgConst.MSG_TCP_ACTION, params[0].toString().getBytes("utf-8"));

					String info = new String(buf, "utf-8");

					long timeout = dTime - (System.currentTimeMillis() - lastTime);
					AppLog.LogD(tag, "timeout---------- :" + timeout);

					if (timeout > 0) {
						TimeUnit.MILLISECONDS.sleep(timeout);
					}
					AppLog.LogD(tag, info);
					return info;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				// AppLog.LogD(tag, result);
				try {
					callBack.handle(new JSONObject(result));
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					callBack.error(e);
				}

				callBack.complete();
			}

		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, json);

	}

	/**
	 * ���ƽ̨�汾�Ƿ��и��£�����У��͸��°�װ
	 * 
	 * @param isInfo
	 *            �ڼ�鵽û���°汾��ʱ���Ƿ���ʾ�û�
	 */
	@SuppressLint("NewApi")
	public static void checkUpdate(final Context context, final String cityId, final int version, final boolean isInfo, final String passengerId, final Callback<String> newVersion) {
		// Toast.makeText(context, "���ڼ��汾", Toast.LENGTH_LONG).show();

		new AsyncTask<Object, JSONObject, Object>() {
			@Override
			protected void onProgressUpdate(final JSONObject... values) {

				if (values == null) {// ������
					newVersion.handle("current");
					return;
				}

				try {
					final boolean force = values[0].getBoolean("force");
					final String mark = values[0].getString("mark");
					String pathTmp = "";
					if (values[0].isNull("path")) {

					} else {
						pathTmp = values[0].getString("path");
					}

					final String path = pathTmp;

					final String version = values[0].getString("version");
					// newVersion.handle(version);

					long netVersion = Long.valueOf(version);
					long currentVersion = ETApp.getInstance().getMobileInfo().getVerisonCode();

					if (netVersion <= currentVersion) {
						newVersion.handle("current");
					} else {
						Window.confirm(force, context.getApplicationContext(), "�����°汾", mark, new Callback<Object>() {

							@Override
							public void handle(Object param) {

								Intent intent = new Intent();
								intent.setAction("android.intent.action.VIEW");
								Uri content_url = Uri.parse(path);
								intent.setData(content_url);
								context.startActivity(intent);
								System.exit(1);
							}
						}, new Callback<Object>() {

							@Override
							public void handle(Object param) {
								// TODO Auto-generated method stub
								// System.exit(1);
							}
						}, "��������", "�Ժ�����");
					}

				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

			@Override
			protected Object doInBackground(Object... params) {
				NewNetworkRequest.check(version, cityId, passengerId, new Callback<Object>() {
					@Override
					public void handle(Object param) {
						if (param != null) {
							try {
								publishProgress((JSONObject) param);
							} catch (Throwable e) {
								e.printStackTrace();
							}
						} else {// ������
							publishProgress(null);
						}
					}
				});
				return null;
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, version);
	}

	/**
	 * ��ȡ�û����Ƽ���ʷ��Ϣ �����Ѿ��Ƽ���100��
	 * 
	 * @param handler
	 * @param passengerId
	 * @param cityId
	 */
	public static void sendRmdMyinfoRequest(final Handler handler, final String passengerId, final String cityId) {

		new Thread(new Runnable() {
			@SuppressLint("NewApi")
			public void run() {

				JsonObject json = new JsonObject();
				json.addProperty("action", "recommendAction");
				json.addProperty("method", "getRecommendResult");
				json.addProperty("passengerId", passengerId);
				json.addProperty("cityId", cityId); //
				json.addProperty("type", 2); //

				AppLog.LogD(json.toString());
				Message msg = Message.obtain();
				msg.arg1 = 102;
				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {

						String retJson = new String(response, "UTF-8");
						AppLog.LogD(retJson);

						msg.obj = retJson;
						if (handler != null) {
							handler.sendMessage(msg);
						}
						// context.sendBroadcast(intent);
					} else {
						AppLog.LogD("����ֵΪnull -- sendRmdMyinfoRequest");
						if (handler != null) {
							msg.obj = null;
							handler.sendMessage(msg);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (handler != null) {
						msg.obj = null;
						handler.sendMessage(msg);
					}
				}
			}
		}).start();
	}

	/**
	 * �ϴ��绰����
	 * 
	 * @param handler
	 * @param passengerId
	 * @param cityId
	 * @param phones
	 */
	public static void uploadPhones(final Handler handler, final String passengerId, final String cityId, final List<ContactEntity> phones) {

		new Thread(new Runnable() {
			public void run() {

				JsonObject json = new JsonObject();
				json.addProperty("action", "recommendAction");
				json.addProperty("method", "upPhones");
				json.addProperty("passengerId", passengerId);
				json.addProperty("cityId", cityId); //
				//

				JsonArray ja = new JsonArray();

				for (ContactEntity ce : phones) {
					JsonObject js = new JsonObject();
					js.addProperty("name", ce.name);
					js.addProperty("phone", ce.phone);
					ja.add(js);
				}
				json.add("phones", ja);

				// Log.d("tag", json.toString());
				AppLog.LogD(json.toString());
				Message msg = Message.obtain();
				msg.arg1 = 200;
				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {

						String retJson = new String(response, "UTF-8");
						// Log.d("tag", retJson);

						msg.obj = retJson;
						if (handler != null) {
							handler.sendMessage(msg);
						}
						// context.sendBroadcast(intent);
					} else {
						AppLog.LogD("����ֵΪnull -- sendRmdMyinfoRequest");
						if (handler != null) {
							msg.obj = null;
							handler.sendMessage(msg);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (handler != null) {
						msg.obj = null;
						handler.sendMessage(msg);
					}
				}
			}
		}).start();
	}

	/**
	 * ��ȡ�����ࡱ�Ĳ˵�
	 * 
	 * @param handler
	 * @param cityId
	 * @param passengerId
	 */
	public static void getMoreMenus(final Handler handler, final String cityId, final String passengerId) {
		new Thread(new Runnable() {
			public void run() {

				JsonObject json = new JsonObject();
				json.addProperty("action", "proxyAction");
				json.addProperty("method", "query");
				json.addProperty("passengerId", passengerId);
				json.addProperty("cityId", cityId); //
				json.addProperty("clientVersion", String.valueOf(ETApp.getInstance().getMobileInfo().getVerisonCode())); //

				json.addProperty("op", "getMoreMenus"); //

				AppLog.LogD("��ȡ�����ࡱ " + json.toString());
				Message msg = Message.obtain();
				msg.what = MoreActivity.MENU_FROM_NET_OK;
				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {

						String retJson = new String(response, "UTF-8");
						AppLog.LogD("tag", retJson);

						msg.obj = retJson;
						if (handler != null) {
							handler.sendMessage(msg);
						}
						// context.sendBroadcast(intent);
					} else {
						AppLog.LogD("����ֵΪnull -- sendRmdMyinfoRequest");
						if (handler != null) {
							msg.obj = null;
							handler.sendMessage(msg);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (handler != null) {
						msg.obj = null;
						handler.sendMessage(msg);
					}
				}
			}
		}).start();

	}

	/**
	 * ������鷴��
	 * 
	 * @param handler
	 * @param cityId
	 *            /** ��������: 1.��ʱ�򳵽��� 2.��ͨͶ�ߺͽ��� 3.Ͷ��˾�� 4.Ͷ�߿ͷ� 5.������� 6.������ѯ
	 * 
	 * @param passengerId
	 */
	public static void feedBack(final Handler handler, final String cityId, final String passengerId, final String info, final int type) {
		new Thread(new Runnable() {
			public void run() {

				JsonObject json = new JsonObject();
				json.addProperty("action", "suggestAction");
				json.addProperty("method", "suggestApp");
				json.addProperty("objectId", Long.valueOf(passengerId));
				json.addProperty("_SUGGESTER_ID", Long.valueOf(passengerId));
				json.addProperty("_USER_ID", 0);

				json.addProperty("cityId", Integer.valueOf(cityId)); //
				json.addProperty("content", info); //
				json.addProperty("type", type); //
				json.addProperty("op", "suggest"); //
				// json.addProperty("objectType", 2); //

				AppLog.LogD("������鷴�� " + json.toString());
				Message msg = Message.obtain();
				msg.what = SuggestionActivity.FEEDBACK_OK;
				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {

						String retJson = new String(response, "UTF-8");
						// Log.d("tag", retJson);
						AppLog.LogD("������鷴�� ���" + retJson.toString());
						msg.obj = retJson;
						if (handler != null) {
							handler.sendMessage(msg);
						}
						// context.sendBroadcast(intent);
					} else {
						AppLog.LogD("����ֵΪnull -- sendRmdMyinfoRequest");
						if (handler != null) {
							msg.obj = null;
							handler.sendMessage(msg);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (handler != null) {
						msg.obj = null;
						handler.sendMessage(msg);
					}
				}
			}
		}).start();

	}

	/**
	 * ���� /** ��������: 1.��ʱ�򳵽��� 2.��ͨͶ�ߺͽ��� 3.Ͷ��˾�� 4.Ͷ�߿ͷ� 5.������� 6.������ѯ 7.��������
	 * 
	 * @param handler
	 * @param bookId
	 * @param cityId
	 * @param passengerId
	 * @param taxiId
	 * @param info
	 * @param value
	 * @param type
	 * @return void
	 */
	public static void suggest(final Handler handler, final int bookId, final String cityId, final String passengerId, final Long taxiId, final String info, final int value, final int type) {
		new Thread(new Runnable() {
			public void run() {

				JsonObject json = new JsonObject();
				json.addProperty("action", "proxyAction");
				json.addProperty("method", "query");
				json.addProperty("_SUGGESTER_ID", Long.valueOf(passengerId));
				json.addProperty("_USER_ID", taxiId);

				json.addProperty("cityId", Integer.valueOf(cityId)); //
				json.addProperty("content", info); //
				json.addProperty("type", type); //
				json.addProperty("bookId", bookId);
				json.addProperty("op", "suggestByBookId"); //
				json.addProperty("evaluate", value); //
				// json.addProperty("objectType", 2); //

				AppLog.LogD("���� " + json.toString());
				Message msg = Message.obtain();
				msg.what = SuggestionActivity.FEEDBACK_OK;
				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {

						String retJson = new String(response, "UTF-8");
						// Log.d("tag", retJson);
						AppLog.LogD("���� ���" + retJson.toString());
						msg.obj = retJson;
						if (handler != null) {
							handler.sendMessage(msg);
						}
						// context.sendBroadcast(intent);
					} else {
						AppLog.LogD("����ֵΪnull -- sendRmdMyinfoRequest");
						if (handler != null) {
							msg.obj = null;
							handler.sendMessage(msg);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (handler != null) {
						msg.obj = null;
						handler.sendMessage(msg);
					}
				}
			}
		}).start();

	}

	/**
	 * �µ����۷���
	 * 
	 * @param handler
	 * @param bookId
	 *            BookBean��getId()
	 * @param taxiId
	 *            BookBean��getReplyerId()
	 * @param value
	 *            ����
	 * @deprecated
	 */
	public static void suggest(final Handler handler, final Long bookId, final Long taxiId, final int value) {
		new Thread(new Runnable() {
			public void run() {
				JsonObject json = new JsonObject();
				json.addProperty("action", "suggestAction");
				json.addProperty("method", "suggestByPassenger");

				json.addProperty("souce", 0); // 0 ƽ̨����
				json.addProperty("bookId", bookId); // BookBean��getId()
				json.addProperty("taxiId", taxiId);// BookBean��getReplyerId()
				json.addProperty("value", value);// ����

				AppLog.LogD("���� " + json.toString());
				Message msg = Message.obtain();
				msg.what = SuggestionActivity.FEEDBACK_OK;
				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {

						String retJson = new String(response, "UTF-8");
						// Log.d("tag", retJson);
						AppLog.LogD("���� ���" + retJson.toString());
						msg.obj = retJson;
						msg.arg1 = value;// ����
						if (handler != null) {
							handler.sendMessage(msg);
						}
						// context.sendBroadcast(intent);
					} else {
						AppLog.LogD("����ֵΪnull -- sendRmdMyinfoRequest");
						if (handler != null) {
							msg.obj = null;
							msg.arg1 = -1;
							handler.sendMessage(msg);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (handler != null) {
						msg.obj = null;
						msg.arg1 = -1;
						handler.sendMessage(msg);
					}
				}
			}
		}).start();

	}

	/**
	 * ��ȡ�û�������Ϣ ��profile info��
	 * 
	 * @param handler
	 * @param cityId
	 * @param passengerId
	 */
	public static void requestUserfileInfo(final Handler handler, final String cityId, final String passengerId) {
		new Thread(new Runnable() {
			public void run() {

				JsonObject json = new JsonObject();
				json.addProperty("action", "proxyAction");
				json.addProperty("method", "query");
				json.addProperty("cityId", cityId);
				json.addProperty("passengerId", passengerId);
				json.addProperty("op", "getStatByPassengerId"); //

				AppLog.LogD("��ȡ�û���Ϣ " + json.toString());
				Message msg = Message.obtain();
				msg.what = USER_PROFILE;

				// if (isMock) {
				// YDUserProfile a = new YDUserProfile();
				// a.setBadCommentCounts(100);
				// a.setGoodCommentCounts(0);
				// a.setMoney(1000);
				// a.setRank(100);
				// a.setRmdPassengerCounts(18);
				// a.setRmdTaxiCounts(0);
				// a.setSendBookCounts(100);
				// a.setWeiyueCounts(9);
				// msg.obj = a;
				// if (handler != null) {
				// handler.sendMessage(msg);
				// }
				//
				// return;
				// }

				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {

						String retJson = new String(response, "UTF-8");
						// Log.d("tag", retJson);
						AppLog.LogD("��ȡ�û���Ϣ���" + retJson.toString());
						msg.obj = retJson;
						if (handler != null) {
							handler.sendMessage(msg);
						}
						// context.sendBroadcast(intent);
					} else {
						AppLog.LogD("����ֵΪnull ");
						if (handler != null) {
							msg.obj = null;
							handler.sendMessage(msg);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (handler != null) {
						msg.obj = null;
						handler.sendMessage(msg);
					}
				}
			}
		}).start();

	}

	/**
	 * ��ȡ������Ϣ
	 * 
	 * @param handler
	 * @param cityId
	 * @param passengerId
	 * @param counts
	 * @param startId
	 * @param order
	 */
	public static void requestUserComments(final Handler handler, final int action, final String cityId, final String passengerId, final int counts, final int startId, final String order) {

		new Thread(new Runnable() {
			public void run() {

				JsonObject json = new JsonObject();
				json.addProperty("action", "proxyAction");
				json.addProperty("method", "query");
				json.addProperty("cityId", cityId);
				json.addProperty("objectId", passengerId);
				json.addProperty("type", 1 | 7);
				json.addProperty("op", "getEvaluateList"); //
				// json.addProperty("pageCounts", counts); //
				// json.addProperty("pageCounts", counts); //
				json.addProperty("orderName", "_ID"); //
				json.addProperty("startId", startId); //
				json.addProperty("order", order); //

				AppLog.LogD("��ȡ������Ϣ " + json.toString());

				Message msg = Message.obtain();
				msg.what = USER_COMMENTS;
				msg.arg1 = action;

				// if (isMock) {
				// YDUserComments a = new YDUserComments();
				// if (action == UserProfileActivity.LISTVIEW_ACTION_INIT) {
				// a = new YDUserComments();
				// a.mockData(counts, startId);
				// } else if (action ==
				// UserProfileActivity.LISTVIEW_ACTION_REFRESH) {
				// a = new YDUserComments();
				// a.mockData(counts, startId);
				// } else if (action ==
				// UserProfileActivity.LISTVIEW_ACTION_SCROLL) {
				// a = new YDUserComments();
				// a.mockData(counts, startId);
				// }
				//
				// msg.obj = a.comments;
				// if (handler != null) {
				// handler.sendMessage(msg);
				// }
				//
				// return;
				// }

				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {

						String retJson = new String(response, "UTF-8");
						// Log.d("tag", retJson);
						AppLog.LogD("��ȡ������Ϣ��� ----- " + retJson);
						Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
						YDUserComments yc = gson.fromJson(retJson, YDUserComments.class);
						msg.obj = yc.comments;
						if (handler != null) {
							handler.sendMessage(msg);
						}
						// context.sendBroadcast(intent);
					} else {
						AppLog.LogD("����ֵΪnull ");
						if (handler != null) {
							msg.obj = null;
							handler.sendMessage(msg);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (handler != null) {
						msg.obj = null;
						handler.sendMessage(msg);
					}
				}
			}
		}).start();

	}

	/**
	 * ��ȡ��ʷ��Ϣ<getMessageList>
	 * 
	 * @param handler
	 * @param passengerId
	 * @param cityId
	 * @param start
	 */
	public static void requestMsgListRemote(final Handler handler, final String passengerId, final String cityId, final int start) {

		new Thread(new Runnable() {
			public void run() {

				List<MsgBean> result = null;

				/*
				 * param.put("method", "query"); param.put("op",
				 * "getMessageList"); param.put("cityId",
				 * TaxiState.Driver.cityId); param.put("objectId",
				 * TaxiState.Driver.id); param.put("objectType", 1);
				 * param.put("startId", start); param.put("order", "asc");
				 * param.put("orderType", "_ID");
				 */
				JsonObject json = new JsonObject();
				json.addProperty("action", "proxyAction");
				json.addProperty("method", "query");
				json.addProperty("cityId", Integer.valueOf(cityId));
				json.addProperty("objectId", Long.valueOf(passengerId));
				json.addProperty("op", "getMessageList"); //
				json.addProperty("startId", start); //
				// json.addProperty("orderName", passengerId); //
				json.addProperty("order", "desc");
				json.addProperty("objectType", 2); // �������ͣ�1.˾����2. �˿�

				AppLog.LogD("��ȡ��ʷ��Ϣ " + json.toString());

				Message msg = Message.obtain();
				// msg.what = MSG_HISTORY_SHORT_MSG_REMOTE;

				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {

						String retJson = new String(response, "UTF-8");

						AppLog.LogD("��ȡ��ʷ��Ϣ���" + retJson);

						JSONObject job = new JSONObject(retJson);

						if (job.getInt("error") == 0) {
							Gson gson = new Gson();
							Type type = new TypeToken<List<MsgBean>>() {
							}.getType();
							result = gson.fromJson(job.getString("datas"), type);
						} else {

						}

						msg.obj = result;
						if (handler != null) {
							handler.sendMessage(msg);
						}
						// context.sendBroadcast(intent);
					} else {
						AppLog.LogD("����ֵΪnull ");
						if (handler != null) {
							msg.obj = null;
							handler.sendMessage(msg);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (handler != null) {
						msg.obj = null;
						handler.sendMessage(msg);
					}
				}
			}
		}).start();
	}

	// �°���ȷ�
	public static class TipPrices {
		public int error;
		public String errormsg;
		public int onlinePayment;
		public int[] priceList;
		public String msg;
	}

	/**
	 * * �°��ȡ���ȷ��б������ȷ���ʾmsg
	 * 
	 * @param cityId
	 * @param bookType
	 *            �°漰ʱ������1���ɰ漰ʱ������2��IOS1.5ԤԼ������3���ɰ�ԤԼ������4��
	 *            Android�°�ԤԼ������5�����ۣ�6�����ͻ���7��
	 * 
	 * 
	 * 
	 * @param clientType
	 *            �Ƶ��̶�Ϊ��pad
	 * @param callback
	 * @return void
	 */
	public static synchronized void getDiaoDuPriceList(final int cityId, final int bookType, final String clientType, final Callback<Object> callback) {

		final Handler handlerPrice = new Handler(ETApp.getInstance().getMainLooper()) {
			public void handleMessage(Message msg) {

				AppLog.LogD(tag, "getDiaoDuPriceList ---- ");

				TipPrices prices = null;
				switch (msg.arg1) {

				case FAILED:
					break;

				// ��ʱ�ٳ�
				case Const.JISHI_BOOK_TYPE:
					prices = (TipPrices) msg.obj;
					if (callback != null) {
						callback.handle(prices);
					}
					break;

				// ԤԼ����
				case Const.BOOK_BOOK_TYPE:
					prices = (TipPrices) msg.obj;
					if (callback != null) {
						callback.handle(prices);
					}
					break;
				// �ӻ��ͻ�
				case Const.AIRPORT_BOOK_TYPE:
					prices = (TipPrices) msg.obj;
					if (callback != null) {
						callback.handle(prices);
					}
					break;
				// ����
				case Const.DRIVINT_ORDER_BOOK_TYPE:
					prices = (TipPrices) msg.obj;
					if (callback != null) {
						callback.handle(prices);
					}
					break;
				default:
					break;
				}

			}
		};

		new Thread(new Runnable() {
			public void run() {
				// ���ص��Ƕ�����ID
				AppLog.LogD(tag, "��ʼ���ݵ��ȷ���Ϣ ������ ");
				Message message = Message.obtain();
				Gson gson = new Gson();
				JsonObject json = new JsonObject();
				// json.addProperty("action", "proxyAction");
				// json.addProperty("method", "query");
				// json.addProperty("op", "getPriceList");
				//
				// json.addProperty("bookType", bookType);
				// json.addProperty("cityId", cityId);
				// json.addProperty("clientType", clientType);
				// �µļ۸��б�Ľӿ�
				json.addProperty("action", "billAction");
				json.addProperty("method", "getOrderPaymentList");
				json.addProperty("cityId", cityId);
				json.addProperty("bookType", bookType);
				json.addProperty("clientType", "" + clientType);
				json.addProperty("clientVersion", "" + ETApp.getInstance().getMobileInfo().getVerisonCode());
				message.what = 1;

				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {
						String retString = new String(response, "utf-8");
						AppLog.LogD("xyw", "price list:" + retString);
						TipPrices tips = new TipPrices();
						JSONObject js = new JSONObject(retString);
						tips.error = js.getInt("error");
						tips.errormsg = js.getString("errormsg");

						if (tips.error == 0) {
							tips.onlinePayment = js.getJSONObject("datas").getInt("onlinePayment");
							tips.msg = js.getJSONObject("datas").getString("msg");
							JSONArray jsArray = js.getJSONObject("datas").getJSONArray("priceList");
							int length = jsArray.length();
							tips.priceList = new int[length];
							for (int i = 0; i < length; i++) {
								tips.priceList[i] = jsArray.getInt(i);
							}

							if (bookType == Const.JISHI_BOOK_TYPE) {
								if (tips.priceList.length > 0) {
									ETApp.getInstance().saveCacheString(Const.JISHI_PRICELIST, retString);
								}
							} else if (bookType == Const.BOOK_BOOK_TYPE) {
								if (tips.priceList.length > 0) {
									ETApp.getInstance().saveCacheString(Const.BOOK_PRICELIST, retString);
								}
							} else if (bookType == Const.AIRPORT_BOOK_TYPE) {
								if (tips.priceList.length > 0) {
									ETApp.getInstance().saveCacheString(Const.AIRPORT_PRICELIST, retString);
								}
							} else if (bookType == Const.DRIVINT_ORDER_BOOK_TYPE) {
								if (tips.priceList.length > 0) {
									ETApp.getInstance().saveCacheString(Const.DRIVING_ORDER_PRICELIST, retString);
								}
							}
							// ����֧�����
							message.arg1 = bookType;
							message.obj = tips;
							handlerPrice.sendMessage(message);
						} else {
							message.arg1 = bookType;
							message.obj = tips;
							AppLog.LogD(tag, "���ȡ�ݵ��ȷ� ʧ�� --0- ");
							handlerPrice.sendMessage(message);
						}

					} else {
						message.arg1 = FAILED;
						message.obj = null;
						AppLog.LogD(tag, "���ȡ�ݵ��ȷ� ʧ�� --1- ");
						handlerPrice.sendMessage(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
					message.arg1 = FAILED;
					message.obj = null;
					AppLog.LogD(tag, "���ȡ�ݵ��ȷ� ʧ�� --2- ");
					handlerPrice.sendMessage(message);
				}
			}
		}).start();

	}

	/**
	 * ʱ��������
	 */
	public static class TimeLines {
		public int error;
		public int count;
		public ArrayList<TimeLine> datas;
	}

	public static class TimeLine {
		/**
		 * ����ʱ������,�����ó�ʱ�䲻�ܹ��������upperʱ�䣺��λΪ����
		 */
		public String upper;
		/**
		 * ����ʱ��,�����������ٶ�������lowerʱ���Ժ���ó�����λΪ����
		 */
		public String lower;
	}

	/**
	 * ��ȡʱ��������
	 */
	public static synchronized void getTimeDeadLine(final int cityId, final int bookType, final String clientType, final String clientVersion, final Callback<Object> callback) {

		final Handler handlerPrice = new Handler(ETApp.getInstance().getMainLooper()) {
			public void handleMessage(Message msg) {

				AppLog.LogD(tag, "getTimeDeadLine ---- ");

				TimeLines timeLines = null;
				switch (msg.arg1) {

				case FAILED:
					break;
				// ԤԼ����
				case Const.BOOK_BOOK_TYPE:
					timeLines = (TimeLines) msg.obj;
					if (callback != null) {
						callback.handle(timeLines);
					}
					break;
				// �ӻ��ͻ�
				case Const.AIRPORT_BOOK_TYPE:
					timeLines = (TimeLines) msg.obj;
					if (callback != null) {
						callback.handle(timeLines);
					}
					break;
				default:
					timeLines = (TimeLines) msg.obj;
					if (callback != null) {
						callback.handle(timeLines);
					}
					break;
				}

			}
		};

		new Thread(new Runnable() {
			public void run() {
				// ���ص��Ƕ�����ID
				AppLog.LogD(tag, "��ȡʱ�������� ������ ");

				Message message = Message.obtain();
				message.what = 1;
				// action proxyAction
				// method query
				// cityId Long ����ID
				// clientType String �ն�����
				// clientVersion String �ͻ��˰汾
				// op String getBookTimeInterval
				// bookType Long ���ͣ���ʾģ������
				JsonObject json = new JsonObject();
				json.addProperty("action", "proxyAction");
				json.addProperty("method", "query");
				json.addProperty("cityId", cityId);
				json.addProperty("clientType", clientType);
				json.addProperty("clientVersion", clientVersion);
				json.addProperty("op", "getBookTimeInterval");
				json.addProperty("bookType", bookType);

				Gson gson = new Gson();
				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {
						String retString = new String(response, "utf-8");

						AppLog.LogD("xyw", "time head and dead line:" + retString);

						TimeLines timeLines = gson.fromJson(retString, TimeLines.class);
						if (timeLines.error == 0) {
							/*
							 * if (bookType == Const.AIRPORT_BOOK_TYPE) {
							 * ETApp.getInstance
							 * ().saveCacheString(Const.AIRPORT_PRICELIST,
							 * retString); }
							 */
							message.arg1 = bookType;
							message.obj = timeLines;
							handlerPrice.sendMessage(message);
						} else {
							message.arg1 = FAILED;
							message.obj = null;
							AppLog.LogD(tag, "���ȡ�ݵ��ȷ� ʧ�� --0- ");
							handlerPrice.sendMessage(message);
						}

					} else {
						message.arg1 = FAILED;
						message.obj = null;
						AppLog.LogD(tag, "���ȡ�ݵ��ȷ� ʧ�� --1- ");
						handlerPrice.sendMessage(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
					message.arg1 = FAILED;
					message.obj = null;
					AppLog.LogD(tag, "���ȡ�ݵ��ȷ� ʧ�� --2- ");
					handlerPrice.sendMessage(message);
				}
			}
		}).start();

	}

	// ****************��־�ϴ�*******************
	/**
	 * �����ϴ���־��Url����
	 * 
	 */
	public static class LogUrlReturn {
		public int error;
		public int count;
		public ArrayList<LogUrl> datas;
	}

	public static class LogUrl {
		public String url;
	}

	/**
	 * �ӷ�������ȡ��־�ϴ�·��
	 * 
	 * @param cityId
	 *            ����Id
	 * @param callback
	 *            �ص�����
	 */
	public static synchronized void getUploadLogUrl(final int cityId, final Callback<Object> callback) {

		final Handler handler = new Handler(ETApp.getInstance().getMainLooper()) {
			public void handleMessage(Message msg) {

				LogUrlReturn logUrlReturn = null;
				switch (msg.arg1) {
				case FAILED:
					AppLog.LogD("��ȡ��־�ϴ�·��ʧ��");
					break;
				case SUCESS:
					logUrlReturn = (LogUrlReturn) msg.obj;
					if (logUrlReturn.datas.size() > 0) {
						LogUrl logUrl = logUrlReturn.datas.get(0);
						if (callback != null) {
							callback.handle(logUrl.url);
						}
					}
					AppLog.LogD("��ȡ��־�ϴ�·���ɹ�");
					break;
				default:
					break;
				}

			}
		};

		new Thread(new Runnable() {
			public void run() {
				// ���ص��Ƕ�����ID
				Message message = Message.obtain();
				Gson gson = new Gson();

				JsonObject json = new JsonObject();
				json.addProperty("action", "proxyAction");
				json.addProperty("method", "query");
				json.addProperty("op", "getLogUrl");
				json.addProperty("cityId", cityId);
				json.addProperty("clientType", BookConfig.ClientType.CLIENT_TYPE_PASSENGER);
				json.addProperty("clientVersion", String.valueOf(ETApp.getInstance().getMobileInfo().getVerisonCode()));

				message.what = 1;
				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {
						String retString = new String(response, "utf-8");

						LogUrlReturn logUrlReturn = gson.fromJson(retString, LogUrlReturn.class);
						if (logUrlReturn.error == 0) {
							message.arg1 = SUCESS;
							message.obj = logUrlReturn;
							handler.sendMessage(message);
						} else {
							message.arg1 = FAILED;
							message.obj = null;
							handler.sendMessage(message);
						}

					} else {
						message.arg1 = FAILED;
						message.obj = null;
						handler.sendMessage(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
					message.arg1 = FAILED;
					message.obj = null;
					handler.sendMessage(message);
				}
			}
		}).start();

	}

	/**
	 * ��ȡ��������
	 * 
	 * @param cityId
	 *            ���Id���������Լ�Ӧ�ö����Id���Ǵ�assets�е�citys.db���ݿ��е���������������л�ȡ����
	 */
	public static void getWeather(int cityId, final Callback<String> callback) {
		String url = "http://m.weather.com.cn/atad/" + cityId + ".html";
		AppLog.LogD("getWeather--->" + url);
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				Log.d("xyw", "onSuccess-->" + response);
				callback.handle(response);
			}

			@Override
			public void onFailure(Throwable error, String content) {
				// TODO Auto-generated method stub
				super.onFailure(error, content);
				Log.d("xyw", "onFailure-->" + content);
				callback.error(error);
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
				callback.complete();
			}
		});
	}

	/**
	 * Tcp��������
	 * 
	 * @param id
	 * @param jsonObject
	 * @param callback
	 * @return void
	 */
	public static void request(final long id, final JSONObject jsonObject, final Callback<Object> callback) {
		final Handler handler = new Handler(ETApp.getInstance().getMainLooper()) {
			public void handleMessage(Message msg) {
				switch (msg.arg1) {
				case SUCESS:
					if (callback != null)
						callback.handle((JSONObject) msg.obj);
					break;
				case FAILED:
					if (callback != null)
						callback.error((Exception) msg.obj);
				default:
					break;
				}
			}
		};

		new Thread(new Runnable() {
			public void run() {
				Message message = Message.obtain();
				message.what = 1;
				try {
					byte[] response = TcpClient.send(id, MsgConst.MSG_TCP_ACTION, jsonObject.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {
						String res = new String(response, "UTF-8");
						JSONObject retJson = new JSONObject(res);
						if (retJson.getInt("error") == 0) {
							message.arg1 = SUCESS;
							message.obj = retJson;
							handler.sendMessage(message);
						} else {
							throw new Exception("error code is not 0");
						}
					} else {
						throw new Exception("response is null");
					}
				} catch (Exception e) {
					message.arg1 = FAILED;
					message.obj = e;
					handler.sendMessage(message);
				}
			}
		}).start();
	}

	/**
	 * http get
	 * 
	 * @param isRefresh
	 * @param jsonObject
	 * @param callback
	 * @return void
	 */
	public static void httpRequest(final boolean isRefresh, final JSONObject jsonObject, final Callback<Object> callback) {
		AsyncHttpClient client = new AsyncHttpClient();
		String value = jsonObject.toString();
		RequestParams params = new RequestParams("params", value);
		AppLog.LogD("account request url-->" + WXPayEntryActivity.url);
		AppLog.LogD("account request-->" + params.toString());
		client.get(WXPayEntryActivity.url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				// TODO Auto-generated method stub
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				if (callback != null)
					callback.complete();
			}

			@Override
			public void onSuccess(String content) {
				// TODO Auto-generated method stub
				super.onSuccess(content);
				Log.d("xyw", "http-get" + content);

				try {
					JSONObject result = new JSONObject(content);
					if (isRefresh) {
						result.put("clientIsRefresh", true);
					} else {
						result.put("clientIsRefresh", false);
					}

					if (callback != null)
						callback.handle(result);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					callback.error(e);
				}
			}

			@Override
			public void onFailure(Throwable error, String content) {
				// TODO Auto-generated method stub
				super.onFailure(error, content);
				if (callback != null)
					callback.error(error);
			}
		});
	}

	/**
	 * ʹ�÷����������Ͷ���
	 * 
	 * @param phone
	 * @param content
	 * @return
	 */
	public synchronized static void sendMms(final Handler handler, final String phone, final String content) {
		new Thread(new Runnable() {
			public void run() {

				JSONObject param = new JSONObject();
				try {
					param.put("action", "utilAction");
					param.put("method", "sendMms");
					param.put("phone", phone);
					param.put("content", content);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				AppLog.LogD(" ʹ�÷����������Ͷ��� " + param.toString());
				Message msg = Message.obtain();
				msg.what = RegisterActivity.SEND_SMS_OK;
				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, param.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {
						String retJson = new String(response, "UTF-8");
						AppLog.LogD("ʹ�÷����������Ͷ��� ���" + retJson);
						msg.obj = retJson;
						if (handler != null) {
							handler.sendMessage(msg);
						}
					} else {
						AppLog.LogD("����ֵΪnull --  ");
						if (handler != null) {
							msg.obj = null;
							handler.sendMessage(msg);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (handler != null) {
						msg.obj = null;
						handler.sendMessage(msg);
					}
				}
			}
		}).start();
	}

	/**
	 * ��ȡ�ҵ���Ϣ
	 * 
	 * @param passengerId
	 *            �ͻ�id
	 * @param callback
	 *            �ص�
	 */
	public static synchronized void getMyInfo(final String passengerId, final Callback<MyInfo> callback) {

		final Handler handler = new Handler(ETApp.getInstance().getMainLooper()) {
			public void handleMessage(Message msg) {
				MyInfo myInfo = (MyInfo) msg.obj;
				switch (msg.arg1) {
				case FAILED:
					break;
				case SUCESS:
					if (callback != null) {
						callback.handle(myInfo);
					}
					break;
				default:
					break;
				}
			}
		};

		new Thread(new Runnable() {
			public void run() {
				// ���ص��Ƕ�����ID
				Message message = Message.obtain();
				JsonObject json = new JsonObject();
				json.addProperty("action", "proxyAction");
				json.addProperty("method", "query");
				json.addProperty("op", "getStatByPassengerId");
				json.addProperty("passengerId", passengerId);
				message.what = 1;
				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {
						String retString = new String(response, "utf-8");
						AppLog.LogD(retString);

						JSONObject jsonObject = new JSONObject(retString);
						int error = jsonObject.getInt("error");
						if (error == 0) {
							Gson gson = new Gson();
							JSONArray datas = jsonObject.getJSONArray("datas");
							MyInfo myInfo = gson.fromJson(datas.getJSONObject(0).toString(), MyInfo.class);
							message.arg1 = SUCESS;
							message.obj = myInfo;
							handler.sendMessage(message);
						} else {
							message.arg1 = FAILED;
							message.obj = null;
							handler.sendMessage(message);
						}
					} else {
						message.arg1 = FAILED;
						message.obj = null;
						handler.sendMessage(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
					message.arg1 = FAILED;
					message.obj = null;
					handler.sendMessage(message);
				}
			}
		}).start();

	}
}
