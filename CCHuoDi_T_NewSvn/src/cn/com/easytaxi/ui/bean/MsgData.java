package cn.com.easytaxi.ui.bean;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.json.JSONObject;

import android.content.ContentValues;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.client.channel.TcpClient;
import cn.com.easytaxi.client.common.MsgConst;
import cn.com.easytaxi.common.Config;
import cn.com.easytaxi.onetaxi.TaxiState;
import cn.com.easytaxi.taxi.bean.MsgBeanDao;
import cn.com.easytaxi.ui.adapter.DateAdapter;
import cn.com.easytaxi.ui.adapter.LoadCallback;
import cn.com.easytaxi.util.AsyncUtil;
import cn.com.easytaxi.util.DBManager;
import cn.com.easytaxi.util.ETException;
import cn.com.easytaxi.util.GlobalConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * @author shiner
 */
/**
 * @author shiner
 * 
 */
public class MsgData extends BaseData<MsgBean> {
	private static final String TAG = "MsgData";

	public static Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(Date.class, new DateAdapter()).create();

	static int all = 32;
	static int newAll = all;

	/**
	 * 从服务器分页获取消息，自动放入缓存(数据库)
	 * 
	 * @param start
	 * @param cityId
	 * @param passengerId
	 * @return
	 * @return List<MsgBean>
	 */
	private List<MsgBean> getMsgListRemote(int start, String cityId, String passengerId) {
		List<MsgBean> result = null;
		try {
			JSONObject param = new JSONObject();
			param.put("action", GlobalConfig.NEW_TCP_ACTION_PROXY);
			param.put("method", GlobalConfig.NEW_TCP_METHOD_QUERY);
			param.put("op", "getMessageList");
			param.put("cityId", Integer.valueOf(cityId));
			param.put("objectId", Long.valueOf(passengerId));
			param.put("objectType", 2);
			param.put("startId", start);
			param.put("order", "asc");
			param.put("orderType", "_ID");
			AppLog.LogD(TAG, "getMsgListRemote:req:" + param.toString());

			byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, param.toString().getBytes("UTF-8"));

			JSONObject jObj = new JSONObject(new String(response, "UTF-8"));

			AppLog.LogD(TAG, "getMsgListRemote:res:  ---------- " + jObj);
			if (jObj.getInt("error") == 0) {
				Type type = new TypeToken<List<MsgBean>>() {
				}.getType();
				synchronized (MsgData.this) {
					result = gson.fromJson(jObj.getString("datas"), type);
				}
			} else {
				throw new ETException(jObj.getString("errormsg"));
			}

			if (result != null && !result.isEmpty()) {
				Collections.sort(result, new ComparatorId());
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (result == null) {
				result = new ArrayList<MsgBean>();
			}
		}

		return result;
	}

	/**
	 * 从服务器分页获取消息，自动放入缓存(数据库)
	 * 
	 * @param id
	 *            司机id
	 * @return
	 */
	public void getMsgList(final int start, final int size, final String cityId, final String passengerId, final boolean loadNew, LoadCallback<List<MsgBean>> callback) {
		if (loadNew) {// 加载新消息（列表下拉刷新时）
			AsyncUtil.goAsync(new Callable<List<MsgBean>>() {

				@Override
				public List<MsgBean> call() {
					List<MsgBean> msgs = getMsgListRemote(getCount(), cityId, passengerId);

					// 缓存到数据库
					insert(msgs);

					return msgs;
				}
			}, callback);

		} else {// 上拉加载更多
			final List<MsgBean> result = getMsg(start, Integer.MAX_VALUE);//从本地库中加载数据

			if (result != null && !result.isEmpty()) { // 先返回给调用者，再从网络加载
				AsyncUtil.handle(callback, result);
			}
			AsyncUtil.goAsync(new Callable<List<MsgBean>>() {

				@Override
				public List<MsgBean> call() throws Exception {
					List<MsgBean> msgs = getMsgListRemote(start + result.size(), cityId, passengerId);
					// 缓存到数据库
					insert(msgs);

					return msgs;
				}
			}, callback);
		}
	}

	/**
	 * 查询
	 * 
	 * @param start
	 * @param size
	 * @return
	 */
	private List<MsgBean> getMsg(int start, int size) {
		MsgBeanDao dao = DBManager.getMsgDao(ETApp.getInstance());
		return dao.queryBuilder().orderDesc(MsgBeanDao.Properties.Id).offset(start).limit(size).list();
	}

	/**
	 * 本地标记为已读
	 * 
	 * @param msgId
	 * @return
	 */
	public int markLocalRead(Long msgId) {
		MsgBeanDao dao = DBManager.getMsgDao(ETApp.getInstance());
		ContentValues values = new ContentValues();
		values.put(MsgBeanDao.Properties.Read.columnName, true);
		int result = dao.getDatabase().update(MsgBeanDao.TABLENAME, values, MsgBeanDao.Properties.Id.columnName + " = ?", new String[] { msgId + "" });
		return result;
	}

	/**
	 * 标记为已读
	 * 
	 * @param msgId
	 * @return
	 */
	public void markRead(final Long msgId, LoadCallback<Boolean> callback) {
		// 先更新缓存
		markLocalRead(msgId);

		// 再标记到服务器
		AsyncUtil.goAsync(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return makeReadRemote(msgId);
			}
		}, callback);
	}

	public boolean makeReadRemote(final Long msgId) throws Exception {
		boolean result = false;
		JSONObject param = new JSONObject();
		param.put("action", Config.NEW_TCP_ACTION);
		param.put("method", Config.NEW_TCP_ACTION_QUERY);
		param.put("op", "setMessageMark");
		param.put("mark", true);
		param.put("Id", msgId);

		AppLog.LogD(TAG, "makeReadRemote:req:" + param.toString());
		byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, param.toString().getBytes("UTF-8"));
		JSONObject jObj = new JSONObject(new String(response, "UTF-8"));
		AppLog.LogD(TAG, "makeReadRemote:res:" + jObj.toString());
		if (jObj.getInt("error") == 0) {
			result = true;
		} else {
			throw new ETException(jObj.getString("errormsg"));
		}
		return result;

	}

	public static void saveMsg(MsgBean msg) {

		MsgBeanDao dao = DBManager.getMsgDao(ETApp.getInstance());
		// ContentValues values = new ContentValues();
		dao.insert(msg);
	}

	/**
	 * 标记全部为已读
	 * 
	 * @return
	 */
	public int makeAllRead() {
		MsgBeanDao dao = DBManager.getMsgDao(ETApp.getInstance());
		ContentValues values = new ContentValues();
		values.put(MsgBeanDao.Properties.Read.columnName, true);
		int result = dao.getDatabase().update(MsgBeanDao.TABLENAME, values, null, null);
		return result;
	}

	/**
	 * id比较器
	 * 
	 * @author shiner
	 * 
	 */
	public static class ComparatorId implements Comparator<MsgBean> {

		@Override
		public int compare(MsgBean object1, MsgBean object2) {
			try {
				if (object1 == null) {
					return -1;
				} else if (object2 == null) {
					return 1;
				}
				return (int) (object2.getCacheId() - object1.getCacheId());
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}
		}
	};
}
