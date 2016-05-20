package cn.com.easytaxi.service;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import cn.com.easytaxi.BookConfig;
import cn.com.easytaxi.MobileInfo;
import cn.com.easytaxi.NewNetworkRequest;
import cn.com.easytaxi.NewNetworkRequest.TimeLines;
import cn.com.easytaxi.book.BookBean;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.common.Const;
import cn.com.easytaxi.common.SessionAdapter;
import cn.com.easytaxi.common.SocketUtil;
import cn.com.easytaxi.common.User;
import cn.com.easytaxi.platform.AlarmBookActivity;
import cn.com.easytaxi.platform.MainActivityNew;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 历史订单定时提醒服务
 * 
 * @author Administrator
 * 
 */
public class AlarmClockBookService extends Service {

	/**
	 * 当count == START_ID,表示刷新订单列表
	 */
	public static final long START_ID = Long.MAX_VALUE;
	/**
	 * 订单结束之前多少分钟提醒用户处理订单：单位分钟
	 */
	private final int BEFORE_BOOK_OVER = 31;
	/**
	 * 订单结束之后多少分钟的时候提醒用户处理订单：单位分钟
	 */
	private final int AFTER_BOOK_OVER = 25;
	/**
	 * 订单列表
	 */
	public ArrayList<BookBean> datas = new ArrayList<BookBean>();
	/**
	 * 订单列表的条数
	 */
	private long count = 0;
	/**
	 * 每次从服务器查询订单的条数
	 */
	private int pageSize = 5;
	/**
	 * 订车时间上下线
	 */
	private NewNetworkRequest.TimeLine timeLine;
	/**
	 * 时间上下线获取回调
	 */
	private Callback<Object> timeLinesCallBack = new Callback<Object>() {
		@Override
		public void handle(Object param) {
			if (param != null) {
				TimeLines timeLines = (NewNetworkRequest.TimeLines) param;
				ArrayList<NewNetworkRequest.TimeLine> timeLineList = timeLines.datas;
				if (timeLineList.size() > 0) {
					timeLine = timeLineList.get(0);
					// 从服务器获取满足条件的订单列表
					getAPage();
				}
			}
		}
	};
	/**
	 * 数据库
	 */
	private SessionAdapter session;
	private Context context;
	private MobileInfo mobileInfo;
	public SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Handler handler;

