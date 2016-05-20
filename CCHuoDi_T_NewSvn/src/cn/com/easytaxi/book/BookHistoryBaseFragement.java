package cn.com.easytaxi.book;

import java.lang.ref.SoftReference;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.BookConfig;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.book.BookBean;
import cn.com.easytaxi.book.BookUtil;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.common.Const;
import cn.com.easytaxi.common.SocketUtil;
import cn.com.easytaxi.platform.MainActivityNew;
import cn.com.easytaxi.ui.BaseFragement;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

public class BookHistoryBaseFragement extends BaseFragement {

	public BookHistoryFragementActivity bookParent;
	public static HashMap<String, SoftReference<Object>> cache = new HashMap<String, SoftReference<Object>>();
	public static SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat f_d = new SimpleDateFormat("HH时mm分ss秒");
	public final Calendar c = Calendar.getInstance();

	public static final long START_ID = Long.MAX_VALUE;
	// public static long count;
	/**
	 * 当前显示到了第几条记录开始
	 */
	public static int bookIndex = 0;
	/**
	 * 每页的数量：后面从服务器获取
	 */
	public int pageSize = 10;
	public ArrayList<BookBean> datas;
	public static boolean enableLoadMore = true;
	private Handler handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		bookParent = (BookHistoryFragementActivity) getActivity();

