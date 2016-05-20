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
 * ��ʷ������ʱ���ѷ���
 * 
 * @author Administrator
 * 
 */
public class AlarmClockBookService extends Service {

	/**
	 * ��count == START_ID,��ʾˢ�¶����б�
	 */
	public static final long START_ID = Long.MAX_VALUE;
	/**
	 * ��������֮ǰ���ٷ��������û�����������λ����
	 */
	private final int BEFORE_BOOK_OVER = 31;
	/**
	 * ��������֮����ٷ��ӵ�ʱ�������û�����������λ����
	 */
	private final int AFTER_BOOK_OVER = 25;
	/**
	 * �����б�
	 */
	public ArrayList<BookBean> datas = new ArrayList<BookBean>();
	/**
	 * �����б������
	 */
	private long count = 0;
	/**
	 * ÿ�δӷ�������ѯ����������
	 */
	private int pageSize = 5;
	/**
	 * ����ʱ��������
	 */
	private NewNetworkRequest.TimeLine timeLine;
	/**
	 * ʱ�������߻�ȡ�ص�
	 */
	private Callback<Object> timeLinesCallBack = new Callback<Object>() {
		@Override
		public void handle(Object param) {
			if (param != null) {
				TimeLines timeLines = (NewNetworkRequest.TimeLines) param;
				ArrayList<NewNetworkRequest.TimeLine> timeLineList = timeLines.datas;
				if (timeLineList.size() > 0) {
					timeLine = timeLineList.get(0);
					// �ӷ�������ȡ���������Ķ����б�
					getAPage();
				}
			}
		}
	};
	/**
	 * ���ݿ�
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
			case 1:// �������а�Сʱ��ʱ
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), AlarmBookActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("alarmInfo", "��ע�⣬��ԤԼ���ó���Сʱ�������");
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
			case 2:// ������ʱ��Сʱ�Ķ���
				Intent intent1 = new Intent();
				intent1.setClass(getApplicationContext(), AlarmBookActivity.class);
				Bundle bundle1 = new Bundle();
				bundle1.putString("alarmInfo", "�����⣬���ж�����Ҫ����");
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
	 * ������ѵ�ʱ����
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
		// ��ʼ�������б�
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
	 * ע����¶����б�ļ�����
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
				// �ӷ�������ȡ���������Ķ����б�
				getAPage();
			} else {
				initDatas();
			}
		}
	}

	/**
	 * ��ȡ�û�id
	 * 
	 * @return �û�id���绰��
	 */
	public String getPassengerId() {
		// �ж��Ƿ����³���ע�����
		String id;
		id = session.get(User._MOBILE_NEW);
		// �°汾ע���½�õ��˺� mobileNew
		if (TextUtils.isEmpty(id)) {
			id = session.get(User._MOBILE);
			if (TextUtils.isEmpty(id)) {
				id = null;
			}
		}
		return id;
	}