	private BookBean alarmBookBean;
	public Handler handMain = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:// 订单还有半小时超时
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), AlarmBookActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("alarmInfo", "请注意，您预约的用车半小时后出发！");
				if (alarmBookBean != null) {
					bundle.putSerializable("book", alarmBookBean);
					// bundle.putString("str", alarmBookBean.toString());
				} else {
					bundle.putSerializable("book", null);
				}
				intent.putExtras(bundle);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				break;
			case 2:// 即将超时半小时的订单
				Intent intent1 = new Intent();
				intent1.setClass(getApplicationContext(), AlarmBookActivity.class);
				Bundle bundle1 = new Bundle();
				bundle1.putString("alarmInfo", "请留意，您有订单需要处理！");
				if (alarmBookBean != null) {
					bundle1.putSerializable("book", alarmBookBean);
					// bundle1.putString("str", alarmBookBean.toString());
				} else {
					bundle1.putSerializable("book", null);
				}
				intent1.putExtras(bundle1);
				intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent1);
				break;
			default:
				break;
			}
		}
	};
	/**
	 * 检测提醒的时间间隔
	 */
	private int CHECK_TIME = 30 * 1000;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		context = getBaseContext();
		session = new SessionAdapter(this);
		mobileInfo = MobileInfo.getInstance(this);
		registReceiver();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 初始化订单列表
		if (null == timeLine) {
			initDatas();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		try {
			if (refreshReceiver != null) {
				context.unregisterReceiver(refreshReceiver);
			}
		} catch (Exception e) {
		}
		super.onDestroy();
	}

	private ReloadReceiver refreshReceiver;

	private class ReloadReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			onRefresh();
		}

	}

	/**
	 * 注册更新订单列表的监听器
	 */
	private void registReceiver() {
		refreshReceiver = new ReloadReceiver();
		IntentFilter filter = new IntentFilter("cn.com.easytaxi.book.refresh_list");
		filter.addAction(Const.BOOK_STATE_CHANGED_LIST);
		context.registerReceiver(refreshReceiver, filter);
	}

	public void onRefresh() {
		if (!StringUtils.isEmpty(getPassengerId())) {
			if (timeLine != null) {
				count = START_ID;
				// 从服务器获取满足条件的订单列表
				getAPage();
			} else {
				initDatas();
			}
		}
	}

	/**
	 * 获取用户id
	 * 
	 * @return 用户id：电话号
	 */
	public String getPassengerId() {
		// 判断是否用新程序注册过，
		String id;
		id = session.get(User._MOBILE_NEW);
		// 新版本注册登陆用的账号 mobileNew
		if (TextUtils.isEmpty(id)) {
			id = session.get(User._MOBILE);
			if (TextUtils.isEmpty(id)) {
				id = null;
			}
		}
		return id;
	}

	/**
	 * 判断用户是否处于登录状态
	 */
	public boolean isLogin() {
		if (!TextUtils.isEmpty(getPassengerId())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 初始化订单列表
	 */
	private void initDatas() {
		if (!StringUtils.isEmpty(getPassengerId())) {
			count = START_ID;
			getTimeDeadLine();
		} else {
		}
	}

	public void getAPage() {
		loadPage(new Callback<List<BookBean>>() {
			@Override
			public void handle(List<BookBean> result) {
				synchronized (datas) {
					if (count == START_ID) {// 刷新
						datas.clear();
					}
					if (result != null && result.size() > 0) {
						for (BookBean bean : result) {
							long id = bean.getId();
							if (count > id) {
								count = id;
							}
						}
					} else {
						count = Long.MAX_VALUE;
					}

					// 返回的列表是否有超出时间线的：删除超过提醒时间的
					boolean isGetMore = isGetMore(result);

					// 筛选出列表中需要提醒的订单
					boolean isNeedAlarm = false;
					ArrayList<BookBean> removeList = new ArrayList<BookBean>();
					for (BookBean bookBean : result) {
						isNeedAlarm = isBookNeedAlarmClock(bookBean);
						if (!isNeedAlarm) {
							removeList.add(bookBean);
						}
					}
					result.removeAll(removeList);
					// 把筛选过的list放到列表中
					datas.addAll(result);

					removeDuplicate(datas);

					// 继续获取
					if (isGetMore) {
						getAPage();
					}

					if (!isGetMore) {
						if (datas.size() > 0) {
							// 开始维护删选的订单列表结果中数据的时间，在时间条件满足条件时候给于提醒用户处理订单
							startLoopTime();
						}
					}
				}
			}

			@Override
			public void complete() {
				super.complete();
			}

			@Override
			public void error(Throwable e) {
				super.error(e);
			}
		});
	}

	/**
	 * 判断列表中是否有超过时间线的订单:超出时间线的删除掉
	 * 
	 * @return ture:表示需要获取更多订单列表 false则不再获取
	 */
	public boolean isGetMore(List<BookBean> result) {
		// 查看每条订单发订单的时间
		long submitTime = 0;
		// 时间上线：单位分钟
		String upperTime = "0";
		if (null != timeLine) {
			upperTime = timeLine.upper;
		}
		Date nowTime;
		Date newTimeLine;
		// 是否继续从服务器获取更多订单列表数据
		boolean isGetMore = true;
		if (result.size() < pageSize) {
			isGetMore = false;
		}
		for (BookBean bookBean : result) {
			try {
				submitTime = f.parse(bookBean.getSubmitTime()).getTime();
				// 该时间是否在需要提醒的时间段
				nowTime = new Date();
				newTimeLine = new Date(nowTime.getTime() - Integer.parseInt(upperTime) * 60 * 1000 - AFTER_BOOK_OVER * 60 * 1000);

				if (newTimeLine.getTime() < submitTime) {// 继续
					continue;
				} else {
					result.remove(bookBean);// 移除掉超过时间线的
					isGetMore = false;
					// 不再从服务器获取数据
					break;
				}
			} catch (ParseException e) {
				isGetMore = false;
				e.printStackTrace();
			}
		}

		return isGetMore;
	}

	/**
	 * 从服务器加载订单
	 * 
	 * @param callback
	 */
	public void loadPage(final Callback<List<BookBean>> callback) {
		JSONObject json = new JSONObject();
		try {
			json.put("action", "scheduleAction");
			json.put("method", "getBookListByPassenger");
			json.put("startId", count);
			json.put("size", pageSize);
			String mobile = getPassengerId();
			if (StringUtils.isEmpty(mobile)) {
				mobile = "0";
			}
			json.put("passengerId", mobile);
			json.put("bookType", 5);

			json.put("cityId", MainActivityNew.cityId);
			json.put("cityName", MainActivityNew.currentCityName);
			json.put("clientType", BookConfig.ClientType.CLIENT_TYPE_PASSENGER);
			// 查询订单
			SocketUtil.getJSONArray(Long.valueOf(mobile), json, new Callback<byte[]>() {

				@Override
				public void handle(byte[] param) {
					try {
						if (param != null) {
							JSONObject jsonObject = new JSONObject(new String(param, "UTF-8"));
							if (jsonObject.getInt("error") == 0) {
								String array = jsonObject.get("books").toString();
								Gson gson = new Gson();
								Type type = new TypeToken<List<BookBean>>() {
								}.getType();
								List<BookBean> result = gson.fromJson(array, type);

								if (callback != null) {
									callback.handle(result);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void complete() {
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

	/**
	 * 移除掉重复的订单
	 * 
	 * @param list
	 */
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

	/**
	 * 获取预约时间上线限制
	 */
	public void getTimeDeadLine() {
		String version = "" + mobileInfo.getVerisonCode();
		int cityId = 1;
		try {
			cityId = (Integer.parseInt(session.get("_CITY_ID")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		NewNetworkRequest.getTimeDeadLine(cityId, Const.BOOK_BOOK_TYPE, BookConfig.ClientType.CLIENT_TYPE_PASSENGER, version, timeLinesCallBack);
	}

	/**
	 * 判断该订单是否需要闹钟提醒
	 * 
	 * @param bookBean
	 * @return
	 */
	/*
	 * public boolean isBookNeedAlarmClock1(BookBean b) { if (BookUtil.isNew(b))
	 * {//订单处于提交状态，未接单：此时可取消订单 。 return true; }else if
	 * (!BookUtil.isEffective(b.getReplyerId()) && b.getState() == 2) {//
	 * 未接单乘客取消。 return false; }else if (BookUtil.isEffective(b.getReplyerId())
	 * && b.getState() == 2) {// 接单后乘客取消。 return false; } else if
	 * (BookUtil.isEffective(b.getReplyerId()) && b.getState() == 3) {//
	 * 接单后司机取消：司机端暂无此情况。 return false; }else if (BookUtil.isActive(b))
	 * {//司机已接单,服务未结束 。 return true; }else if
	 * (BookUtil.isEffective(b.getEvaluate()) &&
	 * BookUtil.isEffective(b.getReplyerId())) {//服务结束，我已评价。 return false; }
	 * else {//历史订单 boolean isNeedAlarm; try { //已超时，但超时时间少于 提醒时间
	 * AFTER_BOOK_OVER ，则需要提醒 。 isNeedAlarm =
	 * ToolUtil.compareTime(BaseBookLoader
	 * .f.parse(b.getUseTime()).getTime()+AFTER_BOOK_OVER
	 * *60*1000,System.currentTimeMillis()); } catch (ParseException e1) {
	 * isNeedAlarm = false; } //超时，且需要提醒 if(isNeedAlarm){ if
	 * (BookUtil.isEffective(b.getReplyerId()) && b.getState() == 0) {// 已接单，超时。
	 * return true; } else if(!BookUtil.isEffective(b.getReplyerId()) &&
	 * b.getState() == 0) {// 未接单超时。 return false; }else if
	 * (BookUtil.isEffective(b.getReplyerId()) && b.getState() == 1 ) {// 订单已完成
	 * 。 return true; } } } return false; }
	 */
	public boolean isBookNeedAlarmClock(BookBean b) {
		boolean isNeedAlarm = false;
		switch (b.getState()) {
		// 司机接单前:调度中
		case 0x01:
			isNeedAlarm = false;
			break;
		// 调度失败
		case 0x03:
			isNeedAlarm = false;
			break;
		// 未接单乘客取消:我已取消
		case 0x04:
			isNeedAlarm = false;
			break;
		// 执行订单-有司机接单:已接单
		case 0x05:
			isNeedAlarm = true;
			break;
		// 结束订单中-乘客确认上车/司机确认上车，乘客终止订单/司机终止订单:结束订单中
		case 0x06:
			isNeedAlarm = false;
			break;
		// 订单完成-司机确认上车/乘客确认上车
		case 0x07:
			isNeedAlarm = false;
			break;
		// 订单执行失败-乘客同意终止订单/司机同意终止订单，争议仲裁
		case 0x08:
			isNeedAlarm = false;
			break;
		// 争议-乘客不同意终止订单/司机不同意终止订单，司机未确认上车/乘客未确认上车，订单执行超时
		case 0x09:
			isNeedAlarm = false;
			break;
		default:
			isNeedAlarm = false;
			break;
		}
		return isNeedAlarm;
	}

	/**
	 * 开始轮训判断订单是否应该提醒处理了
	 */
	public void startLoopTime() {
		if (handler == null) {
			handler = new Handler() {
				public void handleMessage(android.os.Message msg) {
					try {
						Iterator<BookBean> itr = datas.iterator();
						List<BookBean> haveAlarmList = new ArrayList<BookBean>();
						while (itr.hasNext()) {
							BookBean b = itr.next();

							long t = f.parse(b.getUseTime()).getTime() - System.currentTimeMillis();
							b.setDyTime(t);

							// 时间在提醒间隔，则提醒
							if (b.getDyTime() >= BEFORE_BOOK_OVER * 60 * 1000 - CHECK_TIME - 1 * 1000 && b.getDyTime() <= BEFORE_BOOK_OVER * 60 * 1000) {
								if (b.getState() == 0x05) {// 执行订单-有司机接单:已接单：才提醒"请注意，您预约的用车半小时后出发！"
									alarmBookBean = b;
									handMain.sendEmptyMessageDelayed(1, CHECK_TIME);
									haveAlarmList.add(b);
								}
							} else if (b.getDyTime() <= -AFTER_BOOK_OVER * 60 * 1000 + CHECK_TIME + 1 * 1000 && b.getDyTime() >= -AFTER_BOOK_OVER * 60 * 1000) {
								alarmBookBean = b;
								handMain.sendEmptyMessageDelayed(2, CHECK_TIME);
								haveAlarmList.add(b);
							}
							/*
							 * if (b.getDyTime() <= 0) {
							 * context.sendBroadcast(new Intent(
							 * "cn.com.easytaxi.book.refresh_list")); continue;
							 * }
							 */
						}
						datas.removeAll(haveAlarmList);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (datas != null && datas.size() > 0) {
						sendEmptyMessageDelayed(0, CHECK_TIME);// 每半分钟判断一次是否需要提醒
					}
				};
			};
		}
		handler.sendEmptyMessage(0);
	}
}