		/*
		 * if (ETApp.cache.containsKey(("booklist"))) { datas =
		 * (ArrayList<BookBean>) ETApp.cache.get(("booklist")).get(); if (datas
		 * == null) { datas = new ArrayList<BookBean>();
		 * 
		 * synchronized (cache) { ETApp.cache.put("booklist", new
		 * SoftReference<Object>(datas)); } } } else { datas = new
		 * ArrayList<BookBean>(); synchronized (cache) {
		 * ETApp.cache.put("booklist", new SoftReference<Object>(datas)); } }
		 */
		datas = new ArrayList<BookBean>();
		bookIndex = 0;
	}

	public String getPassengerId() {
		return bookParent.getPassengerId();
	}

	protected boolean isLogin() {
		if (ETApp.getInstance().isLogin()) {
			return true;
		} else {
			return false;
		}
	}

	public void startLoopTime() {
		if (handler != null) {
			return;
		}
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				try {
					Iterator<BookBean> itr = datas.iterator();

					while (itr.hasNext()) {
						BookBean b = itr.next();
						if (BookUtil.isNew(b)) {
							if (b.getDyTime() == 0) {
								long t = f.parse(b.getUseTime()).getTime() - System.currentTimeMillis();
								b.setDyTime(t);
							} else {
								b.setDyTime(b.getDyTime() - 1000);
							}
							if (b.getDyTime() <= 0) {
								onTimeChange();
								bookParent.sendBroadcast(new Intent("cn.com.easytaxi.book.refresh_list"));
								continue;
							}
						} else if (BookUtil.isActive(b)) {
							if (b.getDyTime() == 0) {
								long t = f.parse(b.getUseTime()).getTime() - System.currentTimeMillis();
								b.setDyTime(t);
							} else {
								b.setDyTime(b.getDyTime() - 1000);
							}
							if (b.getDyTime() <= 0) {
								onTimeChange();
								bookParent.sendBroadcast(new Intent("cn.com.easytaxi.book.refresh_list"));
								continue;
							}
						} else {

						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				onTimeChange();
				sendEmptyMessageDelayed(0, 1000);
			};
		};

		handler.sendEmptyMessage(0);

	}

	public void stopLoopTime() {
		if (handler != null) {
			handler.removeMessages(0);
			handler = null;
		}
		try {
			for (BookBean b : datas) {
				b.setDyTime(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getTimeStr(long time) {
		// return f_d.format(new Date(time - 8 * 3600 * 1000));// 减去时差
		int HH = (int) (time / 3600000);
		int mm = (int) ((time - HH * 3600000) / 60000);
		int ss = (int) (time - HH * 3600000 - mm * 60000) / 1000;
		return HH + "时" + mm + "分" + ss + "秒";
	}

	// action proxyAction
	// method query
	// 请求
	// 参数 描述 必填
	// op getHistoryBookListByPassengerId2 是
	// orderName 排序字段名 是
	// passengerId 乘客Id 是
	// order 排序方式（asc/desc） 是
	// startId 分页ID 是
	// 响应
	// datas [
	// {
	// 订单
	// }
	// ]

	public String getJsonString(JSONObject jsonObject, String name) {
		try {
			return jsonObject.getString(name);
		} catch (Exception e) {
			return "";
		}
	}

	public int getJsonInt(JSONObject jsonObject, String name) {
		try {
			return Integer.parseInt(jsonObject.getString(name));
		} catch (Exception e) {
			return -1;
		}
	}

	public long getJsonLong(JSONObject jsonObject, String name) {
		try {
			return Long.parseLong(jsonObject.getString(name));
		} catch (Exception e) {
			return -1;
		}
	}

	public void loadPage(final boolean dialog, final Callback<Integer> callback) {
		if (dialog) {
			// showDialog(0);
		}
		JSONObject json = new JSONObject();
		try {
			json.put("action", "proxyAction");
			json.put("method", "query");
			json.put("op", "getHistoryBookListByPassengerId");

			json.put("orderName", "_SUBMIT_TIME");// orderName 排序字段名

			json.put("order", "desc");

			json.put("startId", bookIndex);// startId 分页ID

			String mobile = getPassengerId();
			if (StringUtils.isEmpty(mobile)) {
				mobile = "0";
			}
			json.put("passengerId", mobile);
			
			json.put("cityId", MainActivityNew.cityId);
			json.put("cityName",MainActivityNew.currentCityName);
			json.put("clientType", BookConfig.ClientType.CLIENT_TYPE_PASSENGER);
			SocketUtil.getJSONArray(Long.valueOf(mobile), json, new Callback<byte[]>() {

				@Override
				public void handle(byte[] param) {
					try {
						if (param != null) {

							JSONObject jsonObject = new JSONObject(new String(param, "UTF-8"));
							if (jsonObject.getInt("error") == 0) {
								// String array =
								// jsonObject.get("datas").toString();
								// Gson gson = new Gson();
								// Type type = new TypeToken<List<BookBean>>() {
								// }.getType();
								// List<BookBean> result = gson.fromJson(array,
								// type);
								List<BookBean> result = new ArrayList<BookBean>();
								JSONArray jsonArray = jsonObject.getJSONArray("datas");
								AppLog.LogD("xyw", "book list-->" + jsonArray);
								int length = jsonArray.length();
								JSONObject jsonObjectBookBean;
								for (int i = 0; i < length; i++) {
									jsonObjectBookBean = (JSONObject) jsonArray.get(i);
									BookBean bookBean = new BookBean();

									bookBean.setCityName(getJsonString(jsonObjectBookBean, "_CITY_NAME"));
									bookBean.setStartAddress(getJsonString(jsonObjectBookBean, "_START_ADDRESS"));
									bookBean.setPassengerId(getJsonLong(jsonObjectBookBean, "_PASSENGER_ID"));
									bookBean.setSubmitTime(getJsonString(jsonObjectBookBean, "_SUBMIT_TIME"));
									// 新版bookState取代旧版state
									bookBean.setState(getJsonInt(jsonObjectBookBean, "_BOOK_STATE"));
									bookBean.setPassengerPhone(getJsonString(jsonObjectBookBean, "_PASSENGER_PHONE"));
									bookBean.setStartLongitude(getJsonInt(jsonObjectBookBean, "_START_LONGITUDE"));
//									bookBean.setForecastPrice(getJsonInt(jsonObjectBookBean, "forecastPrice"));
									bookBean.setBookType(getJsonInt(jsonObjectBookBean, "_BOOK_TYPE"));
									bookBean.setEndAddress(getJsonString(jsonObjectBookBean, "_END_ADDRESS"));
//									bookBean.setForecastDistance(getJsonInt(jsonObjectBookBean, "forecastDistance"));
									bookBean.setId(getJsonLong(jsonObjectBookBean, "_ID"));
//									bookBean.setPreTime(getJsonString(jsonObjectBookBean, "currentDate"));
									bookBean.setStartLatitude(getJsonInt(jsonObjectBookBean, "_START_LATITUDE"));
//									bookBean.setEndLatitude(getJsonInt(jsonObjectBookBean, "endLat"));
									bookBean.setUseTime(getJsonLong(jsonObjectBookBean, "_USE_TIME"));
									bookBean.setReplyerNumber(getJsonString(jsonObjectBookBean, "_REPLYER_NUMBER"));
									bookBean.setReplyerId(getJsonLong(jsonObjectBookBean, "_REPLYER_ID"));
									bookBean.setReplyerPhone(getJsonString(jsonObjectBookBean, "_REPLYER_PHONE"));
									bookBean.setReplyerName(getJsonString(jsonObjectBookBean, "_REPLYER_NAME"));
//									bookBean.setReplyerCompany(getJsonString(jsonObjectBookBean, "_REPLYER_COMPANY"));
//									bookBean.setReplyTime(getJsonString(jsonObjectBookBean, "replyTime"));
//									bookBean.setPriceMode(getJsonInt(jsonObjectBookBean, "priceMode"));
//									bookBean.setPrice(getJsonInt(jsonObjectBookBean, "earnst"));
//									bookBean.setPrice(getJsonInt(jsonObjectBookBean, "earnst"));
									bookBean.setBookFlags(getJsonInt(jsonObjectBookBean, "_BOOK_FLAGS"));
									result.add(bookBean);
								}

								synchronized (datas) {
									if (bookIndex == 0) {// 刷新
										datas.clear();
										// 初始化每页的条数
										pageSize = jsonObject.getInt("count");
										if (pageSize <= 0) {
											pageSize = 10;
										}
									}

									datas.addAll(result);
									removeDuplicate(datas);

									if (result.size() < pageSize) {
										enableLoadMore = false;
									} else {
										enableLoadMore = true;
									}
									bookIndex = bookIndex + result.size();
								}
								if (callback != null) {
									callback.handle(result.size());
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void complete() {
					if (dialog) {
						// dismissDialog(0);
					}
					if (callback != null) {
						callback.complete();
					}
				}

				@Override
				public void error(Throwable e) {
					super.error(e);
				}

			});
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	// 老版本获取订单列表：没有过滤即时打车订单
	public void loadPageOld(final boolean dialog, final Callback<Integer> callback) {/*
																					 * if
																					 * (
																					 * dialog
																					 * )
																					 * {
																					 * /
																					 * /
																					 * showDialog
																					 * (
																					 * 0
																					 * )
																					 * ;
																					 * }
																					 * JSONObject
																					 * json
																					 * =
																					 * new
																					 * JSONObject
																					 * (
																					 * )
																					 * ;
																					 * try
																					 * {
																					 * json
																					 * .
																					 * put
																					 * (
																					 * "action"
																					 * ,
																					 * "scheduleAction"
																					 * )
																					 * ;
																					 * json
																					 * .
																					 * put
																					 * (
																					 * "method"
																					 * ,
																					 * "getBookListByPassenger"
																					 * )
																					 * ;
																					 * json
																					 * .
																					 * put
																					 * (
																					 * "startId"
																					 * ,
																					 * count
																					 * )
																					 * ;
																					 * json
																					 * .
																					 * put
																					 * (
																					 * "size"
																					 * ,
																					 * pageSize
																					 * )
																					 * ;
																					 * 
																					 * String
																					 * mobile
																					 * =
																					 * getPassengerId
																					 * (
																					 * )
																					 * ;
																					 * if
																					 * (
																					 * StringUtils
																					 * .
																					 * isEmpty
																					 * (
																					 * mobile
																					 * )
																					 * )
																					 * {
																					 * mobile
																					 * =
																					 * "0"
																					 * ;
																					 * }
																					 * 
																					 * json
																					 * .
																					 * put
																					 * (
																					 * "passengerId"
																					 * ,
																					 * mobile
																					 * )
																					 * ;
																					 * json
																					 * .
																					 * put
																					 * (
																					 * "bookType"
																					 * ,
																					 * 5
																					 * )
																					 * ;
																					 * /
																					 * /
																					 * json
																					 * .
																					 * put
																					 * (
																					 * "state"
																					 * ,
																					 * 5
																					 * )
																					 * ;
																					 * /
																					 * /
																					 * AppLog
																					 * .
																					 * LogA
																					 * (
																					 * "xyw"
																					 * ,
																					 * "refresh booklist--->"
																					 * +
																					 * json
																					 * .
																					 * toString
																					 * (
																					 * )
																					 * )
																					 * ;
																					 * SocketUtil
																					 * .
																					 * getJSONArray
																					 * (
																					 * Long
																					 * .
																					 * valueOf
																					 * (
																					 * mobile
																					 * )
																					 * ,
																					 * json
																					 * ,
																					 * new
																					 * Callback
																					 * <
																					 * byte
																					 * [
																					 * ]
																					 * >
																					 * (
																					 * )
																					 * {
																					 * 
																					 * @
																					 * Override
																					 * public
																					 * void
																					 * handle
																					 * (
																					 * byte
																					 * [
																					 * ]
																					 * param
																					 * )
																					 * {
																					 * try
																					 * {
																					 * if
																					 * (
																					 * param
																					 * !=
																					 * null
																					 * )
																					 * {
																					 * 
																					 * JSONObject
																					 * jsonObject
																					 * =
																					 * new
																					 * JSONObject
																					 * (
																					 * new
																					 * String
																					 * (
																					 * param
																					 * ,
																					 * "UTF-8"
																					 * )
																					 * )
																					 * ;
																					 * if
																					 * (
																					 * jsonObject
																					 * .
																					 * getInt
																					 * (
																					 * "error"
																					 * )
																					 * ==
																					 * 0
																					 * )
																					 * {
																					 * String
																					 * array
																					 * =
																					 * jsonObject
																					 * .
																					 * get
																					 * (
																					 * "books"
																					 * )
																					 * .
																					 * toString
																					 * (
																					 * )
																					 * ;
																					 * Gson
																					 * gson
																					 * =
																					 * new
																					 * Gson
																					 * (
																					 * )
																					 * ;
																					 * Type
																					 * type
																					 * =
																					 * new
																					 * TypeToken
																					 * <
																					 * List
																					 * <
																					 * BookBean
																					 * >>
																					 * (
																					 * )
																					 * {
																					 * }
																					 * .
																					 * getType
																					 * (
																					 * )
																					 * ;
																					 * /
																					 * /
																					 * AppLog
																					 * .
																					 * LogA
																					 * (
																					 * "xyw"
																					 * ,
																					 * array
																					 * )
																					 * ;
																					 * List
																					 * <
																					 * BookBean
																					 * >
																					 * result
																					 * =
																					 * gson
																					 * .
																					 * fromJson
																					 * (
																					 * array
																					 * ,
																					 * type
																					 * )
																					 * ;
																					 * synchronized
																					 * (
																					 * datas
																					 * )
																					 * {
																					 * if
																					 * (
																					 * count
																					 * ==
																					 * START_ID
																					 * )
																					 * {
																					 * /
																					 * /
																					 * 刷新
																					 * datas
																					 * .
																					 * clear
																					 * (
																					 * )
																					 * ;
																					 * }
																					 * 
																					 * if
																					 * (
																					 * result
																					 * !=
																					 * null
																					 * &&
																					 * result
																					 * .
																					 * size
																					 * (
																					 * )
																					 * >
																					 * 0
																					 * )
																					 * {
																					 * /
																					 * /
																					 * count
																					 * =
																					 * result
																					 * .
																					 * get
																					 * (
																					 * result
																					 * .
																					 * size
																					 * (
																					 * )
																					 * -
																					 * 1
																					 * )
																					 * .
																					 * getId
																					 * (
																					 * )
																					 * ;
																					 * for
																					 * (
																					 * BookBean
																					 * bean
																					 * :
																					 * result
																					 * )
																					 * {
																					 * long
																					 * id
																					 * =
																					 * bean
																					 * .
																					 * getId
																					 * (
																					 * )
																					 * ;
																					 * if
																					 * (
																					 * count
																					 * >
																					 * id
																					 * )
																					 * {
																					 * count
																					 * =
																					 * id
																					 * ;
																					 * }
																					 * }
																					 * 
																					 * }
																					 * else
																					 * {
																					 * count
																					 * =
																					 * Long
																					 * .
																					 * MAX_VALUE
																					 * ;
																					 * }
																					 * 
																					 * datas
																					 * .
																					 * addAll
																					 * (
																					 * result
																					 * )
																					 * ;
																					 * removeDuplicate
																					 * (
																					 * datas
																					 * )
																					 * ;
																					 * if
																					 * (
																					 * result
																					 * .
																					 * size
																					 * (
																					 * )
																					 * <
																					 * pageSize
																					 * )
																					 * {
																					 * enableLoadMore
																					 * =
																					 * false
																					 * ;
																					 * }
																					 * else
																					 * {
																					 * enableLoadMore
																					 * =
																					 * true
																					 * ;
																					 * }
																					 * }
																					 * if
																					 * (
																					 * callback
																					 * !=
																					 * null
																					 * )
																					 * {
																					 * callback
																					 * .
																					 * handle
																					 * (
																					 * result
																					 * .
																					 * size
																					 * (
																					 * )
																					 * )
																					 * ;
																					 * }
																					 * }
																					 * }
																					 * }
																					 * catch
																					 * (
																					 * Exception
																					 * e
																					 * )
																					 * {
																					 * e
																					 * .
																					 * printStackTrace
																					 * (
																					 * )
																					 * ;
																					 * }
																					 * }
																					 * 
																					 * @
																					 * Override
																					 * public
																					 * void
																					 * complete
																					 * (
																					 * )
																					 * {
																					 * if
																					 * (
																					 * dialog
																					 * )
																					 * {
																					 * /
																					 * /
																					 * dismissDialog
																					 * (
																					 * 0
																					 * )
																					 * ;
																					 * }
																					 * if
																					 * (
																					 * callback
																					 * !=
																					 * null
																					 * )
																					 * {
																					 * callback
																					 * .
																					 * complete
																					 * (
																					 * )
																					 * ;
																					 * }
																					 * }
																					 * 
																					 * @
																					 * Override
																					 * public
																					 * void
																					 * error
																					 * (
																					 * Throwable
																					 * e
																					 * )
																					 * {
																					 * super
																					 * .
																					 * error
																					 * (
																					 * e
																					 * )
																					 * ;
																					 * }
																					 * 
																					 * }
																					 * )
																					 * ;
																					 * }
																					 * catch
																					 * (
																					 * Exception
																					 * e1
																					 * )
																					 * {
																					 * e1
																					 * .
																					 * printStackTrace
																					 * (
																					 * )
																					 * ;
																					 * }
																					 */
	}

	public void removeDuplicate(ArrayList<BookBean> list) {
		try {
			ArrayList<Long> tmp = new ArrayList<Long>();
			Iterator<BookBean> itr = list.iterator();
			while (itr.hasNext()) {
				BookBean b = itr.next();
				if (tmp.contains(b.getId())) {
					itr.remove();
				} else {
					tmp.add(b.getId());
				}
			}
			tmp.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * @Override protected Dialog onCreateDialog(int id) { switch (id) { case 0:
	 * ProgressDialog pd = new ProgressDialog(this); pd.setMessage("请稍后...");
	 * return pd; default: break; } return super.onCreateDialog(id); }
	 */

	public void onTimeChange() {
	};

	public void loadData() {
	};
}