	/**
	 * �ж��û��Ƿ��ڵ�¼״̬
	 */
	public boolean isLogin() {
		if (!TextUtils.isEmpty(getPassengerId())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ��ʼ�������б�
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
					if (count == START_ID) {// ˢ��
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

					// ���ص��б��Ƿ��г���ʱ���ߵģ�ɾ����������ʱ���
					boolean isGetMore = isGetMore(result);

					// ɸѡ���б�����Ҫ���ѵĶ���
					boolean isNeedAlarm = false;
					ArrayList<BookBean> removeList = new ArrayList<BookBean>();
					for (BookBean bookBean : result) {
						isNeedAlarm = isBookNeedAlarmClock(bookBean);
						if (!isNeedAlarm) {
							removeList.add(bookBean);
						}
					}
					result.removeAll(removeList);
					// ��ɸѡ����list�ŵ��б���
					datas.addAll(result);

					removeDuplicate(datas);

					// ������ȡ
					if (isGetMore) {
						getAPage();
					}

					if (!isGetMore) {
						if (datas.size() > 0) {
							// ��ʼά��ɾѡ�Ķ����б��������ݵ�ʱ�䣬��ʱ��������������ʱ����������û�������
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
	 * �ж��б����Ƿ��г���ʱ���ߵĶ���:����ʱ���ߵ�ɾ����
	 * 
	 * @return ture:��ʾ��Ҫ��ȡ���ඩ���б� false���ٻ�ȡ
	 */
	public boolean isGetMore(List<BookBean> result) {
		// �鿴ÿ��������������ʱ��
		long submitTime = 0;
		// ʱ�����ߣ���λ����
		String upperTime = "0";
		if (null != timeLine) {
			upperTime = timeLine.upper;
		}
		Date nowTime;
		Date newTimeLine;
		// �Ƿ�����ӷ�������ȡ���ඩ���б�����
		boolean isGetMore = true;
		if (result.size() < pageSize) {
			isGetMore = false;
		}
		for (BookBean bookBean : result) {
			try {
				submitTime = f.parse(bookBean.getSubmitTime()).getTime();
				// ��ʱ���Ƿ�����Ҫ���ѵ�ʱ���
				nowTime = new Date();
				newTimeLine = new Date(nowTime.getTime() - Integer.parseInt(upperTime) * 60 * 1000 - AFTER_BOOK_OVER * 60 * 1000);

				if (newTimeLine.getTime() < submitTime) {// ����
					continue;
				} else {
					result.remove(bookBean);// �Ƴ�������ʱ���ߵ�
					isGetMore = false;
					// ���ٴӷ�������ȡ����
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
	 * �ӷ��������ض���
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
			// ��ѯ����
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
	 * �Ƴ����ظ��Ķ���
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
	 * ��ȡԤԼʱ����������
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
	 * �жϸö����Ƿ���Ҫ��������
	 * 
	 * @param bookBean
	 * @return
	 */
	/*
	 * public boolean isBookNeedAlarmClock1(BookBean b) { if (BookUtil.isNew(b))
	 * {//���������ύ״̬��δ�ӵ�����ʱ��ȡ������ �� return true; }else if
	 * (!BookUtil.isEffective(b.getReplyerId()) && b.getState() == 2) {//
	 * δ�ӵ��˿�ȡ���� return false; }else if (BookUtil.isEffective(b.getReplyerId())
	 * && b.getState() == 2) {// �ӵ���˿�ȡ���� return false; } else if
	 * (BookUtil.isEffective(b.getReplyerId()) && b.getState() == 3) {//
	 * �ӵ���˾��ȡ����˾�������޴������ return false; }else if (BookUtil.isActive(b))
	 * {//˾���ѽӵ�,����δ���� �� return true; }else if
	 * (BookUtil.isEffective(b.getEvaluate()) &&
	 * BookUtil.isEffective(b.getReplyerId())) {//����������������ۡ� return false; }
	 * else {//��ʷ���� boolean isNeedAlarm; try { //�ѳ�ʱ������ʱʱ������ ����ʱ��
	 * AFTER_BOOK_OVER ������Ҫ���� �� isNeedAlarm =
	 * ToolUtil.compareTime(BaseBookLoader
	 * .f.parse(b.getUseTime()).getTime()+AFTER_BOOK_OVER
	 * *60*1000,System.currentTimeMillis()); } catch (ParseException e1) {
	 * isNeedAlarm = false; } //��ʱ������Ҫ���� if(isNeedAlarm){ if
	 * (BookUtil.isEffective(b.getReplyerId()) && b.getState() == 0) {// �ѽӵ�����ʱ��
	 * return true; } else if(!BookUtil.isEffective(b.getReplyerId()) &&
	 * b.getState() == 0) {// δ�ӵ���ʱ�� return false; }else if
	 * (BookUtil.isEffective(b.getReplyerId()) && b.getState() == 1 ) {// ���������
	 * �� return true; } } } return false; }
	 */
	public boolean isBookNeedAlarmClock(BookBean b) {
		boolean isNeedAlarm = false;
		switch (b.getState()) {
		// ˾���ӵ�ǰ:������
		case 0x01:
			isNeedAlarm = false;
			break;
		// ����ʧ��
		case 0x03:
			isNeedAlarm = false;
			break;
		// δ�ӵ��˿�ȡ��:����ȡ��
		case 0x04:
			isNeedAlarm = false;
			break;
		// ִ�ж���-��˾���ӵ�:�ѽӵ�
		case 0x05:
			isNeedAlarm = true;
			break;
		// ����������-�˿�ȷ���ϳ�/˾��ȷ���ϳ����˿���ֹ����/˾����ֹ����:����������
		case 0x06:
			isNeedAlarm = false;
			break;
		// �������-˾��ȷ���ϳ�/�˿�ȷ���ϳ�
		case 0x07:
			isNeedAlarm = false;
			break;
		// ����ִ��ʧ��-�˿�ͬ����ֹ����/˾��ͬ����ֹ�����������ٲ�
		case 0x08:
			isNeedAlarm = false;
			break;
		// ����-�˿Ͳ�ͬ����ֹ����/˾����ͬ����ֹ������˾��δȷ���ϳ�/�˿�δȷ���ϳ�������ִ�г�ʱ
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
	 * ��ʼ��ѵ�ж϶����Ƿ�Ӧ�����Ѵ�����
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

							// ʱ�������Ѽ����������
							if (b.getDyTime() >= BEFORE_BOOK_OVER * 60 * 1000 - CHECK_TIME - 1 * 1000 && b.getDyTime() <= BEFORE_BOOK_OVER * 60 * 1000) {
								if (b.getState() == 0x05) {// ִ�ж���-��˾���ӵ�:�ѽӵ���������"��ע�⣬��ԤԼ���ó���Сʱ�������"
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
						sendEmptyMessageDelayed(0, CHECK_TIME);// ÿ������ж�һ���Ƿ���Ҫ����
					}
				};
			};
		}
		handler.sendEmptyMessage(0);
	}
}
