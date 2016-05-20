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
		// 返回的是订单的ID
		AppLog.LogD(tag, "开始根据调度费信息 。。。 ");

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
	 * 获取调度费列表
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
				// 返回的是订单的ID
				AppLog.LogD(tag, "开始根据调度费信息 。。。 ");
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
							AppLog.LogD(tag, "标获据调度费完成 --- " + retString);
							handlerPrice.sendMessage(message);
						} else {
							if (type == 2) {
								message.arg1 = BOOK_REQUEST;
								message.obj = null;
								AppLog.LogD(tag, "标获取据调度费 失败 --1- ");
								handlerPrice.sendMessage(message);
							} else {
								String ret = ETApp.getInstance().getCacheString("pricelist");
								DioaDuPrices dioaDuPricesTmp = gson.fromJson(ret, DioaDuPrices.class);
								message.arg1 = SUCESS;
								message.obj = dioaDuPricesTmp.prices;
								AppLog.LogD(tag, "标获取据调度费 失败 --1- ");
								handlerPrice.sendMessage(message);
							}
						}

					} else {
						if (type == 2) {
							message.arg1 = BOOK_REQUEST;
							message.obj = null;
							AppLog.LogD(tag, "标获取据调度费 失败 --2- ");
							handlerPrice.sendMessage(message);
						} else {
							String ret = ETApp.getInstance().getCacheString("pricelist");
							DioaDuPrices dioaDuPricesTmp = gson.fromJson(ret, DioaDuPrices.class);
							message.arg1 = SUCESS;
							message.obj = dioaDuPricesTmp.prices;
							AppLog.LogD(tag, "标获取据调度费 失败 --2- ");
							handlerPrice.sendMessage(message);
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
					if (type == 2) {
						message.arg1 = BOOK_REQUEST;
						message.obj = null;
						AppLog.LogD(tag, "标获取据调度费 失败 --2- ");
						handlerPrice.sendMessage(message);
					} else {
						String ret = ETApp.getInstance().getCacheString("pricelist");
						DioaDuPrices dioaDuPricesTmp = gson.fromJson(ret, DioaDuPrices.class);
						message.arg1 = SUCESS;
						message.obj = dioaDuPricesTmp.prices;
						AppLog.LogD(tag, "标获取据调度费 失败 --2- ");
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
	 * 更具经纬度 获取 地址
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
				// 返回的是订单的ID

				Message message = Message.obtain();

				JsonObject json = new JsonObject();
				json.addProperty("action", "geoCodingAction");
				json.addProperty("method", "getAddressByLocation");

				json.addProperty("lat", lat);
				json.addProperty("lng", lng);

				AppLog.LogD(tag, "开始根据坐标获取地址信息 。。。 " + json.toString());
				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {
						String address = new String(response, "UTF-8");
						message.arg1 = SUCESS;
						message.obj = address;
						AppLog.LogD(tag, "标获取地址信息 完成 地址为： " + message.obj);

						handler.sendMessage(message);
					} else {
						message.arg1 = FAILED;
						AppLog.LogD(tag, "标获取地址信息 失败 --1- ");
						handler.sendMessage(message);

					}
				} catch (Exception e) {
					e.printStackTrace();
					message.arg1 = FAILED;
					AppLog.LogD(tag, "标获取地址信息 --2- ");
					handler.sendMessage(message);
				}
			}
		});
		// t.setPriority(6);
		t.start();
	}

	/**
	 * 更具地址获取 经纬度
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
				// 返回的是订单的ID
				AppLog.LogD(tag, "开始根据地址获取经纬度 。。。 " + city + " , " + address);
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
						AppLog.LogD(tag, "地址获取经纬度 完成 --- " + message.obj);

						handler.sendMessage(message);
					} else {
						message.arg1 = FAILED;
						AppLog.LogD(tag, "地址获取经纬度 失败 --1- ");
						handler.sendMessage(message);

					}
				} catch (Exception e) {
					e.printStackTrace();
					message.arg1 = FAILED;
					AppLog.LogD(tag, "地址获取经纬度 --2- ");
					handler.sendMessage(message);
				}
			}
		}).start();
	}

	/**
	 * 乘客拉回应接单
	 */
	public static synchronized void getReplyBook(final long bookId, final Callback<BookBean> callback) {

		new Thread(new Runnable() {
			public void run() {
				// 返回的是订单的ID

				JsonObject json = new JsonObject();
				json.addProperty("action", "scheduleAction");
				json.addProperty("method", "getReplyBook");
				json.addProperty("bookId", bookId);
				AppLog.LogD(tag, " 开始 获取接单后的订单  :  " + json.toString());
				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {
						String res = new String(response, "UTF-8");

						JSONObject jo = new JSONObject(res);
						int error = jo.getInt("error");
						if (error == 0) {
							AppLog.LogD(tag, "  获取接单后的订单 :  " + res.toString());
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

	// 提交即时订单
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

				// 简直无法改。。。。。。。。。。。。。。。。。
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

				AppLog.LogD("xyw", "即时打车submit json：" + json);
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
	 *            出租车
	 * @param t_lng
	 * @param p_lat
	 *            乘客
	 * @param p_lng
	 * @param d_lat
	 *            目的地
	 * @param d_lng
	 * @param callback
	 * 
	 *            <p>
	 *            row.put("error", 0); row.put("distance", 446); //出租车乘客距离
	 *            row.put("price", 33); //出租车乘客距离 row.put("course", 11);
	 *            //出租车乘客距离 row.put("time", 33); //出租车乘客距离 row.put("wait", 5);
	 *            //出租车乘客距离
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
				// 返回的是订单的ID

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
				AppLog.LogD(tag, "获取 mark 开始 --- " + json.toString());
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
							AppLog.LogD(tag, "获取 mark 完成 --- " + retJson.toString());
						} else {
							message.arg1 = FAILED;
						}

						handler.sendMessage(message);
					} else {
						message.arg1 = FAILED;
						AppLog.LogD(tag, "获取 mark单 失败 --1- ");
						handler.sendMessage(message);

					}
				} catch (Exception e) {
					e.printStackTrace();
					message.arg1 = FAILED;
					AppLog.LogD(tag, "获取 mark --2- ");
					handler.sendMessage(message);
				}
			}
		}).start();
	}

	// 0 1-21 22---
	// 头 城市 数据
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
				// 返回的是订单的ID
				AppLog.LogD(tag, "sendSound 。。。 ");
				Message message = Message.obtain();

				message.what = 1;
				try {
					byte[] response = TcpClient.send(1L, 0xF00008, datas);
					if (response != null && response.length > 0) {

						String rs = new String(response, "UTF-8");
						// {"error":0,"bookId":1364286932656,"timeout":250000}

						JSONObject retJson = new JSONObject(rs);

						AppLog.LogD(tag, "sendSound return  。。。 " + rs);

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
						AppLog.LogD(tag, "发语音- 失败 --1- ");
						handler.sendMessage(message);

					}
				} catch (Exception e) {
					e.printStackTrace();
					message.arg1 = FAILED;
					message.obj = null;
					AppLog.LogD(tag, "发语音- --2- ");
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
				// 取消订单
				AppLog.LogD(tag, "cancelBook 。。。 ");
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
							AppLog.LogD(tag, "取消订单--- " + retJson.toString());
						} else {
							message.arg1 = FAILED;
						}

						handler.sendMessage(message);
					} else {
						message.arg1 = FAILED;
						AppLog.LogD(tag, "取消订单单 失败 --1- ");
						handler.sendMessage(message);

					}
				} catch (Exception e) {
					e.printStackTrace();
					message.arg1 = FAILED;
					AppLog.LogD(tag, "取消订单 --2- ");
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
				// 取消订单
				AppLog.LogD(tag, "regNewUser 。。。 ");
				Message message = Message.obtain();

				JsonObject json = new JsonObject();
				json.addProperty("action", "userAction");
				json.addProperty("method", "regPassenger");

				json.addProperty("name", name);
				json.addProperty("phone", phone); //

				json.addProperty("id", id);//
				json.addProperty("sex", sex);// 评分

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
							AppLog.LogD(tag, "注册成功-- " + retJson.toString());
						} else {
							message.arg1 = FAILED;
						}

						handler.sendMessage(message);
					} else {
						message.arg1 = FAILED;
						AppLog.LogD(tag, "注册失败 --1- ");
						handler.sendMessage(message);

					}
				} catch (Exception e) {
					e.printStackTrace();
					message.arg1 = FAILED;
					AppLog.LogD(tag, "注册失败 --2- ");
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

		// 1.android乘客端 2. android司机端，3.android Pad酒店端
		json.addProperty("clientVersion", version); // 版本号
		json.addProperty("type", 1);

		json.addProperty("id", passengerId);// 电话号
		json.addProperty("cityId", MainActivityNew.cityId);
		json.addProperty("cityName", MainActivityNew.currentCityName);
		json.addProperty("clientType", BookConfig.ClientType.CLIENT_TYPE_PASSENGER);

		AppLog.LogD(tag, " --升级检测- " + json.toString());
		// response != null && response.length > 0
		try {
			byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
			if (response != null && response.length > 0) {

				String rs = new String(response, "UTF-8");
				AppLog.LogD(tag, " -升级检测 结果-- " + rs);
				JSONObject retJson = new JSONObject(rs);
				int error = retJson.getInt("error");

				if (error == 0) {
					// message.obj = retJson;
					if (callback != null)
						callback.handle(retJson);

				} else {
					// erro != 0 不升级
					if (callback != null)
						callback.handle(null);
				}
			} else {

				AppLog.LogD(tag, "升级检测 结果失败 --1- ");
				// handler.sendMessage(message);
				if (callback != null)
					callback.handle(null);

			}
		} catch (Exception e) {
			e.printStackTrace();

			AppLog.LogD(tag, "升级检测 结果失败 --2- ");
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

				AppLog.LogD(tag, "getNearbyTaxis 。。。 ");
				JsonObject json = new JsonObject();
				json.addProperty("action", "locationAction");
				json.addProperty("method", "getNearbyTaxis1");

				// 1.android乘客端 2. android司机端，3.android Pad酒店端
				json.addProperty("latitude", lat); // 版本号
				json.addProperty("longitude", lng); // 版本号
				json.addProperty("cityId", cityId); // 版本号

				// 2013-0513接口变动为 getNearbyTaxis1

				// AppLog.LogD(tag, " --获取周围车---=====- " + json.toString());

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
							AppLog.LogD(tag, "获取周围车成功-- " + retJson.toString());
						} else {
							message.arg1 = FAILED;
						}

						handler.sendMessageDelayed(message, 50);
					} else {
						message.arg1 = FAILED;
						AppLog.LogD(tag, "获取周围车失败 --1- ");
						handler.sendMessageDelayed(message, 50);

					}
				} catch (Exception e) {
					e.printStackTrace();
					message.arg1 = FAILED;
					AppLog.LogD(tag, "获取周围车 --2- ");
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

				AppLog.LogD(tag, "getTaxiLocation 。。。 ");
				JsonObject json = new JsonObject();
				json.addProperty("action", "locationAction");
				json.addProperty("method", "getTaxiLocation");
				json.addProperty("taxiId", taxiId);

				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {

						String rs = new String(response, "UTF-8");
						// {"error":0,"bookId":1364286932656,"timeout":250000}
						AppLog.LogD(tag, " --获取车辆位置--- " + rs);
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
						AppLog.LogD(tag, "获取车辆位置 --1- ");
						handler.sendMessage(message);

					}
				} catch (Exception e) {
					e.printStackTrace();
					message.arg1 = FAILED;
					AppLog.LogD(tag, "获取车辆位置 --2- ");
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
						AppLog.LogD(tag, "获取城市成功 - - " + retJson);
						// Intent intent = new
						// Intent(CityTool.ACTION_RESPONSE_GETCITY);
						// intent.putExtra("city", retJson);
						if (callback != null) {

							callback.handle(retJson);
						}
						// context.sendBroadcast(intent);

					} else {

						AppLog.LogD(tag, "获取城市信息失败 --1- ");

					}
				} catch (Exception e) {
					e.printStackTrace();

					AppLog.LogD(tag, "获取城信息 --2- ");

				}
			}
		}).start();

	}

	/**
	 * 
	 * @param sendType
	 *            1 推荐给司机 ，0 推荐给乘客
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

					// 0乘客对剑司机
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
	 *            1.乘客推荐乘客，2.乘客推荐司机，//3，司机推荐司机，4，司机推荐乘客
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
						AppLog.LogD("返回值为null");
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
	 * 检查平台版本是否有更新，如果有，就更新安装
	 * 
	 * @param isInfo
	 *            在检查到没有新版本的时候是否提示用户
	 */
	@SuppressLint("NewApi")
	public static void checkUpdate(final Context context, final String cityId, final int version, final boolean isInfo, final String passengerId, final Callback<String> newVersion) {
		// Toast.makeText(context, "正在检查版本", Toast.LENGTH_LONG).show();

		new AsyncTask<Object, JSONObject, Object>() {
			@Override
			protected void onProgressUpdate(final JSONObject... values) {

				if (values == null) {// 不升级
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
						Window.confirm(force, context.getApplicationContext(), "发现新版本", mark, new Callback<Object>() {

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
						}, "立即升级", "以后升级");
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
						} else {// 不升级
							publishProgress(null);
						}
					}
				});
				return null;
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, version);
	}

	/**
	 * 获取用户的推荐历史信息 ：您已经推荐了100人
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
						AppLog.LogD("返回值为null -- sendRmdMyinfoRequest");
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
	 * 上传电话号码
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
						AppLog.LogD("返回值为null -- sendRmdMyinfoRequest");
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
	 * 获取“更多”的菜单
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

				AppLog.LogD("获取“更多” " + json.toString());
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
						AppLog.LogD("返回值为null -- sendRmdMyinfoRequest");
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
	 * 意见建议反馈
	 * 
	 * @param handler
	 * @param cityId
	 *            /** 建议类型: 1.即时打车结束 2.普通投诉和建议 3.投诉司机 4.投诉客服 5.软件建议 6.问题咨询
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

				AppLog.LogD("意见建议反馈 " + json.toString());
				Message msg = Message.obtain();
				msg.what = SuggestionActivity.FEEDBACK_OK;
				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {

						String retJson = new String(response, "UTF-8");
						// Log.d("tag", retJson);
						AppLog.LogD("意见建议反馈 结果" + retJson.toString());
						msg.obj = retJson;
						if (handler != null) {
							handler.sendMessage(msg);
						}
						// context.sendBroadcast(intent);
					} else {
						AppLog.LogD("返回值为null -- sendRmdMyinfoRequest");
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
	 * 评价 /** 建议类型: 1.即时打车结束 2.普通投诉和建议 3.投诉司机 4.投诉客服 5.软件建议 6.问题咨询 7.订单评价
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

				AppLog.LogD("评价 " + json.toString());
				Message msg = Message.obtain();
				msg.what = SuggestionActivity.FEEDBACK_OK;
				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {

						String retJson = new String(response, "UTF-8");
						// Log.d("tag", retJson);
						AppLog.LogD("评价 结果" + retJson.toString());
						msg.obj = retJson;
						if (handler != null) {
							handler.sendMessage(msg);
						}
						// context.sendBroadcast(intent);
					} else {
						AppLog.LogD("返回值为null -- sendRmdMyinfoRequest");
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
	 * 新的评论方法
	 * 
	 * @param handler
	 * @param bookId
	 *            BookBean的getId()
	 * @param taxiId
	 *            BookBean的getReplyerId()
	 * @param value
	 *            评分
	 * @deprecated
	 */
	public static void suggest(final Handler handler, final Long bookId, final Long taxiId, final int value) {
		new Thread(new Runnable() {
			public void run() {
				JsonObject json = new JsonObject();
				json.addProperty("action", "suggestAction");
				json.addProperty("method", "suggestByPassenger");

				json.addProperty("souce", 0); // 0 平台类型
				json.addProperty("bookId", bookId); // BookBean的getId()
				json.addProperty("taxiId", taxiId);// BookBean的getReplyerId()
				json.addProperty("value", value);// 评分

				AppLog.LogD("评价 " + json.toString());
				Message msg = Message.obtain();
				msg.what = SuggestionActivity.FEEDBACK_OK;
				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {

						String retJson = new String(response, "UTF-8");
						// Log.d("tag", retJson);
						AppLog.LogD("评价 结果" + retJson.toString());
						msg.obj = retJson;
						msg.arg1 = value;// 评分
						if (handler != null) {
							handler.sendMessage(msg);
						}
						// context.sendBroadcast(intent);
					} else {
						AppLog.LogD("返回值为null -- sendRmdMyinfoRequest");
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
	 * 获取用户评论信息 （profile info）
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

				AppLog.LogD("获取用户信息 " + json.toString());
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
						AppLog.LogD("获取用户信息结果" + retJson.toString());
						msg.obj = retJson;
						if (handler != null) {
							handler.sendMessage(msg);
						}
						// context.sendBroadcast(intent);
					} else {
						AppLog.LogD("返回值为null ");
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
	 * 获取评论信息
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

				AppLog.LogD("获取评论信息 " + json.toString());

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
						AppLog.LogD("获取评论信息结果 ----- " + retJson);
						Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
						YDUserComments yc = gson.fromJson(retJson, YDUserComments.class);
						msg.obj = yc.comments;
						if (handler != null) {
							handler.sendMessage(msg);
						}
						// context.sendBroadcast(intent);
					} else {
						AppLog.LogD("返回值为null ");
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
	 * 获取历史消息<getMessageList>
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
				json.addProperty("objectType", 2); // 对象类型，1.司机，2. 乘客

				AppLog.LogD("获取历史消息 " + json.toString());

				Message msg = Message.obtain();
				// msg.what = MSG_HISTORY_SHORT_MSG_REMOTE;

				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {

						String retJson = new String(response, "UTF-8");

						AppLog.LogD("获取历史消息结果" + retJson);

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
						AppLog.LogD("返回值为null ");
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

	// 新版调度费
	public static class TipPrices {
		public int error;
		public String errormsg;
		public int onlinePayment;
		public int[] priceList;
		public String msg;
	}

	/**
	 * * 新版获取调度费列表，含调度费提示msg
	 * 
	 * @param cityId
	 * @param bookType
	 *            新版及时订单（1）旧版及时订单（2）IOS1.5预约订单（3）旧版预约订单（4）
	 *            Android新版预约订单（5）代价（6）接送机（7）
	 * 
	 * 
	 * 
	 * @param clientType
	 *            酒店版固定为：pad
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

				// 即时召车
				case Const.JISHI_BOOK_TYPE:
					prices = (TipPrices) msg.obj;
					if (callback != null) {
						callback.handle(prices);
					}
					break;

				// 预约订车
				case Const.BOOK_BOOK_TYPE:
					prices = (TipPrices) msg.obj;
					if (callback != null) {
						callback.handle(prices);
					}
					break;
				// 接机送机
				case Const.AIRPORT_BOOK_TYPE:
					prices = (TipPrices) msg.obj;
					if (callback != null) {
						callback.handle(prices);
					}
					break;
				// 代驾
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
				// 返回的是订单的ID
				AppLog.LogD(tag, "开始根据调度费信息 。。。 ");
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
				// 新的价格列表的接口
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
							// 在线支付金额
							message.arg1 = bookType;
							message.obj = tips;
							handlerPrice.sendMessage(message);
						} else {
							message.arg1 = bookType;
							message.obj = tips;
							AppLog.LogD(tag, "标获取据调度费 失败 --0- ");
							handlerPrice.sendMessage(message);
						}

					} else {
						message.arg1 = FAILED;
						message.obj = null;
						AppLog.LogD(tag, "标获取据调度费 失败 --1- ");
						handlerPrice.sendMessage(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
					message.arg1 = FAILED;
					message.obj = null;
					AppLog.LogD(tag, "标获取据调度费 失败 --2- ");
					handlerPrice.sendMessage(message);
				}
			}
		}).start();

	}

	/**
	 * 时间上下线
	 */
	public static class TimeLines {
		public int error;
		public int count;
		public ArrayList<TimeLine> datas;
	}

	public static class TimeLine {
		/**
		 * 订车时间上线,订购用车时间不能够大于这个upper时间：单位为分钟
		 */
		public String upper;
		/**
		 * 订车时间,距离现在至少订购必须lower时间以后的用车：单位为分钟
		 */
		public String lower;
	}

	/**
	 * 获取时间上下线
	 */
	public static synchronized void getTimeDeadLine(final int cityId, final int bookType, final String clientType, final String clientVersion, final Callback<Object> callback) {

		final Handler handlerPrice = new Handler(ETApp.getInstance().getMainLooper()) {
			public void handleMessage(Message msg) {

				AppLog.LogD(tag, "getTimeDeadLine ---- ");

				TimeLines timeLines = null;
				switch (msg.arg1) {

				case FAILED:
					break;
				// 预约订车
				case Const.BOOK_BOOK_TYPE:
					timeLines = (TimeLines) msg.obj;
					if (callback != null) {
						callback.handle(timeLines);
					}
					break;
				// 接机送机
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
				// 返回的是订单的ID
				AppLog.LogD(tag, "获取时间上下线 。。。 ");

				Message message = Message.obtain();
				message.what = 1;
				// action proxyAction
				// method query
				// cityId Long 城市ID
				// clientType String 终端类型
				// clientVersion String 客户端版本
				// op String getBookTimeInterval
				// bookType Long 类型：表示模块类型
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
							AppLog.LogD(tag, "标获取据调度费 失败 --0- ");
							handlerPrice.sendMessage(message);
						}

					} else {
						message.arg1 = FAILED;
						message.obj = null;
						AppLog.LogD(tag, "标获取据调度费 失败 --1- ");
						handlerPrice.sendMessage(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
					message.arg1 = FAILED;
					message.obj = null;
					AppLog.LogD(tag, "标获取据调度费 失败 --2- ");
					handlerPrice.sendMessage(message);
				}
			}
		}).start();

	}

	// ****************日志上传*******************
	/**
	 * 返回上传日志的Url对象
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
	 * 从服务器获取日志上传路径
	 * 
	 * @param cityId
	 *            城市Id
	 * @param callback
	 *            回调函数
	 */
	public static synchronized void getUploadLogUrl(final int cityId, final Callback<Object> callback) {

		final Handler handler = new Handler(ETApp.getInstance().getMainLooper()) {
			public void handleMessage(Message msg) {

				LogUrlReturn logUrlReturn = null;
				switch (msg.arg1) {
				case FAILED:
					AppLog.LogD("获取日志上传路径失败");
					break;
				case SUCESS:
					logUrlReturn = (LogUrlReturn) msg.obj;
					if (logUrlReturn.datas.size() > 0) {
						LogUrl logUrl = logUrlReturn.datas.get(0);
						if (callback != null) {
							callback.handle(logUrl.url);
						}
					}
					AppLog.LogD("获取日志上传路径成功");
					break;
				default:
					break;
				}

			}
		};

		new Thread(new Runnable() {
			public void run() {
				// 返回的是订单的ID
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
	 * 获取天气数据
	 * 
	 * @param cityId
	 *            这个Id不是我们自己应用定义的Id，是从assets中的citys.db数据库中第三方定义的数据中获取到的
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
	 * Tcp网络请求
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
	 * 使用服务器来发送短信
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

				AppLog.LogD(" 使用服务器来发送短信 " + param.toString());
				Message msg = Message.obtain();
				msg.what = RegisterActivity.SEND_SMS_OK;
				try {
					byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, param.toString().getBytes("UTF-8"));
					if (response != null && response.length > 0) {
						String retJson = new String(response, "UTF-8");
						AppLog.LogD("使用服务器来发送短信 结果" + retJson);
						msg.obj = retJson;
						if (handler != null) {
							handler.sendMessage(msg);
						}
					} else {
						AppLog.LogD("返回值为null --  ");
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
	 * 获取我的信息
	 * 
	 * @param passengerId
	 *            客户id
	 * @param callback
	 *            回调
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
				// 返回的是订单的ID
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
